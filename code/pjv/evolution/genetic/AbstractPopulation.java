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
package pjv.evolution.genetic;

import java.util.Arrays;
import pjv.evolution.util.Edge;
import pjv.evolution.util.Pair;
import pjv.evolution.util.StateSpace;

/**
 * Population of individuals used by the evolutionary algorithm.
 *
 * @author Tomas Barton (modified by Jan Havlůj {@literal <jan@havluj.eu>})
 */
public class AbstractPopulation {

    /**
     * Array of all the individuals in the population.
     */
    protected AbstractIndividual[] individuals = null;

    /**
     * Average fitness in the population.
     */
    protected double avgFitness = 0;

    /**
     * Best fitness in the population.
     */
    private double bestFitness = 0;

    /**
     * Computes an average fitness of all the individuals in the population,
     * using their <code>getFitness</code> method.
     *
     * @return average fitness in the population
     */
    public double getAvgFitness() {
	  avgFitness = 0;
	  for (AbstractIndividual individual : individuals) {
		individual.getFitness();
		avgFitness += individual.getFitness();
		bestFitness = Math.max(bestFitness, individual.getFitness());
	  }
	  avgFitness = avgFitness / individuals.length;
	  return avgFitness;
    }

    /**
     * Returns the best individual (the elite) currently present in the
     * population, based of the values returned by the <code>getFitness</code>
     * method.
     *
     * @return The elite individual
     */
    public AbstractIndividual getBestIndividual() {
	  AbstractIndividual best = this.individuals[0];
	  for (AbstractIndividual individual : this.individuals) {
		if (Double.isNaN(best.getFitness()) || individual.getFitness() > best.getFitness()) {
		    best = individual;
		}
	  }
	  return best;
    }

    /**
     * Returns a <code>pair</code> of two integers. The first one is a number of
     * nodes that are activated and the second one is a number of how many edges
     * are not covered.
     *
     * @param individual Usually the best individual in a population
     * @return <code>pair</code> of two integers
     */
    public Pair getVertexCover(AbstractIndividual individual) {
	  Pair<Integer, Integer> pair = new Pair<>();
	  int activeNodeCounter = 0;
	  int notCoveredEdgesCount = 0;
	  for (int i = 0; i < StateSpace.nodesCount(); i++) {
		if (individual.isNodeSelected(i)) {
		    activeNodeCounter++;
		} else {
		    for (Edge edge : StateSpace.getNode(i).getEdges()) {
			  if (!individual.isNodeSelected(edge.getToId())) {
				notCoveredEdgesCount++;
			  }
		    }
		}
	  }
	  pair.a = activeNodeCounter;
	  pair.b = notCoveredEdgesCount;

	  return pair;
    }

    /**
     * Internally sorts the individuals in the population according to their
     * fitness values
     */
    public void sortByFitness() {
	  Arrays.sort(individuals);
    }

    /**
     * Gets a reference to the internal array of individuals.
     *
     * @return The array of individuals
     */
    public AbstractIndividual[] getIndividuals() {
	  return individuals;
    }

    /**
     * Gets the number of individuals in the population.
     *
     * @return Population size
     */
    public int size() {
	  return individuals.length;
    }

    /**
     * Gets an individual at a specified index.
     *
     * @param idx The index of the individual to be returned
     * @return The individual at the index given
     */
    public AbstractIndividual getIndividual(int idx) {
	  return this.individuals[idx];
    }

    /**
     *
     * @param index which individual to replace
     * @param individual new replacing individual
     */
    public void setIndividualAt(int index, AbstractIndividual individual) {
	  individuals[index] = individual;
    }

    /**
     * Gets the fitness of the elite individual in the population.
     *
     * @return Fitness of the fittest individual
     */
    public double getBestFitness() {
	  return bestFitness;
    }

    /**
     * Sets the fitness of the elite individual in the population.
     *
     * @param bestFitness The new elite fitness
     */
    public void setBestFitness(double bestFitness) {
	  this.bestFitness = bestFitness;
    }

    @Override
    public String toString() {
	  StringBuilder sb = new StringBuilder();
	  sb.append("=== POPULATION ===\n");
	  for (AbstractIndividual individual : individuals) {
		sb.append(individual.toString());
		sb.append("\n");
	  }
	  sb.append("=== avgFIT: ").append(avgFitness).append(" ===\n");
	  return sb.toString();
    }
}
