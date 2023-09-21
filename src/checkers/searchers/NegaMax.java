package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import checkers.evaluators.BasicEvaluator;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class NegaMax extends CheckersSearcher {
    private int numNodes = 0;
    public NegaMax(ToIntFunction<Checkerboard> e) {
        super(e);
    }

    @Override
    public int numNodesExpanded() {
        return numNodes;
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        return NegaMax(board, getDepthLimit());
    }

    public Optional<Duple<Integer, Move>> NegaMax(Checkerboard board, Integer depth_limit) {
        PlayerColor currPlayer = board.getCurrentPlayer();
        if(board.gameOver()){
            if(board.playerWins(board.getCurrentPlayer())){
                return Optional.of(new Duple<>(Integer.MAX_VALUE, board.getLastMove()));
            } else if (board.playerWins(board.getCurrentPlayer().opponent())) {
                return Optional.of(new Duple<>(-Integer.MAX_VALUE, board.getLastMove()));
            } else{
                return Optional.of(new Duple<>(0, board.getLastMove()));
            }
        } else if(depth_limit == 0){
            return Optional.of(new Duple<>(getEvaluator().applyAsInt(board), board.getLastMove()));
        }
        int best_score = Integer.MIN_VALUE;
        Move best_move = null;
        for(Move move: board.getLegalMoves(board.getCurrentPlayer())) {
            numNodes += 1;
            Checkerboard new_board = board.duplicate();
            new_board.move(move);
            Optional<Duple<Integer,Move>> result = NegaMax(new_board, depth_limit - 1);
            int value = result.get().getFirst();
            if (currPlayer != new_board.getCurrentPlayer()) {
                value = -value;
            }
            if (value > best_score) {
                best_score = value;
                best_move = move;
            }
        }
        return Optional.of(new Duple<>(best_score, best_move));
    }
}
