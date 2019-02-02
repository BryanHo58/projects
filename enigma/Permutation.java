package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Bryan Ho
 */
class Permutation {

    /** An array of strings that contains the cyclic permutations. */
    private String[] _cycles;

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String copy = cycles;
        copy = copy.replace(" ", "");
        copy = copy.replace("(", "");
        copy = copy.replace(")", " ");
        copy = copy.trim();
        _cycles = copy.split(" ");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String[] newCopy = new String[_cycles.length + 1];
        for (int i = 0; i < newCopy.length; i++) {
            newCopy[i] = _cycles[i];
        }
        newCopy[_cycles.length + 1] = cycle;
        _cycles = newCopy;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return  _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char initial = alphabet().toChar(p % size());
        char translation;
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == initial) {
                    if (j == _cycles[i].length() - 1) {
                        translation = _cycles[i].charAt(0);
                    } else {
                        translation = _cycles[i].charAt(j + 1);
                    }
                    return alphabet().toInt(translation);
                }
            }
        }
        return p;

    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char initial = alphabet().toChar(c % size());
        char translation;
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == initial) {
                    if (j == 0) {
                        translation = _cycles[i].charAt
                                (_cycles[i].length() - 1);
                    } else {
                        translation = _cycles[i].charAt(j - 1);
                    }
                    return alphabet().toInt(translation);
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = alphabet().toInt(p);
        return alphabet().toChar(permute(index));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        int index = alphabet().toInt(c);
        return alphabet().toChar(invert(index));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < size(); i++) {
            char character = _alphabet.toChar(i);
            if (character == permute(character)
                    || character == invert(character)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
}
