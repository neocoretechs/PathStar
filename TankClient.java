package com.neocoretechs.pathstar;
import com.neocoretechs.powerspaces.*;
import java.io.*;
import java.util.*;
/**
* Connects to local TopoTank on port 8080 and invokes method
* First arg is method
* rest are comma-delim args
* @author Groff Copyright (C) NeoCoreTechs 2000
*/
public class TankClient
{
        public static void main(String[] argv) {
                try  {
                       PowerSpace PS = new PowerSpace("127.0.0.1",8202);
                       PKRemote pkr = PS.getRemote("com.neocoretechs.pathstar.TopoTank");
                       Object o = null;
                       StringTokenizer st = new StringTokenizer(argv[1],",");
                       String[] toke = new String[st.countTokens()];
                       int i = 0;
                       for(; st.hasMoreTokens(); toke[i++] = st.nextToken());
                       for(int j = 0 ; j < toke.length ; j++) System.out.println(toke[j]);
                       switch(toke.length) {
                        case 0:
                                o = pkr.invoke(argv[0]);
                                break;
                        case 1:
                                o = pkr.invoke(argv[0],toke[0]);
                                break;
                        case 2:
                                o = pkr.invoke(argv[0],toke[0],toke[1]);
                                break;
                        case 3:
                                o = pkr.invoke(argv[0],toke[0],toke[1],toke[2]);
                                break;
                        case 4:
                                o = pkr.invoke(argv[0],toke[0],toke[1],toke[2],toke[3]);
                                break;
                        case 5:
                                o = pkr.invoke(argv[0],toke[0],toke[1],toke[2],toke[3],toke[4]);
                                break;
                        case 6:
                                o = pkr.invoke(argv[0],toke[0],toke[1],toke[2],toke[3],toke[4],toke[5]);
                                break;
                        case 7:
                                o = pkr.invoke(argv[0],toke[0],toke[1],toke[2],toke[3],toke[4],toke[5],toke[6]);
                                break;
                        default:
                                System.out.println("Wrong number of args");
                                System.exit(1);
                       }
                       System.out.println(o);
                       PS.Unplug();
                } catch(Exception e) { System.out.println(e.getMessage()); e.printStackTrace(); }
                System.exit(0);
        }
}
