/*
 * The MIT License
 *
 * Copyright 2015 Jan Havlůj.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, this permission notice and the original author's 
 * name shall be included in all copies or substantial portions of the Software. 
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pjv.evolution.genetic.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.application.Platform;
import pjv.evolution.PJVEvolutionController;
import pjv.evolution.genetic.AbstractEvolution;
import pjv.evolution.genetic.AbstractIndividual;
import pjv.evolution.util.Pair;
import pjv.evolution.util.StateSpace;

/**
 * Concrete implementation of evolutionary algorithm. Inherits from <code>
 * AbstractEvolution</code>. Needs to be runnable as the evolution is to be run
 * in a separate thread.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>}
 */
public class Evolution extends AbstractEvolution<Individual> implements Runnable {

    /**
     * Start and final average fitness.
     */
    private final Pair<Double, Double> avgFitness;

    /**
     * Start and final best fitness in whole population.
     */
    private final Pair<Double, Double> bestFitness;

    /**
     * Start and final time.
     */
    private final Pair<Long, Long> time;

    /**
     * How often to print status of evolution.
     */
    private final int debugLimit = 100;

    /**
     * Instance of Random because of the frequent usage of random number
     * generation within the evolution.
     */
    private final Random rand = new Random();

    /**
     * The population to be used in the evolution.
     */
    Population population;

    /**
     * Configure the evolution.
     *
     * @param cl reference to the GUI controller
     * @param g generations count
     * @param pop population size
     * @param mr mutation rate
     * @param cp crossover probability
     */
    public Evolution(PJVEvolutionController cl, int g, int pop, double mr, double cp) {
	  generations = g;
	  populationSize = pop;
	  mutationProbability = mr;
	  crossoverProbability = cp;
	  context = cl;

	  avgFitness = new Pair<>();
	  bestFitness = new Pair<>();
	  time = new Pair<>();
    }

