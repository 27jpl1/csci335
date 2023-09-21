package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.PlayerColor;

import java.util.function.ToIntFunction;

public class KingsEvaluator implements ToIntFunction<Checkerboard> {
    @Override
    public int applyAsInt(Checkerboard value) {
        PlayerColor currPlayer = value.getCurrentPlayer();
        PlayerColor opponent = currPlayer.opponent();
        return (value.numPiecesOf(currPlayer) + value.numKingsOf(currPlayer) - value.numPiecesOf(opponent)) - value.numKingsOf(opponent);
    }
}
