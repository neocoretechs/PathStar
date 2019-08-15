package com.neocoretechs.pathstar;
import com.neocoretechs.pathstar.sde.*;
import com.neocoretechs.pathstar.shapefile.*;
import com.esri.sde.client.*;
import java.io.*;
import java.net.*;
public class Main {
    /**
    * 0 - server
    * 1 - instance
    * 2 - user
    * 3 - pass
    * 4 - table
    */
    public static void main(String[] argv) throws Exception {
    try {
        // SDEConnection sdec = new SDEConnection(argv[0], argv[1], argv[2], argv[3]);
        // InfoStream lis = sdec.createStream("foo", argv[4]);
        // new SDETableReader(argv[4], lis.getStream(), l);
        Connection sdec = new Connection(argv[0], argv[1], argv[2], argv[3]);
        Layer l = new Layer();
        new SDETableReader(argv[4], new Stream(sdec), l);
        String[] orderFields = new String[4];
        orderFields[0] = "ROUTE_";
        orderFields[1] = "DIR";
        orderFields[2] = "SEGNUM";
        orderFields[3] = "ZFLM_";
        String[] groupFields = new String[3];
        groupFields[0] = "ROUTE_";
        groupFields[1] = "DIR";
        groupFields[2] = "SEGNUM";
        String[] xyFields = new String[2];
        xyFields[0] = "X_COORD";
        xyFields[1] = "Y_COORD";
        // old way
//        ShapeStreamReader ssr = new ShapeStreamReader(new FileInputStream("str1099.shp"));
//        Layer topoLayer = ssr.return_layer();
        // now connect to topo tank
        Socket tts = new Socket("kahuna",8880);
        OutputStream os = tts.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject("str1099");
        oos.flush();        
        System.out.println("Reading topo layer...");
        InputStream is = tts.getInputStream();
        ObjectInputStream iis = new ObjectInputStream(is);
        Layer topoLayer = (Layer)(iis.readObject());
        iis.close();
        tts.close();
        //
        System.out.println("Done reading topo layer");
        // create topo and pathstar
        pathStar ps = new pathStar();
        ps.buildTopology(topoLayer);
        RouteLayerGenerator rlg = new RouteLayerGenerator(ps, l, orderFields, groupFields, xyFields);
        Layer routes = rlg.generateRouteLayer();
        System.out.println("Route Layer has "+routes.getNumberFeatures());
        // now write new table
        new SDETableWriter(argv[5], sdec, routes);
     } catch(SdeExn sdex) {
                System.out.println(SDEerrs.getMessage(sdex));
                sdex.printStackTrace();
     }
     }
}
