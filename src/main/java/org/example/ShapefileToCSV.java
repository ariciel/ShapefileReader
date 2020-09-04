package org.example;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

/*
Shapefile to CSV
.dbf and .shp file have to be in the same directory
Reference: http://www.gisdeveloper.co.kr/?p=1386
 */

public class ShapefileToCSV {
    public static void main(String[] args) {
        try {
            String fileName = args[0] + args[0].substring(args[0].lastIndexOf("/"));
            FileWriter writer = new FileWriter(new File(fileName + ".csv"));
            ShpFiles shpFile = new ShpFiles(fileName + ".shp");
            GeometryFactory geometryFactory = new GeometryFactory();
            ShapefileReader sr = new ShapefileReader(shpFile, true, false, geometryFactory);
            DbaseFileReader dr = new DbaseFileReader(shpFile, false, Charset.forName("EUC-KR"));
            DbaseFileHeader header = dr.getHeader();
            int numFields = header.getNumFields();

            writer.append("X, Y, ");
            for (int iField = 0; iField < numFields - 1; ++iField) {
                String fieldName = header.getFieldName(iField);
                writer.append(fieldName).append(", ");
            }
            writer.append(header.getFieldName(numFields - 1)).append("\n");

            while (sr.hasNext()) {
                // Can't happen
                if (!dr.hasNext()) {
                   System.out.println("ERROR");
                   break;
                }
                ShapefileReader.Record record = sr.nextRecord();
                Geometry shape = (Geometry)record.shape();
                Point centroid = shape.getCentroid();
                writer.append(String.valueOf(centroid.getX())).append(", ")
                        .append(String.valueOf(centroid.getY())).append(", ");

                Object[] values = dr.readEntry();
                for (int iField = 0; iField < numFields - 1; ++iField) {
                    writer.append(String.valueOf(values[iField])).append(", ");
                }
                writer.append(String.valueOf(values[numFields - 1])).append("\n");
            }

            sr.close();
            dr.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}