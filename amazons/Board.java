package amazons;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;

import static amazons.Piece.*;
import static amazons.Move.mv;
import static amazons.Square.*;


/** The state of an Amazons Game.
 *  @author Bryan HO
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** Collection of moves made. */
    private LinkedList<Move> daMoves = new LinkedList<Move>();

    /** Returns daMoves. */
    LinkedList<Move> daMoves() {
        return daMoves;
    }

    /** Collection of board pieces. */
    private HashMap<Square, Piece> daBoard = new HashMap<>();

    /** Returns daMoves. */
    HashMap<Square, Piece> daBoard() {
        return daBoard;
    }

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL, but changes turn for TEST. */
    Board(Board model, int test) {
        copyPrime(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        init();
        this.daBoard = new HashMap<Square, Piece>(model.daBoard);
        this.daMoves.addAll(model.daMoves);
        this._turn = model.turn();
        this._winner = model.winner();
    }

    /** Copies MODEL into me. */
    void copyPrime(Board model) {
        init();
        this.daBoard = new HashMap<Square, Piece>(model.daBoard);
        this.daMoves.addAll(model.daMoves);
        this._turn = model.turn().opponent();
        this._winner = model.winner();
    }

    /** Clears the board to the initial position. */
    void init() {
        daBoard.clear();
        daMoves.clear();
        _turn = WHITE;
        _winner = null;
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            daBoard.put(Square.sq(i), EMPTY);
        }
        for (String i : Arrays.asList("d1", "g1", "a4", "j4")) {
            daBoard.put(Square.sq(i), WHITE);
        }
        for (String i : Arrays.asList("a7", "j7", "d10", "g10")) {
            daBoard.put(Square.sq(i), BLACK);
        }
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return daMoves.size();
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (!legalMoves(_turn).hasNext()) {
            _winner = _turn.opponent();
        }
        return _winner;
    }


    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return daBoard.get(s);
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return get(Square.sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        daBoard.put(s, p);
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        put(p, sq(col, row));
        _winner = EMPTY;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to)) {
            return false;
        } else {

            int direction = from.direction(to);
            int step = 1;
            Square check = from;
            while (check != to) {
                check = from.queenMove(direction, step);
                step++;
                if (check == asEmpty) {
                    continue;
                } else if (daBoard.get(check) != EMPTY) {
                    return false;
                }
            }
            return true;
        }
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        Piece squareName = daBoard.get(from);
        return squareName.toName() != null && squareName == _turn;
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from) && isUnblockedMove(from, to, null)
                && isUnblockedMove(to, spear, from);

    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        }
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        Move make = mv(from, to, spear);
        daMoves.add(make);
        daBoard.put(to, daBoard.get(from));
        daBoard.put(from, EMPTY);
        daBoard.put(spear, SPEAR);
        _winner = winner();
        _turn = _turn.opponent();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        if (move != null) {
            makeMove(move.from(), move.to(), move.spear());
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (daMoves.size() > 0) {
            Move last = daMoves.removeLast();
            daBoard.put(last.from(), daBoard.get(last.to()));
            daBoard.put(last.to(), EMPTY);
            daBoard.put(last.spear(), EMPTY);
            _turn = _turn.opponent();
        }
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square check = _from.queenMove(_dir, _steps);
            toNext();
            return check;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            while (hasNext()) {
                _steps++;
                Square check;
                try {
                    check = _from.queenMove(_dir, _steps);
                } catch (IllegalArgumentException e) {
                    shift();
                    continue;
                }
                if (!isUnblockedMove(_from, check, _asEmpty)) {
                    shift();
                } else {
                    break;
                }
            }
        }

        /** Direction shift. */
        private void shift() {
            _dir++;
            _steps = 0;
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            ArrayList<Square> starts = new ArrayList<>();
            for (Square square : daBoard.keySet()) {
                if (daBoard.get(square) == side) {
                    starts.add(square);
                }
            }
            _startingSquares = starts.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return cont || _spearThrows.hasNext()
                    || _pieceMoves.hasNext() || _startingSquares.hasNext();
        }

        @Override
        public Move next() {
            Move make = mv(_start, _end, _spear);
            cont = false;
            toNext();
            return make;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            while (hasNext()) {
                if (_startingSquares.hasNext() && (_pieceMoves == null
                        || !_pieceMoves.hasNext() && !_spearThrows.hasNext())) {
                    _start = _startingSquares.next();
                    _pieceMoves = reachableFrom(_start, null);
                }
                if (_pieceMoves != null && _pieceMoves.hasNext()
                        && (_spearThrows == null || !_spearThrows.hasNext())) {
                    _end = _pieceMoves.next();
                    _spearThrows = reachableFrom(_end, _start);
                }
                if (_spearThrows != null && _spearThrows.hasNext()) {
                    _spear = _spearThrows.next();
                    Move check = mv(_start, _end, _spear);
                    if (!isLegal(check)) {
                        toNext();
                    } else {
                        cont = true;
                        break;
                    }
                }
            }
        }
        /** Keeps track if Iterator is over. */
        private boolean cont = false;
        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Square that _start is moving toward. */
        private Square _end;
        /** The spear that is to be thrown. */
        private Square _spear;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String result = "";
        String line = "";
        for (int i = 0; i < SIZE * SIZE; i++) {
            if (i % 10 == 0) {
                line = line.concat("   ");
            }
            if (i % 10 == 9) {
                line = line.concat(daBoard.get(sq(i)).toString() + "\n");
                result = line.concat(result);
                line = "";
            } else {
                line = line.concat(daBoard.get(sq(i)).toString() + " ");
            }
        }
        return result;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner = EMPTY;
}
