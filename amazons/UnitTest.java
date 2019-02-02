package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;
import ucb.junit.textui;


import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

import static amazons.Board.*;


/** The suite of all JUnit tests for the amazons package.
 *  @author Bryan Ho
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }
    /** Test time.
    @Test
    public void testTime() {
        Board b = new Board();
        int total = 0;
        while (true) {
            Move check = Controller.randomMove();
            if (b.isLegal(check)) {
                total++;
                b.makeMove(check);
            }
            if (total == 20) {
                break;
            }
        }
        System.out.printf("===%n%s===%n", b);
        System.out.println(iteratorSize(b.legalMoves(WHITE)));
    }
    /** Test LinkedList. */
    @Test
    public void testLinkedList() {
        LinkedList<Integer> a = new LinkedList<>();
        a.add(1);
        a.add(2);
        LinkedList<Integer> b = new LinkedList<>();
        b.addAll(a);
        b.remove();
        assertFalse(a.size() == b.size());
        assertFalse(a == b);
    }
    /** Test move functions. */
    @Test
    public void testMoves() {
        Board b = new Board();
        b.makeMove(null);
        ArrayList<Square> whites = new ArrayList<Square>();
        for (Square square : b.daBoard().keySet()) {
            if (b.daBoard().get(square) == WHITE) {
                whites.add(square);
            }
        }
        assertTrue(whites.size() == 4);
        assertEquals(WHITE, b.turn());
        assertEquals(WHITE, b.daBoard().get(Square.sq("a4")));
        b.makeMove(Move.mv("a4 a3 a5"));
        whites = new ArrayList<Square>();
        for (Square square : b.daBoard().keySet()) {
            if (b.daBoard().get(square) == WHITE) {
                whites.add(square);
            }
        }
        assertTrue(whites.size() == 4);
        assertEquals(EMPTY, b.daBoard().get(Square.sq("a4")));
        assertEquals(WHITE, b.daBoard().get(Square.sq("a3")));
        assertEquals(BLACK, b.daBoard().get(Square.sq("d10")));
        b.makeMove(Move.mv("d10 e10 f10"));
        assertEquals(EMPTY, b.daBoard().get(Square.sq("d10")));
        assertEquals(BLACK, b.daBoard().get(Square.sq("e10")));
    }
    /** Test square placement functions. */
    @Test
    public void testSquares() {
        Board b = new Board();
        b.put(EMPTY, Square.sq(93));
        assertTrue(b.isUnblockedMove(Square.sq(3), Square.sq(93), null));
        b.put(SPEAR, Square.sq("d10"));
        assertFalse(b.isUnblockedMove(Square.sq(3), Square.sq(93), null));
        b.put(EMPTY, Square.sq("d", "10"));
        assertTrue(b.isUnblockedMove(Square.sq(3), Square.sq(93), null));
        b.put(SPEAR, Square.sq(3, 9));
        assertFalse(b.isUnblockedMove(Square.sq(3), Square.sq(93), null));
        b.put(EMPTY, Square.sq(93));
        assertTrue(b.isUnblockedMove(Square.sq(3), Square.sq(93), null));
    }

    /** Test legal stuff. */
    @Test
    public void testLegality() {
        Board b = new Board();
        assertTrue(b.isUnblockedMove(Square.sq(3), Square.sq(14), null));
        b.put(SPEAR, Square.sq(14));
        assertTrue(b.isLegal(Square.sq(3)));
        assertFalse(b.isUnblockedMove(Square.sq(3), Square.sq(14), null));
        assertFalse(b.isUnblockedMove(Square.sq(3), Square.sq(24), null));
        assertTrue(b.isUnblockedMove(Square.sq(3),
                Square.sq(25), Square.sq(14)));
        assertTrue(b.isUnblockedMove(Square.sq(3),
                Square.sq(13), null));
        assertTrue(b.isUnblockedMove(Square.sq(3),
                Square.sq(13), Square.sq(14)));
        assertTrue(b.isUnblockedMove(Square.sq(3),
                Square.sq(13), Square.sq(12)));
        assertTrue(b.isLegal(Square.sq(3), Square.sq(13), Square.sq(23)));
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    /** Tests reachableFromIterator to make sure it returns all reachable
     *  Squares. This method may need to be changed based on
     *   your implementation. */
    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMTESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMTEST.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMTEST.size(), numSquares);
        assertEquals(REACHABLEFROMTEST.size(), squares.size());
        b = new Board();
        b.put(WHITE, Square.sq(90));
        b.put(SPEAR, Square.sq(80));
        b.put(SPEAR, Square.sq(81));
        b.put(SPEAR, Square.sq(91));
        for (Iterator<Square> list = b.reachableFrom(Square.sq(90),
                null); list.hasNext(); list.next()) {
            System.out.println("hello");
        }
    }

    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        int numMoves = 0;
        Iterator<Move> legalMoves = b.legalMoves(WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(b.isLegal(m));
            numMoves += 1;
            if (!legalMoves.hasNext()) {
                System.out.println("hello");
            }
        }
        System.out.println(numMoves);
        numMoves = 0;

        b.makeMove(Move.mv("g1-g2(h2)"));
        System.out.printf("===%n%s===%n", b);
        b.makeMove(Move.mv("j7-j8(j9)"));
        System.out.printf("===%n%s===%n", b);
        legalMoves = b.legalMoves(WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(b.isLegal(m));
            numMoves += 1;
            if (!legalMoves.hasNext()) {
                System.out.println("hello");
            }
        }
        System.out.println(numMoves);
        b = new Board();
        int score = 0;
        ArrayList<Square> starts = new ArrayList<>();
        for (Square square : b.daBoard().keySet()) {
            if (b.daBoard().get(square) == WHITE) {
                starts.add(square);
            }
        }
        for (Square square : starts) {
            score += iteratorSize(b.reachableFrom(square, null));
        }
        System.out.println(score);
    }

    /** Returns size of ITERATOR. */
    private static int iteratorSize(Iterator iterator) {
        int result = 0;
        for ( ; iterator.hasNext(); result++) {
            iterator.next();
        }
        return result;
    }


    public static void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLEFROMTESTBOARD =
    {
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, W, W },
        { E, E, E, E, E, E, E, S, E, S },
        { E, E, E, S, S, S, S, E, E, S },
        { E, E, E, S, E, E, E, E, B, E },
        { E, E, E, S, E, W, E, E, B, E },
        { E, E, E, S, S, S, B, W, B, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
    };

    static final Piece[][] BRYANTEST =
    {
        { W, S, E, E, E, E, E, E, E, E },
        { E, S, E, E, E, E, E, S, S, S },
        { E, S, E, E, E, E, E, S, W, S },
        { S, S, E, S, S, S, S, S, E, S },
        { E, E, E, S, S, E, S, S, B, S },
        { E, E, E, S, S, W, E, S, B, E },
        { E, E, E, S, S, S, B, S, B, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
    };

    static final Set<Square> REACHABLEFROMTEST =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));



}
