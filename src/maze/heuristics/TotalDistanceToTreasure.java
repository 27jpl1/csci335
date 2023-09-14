package maze.heuristics;

import core.Pos;
import maze.core.MazeExplorer;

import java.util.Set;
import java.util.function.ToIntFunction;

public class TotalDistanceToTreasure implements ToIntFunction<MazeExplorer> {


    @Override
    public int applyAsInt(MazeExplorer node) {
        Set<Pos> treasures = node.getAllTreasureFromMaze();
        int totDistanceToTreasure = 0;
        for (Pos treasure: treasures) {
            int distanceTo = node.getLocation().getManhattanDist(treasure);
            totDistanceToTreasure += distanceTo;
        }
        return totDistanceToTreasure;
    }
}
