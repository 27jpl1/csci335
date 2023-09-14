package maze.heuristics;

import core.Pos;
import maze.core.MazeExplorer;

import java.util.Set;
import java.util.function.ToIntFunction;

public class TotTreasures implements ToIntFunction<MazeExplorer> {
    @Override
    public int applyAsInt(MazeExplorer node) {
        int treasures = node.getAllTreasureFromMaze().size();
        int treasuresFound = node.getNumTreasuresFound();
        return treasures - treasuresFound;
    }
}
