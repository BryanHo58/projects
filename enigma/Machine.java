package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Bryan Ho
 */
class Machine {

    /** A list of all the available rotors for the machine. */
    private Object[] _allRotors;

    /** The number of rotors in the machine. */
    private int _numRotors;

    /** The number of pawls in the machine. */
    private int _pawls;

    /** The plugboard used for the machine. */
    private Permutation _plugboard;

    /** A list of all the rotors placed within the machine. */
    private Rotor[] _rotors;
    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray();
        _rotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Returns the rotors of the machine. */
    Rotor[] rotors() {
        return _rotors;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) throws EnigmaException {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _allRotors.length; j++) {
                if (rotors[i].equals(((Rotor) _allRotors[j]).name())) {
                    _rotors[i] = (Rotor) _allRotors[j];
                }
            }
        }
        if (!_rotors[0].reflecting()) {
            throw error("first rotor should be reflector");
        }
        int totalRotors = 0;
        int movingRotors = 0;
        for (int i = 0; i < _rotors.length; i++) {
            if (_rotors[i] == null) {
                throw error("missing rotor");
            }
            if (_rotors[i].rotates()) {
                movingRotors += 1;
            }
            String name = _rotors[i].name();
            for (int j = 0; j < _allRotors.length; j++) {
                if (_rotors[i].name().equals
                        (((Rotor) _allRotors[j]).name())) {
                    totalRotors += 1;
                }
            }
            for (int j = i + 1; j < _rotors.length; j++) {
                if (_rotors[j] != null && name.equals(_rotors[j].name())) {
                    throw error("rotors cannot be repeated");
                }
            }
        }
        if (_rotors.length != rotors.length || totalRotors != numRotors()
                || movingRotors != numPawls()) {
            throw error("incorrect set of rotors");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) throws EnigmaException {
        if (setting.length() != (numRotors() - 1)) {
            throw error("incorrect amount of settings");
        }
        for (int i = 1; i < setting.length() + 1; i++) {
            char position = setting.charAt(i - 1);
            if (_alphabet.contains(position)) {
                _rotors[i].set(position);
            } else {
                throw error("position not valid in alphabet");
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Loops through the entire machine to configure all the
     * rotors according to notches each recursive call checks
     * the rotor at INDEX and decides whether it should
     * advance or not. Additionally, it checks if the PREV
     * rotor should advance or not. */
    void configurate(int index, boolean prev) {
        Rotor current = _rotors[index];
        if (index == numRotors() - 1) {
            if (current.atNotch() && _rotors[index - 1].rotates() && !prev) {
                _rotors[index - 1].advance();
            }
            current.advance();
        } else {
            if (current.rotates() && current.atNotch()
                    && _rotors[index - 1].rotates()) {
                if (!prev) {
                    _rotors[index - 1].advance();
                }
                current.advance();
                prev = true;
            } else {
                prev = false;
            }
            configurate(index + 1, prev);
        }
    }
    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        configurate(0, false);
        int signal = _plugboard.permute(c);
        for (int i = numRotors() - 1; i >= 0; i--) {
            signal =  _rotors[i].convertForward(signal);
        }

        for (int j = 1; j < numRotors(); j++) {
            signal = _rotors[j].convertBackward(signal);
        }

        return _plugboard.permute(signal);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            char translated = _alphabet.toChar
                    (convert(_alphabet.toInt(msg.charAt(i))));
            result += translated;
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
}
