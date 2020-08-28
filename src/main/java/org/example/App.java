package org.example;

import java.io.IOException;
import java.net.MalformedURLException;

import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/*
Shapefile Reader
Reference: http://www.gisdeveloper.co.kr/?p=1386
 */

public class App {
    public static void main(String[] args) {
        try {
            ShpFiles shpFile = new ShpFiles("src/main/resources/AL_36_D002_20200801.shp");

            GeometryFactory geometryFactory = new GeometryFactory();
            ShapefileReader r = new ShapefileReader(shpFile, true, false, geometryFactory);

            while (r.hasNext()) {
                Record record = r.nextRecord();
                Geometry shape = (Geometry)record.shape();
                Point centroid = shape.getCentroid();
                System.out.println("(" + centroid.getX() + ", " + centroid.getY() + ")");
            }
            r.close();
        }
        catch (MalformedURLException | ShapefileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}