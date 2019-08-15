package com.neocoretechs.pathstar;
import java.util.*;
/**
* A* path finding algorithm. this one uses float values for estimation.
* An "open" and "closed" table are used to keep track of progress
*
*/
final class node {
  State state;
  float costs;
  float distance;
  float total;
  node parent;
  node(State theState, node theParent,  float theCosts, float theDistance) {
    state = theState;
    parent = theParent;
    costs = theCosts;
    distance = theDistance;
    total = theCosts + theDistance;
  };
}


public final class AStar {

  private final Hashtable open = new Hashtable(500);
  private final Hashtable closed = new Hashtable(500);
  public int evaluated = 0;
  public int expanded = 0;
  public float bestTotal = 0;
  public boolean ready = false;
  private boolean newBest = false;
//  private float tolerance = 0.000001F;
  private final Vector nodes = new Vector(); //sorted open node


  private synchronized void setBest (float value) {
    bestTotal = value;
    newBest = true;
    notify(); // All?
    Thread.currentThread().yield();  //for getNewBest
  }

//  public void setTolerance(float tol) { tolerance = tol; }

  public synchronized float getNewBest() {
    while(!newBest) {
      try {
	wait();
      } catch (InterruptedException e) {
      }
    }
    newBest = false;
    return bestTotal;
  }
  

  private node search() {
    node best;
    Vector childStates;
    float childCosts;
    Vector children = new Vector();
    
    while (!(nodes.isEmpty())) {
      best = (node) nodes.firstElement();
       if(closed.get(best.state) != null) { //to avoid having to remove
 	nodes.removeElementAt(0);          // improved nodes from nodes
 	continue;
       }
      if (!(best.total == bestTotal)) {
        setBest(best.total);
      }
      if ((best.state).goalp()) return best;

      children.removeAllElements();
//      System.out.println("child gen start");
      childStates = (best.state).generateChildren();
//      System.out.println("child gen end "+ childStates);
//      childCosts = tolerance + best.costs;
      expanded++;
      //
      // iterate through children states
      //
      for (int i = 0; i < childStates.size(); i++) {
	State childState = (State) childStates.elementAt(i);
        childCosts = childState.costs() + best.costs;
	node closedNode = null;
	node openNode = null;
	node theNode = null;

	if ((closedNode = (node) closed.get(childState)) == null)
	  openNode = (node) open.get(childState);
	theNode = (openNode != null) ? openNode : closedNode;
	if (theNode != null) {
	  if (childCosts < theNode.costs) {
	    if (closedNode != null) {        
	      open.put(childState, theNode);
	      closed.remove(childState);
	    } else {
              float dist = theNode.distance;
	      theNode = new node(childState, best, childCosts, dist);
	      open.put(childState, theNode);
	       //nodes.removeElement(theNode); //get rid of this
	    }
	    theNode.costs = childCosts;
	    theNode.total = theNode.costs + theNode.distance;
	    theNode.parent = best;
	    children.addElement(theNode);
	    
	  }
	} else {
          float estimation;
	  node newNode;

	  estimation = childState.estimate();
	  newNode = new node(childState, best, childCosts, estimation);
	  open.put(childState, newNode);
	  evaluated++;
	  children.addElement(newNode);
	}
      }
      open.remove(best.state);
      closed.put(best.state, best);
      nodes.removeElementAt(0);
      addToNodes(children); // update nodes
      
    }
    return null; //no open nodes and no solution
  }

/*
  private int rbsearch(int l, int h, int tot, int costs){
    if(l>h) return l; //insert before l
    int cur = (l+h)/2;
    int ot = ((node) nodes.elementAt(cur)).total;
    if((tot < ot) ||
       (tot == ot && costs >= ((node) nodes.elementAt(cur)).costs))
      return rbsearch(l, cur-1, tot, costs);
    return rbsearch(cur+1, h, tot, costs);
  }
*/

  private int bsearch(int l, int h, float tot, float costs){
    int lo = l;
    int hi = h;
    while(lo<=hi) {
      int cur = (lo+hi)/2;
      float ot = ((node) nodes.elementAt(cur)).total;
      if((tot < ot) ||
	 (tot == ot && costs >= ((node) nodes.elementAt(cur)).costs))
	hi = cur - 1;
      else
	lo = cur + 1;
    }
    return lo; //insert before lo
  }
      


  private  void addToNodes(Vector children) {
    for (int i = 0; i < children.size(); i++) {
      node newNode = (node) children.elementAt(i);
      float newTotal = newNode.total;
      float newCosts = newNode.costs;
      boolean done = false;
      int idx = bsearch(0, nodes.size()-1, newTotal, newCosts);
      nodes.insertElementAt(newNode, idx);    
    }
  }


  public final Vector solve (State initialState) {
    node solution;
    node firstNode;
    float estimation;
    
    expanded = 0;
    evaluated = 1;
    estimation = initialState.estimate();
    firstNode = new node(initialState, null, 0.0F, estimation);

    open.put(initialState, firstNode);
    nodes.addElement(firstNode);

    solution = search();
    nodes.removeAllElements();
    open.clear();
    closed.clear();
    ready = true;
    setBest(bestTotal);
    return getSequence(solution);
  }


  private Vector getSequence(node n) {
    Vector result;
    if (n == null) {
      result = new Vector();
    } else {
      result = getSequence (n.parent);
      result.addElement(n.state);
    }
    return result;
  }

}