    @Override
    public void run() {
	  // Initialize the population, show the "generating first population" panel
	  Platform.runLater(() -> {
		context.setFirstGenerationPanelVisibility(true);
	  });
	  population = new Population(this, populationSize);

	  Random random = new Random();

	  // Collect initial system time, average fitness, and the best fitness
	  time.a = System.currentTimeMillis();
	  avgFitness.a = population.getAvgFitness();
	  AbstractIndividual best = population.getBestIndividual();
	  bestFitness.a = best.getFitness();

	  /// print out current population status
	  System.out.println(population);

	  // remember the best Individual of all times
	  AbstractIndividual topIndividual = population.getBestIndividual();

	  // implement the catastrophe
	  int catastropheCountdown = 200;
	  double lastFitness = population.getBestFitness();

	  // hide the "generating first population" panel
	  Platform.runLater(() -> {
		context.setFirstGenerationPanelVisibility(false);
	  });

	  boolean interrupted = false;

	  // Run evolution cycle for the number of generations set in GUI
	  for (int g = 0; g < generations; g++) {
		if (Thread.interrupted()) {
		    interrupted = true;
		    break;
		}

		// request to refresh the GUI
		int gen = g;
		Platform.runLater(() -> {
		    context.refreshFitness(population.getAvgFitness(), population.getBestFitness(), gen);
		    context.refreshGeneration(gen);
		    context.refreshVertexCover(StateSpace.nodesCount(), (int) population.getVertexCover(population.getBestIndividual()).a);
		    context.refreshEdgeCoverage(StateSpace.edgesCount(), (int) population.getVertexCover(population.getBestIndividual()).b);
		    // redraw map after 5 generations
		    if (gen % 10 == 0) {
			  context.redrawMap(population.getBestIndividual());
		    }
		});

		// initialize the next generation's population
		ArrayList<AbstractIndividual> newInds = new ArrayList<>();

		/**
		 * Catastrophe.
		 *
		 * if the best fitness has not changed for 200 generations, select
		 * couple of random individuals from the current population (not the
		 * best ones!) and by simulated annealing create the rest.
		 */
		if (lastFitness == population.getBestFitness()) {
		    catastropheCountdown--;
		} else {
		    lastFitness = population.getBestFitness();
		    catastropheCountdown = 200;
		}

		if (catastropheCountdown < 1) {
		    // select 8 parents
		    List<AbstractIndividual> parents = population.selectIndividuals(8);
		    parents.stream().forEach((parent) -> {
			  newInds.add(parent.deepCopy());
		    });
		    // if we don't add the top individual, we get a better result
		    // sometime, but the result at the end isn't the overall best
		    // solution we have found
		    newInds.add(topIndividual);
		    for (int i = newInds.size(); i < population.size(); i++) {
			  newInds.add(new Individual(this, true));
			  newInds.get(newInds.size() - 1).computeFitness();
		    }
		    catastropheCountdown = 200;
		} else {

		    // elitism: Preserve the best individual
		    // (this is quite exploatory and may lead to premature convergence!)
		    // ----
		    // I tried not to copy the best individiual, but the fitness at the 
		    // end is sometimes even worse than fitness we started with
		    newInds.add(population.getBestIndividual().deepCopy());

		    /**
		     * Deterministic crowding.
		     *
		     * If the offspring has a higher fitness than its parent, it
		     * will replace the parent. If the offsprings fitness is lower
		     * than its parents, we will keep both the offspring and the
		     * parent.
		     */
		    // keep filling the new population while not enough individuals in there
		    while (newInds.size() < populationSize) {

			  // select 2 parents
			  List<AbstractIndividual> parents = population.selectIndividuals(2);

			  Pair<AbstractIndividual, AbstractIndividual> offspring;
			  // with some probability, perform crossover
			  if (crossoverProbability < random.nextDouble()) {
				offspring = parents.get(0).deepCopy().crossover(
					  parents.get(1).deepCopy());
			  } else { // otherwise, only copy the parents
				offspring = new Pair<>();
				offspring.a = parents.get(0).deepCopy();
				offspring.b = parents.get(1).deepCopy();
			  }
			  // mutate first offspring, add it to the new population
			  offspring.a.mutate(mutationProbability);
			  offspring.a.computeFitness();
			  newInds.add(offspring.a);

			  // if there is still space left in the new population, add also
			  // the second offspring
			  if (newInds.size() < populationSize) {
				offspring.b.mutate(mutationProbability);
				offspring.b.computeFitness();
				newInds.add(offspring.b);
			  }

			  Individual a = (Individual) offspring.a;
			  Individual b = (Individual) offspring.b;

			  int aToFirst = a.getHammingDistance(parents.get(0));
			  int bToFirst = b.getHammingDistance(parents.get(0));
			  int aToSecond = a.getHammingDistance(parents.get(1));
			  int bToSecond = b.getHammingDistance(parents.get(1));

			  if (aToFirst < aToSecond) {
				if (offspring.a.getFitness() < parents.get(0).getFitness()) {
				    // if there is still space left in the new population
				    if (newInds.size() < populationSize) {
					  if (!newInds.contains(parents.get(0))) {
						newInds.add(parents.get(0).deepCopy());
					  }
				    }
				}
			  } else {
				if (offspring.a.getFitness() < parents.get(1).getFitness()) {
				    // if there is still space left in the new population
				    if (newInds.size() < populationSize) {
					  if (!newInds.contains(parents.get(1))) {
						newInds.add(parents.get(1).deepCopy());
					  }
				    }
				}
			  }

			  if (bToFirst < bToSecond) {
				if (offspring.b.getFitness() < parents.get(0).getFitness()) {
				    // if there is still space left in the new population
				    if (newInds.size() < populationSize) {
					  if (!newInds.contains(parents.get(0))) {
						newInds.add(parents.get(0).deepCopy());
					  }
				    }
				}
			  } else {
				if (offspring.b.getFitness() < parents.get(1).getFitness()) {
				    // if there is still space left in the new population
				    if (newInds.size() < populationSize) {
					  if (!newInds.contains(parents.get(1))) {
						newInds.add(parents.get(1).deepCopy());
					  }
				    }
				}
			  }
		    }
		}

		// replace the current population with the new one
		for (int i = 0; i < newInds.size(); i++) {
		    population.setIndividualAt(i, newInds.get(i));
		}

		if (topIndividual.getFitness() < population.getBestIndividual().getFitness()) {
		    topIndividual = population.getBestIndividual();
		}

		// print statistic
		System.out.println("gen: " + g + "\t bestFit: " + population.getBestIndividual().getFitness() + "\t avgFit: " + population.getAvgFitness());

		if (g % debugLimit == 0) {
		    best = population.getBestIndividual();
		}
	  }

	  if (!interrupted) {
		Platform.runLater(() -> {
		    context.refreshFitness(population.getAvgFitness(), population.getBestFitness(), generations);
		    context.refreshGeneration(generations);
		    context.refreshVertexCover(StateSpace.nodesCount(), (int) population.getVertexCover(population.getBestIndividual()).a);
		    context.refreshEdgeCoverage(StateSpace.edgesCount(), (int) population.getVertexCover(population.getBestIndividual()).b);
		    context.redrawMap(population.getBestIndividual());
		});
	  }

	  // === END ===
	  time.b = System.currentTimeMillis();
	  population.sortByFitness();
	  avgFitness.b = population.getAvgFitness();
	  bestFitness.b = best.getFitness();
	  //updateMap(best);
	  System.out.println("Evolution has finished after " + ((time.b - time.a) / 1000.0) + " s...");
	  System.out.println("avgFit(G:0)= " + avgFitness.a + " avgFit(G:" + (generations - 1) + ")= " + avgFitness.b + " -> " + ((avgFitness.b / avgFitness.a) * 100) + " %");
	  System.out.println("bstFit(G:0)= " + bestFitness.a + " bstFit(G:" + (generations - 1) + ")= " + bestFitness.b + " -> " + ((bestFitness.b / bestFitness.a) * 100) + " %");
	  System.out.println("bestIndividual in current population= " + population.getBestIndividual());
	  System.out.println("bestIndividual of all times= " + topIndividual);
	  //System.out.println(pop);

	  System.out.println("========== Evolution finished =============");

	  Platform.runLater(() -> {
		context.evolutionStopped();
	  });
    }
}
