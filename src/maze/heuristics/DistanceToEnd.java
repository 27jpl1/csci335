package maze.heuristics;

import core.Pos;
import maze.core.MazeExplorer;

import java.util.function.ToIntFunction;

public class DistanceToEnd implements ToIntFunction<MazeExplorer> {

    @Override
    public int applyAsInt(MazeExplorer node) {
        return node.getLocation().getManhattanDist(node.getGoal().getLocation());
    }
}
