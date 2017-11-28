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

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.LatLongBounds;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.shapes.*;
import cz.vutbr.fit.pdb.core.model.Property;
import oracle.spatial.geometry.JGeometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter converting from Oracle JGeometry to Google Map Shape
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see MapShape
 * @see JGeometry
 */
public class MapShapeAdapter {

    /**
     * Convert JGeometry to MapShape
     *
     * @param geometry source JGeometry
     * @return converted MapShape
     */
    public static MapShape jGeometry2MapShape(JGeometry geometry, Property.Type type) {
        // TODO
        if (geometry.isCircle()) {
            System.out.println("CIRCLE");
            /*
            LatLong point = new LatLong(x, y);
            CircleOptions circleOptions = new CircleOptions()
                    .center(point)
                    .radius(20)
                    .strokeColor("black")
                    .strokeWeight(2)
                    .fillColor("gray")
                    .fillOpacity(0.3);
            */
            return null;
        } else if (geometry.isRectangle()) {
            java.util.List<LatLong> col = LatLngToLngLat(geometry.getOrdinatesArray());

            LatLongBounds rb = new LatLongBounds(col.get(0), col.get(1));
            RectangleOptions rectangleOptions = new RectangleOptions()
                    .bounds(rb)
                    .strokeColor("green")
                    .strokeWeight(2)
                    .fillColor("green");

            return new Rectangle(rectangleOptions);

        } else if (geometry.getType() == 2) {
            java.util.List<LatLong> col = LatLngToLngLat(geometry.getOrdinatesArray());
            MVCArray mvc = new MVCArray(col.toArray());

            Polyline polyline = new Polyline(new PolylineOptions()
                    .path(mvc)
                    .strokeColor("blue")
                    .editable(false)
                    .strokeWeight(4));

            return polyline;
        } else {
            java.util.List<LatLong> col = LatLngToLngLat(geometry.getOrdinatesArray());
            MVCArray mvc = new MVCArray(col.toArray());

            PolygonOptions polygonOptions = new PolygonOptions()
                    .paths(mvc)
                    .strokeColor("black")
                    .strokeWeight(2)
                    .editable(false)
                    .fillColor("gray")
                    .fillOpacity(0.5);

            if (type == Property.Type.LAND) {
                polygonOptions.fillColor("white").strokeColor("white");
            }

            Polygon polygon = new Polygon(polygonOptions);
            return polygon;
        }
    }

    //TODO: javadoc
    public static List<LatLong> LatLngToLngLat(double[] coordinates) {
        java.util.List<LatLong> col = new ArrayList<>();

        double x = 0;
        double y = 0;
        for (int i = 0; i < coordinates.length; i++) {
            if (i % 2 == 0) {
                y = coordinates[i];
            } else {
                x = coordinates[i];
                col.add(new LatLong(x, y));
            }
        }

        return col;
    }
}
