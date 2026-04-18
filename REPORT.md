# COS-314 Assignment 2 Report

## 1. Genetic Algorithm (GA)

### (a) Functionality
The Genetic Algorithm solves each 0/1 knapsack instance as follows:

1. Representation:
- A candidate solution is a binary vector of length $n$ (number of items).
- Gene value 1 means the item is selected; 0 means not selected.

2. Initialization:
- Population of 100 individuals.
- Initialization is capacity-aware: each bit is randomly considered, but an item is only added if it still fits in the knapsack.

3. Fitness:
- Fitness is total selected value if total weight is within capacity.
- If overweight, fitness is 0.

4. Parent Selection:
- Tournament selection with tournament size 5.
- The highest-fitness candidate in each tournament is selected.

5. Variation Operators:
- Single-point crossover with probability 0.8.
- Bit-flip mutation with probability 0.05 per gene.

6. Feasibility Repair:
- After mutation/crossover, any overweight offspring are repaired by iteratively removing selected items with the worst value/weight ratio until feasible.

7. Survivor Policy:
- Elitism is used: the best individual of the current generation is copied unchanged to the next generation.
- Remaining population slots are filled with offspring.

8. Stopping Criterion:
- Runs for 1000 generations.
- Returns the best fitness found over all generations.

Overall, GA provides broad exploration (population, crossover, mutation) with strong exploitation via tournament pressure, elitism, and a repair operator that keeps search focused on feasible high-value regions.

### (b) Configuration Description And Experimental Setup
GA parameter values used in the code:
- Population size: 100
- Generations: 1000
- Crossover rate: 0.8
- Mutation rate: 0.05
- Tournament size: 5

Experimental setup:
- 11 benchmark instances from the data folder were used.
- Single seed value: 42.
- Runtime measured per instance with wall-clock timing in seconds.
- For each instance, GA is run once and compared against ILS and known optimum.

## 2. Iterated Local Search (ILS)

### (a) Functionality
The Iterated Local Search implementation follows this process:

1. Initial Solution Construction:
- Build a greedy feasible starting solution by sorting items descending by value/weight ratio.
- Add items while they fit capacity.

2. Local Search:
- Bit-flip hill climbing.
- Repeatedly scan all item positions; accept a bit flip if fitness strictly improves.
- Continue until a full pass yields no improvement (local optimum).

3. Perturbation:
- To escape local optima, flip 3 random bits (perturb strength = 3).
- Repair the result if it is infeasible, again by removing worst value/weight items until feasible.

4. Acceptance Criterion:
- Greedy acceptance: replace current solution if candidate fitness is at least as good.

5. Best-So-Far Tracking:
- The global best solution over all iterations is retained and returned.

6. Stopping Criterion:
- Runs for 1000 ILS iterations.

This ILS strongly exploits local structure (hill climbing) and uses lightweight random perturbations to restart search around new neighborhoods.

### (b) Configuration Description And Experimental Setup
ILS parameter values used in the code:
- Maximum iterations: 1000
- Perturbation strength: 3 random bit flips per iteration

Experimental setup:
- Same 11 benchmark instances as GA.
- Same seed value: 42.
- Runtime measured per instance with wall-clock timing in seconds.
- ILS and GA are executed sequentially on each instance for direct paired comparison.

## 3. Results Table

