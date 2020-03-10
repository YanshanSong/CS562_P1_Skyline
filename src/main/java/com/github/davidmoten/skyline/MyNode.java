package com.github.davidmoten.skyline;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.geometry.Point;

/**
 * Define a data structure that will be added into PriorityQueue.
 */
public class MyNode {
    private double x;
    private double y;
    private double manhattanDistance;
    private boolean isIntermediateNode;
    private Node<Object, Point> node;
    private Entry<Object, Point> entry;


    public MyNode(Node<Object, Point> node) {
        x = node.geometry().mbr().x1();
        y = node.geometry().mbr().y1();
        manhattanDistance = x + y;
        isIntermediateNode = true;
        this.node = node;
    }

    public MyNode(Entry<Object, Point> entry) {
        x = entry.geometry().x();
        y = entry.geometry().y();
        manhattanDistance = x + y;
        isIntermediateNode = false;
        this.entry = entry;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getManhattanDistance() {
        return manhattanDistance;
    }

    public void setManhattanDistance(int manhattanDistance) {
        this.manhattanDistance = manhattanDistance;
    }

    public boolean isIntermediateNode() {
        return isIntermediateNode;
    }

    public void setIntermediateNode(boolean intermediateNode) {
        isIntermediateNode = intermediateNode;
    }

    public Node<Object, Point> getNode() {
        return node;
    }

    public void setNode(Node<Object, Point> node) {
        this.node = node;
    }

    public Entry<Object, Point> getEntry() {
        return entry;
    }

    public void setEntry(Entry<Object, Point> entry) {
        this.entry = entry;
    }
}
