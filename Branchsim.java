import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class GHistory {
    private int bits;
    private int mask;
    private int value;

    public GHistory(int bits) {
        this.bits = bits;
        this.mask = (int) (Math.pow(2, bits) - 1);
        this.value = 0;
    }

    public String getHistory() {
        return Integer.toBinaryString(this.value);
    }

    public String addHistory(String actual) {
        this.value = (this.value << 1);

        if(actual.equals("T")){
            this.value += 1;
        }

        this.value &= this.mask;
        return this.getHistory();
    }
}

public class Branchsim {

    private static final String TAKEN = "T";
    private static final String NOT_TAKEN = "N";

    // 2-bit predictor
    private static final String TAKEN_STRONG = "11";
    private static final String TAKEN_WEAK = "10";
    private static final String NOT_TAKEN_STRONG = "00";
    private static final String NOT_TAKEN_WEAK = "01";

    public static void main(String[] args) throws IOException {
        // 1. read arguments (fileName, m, n, bits_to_index)
        if(args.length < 4){
            System.out.println("Usage: java Branchscim [FILE_NAME] [M_HISTORY_BIT] [N_BIT_PREDICTOR] [BITS_TO_INDEX]");
            System.out.println("FILE_NAME ∈ {gcc-8M.txt, gcc-10k.txt, 2bit-good}");
            System.out.println("M_HISTORY_BIT ∈ [0, 12]");
            System.out.println("N_BIT_PREDICTOR ∈ [1, 2]");
            System.out.println("BITS_TO_INDEX ∈ [4, 12]");
            return;
        }
        String fileName = args[0];
        int m = Integer.parseInt(args[1]);
        int n = Integer.parseInt(args[2]);
        int bitsToIndex = Integer.parseInt(args[3]);
        if(m > 12 || bitsToIndex > 12 || n > 2 || n < 1) {
            //TODO throw exception
            return;
        }

        // 2. Init the table and global history
        GHistory history = new GHistory(m);
        Map<String, String[]> BHT = initBranchHistoryTable(m, n, bitsToIndex);

        // 3. Run the predictor
        int total = 0;
        int accurateCount = 0;
        String trace;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((trace = br.readLine()) != null) {
            String[] tokens = trace.split(" ");
            String address = tokens[0]; // "480558"
            String actual = tokens[1];  // "T" or "N"

            // 3.1 get the predictor and do predict
            int key = getKeyByAddress(address, bitsToIndex);
            String predictor = BHT.get(history.getHistory())[key];
            String prediction = predict(predictor);

            // 3.3 do the statistics
            total ++;
            accurateCount += prediction.equals(actual) ? 1 : 0;

            // 3.3 update the predictor and global history
            String newState = "";
            if(n == 2){
                newState = getNextState_2bit(predictor, actual);
            }else {
                newState = actual;
            }

            String his = history.getHistory();
            BHT.get(his)[key] = newState;
            history.addHistory(actual);
        }

        float accuracy = (float) accurateCount / total;
        System.out.println("accuracy: " + accuracy + "%");

        return;
    }

    // ("48bec6", 8) -> c6 (in int)
    // ("48bec6", 4) -> 6 (in int)
    private static int getKeyByAddress(String address, int bitsToIndex){
        int mask = (int) (Math.pow(2, bitsToIndex) - 1);
        return Integer.parseInt(address, 16) & mask;
    }

    private static String predict(String predictor){
        if(predictor.equals(TAKEN_STRONG) || predictor.equals(TAKEN_WEAK)){
            return TAKEN;
        }else{
            return NOT_TAKEN;
        }
    }

    private static Map<String, String[]> initBranchHistoryTable(int m, int n, int bitsToIndex){

        Map<String, String[]> BHT = new HashMap<>();
        int max = (int) Math.pow(2, m);
        for(int i = 0; i < max; i++){
            String[] predictors = new String[(int)Math.pow(2, bitsToIndex)];

            if(n == 2){
                Arrays.fill(predictors, NOT_TAKEN_STRONG);
            }else{ // n == 1
                Arrays.fill(predictors, "0");
            }

            BHT.put(Integer.toBinaryString(i), predictors);
        }
        return BHT;
    }

    private static String getNextState_2bit(String currentStatus, String actual) {

        String state = "";
        switch (currentStatus) {
            case NOT_TAKEN_STRONG:
                if (actual.equals(TAKEN)) state = NOT_TAKEN_WEAK;
                else state = NOT_TAKEN_STRONG;
                break;
            case TAKEN_STRONG:
                if (actual.equals(TAKEN)) state = TAKEN_STRONG;
                else state = TAKEN_WEAK;
                break;

            case NOT_TAKEN_WEAK:
            case TAKEN_WEAK:
                if (actual.equals(TAKEN)) state = TAKEN_STRONG;
                else state = NOT_TAKEN_STRONG;
                break;
        }
        return state;
    }

}

//