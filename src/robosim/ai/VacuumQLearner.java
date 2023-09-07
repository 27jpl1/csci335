package robosim.ai;

import core.Duple;
import robosim.core.*;
import robosim.reinforcement.QTable;

public class VacuumQLearner implements Controller {
    private QTable table = new QTable(3, 3, 0, 10, 10,.5);
    private int lastAction = 1; //setting last action to a turn means no reward added
    private int currTotalObjects;
    private int prevTotalObjects;
    private int totalReward;
    //action 0 is forward
    //action 1 is left
    //action 2 is right
    //state 0 is most dirt in front of robot
    //state 1 is most dirt to left of robot
    //state 2 is most dirt to right of robot
    @Override
    public void control(Simulator sim) {
        int leftDirt = 0;
        int rightDirt = 0;
        int straightDirt = 0;
        if(sim.getTotalMoves() == 0){
            prevTotalObjects = sim.TotalObjects();
            currTotalObjects = sim.TotalObjects();
        }
        else {
            prevTotalObjects = currTotalObjects;
            currTotalObjects = sim.TotalObjects();
        }
        for (Duple<SimObject, Polar> obj: sim.allVisibleObjects()) {
            if (obj.getFirst().isVacuumable()) {
                if (Math.abs(obj.getSecond().getTheta()) < Robot.ANGULAR_VELOCITY) {
                    straightDirt += 1;
                } else if (obj.getSecond().getTheta() < 0) {
                    leftDirt += 1;
                } else {
                    rightDirt += 1;
                }
            }
        }
        if(straightDirt > leftDirt && straightDirt > rightDirt){
            lastAction = table.senseActLearn(0, getRewardforAction(sim)); //reward is a placeholder for now
        }
        else if(leftDirt > straightDirt && leftDirt > rightDirt){
            lastAction = table.senseActLearn(1, getRewardforAction(sim));
        }
        else{
            lastAction = table.senseActLearn(2, getRewardforAction(sim));
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

    public int getRewardforAction(Simulator sim){
        if(prevTotalObjects > currTotalObjects){
            totalReward += 50;
            System.out.println("Total Reward = " + totalReward);
            return 50;
        }
        else if(sim.wasHit()){
            totalReward -= 10;
            System.out.println("Total Reward = " + totalReward);
            return -10;
        }
        else{
            System.out.println("Total Reward = " + totalReward);
            return 0;
        }
    }
}
