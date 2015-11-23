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
import pjv.evolution.genetic.AbstractEvolution;
import pjv.evolution.genetic.AbstractIndividual;
import pjv.evolution.genetic.AbstractPopulation;

/**
 * Concrete implementation of population of individuals used by the evolutionary
 * algorithm.
 * 
 * @author Jan Havlůj {@literal <jan@havluj.eu>}
 */
public class Population extends AbstractPopulation {

    /**
     * Initialize a population in the evolution. Not randomly.
     *
     * @param evolution current evolution reference
     * @param size size of population
     */
    public Population(AbstractEvolution evolution, int size) {
	  individuals = new Individual[size];
	  for (int i = 0; i < individuals.length; i++) {
		individuals[i] = new Individual(evolution, false);
		individuals[i].computeFitness();
	  }
    }

    /**
     * Method to select individuals from population. Selects the best N
     * individuals.
     *
     * Roulette wheel selection: If Fi is the fitness of individual i in the
     * population, its probability of being selected is Pi = (Fi/sum of all the
     * fitness in population)
     *
     * @param count The number of individuals to be selected
     * @return List of selected individuals
     */
    public List<AbstractIndividual> selectIndividuals(int count) {
	  ArrayList<AbstractIndividual> selected = new ArrayList<>();

	  Random r = new Random();
	  // find best individual and calculate the sum of individuals' fitnesses
	  AbstractIndividual best = this.individuals[0];
	  double totalFitness = 0.0;
	  for (AbstractIndividual individual : this.individuals) {
		totalFitness += individual.getFitness();
		if (individual.getFitness() > best.getFitness()) {
		    best = individual;
		}
	  }

	  int index;
	  while (selected.size() < count) {
		while (true) {
		    index = (r.nextInt(individuals.length));
		    if (r.nextDouble() < (individuals[index].getFitness() / totalFitness)) {
			  break;
		    }
		}
		selected.add(individuals[index]);
	  }

	  /**
	   * Tournament selection. we split the population into N groups of
	   * individuals and we pick the best one out of each group.
	   */
	  /*
	   int split = individuals.length / count;
	   for(int i = 0; i < count; i++) {
	   AbstractIndividual best = this.individuals[split * i];
	   for (int j = (split * i); j < (split * (i + 1)); j++) {
	   if (individuals[j].getFitness() > best.getFitness() && !selected.contains(individuals[j])) {
	   best = individuals[j];
	   }
	   }
	   selected.add(best);
	   }
	   */
	  /**
	   * elitism: select the best N individuals.
	   *
	   * Doesn't work well, leads to premature convergence.
	   */
	  /*
	   while (selected.size() < count) {
	   AbstractIndividual best = this.individuals[0];
	   for (AbstractIndividual individual : this.individuals) {
	   if (individual.getFitness() > best.getFitness() && !selected.contains(individual)) {
	   best = individual;
	   }
	   }
	   selected.add(best);
	   }
	   */
	  /**
	   * Random selection. Primitive, doesn't work well
	   */
	  /*
	   Random r = new Random();
	   AbstractIndividual individual = individuals[r.nextInt(individuals.length)];
	   while (selected.size() < count) {
	   selected.add(individual);
	   individual = individuals[r.nextInt(individuals.length)];
	   }
	   */
	  return selected;
    }
}
