package enigma;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import static org.junit.Assert.*;
import static enigma.TestUtils.*;

public class MachineTest {
    private Machine machine;
    static final Alphabet TEST = new CharacterRange('A', 'C');
    static final ArrayList<Rotor> ALL_ROTORS = new ArrayList<>();
    static {
        ALL_ROTORS.add(new Reflector("B", new Permutation(
                "(AE) (BN) (CK) (DQ) (FU) (GY) "
                        + "(HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", UPPER)));
        ALL_ROTORS.add(new FixedRotor("BETA", new Permutation(
                "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", UPPER)));
        ALL_ROTORS.add(new MovingRotor("I", new Permutation(
                "(AELTPHQXRU) (BKNW) (CMOY) "
                        + "(DFG) (IV) (JZ) (S)", UPPER), "Q"));
        ALL_ROTORS.add(new MovingRotor("II", new Permutation(
                "(FIXVYOMW) (CDKLHUP) (ESZ) "
                        + "(BJ) (GR) (NT) (A) (Q)", UPPER), "E"));
        ALL_ROTORS.add(new MovingRotor("III", new Permutation(
                "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER), "V"));
        ALL_ROTORS.add(new MovingRotor("IV", new Permutation(
                "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", UPPER), "J"));
        ALL_ROTORS.add(new Reflector("Test1", new Permutation("", TEST)));
        ALL_ROTORS.add(new FixedRotor("Test2", new Permutation("", TEST)));
        ALL_ROTORS.add(new MovingRotor("Test3",
                new Permutation("", TEST), "C"));
        ALL_ROTORS.add(new MovingRotor("Test4",
                new Permutation("", TEST), "C"));

    }
    Rotor extraRotor = new FixedRotor("Extra", new Permutation("", TEST));

    private String[] enigma = {"B", "BETA", "III", "IV", "I"};
    private String[] testEnigma = {"Test1", "Test2", "Test3", "Test4"};
    private String[] testEnigma2 = {"Test2", "BETA", "III", "IV", "I"};
    private String[] testEnigma3 = {"B", "BETA", "III", "IV"};
    private String[] testEnigma4 = {"B", "BETA", "III", "IV", "Extra"};

    private void setMachine(Alphabet alpha, int numrotors,
                            int pawls, Collection<Rotor> allRotors) {
        machine = new Machine(alpha, numrotors, pawls, allRotors);
    }

    @Test
    public void testConvert() {
        String input = "FROMHISSHOULDERHIAWATHATOOKTHECAMERAO"
                + "FROSEWOODMADEOFSLIDINGFOLDINGROSEWOOD";
        String output = "QVPQSOKOILPUBKJZPISFXDWBHCNSCXNUOAATZXS"
                + "RCFYDGUFLPNXGXIXTYJUJRCAUGEUNCFMKUF";
        setMachine(UPPER, 5, 3, ALL_ROTORS);
        machine.insertRotors(enigma);
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation(
                "(HQ) (EX) (IP) (TR) (BY)", UPPER));
        String result = machine.convert(input);
        assertEquals("incorrect forward conversion", output, result);
        machine.setRotors("AXLE");
        result = machine.convert(output);
        assertEquals("incorrect backward conversion", input, result);
        setMachine(TEST, 4, 2, ALL_ROTORS);
        machine.insertRotors(testEnigma);
        machine.setPlugboard(new Permutation("(AB) (C)", TEST));
        machine.convert("AAAAAAAAAAAAAAAAAAAA");
        for (Rotor r : machine.rotors()) {
            System.out.println(r.setting());
        }
    }

    @Test
    public void testErrors() {
        setMachine(UPPER, 5, 3, ALL_ROTORS);
        machine.insertRotors(enigma);
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation(
                "(HQ) (EX) (IP) (TR) (BY)", UPPER));
        try {
            machine.setRotors("AXL!");
            fail("error not found");
        } catch (EnigmaException e) {
            System.out.println("caught setting alphabet error");
            try {
                setMachine(UPPER, 5, 3, ALL_ROTORS);
                machine.insertRotors(testEnigma2);
                fail("error not found");
            } catch (EnigmaException f) {
                System.out.println("caught reflector error");
                try {
                    setMachine(UPPER, 5, 3, ALL_ROTORS);
                    machine.insertRotors(testEnigma3);
                    fail("error not found");
                } catch (EnigmaException g) {
                    System.out.println("caught rotor length error");
                    try {
                        setMachine(UPPER, 5, 3, ALL_ROTORS);
                        machine.insertRotors(testEnigma4);
                        fail("error not found");
                    } catch (EnigmaException h) {
                        System.out.println("caught undefined rotor error");
                        try {
                            setMachine(UPPER, 5, 3, ALL_ROTORS);
                            machine.insertRotors(enigma);
                            machine.setRotors("AAA");
                            fail("error not found");
                        } catch (EnigmaException i) {
                            System.out.println("caught setting length error");
                        }
                    }
                }
            }
        }
    }
}
