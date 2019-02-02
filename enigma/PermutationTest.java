package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Bryan Ho
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void myTests() {
        Permutation tester = new Permutation(
                "(AZYXWVUTSRQP) (ONMLKJIHGFEDCB)", UPPER);
        Permutation tester2 = new Permutation("(ABCD)", UPPER);
        Permutation tester3 = new Permutation("", UPPER);
        List<Character> list = new ArrayList<Character>();
        List<Character> translated = new ArrayList<Character>();
        List<Character> list2 = new ArrayList<Character>();
        List<Character> translated2 = new ArrayList<Character>();
        List<Character> list3 = new ArrayList<Character>();
        List<Character> translated3 = new ArrayList<Character>();
        for (char c : "HELLO".toCharArray()) {
            list.add(c);
        }
        for (char c : "GDKKN".toCharArray()) {
            translated.add(c);
        }
        for (char c : "ABCDE".toCharArray()) {
            list2.add(c);
        }
        for (char c : "BCDAE".toCharArray()) {
            translated2.add(c);
        }
        for (char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            list3.add(c);
        }
        for (char c : "ZOBCDEFGHIJKLMNAPQRSTUVWXY".toCharArray()) {
            translated3.add(c);
        }
        for (char c : list) {
            assert tester.invert(tester.permute(c)) == c;
        }
        for (char c : list2) {
            assert tester2.invert(tester2.permute(c)) == c;
        }
        for (int i = 0; i < list.size(); i++) {
            assert tester.permute(list.get(i)) == translated.get(i);
            assert tester.invert(translated.get(i)) == list.get(i);
            assert tester2.permute(list2.get(i)) == translated2.get(i);
            assert tester2.invert(translated2.get(i)) == list2.get(i);
        }
        for (int i = 0; i < list3.size(); i++) {
            assert tester.permute(list3.get(i)) == translated3.get(i);
            assert tester.invert(translated3.get(i)) == list3.get(i);
        }
        assertFalse(tester3.derangement());
        assertFalse(tester2.derangement());
        assertTrue(tester.derangement());
    }
}
