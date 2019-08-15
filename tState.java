package com.neocoretechs.pathstar;
import java.util.Vector;
import java.util.Hashtable;
import java.awt.geom.*;
/**
* Implementation of "State" with the concreteity of endpoint nodes and an arc.
* This is used in pathfinding
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class tState implements State {
  private boolean notLine;

  private Point2D.Float thisNode;
  private Point2D.Float oppoNode;

  private sArc thisLine;
  private float thisLineLength;

  private boolean lineDir;

  private static Point2D.Float destxy;

  // the Hashtable of Vectors of node->lines
  public static Hashtable nodeTable = new Hashtable();

  public tState(Point2D.Float tnode) {
        thisNode = tnode;
        thisLine = null;
        notLine = true;
  }
  public tState(Point2D.Float tendp, sArc tline, boolean tdir) {
        thisNode = tendp;
        thisLine = tline;
        notLine = false;
        // put in Hashtable of Vectors, each endpoint pointing to this
        thisLineLength = tline.getArcLength();
        lineDir = tdir;
        buildNodes(thisNode);
        oppoNode = tdir ? (Point2D.Float)(thisLine.getP2()) :
                          (Point2D.Float)(thisLine.getP1());
  }

  /**
  * Return the arc within this state
  */
  public sArc getArc() { return thisLine; }

  /**
  * Return direction the arc was built in (true - natural dir, false - opposite)
  */
  public boolean getDir() { return lineDir; }

  /**
  * Find the node in table, extract all line segs...ifnot in node table
  * create Vector and place with key being endpoint
  */
  private void buildNodes(Point2D.Float lineEndPoint) {
        Vector vn = (Vector)(nodeTable.get(lineEndPoint));
        if( vn == null ) {
                vn = new Vector();
                vn.addElement(this);
                nodeTable.put(lineEndPoint, vn);
        } else
                if( !vn.contains(this) )
                        vn.addElement(this);
  }

  public static void setDest(float dx, float dy ) {
        destxy = new Point2D.Float(dx, dy);
  }
  public static void setDest(Point2D.Float dxy) {
        destxy = dxy;
  }

  public static void clear() { nodeTable.clear(); }

  /**
  * return Vector of states at this node.  first we update old nodeFrom
  * to the opposite endpoint of line it was on.
  */
  public Vector generateChildren() {
        if( notLine )
                return (Vector)(nodeTable.get(thisNode));
        return (Vector)(nodeTable.get(oppoNode));
  }

  /**
  * See if we have reached goal.  This happens before child gen so we
  * can check the nodeFrom to see if it matches destination
  */
  public boolean goalp() {
        return notLine ? thisNode.equals(destxy):
                oppoNode.equals(destxy);
  }

  /**
  * estimate is Sedgewick-Vitter Euclidian dist to goal
  */
  public float estimate() {
        
        return (float)(destxy.distance(notLine ? thisNode : oppoNode));
  }
  /**
  * costs is distance of line seg
  */
  public float costs() { return thisLineLength; } 

  public String toString() {
  if( notLine )
        return "start node " + thisNode;
        else
        return
//        ( String.valueOf(thisLine.getP1().getX())+
//        "," + String.valueOf(thisLine.getP1().getY())+" "+
//        String.valueOf(thisLine.getP2().getX())+
//        "," + String.valueOf(thisLine.getP2().getY())+" "+lineDir );
        ( String.valueOf(thisNode.getX())+
        "," + String.valueOf(thisNode.getY())+" "+
        String.valueOf(oppoNode.getX())+
        "," + String.valueOf(oppoNode.getY())+" " );
  }
}
