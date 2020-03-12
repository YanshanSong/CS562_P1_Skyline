package com.github.davidmoten.skyline;


import com.github.davidmoten.rtree.Entries;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is used to read data entries from "resource/dataset1.txt"
 */
public class Preprocess {

    private static String filePath = "src/main/resource/dataset1.txt";

    public static List<Entry<Object, Point>> getDataEntries() throws IOException {
        List<Entry<Object, Point>> dataEntries = new ArrayList<Entry<Object, Point>>();
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null) {
            String[] item = line.split(" ");
            if(item.length == 2) {
                double x = Double.parseDouble(item[0]);
                double y = Double.parseDouble(item[1]);
                Entry<Object, Point> entry = Entries.entry(new Object(), Geometries.point(x, y));
                dataEntries.add(entry);
            }
        }
        br.close();
        fr.close();
        return dataEntries;
    }

    public static void main(String[] args) throws IOException {
        List<Entry<Object, Point>> dataEntries = getDataEntries();
        for(Entry<Object, Point> dataEntry: dataEntries) {
            System.out.println(dataEntry.toString());
        }
    }
}
