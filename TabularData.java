package com.neocoretechs.pathstar;
import java.util.*;
import java.awt.geom.*;
/**
* The TabularData class uses the TreeMap to sort the elements of Layer
* tables according to a particular field set
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class TabularData {
    private TreeMap treeMap = null;
    // comparator
    private Iterator iter;

    public void put(int row, int[] columns, Layer tvalue) {
       TabularDataNode ttdn = new TabularDataNode(row, columns, tvalue);
       if( treeMap == null )
                treeMap = new TreeMap(ttdn);
       treeMap.put(ttdn, tvalue);
    }
    /**
    * Get closest
    */
    public void setFirst() {
       try {
                iter = treeMap.keySet().iterator();
       } catch(Exception nse) {
                System.out.println("Tabular data exception "+nse.getMessage());
       }
    }

    /**
    * return row ordinal for next in ordered set
    * @return Next row number in Layer or -1 if end
    */
    public int getNext() {
        TabularDataNode ltn;
        if(!iter.hasNext()) return -1;
        ltn = (TabularDataNode)(iter.next());
        return ltn.row;
    }
    /**
    * return row ordinal for next in ordered set
    * @return Next row number in Layer or -1 if end
    */
    public TabularDataNode getNextNode() {
        TabularDataNode ltn;
        if(!iter.hasNext()) return null;
        ltn = (TabularDataNode)(iter.next());
        return ltn;
    }
}
