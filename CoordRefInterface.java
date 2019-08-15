package com.neocoretechs.pathstar;
/**
* CoordRefInterface provides an abstraction for changing coordinate references
* for different shape types.  Primarily it was created to plug in different
* implementations of coord ref changers
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public interface CoordRefInterface {
        public void setCoordinateReference(Object cr);
        public Object getCoordinateReference();
        // Anything you desire...
        public void setShape(Object s);
        //
        public Object getShape();
}
