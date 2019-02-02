package enigma;



import org.junit.Test;


public class MainTest {
    String[] trivial = {"testing/correct/default.conf",
                        "testing/correct/trivial1.inp",
                        "testing/correct/trivial1.out"};
    String[] hiawatha = {"testing/correct/default.conf",
                         "enigma/Hiawatha.inp",
                         "enigma/Hiawatha.out"};
    String[] hiawathaCustom = {"enigma/CustomAlphabet.conf",
                               "enigma/Hiawatha.inp",
                               "enigma/HiawathaCustom.out"};
    String[] allStar = {"testing/correct/default.conf",
                        "enigma/AllStar.inp",
                        "enigma/AllStar.out"};
    String[] errorTest = {"testing/correct/default.conf",
                          "enigma/errorTest.inp",
                          "enigma/errorTest.out"};


    @Test
    public void testMain() {
        Main.main(trivial);
        Main.main(hiawatha);
        Main.main(hiawathaCustom);
        Main.main(allStar);
        try {
            Main.main(errorTest);
        } catch (EnigmaException e) {
            System.out.println("caught bad file");
        }
    }
}

