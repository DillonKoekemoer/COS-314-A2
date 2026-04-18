# COS-314-A2 Knapsack Solver

This project solves 0/1 Knapsack instances using:
- Iterated Local Search (ILS)
- Genetic Algorithm (GA)

The program requests a seed value at startup and prints a comparison table.

## Requirements

- JDK 8 or newer installed
- `java`, `javac`, and `jar` available on PATH

## Project Layout

- `src/` - Java source files
- `data/` - Knapsack instance files (must be present when running)
- `out/` - Compiled class files
- `KnapsackSolver.jar` - Executable jar

## Compile From Source

Run these commands from the project root:

```powershell
javac -d out src/*.java
jar cfe KnapsackSolver.jar Main -C out .
```

## Run The Program

From the project root:

```powershell
java -jar KnapsackSolver.jar
```

You will be prompted to enter a seed value. Example:

```text
Enter seed value: 67
```

## Important Runtime Note

Keep the `data` folder in the same directory as `KnapsackSolver.jar` when running the jar.

## Quick Rebuild And Run

```powershell
javac -d out src/*.java
jar cfe KnapsackSolver.jar Main -C out .
java -jar KnapsackSolver.jar
```

