package com.github.davidmoten.rtree;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

public final class Visualizer {

    private final RTree<?, Geometry> tree;
    private final int width;
    private final int height;
    private final Rectangle view;
    private final int maxDepth;

    Visualizer(RTree<?, Geometry> tree, int width, int height, Rectangle view) {
        this.tree = tree;
        this.width = width;
        this.height = height;
        this.view = view;
        this.maxDepth = calculateMaxDepth(tree.root());
    }

    private static <R, S extends Geometry> int calculateMaxDepth(
            Optional<? extends Node<R, S>> root) {
        if (!root.isPresent())
            return 0;
        else
            return calculateDepth(root.get(), 0);
    }

    private static <R, S extends Geometry> int calculateDepth(Node<R, S> node, int depth) {
        if (node instanceof Leaf)
            return depth + 1;
        else
            return calculateDepth(((NonLeaf<R, S>) node).child(0), depth + 1);
    }

    public BufferedImage createImage() {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = (Graphics2D) image.getGraphics();
        g.setBackground(Color.white);
        g.clearRect(0, 0, width, height);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));

//        if (tree.root().isPresent()) {
//            final List<RectangleDepth> nodeDepths = getNodeDepthsSortedByDepth(tree.root().get());
//            drawNode(g, nodeDepths);
//        }
        drawSkyLinePoints(g, tree.getSkyLinePoints());
        drawSkyLine(g, tree.getSkyLinePoints());
        return image;
    }

    private <T, S extends Geometry> List<RectangleDepth> getNodeDepthsSortedByDepth(
            Node<T, S> root) {
        final List<RectangleDepth> list = getRectangleDepths(root, 0);
        Collections.sort(list, new Comparator<RectangleDepth>() {
            @Override
            public int compare(RectangleDepth n1, RectangleDepth n2) {
                return ((Integer) n1.getDepth()).compareTo(n2.getDepth());
            }
        });
        return list;
    }

    private <T, S extends Geometry> List<RectangleDepth> getRectangleDepths(Node<T, S> node,
            int depth) {
        final List<RectangleDepth> list = new ArrayList<RectangleDepth>();
        list.add(new RectangleDepth(node.geometry().mbr(), depth));
        if (node instanceof Leaf) {
            final Leaf<T, S> leaf = (Leaf<T, S>) node;
            for (final Entry<T, S> entry : leaf.entries()) {
                list.add(new RectangleDepth(entry.geometry().mbr(), depth + 2));
            }
        } else {
            final NonLeaf<T, S> n = (NonLeaf<T, S>) node;
            for (int i = 0; i < n.count(); i++) {
                list.addAll(getRectangleDepths(n.child(i), depth + 1));
            }
        }
        return list;
    }

    private void drawNode(Graphics2D g, List<RectangleDepth> nodes) {
        for (final RectangleDepth node : nodes) {
            final Color color = Color.getHSBColor(node.getDepth() / (maxDepth + 1f), 1f, 1f);
            g.setStroke(new BasicStroke(Math.max(0.5f, maxDepth - node.getDepth() + 1 - 1)));
            g.setColor(color);
            final Rectangle r = node.getRectangle();
            drawRectangle(g, r);
        }
    }

    private void drawRectangle(Graphics2D g, Rectangle r) {
        final double x1 = (r.x1() - view.x1()) / (view.x2() - view.x1()) * width;
        final double y1 = (r.y1() - view.y1()) / (view.y2() - view.y1()) * height;
        final double x2 = (r.x2() - view.x1()) / (view.x2() - view.x1()) * width;
        final double y2 = (r.y2() - view.y1()) / (view.y2() - view.y1()) * height;
        g.drawRect(rnd(x1), rnd(y1), Math.max(rnd(x2 - x1), 1), Math.max(rnd(y2 - y1), 1));
    }

    private void drawSkyLinePoints(Graphics2D g, List<? extends Entry<?, Geometry>> skyLinePoints) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3f));
        for(Entry<?, Geometry> skylinePoint: skyLinePoints) {
            final double x = (skylinePoint.geometry().mbr().x1() - view.x1()) / (view.x2() - view.x1()) * width;
            final double y = (skylinePoint.geometry().mbr().y1() - view.y1()) / (view.y2() - view.y1()) * height;
            g.drawRect(rnd(x), rnd(y), 3, 3);
        }
    }

    private void drawSkyLine(Graphics2D g, List<? extends Entry<?, Geometry>> skyLinePoints) {
        if(skyLinePoints.size() > 0) {
            int size = skyLinePoints.size();
            g.setStroke(new BasicStroke(2f));
            // Draw the first line(vertical line).
            Entry<?, Geometry> skylinePoint_1 = skyLinePoints.get(0);
            double x1 = (skylinePoint_1.geometry().mbr().x1() - view.x1()) / (view.x2() - view.x1()) * width;
            double y1 = height;
            double x2 = x1;
            double y2 = (skylinePoint_1.geometry().mbr().y1() - view.y1()) / (view.y2() - view.y1()) * height;
            g.drawLine(rnd(x1), rnd(y1), rnd(x2), rnd(y2));

            for(int i = 0; i < skyLinePoints.size()-1; i++) {
                Entry<?, Geometry> currentPoint = skyLinePoints.get(i);
                Entry<?, Geometry> nextPoint = skyLinePoints.get(i+1);
                // Draw the horizontal line between the current node and the next node.
                x1 = (currentPoint.geometry().mbr().x1() - view.x1()) / (view.x2() - view.x1()) * width;
                y1 = (currentPoint.geometry().mbr().y1() - view.y1()) / (view.y2() - view.y1()) * height;
                x2 = (nextPoint.geometry().mbr().x1() - view.x1()) / (view.x2() - view.x1()) * width;
                y2 = y1;
                g.drawLine(rnd(x1), rnd(y1), rnd(x2), rnd(y2));
                // Draw the vertical line between the current node and the next node.
                x1 = x2;
                y2 = (nextPoint.geometry().mbr().y1() - view.y1()) / (view.y2() - view.y1()) * height;
                g.drawLine(rnd(x1), rnd(y1), rnd(x2), rnd(y2));
            }

            // Draw the last line (horizontal line).
            Entry<?, Geometry> skylinePoint_n = skyLinePoints.get(size-1);
            x1 = (skylinePoint_n.geometry().mbr().x1() - view.x1()) / (view.x2() - view.x1()) * width;
            y1 = (skylinePoint_n.geometry().mbr().y1() - view.y1()) / (view.y2() - view.y1()) * height;
            x2 = width;
            y2 = y1;
            g.drawLine(rnd(x1), rnd(y1), rnd(x2), rnd(y2));
        }
    }

    private static int rnd(double d) {
        return (int) Math.round(d);
    }

    public void save(File file, String imageFormat) {
        ImageSaver.save(createImage(), file, imageFormat);
    }

    public void save(String filename, String imageFormat) {
        save(new File(filename), imageFormat);
    }

    public void save(String filename) {
        save(new File(filename), "PNG");
    }
}
