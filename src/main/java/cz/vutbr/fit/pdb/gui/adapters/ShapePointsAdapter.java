package cz.vutbr.fit.pdb.gui.adapters;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.LatLongBounds;
import com.lynden.gmapsfx.javascript.object.MVCArray;

/**
 *Get coordinates of geometries edited in the map. Those coordinates will be saved to DB.
 */
public class ShapePointsAdapter {

    private int length;
    private MVCArray shape;
    private LatLongBounds bounds;

    public ShapePointsAdapter(MVCArray shape) {
        this.shape = shape;
        this.length = shape.getLength();
    }

    public ShapePointsAdapter(LatLongBounds bounds) {
        this.bounds = bounds;
    }

    public ShapePointsAdapter() {

    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     *Returns new coordinates for all objects edited on the map, except rectangle
     *
     * @param shape MVC array represents shape of com.lynden.gmapsfx.javascript.object for all geometries except rectangle
     * @return      new coordinates for geometries, those will be saved to DB
     */
    public double[] getNewCoordinates(MVCArray shape) {
        this.setLength(shape.getLength());
        double newShapeCoordinates [] = new double[length*2];
        int j  = 0;

        for (int i = 0; i < length; i++)
        {
            String coordinatesString = shape.getArray().getSlot(i).toString();
            System.out.println("save: " + coordinatesString);

            int lngStart = coordinatesString.indexOf(' ') +1;
            int lngEnd = coordinatesString.indexOf(')');
            newShapeCoordinates[j] = Double.parseDouble(coordinatesString.substring(lngStart, lngEnd));
            j++;

            int latStart  = coordinatesString.indexOf('(') +1;
            int latEnd = coordinatesString.indexOf(',');
            newShapeCoordinates[j] = Double.parseDouble(coordinatesString.substring(latStart,latEnd));
            j++;
        }
        return newShapeCoordinates;
    }

    /**
     *Returns new coordinates of rectangle edited on the map
     *
     * @param bounds bounds of new rectangle geometry on the map
     * @return       new rectangle coordinates, those will be saved to DB
     */
    public double[] getNewCoordinates(LatLongBounds bounds) {
        System.out.println(bounds.toString());
        double swLat = bounds.getSouthWest().getLatitude();
        double swLng = bounds.getSouthWest().getLongitude();
        double neLat = bounds.getNorthEast().getLatitude();
        double neLng = bounds.getNorthEast().getLongitude();

        double newShapeCoordinates[] = {swLng, swLat, neLng, neLat};
        return newShapeCoordinates;
    }
}
