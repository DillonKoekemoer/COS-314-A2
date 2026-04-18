import java.io.File;
import java.util.Scanner;

public class KnapsackInstance 
{

    public String filename;
    public int numItems;
    public double capacity;
    public double[] weights;
    public double[] values;

   
     //Laai knapsack instance van file
    
    public static KnapsackInstance fromFile(String path) throws Exception 
    {
        KnapsackInstance inst = new KnapsackInstance();
        inst.filename = new File(path).getName();

        Scanner scanner = new Scanner(new File(path));
        inst.numItems  = scanner.nextInt();
        inst.capacity = scanner.nextDouble();
        inst.weights = new double[inst.numItems];
        inst.values = new double[inst.numItems];

        for (int i = 0; i < inst.numItems; i++) 
        {
            inst.values[i]  = scanner.nextDouble();
            inst.weights[i] = scanner.nextDouble(); 
        }
        scanner.close();
        return inst;
    }
}