package com.neocoretechs.pathstar;
import java.awt.Color;
import java.io.*;
/**
* The Point type. simple X,Y with attributes
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public final class sPoint implements sShape, Serializable, Cloneable
{
    private double x;
    private double y;
    private int symbolShape = 1;
    private Color symbolColor = Color.red;
    private int symbolSize = 10;
    private static final int shapeType = 1;

    public sPoint() {}

    public sPoint(double[] d) {
        x = d[0];
        y = d[1];
    }

    public sPoint(double d1, double d2)
    {
        x = d1;
        y = d2;
    }

    public Object copy() throws CloneNotSupportedException { return clone(); }

    public int getShapeType() { return shapeType; }

    public synchronized void setCoords(double d1, double d2)
    {
        x = d1;
        y = d2;
    }

    public synchronized void computeBoundingBox()
    {
    }

    public double[] getBoundingBox() { return new double[]{x,y,x,y}; }

    public synchronized double getX() { return x; }
    public synchronized double getY() { return y; }
    public synchronized int getSymbolShape() { return symbolShape; }
    public synchronized int getSymbolSize() { return symbolSize; }
    public synchronized Color getSymbolColor() { return symbolColor; }
    public synchronized void setSymbolShape(int ss) { symbolShape = ss; }
    public synchronized void setSymbolSize(int ss) { symbolSize = ss; }
    public synchronized void setSymbolColor(Color sc) { symbolColor = sc; }
}
