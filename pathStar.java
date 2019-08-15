package com.neocoretechs.pathstar;
import java.util.*;
import java.awt.geom.*;
/**
* pathStar - pathStar finds routes through a network using a modified
* AStar pathfinding algorithm where the costs are the length of the
* line segment and the estimate is the Sedgewick-Vitter Euclidian distance
* to the destination.  An in-memory Peano key radix trie is used to resolve
* the Vector
* of points composing the path to closest actual nodes in the network.<br>
* Use the setCoverage method to set the radix scale (power of 10 to * coords).
* X and Y offsets (for coords in negative space, Peano key funny about negs)<br>
* Use the buildTopology method to add segments to the network.<br>
* Use the buildRoutes method to pass a Vector of Point2D.Floats which are
* the points to route through.  You will get back a Vector of Vectors of segments
* between nodes, from first point in route array to last point.
* @author Groff Copyright (C) NeoCoreTechs, 2000
*/
public final class pathStar {
        private AStar as = new AStar();
        private RadixTree rtree = new RadixTree(0, 0.0F, 0.0F);

        public pathStar() {}

        public pathStar(int iradix, float txoffset, float tyoffset) {
                rtree = new RadixTree(iradix, txoffset, tyoffset);
        }
        /**
        * Set the coverage area and construct radix tree
        * @param iradix Power of 10 to multiply coords by to scale
        * @param txoffset The X offset to add
        * @param tyoffset The Y offset to add
        */
        public void setCoverage(int iradix, float txoffset, float tyoffset) {
                rtree = new RadixTree(iradix, txoffset, tyoffset);
        }
        /**
        * Build the state and radix trie tables
        * @param sa The arc whose endpoints are extracted
        */
        public void buildTopology(sArc sa) {
                Point2D.Float l0P1 = sa.getP1();
                Point2D.Float l0P2 = sa.getP2();
                new tState( l0P1, sa, true );
                new tState( l0P2, sa, false );
//                System.out.println(l0P1.toString()+l0P2.toString());
                rtree.put((float)(l0P1.getX()), (float)(l0P1.getY()), l0P1);
                rtree.put((float)(l0P2.getX()), (float)(l0P2.getY()), l0P2);
        }
        /**
        * Build the state and radix trie tables
        * @param sa The arc whose endpoints are extracted
        */
        public void buildTopology(Layer la) {
            try {
                int nc = la.getNumberFeatures();
                System.out.println("Begin build...");
                long s1 = System.currentTimeMillis();
                for(int j = 0; j < nc ; j++) {
                        sArc ca = (sArc)(la.getShape(j));
//                        System.out.println(ca.getP1()+" "+ca.getP2());
                        buildTopology(ca);
                }
                long s2 = System.currentTimeMillis();
                System.out.println("End build..."+(s2-s1));
            } catch(Exception ie) { System.out.println(ie.getMessage()); ie.printStackTrace(); }
         }

        /**
        * build the route from Vector of Point2D.Float points
        * on the network.
        * @param routePoints The Vector of points to route through
        * @return The Vector of Vectors of each node-node collection of segs
        */
        public Vector buildRoutes(Vector routePoints) throws Exception {
                // set origin and dest, find closest radixtree nodes
                Point2D.Float dest = null;
                Point2D.Float orig = null;

                Point2D.Float routePointDest = null;
                Point2D.Float routePointOrigin = null;

                // Vector of Vectors of route segments
                Vector routeSegs = new Vector();

//                long s2 = System.currentTimeMillis();

                //
                // If we are on the first point, extract origin from radix tree closest
                // otherwise we are using old destination for new origin at each iteration.
                //
                int rpctr = routePoints.size()-1;
                for( int i = 0 ; i < rpctr ; i++ ) {
                        if( orig == null ) {
                                routePointOrigin = (Point2D.Float)(routePoints.elementAt(i));
                                orig = (Point2D.Float)( rtree.get((float)(routePointOrigin.getX()),(float)(routePointOrigin.getY())) );
                        } else {
                                routePointOrigin = routePointDest;
                                orig = dest;
                        }
//                        System.out.println("Origin: "+routePointOrigin+" "+orig);
                        State s = new tState(orig);
                        routePointDest = (Point2D.Float)(routePoints.elementAt(i+1)); 
                        dest = (Point2D.Float)( rtree.get((float)(routePointDest.getX()),(float)(routePointDest.getY())) );
                        tState.setDest(dest);
//                        System.out.println("Destination: "+routePointDest+" "+dest);
                        Vector v = as.solve(s);
                        if( v == null ) {
                                System.out.println("No route through at point "+dest);
                                System.exit(1);
                        }
                        routeSegs.addElement(v);
//                        tState.clear();
//                for(int i = 0 ; i < v.size() ; i++) {
//                     System.out.println(v.elementAt(i).toString());
//                }
                } // for each route point

//                long s3 = System.currentTimeMillis();
//                System.out.println("solve: "+(s3-s2));

                return routeSegs;
        }
        /**
        * Pass the Vector of tState vectors and extract a
        * new Vector of measured points with redundant points removed
        * @param routeSegs The Vector of tState Vectors
        * @return Vector of Point3Ds with Z as measure
        */
        public static Vector getMeasuredPoints(Vector routeSegs) {
                Enumeration ev = routeSegs.elements();

                float pathLength = 0.0F;
                int numSegs = 0;
                // we throw out first point unless first arc in
                // contigous stream of points else we get redundant pts
                int jStrt = 0;

                Vector mPoints = new Vector();

                while(ev.hasMoreElements()) {
                        Vector v = (Vector)(ev.nextElement());
                        // element 0 is always start node tState, get rid of it
                        v.removeElementAt(0);
                        numSegs+=v.size();
                        for(int i = 0 ; i < v.size() ; i++) {
                                // create array of points
                                tState tArc = ((tState)(v.elementAt(i)));
                                Point3D[] tArcPts = tArc.getArc().getMeasuredPoints(tArc.getDir());
                                for(int j=jStrt; j < tArcPts.length; j++) {
                                        tArcPts[j].setZ(tArcPts[j].getZ() + pathLength);
                                        mPoints.addElement(tArcPts[j]);
                                }
                                pathLength+= tArc.costs();
                                jStrt = 1;
                                //System.out.println(v.elementAt(i).toString());
                                //System.out.println("num ="+v.size());
                        }
                }
//                System.out.println("Length: "+pathLength+" in "+numSegs+" segments "+mPoints.size()+" points");
//                System.out.println(mPoints);
                return mPoints;
        }
}
