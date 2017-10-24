/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import oracle.spatial.geometry.JGeometry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Property {

    public enum Type {
        PREFAB,
        APARTMENT,
        HOUSE,
        TERRACE_HOUSE,
        LAND
    }

    private String id;

    private Type type;

    private String name;

    private String description;

    private Double priceCurrent;

    private HashMap<String, Double> priceHistory;

    private JGeometry geometry;

    // TODO

    public Property() {
        id = null;
        type = null;
        name = "";
        description = "";
        priceCurrent = 0d;
        priceHistory = new HashMap<>();
        geometry = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPriceCurrent() {
        return priceCurrent;
    }

    public void setPriceCurrent(Double priceCurrent) {
        this.priceCurrent = priceCurrent;
    }

    public HashMap<String, Double> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(HashMap<String, Double> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public JGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    public List<Property> getSimilar() {
        // sample data

        // Polygon
        Property property1 = new Property();
        property1.setId("1");
        property1.setName("Polygon");
        property1.setType(Property.Type.APARTMENT);
        property1.setPriceCurrent(1000000d);
        property1.setGeometry(JGeometry.createLinearPolygon(new double[2], 2, 2));

        // Rectangle
        Property property2 = new Property();
        property2.setId("2");
        property2.setName("Rectangle");
        property2.setType(Property.Type.HOUSE);
        property2.setPriceCurrent(500000d);
        property2.setGeometry(new JGeometry(2, 2, 2, 2, 2));

        // Circle
        Property property3 = new Property();
        property3.setId("3");
        property3.setName("Circle");
        property3.setType(Property.Type.APARTMENT);
        property3.setPriceCurrent(42000000d);
        property3.setGeometry(JGeometry.createCircle(2, 2, 2, 2));

        // Property list
        List<Property> propertyList = new LinkedList<>();
        propertyList.add(property1);
        propertyList.add(property2);
        propertyList.add(property3);

        return propertyList;
    }
}
