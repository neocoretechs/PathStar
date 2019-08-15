package com.neocoretechs.pathstar;
import java.io.*;
import java.util.*;
import java.awt.geom.*;
/**
* The RouteLayerGenerator is the main route generation class.  It is
* designed to take a pathStar object with built topology, an input Layer
* containing the DB attributes to group for routing, the names of the
* group columns, and the XY columns.
* The output of the generateRouteLayer method is a new Layer with the
* grouped attributes and an sArc3D Shape.
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class RouteLayerGenerator {
        pathStar ps;
        Layer inputLayer;
        String[] ordColumns;
        String[] groupColumns;
        String[] groupColumnTypes;
        int[] groupColumnSize;
        int[] groupColumnScale;
        boolean[] groupColumnNullable;
        String[] xyColumns;
        int[] iordColumns;
        int[] igroupColumns;
        int[] ixyColumns = new int[2];
        /**
        * @param tps The pathStar object
        * @param tinputLayer The input Layer which contains tabular data for grouping
        * @param tordColums The String array of order columns
        * @param tgroupColumns The String array of group column names
        * @param txyColumns The xy columns for routing points
        * @throws Exception if column not found
        */
        public RouteLayerGenerator(pathStar tps, Layer tinputLayer,
                                        String[] tordColumns,
                                        String[] tgroupColumns,
                                        String[] txyColumns) throws Exception {
                ps = tps;
                inputLayer = tinputLayer;

                groupColumns = tgroupColumns;
                igroupColumns = new int[groupColumns.length];
                groupColumnTypes = new String[groupColumns.length];
                groupColumnSize = new int[groupColumns.length];
                groupColumnScale = new int[groupColumns.length];
                groupColumnNullable = new boolean[groupColumns.length];

                ordColumns = tordColumns;
                iordColumns = new int[ordColumns.length];

                xyColumns = txyColumns;
                // translate names to ordinals
                int c = inputLayer.getNumberColumns();
                // find order columns
                for(int i = 0; i < ordColumns.length; i++) {
                        boolean found = false;
                        for(int i2 = 0; i2 < c; i2++ ) {
                                if( inputLayer.names[i2].equals(ordColumns[i]) ) {
                                        iordColumns[i] = i2;
                                        found = true;
                                        break;
                                }
                        }
                        if( !found ) throw new Exception("Route ordering column named "+ordColumns[i]+" is not in the table");
                }
                // find grouping columns
                // translate String names to column array index values
                for(int i = 0; i < groupColumns.length; i++) {
                        boolean found = false;
                        for(int i2 = 0; i2 < c; i2++ ) {
                                if( inputLayer.names[i2].equals(groupColumns[i]) ) {
                                        igroupColumns[i] = i2;
                                        groupColumnTypes[i] = inputLayer.types[i2];
                                        groupColumnSize[i] = inputLayer.fieldSize[i2];
                                        groupColumnScale[i] = inputLayer.fieldScale[i2];
                                        groupColumnNullable[i] = inputLayer.fieldNullable[i2];
                                        found = true;
                                        break;
                                }
                        }
                        if( !found ) throw new Exception("Route grouping column named "+groupColumns[i]+" is not in the table");
                }
                // now look for xy cols
                for(int i = 0; i < xyColumns.length; i++) {
                        boolean found = false;
                        for(int i2 = 0; i2 < c; i2++ ) {
                                if( inputLayer.names[i2].equals(xyColumns[i]) ) {
                                        ixyColumns[i] = i2;
                                        found = true;
                                        break;
                                }
                        }
                        if( !found ) throw new Exception("Route XY column named "+xyColumns[i]+" is not in the table");
                }
        }

        /**
        * calc the path from Vector of Point2D.FLoats
        * @param pathPoints The Point2D.Float Vector
        */
        public sArc3D calcPath(Vector pathPoints) throws Exception {
//                System.out.println("Path calc points: "+pathPoints.size());
                 return new sArc3D(ps.getMeasuredPoints(ps.buildRoutes(pathPoints)));
        }

        /**
        * Generate a route layer from tabular data layer and XY fields.
        * We retrieve by order of ordering fields and group by the
        * group fields.
        * @return The new layer with tabular data groups and sArc3D types
        */
        public Layer generateRouteLayer() throws Exception {
                System.out.println("Generating route layer");
                long s1 = System.currentTimeMillis();
                TabularData td = new TabularData();
                Layer newLayer = new Layer("Routes");
                // build the tabular data TreeMap
                for(int i = 0 ;i <  inputLayer.getNumberRows() ; i++ ) {
                        td.put(i, iordColumns, inputLayer);
//                        System.out.print(i+"\r");
                }
                long s2 = System.currentTimeMillis()-s1;
                System.out.println("Tree build time:"+s2);
                td.setFirst();
                TabularDataNode rowNode;
                TabularDataNode oldNode = null;
                // Vector of points by group
                Vector pointVec = new Vector();
                // Vector of first row in group to extract group elems from
                Vector rowVec = new Vector();
                int ct = 0;
                while( (rowNode = td.getNextNode()) != null ) {
//                       System.out.println( rowNode );
//                       ++ct;
//                       if( ct/10 == ((float)(ct))/10.0) System.in.read();
                         if( oldNode == null ) {
                                // don't compare, just put in
                                float x = Float.valueOf(inputLayer.getElement(ixyColumns[0], rowNode.row)).floatValue();
                                float y = Float.valueOf(inputLayer.getElement(ixyColumns[1], rowNode.row)).floatValue();
                                pointVec.addElement( new Point2D.Float(x, y) );
                                rowVec.addElement(new Integer(rowNode.row));
                         } else {
                                // if the same, move a point to the array
                                // compare grouping columns, order of
                                // retrieval is by order columns
                                int retVal = 0;
                                for(int i = 0; i < groupColumns.length; i++) {
                                        String elem1 = inputLayer.getElement(igroupColumns[i], rowNode.row);
                                        String elem2 = inputLayer.getElement(igroupColumns[i], oldNode.row);
                                        retVal = elem1.compareTo(elem2);
                                        if( retVal != 0 ) {
                                                // They are not equal, gen the points and place
                                                newLayer.addShape(calcPath(pointVec));
                                                pointVec.removeAllElements();
                                                //
                                                float x = Float.valueOf(inputLayer.getElement(ixyColumns[0], rowNode.row)).floatValue();
                                                float y = Float.valueOf(inputLayer.getElement(ixyColumns[1], rowNode.row)).floatValue();
                                                pointVec.addElement( new Point2D.Float(x, y) );
                                                rowVec.addElement(new Integer(rowNode.row));
                                                //System.out.println("For group "+rowNode.toString(igroupColumns));
                                                break;
                                        }
                                }
                                // did it come out even?
                                if( retVal == 0 ) {
                                        float x = Float.valueOf(inputLayer.getElement(ixyColumns[0], rowNode.row)).floatValue();
                                        float y = Float.valueOf(inputLayer.getElement(ixyColumns[1], rowNode.row)).floatValue();
                                        pointVec.addElement( new Point2D.Float(x, y) );
                                }
                         }
                         oldNode = rowNode;
                }
                // gen final
                newLayer.addShape(calcPath(pointVec));
                //
                // now create Layer table
                //
                newLayer.createTable(groupColumns.length, rowVec.size());
                for(int i = 0 ; i < groupColumns.length ; i++) {
                        newLayer.names[i] = groupColumns[i];
                        newLayer.types[i] = groupColumnTypes[i];
                        newLayer.fieldSize[i] = groupColumnSize[i];
                        newLayer.fieldScale[i] = groupColumnScale[i];
                        newLayer.fieldNullable[i] = groupColumnNullable[i];
                }
                for(int irow = 0; irow < rowVec.size(); irow++) {
                  int rowVal = ((Integer)(rowVec.elementAt(irow))).intValue();
                  for(int icol =  0; icol < igroupColumns.length; icol++) {
                       newLayer.addElement(inputLayer.getElement(igroupColumns[icol], rowVal),
                                                                  icol, irow);
                  }
                }
                return newLayer;
        }
}