| Instance | Known Optimum | ILS Best | GA Best | ILS Runtime (s) | GA Runtime (s) |
|---|---:|---:|---:|---:|---:|
| f1_l-d_kp_10_269 | 295.0000 | 295.0000 | 295.0000 | 0.036 | 0.040 |
| f2_l-d_kp_20_878 | 1024.0000 | 1024.0000 | 1024.0000 | 0.002 | 0.046 |
| f3_l-d_kp_4_20 | 35.0000 | 35.0000 | 35.0000 | 0.000 | 0.018 |
| f4_l-d_kp_4_11 | 23.0000 | 22.0000 | 23.0000 | 0.000 | 0.019 |
| f5_l-d_kp_15_375 | 481.0694 | 481.0694 | 481.0694 | 0.001 | 0.026 |
| f6_l-d_kp_10_60 | 52.0000 | 52.0000 | 52.0000 | 0.011 | 0.022 |
| f7_l-d_kp_7_50 | 107.0000 | 107.0000 | 107.0000 | 0.000 | 0.014 |
| knapPI_1_100_1000_1 | 9147.0000 | 8900.0000 | 9147.0000 | 0.011 | 0.166 |
| f8_l-d_kp_23_10000 | 9767.0000 | 9767.0000 | 9767.0000 | 0.001 | 0.042 |
| f9_l-d_kp_5_80 | 130.0000 | 130.0000 | 130.0000 | 0.001 | 0.013 |
| f10_l-d_kp_20_879 | 1025.0000 | 1025.0000 | 1025.0000 | 0.002 | 0.038 |

Summary:
- Optimum reached: ILS 9/11, GA 11/11.
- Mean absolute optimum gap: ILS 22.5455, GA 0.0000.
- Mean percentage optimum gap: ILS 0.6407%, GA 0.0000%.
- Mean runtime: ILS 0.00591 s, GA 0.04036 s.
- ILS is approximately 6.83x faster on average.

## 4. Statistical Analysis (One-Tailed Wilcoxon Signed-Rank, $\alpha = 0.05$)

Note: Wilcoxon signed-rank tests paired median differences (not means). It is still an accepted non-parametric paired test for comparative performance.

### 4.1 Solution Quality Test
Define paired differences per instance as:
$$
 d_i = \text{GA}_i - \text{ILS}_i
$$

Hypotheses (one-tailed):
- $H_0$: No positive shift in paired differences (equivalent central tendency).
- $H_1$: GA produces larger best values than ILS ($d_i > 0$).

Computed from the results:
- Non-zero pairs: 2 (only instances f4 and knapPI_1_100_1000_1 differ).
- Test statistic: $W^+ = 3$.
- Exact one-tailed p-value: $p = 0.25$.

Decision:
- Since $p = 0.25 > 0.05$, fail to reject $H_0$.
- There is not enough statistical evidence at 5% level (with this sample and many ties) that GA is superior in solution quality.

### 4.2 Runtime Test
Define paired differences per instance as:
$$
 d_i = \text{GA time}_i - \text{ILS time}_i
$$

Hypotheses (one-tailed):
- $H_0$: No positive shift in paired runtime differences (equivalent central tendency).
- $H_1$: GA is slower than ILS ($d_i > 0$).

Computed from the results:
- Non-zero pairs: 11.
- Test statistic: $W^+ = 66$ (all positive differences).
- Exact one-tailed p-value: $p = 0.000488$.

Decision:
- Since $p = 0.000488 < 0.05$, reject $H_0$.
- GA runtime is significantly slower than ILS runtime at the 5% level.

## 5. Critical Analysis Of Results

1. Accuracy vs speed trade-off:
- GA achieved all 11 known optima but required significantly more time.
- ILS was much faster and still solved 9/11 instances optimally.

2. Robustness on larger/harder instance:
- The main quality difference appears on knapPI_1_100_1000_1, where ILS got trapped below optimum while GA reached the optimum.
- This suggests GA's population-based exploration and recombination help avoid some local-optimum traps that affect single-trajectory local search.

3. Statistical interpretation:
- The quality Wilcoxon test did not reach significance because most pairs are ties (only 2 informative differences), reducing test power.
- Runtime differences are consistent and strong across all instances, yielding clear statistical significance in favor of ILS speed.

4. Practical recommendation:
- If runtime is critical and near-optimal quality is acceptable, ILS is preferred.
- If maximizing solution quality/robustness is critical, GA is preferred despite higher computational cost.

5. Improvement opportunities:
- For ILS: adaptive perturbation strength, occasional random restarts, or multi-start ILS can improve robustness.
- For GA: reduce runtime via early stopping (no-improvement generations), smaller adaptive population, or parallel fitness evaluation.
