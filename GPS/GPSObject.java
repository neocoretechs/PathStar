import java.io.*;
public class GPSObject implements Serializable {
        static final long serialVersionUID = 7408785650962668159L;
        private String id;
        private double[] coords;
        private long time;
        public GPSObject(String tid, double tlat, double tlon, long ttime) {
                id = tid;
                coords = new double[2];
                coords[0] = tlat;
                coords[1] = tlon;
                time = ttime;
        }
        public String getId() { return id; }
        public synchronized double[] getCoords() { return coords; }
        public synchronized long getTime() { return time; }
        public synchronized void setCoords(double tlat, double tlon) {
                        coords[0] = tlat;
                        coords[1] = tlon;
        }
        public synchronized void setTime(long ttime) {
                time = ttime;
        }
        // Domain storage pimary key
        public int compareTo(GPSObject gpso) {
                int c1 = id.compareTo(gpso.getId());
                if( c1 != 0 ) return c1;
                if( time > gpso.getTime() ) return 1;
                if( time < gpso.getTime() ) return -1;
                return 0;
        }
}
