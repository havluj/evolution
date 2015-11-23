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
package pjv.evolution;

import pjv.evolution.genetic.AbstractIndividual;

/**
 * Interface that represents all the GUI controlling methods we can call from
 * inside the evolution.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>}
 */
public interface GuiCommunication {

    /**
     * Refreshes generation counter and progress bar in the GUI.
     *
     * @param gen generation number
     */
    void refreshGeneration(int gen);

    /**
     * Refreshes vertex cover number and plot in the GUI.
     *
     * @param total number of nodes
     * @param enabled number of nodes turned on
     */
    void refreshVertexCover(int total, int enabled);

    /**
     * Refreshes covered and uncovered edge numbers in the GUI.
     *
     * @param total number of edges
     * @param uncovered number of nodes that are not covered
     */
    void refreshEdgeCoverage(int total, int uncovered);

    /**
     * Refreshes best and average fitness number and plot in the GUI.
     *
     * @param avgFitness average fitness in the population
     * @param bestFitness best fitness in the population
     * @param gen generation count
     */
    void refreshFitness(double avgFitness, double bestFitness, int gen);

    /**
     * When the evolution stops, call this method to enable all the GUI buttons.
     */
    public void evolutionStopped();

    /**
     * Redraws the map canvas according to the current individual.
     *
     * @param individual usually the best individual from a population
     */
    public void redrawMap(AbstractIndividual individual);

    /**
     * Show or hide the information panel that the first population in evolution
     * is being generated.
     *
     * @param visibility true for making it visible, false for hiding it
     */
    public void setFirstGenerationPanelVisibility(boolean visibility);
}
