package org.example;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

/*
Dbf Reader
.dbf and .shp file have to be in the same directory
Reference: http://www.gisdeveloper.co.kr/?p=1386
 */

public class DbfReader {
    public static void main(String[] args) {
        try {
            /*
            AL_36_D168_20200825.shp
            AL_00_D158_20200207.shp
            AL_36_D002_20200801.shp
            */
            String fileName = "AL_36_D168_20200825.shp";
            ShpFiles shpFile = new ShpFiles("src/main/resources/" + fileName);

            DbaseFileReader dr = new DbaseFileReader(shpFile, false, Charset.forName("EUC-KR"));
            DbaseFileHeader header = dr.getHeader();
            int numFields = header.getNumFields();

            for (int iField = 0; iField < numFields - 1; ++iField) {
                String fieldName = header.getFieldName(iField);
                System.out.print(fieldName + ", ");
            }
            System.out.println(header.getFieldName(numFields - 1));

            while (dr.hasNext()) {
                Object[] values = dr.readEntry();
                for (int iField = 0; iField < numFields - 1; ++iField) {
                    System.out.print(values[iField] + ", ");
                }
                System.out.println(values[numFields - 1]);
            }

            dr.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}