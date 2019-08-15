package com.neocoretechs.pathstar;
import java.util.Vector;
/**
* State class.  Abstracts the primary component of path finding to
* the generation of children nodes (also States), is the goal found,
* and estimate of the proximity to the goal.
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public interface State {
  public Vector generateChildren();
  public boolean goalp();
  public float costs();
  public float estimate();
}



 
