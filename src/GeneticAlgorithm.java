import java.util.Random;

/**
 * Genetic Algorithm for the 0/1 Knapsack Problem.
 */
public class GeneticAlgorithm {

    // --- Configuration ---
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 1000;
    private static final double CROSSOVER_RATE = 0.8;
    private static final double MUTATION_RATE = 0.05;
    private static final int TOURNAMENT_SIZE = 5;

    private final Random rand;

    public GeneticAlgorithm(long seed) {
        this.rand = new Random(seed);
    }

    /**
     * Runs the GA on the given instance and returns the best value found.
     */
    public double solve(KnapsackInstance inst) {
        int n = inst.numItems;
        int[][] population = new int[POPULATION_SIZE][n];

    // 1. Initialize population randomly (Capacity-Aware)
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double currentWeight = 0;
            for (int j = 0; j < n; j++) {
                // Try to add the item randomly (50% chance), BUT only if it fits!
                if (rand.nextBoolean() && currentWeight + inst.weights[j] <= inst.capacity) {
                    population[i][j] = 1;
                    currentWeight += inst.weights[j];
                } else {
                    population[i][j] = 0;
                }
            }
        }

        int[] bestOverall = null;
        double bestOverallFitness = -1;

        // 2. Evolution Loop
        for (int gen = 0; gen < MAX_GENERATIONS; gen++) {
            int[][] newPopulation = new int[POPULATION_SIZE][n];

            // Find the best individual in the current generation (Elitism)
            int bestIdx = 0;
            double bestFit = fitness(population[0], inst);
            for (int i = 1; i < POPULATION_SIZE; i++) {
                double fit = fitness(population[i], inst);
                if (fit > bestFit) {
                    bestFit = fit;
                    bestIdx = i;
                }
            }

            // Update global best
            if (bestOverall == null || bestFit > bestOverallFitness) {
                bestOverallFitness = bestFit;
                bestOverall = population[bestIdx].clone();
            }

            // Carry over the best individual unchanged
            newPopulation[0] = population[bestIdx].clone();

            // Create the rest of the new generation
            for (int i = 1; i < POPULATION_SIZE; i += 2) {
                // Selection
                int[] p1 = select(population, inst);
                int[] p2 = select(population, inst);

                int[] c1 = p1.clone();
                int[] c2 = p2.clone();

                // Crossover
                if (rand.nextDouble() < CROSSOVER_RATE) {
                    crossover(p1, p2, c1, c2);
                }

                // Mutation
                mutate(c1);
                mutate(c2);

                newPopulation[i] = c1;
                if (i + 1 < POPULATION_SIZE) {
                    newPopulation[i + 1] = c2;
                }
            }

            population = newPopulation;
        }

        return bestOverallFitness;
    }

    // -------------------------------------------------------
    // Selection — Tournament Selection
    // -------------------------------------------------------
    private int[] select(int[][] population, KnapsackInstance inst) {
        int bestIdx = rand.nextInt(POPULATION_SIZE);
        double bestFit = fitness(population[bestIdx], inst);

        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            int idx = rand.nextInt(POPULATION_SIZE);
            double fit = fitness(population[idx], inst);
            if (fit > bestFit) {
                bestFit = fit;
                bestIdx = idx;
            }
        }
        return population[bestIdx];
    }

    // -------------------------------------------------------
    // Crossover — Single-Point Crossover
    // -------------------------------------------------------
    private void crossover(int[] p1, int[] p2, int[] c1, int[] c2) {
        int crossoverPoint = rand.nextInt(p1.length);
        for (int i = crossoverPoint; i < p1.length; i++) {
            c1[i] = p2[i];
            c2[i] = p1[i];
        }
    }

    // -------------------------------------------------------
    // Mutation — Bit-Flip Mutation
    // -------------------------------------------------------
    private void mutate(int[] individual) {
        for (int i = 0; i < individual.length; i++) {
            if (rand.nextDouble() < MUTATION_RATE) {
                individual[i] = 1 - individual[i]; // Flip bit
            }
        }
    }

    // -------------------------------------------------------
    // Fitness — total value if within capacity, else 0
    // -------------------------------------------------------
    private double fitness(int[] solution, KnapsackInstance inst) {
        double totalWeight = 0;
        double totalValue = 0;
        for (int i = 0; i < inst.numItems; i++) {
            if (solution[i] == 1) {
                totalWeight += inst.weights[i];
                totalValue += inst.values[i];
            }
        }
        return (totalWeight <= inst.capacity) ? totalValue : 0;
    }
}