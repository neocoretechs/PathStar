package com.neocoretechs.pathstar;
import java.awt.geom.*;
import java.awt.Color;
import java.io.*;
import java.util.*;
/**
* This an Arc class + dimension 3.
* Contains a single part.  Array of Point3D's and a bounding box.
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public final class sArc3D implements sShape, Serializable, Cloneable
{
    private Point3D xyz[];
    private double BoundingBox[]= {0.0,0.0,0.0,0.0};
    private int penWidth = 1;
    private int penPattern = 2;
    private Color penColor = Color.black;
    private boolean smooth = false;
    private int numberPoints;
    private static final int shapeType = 7;

    /**
    * Default Constructor 
    */
    sArc3D() {}

    /**
    * Ctor taking 3D array
    * @param pts The array of Point3D
    */
    sArc3D(Point3D[] pts) {
        xyz = pts;
        numberPoints = xyz.length;
    }

    sArc3D(Vector pts) {
        xyz = new Point3D[pts.size()];
        pts.copyInto(xyz);
        numberPoints = xyz.length;
    }

    public Object copy() throws CloneNotSupportedException { return clone(); }

    public int getShapeType() { return shapeType; }

    public Point3D[] getPoints() { return xyz; }

    public synchronized int getNumberPoints()
    {
        return numberPoints;
    }

    public synchronized void computeBoundingBox()
    {
        BoundingBox[0] = 1.0E8;
        BoundingBox[1] = 1.0E8;
        BoundingBox[2] = -1.0E8;
        BoundingBox[3] = -1.0E8;
        for (int i = 0; i < numberPoints; i++)
        {
            if (xyz[i].getX() < BoundingBox[0])
                BoundingBox[0] = xyz[i].getX();
            if (xyz[i].getY() < BoundingBox[1])
                BoundingBox[1] = xyz[i].getY();
            if (xyz[i].getX() > BoundingBox[2])
                BoundingBox[2] = xyz[i].getX();
            if (xyz[i].getY() > BoundingBox[3])
                BoundingBox[3] = xyz[i].getY();
        }
//        System.err.println(" " + BoundingBox[1] + " " + BoundingBox[2] + " " + BoundingBox[3]);
    }

    public synchronized double[] getBoundingBox() { return BoundingBox; }
    public synchronized void setBoundingBox(int bbe, double bbv ) {
        BoundingBox[bbe] = bbv;
    }

    public synchronized float getArcLength() {
        return numberPoints > 0 ? xyz[numberPoints-1].getZ() : 0;
    }

    public synchronized int getPenWidth() { return penWidth; }
    public synchronized int getPenPattern() { return penPattern; }
    public synchronized Color getPenColor() { return penColor; }
    public synchronized boolean getSmooth() { return smooth; }
    public synchronized void setPenWidth(int pw) { penWidth = pw; }
    public synchronized void setPenPattern(int pp) { penPattern = pp; }
    public synchronized void setPenColor(Color pc) { penColor = pc; }
    public synchronized void setSmooth(boolean sm) { smooth = sm; }
}
