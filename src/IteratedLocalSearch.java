import java.util.Arrays;
import java.util.Random;


public class IteratedLocalSearch 
{

  
    private static final int MaxItetations   = 1000;
    private static final int PerturbStrength = 3;    //bits geflip tydens perturb

    private final Random rand;

    public IteratedLocalSearch(long seed) 
    {
        this.rand = new Random(seed);
    }

 
    
    public double solve(KnapsackInstance inst) 
    {
        int[] current = generateInitialSolution(inst);
        current = localSearch(current, inst);
        int[] best = current.clone();

        for (int iter = 0; iter < MaxItetations; iter++) 
        {
            int[] perturbed = perturb(current, PerturbStrength, inst);
            int[] candidate = localSearch(perturbed, inst);

            //greedy acceptance
            if (fitness(candidate, inst) >= fitness(current, inst))
            {
                current = candidate;
            }

            //global best update
            if (fitness(current, inst) > fitness(best, inst)) 
                {
                best = current.clone();
            }
        }

        return fitness(best, inst);
    }

  
    //initial solution


    private int[] generateInitialSolution(KnapsackInstance inst) 
    {
        int n = inst.numItems;
        int[] sol = new int[n];
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) 
        {
            idx[i] = i;
        }
        Arrays.sort(idx, (a, b) -> Double.compare(inst.values[b] / inst.weights[b],
                           inst.values[a] / inst.weights[a]));

        double totalWeight = 0;
        for (int i : idx) 
        {
            if (totalWeight + inst.weights[i] <= inst.capacity) 
            {
                sol[i]       = 1;
                totalWeight += inst.weights[i];
            }
        }
        return sol;
    }


    private int[] localSearch(int[] solution, KnapsackInstance inst) 
    {
        boolean improved = true;
        int[] current = solution.clone();

        while (improved) 
        {
            improved = false;
            for (int i = 0; i < inst.numItems; i++) 
            {
                current[i] = 1 - current[i];  //flip bit i

                if (fitness(current, inst) > fitness(solution, inst)) 
                {
                    solution = current.clone(); //aanvaar improvement
                    improved = true;
                } else 
                {
                    current[i] = 1 - current[i];  //gaan terug
                }
            }
            current = solution.clone();
        }
        return solution;
    }


    private int[] perturb(int[] solution, int k, KnapsackInstance inst) 
    {
        int[] newSol = solution.clone();
        for (int i = 0; i < k; i++) 
        {
            int idx = rand.nextInt(newSol.length);
            newSol[idx] = 1 - newSol[idx];
        }
        repair(newSol, inst);
        return newSol;
    }


    public double fitness(int[] solution, KnapsackInstance inst) 
    {
        double totalWeight = 0;
        double totalValue = 0;
        for (int i = 0; i < inst.numItems; i++) 
        {
            if (solution[i] == 1) 
                {
                totalWeight += inst.weights[i];
                totalValue  += inst.values[i];
            }
        }
        return (totalWeight <= inst.capacity) ? totalValue : 0;
    }

    private void repair(int[] solution, KnapsackInstance inst) 
    {
        double totalWeight = 0;
        for (int i = 0; i < inst.numItems; i++) 
        {
            if (solution[i] == 1)
            {
                totalWeight += inst.weights[i];
            }
        }

        while (totalWeight > inst.capacity) 
        {
            int worstIdx = -1;
            double worstRatio = Double.POSITIVE_INFINITY;

            for (int i = 0; i < inst.numItems; i++) 
                {
                if (solution[i] == 1) {
                    double ratio = inst.values[i] / inst.weights[i];
                    if (ratio < worstRatio) 
                    {
                        worstRatio = ratio;
                        worstIdx = i;
                    }
                }
            }

            if (worstIdx == -1) 
            {
                break;
            }

            solution[worstIdx] = 0;
            totalWeight -= inst.weights[worstIdx];
        }
    }
}