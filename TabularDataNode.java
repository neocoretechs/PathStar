package com.neocoretechs.pathstar;
import java.util.*;
/**
* The TabularDataNode is used as an element in the TreeMap binary tree
* that holds order of tabular data for given key elements.  It provides a means
* to compare the keys of nodes in the tree.
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class TabularDataNode implements Comparator {
        public int[] columns;
        public int row;
        public Layer dataLayer;

        public TabularDataNode() {}

        public TabularDataNode(int trow, int[] tcolumns, Layer tdataLayer) {
                row = trow;
                columns = tcolumns;
                dataLayer = tdataLayer;
        }
        /**
        * compare method of Comparator interface
        */
        public int compare(Object o1, Object o2) {
                for(int i = 0; i < columns.length; i++) {
                        int column = ((TabularDataNode)o1).columns[i];
                        int crow = ((TabularDataNode)o1).row;
                        String elem1 = dataLayer.getElement(column, crow);
                        int column2 = ((TabularDataNode)o2).columns[i];
                        int crow2 = ((TabularDataNode)o2).row;
                        String elem2 = dataLayer.getElement(column2, crow2);
                        int retval = elem1.compareTo(elem2);
                        if( retval != 0 ) return retval;
                }
                return 0;
        }
        public boolean equals(Object o2) {
                for(int i = 0; i < columns.length; i++) {
                        String elem1 = dataLayer.getElement(columns[i], row);
                        int column2 = ((TabularDataNode)o2).columns[i];
                        int crow2 = ((TabularDataNode)o2).row;
                        String elem2 = dataLayer.getElement(column2, crow2);
                        int retval = elem1.compareTo(elem2);
                        if( retval != 0 ) return false;
                }
                return true;
        }

        public String toString() {
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < columns.length; i++) {
                        String elem1 = dataLayer.getElement(columns[i], row);
                        sb.append(dataLayer.names[columns[i]]);
                        sb.append(",");
                        sb.append(dataLayer.types[columns[i]]);
                        sb.append(":");
                        sb.append(elem1);
                        sb.append("\r\n");
                }
                return sb.toString();
        }

        public String toString(int[] tcolumns) {
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < tcolumns.length; i++) {
                        String elem1 = dataLayer.getElement(tcolumns[i], row);
                        sb.append(dataLayer.names[tcolumns[i]]);
                        sb.append(",");
                        sb.append(dataLayer.types[tcolumns[i]]);
                        sb.append(":");
                        sb.append(elem1);
                        sb.append("\r\n");
                }
                return sb.toString();
        }
}
