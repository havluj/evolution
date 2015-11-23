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
package pjv.evolution.util;

import java.util.ArrayList;
import java.util.List;

/**
 * State space class providing access to all the Nodes and Edges present in the
 * map.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>} (original by Tomas Barton)
 */
public class StateSpace {

    /**
     * Static list of all nodes for the evolution representing one map.
     */
    private static List<Node> nodes;

    /**
     * Static list of all edges for the evolution representing one map.
     */
    private static List<Edge> edges = new ArrayList<>();

    /**
     * Fill the nodes and edges lists.
     *
     * @param n list of nodes
     * @param e list of edges
     */
    public static void setStateSpace(List<Node> n, List<Edge> e) {
	  nodes = n;
	  edges = e;

	  // add edges to each node
	  edges.stream().forEach((edge) -> {
		nodes.get(edge.fromId).addEdge(edge);
	  });
    }

    /**
     * Clear the lists of nodes and edges.
     */
    public static void clear() {
	  if (nodes != null && !nodes.isEmpty()) {
		nodes.clear();
	  }
	  if (!edges.isEmpty()) {
		edges.clear();
	  }
    }

    /**
     * Gets the total number of edges on the map.
     *
     * @return The number of nodes
     */
    public static int nodesCount() {
	  if (nodes != null) {
		return nodes.size();
	  }
	  return 0;
    }

    /**
     * Gets the total number of edges (roads) on the map.
     *
     * @return The number of edges
     */
    public static int edgesCount() {
	  return edges.size();
    }

    /**
     * Gets a reference to the list of all nodes.
     *
     * @return The list of all nodes
     */
    public static List<Node> getNodes() {
	  return nodes;
    }

    /**
     * Gets a reference to a list of all edges.
     *
     * @return The list of all edges
     */
    public static List<Edge> getEdges() {
	  return edges;
    }

    /**
     * Gets a node at given index, counting from 0 to |Nodes|-1
     *
     * @param idx Index of the node to be retrieved
     * @return The Node object at the index given
     */
    public static Node getNode(int idx) {
	  return nodes.get(idx);
    }

    /**
     * Gets an edge at given index, counting from 0 to |Edges|-1
     *
     * @param idx Index of the edge to be retrieved
     * @return The Edge object at the index given
     */
    public static Edge getEdge(int idx) {
	  return edges.get(idx);
    }
}
