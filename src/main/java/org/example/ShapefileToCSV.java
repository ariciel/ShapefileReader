package org.example;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

/*
Shapefile to CSV
.dbf and .shp file have to be in the same directory

Reference:
http://www.gisdeveloper.co.kr/?p=1386
https://blog.hometown.co.kr/111
 */

public class ShapefileToCSV {
    public static void main(String[] args) {
        try {
            String fileName = args[0] + args[0].substring(args[0].lastIndexOf("/"));
            ShpFiles shpFile = new ShpFiles(fileName + ".shp");
            GeometryFactory geometryFactory = new GeometryFactory();
            ShapefileReader sr = new ShapefileReader(shpFile, true, false, geometryFactory);
            DbaseFileReader dr = new DbaseFileReader(shpFile, false, Charset.forName("EUC-KR"));
            DbaseFileHeader header = dr.getHeader();
            int numFields = header.getNumFields();

            BufferedReader reader = new BufferedReader(new FileReader(fileName + ".prj"));
            FileWriter writer = new FileWriter(new File(fileName + ".csv"));

            String sourceWKT = reader.readLine();
            CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
            CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(sourceWKT);
            // 3857 for Google Map, 4326 for WGS84
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

            writer.append("X, Y, ");
            for (int iField = 0; iField < numFields - 1; ++iField) {
                String fieldName = header.getFieldName(iField);
                writer.append(fieldName).append(", ");
            }
            writer.append(header.getFieldName(numFields - 1)).append("\n");

            while (sr.hasNext()) {
                // Can't happen
                if (!dr.hasNext()) {
                    // NEED TO FIX
                    System.out.println("ERROR");
                    break;
                }
                ShapefileReader.Record record = sr.nextRecord();
                Geometry transCoordGeometry = JTS.transform((Geometry)record.shape(), transform);
                Point centroid = transCoordGeometry.getCentroid();
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
            reader.close();
            writer.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAuthorityCodeException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (TransformException e) {
            e.printStackTrace();
        }
    }
}