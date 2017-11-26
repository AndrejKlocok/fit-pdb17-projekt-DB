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
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import com.lynden.gmapsfx.shapes.Polygon;
import com.lynden.gmapsfx.shapes.PolygonOptions;
import oracle.spatial.geometry.JGeometry;

import java.util.ArrayList;

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
    public static MapShape jGeometry2MapShape(JGeometry geometry) {
        // TODO

        /*if (geometry.isCircle()) {
            /*LatLongBounds rb = new LatLongBounds(new LatLong(geometry.getOrdinatesArray()[1], geometry.getOrdinatesArray()[0]),
                    new LatLong(geometry.getOrdinatesArray()[3], geometry.getOrdinatesArray()[2]));
            RectangleOptions rectangleOptions = new RectangleOptions()
                    .bounds(rb)
                    .strokeColor("black")
                    .strokeWeight(2)
                    .fillColor("gray");
            return new Rectangle(rectangleOptions);*/

        if (geometry.isCircle()) {
            LatLong point = new LatLong(49.191882, 16.606972);
            CircleOptions circleOptions = new CircleOptions()
                    .center(point)
                    .radius(20)
                    .strokeColor("black")
                    .strokeWeight(2)
                    .fillColor("gray")
                    .fillOpacity(0.3);

            return new Circle(circleOptions);

        } else {
            java.util.List<LatLong> col = new ArrayList<>();

            double coords[] = geometry.getOrdinatesArray();
            double x = 0;
            double y = 0;
            for (int i = 0; i < coords.length; i++) {

                if (i % 2 == 0) {
                    y = coords[i];
                    //System.out.println("y " + y);
                } else {
                    x = coords[i];
                    //System.out.println("x " + x);
                    //System.out.println("col add: " + x + " " +y);
                    col.add(new LatLong(x, y));
                }
            }
            /*col.add(new LatLong(geometry.getOrdinatesArray()[1], geometry.getOrdinatesArray()[0])); //TR
            col.add(new LatLong(geometry.getOrdinatesArray()[3], geometry.getOrdinatesArray()[2])); //TL
            col.add(new LatLong(geometry.getOrdinatesArray()[5], geometry.getOrdinatesArray()[4])); //BL
            col.add(new LatLong(geometry.getOrdinatesArray()[7], geometry.getOrdinatesArray()[6])); //BR*/

            MVCArray mvc = new MVCArray(col.toArray());

            Polygon polygon = new Polygon(new PolygonOptions()
                    .paths(mvc)
                    .strokeColor("black")
                    .strokeWeight(2)
                    .editable(false)
                    .fillColor("gray")
                    .fillOpacity(0.5));

            return polygon;
        }
    }
}
