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

import pjv.evolution.PJVEvolutionController;

/**
 * Abstract superclass for evolutionary algorithm. This class is to be inherited
 * to create concrete evolutionary algorithms.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>} (original by Tomas Barton)
 * @param <Individual> Individual in the evolution
 */
public abstract class AbstractEvolution<Individual extends AbstractIndividual> implements Runnable {

    /**
     * Number of generations.
     */
    protected int generations = 1000;

    /**
     * Size of the population.
     */
    protected int populationSize = 100;

    /**
     * Probability of mutation.
     */
    protected double mutationProbability = 0.01;

    /**
     * Probability of crossover.
     */
    protected double crossoverProbability = 0.25;

    /**
     * Reference to the GUI.
     */
    protected PJVEvolutionController context;

    /**
     * Gets the number of individuals in the population used by the evolutionary
     * algorithm.
     *
     * @return Size of the population
     */
    public int getPopulationSize() {
	  return populationSize;
    }

    /**
     * Sets the number of individuals in the population used by the evolutionary
     * algorithm.
     *
     * @param populationSize Size of the population
     */
    protected void setPopulationSize(int populationSize) {
	  this.populationSize = populationSize;
    }

    /**
     * Gets the mutation probability used by the evolutionary algorithm. Meaning
     * of this number is ambiguous, depending on the actual type of EA being
     * used. In case of genetic algorithm, it may encode the probability of
     * bit-flip mutation for each bit.
     *
     * @return The current mutation probability
     */
    public double getMutationProbability() {
	  return mutationProbability;
    }

    /**
     * Sets the mutation probability to be used by the evolutionary algorithm.
     * Meaning of this number is ambiguous, depending on the actual type of EA
     * being used. In case of genetic algorithm, it may encode the probability
     * of bit-flip mutation for each bit.
     *
     * @param prob The new mutation probability
     */
    protected void setMutationProbability(double prob) {
	  this.mutationProbability = prob;
    }

    /**
     * Gets the crossover probability used by the evolutionary algorithm. This
     * is the probability of selected the individuals to be crossed over before
     * being mutated.
     *
     * @return The current crossover probability
     */
    public double getCrossoverProbability() {
	  return crossoverProbability;
    }

    /**
     * Sets the crossover probability to be used by the evolutionary algorithm.
     * This is the probability of the selected individuals to be crossed over
     * before being mutated.
     *
     * @param prob The new crossover probability
     */
    protected void setCrossoverProbability(double prob) {
	  this.crossoverProbability = prob;
    }

    @Override
    public String toString() {
	  StringBuilder sb = new StringBuilder();
	  sb.append("generations = ").append(generations).append("\n");
	  sb.append("population = ").append(getPopulationSize()).append("\n");
	  sb.append("mutation = ").append(getMutationProbability()).append("\n");
	  sb.append("crossover = ").append(getCrossoverProbability()).append("\n");
	  sb.append("]");
	  return sb.toString();
    }
}
