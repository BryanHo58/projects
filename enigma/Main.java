package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Bryan Ho
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 "
                    + "command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        String setting;
        if (!_input.hasNext() || !_input.hasNextLine()) {
            throw error("incomplete file");
        }
        String nextLine = _input.nextLine();
        if (!nextLine.contains("*")) {
            throw error("message with no configuration");
        }
        while (_input.hasNext()) {
            setting = nextLine;
            setUp(enigma, setting);
            nextLine = _input.nextLine().toUpperCase();
            while (!nextLine.contains("*")) {
                nextLine = nextLine.replace(" ", "");
                String translated = enigma.convert(nextLine);
                if (nextLine.isEmpty()) {
                    _output.println(nextLine);
                } else {
                    printMessageLine(translated);
                }
                if (!_input.hasNext() && !_input.hasNextLine()) {
                    nextLine = "* end";
                } else {
                    nextLine = _input.nextLine().toUpperCase();
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet = _config.next();
            if (alphabet.contains("(") || alphabet.contains(")")
                    || alphabet.contains("*")) {
                throw error("invalid alphabet");
            }
            if (alphabet.contains("-")) {
                _alphabet = new CharacterRange(alphabet.charAt(0),
                        alphabet.charAt(2));
            } else {
                for (int i = 0; i < alphabet.length(); i++) {
                    for (int j = i + 1; j < alphabet.length(); j++) {
                        if (alphabet.charAt(i) == alphabet.charAt(j)) {
                            throw error(
                                    "letters cannot be repeated");
                        }
                    }
                }
                _alphabet = new CharacterMap(alphabet);
            }
            if (!_config.hasNext()) {
                throw error(
                        "invalid config - missing number of rotors");
            }
            int numRotors = _config.nextInt();
            if (!_config.hasNext()) {
                throw error(
                        "invalid config - missing number of prawls");
            }
            int pawls = _config.nextInt();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            current = _config.next().toUpperCase();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = current;
            String notches = _config.next().toUpperCase();
            char rotorType = notches.charAt(0);
            notches = notches.substring(1);
            String token = _config.next().toUpperCase();
            String permutation = "";
            while (token.contains("(") && _config.hasNext()) {
                if (!token.contains(")")) {
                    throw error("bad permutation formatting");
                }
                permutation = permutation.concat(token + " ");
                token = _config.next().toUpperCase();
                current = token;
            }
            if (!_config.hasNext()) {
                permutation += token + " ";
            }
            Permutation cipher = new Permutation(permutation, _alphabet);
            if (rotorType == 'M') {
                return new MovingRotor(name, cipher, notches);
            } else if (rotorType == 'N') {
                return new FixedRotor(name, cipher);
            } else {
                if (!cipher.derangement()) {
                    throw error("reflector permutation "
                            + "needs to be a derangement");
                } else {
                    return new Reflector(name, cipher);
                }
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        int numRotors = M.numRotors();
        if (!settings.contains("*")) {
            throw error("first line should be rotors");
        }
        if (settings.length() < 2 || settings.charAt(1) != ' ') {
            throw error("incorrect set up for rotors");
        }
        settings = settings.substring(2);
        String[] configuration = settings.split(" ");
        String[] rotors = new String[numRotors];
        for (int i = 0; i < numRotors; i++) {
            if (configuration[i].contains("(")) {
                throw error("incorrect amount of rotors");
            }
            rotors[i] = configuration[i];
        }
        M.insertRotors(rotors);
        if (!M.rotors()[0].reflecting()) {
            throw error(("first rotor should be a reflector"));
        }
        String positions = configuration[numRotors];
        M.setRotors(positions);
        if (positions.contains("(") || positions.length() != (numRotors - 1)) {
            throw error("incorrect initial settings");
        }
        String plugboard = "";
        for (int i = numRotors + 1; i < configuration.length; i++) {
            if (!configuration[i].contains("(")) {
                throw error("incorrect formatting of plugboard");
            }
            plugboard = plugboard.concat(configuration[i] + " ");
        }
        M.setPlugboard(new Permutation(plugboard, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int groups = msg.length() / 5;
        if (msg.length() % 5 > 0) {
            groups += 1;
        }
        for (int i = 0; i < groups; i++) {
            if (msg.length() > 5) {
                _output.print(msg.substring(0, 5) + " ");
                msg = msg.substring(5);
            } else {
                if (msg.length() == 5) {
                    _output.println(msg + " ");
                } else {
                    _output.println(msg);
                }
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** A non-permanent string that helps keep track of rotors. */
    private String current;
}
