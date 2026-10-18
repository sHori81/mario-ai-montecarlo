package ch.idsia.agents;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.LearningTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;

public class LearningWithMC implements LearningAgent{
	private Agent agent;
	private String name = "LearningWithMC";
	MCAgent mcagent = new MCAgent();
	//目標値(4096.0はステージの右端)
	private float goal = 4096.0f;
	private String args;
	//試行回数
	private int numOfTrial = 50000;
	//コンストラクタ
	private float penalty;
	public LearningWithMC(String args){
		this.args = args;
		agent = new MCAgent();
	}
	//学習部分
	//1000回学習してその中でもっとも良かったものをリプレイ
	

	public static void replay(String filename,MarioAIOptions marioAIOptions, LearningWithMC learningAgent){
		try{
			File f = new File(filename);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String s = br.readLine();
			if(s != null) {
				int size = Integer.parseInt(s);
				for(int i=0; i < size; ++i) {
					s = br.readLine();
					if(s != null)MCAgent.best.add(Integer.parseInt(s));
				}
			}
			br.close();
		}
		catch(IOException e){
		    System.out.println(e);
		}
		System.out.println(MCAgent.best.get(0));
		learningAgent.show();
	} 

	public void learn(){
		for(int i = 0; i < numOfTrial; ++i){
			//目標値までマリオが到達したらshowして終了
			if(run() == 1){
				show();
				break;
			}
			if(i % 3000 == 2999 )
				show();
		}
		try{
			//学習した行動価値関数を書き込み
			File f = new File("MonteCarlo4-2.txt");
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(String.valueOf(MCAgent.best.size()));
			bw.newLine();
			for(int i=0; i < MCAgent.best.size(); ++i) {
				bw.write(String.valueOf(MCAgent.best.get(i)));
				bw.newLine();
			}
			bw.close();
			System.exit(0);
		}
		catch(IOException e){
		    System.out.println(e);
		}
	}
	
	//リプレイ
	public void show(){
		MCAgent.ini();
		MarioAIOptions marioAIOptions = new MarioAIOptions();
		BasicTask basicTask = new BasicTask(marioAIOptions);

		/* ステージ生成 */
		marioAIOptions.setArgs(this.args);
		MCAgent.setMode(true);


	    /* プレイ画面出力するか否か */
	    marioAIOptions.setVisualization(true);
		/* MCAgentをセット */
		marioAIOptions.setAgent(agent);
		basicTask.setOptionsAndReset(marioAIOptions);

		if ( !basicTask.runSingleEpisode(1) ){
			System.out.println("MarioAI: out of computational time"
			+ " per action! Agent disqualified!");
		}
		//basicTask.doEpisodes(1, true, 1);
		/* 評価値(距離)をセット */
		EvaluationInfo evaluationInfo = basicTask.getEvaluationInfo();
		//報酬取得
		float reward = evaluationInfo.distancePassedPhys;
		System.out.println("報酬は" + reward);
	}
	//学習
	//画面に表示はしない
	public int run(){
		MCAgent.ini();
		/* MCAgentをプレイさせる */
		MarioAIOptions marioAIOptions = new MarioAIOptions();
		BasicTask basicTask = new BasicTask(marioAIOptions);

		/* ステージ生成 */
		marioAIOptions.setArgs(this.args);
		MCAgent.setMode(false);


	    /* プレイ画面出力するか否か */
	    marioAIOptions.setVisualization(false);
		/* MCAgentをセット */
		marioAIOptions.setAgent(agent);
		basicTask.setOptionsAndReset(marioAIOptions);

		if ( !basicTask.runSingleEpisode(1) ){
			System.out.println("MarioAI: out of computational time"
			+ " per action! Agent disqualified!");
		}

		/* 評価値(距離)をセット */
		EvaluationInfo evaluationInfo = basicTask.getEvaluationInfo();
	
		//報酬取得
		float reward = 0;
		float distance = evaluationInfo.distancePassedPhys;
		//System.out.println(reward +","+ MCAgent.bestScore);
		if (distance  > MCAgent.bestScore){
			reward = distance - mcagent.penalty;
		}else{
			reward = evaluationInfo.distancePassedPhys + (evaluationInfo.distancePassedPhys -  MCAgent.bestScore)/ MCAgent.bestScore - mcagent.penalty;}
		//reward -= (evaluationInfo.marioStatus == 0) ? 1000 : 0;
		System.out.println(reward + ",ペナルティ:" + mcagent.penalty+ "," + "ベスト:" + distance);
//		//ベストスコアが出たら更新
		mcagent.penalty = 0;
		mcagent.best_distancePassedPhys = 0;
		mcagent.before_distancePassedPhys = 0;
		mcagent.now_distancePassedPhys = 0;
		
		if(reward > MCAgent.bestScore){
			MCAgent.bestScore = reward;
			MCAgent.best = new ArrayList<Integer>(MCAgent.actions);
			
		}else {
			
		}
		Iterator<KeyOfMC> itr = MCAgent.selected.keySet().iterator();
		//価値関数を更新
		while(itr.hasNext()){
			KeyOfMC key = (KeyOfMC)itr.next();
			MCAgent.sumValue[key.getState()][key.getCliff()][key.getWall()][key.getAbleToJump()][key.getAction()]+= reward;
			MCAgent.num[key.getState()][key.getCliff()][key.getWall()][key.getAbleToJump()][key.getAction()]++;
			MCAgent.qValue[key.getState()][key.getCliff()][key.getWall()][key.getAbleToJump()][key.getAction()] =
					MCAgent.sumValue[key.getState()][key.getCliff()][key.getWall()][key.getAbleToJump()][key.getAction()]
							/ (float)MCAgent.num[key.getState()][key.getCliff()][key.getWall()][key.getAbleToJump()][key.getAction()];
		}
		return evaluationInfo.marioStatus;
		
	}
	
	
	
		
	//////////////////////////////ここからは必要なし//////////////////////////////
	@Override
	public boolean[] getAction() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void integrateObservation(Environment environment) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void giveReward(float reward) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setLearningTask(LearningTask learningTask) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Agent getBestAgent() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setEvaluationQuota(long num) {
		// TODO Auto-generated method stub
		
	}
}