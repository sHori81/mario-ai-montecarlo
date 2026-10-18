package ch.idsia.scenarios;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.IgnoreObstacleAgent;
import ch.idsia.agents.controllers.Task2Agent;
import ch.idsia.agents.controllers.Task3Agent;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class MainTask3
{
public static void main(String[] args)
{
    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);

    final Agent agent = new Task3Agent();
    marioAIOptions.setAgent(agent);
    
    marioAIOptions.setArgs("-lhs off -ltb on -lg off -lb off -ld 1 -ls 0 -le g");
    
    final BasicTask basicTask = new BasicTask(marioAIOptions);
    basicTask.setOptionsAndReset(marioAIOptions);
    basicTask.doEpisodes(1,true,1);
    System.exit(0);
}

}
