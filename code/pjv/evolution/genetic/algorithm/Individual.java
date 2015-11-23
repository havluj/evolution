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

import java.util.List;
import java.util.Random;
import java.util.Vector;
import pjv.evolution.genetic.AbstractEvolution;
import pjv.evolution.genetic.AbstractIndividual;
import pjv.evolution.util.Edge;
import pjv.evolution.util.Pair;
import pjv.evolution.util.StateSpace;

/**
 * Class for individuals in evolutionary algorithm. It declares the genotype,
 * among with related operations such as the mutation and the crossover, as well
 * as computing fitness.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>}
 */
public final class Individual extends AbstractIndividual {

    /**
     * Fitness of the individual.
     */
    private double fitness = Double.NaN;

    /**
     * Current evolution reference.
     */
    private final AbstractEvolution evolution;

    /**
     * Definition of individual's genotype.
     */
    private Vector<Boolean> genotype;

    /**
     * Creates a new individual.
     *
     * Either a random init or Simulated annealing.
     *
     * @param evolution The evolution object
     * @param randomInit <code>true</code> if the individual should be
     * initialized randomly (we do wish to initialize if we copy the individual)
     */
    public Individual(AbstractEvolution evolution, boolean randomInit) {
	  this.genotype = new Vector<>(StateSpace.nodesCount());
	  this.evolution = evolution;

	  Random r = new Random();

	  if (randomInit) {
		for (int i = 0; i < StateSpace.nodesCount(); i++) {
		    boolean x = r.nextBoolean();
		    genotype.add(x);
		}
		repair();
	  } else {
		// Set initial temp
		double temperature = 10000;
		// Cooling rate
		double coolingRate = 0.008;

		// initialize the individual
		for (int i = 0; i < StateSpace.nodesCount(); i++) {
		    //boolean x = r.nextBoolean();
		    genotype.add(false);
		}
		repair();

		Individual current = this.deepCopy();
		Individual best = this.deepCopy();

		int run = 0;
		// until frozen
		while (temperature > 1) {
		    // copy individual and mutate it little bit (to find a close neighbour)
		    Individual y = current.deepCopy();

		    // select random edge and flip which node is turned on
		    int randomIndex = r.nextInt(StateSpace.edgesCount());
		    int fromIndex = StateSpace.getEdge(randomIndex).getFromId();
		    int toIndex = StateSpace.getEdge(randomIndex).getToId();
		    // negate their values
		    y.genotype.set(fromIndex, !y.genotype.get(fromIndex));
		    y.genotype.set(toIndex, !y.genotype.get(toIndex));
		    y.repair();

		    double nextRandom = r.nextDouble();
		    // if the new one is better, replace the old one right away
		    // if the old one is worse, calculate probability of replacing
		    // the new one by the old one. We will get the probability as
		    // e ^ ((current.fitness - y.fitness) / temperature)
		    if (acceptanceProbability(current.fitness, y.fitness, temperature) > nextRandom) {
			  current = y.deepCopy();
		    } else {
			  int ahoj = 2;
		    }

		    // if current solution is better than the best one, replace it
		    if (current.fitness > best.fitness) {
			  best = current.deepCopy();
		    }

		    // lower the temperature
		    temperature *= 1 - coolingRate;
		    run++;
		}

		for (int i = 0; i < best.genotype.size(); i++) {
		    this.genotype.set(i, best.genotype.get(i));
		}
		this.fitness = best.fitness;
	  }

    }

    /**
     * The acceptance probability function takes in the old fitness, new
     * fitness, and current temperature and spits out a number between 0 and 1,
     * which is a sort of recommendation on whether or not to jump to the new
     * solution.
     *
     * @param oldFitness fitness of the previous individual
     * @param newFitness fitness of the new individual
     * @param temperature declining temperature for simulated annealing
     * @return probability of a new individual replacing the old one
     */
    public double acceptanceProbability(double oldFitness, double newFitness, double temperature) {
	  if (newFitness >= oldFitness) {
		return 1.0;
	  }

	  // smaller the exponent (bigger difference in fitnesses or lower
	  // temperature), smaller the chance
	  double exponent = (newFitness - oldFitness) * 10 / temperature;
	  double result = Math.exp(exponent);
	  return result;
    }

    @Override
    public boolean isNodeSelected(int j) {
	  return genotype.get(j);
    }

    /**
     * Evaluate the value of the fitness function for the individual. After the
     * fitness is computed, the <code>getFitness</code> may be called
     * repeatedly, saving computation time.
     */
    @Override
    public void computeFitness() {
	  this.fitness = 0;

	  genotype.stream().filter((gene) -> (!gene)).forEach((_item) -> {
		fitness += 10;
	  });

	  StateSpace.getEdges().stream().forEach((edge) -> {
		if ((isNodeSelected(edge.getFromId())) && (isNodeSelected(edge.getToId()))) {
		    // punish if both nodes are enabled
		    fitness -= 2;
		} else {
		    // only one of them is enable
		    // punish by tiny amount if the one with fewer edges is enabled
		    List<Edge> from = StateSpace.getNode(edge.getFromId()).getEdges();
		    List<Edge> to = StateSpace.getNode(edge.getToId()).getEdges();
		    if (from.size() > to.size()) {
			  if (isNodeSelected(edge.getToId())) {
				fitness -= 0.8;
			  }
		    } else {
			  if (isNodeSelected(edge.getFromId())) {
				fitness -= 0.8;
			  }
		    }
		}
	  });
    }

