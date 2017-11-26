/*
 * Copyright (C) 2017 VUT FIT PDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.vutbr.fit.pdb.gui.view;

import com.lynden.gmapsfx.javascript.object.GMapPoint;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;

import java.awt.*;

/**
 * Helper class to converting latitude and longitude on map to point of map component and vice versa
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class MapUtil {

    /**
     * Convert latitude and longitude to point of map component.
     * see https://stackoverflow.com/questions/25219346/how-to-convert-from-x-y-screen-coordinates-to-latlng-google-maps
     *
     * @param latLng latitude and longitude of map
     * @param map    map of which context will be converting
     * @return converted latitude and longitude to point of map component
     */
    public static Point latLng2Point(LatLong latLng, GoogleMap map) {
        GMapPoint topRight = map.getProjection().fromLatLngToPoint(map.getBounds().getNorthEast());
        GMapPoint bottomLeft = map.getProjection().fromLatLngToPoint(map.getBounds().getSouthWest());
        double scale = Math.pow(2, map.getZoom());
        GMapPoint worldPoint = map.getProjection().fromLatLngToPoint(latLng);
        return new Point((int) ((worldPoint.getX() - bottomLeft.getX()) * scale), (int) ((worldPoint.getY() - topRight.getY()) * scale));
    }

    /**
     * Convert point of map component to latitude and longitude on map.
     * see https://stackoverflow.com/questions/25219346/how-to-convert-from-x-y-screen-coordinates-to-latlng-google-maps
     *
     * @param point point of map component
     * @param map   map of which context will be converting
     * @return converted point to latitude and longitude
     */
    public static LatLong point2LatLng(Point point, GoogleMap map) {
        GMapPoint topRight = map.getProjection().fromLatLngToPoint(map.getBounds().getNorthEast());
        GMapPoint bottomLeft = map.getProjection().fromLatLngToPoint(map.getBounds().getSouthWest());
        double scale = Math.pow(2, map.getZoom());
        Point worldPoint = new Point((int) (point.x / scale + bottomLeft.getX()), (int) (point.y / scale + topRight.getY()));
        return new LatLong(point.getX(), point.getY());//map.getProjection().fromPointToLatLng(worldPoint);
    }
}
