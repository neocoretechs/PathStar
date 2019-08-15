package com.neocoretechs.pathstar;
import java.awt.geom.*;
/**
* 3D Point, the Z may be a measure along an arc
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class Point3D {
        private Point2D.Float xy;
        private float z;
        public Point3D(float tx, float ty, float tz) {
                xy = new Point2D.Float(tx, ty);
                z = tz;
        }
        public Point3D(double tx, double ty, float tz) {
                xy = new Point2D.Float((float)(tx), (float)(ty));
                z = tz;
        }
        public float getX() { return (float)(xy.getX()); }
        public float getY() { return (float)(xy.getY()); }
        public float getZ() { return z; }
        public void setXY(float tx, float ty) { xy.setLocation(tx, ty); }
        public void setZ(float tz) { z = tz; }

        public String toString() { return xy.toString()+","+z; }
}
