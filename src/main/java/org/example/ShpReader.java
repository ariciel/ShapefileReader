package org.example;

import java.io.IOException;

import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/*
Shapefile Reader

Reference: http://www.gisdeveloper.co.kr/?p=1386
 */

public class ShpReader {
    public static void main(String[] args) {
        try {
            String fileName = args[0] + args[0].substring(args[0].lastIndexOf("/"));
            ShpFiles shpFile = new ShpFiles(fileName + ".shp");
            GeometryFactory geometryFactory = new GeometryFactory();
            ShapefileReader shpReader = new ShapefileReader(shpFile, true, false, geometryFactory);

            while (shpReader.hasNext()) {
                Record record = shpReader.nextRecord();
                Geometry shape = (Geometry)record.shape();
                Point centroid = shape.getCentroid();
                System.out.println(centroid.getX() + ", " + centroid.getY());
            }
            shpReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}