package com.github.davidmoten.skyline;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import rx.Observable;

import java.util.List;

public class SkylineUpdate {

    public static void insert(List<Entry<Object, Point>> skylinePoints, Entry<Object, Point> entry) {
        // Check whether the new entry is dominated by any current skyline point;
        boolean flag = false;
        for(Entry<Object, Point> skylinePoint: skylinePoints) {
            if(Skyline.judgeIfDominate(skylinePoint.geometry().x(), skylinePoint.geometry().y(), entry.geometry().x(), entry.geometry().y())) {
                flag = true;
            }
        }
        if(!flag) {
            // Delete the skyline points that are dominated by the new entry.
            skylinePoints.removeIf(skylinePoint -> Skyline.judgeIfDominate(entry.geometry().x(), entry.geometry().y(), skylinePoint.geometry().x(), skylinePoint.geometry().y()));
            // Add this new entry to the list of the skyline points.
            skylinePoints.add(entry);
            // Sort the list.
            skylinePoints.sort((o1, o2) -> (int) (Math.floor(o1.geometry().x() - o2.geometry().x())));
        }
    }


    public static void delete(RTree<Object, Point> rTree, List<Entry<Object, Point>> skylinePoints, Entry<Object, Point> entry) {
        // Check whether the entry to be deleted is in the skylinePoints
        int index = -1;
        for(int i = 0; i < skylinePoints.size(); i++) {
            if(skylinePoints.get(i).geometry().x() == entry.geometry().x() && skylinePoints.get(i).geometry().y() == entry.geometry().y()) {
                index = i;
                break;
            }
        }
        if(index >= 0) {
            double boundX2 = rTree.mbr().get().x2();
            double boundY2 = rTree.mbr().get().y2();

            double regionX1 = skylinePoints.get(index).geometry().x();
            double regionY1 = skylinePoints.get(index).geometry().y();
            double regionX2 = (index+1) < skylinePoints.size() ? skylinePoints.get(index+1).geometry().x() : boundX2;
            double regionY2 = (index-1) >= 0 ? skylinePoints.get(index-1).geometry().y() : boundY2;

            // Create a region RTree.
            Observable<Entry<Object, Point>> results = rTree.search(Geometries.rectangle(regionX1, regionY1, regionX2, regionY2));
            List<Entry<Object, Point>> regionEntries = results.toList().toBlocking().single();
            regionEntries.removeIf(e -> (e.geometry().x() == regionX1 && e.geometry().y() == regionY1));
            RTree<Object, Point> regionRTree = RTree.create();
            regionRTree = regionRTree.add(regionEntries);

            // Find the region skyline points.
            Skyline skyline = new Skyline(regionRTree);
            skyline.findSkylinePoints();

            // Combine the region skyline points and the original skyline points.
            skylinePoints.remove(index);
            List<Entry<Object, Point>> regionSkylinePoints = skyline.getSkyLinePoints();
            for(Entry<Object, Point> skylinePoint: skylinePoints) {
                regionSkylinePoints.removeIf(e -> Skyline.judgeIfDominate(skylinePoint.geometry().x(), skylinePoint.geometry().y(), e.geometry().x(), e.geometry().y()));
            }
            skylinePoints.addAll(regionSkylinePoints);
            skylinePoints.sort((o1, o2) -> (int) Math.floor(o1.geometry().x() - o2.geometry().x()));
        }
    }
}
