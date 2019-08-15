package com.neocoretechs.pathstar;
import java.util.*;
import java.awt.geom.*;
/**
* Radix tree, or trie as it says in the lit.
* We are taking 2 32 bit ints and making a 64 bit linear key
* representing an x,y coord.  We are interchanging bits to do this al la Peano
* key.
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class RadixTree {
        public float radix = 1.0F;
        public float xoffset, yoffset;
        private TreeMap treeMap;
        // comparator
        private RadixTreeNode rtn = new RadixTreeNode();
        private RadixTreeNode rtnHigh = new RadixTreeNode();

        public RadixTree(int tradix, float txoffset, float tyoffset) {
                if( tradix > 0 ) radix = (float)(Math.pow((double)10, (double)tradix));
                xoffset = txoffset;
                yoffset = tyoffset;
                treeMap = new TreeMap(rtn);
        }

        public long makeKey(float x, float y) {
                int xi = (int)((x + xoffset) * radix);
                int yi = (int)((y + yoffset) * radix);

                long kxy;

                kxy = (xi >> 31) & 1;
                kxy <<= 1;
                kxy += (yi >> 31) & 1;

                kxy <<= 1;
                kxy += (xi >> 30) & 1;
                kxy <<= 1;
                kxy += (yi >> 30) & 1;

                kxy <<= 1;
                kxy += (xi >> 29) & 1;
                kxy <<= 1;
                kxy += (yi >> 29) & 1;

                kxy <<= 1;
                kxy += (xi >> 28) & 1;
                kxy <<= 1;
                kxy += (yi >> 28) & 1;

                kxy <<= 1;
                kxy += (xi >> 27) & 1;
                kxy <<= 1;
                kxy += (yi >> 27) & 1;

                kxy <<= 1;
                kxy += (xi >> 26) & 1;
                kxy <<= 1;
                kxy += (yi >> 26) & 1;

                kxy <<= 1;
                kxy += (xi >> 25) & 1;
                kxy <<= 1;
                kxy += (yi >> 25) & 1;

                kxy <<= 1;
                kxy += (xi >> 24) & 1;
                kxy <<= 1;
                kxy += (yi >> 24) & 1;

                kxy <<= 1;
                kxy += (xi >> 23) & 1;
                kxy <<= 1;
                kxy += (yi >> 23) & 1;

                kxy <<= 1;
                kxy += (xi >> 22) & 1;
                kxy <<= 1;
                kxy += (yi >> 22) & 1;

                kxy <<= 1;
                kxy += (xi >> 21) & 1;
                kxy <<= 1;
                kxy += (yi >> 21) & 1;

                kxy <<= 1;
                kxy += (xi >> 20) & 1;
                kxy <<= 1;
                kxy += (yi >> 20) & 1;

                kxy <<= 1;
                kxy += (xi >> 19) & 1;
                kxy <<= 1;
                kxy += (yi >> 19) & 1;

                kxy <<= 1;
                kxy += (xi >> 18) & 1;
                kxy <<= 1;
                kxy += (yi >> 18) & 1;

                kxy <<= 1;
                kxy += (xi >> 17) & 1;
                kxy <<= 1;
                kxy += (yi >> 17) & 1;

                kxy <<= 1;
                kxy += (xi >> 16) & 1;
                kxy <<= 1;
                kxy += (yi >> 16) & 1;

                kxy <<= 1;
                kxy += (xi >> 15) & 1;
                kxy <<= 1;
                kxy += (yi >> 15) & 1;

                kxy <<= 1;
                kxy += (xi >> 14) & 1;
                kxy <<= 1;
                kxy += (yi >> 14) & 1;

                kxy <<= 1;
                kxy += (xi >> 13) & 1;
                kxy <<= 1;
                kxy += (yi >> 13) & 1;

                kxy <<= 1;
                kxy += (xi >> 12) & 1;
                kxy <<= 1;
                kxy += (yi >> 12) & 1;

                kxy <<= 1;
                kxy += (xi >> 11) & 1;
                kxy <<= 1;
                kxy += (yi >> 11) & 1;

                kxy <<= 1;
                kxy += (xi >> 10) & 1;
                kxy <<= 1;
                kxy += (yi >> 10) & 1;

                kxy <<= 1;
                kxy += (xi >> 9) & 1;
                kxy <<= 1;
                kxy += (yi >> 9) & 1;

                kxy <<= 1;
                kxy += (xi >> 8) & 1;
                kxy <<= 1;
                kxy += (yi >> 8) & 1;

                kxy <<= 1;
                kxy += (xi >> 7) & 1;
                kxy <<= 1;
                kxy += (yi >> 7) & 1;

                kxy <<= 1;
                kxy += (xi >> 6) & 1;
                kxy <<= 1;
                kxy += (yi >> 6) & 1;

                kxy <<= 1;
                kxy += (xi >> 5) & 1;
                kxy <<= 1;
                kxy += (yi >> 5) & 1;

                kxy <<= 1;
                kxy += (xi >> 4) & 1;
                kxy <<= 1;
                kxy += (yi >> 4) & 1;

                kxy <<= 1;
                kxy += (xi >> 3) & 1;
                kxy <<= 1;
                kxy += (yi >> 3) & 1;

                kxy <<= 1;
                kxy += (xi >> 2) & 1;
                kxy <<= 1;
                kxy += (yi >> 2) & 1;

                kxy <<= 1;
                kxy += (xi >> 1) & 1;
                kxy <<= 1;
                kxy += (yi >> 1) & 1;

                kxy <<= 1;
                kxy += xi & 1;
                kxy <<= 1;
                kxy += yi & 1;

                return kxy;
    }

    public void put(float x, float y, Object tvalue) {       
       RadixTreeNode trtn = new RadixTreeNode();
       trtn.radixKey = makeKey(x, y);
       treeMap.put(trtn, tvalue);
    }
    /**
    * Get closest
    */
    public Object get(float x, float y) {
       rtn.radixKey = makeKey(x, y);
       long targKey = rtn.radixKey;
       // flattened bit range, may lose some on bounds
       // rtn.radixKey &= 0x7FFFFFFFFFF00000L;
       // rtnHigh.radixKey = rtn.radixKey | 0xFFFFFL;
       // we make a real range, but it costs a little
        rtn.radixKey = makeKey(x-500.0F,y-500.0F);
        rtnHigh.radixKey = makeKey(x+500.0F,y+500.0F);
//        rtnHigh.radixKey = 0x7FFFFFFFFFFFFFFFL;
//       System.out.println(rtn.radixKey+" "+rtnHigh.radixKey);
       SortedMap tsm = treeMap.subMap(rtn, rtnHigh);
       Object ltn = null;
       Object gtn = null;
       float diff, minDiff = 0.0F;
       Iterator iter;
       try {
                iter = tsm.keySet().iterator();
                boolean first = true;
                while(iter.hasNext()) {
                        if( first ) {
                                ltn = iter.next();
                                first = false;
                                minDiff = (float)((Point2D.Float)(treeMap.get(ltn))).distance(x,y);
                                //minDiff =(long)(Math.abs(((RadixTreeNode)(ltn)).radixKey - targKey));
//                        System.out.println("Found init "+ltn+" "+minDiff);
                        } else {
                                gtn = iter.next();
                                // find diffs in radix value,
                                // least diff between rtn and here gets it
                                //diff =(long)(Math.abs(((RadixTreeNode)(gtn)).radixKey - targKey));
                                diff = (float)((Point2D.Float)(treeMap.get(gtn))).distance(x,y);
                                if( diff < minDiff ) {
                                        minDiff = diff;
                                        ltn = gtn;
//                                        System.out.println("Found "+ltn+" from "+gtn+" diff "+minDiff);
                                }
//                        System.out.println("Iteration was "+gtn);
                        }
               }
       } catch(Exception nse) {
                System.out.println("Path exception "+nse.getMessage());
                return null;
       }
//       System.out.println(ltn);
       return treeMap.get(ltn);
    }

}
