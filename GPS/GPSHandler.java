/**
* GPS coord upload and retrieve
* @author Groff (C) NeoCoreTechs 2000
*/
package com.neocoretechs.powerspaces.server.handler;
import com.neocoretechs.powerspaces.*;
import com.neocoretechs.powerspaces.server.*;
import java.util.*;
import java.io.*;
import GPSObject;

public class GPSHandler {
        //
        private static Hashtable idAndCoords = new Hashtable();
        private static Vector reXmit = new Vector();
        private static PowerSpace PS = null;
        private static PKRemote pkr = null;
        private static Object[] oargs = new Object[1];
        /**
        */
        public static synchronized Object PowerKernel_SendGPS(Integer leg, CustomerConnectionPanel ccp, GPSObject gpso ) throws PowerSpaceException, FinishedException
	{
                try {
                    idAndCoords.put(gpso.getId(), gpso);
                    if( reXmit.contains(gpso.getId()) ) {
                        oargs[0] = gpso;
                        pkr.invoke("SendGPS",oargs);
                    }
                    // check waiters and notify
                    Enumeration evw = ConnectionPanel.CustomerConnectionPanelTable.elements();
                    if( evw != null ) {
                        // each panel
                        while( evw.hasMoreElements() ) {
                                CustomerConnectionPanel eccp = (CustomerConnectionPanel)(evw.nextElement());
                                // get the vector of sessions waited on from CCP properties table
                                Vector wv = (Vector)(eccp.properties.get("GPSWait"));
                                if( wv == null ) continue;
                                if( wv.contains(gpso.getId()) ) {
                                        eccp.queuePacket(gpso);
                                        System.out.println("SendGPS queued Packet to " + eccp.getSession());
                                }
                        }
                    }
                        
                } catch(Exception e) {
                        System.out.println("SendGPS exception "+e);
                        return e;
		}
                return "OK";
        }

        /**
        * Send GPS and Store in DB
        */
        public static synchronized Object PowerKernel_SendGPSandStore( Integer leg, CustomerConnectionPanel ccp, GPSObject gpso ) throws PowerSpaceException, FinishedException
	{
                Object oret = PowerKernel_SendGPS(leg, ccp, gpso);
                if( oret instanceof Exception ) return oret;
                return DomainHandler.PowerKernel_Store(leg, ccp, "GPSdb", gpso);
        }

        /**
        * Wait to be notified, then queue the GPSObject
        * We don't do anything but set up wait vector in CCP and
        * SendGPS does the rest; notifying those in the wait vector
        */
        public static synchronized void PowerKernel_GetGPS( Integer leg, CustomerConnectionPanel ccp, String id ) throws PowerSpaceException, FinishedException
	{
                try {
                    GPSObject tgpso = (GPSObject)(idAndCoords.get(id));    
                    Vector wv = (Vector)(ccp.properties.get("GPSWait"));
                    // create and queue first one?
                    if( wv == null ) {
                                Vector v = new Vector();
                                v.addElement(id);
                                ccp.properties.put("GPSWait", v);
                                // queue first one
                                if( tgpso != null ) {
                                        ccp.queuePacket(tgpso);
                                        System.out.println("GetGPS queued Packet to " + ccp.getSession());
                                }
                    } else {
                                // we have wait vector, does it have ID?
                                if( !wv.contains( id ) ) {
                                        wv.addElement(id);
                                        // and queue first
                                        if( tgpso != null ) {
                                                ccp.queuePacket(tgpso);
                                                System.out.println("GetGPS queued Packet to " + ccp.getSession());
                                        }
                                }
                    }
                } catch(Exception e) {
                        System.out.println("GetGPS exception "+e);
                        try {
                        ccp.queuePacket(e);
                        } catch(IOException ioe) {} //what more can we do?
                          catch(ClassNotFoundException cnfe) {}
		}
        }

        /**
        * Set up wait for GPSObjects for all ids.  We will be notified
        * via queued packet by SendGPS.
        * Client has to just keep getting queued packets via powerspace.collect.
        */
        public static synchronized Object PowerKernel_GetGPSAll( Integer leg, CustomerConnectionPanel ccp) throws PowerSpaceException, FinishedException
	{
                Enumeration enum = idAndCoords.keys();
                String ids;
                Vector wv = (Vector)(ccp.properties.get("GPSWait"));
                // create?
                if( wv == null ) {
                        wv = new Vector();
                        ccp.properties.put("GPSWait", wv);
                }
                while(enum.hasMoreElements()) {
                    ids = (String)(enum.nextElement());
                    // we have wait vector, does it have ID?
                    if( !wv.contains( ids ) ) {
                        wv.addElement(ids);
                    }
                }
                return "OK";
        }

        /**
        * Set retransmission to another GPS server, coords will be forwarded
        */
        public static synchronized Object PowerKernel_SetRetransmit( Integer leg, CustomerConnectionPanel ccp, String id, String toURL ) throws PowerSpaceException, FinishedException
	{
                try {
                    if( !reXmit.contains(id) ) {
                        reXmit.addElement(id);
                        PS = new PowerSpace(toURL, id);
                        pkr = PS.getRemote("com.neocoretechs.powerspaces.server.handler.GPSHandler");
                    }
                } catch(Exception e) {
                        System.out.println("SetRetransmit exception connecting to PowerSpace "+e);
                        return e;
                }
                return "OK";
        }
}

