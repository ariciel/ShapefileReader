package org.example;

import java.io.IOException;
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
            String fileName = args[0] + args[0].substring(args[0].lastIndexOf("/"));
            ShpFiles shpFile = new ShpFiles(fileName + ".shp");
            DbaseFileReader dbfReader = new DbaseFileReader(shpFile, false, Charset.forName("MS949"));

            DbaseFileHeader header = dbfReader.getHeader();
            int numFields = header.getNumFields();

            for (int i = 0; i < numFields - 1; ++i) {
                System.out.print(header.getFieldName(i) + ", ");
            }
            System.out.println(header.getFieldName(numFields - 1));

            while (dbfReader.hasNext()) {
                Object[] values = dbfReader.readEntry();
                for (int i = 0; i < numFields - 1; ++i) {
                    System.out.print(values[i] + ", ");
                }
                System.out.println(values[numFields - 1]);
            }
            dbfReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}