/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import oracle.spatial.geometry.JGeometry;

import java.util.Date;
import java.util.Iterator;
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

    private List<GroundPlan> groundPlans;

    private List<PropertyPrice> priceHistory;

    private List<Owner> ownerHistory;

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

    public List<GroundPlan> getGroundPlans() {
        return groundPlans;
    }

    public void setGroundPlans(List<GroundPlan> groundPlans) {
        this.groundPlans = groundPlans;
    }

    public List<PropertyPrice> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PropertyPrice> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public List<Owner> getOwnerHistory() {
        return ownerHistory;
    }

    public void setOwnerHistory(List<Owner> ownerHistory) {
        this.ownerHistory = ownerHistory;
    }

    public JGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    public PropertyPrice getPriceCurrent() {
        PropertyPrice propertyPrice = priceHistory.size() > 0 ? priceHistory.get(priceHistory.size() - 1) : null;
        if(propertyPrice == null)
            return null;

        for (PropertyPrice pp : priceHistory) {
            if (pp.getValidTo().after(propertyPrice.getValidTo())) {
                propertyPrice = pp;
            }
        }
        return propertyPrice;
    }

    public Owner getOwnerCurrent(){
        Owner owner = this.getOwnerLast();
        Date currentDate = new Date();

        if(owner == null)
            return null;

        //If last owner of property is valid in current time
        if(currentDate.after(owner.getValidFrom()) && currentDate.before(owner.getValidTo()))
            return owner;

        return null;

    }

    public Owner getOwnerLast() {
        Owner owner = ownerHistory.size() > 0 ? ownerHistory.get(ownerHistory.size() - 1) : null;
        if(owner == null)
            return null;

        for (Owner owner_tmp : ownerHistory) {
            if (owner_tmp.getValidTo().after(owner.getValidTo())) {
                owner = owner_tmp;
            }
        }
        return owner;

    }
}
