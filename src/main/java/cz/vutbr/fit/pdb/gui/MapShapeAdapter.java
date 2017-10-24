/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.LatLongBounds;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.shapes.*;
import oracle.spatial.geometry.JGeometry;

import java.util.ArrayList;

public class MapShapeAdapter {

    public static MapShape jGeometry2MapShape(JGeometry geometry) {
        // TODO

        if (geometry.isRectangle()) {
            LatLongBounds rb = new LatLongBounds(new LatLong(49.191438, 16.607195), new LatLong(49.191561, 16.607505));
            RectangleOptions rectangleOptions = new RectangleOptions()
                    .bounds(rb)
                    .strokeColor("black")
                    .strokeWeight(2)
                    .fillColor("gray");
            return new Rectangle(rectangleOptions);

        } else if (geometry.isCircle()) {
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

            col.add(new LatLong(49.191381, 16.606092));
            col.add(new LatLong(49.191434, 16.606301));
            col.add(new LatLong(49.191717, 16.606153));
            col.add(new LatLong(49.191664, 16.605969));

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
