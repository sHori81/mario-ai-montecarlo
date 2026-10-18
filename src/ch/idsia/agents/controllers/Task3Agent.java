/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

public class Task3Agent extends BasicMarioAIAgent implements Agent
{
boolean shoot = false;

public Task3Agent()
{
    super("OwnAgent");
    reset();
}

public void reset()
{
	action = new boolean[Environment.numberOfKeys];
	action[Mario.KEY_SPEED] = true;
	action[Mario.KEY_RIGHT] = true;
}

public boolean isObstacle(int r, int c){
	return getReceptiveFieldCellValue(r,c)==GeneralizerLevelScene.BRICK 
			|| getReceptiveFieldCellValue(r,c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
			|| getReceptiveFieldCellValue(r,c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
			|| getReceptiveFieldCellValue(r,c)==GeneralizerLevelScene.LADDER;
}

public boolean isEnemy(double x , double y, double theta_1, double theta_2) {
	for(int i = 0; i <= 9; i++) {
		for(int j = -9; j <= 9; j++) {
			
			int X = marioEgoCol;
			int Y = marioEgoRow;
			double distance = 0;
			double angle = 0;
			double dangerAngle_1 = Math.PI*theta_1/180;
			double dangerAngle_2 = Math.PI*theta_2/180;
			
			if(getEnemiesCellValue(X + i, Y + j) != 0 &&  !isObstacle(X + i,Y + j)) {
				distance = Math.sqrt(i^2 + j^2);
				angle = Math.atan2(j, i);
				
				if(x < distance && distance < y
						&& (dangerAngle_1< angle && angle <dangerAngle_2)){
					return true;}
			}
		}
	}
	return false;
}

private boolean isCreature(int c)
{
    switch (c)
    {
        case Sprite.KIND_GOOMBA:
            return true;
    }
    return false;
}

public boolean isHole(int c){
	for(int i = 1; i < 4 ; i++) {
		if(getReceptiveFieldCellValue(marioEgoRow + i, c) != 0) {
			return false;
		}
	}
	return true;	 
}


public boolean[] getAction(){
	int r = marioEgoRow;
    int c = marioEgoCol;
	if(isObstacle(r, c + 1)
			||isObstacle(r, c + 2)
			|isObstacle(r, c + 3)
			||isHole(c + 1))
			{
				action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
			}
	
	if(isEnemy(0,6,60,100)){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;}
	
	action[Mario.KEY_SPEED] = !(isEnemy(0,6,45,135));
	
	if((isEnemy(3,5,45,60) && !isMarioOnGround && action[Mario.KEY_SPEED])||
			(isEnemy(0,3,30,45) && !isMarioOnGround && !action[Mario.KEY_SPEED])||
			isEnemy(2,5,85,190) && !isMarioOnGround) {
		action[Mario.KEY_LEFT] = true;
		action[Mario.KEY_RIGHT] = false;
	}else {
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_LEFT] = false;
	}
	
	
	return action;
}





}

