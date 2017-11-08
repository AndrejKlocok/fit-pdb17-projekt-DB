/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.view;

import com.lynden.gmapsfx.javascript.object.GMapPoint;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;

import java.awt.*;

public class MapUtil {

    public static Point latLng2Point(LatLong latLng, GoogleMap map) {
        // https://stackoverflow.com/questions/25219346/how-to-convert-from-x-y-screen-coordinates-to-latlng-google-maps
        GMapPoint topRight = map.getProjection().fromLatLngToPoint(map.getBounds().getNorthEast());
        GMapPoint bottomLeft = map.getProjection().fromLatLngToPoint(map.getBounds().getSouthWest());
        double scale = Math.pow(2, map.getZoom());
        GMapPoint worldPoint = map.getProjection().fromLatLngToPoint(latLng);
        return new Point((int) ((worldPoint.getX() - bottomLeft.getX()) * scale), (int) ((worldPoint.getY() - topRight.getY()) * scale));
    }

    public static LatLong point2LatLng(Point point, GoogleMap map) {
        // https://stackoverflow.com/questions/25219346/how-to-convert-from-x-y-screen-coordinates-to-latlng-google-maps

        GMapPoint topRight = map.getProjection().fromLatLngToPoint(map.getBounds().getNorthEast());
        GMapPoint bottomLeft = map.getProjection().fromLatLngToPoint(map.getBounds().getSouthWest());
        double scale = Math.pow(2, map.getZoom());
        Point worldPoint = new Point((int) (point.x / scale + bottomLeft.getX()), (int) (point.y / scale + topRight.getY()));
        return new LatLong(point.getX(), point.getY());//map.getProjection().fromPointToLatLng(worldPoint);
    }
}
