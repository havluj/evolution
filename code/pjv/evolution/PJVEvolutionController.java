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

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pjv.evolution.genetic.AbstractIndividual;
import pjv.evolution.genetic.algorithm.Evolution;
import pjv.evolution.map.MapLoader;
import pjv.evolution.map.MapsBrowser;
import pjv.evolution.util.Edge;
import pjv.evolution.util.StateSpace;

/**
 * The main and only controller representing GUI for the genetic algorithm. This
 * controller is connected to FXMLDocument.fxml in this package.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>}
 */
public class PJVEvolutionController implements Initializable, GuiCommunication {

    /**
     * Drawing canvas where we draw the maps.
     */
    @FXML
    private Canvas mapCanvas;

    /**
     * By default hidden Panel, which we show when we compute the first
     * population in the evolution and hide when we are done.
     */
    @FXML
    private AnchorPane firstPopulationPanel;

    /**
     * Number of generations already computed.
     */
    @FXML
    private Text generationCount;

    /**
     * Progress bar of generations already computed.
     */
    @FXML
    private ProgressBar generationBar;

    /**
     * Percentage of how many nodes are enabled from the entire state space.
     */
    @FXML
    private Text vertexCover;

    /**
     * Pie chart that displays the ratio of nodes enabled and nodes disabled.
     */
    @FXML
    private PieChart vertexCoverPie;

    /**
     * Top individual's fitness in current population.
     */
    @FXML
    private Text fitness;

    /**
     * Line chart of average fitness and best fitness in each population across
     * generations.
     */
    @FXML
    private LineChart fitnessChart;

    /**
     * Number of edges where either of its nodes is turned on.
     */
    @FXML
    private Text uncoveredEdges;

    /**
     * Number of edges where at least one of its nodes is turned on.
     */
    @FXML
    private Text coveredEdges;

    /**
     * Number representation of generationSizeSlider.
     */
    @FXML
    private Text generationSize;

    /**
     * Slider that lets us configure how many generations we want in our next
     * evolution.
     */
    @FXML
    private Slider generationSizeSlider;

    /**
     * Number representation of populationSizeSlider.
     */
    @FXML
    private Text populationSize;

    /**
     * Slider that lets us configure how big population we want in each
     * generation of our next evolution.
     */
    @FXML
    private Slider populationSizeSlider;

    /**
     * Number representation of mutationRateSlider.
     */
    @FXML
    private Text mutationRate;

    /**
     * Slider that lets us configure what mutation rate we want in our next
     * evolution.
     */
    @FXML
    private Slider mutationRateSlider;

    /**
     * Number representation of crossoverProbabilitySlider.
     */
    @FXML
    private Text crossoverProbability;

    /**
     * Slider that lets us configure how large crossover probability we want in
     * our next evolution.
     */
    @FXML
    private Slider crossoverProbabilitySlider;

    /**
     * Select box that lets us choose which map should the evolutionary
     * algorithm try to solve. It shows all maps that have folders in the "maps"
     * folder in application's directory.
     */
    @FXML
    private ChoiceBox<String> mapSelect;

    /**
     * Button that starts the evolution.
     */
    @FXML
    private Button start;

    /**
     * Button that stops the evolution.
     */
    @FXML
    private Button stop;

    /**
     * A thread the evolution is running on.
     */
    Thread evo;

    /**
     * A class that loads the map into State Space.
     */
    private MapLoader map;

    /**
     * List of available maps.
     */
    private final ObservableList<String> mapItems = FXCollections.observableArrayList();

