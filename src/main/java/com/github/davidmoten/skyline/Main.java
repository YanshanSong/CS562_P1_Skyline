package com.github.davidmoten.skyline;


import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.internal.EntryDefault;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // Create a RTree
        RTree<Object, Point> rTree = RTree.create();
        // Add data entries to the RTree.
        List<Entry<Object, Point>> dataEntries = Preprocess.getDataEntries();
        rTree = rTree.add(dataEntries);
        // Find the skyline points.
        Skyline skyLine = new Skyline(rTree);
        skyLine.findSkylinePoints();
        List<Entry<Object, Point>> skylinePoints = skyLine.getSkyLinePoints();
//        for(Entry<Object, Point> entry: skylinePoints) {
//            System.out.println(entry);
//        }
//        Entry [value=java.lang.Object@433c675d, geometry=Point [x=33.82, y=25.82]]
//        Entry [value=java.lang.Object@1a6c5a9e, geometry=Point [x=33.92, y=20.08]]
//        Entry [value=java.lang.Object@37bba400, geometry=Point [x=34.36, y=19.77]]
//        Entry [value=java.lang.Object@179d3b25, geometry=Point [x=34.78, y=19.24]]
//        Entry [value=java.lang.Object@254989ff, geometry=Point [x=35.32, y=19.2]]
//        Entry [value=java.lang.Object@5d099f62, geometry=Point [x=36.13, y=19.05]]
//        Entry [value=java.lang.Object@37f8bb67, geometry=Point [x=36.6, y=19.0]]
//        Entry [value=java.lang.Object@49c2faae, geometry=Point [x=41.47, y=18.52]]
        rTree.setSkyLinePoints(skylinePoints);
        rTree.visualize(600, 600).save("src/docs/skyline.png");

        // Insert Test
//        Entry<Object, Point> entry = new EntryDefault<>(new Object(), Geometries.point(34, 19.0));
//        rTree = rTree.add(entry);
//        SkylineUpdate.insert(skylinePoints, entry);
//        rTree.setSkyLinePoints(skylinePoints);
//        rTree.visualize(600, 600).save("src/docs/skyline_insert_test.png");

        // Delete Test
        Entry<Object, Point> entry = new EntryDefault<>(new Object(), Geometries.point(36.6, 19.0));
        rTree = rTree.delete(entry, true);
        SkylineUpdate.delete(rTree, skylinePoints, entry);
        rTree.setSkyLinePoints(skylinePoints);
        rTree.visualize(600, 600).save("src/docs/skyline_delete_test.png");
    }
}
