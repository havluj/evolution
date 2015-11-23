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

/**
 * Class representing one edge. Contains two ids of two nodes.
 *
 * @author Jan Havlůj {@literal <jan@havluj.eu>} (original by Tomas Barton)
 */
public class Edge {

    /**
     * Id of a node that the edge leads from.
     */
    protected int fromId;

    /**
     * Id of a node that the edge leads to.
     */
    protected int toId;

    /**
     * Constructor that sets <code>fromId</code> and <code>toId</code>.
     *
     * @param fromId Id of a node that the edge leads from.
     * @param toId Id of a node that the edge leads to.
     */
    public Edge(int fromId, int toId) {
	  this.fromId = fromId;
	  this.toId = toId;
    }

    /**
     * Gets the index of the source node, counting from 0 to |Nodes|-1.
     *
     * @return Index of the source node
     */
    public int getFromId() {
	  return this.fromId;
    }

    /**
     * Sets the index of the source node, counting from 0
     *
     * @param value The new source node's index
     */
    public void setFromId(int value) {
	  this.fromId = value;
    }

    /**
     * Gets the index of the destination node, counting from 0 to |Nodes|-1.
     *
     * @return Index of the destination node
     */
    public int getToId() {
	  return this.toId;
    }

    /**
     * Sets the index of the destination node, counting from 0
     *
     * @param value The new destination node's index
     */
    public void setToId(int value) {
	  this.toId = value;
    }

    /**
     * Test whether this is the same edge as the other given as the argument.
     *
     * @param obj Other object to be tested for equality to the current
     * @return <code>true</code> is the argument equals to edge,
     * <code>false</code> if not
     */
    @Override
    public boolean equals(Object obj) {
	  if (obj instanceof Edge) {
		Edge other = (Edge) obj;
		if ((this.getFromId() == other.getFromId()) && (this.getToId() == other.getToId())) {
		    return true;
		}
		if ((this.getFromId() == other.getToId()) && (this.getToId() == other.getFromId())) {
		    return true;
		}
	  }
	  return false;
    }

    /**
     * Computes a hash value of the edge.
     *
     * @return The hash value
     */
    @Override
    public int hashCode() {
	  int hash = Math.max(fromId, toId);
	  hash = 83 * hash + Math.min(fromId, toId);
	  return hash;
    }
}