    /**
     * Only return the computed fitness value
     *
     * @return value of fitness function
     */
    @Override
    public double getFitness() {
	  return this.fitness;
    }

    /**
     * Make sure the solution is correct (that all the edges are covered) and if
     * not, repair it.
     *
     * This function should be called every time we make a change to the
     * genotype (mutate, crossover, init, etc.). If an edge is not covered, turn
     * on the node with more edges.
     */
    public void repair() {
	  StateSpace.getEdges().stream().filter((edge) -> (!(isNodeSelected(edge.getFromId()))
		    && !(isNodeSelected(edge.getToId())))).forEach((edge) -> {
			  // turn the one with more edges on
			  List<Edge> from = StateSpace.getNode(edge.getFromId()).getEdges();
			  List<Edge> to = StateSpace.getNode(edge.getToId()).getEdges();
			  if (from.size() > to.size()) {
				genotype.set(edge.getFromId(), Boolean.TRUE);
			  } else {
				genotype.set(edge.getToId(), Boolean.TRUE);
			  }
		    });
	  computeFitness();
    }

    /**
     * Does random changes in the individual's genotype, taking mutation
     * probability into account.
     *
     * @param mutationRate Probability of a bit being inverted, i.e. a node
     * being added to/removed from the vertex cover.
     */
    @Override
    public void mutate(double mutationRate) {

	  Random r = new Random();
	  for (int i = 0; i < genotype.size(); i++) {
		boolean result = r.nextDouble() < mutationRate;
		if (genotype.get(i) != result) {
		    genotype.set(i, result);
		}
	  }
	  repair();
    }

    /**
     * Crosses the current individual over with other individual given as a
     * parameter, yielding a pair of offsprings.
     *
     * Algorithm will run 3-8 point crossover.
     *
     * @param other The other individual to be crossed over with
     * @return A couple of offspring individuals
     */
    @Override
    public Pair crossover(AbstractIndividual other) {

	  Pair<Individual, Individual> result = new Pair();
	  Individual y = (Individual) other;

	  Random r = new Random();
	  int random = r.nextInt(5);
	  random += 3; // 3-8 points of crossover

	  Individual crossOne = new Individual(evolution, true);
	  Individual crossTwo = new Individual(evolution, true);

	  int split = this.genotype.size() / random;
	  for (int i = 0; i < random; i++) {
		int upperBound;
		if ((i - 1) == random) {
		    upperBound = this.genotype.size() + 1;
		} else {
		    upperBound = (split * (i + 1));
		}

		for (int j = (split * i); j < upperBound; j++) {
		    if (i % 2 == 0) {
			  crossOne.genotype.set(i, this.genotype.get(i));
			  crossTwo.genotype.set(i, y.genotype.get(i));
		    } else {
			  crossOne.genotype.set(i, y.genotype.get(i));
			  crossTwo.genotype.set(i, this.genotype.get(i));
		    }
		}
	  }

	  /*
	   // one-point crossover
	   // doesnt work that well
	   for (int i = 0; i < this.genotype.size(); i++) {
	   if(i < this.genotype.size() / random) {
	   crossOne.genotype.set(i, this.genotype.get(i));
	   crossTwo.genotype.set(i, y.genotype.get(i));
	   } else {
	   crossOne.genotype.set(i, y.genotype.get(i));
	   crossTwo.genotype.set(i, this.genotype.get(i));
	   }
	   }
	   */
	  crossOne.repair();
	  crossTwo.repair();

	  result.a = crossOne;
	  result.b = crossTwo;

	  return result;
    }

    /**
     * When you are changing an individual (ie at crossover) you probably don't
     * want to affect the old one (you don't want to destruct it). So you have
     * to implement "deep copy" of this object.
     *
     * @return identical individual
     */
    @Override
    public Individual deepCopy() {
	  Individual newOne = new Individual(evolution, true);

	  for (int i = 0; i < this.genotype.size(); i++) {
		newOne.genotype.set(i, this.genotype.get(i));
	  }
	  newOne.fitness = this.fitness;
	  return newOne;
    }

    /**
     * Get Hamming Distance.
     *
     * Use this function in evolution to find out which offspring is closer to
     * which parent.
     *
     * @param other the second individual
     * @return distance
     */
    public int getHammingDistance(AbstractIndividual other) {
	  int distance = 0;
	  for (int i = 0; i < genotype.size(); i++) {
		if (isNodeSelected(i) != other.isNodeSelected(i)) {
		    distance++;
		}
	  }

	  return distance;
    }

    /**
     * Return a string representation of the individual.
     *
     * @return The string representing this object.
     */
    @Override
    public String toString() {
	  StringBuilder sb = new StringBuilder();

	  sb.append(super.toString());
	  sb.append(" fitness: ").append(getFitness());

	  return sb.toString();
    }
}
