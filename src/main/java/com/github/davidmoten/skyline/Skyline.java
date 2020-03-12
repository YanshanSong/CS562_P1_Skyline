package com.github.davidmoten.skyline;

import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class Skyline {
    private RTree<Object, Point> rTree;
    private PriorityQueue<MyNode> pq;
    private List<Entry<Object, Point>> skyLinePoints;


    public Skyline(RTree<Object, Point> rTree) {
        this.rTree = rTree;
        pq = new PriorityQueue<>(new Comparator<MyNode>() {
            @Override
            public int compare(MyNode o1, MyNode o2) {
                return (int) (o1.getManhattanDistance() - o2.getManhattanDistance());
            }
        });
        skyLinePoints = new ArrayList<>();
    }


    public RTree<Object, Point> getRTree() {
        return rTree;
    }


    public List<Entry<Object, Point>> getSkyLinePoints() {
        return skyLinePoints;
    }


    public static boolean judgeIfDominate(double x1, double y1, double x2, double y2) {
        return x1 <= x2 && y1 <= y2 && (Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2) != 0);
    }


    /**
     * Find out all the skyline points of the Rtree.
     */
    public void findSkylinePoints() {
        // Add the root node of the RTree to the pq.
        pq.add(new MyNode(rTree.root().get()));
        while(pq.peek() != null) {
            MyNode myNode = pq.poll();
            if(myNode.isIntermediateNode()) { // If the node is intermediate, expand it and add all its children to pq.
                Node<Object, Point> node = myNode.getNode();
                if(node instanceof NonLeaf) {
                    NonLeaf<Object, Point> nonLeaf = (NonLeaf<Object, Point>) node;
                    for(int i = 0; i < nonLeaf.count(); i++) {
                        pq.add(new MyNode(nonLeaf.child(i)));
                    }
                }else{
                    Leaf<Object, Point> leaf = (Leaf<Object, Point>) node;
                    for(Entry<Object, Point> entry: leaf.entries()) {
                        pq.add(new MyNode(entry));
                    }
                }
            }else {
                // Judge whether myNode is dominated by any node in ArrayList
                boolean dominance = false;
                for (Entry<Object, Point> skyLinePoint : skyLinePoints) {
                    if (judgeIfDominate(skyLinePoint.geometry().x(), skyLinePoint.geometry().y(), myNode.getX(), myNode.getY())) {
                        dominance = true;
                        break;
                    }
                }
                if(!dominance) {
                    skyLinePoints.add(myNode.getEntry());
                    // Remove the node in the pq that is dominated by myNode
                    pq.removeIf(myNode1 -> judgeIfDominate(myNode.getX(), myNode.getY(), myNode1.getX(), myNode1.getY()));
                }
            }
        }
        // Sort by x;
        skyLinePoints.sort((o1, o2) -> (int) Math.floor (o1.geometry().x() - o2.geometry().x()));
    }


    public void traverse(Node<Object, Point> node) {
        System.out.println(node.geometry());
        if(node instanceof NonLeaf) {
            NonLeaf<Object, Point> nonLeaf = (NonLeaf<Object, Point>) node;
            for(int i = 0; i < nonLeaf.count(); i++) {
                traverse(nonLeaf.child(i));
            }
        }else{
            Leaf<Object, Point> leaf = (Leaf<Object, Point>) node;
            for(Entry<Object, Point> entry: leaf.entries()) {
                System.out.println(entry.geometry());
            }
        }

    }
}
