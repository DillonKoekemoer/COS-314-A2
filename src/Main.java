import java.io.File;
import java.util.Scanner;

/**
 * Entry point for the Knapsack solver.
 *
 * Reads a seed from the user, then runs ILS on all 10 problem instances
 * and prints a results table.
 *
 * Expects problem files inside a folder called "data" in the working directory:
 *   data/f1_l-d_kp_10_269
 *   data/f2_l-d_kp_20_878
 *   ... etc.
 */
public class Main {

    private static final String DATA_FOLDER = "data";

    // Problem instance filenames — must match exactly what is in the data folder
    private static final String[] FILES = {
        "f1_l-d_kp_10_269",
        "f2_l-d_kp_20_878",
        "f3_l-d_kp_4_20",
        "f4_l-d_kp_4_11",
        "f5_l-d_kp_15_375",
        "f6_l-d_kp_10_60",
        "f7_l-d_kp_7_50",
        "knapPI_1_100_1000_1",
        "f8_l-d_kp_23_10000",
        "f9_l-d_kp_5_80",
        "f10_l-d_kp_20_879"
    };

    // Known optimums — for display in the results table only.
    // These are NOT passed to or used by any algorithm.
    private static final double[] KNOWN_OPTIMUMS = {
        295, 1024, 35, 23, 481.0694, 52, 107, 9147, 9767, 130, 1025
    };

    private static String repeatChar(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // --- Get seed from user ---
        Scanner sc = new Scanner(System.in);
        Long seed = null;
        while (seed == null) {
            System.out.print("Enter seed value: ");
            if (sc.hasNextLong()) {
                seed = sc.nextLong();
            } else {
                System.out.println("Invalid seed. Please enter a whole number.");
                sc.next();
            }
        }
        sc.close();

        // --- Print table header ---  
        System.out.println();
        System.out.printf("%-30s %-12s %-12s %-15s %-15s %-12s%n",
            "Problem Instance", "Algorithm", "Seed Value", "Best Solution", "Known Optimum", "Runtime (s)");
        System.out.println(repeatChar('-', 100));

        // --- Run ILS on each instance ---
        for (int i = 0; i < FILES.length; i++) {
            String path = DATA_FOLDER + File.separator + FILES[i];

            try {
                System.out.println();
                KnapsackInstance inst = KnapsackInstance.fromFile(path);

                // ILS
                IteratedLocalSearch ils = new IteratedLocalSearch(seed);
                long t1       = System.currentTimeMillis();
                double ilsBest = ils.solve(inst);
                double ilsTime = (System.currentTimeMillis() - t1) / 1000.0;

                System.out.printf("%-30s %-12s %-12d %-15.4f %-15.4f %-12.3f%n",
                    FILES[i], "ILS", seed, ilsBest, KNOWN_OPTIMUMS[i], ilsTime);

            // GA
                GeneticAlgorithm ga = new GeneticAlgorithm(seed);
                long t2       = System.currentTimeMillis();
                double gaBest = ga.solve(inst);
                double gaTime = (System.currentTimeMillis() - t2) / 1000.0;

                System.out.printf("%-30s %-12s %-12d %-15.4f %-15.4f %-12.3f%n",
                    "", "GA", seed, gaBest, KNOWN_OPTIMUMS[i], gaTime);

            } catch (Exception e) {
                System.err.println("Could not read: " + path + " — " + e.getMessage());
            }
        }
    }
}