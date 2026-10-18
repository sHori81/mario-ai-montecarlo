package ch.idsia.agents;
import java.util.*;

public final class KeyOfMC {

	    private final int state;
	    private final int cliff;
	    private final int wall;
	    private final int ableToJump;
	    private final int action;
	    

	    public KeyOfMC(int state, int cliff, int wall, int ableToJump,int action) {
	        this.state = state;
	        this.cliff = cliff;
	        this.wall = wall;
	        this.ableToJump = ableToJump;
	        this.action = action;
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (obj instanceof KeyOfMC) {
	        	KeyOfMC key = (KeyOfMC) obj;
	            return this.state == key.state && this.cliff == key.cliff && this.wall == key.wall
	            		&& this.ableToJump == key.ableToJump && this.action == key.action;
	        } else {
	            return false;
	        }
	    }

	    @Override
	    public int hashCode() {
	        return state + cliff + wall + ableToJump + action;
	    }
	    public int getState(){
	    	return state;
	    }
	    public int getCliff(){
	    	return cliff;
	    }
	    public int getWall(){
	    	return wall;
	    }
	    public int getAbleToJump(){
	    	return ableToJump;
	    }
	    public int getAction(){
	    	return action;
	    }
}
