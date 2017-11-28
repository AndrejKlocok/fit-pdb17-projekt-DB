/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
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

public class MapShapeAdapter {

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
         }else if (geometry.isRectangle()) {
             System.out.println("RECTANGLE SHAPE");
             java.util.List<LatLong> col = LatLngToLngLat(geometry.getOrdinatesArray());

             LatLongBounds rb = new LatLongBounds(col.get(0), col.get(1));
             RectangleOptions rectangleOptions = new RectangleOptions()
                     .bounds(rb)
                     .strokeColor("green")
                     .strokeWeight(2)
                     .fillColor("green");
             return new Rectangle(rectangleOptions);
         } else if(geometry.getType() == 2) {
             System.out.println("POLYLINE SHAPE");
             java.util.List<LatLong> col = LatLngToLngLat(geometry.getOrdinatesArray());
             MVCArray mvc = new MVCArray(col.toArray());

             Polyline polyline = new Polyline(new PolylineOptions()
                     .path(mvc)
                     .strokeColor("blue")
                     .editable(false)
                     .strokeWeight(4));

             return polyline;
         } else {
            System.out.println("POLYGON SHAPE");
            java.util.List<LatLong> col = LatLngToLngLat(geometry.getOrdinatesArray());
            MVCArray mvc = new MVCArray(col.toArray());


            PolygonOptions polygonOptions = new PolygonOptions()
                    .paths(mvc)
                    .strokeColor("black")
                    .strokeWeight(2)
                    .editable(false)
                    .fillColor("gray")
                    .fillOpacity(0.5);

            if(type == Property.Type.LAND) {
                polygonOptions.fillColor("white").strokeColor("white");
            }

            Polygon polygon = new Polygon(polygonOptions);
            return polygon;
        }
    }

    public static List<LatLong> LatLngToLngLat(double[] coordinates) {
        java.util.List<LatLong> col = new ArrayList<>();

        double x = 0;
        double y = 0;
        for (int i = 0; i < coordinates.length; i++) {
            if(i % 2 == 0) {
                y = coordinates[i];
                //System.out.println("y " + y);
            } else {
                x = coordinates[i];
                //System.out.println("x " + x);
                //System.out.println("col add: " + x + " " +y);
                col.add(new LatLong(x, y));
            }
        }

        return col;
    }
}
