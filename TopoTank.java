package com.neocoretechs.pathstar;
import com.neocoretechs.pathstar.shapefile.*;
import com.neocoretechs.powerspaces.*;
import com.neocoretechs.powerspaces.server.*;
import java.util.*;
import java.io.*;
/**
* TopoTank is PowerSpaces handler that serves up in-memory topology
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class TopoTank {
        private static Hashtable layers = new Hashtable();
        private static Hashtable pathstars = new Hashtable();
        /**
        * Read a shapefile into the tank
        * @param theLayer The new layer name to create
        * @param theFile The file to read from
        */
        public static synchronized Object PowerKernel_LoadShapeFile(Integer leg, CustomerConnectionPanel ccp, String theLayer, String theFile) throws Exception {
           FileInputStream fis = new FileInputStream(theFile);
           ShapeStreamReader ssr = new ShapeStreamReader(fis);
           Layer topoLayer = ssr.return_layer();
           layers.put(theLayer,topoLayer);
           return "Ok";
        }
        /**
        * Read a dbase file into the tank
        * @param theLayer The new layer name to read into or if it exists, an old one
        * @param theFile The file to read from
        */
        public static synchronized Object PowerKernel_LoadDbaseFile(Integer leg, CustomerConnectionPanel ccp, String theLayer, String theFile) throws Exception {
           FileInputStream fis = new FileInputStream(theFile);
           Layer iLayer = getLayer(theLayer);
           if( iLayer == null ) {
                iLayer = new Layer(theLayer);
                layers.put(theLayer,iLayer);
           }
           DbaseFileReader dbr = new DbaseFileReader(fis, iLayer);
           return "Ok";
        }
        /**
        * receive command (i.e. layer name) and send back topo 
        */
        public static synchronized Object PowerKernel_GetLayer(Integer leg, CustomerConnectionPanel ccp, String theLayer) throws PowerSpaceException {
                Layer rl = (Layer)(layers.get(theLayer));
                return rl;
        }
        /**
        * Set a layer in repository
        * @param theLayer The Layer name
        * @param l The Layer to tank
        */
        public static synchronized Object PowerKernel_SetLayer(Integer leg, CustomerConnectionPanel ccp, String theLayer, Layer l) throws PowerSpaceException {
                layers.put(theLayer,l);
                return "Ok";
        }
        /**
        * Get Layer bounding box
        */
        public static synchronized Object PowerKernel_GetLayerBoundingBox(Integer leg, CustomerConnectionPanel ccp, String theLayer) throws PowerSpaceException {
                Layer rl = (Layer)(layers.get(theLayer));
                if( rl == null ) throw new PowerSpaceException("No Layer "+rl+" loaded..");
                return rl.getBoundingBox();
        }
        /**
        * convenience for internal modules
        */
        public static synchronized Layer getLayer(String name) {
                return (Layer)(layers.get(name));
        }
        /**
        * convenience for internal modules
        */
        public static synchronized void setLayer(String name, Layer l) {
                layers.put(name, l);
        }
        /**
        * Build the topology for the layer name
        * @param theLayer The layer name to build the topology for (from layers table)
        * @return "Ok" if ok
        * @throw PowerSpaceException if the layer is not loaded 
        */
        public static synchronized Object PowerKernel_BuildTopology(Integer leg, CustomerConnectionPanel ccp, String theLayer) throws PowerSpaceException {
                Layer rl = (Layer)(layers.get(theLayer));
                if( rl == null ) throw new PowerSpaceException("No Layer "+rl+" loaded..");
                pathStar ps = new pathStar();
                ps.buildTopology(rl);
                pathstars.put(theLayer, ps);
                return "Ok";
        }

        /**
        * Generate routes
        * @param theLayer The layer with tabular to generate from
        * @param topoLayer The layer with topo
        * @param newLayer New layer to generate
        * @param orderfields The fields to order by
        * @param groupfields The fields by which the points are grouped to routes
        * @param xyfields The x and y coord fields
        */
        public static synchronized Object PowerKernel_GenerateRoutes(Integer leg, CustomerConnectionPanel ccp, String theLayer, String topoLayer, String newLayer, String[] orderFields, String[] groupFields, String[] xyFields) throws PowerSpaceException {
                Layer l = getLayer(theLayer);
                if( l == null ) throw new PowerSpaceException("Layer "+theLayer+" not loaded..");
                pathStar ps = (pathStar)(pathstars.get(topoLayer));
                if( ps == null ) throw new PowerSpaceException("Topology not built for layer "+theLayer);
                Layer routes = null;
                try {
                        RouteLayerGenerator rlg = new RouteLayerGenerator(ps, l, orderFields, groupFields, xyFields);
                        routes = rlg.generateRouteLayer();
                } catch(Exception e) {
                        throw new PowerSpaceException(e.getMessage());
                }
                layers.put(newLayer, routes);
                System.out.println("Route Layer has "+routes.getNumberFeatures());
                return "Route Layer has "+routes.getNumberFeatures();
        }
        /**
        * Point in polygon in given layer?
        * @param theLayer The layer with polygons
        * @param tx The X coord
        * @param ty the Y coord
        * @return A Layer with target polygon and attribs
        */
        public static synchronized Object PowerKernel_GetPolygon(Integer leg, CustomerConnectionPanel ccp, String theLayer, String tx, String ty) throws Exception {
                Layer l = getLayer(theLayer);
                if( l == null ) throw new PowerSpaceException("Layer "+theLayer+" not loaded..");
                float x = new Float(tx).floatValue();
                float y = new Float(ty).floatValue();
                Layer newLayer = new Layer();
                for(int i = 0; i < l.getNumberFeatures(); i++) {
                        sPolygon sp = (sPolygon)l.getShape(i);
                        if( sp.isPointIn(x,y) ) {
                                newLayer = new Layer();
                                newLayer.createTable(l.getNumberColumns(), 1);
                                for(int j = 0; j < l.getNumberColumns(); j++) {
                                        newLayer.names[j] = l.names[j];
                                        newLayer.types[j] = l.types[j];
                                        newLayer.addElement(l.getElement(j, i), j, 0);
                                        newLayer.addShape(l.getShape(i));
                                        System.out.println(l.names[j]+" - "+l.getElement(j, i));
                                }
//                                return newLayer;
                        }
                }
                return newLayer;
        }
        /**
        * Coordinate conversion from StatePlane Coordsys 1983
        * to WGS 84.  For an explanation, see pages 44,45 & 104 of NOAA Manual
        * NOS NGS 5 <dd>
        * State Plane Corrdinate System of 1983<dd>
        * James E. Stem <dd>
        * Rockville, MD January 1989<dd>
        * In its original for, longitude (lambda) is given as positive, here
        * we give it as negative. Constants are from page 104 for Oregon North Zone.
        * Stateplane values are in international feet, not metres.
        * @param tx The Stateplane X
        * @param ty The Stateplane Y
        * @return A double[] array of long, lat WGS84
        */
        public static Object PowerKernel_ProjectToWGS84(Integer leg, CustomerConnectionPanel ccp, String tx, String ty) throws Exception {
                double Bo = 45.1687259619;
                double G1 = 8.999007999E-06;
                double G2 = -7.12020E-15;
                double G3 = -3.68630E-20;
                double G4 = -1.3188E-27;
                double Ro = 6350713.9300;
                double No = 166910.7663;
                double Eo = 2500000;
                double sineBo = .709186016884;
                double Lo = 120.5; // central meridian
                double PI = 3.14159265359;
                double dLat, u, Rp, Np, Ep, gamma;
                double E = new Double(tx).doubleValue();
                double N = new Double(ty).doubleValue();
                double[] returnCoords = new double[2];
                N *= .3048; // convert to metres from feets
                E *= .3048; // meters to ft.
                Np = N - No;
                Ep = E - Eo;
                Rp = Ro - Np;
                gamma = Math.atan(Ep/Rp);
                // positive lon
                // returnCoords[0] = Lo - (gamma * 180) / (PI * sineBo);
                // negative
                returnCoords[0] = ((gamma * 180) / (PI * sineBo)) - Lo;
                u = Np - Ep * Math.tan(gamma/2);
                dLat = u * (G1 + u * (G2 + u * (G3 + u * G4)));
                returnCoords[1] = Bo + dLat;
//                return String.valueOf(returnCoords[0])+","+String.valueOf(returnCoords[1]);
                return returnCoords;
        }
}              
