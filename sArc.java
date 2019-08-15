package com.neocoretechs.pathstar;
import java.awt.geom.*;
import java.awt.Color;
import java.io.*;
/**
* This is the Arc class.  Can contain multiple parts.  Array of x, y
* double precision coords and a bounding box.
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public final class sArc implements sShape, Serializable, Cloneable
{
    private double x[];
    private double y[];
    private double BoundingBox[]= {0.0,0.0,0.0,0.0};
    private int actingPoint = 0;
    private int actingPart = 0;
    private int part[];
    private int numberParts = 0;
    private int numberPoints = 0;
    private int penWidth = 1;
    private int penPattern = 2;
    private Color penColor = Color.black;
    private boolean smooth = false;
    private static final int shapeType = 3;

    /**
    * Default Constructor 
    */
    sArc() {}

    /**
    * Ctor where number of points known
    * @param i The number of points in Arc
    */
    public sArc(int i) {
        x = new double[i];
        y = new double[i];
        numberPoints = i;
        actingPoint = 0;
    }
    public Object copy() throws CloneNotSupportedException { return clone(); }

    public int getShapeType() { return shapeType; }

    public Point2D.Float getP1() { return new Point2D.Float((float)x[0], (float)y[0]); }
    public Point2D.Float getP2() { return new Point2D.Float((float)x[actingPoint-1], (float)y[actingPoint-1]); }

    /**
    * Set number of points post-mortem
    * @param i the number of points
    */
    public synchronized void setNumberPoints(int i)
    {
        x = new double[i];
        y = new double[i];
        numberPoints = i;
        actingPoint = 0;
    }

    public synchronized int getNumberPoints()
    {
        return actingPoint;
    }

    public synchronized void setNumberParts(int i)
    {
        numberParts = i;
        part = new int[i];
        actingPart = 0;
    }

    public synchronized void addPart(int i)
    {
        part[actingPart++] = i;
    }

    public synchronized void addNode(double d1, double d2)
    {
        x[actingPoint] = d1;
        y[actingPoint++] = d2;
    }

    public synchronized void setX(double tx, int i) {
        x[i] = tx;
    }

    public synchronized void setY(double ty, int i) {
        y[i] = ty;
    }

    public synchronized double getX(int i)
    {
        return x[i];
    }

    public synchronized double getY(int i)
    {
        return y[i];
    }

    public synchronized boolean check()
    {
        if (actingPoint == numberPoints) return true;
        System.out.println("Number of points in error in Arc instance..");
        return false;
    }

    public synchronized void computeBoundingBox()
    {
        BoundingBox[0] = 1.0E8;
        BoundingBox[1] = 1.0E8;
        BoundingBox[2] = -1.0E8;
        BoundingBox[3] = -1.0E8;
        for (int i = 0; i < numberPoints; i++)
        {
            if (x[i] < BoundingBox[0])
                BoundingBox[0] = x[i];
            if (y[i] < BoundingBox[1])
                BoundingBox[1] = y[i];
            if (x[i] > BoundingBox[2])
                BoundingBox[2] = x[i];
            if (y[i] > BoundingBox[3])
                BoundingBox[3] = y[i];
        }
//        System.err.println(" " + BoundingBox[1] + " " + BoundingBox[2] + " " + BoundingBox[3]);
    }

    public synchronized double[] getBoundingBox() { return BoundingBox; }
    public synchronized void setBoundingBox(int bbe, double bbv ) {
        BoundingBox[bbe] = bbv;
    }

    public synchronized float getArcLength() {
        float thisLineLength = 0.0F;
        for(int i = 0; i < actingPoint-1 ; i++ ) {
                thisLineLength+=(float)(Point2D.Double.distance(x[i],y[i],x[i+1],y[i+1]));
        }
        return thisLineLength;
    }
    /**
    * Return an array of measured points
    * @param tdir Direction of arc to calc true forward false reverse
    * @return An array of Point3D where Z is measure
    */
    public synchronized Point3D[] getMeasuredPoints(boolean tdir) {
        float thisLineLength = 0.0F;
        Point3D[] returnPoints = new Point3D[actingPoint];
        if( tdir ) {
                for(int i = 0; i < actingPoint-1 ; i++ ) {
                        returnPoints[i] = new Point3D(x[i], y[i], thisLineLength);
                        thisLineLength += (float)(Point2D.Double.distance(x[i],y[i],x[i+1],y[i+1]));
                }
                // pop in last point
                returnPoints[actingPoint-1] = new Point3D(x[actingPoint-1], y[actingPoint-1], thisLineLength);
        } else {
                int jcnt = 0;
                for(int i = actingPoint-1; i > 0 ; i-- ) {
                        returnPoints[jcnt++] = new Point3D(x[i], y[i], thisLineLength);
                        thisLineLength += (float)(Point2D.Double.distance(x[i],y[i],x[i-1],y[i-1]));
                }
                // pop in last point
                returnPoints[jcnt] = new Point3D(x[0], y[0], thisLineLength);
        }
        return returnPoints;
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
