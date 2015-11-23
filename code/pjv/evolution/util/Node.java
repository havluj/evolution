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
 * Class representing one Node. Contains coordinates of the node and the node's
 * id.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>}
 */
public class Node {

    /**
     * List of edges coming out of this node.
     */
    protected List<Edge> edge = new ArrayList<>();

    /**
     * Id of the node.
     */
    protected int id;

    /**
     * Coordinate X.
     */
    private final double pointX;

    /**
     * Coordinate Y.
     */
    private final double pointY;

    /**
     * Creates the node.
     *
     * @param id Id of the node.
     * @param pointX Coordinate X.
     * @param pointY Coordinate Y.
     */
    public Node(int id, double pointX, double pointY) {
	  this.id = id;
	  this.pointX = pointX;
	  this.pointY = pointY;
    }

    /**
     * Add a new edge coming from this node to the list.
     *
     * @param e new edge that will be added to the list
     */
    public void addEdge(Edge e) {
	  edge.add(e);
    }

    /**
     * Gets a list of edges coming out of this node.
     *
     * @return list of all the edges coming out of this node
     */
    public List<Edge> getEdges() {
	  return this.edge;
    }

    /**
     * Gets the index of node, counting from 0 to |Nodes|-1.
     *
     * @return Index of the node
     */
    public int getId() {
	  return this.id;
    }

    /**
     * Gets the X coordinate of this node.
     *
     * @return the X coordinate
     */
    public double getPointX() {
	  return pointX;
    }

    /**
     * Gets the Y coordinate of this node.
     *
     * @return the Y coordinate
     */
    public double getPointY() {
	  return pointY;
    }

    @Override
    public boolean equals(Object obj) {
	  if (!(obj instanceof Node)) {
		return false;
	  }
	  Node nodeImpl = (Node) obj;
	  return getId() == nodeImpl.getId();
    }

    @Override
    public int hashCode() {
	  return getId();
    }

}
