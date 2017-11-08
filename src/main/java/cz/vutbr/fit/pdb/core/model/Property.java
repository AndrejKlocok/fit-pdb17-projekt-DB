/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import oracle.spatial.geometry.JGeometry;

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

    private int idProperty;

    private Type type;

    private String name;

    private String description;

    private List<PropertyPrice> priceHistory;

    private JGeometry geometry;


    public Property() {
        idProperty = 0;
        type = null;
        name = "";
        description = "";
        priceHistory = new LinkedList<>();
        geometry = null;
    }

    public Property(int id, Type type, String name, String description, JGeometry geometry) {
        this.idProperty = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.geometry = geometry;
    }

    public Property(int id, Type type, String name, String description) {
        this.idProperty = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public int getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
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

    public List<PropertyPrice> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PropertyPrice> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public JGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    public Double getPriceCurrent() {
        // TODO return last price from price history
        return 4200d;
    }
}
