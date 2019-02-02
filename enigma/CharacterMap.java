package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** An Alphabet consisting of the Unicode characters in a certain range in
 *  order.
 *  @author Bryan Ho
 */
class CharacterMap extends Alphabet {

    /** Mapping of character values to integers. */
    private HashMap<Character, Integer> _library
            = new HashMap<Character, Integer>();

    /** Reverse of the above mapping. */
    private HashMap<Integer, Character> _libraryInverse =
            new HashMap<Integer, Character>();

    /** An alphabet consisting of all characters in LIBRARY. */
    CharacterMap(String library) {
        for (int i = 0; i < library.length(); i++) {
            _library.put(library.charAt(i), i);
            _libraryInverse.put(i, library.charAt(i));
        }
    }

    @Override
    int size() {
        return _library.size();
    }

    @Override
    boolean contains(char ch) {
        return _library.containsKey(ch);
    }

    @Override
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw error("character index out of range");
        }
        return _libraryInverse.get(index);
    }

    @Override
    int toInt(char ch) {
        if (!_library.containsKey(ch)) {
            throw error("character not valid in alphabet");
        }
        return _library.get(ch);
    }
}

