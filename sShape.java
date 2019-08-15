package com.neocoretechs.pathstar;
/**
* Interface for shape types, gives us back the shape type ordinal
* shapes have enough attributes different that we can't completely OO them..
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public interface sShape {
        public int getShapeType();
        public void computeBoundingBox();
        public double[] getBoundingBox();
}
