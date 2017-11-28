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

package cz.vutbr.fit.pdb.gui.adapters;

import com.lynden.gmapsfx.javascript.object.LatLongBounds;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import cz.vutbr.fit.pdb.core.App;

/**
 * Get coordinates of geometries edited in the map. Those coordinates will be saved to DB.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class ShapePointsAdapter {

    private int length;


    /**
     * Set attribute length
     *
     * @param length length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Returns new coordinates for all objects edited on the map, except rectangle
     *
     * @param shape MVC array represents shape of com.lynden.gmapsfx.javascript.object for all geometries except rectangle
     * @return new coordinates for geometries, those will be saved to DB
     */
    public double[] getNewCoordinates(MVCArray shape) {
        this.setLength(shape.getLength());
        double newShapeCoordinates[] = new double[length * 2];
        int j = 0;

        for (int i = 0; i < length; i++) {
            String coordinatesString = shape.getArray().getSlot(i).toString();
            if (App.isDebug()) {
                System.out.println("save: " + coordinatesString);
            }

            int lngStart = coordinatesString.indexOf(' ') + 1;
            int lngEnd = coordinatesString.indexOf(')');
            newShapeCoordinates[j] = Double.parseDouble(coordinatesString.substring(lngStart, lngEnd));
            j++;

            int latStart = coordinatesString.indexOf('(') + 1;
            int latEnd = coordinatesString.indexOf(',');
            newShapeCoordinates[j] = Double.parseDouble(coordinatesString.substring(latStart, latEnd));
            j++;
        }
        return newShapeCoordinates;
    }

    /**
     * Returns new coordinates of rectangle edited on the map
     *
     * @param bounds bounds of new rectangle geometry on the map
     * @return new rectangle coordinates, those will be saved to DB
     */
    public double[] getNewCoordinates(LatLongBounds bounds) {
        if (App.isDebug()) {
            System.out.println(bounds.toString());
        }
        double swLat = bounds.getSouthWest().getLatitude();
        double swLng = bounds.getSouthWest().getLongitude();
        double neLat = bounds.getNorthEast().getLatitude();
        double neLng = bounds.getNorthEast().getLongitude();

        return new double[]{swLng, swLat, neLng, neLat};
    }
}
