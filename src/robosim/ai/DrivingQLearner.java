package robosim.ai;

import robosim.core.Action;
import robosim.core.Controller;
import robosim.core.Simulator;
import robosim.reinforcement.QTable;

public class DrivingQLearner implements Controller{

    private QTable table = new QTable(3, 3, 0, 10, 10,.9);
    //creates a QTable with 3 states and actions, starting state at 0 (for now)
    // will change rateConstant and discount as see fit
    //state 0 is when the robot is close to an object
    //state 1 is when the robot is medium length to and object
    //state 2 is when the robot is far from an object
    //action 0 is forward
    //action 1 is left
    //action 2 is right
    private int lastAction = 1; //setting last action to a turn means no reward added
    private int totalReward = 0;
    @Override  //Seems to work but should check in to make sure that it is doing the right stuff
    public void control(Simulator sim) {
        if (sim.findClosestProblem() < 20){ //if he robot is close to a barrier
            lastAction = table.senseActLearn(0, getRewardforAction(lastAction,sim));
        }
        else if(sim.findClosestProblem() < 50){
            lastAction = table.senseActLearn(1, getRewardforAction(lastAction, sim));
        }
        else{
            lastAction = table.senseActLearn(2, getRewardforAction(lastAction, sim));

        }
        if(lastAction == 0){
            Action.FORWARD.applyTo(sim);
        }
        else if(lastAction == 1){
            Action.LEFT.applyTo(sim);
        }
        else{
            Action.RIGHT.applyTo(sim);
        }
    }

    public int getRewardforAction(int action, Simulator sim){ //track total reward for graph
        if(sim.wasHit()){
            totalReward -= 10;
            System.out.println("Total Reward = " + totalReward);
            return -10;
        }
        else if(action == 0){
            totalReward += 5;
            System.out.println("Total Reward = " + totalReward);
            return 5;
        }
        else{
            System.out.println("Total Reward = " + totalReward);
            return 0;
        }
    }
}
