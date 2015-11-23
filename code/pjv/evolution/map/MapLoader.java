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
package pjv.evolution.map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pjv.evolution.util.Edge;
import pjv.evolution.util.Node;
import pjv.evolution.util.StateSpace;

/**
 * Feeding structured data in map files into state space.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>}
 */
public class MapLoader {

    /**
     * Loads structured data from nodes and edges files in 'dir', parses them
     * and loads them into <code>StateSpace</code>
     *
     * @param dir map's directory name in the "maps" directory
     */
    public MapLoader(String dir) {
	  StateSpace.clear();
	  List<Node> n = new ArrayList<>();
	  List<Edge> e = new ArrayList<>();

	  try (BufferedReader br = new BufferedReader(new FileReader("maps/" + dir + "/nodes"))) {
		String sCurrentLine;

		while ((sCurrentLine = br.readLine()) != null) {
		    String[] entry;
		    entry = sCurrentLine.split(" ", 5);
		    n.add(new Node(Integer.parseInt(entry[0]), Double.parseDouble(entry[1]), Double.parseDouble(entry[2])));
		}
	  } catch (IOException ex) {
	  }

	  try (BufferedReader br = new BufferedReader(new FileReader("maps/" + dir + "/edges"))) {

		String sCurrentLine;

		while ((sCurrentLine = br.readLine()) != null) {
		    String[] entry;
		    entry = sCurrentLine.split(" ", 2);
		    e.add(new Edge(Integer.parseInt(entry[0]), Integer.parseInt(entry[1])));
		}

	  } catch (IOException ex) {
	  }

	  StateSpace.setStateSpace(n, e);
    }
}