    /**
     * Slices for vertex cover pie chart.
     */
    private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
		new PieChart.Data("Enabled", 100),
		new PieChart.Data("Disabled", 0));

    /**
     * Variable that helps to determine whether it is necessary to redraw the
     * vertex cover pie chart.
     */
    private double lastVertexCover = 0.0;

    /**
     * A series that represents the individual data points of the best fitness
     * in each population.
     */
    private XYChart.Series BestFitnessLine;

    /**
     * A series that represents the individual data points of the average
     * fitness in each population.
     */
    private XYChart.Series AverageFitnessLine;

    /**
     * This method is called every time we initialize our application.
     *
     * @param url resource location; there is only one location in our
     * application, so there is no need to worry about it
     * @param rb no need to touch it, because we are using only English
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	  // add all found maps to the select box
	  mapItems.addAll(Arrays.asList(MapsBrowser.listAvailableMaps()));

	  // add individual lines to the fitness line chart
	  BestFitnessLine = new XYChart.Series();
	  BestFitnessLine.setName("Best Fitness");
	  AverageFitnessLine = new XYChart.Series();
	  AverageFitnessLine.setName("Average Fitness");
	  fitnessChart.getData().addAll(BestFitnessLine, AverageFitnessLine);

	  // set data for the vertex cover pie chart
	  vertexCoverPie.setData(pieChartData);

	  // fill the select map box and set the selected item to the first item in the list
	  mapSelect.setItems(mapItems);
	  mapSelect.setValue(mapItems.get(0));
	  map = new MapLoader(mapSelect.getValue());
	  drawMap();

	  // listener for change in the generation size slider
	  generationSizeSlider.valueProperty().addListener(
		    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			  generationSize.setText(Integer.toString(newValue.intValue()));
		    });

	  // listener for change in the population size slider	  
	  populationSizeSlider.valueProperty().addListener(
		    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			  populationSize.setText(Integer.toString(newValue.intValue()));
		    });

	  // listener for change in the mutation probability slider	  
	  mutationRateSlider.valueProperty().addListener(
		    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			  mutationRate.setText(Integer.toString(newValue.intValue()) + "%");
		    });

	  // listener for change in the crossover probability slider	  
	  crossoverProbabilitySlider.valueProperty().addListener(
		    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			  crossoverProbability.setText(Integer.toString(newValue.intValue()) + "%");
		    });

	  // listner for change in the map select box
	  mapSelect.getSelectionModel().selectedIndexProperty().addListener(
		    (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			  map = new MapLoader(mapItems.get((int) newValue));
			  drawMap();
		    });
    }

    /**
     * This method is called when the "start" button is pressed.
     */
    public void startEvolution() {
	  BestFitnessLine.getData().clear();
	  AverageFitnessLine.getData().clear();

	  // get the generation count, population size, mutation rate, crossover probability from gui
	  int gs = Integer.parseInt(generationSize.getText());
	  int ps = Integer.parseInt(populationSize.getText());
	  String mutRate = mutationRate.getText().substring(0, mutationRate.getText().length() - 1); // trim the last character (%)
	  String crossProb = crossoverProbability.getText().substring(0, crossoverProbability.getText().length() - 1); // trim the last character (%)
	  double mr = Double.parseDouble(mutRate) / 100;
	  double cp = Double.parseDouble(crossProb) / 100;

	  // start the evolution in a separate thread
	  Evolution evolution = new Evolution(this, gs, ps, mr, cp);
	  evo = new Thread(evolution);
	  evo.setDaemon(true);
	  evo.start();

	  // disable some gui elements
	  start.setDisable(true);
	  generationSizeSlider.setDisable(true);
	  populationSizeSlider.setDisable(true);
	  mutationRateSlider.setDisable(true);
	  crossoverProbabilitySlider.setDisable(true);
	  mapSelect.setDisable(true);
    }

    /**
     * This method is called when the button "stop" is pressed.
     */
    public void stopButtonClicked() {
	  evo.interrupt();
    }

    @Override
    public void evolutionStopped() {
	  // enable all the gui elements that have been disabled since we started the evolution
	  start.setDisable(false);
	  generationSizeSlider.setDisable(false);
	  populationSizeSlider.setDisable(false);
	  mutationRateSlider.setDisable(false);
	  crossoverProbabilitySlider.setDisable(false);
	  mapSelect.setDisable(false);
    }

    @Override
    public void refreshGeneration(int gen) {
	  generationCount.setText(Integer.toString(gen) + " / " + generationSize.getText());
	  generationBar.setProgress(gen / Double.parseDouble(generationSize.getText()));
    }

    @Override
    public void refreshVertexCover(int total, int enabled) {
	  // compute the current percentage of nodes enabled
	  double current = (double) Double.parseDouble(new java.text.DecimalFormat("0.000").format(((double) enabled / (double) total) * 100));

	  vertexCover.setText(Double.toString(current) + "%");

	  if (lastVertexCover != current) {
		lastVertexCover = current;
		pieChartData.get(0).setPieValue(current);
		pieChartData.get(1).setPieValue(100 - current);
	  }
    }

    @Override
    public void refreshEdgeCoverage(int total, int uncovered) {
	  uncoveredEdges.setText(Integer.toString(uncovered));
	  if (uncovered > 0) {
		uncoveredEdges.setFill(Color.RED);
	  } else {
		uncoveredEdges.setFill(Color.GREEN);
	  }

	  coveredEdges.setText(Integer.toString(total - uncovered));
    }

    @Override
    public void refreshFitness(double avgFitness, double bestFitness, int gen) {
	  fitness.setText(new java.text.DecimalFormat("+#,##0.00;-#").format(bestFitness));
	  BestFitnessLine.getData().add(new XYChart.Data(Integer.toString(gen), bestFitness));
	  AverageFitnessLine.getData().add(new XYChart.Data(Integer.toString(gen), avgFitness));
    }

    @Override
    public void redrawMap(AbstractIndividual individual) {
	  double rightSideLimit = 740;
	  double lowestPoint = 569;
	  mapCanvas.setHeight(lowestPoint);
	  mapCanvas.setWidth(rightSideLimit);

	  GraphicsContext gc = mapCanvas.getGraphicsContext2D();
	  gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight()); // CLEARING THE CANVAS

	  for (Edge edge : StateSpace.getEdges()) {
		double pointXA = StateSpace.getNode(edge.getFromId()).getPointX();
		double pointYA = StateSpace.getNode(edge.getFromId()).getPointY();
		double pointXB = StateSpace.getNode(edge.getToId()).getPointX();
		double pointYB = StateSpace.getNode(edge.getToId()).getPointY();

		if (individual.isNodeSelected(edge.getFromId()) || individual.isNodeSelected(edge.getToId())) {
		    gc.setStroke(Color.GREENYELLOW);
		} else {
		    gc.setStroke(Color.ORANGE);
		}
		gc.strokeLine(pointXA, pointYA, pointXB, pointYB);

		if (pointYA > lowestPoint) {
		    lowestPoint = pointYA;
		    mapCanvas.setHeight(lowestPoint + 20);
		}
		if (pointXA > rightSideLimit) {
		    rightSideLimit = pointXA;
		    mapCanvas.setWidth(rightSideLimit + 20);
		}
		if (individual.isNodeSelected(edge.getFromId())) {
		    gc.setFill(Color.ORANGERED);
		} else {
		    gc.setFill(Color.BLUE);
		}
		gc.fillOval(pointXA - 2, pointYA - 2, 4, 4);

		if (pointYB > lowestPoint) {
		    lowestPoint = pointYA;
		    mapCanvas.setHeight(lowestPoint + 20);
		}
		if (pointXB > rightSideLimit) {
		    rightSideLimit = pointXB;
		    mapCanvas.setWidth(rightSideLimit + 20);
		}
		if (individual.isNodeSelected(edge.getToId())) {
		    gc.setFill(Color.ORANGERED);
		} else {
		    gc.setFill(Color.BLUE);
		}
		gc.fillOval(pointXB - 2, pointYB - 2, 4, 4);
	  }
    }

    /**
     * Draw on the map canvas according to which map is selected.
     */
    public void drawMap() {
	  double rightSideLimit = 740;
	  double lowestPoint = 569;
	  mapCanvas.setHeight(lowestPoint);
	  mapCanvas.setWidth(rightSideLimit);

	  GraphicsContext gc = mapCanvas.getGraphicsContext2D();
	  gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight()); // CLEARING THE CANVAS

	  gc.setFill(Color.BLUE);
	  gc.setStroke(Color.ORANGE);
	  for (Edge edge : StateSpace.getEdges()) {
		double pointXA = StateSpace.getNode(edge.getFromId()).getPointX();
		double pointYA = StateSpace.getNode(edge.getFromId()).getPointY();
		double pointXB = StateSpace.getNode(edge.getToId()).getPointX();
		double pointYB = StateSpace.getNode(edge.getToId()).getPointY();

		gc.strokeLine(pointXA, pointYA, pointXB, pointYB);

		if (pointYA > lowestPoint) {
		    lowestPoint = pointYA;
		    mapCanvas.setHeight(lowestPoint + 20);
		}
		if (pointXA > rightSideLimit) {
		    rightSideLimit = pointXA;
		    mapCanvas.setWidth(rightSideLimit + 20);
		}
		gc.fillOval(pointXA - 2, pointYA - 2, 4, 4);

		if (pointYB > lowestPoint) {
		    lowestPoint = pointYA;
		    mapCanvas.setHeight(lowestPoint + 20);
		}
		if (pointXB > rightSideLimit) {
		    rightSideLimit = pointXB;
		    mapCanvas.setWidth(rightSideLimit + 20);
		}
		gc.fillOval(pointXB - 2, pointYB - 2, 4, 4);
	  }
    }

    @Override
    public void setFirstGenerationPanelVisibility(boolean visibility) {
	  firstPopulationPanel.setVisible(visibility);
    }
}
