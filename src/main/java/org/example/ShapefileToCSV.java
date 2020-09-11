package org.example;

import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.*;
import java.nio.charset.Charset;

/*
Shapefile to CSV

Argument(s): 1
the directory path containing shapefile
(.dbf, .shp, .prj have to be in the same directory)

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
            ShapefileReader shpReader = new ShapefileReader(shpFile, true, false, geometryFactory);
            DbaseFileReader dbfReader = new DbaseFileReader(shpFile, false, Charset.forName("MS949"));
            BufferedReader prjReader = new BufferedReader(new FileReader(fileName + ".prj"));
            FileWriter csvWriter = new FileWriter(new File(fileName + ".csv"));

            // correction for WGS84
            String sourceWKT = prjReader.readLine();
            int splitIndex = sourceWKT.lastIndexOf("],PRIMEM");

            // 좌표계가 구식(1985)이라 과거 보정값을 이용해야 합니다.
            // 아래는 테스트했던 보정값 후보들입니다.
            //+ ",TOWGS84[-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43]"
            //+ ",TOWGS84[-114.82,475.963,675.018,1.162,-2.347,-1.592,6.342]"
            //+ ",TOWGS84[-145.907,505.034,685.756,1.162,-2.347,-1.592,6.342]"
            //+ ",TOWGS84[0,0,0,0,0,0,0]"
            String modWKT = sourceWKT.substring(0, splitIndex)
                    + ",TOWGS84[-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43]"
                    + sourceWKT.substring(splitIndex);

            CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
            CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(modWKT);
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326"); // EPSG:3857 for Google Map
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

            // create CSV header
            DbaseFileHeader header = dbfReader.getHeader();
            int numFields = header.getNumFields();
            csvWriter.append("lat, lon, ");
            for (int i = 0; i < numFields - 1; ++i) {
                csvWriter.append(header.getFieldName(i)).append(", ");
            }
            csvWriter.append(header.getFieldName(numFields - 1)).append("\n");

            // merge .shp and .dbf line-by-line
            while (shpReader.hasNext() && dbfReader.hasNext()) {
                ShapefileReader.Record record = shpReader.nextRecord();
                Geometry transCoordGeometry = JTS.transform((Geometry) record.shape(), transform);
                Coordinate[] coords = transCoordGeometry.getCoordinates();
                Point centroid = transCoordGeometry.getCentroid();
                csvWriter.append(String.valueOf(centroid.getX())).append(", ")
                        .append(String.valueOf(centroid.getY())).append(", ");

                Object[] values = dbfReader.readEntry();
                for (int i = 0; i < numFields - 1; ++i) {
                    csvWriter.append(String.valueOf(values[i])).append(", ");
                }
                csvWriter.append(String.valueOf(values[numFields - 1])).append("\n");
            }
            shpReader.close();
            dbfReader.close();
            prjReader.close();
            csvWriter.close();

        } catch (TransformException | IOException | FactoryException e) {
            e.printStackTrace();
        }
    }
}