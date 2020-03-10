package com.github.davidmoten.skyline;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Point;

import java.util.List;

public class SkyLineUpdate {

    public static void insert(List<Entry<Object, Point>> skylinePoints, Entry<Object, Point> entry) {
        // Check whether the new entry is dominated by any current skyline point;
        boolean flag = false;
        for(Entry<Object, Point> skylinePoint: skylinePoints) {
            if(SkyLine.judgeIfDominate(skylinePoint.geometry().x(), skylinePoint.geometry().y(), entry.geometry().x(), entry.geometry().y())) {
                flag = true;
            }
        }
        if(!flag) {
            // Delete the skyline points that are dominated by the new entry.
            skylinePoints.removeIf(skylinePoint -> SkyLine.judgeIfDominate(entry.geometry().x(), entry.geometry().y(), skylinePoint.geometry().x(), skylinePoint.geometry().y()));
            // Add this new entry to the list of the skyline points.
            skylinePoints.add(entry);
            // Sort the list.
            skylinePoints.sort((o1, o2) -> (int) (Math.floor(o1.geometry().x() - o2.geometry().x())));
        }
    }


    public static void delete(List<Entry<Object, Point>> skylinePoints, Entry<Object, Point> entry) {

    }
}
