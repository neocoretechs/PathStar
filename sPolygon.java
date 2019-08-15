package com.neocoretechs.pathstar;
import java.awt.Color;
import java.io.*;
/**
* Polygon type with symbology
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public final class sPolygon implements sShape, Serializable, Cloneable
{
    private double BoundingBox[] = {0.0,0.0,0.0,0.0};
    private double x[];
    private double y[];
    private boolean closed = false;
    private int numberPoints = 0;
    private int actingPoint = 0;
    private int part[];
    private int numberParts = 0;
    private int actingPart = 0;
    private int PolyPartArray[];
    private int penWidth = 1;
    private int penPattern = 2;
    private Color penColor = Color.black;
    private int brushPattern = 2;
    private Color brushFG = Color.green;
    private Color brushBG = Color.white;
    private boolean filled = false;
    private double centroidX;
    private double centroidY;
    private static final int shapeType = 5;

    public sPolygon() {
    }

    public sPolygon(int i) {
        x = new double[i];
        y = new double[i];
        numberPoints = i;
        actingPoint = 0;
    }

    public int getShapeType() { return shapeType; }

    public Object copy() throws CloneNotSupportedException { return clone(); }

    public synchronized void setNumberPoints(int i) {
        x = new double[i];
        y = new double[i];
        numberPoints = i;
        actingPoint = 0;
    }

    public synchronized void setNumberParts(int i) {
        numberParts = i;
        part = new int[i];
        actingPart = 0;
    }

    public synchronized void addPart(int i) {
        part[actingPart++] = i;
    }

    public synchronized void addNode(double d1, double d2) {
        x[actingPoint] = d1;
        y[actingPoint++] = d2;
    }

    public synchronized void addNode(double d1, double d2, int i) {
        x[i] = d1;
        y[i] = d2;
        actingPoint++;
    }

    public synchronized double getX(int i) {
        return x[i];
    }

    public synchronized double getY(int i) {
        return y[i];
    }

    public synchronized int getNumberPoints() {
        return actingPoint;
    }

    public synchronized void computeBoundingBox() {
        BoundingBox[0] = 1.0E8;
        BoundingBox[1] = 1.0E8;
        BoundingBox[2] = -1.0E8;
        BoundingBox[3] = -1.0E8;
        for (int i = 0; i < numberPoints; i++) {
            if (x[i] < BoundingBox[0])
                BoundingBox[0] = x[i];
            if (y[i] < BoundingBox[1])
                BoundingBox[1] = y[i];
            if (x[i] > BoundingBox[2])
                BoundingBox[2] = x[i];
            if (y[i] > BoundingBox[3])
                BoundingBox[3] = y[i];
        }
    }

    public synchronized boolean check() {
        PolyPartArray = new int[numberParts];
        for (int i = 0; i < numberParts; i++)
        {
            if (i == numberParts - 1)
                PolyPartArray[i] = numberPoints - part[i];
            else
                PolyPartArray[i] = part[i + 1] - part[i];
        }
        if (actingPoint == numberPoints) return true;
        System.out.println(">> Polygon error: number of points inconsistent");
        return false;
    }

    public synchronized double getX(int i, int j) {
        if (part[j] + i >= numberPoints)
            System.out.println(">> Bad Part in Polygon: " + part[j]);
        return x[part[j] + i];
    }

    public synchronized double getY(int i, int j) {
        return y[part[j] + i];
    }

    public synchronized double[] getBoundingBox() { return BoundingBox; }
    public synchronized void setBoundingBox(int bbe, double bbv ) {
        BoundingBox[bbe] = bbv;
    }

    /**
    * Check for point in polygon RELIES ON BOUNDING BOX
    * @param x1 The X coord of is-in?
    * @param y1 The Y
    * @return true if in
    */
/*    public synchronized boolean isPointIn(double x1, double y1) {
        if( x1 < BoundingBox[0] || x1 > BoundingBox[2] ||
            y1 < BoundingBox[1] || y1 > BoundingBox[3] ) return false;
        // check for index in part array matching point
        System.out.println("Points "+numberPoints+" Parts "+numberParts);
        int ipart = 0;
        int numberIntersects = 0;
        double x1Line, y1Line, x2Line, y2Line;
        for(int iparts = 0; iparts < numberParts; iparts++ ) {
                // we go from part ot part for outer loop, if last part we go to numpoints
                int lastpart = (iparts == numberParts-1 ? numberPoints : part[iparts+1]);
                for(int ipoints = part[iparts]; ipoints < lastpart; ipoints++) {
                        x1Line = x[ipoints];
                        y1Line = y[ipoints];
                        // if we're on the lat point, close the shape, so to speak
                        if( ipoints == lastpart - 1 ) {
                                x2Line = x[part[iparts]];
                                y2Line = y[part[iparts]];
                        } else {
                                x2Line = x[ipoints+1];
                                y2Line = y[ipoints+1];
                        }
                        //
                        // now see if our point to Y axis intersects
                        //
                        // does it cross at endpoint? if so ignore 1
                        // we got it when it equaled x2 last time
                        // or when it equals x2 at closure
                        if( intersect(x1, y1, x1, 0.0, x1Line, y1Line, x2Line, y2Line) && x1 != x1Line)
                                        numberIntersects++;
                }
        }
        return ((numberIntersects & 1) == 1);
    }
*/
    /**
    * ccw from Sedgewick
    */
