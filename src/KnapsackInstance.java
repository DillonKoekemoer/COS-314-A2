import java.io.File;
import java.util.Scanner;

/**
 * Represents a single 0/1 Knapsack problem instance.
 * File format expected:
 *   Line 1: <numItems> <capacity>
 *   Lines 2..n+1: <value> <weight>
 */
public class KnapsackInstance {

    public String filename;
    public int    numItems;
    public double capacity;
    public double[] weights;
    public double[] values;

    /**
     * Loads a knapsack instance from the given file path.
     */
    public static KnapsackInstance fromFile(String path) throws Exception {
        KnapsackInstance inst = new KnapsackInstance();
        inst.filename = new File(path).getName();

        Scanner sc = new Scanner(new File(path));
        inst.numItems  = sc.nextInt();
        inst.capacity  = sc.nextDouble();
        inst.weights   = new double[inst.numItems];
        inst.values    = new double[inst.numItems];

        for (int i = 0; i < inst.numItems; i++) {
            inst.values[i]  = sc.nextDouble();
            inst.weights[i] = sc.nextDouble(); 
        }
        sc.close();
        return inst;
    }
}