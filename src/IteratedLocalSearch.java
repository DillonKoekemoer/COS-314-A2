import java.util.Arrays;
import java.util.Random;

/**
 * Iterated Local Search (ILS) for the 0/1 Knapsack Problem.
 *
 * Structure:
 *   1. Generate a greedy initial solution (sort by value/weight ratio)
 *   2. Improve it with bit-flip hill climbing (local search)
 *   3. Repeat for MAX_ITERATIONS:
 *        a. Perturb current solution (flip PERTURB_STRENGTH random bits)
 *        b. Apply local search to the perturbed solution
 *        c. Accept if at least as good as current (greedy acceptance)
 *        d. Track global best
 */
public class IteratedLocalSearch {

    // --- Configuration ---
    private static final int MAX_ITERATIONS   = 1000;
    private static final int PERTURB_STRENGTH = 3;    // bits flipped during perturbation

    private final Random rand;

    public IteratedLocalSearch(long seed) {
        this.rand = new Random(seed);
    }

    // -------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------

    /**
     * Runs ILS on the given instance and returns the best value found.
     */
    public double solve(KnapsackInstance inst) {
        int[] current = generateInitialSolution(inst);
        current       = localSearch(current, inst);
        int[] best    = current.clone();

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            int[] perturbed = perturb(current, PERTURB_STRENGTH);
            int[] candidate = localSearch(perturbed, inst);

            // Greedy acceptance: move to candidate if it is at least as good
            if (fitness(candidate, inst) >= fitness(current, inst)) {
                current = candidate;
            }

            // Update global best
            if (fitness(current, inst) > fitness(best, inst)) {
                best = current.clone();
            }
        }

        return fitness(best, inst);
    }

    // -------------------------------------------------------
    // Initial solution — greedy by value/weight ratio
    // -------------------------------------------------------

    private int[] generateInitialSolution(KnapsackInstance inst) {
        int n         = inst.numItems;
        int[] sol     = new int[n];
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;

        // Sort descending by value-to-weight ratio
        Arrays.sort(idx, (a, b) ->
            Double.compare(inst.values[b] / inst.weights[b],
                           inst.values[a] / inst.weights[a]));

        double totalWeight = 0;
        for (int i : idx) {
            if (totalWeight + inst.weights[i] <= inst.capacity) {
                sol[i]       = 1;
                totalWeight += inst.weights[i];
            }
        }
        return sol;
    }

    // -------------------------------------------------------
    // Local search — bit-flip hill climbing
    // -------------------------------------------------------

    /**
     * Repeatedly scans all items and flips any bit that improves fitness.
     * Stops when a full pass produces no improvement.
     */
    private int[] localSearch(int[] solution, KnapsackInstance inst) {
        boolean improved = true;
        int[] current    = solution.clone();

        while (improved) {
            improved = false;
            for (int i = 0; i < inst.numItems; i++) {
                current[i] = 1 - current[i];              // flip bit i

                if (fitness(current, inst) > fitness(solution, inst)) {
                    solution = current.clone();            // accept improvement
                    improved = true;
                } else {
                    current[i] = 1 - current[i];          // revert
                }
            }
            current = solution.clone();
        }
        return solution;
    }

    // -------------------------------------------------------
    // Perturbation — flip k random bits to escape local optima
    // -------------------------------------------------------

    private int[] perturb(int[] solution, int k) {
        int[] newSol = solution.clone();
        for (int i = 0; i < k; i++) {
            int idx = rand.nextInt(newSol.length);
            newSol[idx] = 1 - newSol[idx];
        }
        return newSol;
    }

    // -------------------------------------------------------
    // Fitness — total value if within capacity, else 0 (infeasible)
    // -------------------------------------------------------

    public double fitness(int[] solution, KnapsackInstance inst) {
        double totalWeight = 0;
        double totalValue  = 0;
        for (int i = 0; i < inst.numItems; i++) {
            if (solution[i] == 1) {
                totalWeight += inst.weights[i];
                totalValue  += inst.values[i];
            }
        }
        return (totalWeight <= inst.capacity) ? totalValue : 0;
    }
}