/*
    public int ccw(double p0x, double p0y, double p1x, double p1y, double p2x, double p2y) {
        double dx1, dy1, dx2, dy2;
        dx1 = p1x-p0x;
        dy1 = p1y-p0y;
        dx2 = p2x-p0x;
        dy2 = p2y-p0y;
        if( dx1*dy2 > dy1*dx2 ) return 1;
        if( dx1*dy2 < dy1*dx2 ) return -1;
        if( (dx1*dx2 < 0) || (dy1*dy2 < 0) ) return -1;
        if( (dx1*dx1+dy1*dy1) < (dx2*dx2+dy2*dy2) ) return 1;
        return 0;
    }
*/
    /**
    * intersect from Sedgewick
    */
/*
    public boolean intersect(double l1p1x, double l1p1y, double l1p2x, double l1p2y,
                             double l2p1x, double l2p1y, double l2p2x, double l2p2y) {
        return ( (ccw(l1p1x, l1p1y, l1p2x, l1p2y, l2p1x, l2p1y) *
                  ccw(l1p1x, l1p1y, l1p2x, l1p2y, l2p2x, l2p2y)) <= 0 &&
                 (ccw(l2p1x, l2p1y, l2p2x, l2p2y, l1p1x, l1p1y) *
                  ccw(l2p1x, l2p1y, l2p2x, l2p2y, l1p2x, l1p2y)) <= 0 );
    }
*/
    public synchronized boolean isPointIn(double x1, double y1) {
        if( x1 < BoundingBox[0] || x1 > BoundingBox[2] ||
            y1 < BoundingBox[1] || y1 > BoundingBox[3] ) return false;
        // check for index in part array matching point
        System.out.println("Points "+numberPoints+" Parts "+numberParts);
        int numberIntersects = 0;
        for(int iparts = 0; iparts < numberParts; iparts++ ) {
                // we go from part ot part for outer loop, if last part we go to numpoints
                int lastpart = (iparts == numberParts-1 ? numberPoints : part[iparts+1]);
                if( inside_polygon(part[iparts], lastpart - part[iparts], x1, y1) )
                                numberIntersects++;
        }
        return ((numberIntersects & 1) == 1);
    }
    /**
     * Tests if a point is inside a polygon.
     * <p>
     * @param xpts horizontal pixel window points of polygon.
     * @param ypts vertical pixel window points of polygon.
     * @param ptx horizontal pixel window points of location
     * @param pty vertical pixel window points of location.
     * @return boolean
     *
     */
    public boolean inside_polygon(
        int offset, int numverts, double ptx, double pty) {

	int j, inside_flag = 0;
	if (numverts <= 2) return false;
        double vtx0x = 0.0;
        double vtx0y = 0.0;
        double vtx1x = 0.0;
        double vtx1y = 0.0;
	double dv0;                        // prevents OVERFLOW!!
	int crossings = 0;
	boolean xflag0 = false, yflag0 = false, yflag1 = false;

        vtx0x = x[offset+numverts-1];
        vtx0y = y[offset+numverts-1];
	// get test bit for above/below Y axis
        yflag0 = ((dv0 = vtx0y - pty) >= 0);

        int pindex;
	for (j=0; j<numverts; j++) {
            pindex = offset + j;
	    if ((j & 0x1) != 0) {	//HACK - slightly changed
                vtx0x = x[pindex];
                vtx0y = y[pindex];
                yflag0 = ((dv0 = vtx0y - pty) >= 0);
	    }
	    else {
                vtx1x = x[pindex];
                vtx1y = y[pindex];
                yflag1 = (vtx1y >= pty);
	    }

	    /* check if points not both above/below X axis - can't hit ray */
	    if (yflag0 != yflag1) {
		/* check if points on same side of Y axis */
                if ((xflag0 = (vtx0x >= ptx)) == (vtx1x >= ptx)) {
		    if (xflag0) crossings++;
		}
		else {
		    crossings +=
                        ((vtx0x - dv0*(vtx1x-vtx0x)/(vtx1y-vtx0y)) >= ptx)
			? 1 : 0;
		}
	    }
	    inside_flag = crossings & 0x01 ;
	}
	return (inside_flag != 0);
    }



    public synchronized int getPenWidth() { return penWidth; }
    public synchronized int getPenPattern() { return penPattern; }
    public synchronized Color getPenColor() { return penColor; }
    public synchronized int getBrushPattern() { return brushPattern; }
    public synchronized Color getBrushFG() { return brushFG; }
    public synchronized Color getBrushBG() { return brushBG; }

    public synchronized void setPenWidth(int pw) { penWidth = pw; }
    public synchronized void setPenPattern(int pp) { penPattern = pp; }
    public synchronized void setPenColor(Color pc) { penColor = pc; }
    public synchronized void setBrushPattern(int bp) { brushPattern = bp; }
    public synchronized void setBrushFG(Color pc) { brushFG = pc; }
    public synchronized void setBrushBG(Color pc) { brushBG = pc; }
    public void setFilled(boolean tfill) { filled = tfill; }
    public boolean getFilled() { return filled; }

    public synchronized int getPolyPartArray(int ppart) { return PolyPartArray[ppart]; }
    public synchronized int getNumberParts() { return numberParts; }
}
