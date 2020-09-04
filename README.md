# ShapefileReader #

A simple ESRI shapefile(`.shp` and/or `.dbf`) reader with GeoTools 23.2.  
(originally copied from [Reference](http://www.gisdeveloper.co.kr/?p=1386),
and refreshed to current version of GeoTools.)

### Functionality ###

* Read `.shp` or `.dbf` file and print to console
* Read both `.shp` and `.dbf` files and merge them into single `.csv`.

### Precautions ###

* Pass the absolute path of the directory containing your data as program argument.
* Make sure to modify settings so that your OS and IDE support UTF-8.  