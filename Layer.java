package com.neocoretechs.pathstar;
import java.util.Vector;
import java.io.*;
/**
* Layer class...each sShape inserted into vector
* Vector sTable is vector of columns of String[] in which each
* String array element
* corresponds to rows for each shape and names[] to each column
* for those shapes
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public final class Layer implements Serializable
{
    String name;
    private Vector v;
    double x_max;
    double y_max;
    double x_min;
    double y_min;
    double x;
    double y;
    private int numberColumns;
    private int numberRows;
    public String names[];
    public String types[];
    // optional
    public int[] fieldSize;
    public int[] fieldScale;
    public boolean[] fieldNullable;
    Vector sTable;
    private double BoundingBox[];
    public boolean has_data;

    public Layer() {
        BoundingBox = new double[4];
        v = new Vector();
    }

    /**
    * Create named Layer
    * @param string The Layer name
    */
    public Layer(String string)
    {
        BoundingBox = new double[4];
        name = string;
        v = new Vector();
    }

    public String getName() { return name; }

    /**
    * Create an internal rep. of dbf table
    * @param i Columns
    * @param j Rows
    */
    public synchronized void createTable(int i, int j)
    {
        numberColumns = i;
        numberRows = j;
        names = new String[i];
        types = new String[i];
        fieldSize = new int[i];
        fieldScale = new int[i];
        fieldNullable = new boolean[i];
        sTable = new Vector(i);
        for (int k = 0; k < i; k++)
        {
            String stringArray[] = new String[j];
            sTable.addElement(stringArray);
        }
    }
    /**
    * look for string 1 in names, then set element i to string2
    * @param string1 The name of the field to set (column)
    * @param string2 The new value
    * @param i The element number in field with string1 name (row)
    */
    public synchronized void addElement(String string1, String string2, int i)
    {
        int idex = -1;
        for (int j = 0; j < numberColumns; j++)
            if (names[j].compareTo(string1) == 0) idex = j;
        String stringArray[] = (String[])sTable.elementAt(idex);
        stringArray[i] = string2;
    }

    public synchronized void addElement(String string2, int column, int row)
    {
        ((String[])sTable.elementAt(column))[row] = string2;
    }

    public synchronized String getElement(String string, int i)
    {
        int idex = -1;
        for (int j = 0; j < numberColumns; j++)
            if (names[j].compareTo(string) == 0) idex = j;
        String stringArray[] = (String[])sTable.elementAt(idex);
        return stringArray[i];
    }

    public synchronized String getElement(int i, int j)
    {
        String stringArray[] = (String[])sTable.elementAt(i);
        return stringArray[j];
    }

    public synchronized int getNumberColumns() { return numberColumns; }
    public synchronized void setNumberColumns(int nc) { numberColumns = nc; }

    public synchronized int getNumberRows() { return numberRows; }
    public synchronized void setNumberRows(int nc) { numberRows = nc; }

    public synchronized void addShape(sShape tShape)
    {
        v.addElement(tShape);
    }

    public synchronized sShape getShape(int i)
    {
        return (sShape)v.elementAt(i);
    }


    public synchronized int getNumberFeatures()
    {
        return v.size();
    }

    public synchronized double[] getBoundingBox()
    {
        return BoundingBox;
    }

    public synchronized void setBoundingBox(int i, double p) {
        BoundingBox[i] = p;
    }

    public synchronized void clear() { v = new Vector(); }
}
