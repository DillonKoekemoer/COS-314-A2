import java.util.Random;

public class GeneticAlgorithm {

    private static final int popSize = 100;
    private static final int maxGens = 1000;
    private static final double crossRate = 0.75;
    private static final double mutateRate = 0.05;
    private static final int tournamentSize = 5;
    private final Random rand;

    public GeneticAlgorithm(long seed) 
    {
        this.rand = new Random(seed);
    }

    public double solve(KnapsackInstance inst) 
    {
        int n = inst.numItems;
        int[][] population = new int[popSize][n];

    //random pop
        for (int i = 0; i < popSize; i++) 
            {
            double currentWeight = 0;
            for (int j = 0; j < n; j++) 
                {
                //add randomly net as dit in pas
                if (rand.nextBoolean() && currentWeight + inst.weights[j] <= inst.capacity) 
                    {
                    population[i][j] = 1;
                    currentWeight += inst.weights[j];
                } else 
                    {
                    population[i][j] = 0;
                }
            }
        }

        int[] bestOverall = null;
        double bestOverallFitness = -1;

        //evo loop
        for (int gen = 0; gen < maxGens; gen++) 
            {
            int[][] newPopulation = new int[popSize][n];

            //elitism
            int bestId = 0;
            double bestFit = fitness(population[0], inst);
            for (int i = 1; i < popSize; i++) 
                {
                double fit = fitness(population[i], inst);
                if (fit > bestFit) {
                    bestFit = fit;
                    bestId = i;
                }
            }

            //update global best
            if (bestOverall == null || bestFit > bestOverallFitness) 
                {
                bestOverallFitness = bestFit;
                bestOverall = population[bestId].clone();
            }

            newPopulation[0] = population[bestId].clone();

            //maak res van die nuwe gen
            for (int i = 1; i < popSize; i += 2) 
                {
                //selection
                int[] p1 = select(population, inst);
                int[] p2 = select(population, inst);

                int[] c1 = p1.clone();
                int[] c2 = p2.clone();

                //crossover
                if (rand.nextDouble() < crossRate) 
                    {
                    crossover(p1, p2, c1, c2);
                }

                //mutation
                mutate(c1);
                mutate(c2);

                repair(c1, inst);
                repair(c2, inst);

                newPopulation[i] = c1;
                if (i + 1 < popSize) 
                    {
                    newPopulation[i + 1] = c2;
                }
            }

            population = newPopulation;
        }

        return bestOverallFitness;
    }

  
    //tournament selection
  
    private int[] select(int[][] population, KnapsackInstance inst) 
    {
        int bestId = rand.nextInt(popSize);
        double bestFit = fitness(population[bestId], inst);

        for (int i = 1; i < tournamentSize; i++)
            {
            int idx = rand.nextInt(popSize);
            double fit = fitness(population[idx], inst);
            if (fit > bestFit) 
                {
                bestFit = fit;
                bestId = idx;
            }
        }
        return population[bestId];
    }


    //single pt crossover
    private void crossover(int[] p1, int[] p2, int[] c1, int[] c2) 
    {
        int crossoverPoint = rand.nextInt(p1.length);
        for (int i = crossoverPoint; i < p1.length; i++) 
            {
            c1[i] = p2[i];
            c2[i] = p1[i];
        }
    }
 
    private void mutate(int[] individual) {
        for (int i = 0; i < individual.length; i++) {
            if (rand.nextDouble() < mutateRate) {
                individual[i] = 1 - individual[i]; // Flip bit
            }
        }
    }

    //fitness
    private double fitness(int[] solution, KnapsackInstance inst) 
    {
        double totalWeight = 0;
        double totalValue = 0;
        for (int i = 0; i < inst.numItems; i++) 
            {
            if (solution[i] == 1) {
                totalWeight += inst.weights[i];
                totalValue += inst.values[i];
            }
        }
        return (totalWeight <= inst.capacity) ? totalValue : 0;
    }

  
    private void repair(int[] individual, KnapsackInstance inst) 
    {
        double totalWeight = 0;
        for (int i = 0; i < inst.numItems; i++) 
            {
            if (individual[i] == 1) 
                {
                totalWeight += inst.weights[i];
            }
        }

        while (totalWeight > inst.capacity) 
            {
            int worstId = -1;
            double worstRatio = Double.POSITIVE_INFINITY;

            for (int i = 0; i < inst.numItems; i++) 
                {
                if (individual[i] == 1)
                     {
                    double ratio = inst.values[i] / inst.weights[i];
                    if (ratio < worstRatio) 
                        {
                        worstRatio = ratio;
                        worstId = i;
                    }
                }
            }

            if (worstId == -1) 
                {
                break;
            }

            individual[worstId] = 0;
            totalWeight -= inst.weights[worstId];
        }
    }
}