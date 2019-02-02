package amazons;

/** NOTICE:
 * This file is a SUGGESTED skeleton.  NOTHING here or in any other source
 * file is sacred.  If any of it confuses you,
 * throw it out and do it your way. */

import java.util.ArrayList;
import java.util.Iterator;

import static amazons.Square.sq;
import static java.lang.Math.*;

import static amazons.Piece.*;


/** A Player that automatically generates moves.
 *  @author Bryan Ho
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** Long used for storing time. */
    private long a;

    /** Long used for storing more time. */
    private long b;

    /** Default board. */
    private Board testBoard = new Board();

    /** Max number of moves as set by our game. */
    private int _max = iteratorSize(testBoard.legalMoves(testBoard.turn()))
            + iteratorSize(testBoard.legalMoves(testBoard.turn().opponent()));

    /** Depth logarithmic. */
    private static double log = (6 * Math.PI) / Math.E;

    /** Depth shift. */
    private static double shift = 10 * 2;

    /** Depth heuristics. */
    private double[] depthFinder = {log, shift};

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        if (move == null) {
            return "null";
        }
        _controller.reportMove(move);
        return move.toString();
    }

    /** Personal depth for AI. */
    private int prevDepth = 1;

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        a = System.currentTimeMillis();
        Board copy = new Board(board());
        if (_myPiece == WHITE) {
            findMove(copy, maxDepth(copy), true, 1, -INFTY, INFTY);
        } else {
            findMove(copy, maxDepth(copy), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        Move best = null;
        int bestest;
        if (sense == 1) {
            bestest = -INFTY;
            Iterator<Move> moves = board.legalMoves(board.turn());
            while (moves.hasNext()) {
                Board test = new Board(board);
                Move check = moves.next();
                test.makeMove(check);
                int response = findMove(test, depth - 1,
                        false, -sense, alpha, beta);
                if (response > bestest) {
                    best = check;
                    alpha = max(alpha, response);
                    bestest = alpha;
                }
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestest = INFTY;
            Iterator<Move> moves = board.legalMoves(board.turn());
            while (moves.hasNext()) {
                Board test = new Board(board);
                Move check = moves.next();
                test.makeMove(check);
                int response = findMove(test, depth - 1,
                        false, -sense, alpha, beta);
                if (response < bestest) {
                    best = check;
                    beta = min(beta, response);
                    bestest = beta;
                }
                if (beta <= alpha) {
                    break;
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = best;
        }
        return bestest;
    }
    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        final double logFactor = 2;
        int M = (int) (logFactor * iteratorSize(
                board.legalMoves(board.turn())));
        int depth = (int) (Math.log(_max / M) / Math.log(depthFinder[0])) + 1;
        if (M < depthFinder[1]) {
            depth = (M < 15) ? (depth + 2) : (depth + 1);
        }
        prevDepth = max(depth, prevDepth);
        int result = min(prevDepth, 5);
        return result;
    }


    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        ArrayList<Square> whites = new ArrayList<>();
        ArrayList<Square> blacks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Square square = sq(i);
            if (board.daBoard().get(square) == WHITE) {
                whites.add(square);
            } else if (board.daBoard().get(square) == BLACK) {
                blacks.add(square);
            }
        }
        int distWhite = 0;
        int distBlack = 0;
        for (int i = 0; i < 100; i++) {
            Square square = sq(i);
            if (board.daBoard().get(square) == EMPTY) {
                for (Square check : whites) {
                    distWhite += distance(square, check);
                }
                for (Square test : blacks) {
                    distBlack += distance(square, test);
                }
            }
        }
        return distBlack - distWhite + 3 * iteratorCheck(board, WHITE)
                - 3 * iteratorCheck(board, BLACK);
    }

    /** Returns distance FROM-TO. */
    private int distance(Square from, Square to) {
        int x = from.col() - to.col();
        int y = from.row() - to.row();
        int result = (int) Math.sqrt(x * x + y * y);
        return result;
    }
    /** Returns length of SIDE iterator on BOARD. */
    public int iteratorCheck(Board board, Piece side) {
        int score = 0;
        ArrayList<Square> starts = new ArrayList<>();
        for (Square square : board.daBoard().keySet()) {
            if (board.daBoard().get(square) == side) {
                starts.add(square);
            }
        }
        for (Square square : starts) {
            score += iteratorSize(board.reachableFrom(square, null));
        }
        return score;
    }

    /** Returns size of ITERATOR. */
    public int iteratorSize(Iterator iterator) {
        int result = 0;
        for ( ; iterator.hasNext(); result++) {
            iterator.next();
        }
        return result;
    }

}
