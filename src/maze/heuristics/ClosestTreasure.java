package maze.heuristics;

import core.Pos;
import maze.core.MazeExplorer;

import java.util.Set;
import java.util.function.ToIntFunction;


public class ClosestTreasure implements ToIntFunction<MazeExplorer> {
    @Override
    public int applyAsInt(MazeExplorer node) {
        Set<Pos> treasures = node.getAllTreasureFromMaze();
        int minDistanceToTreasure = 1000000000;
        for (Pos treasure: treasures) {
            int distanceTo = node.getLocation().getManhattanDist(treasure);
            if(distanceTo < minDistanceToTreasure){
                minDistanceToTreasure = distanceTo;
            }
        }
        return minDistanceToTreasure;
    }
}
