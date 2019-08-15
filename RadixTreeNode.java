package com.neocoretechs.pathstar;
import java.util.*;
/**
* The RadixTreeNode is used as an element in the TreeMap binary tree
* that holds encoded values for spatial envelopes.  It provides a means
* to compare the keys of nodes in the tree.
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class RadixTreeNode implements Comparator {
        public long radixKey;
        /**
        * compare method of Comparator interface
        */
        public int compare(Object o1, Object o2) {
                if( ((RadixTreeNode)o1).radixKey <
                    ((RadixTreeNode)o2).radixKey ) return -1;

                if( ((RadixTreeNode)o1).radixKey >
                    ((RadixTreeNode)o2).radixKey ) return 1;

                return 0;
        }
        public boolean equals(Object o2) {
                return (radixKey == ((RadixTreeNode)o2).radixKey);
        }

        public String toString() { return String.valueOf(radixKey); }
}
