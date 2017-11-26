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

/**
 * Model of database table Property
 */
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

    /**
     * Constructor of @see Property.
     */
    public Property() {
        idProperty = 0;
        type = null;
        name = "";
        description = "";
        priceHistory = new LinkedList<>();
        geometry = null;
    }

    /**
     * Constructor od @see property
     * @param id Integer value, which represents id of property.
     * @param type enum value, which represents type of property.
     * @param name String value, which represents full name of property.
     * @param description String value, which represents description of property.
     * @param geometry @see JGeometry geometry for spatial operations.
     */
    public Property(int id, Type type, String name, String description, JGeometry geometry) {
        this.idProperty = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.geometry = geometry;
    }

    /**
     * Constructor od @see property
     * @param id Integer value, which represents id of property.
     * @param type enum value, which represents type of property.
     * @param name String value, which represents full name of property.
     * @param description String value, which represents description of property.
     */
    public Property(int id, Type type, String name, String description) {
        this.idProperty = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * Method returns Id of property
     * @return Integer value, which represents id of property.
     */
    public int getIdProperty() {
        return idProperty;
    }

    /**
     * Method sets Id of property
     * @param idProperty Integer value, which represents id of property.
     */
    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    /**
     * Method returns type of Property
     * @return enum value, which represents type of property.
     */
    public Type getType() {
        return type;
    }

    /**
     * Method sets type of Property
     * @param type enum value, which represents type of property.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Method returns name of Property
     * @return String value, which represents full name of property.
     */
    public String getName() {
        return name;
    }

    /**
     * Method sets name of Property
     * @param name String value, which represents full name of property.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method returns description of Property
     * @return String value, which represents description of property.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method sets description of Property
     * @param description String value, which represents description of property.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method returns list of ground plans of Property
     * @return List of @see GroundPlan objects.
     */
    public List<GroundPlan> getGroundPlans() {
        return groundPlans;
    }

    /**
     * Method sets ground plans of Property
     * @param groundPlans List of @see GroundPlan objects.
     */
    public void setGroundPlans(List<GroundPlan> groundPlans) {
        this.groundPlans = groundPlans;
    }

    /**
     * Method returns prices of Property
     * @return List of @see PropertyPrice objects.
     */
    public List<PropertyPrice> getPriceHistory() {
        return priceHistory;
    }

    /**
     * Method sets prices of Property
     * @param priceHistory List of @see PropertyPrice objects.
     */
    public void setPriceHistory(List<PropertyPrice> priceHistory) {
        this.priceHistory = priceHistory;
    }

    /**
     * Method returns owners of Property
     * @return List of @see Owner objects.
     */
    public List<Owner> getOwnerHistory() {
        return ownerHistory;
    }

    /**
     * Method sets owners of property.
     * @param ownerHistory List of @see Owner objects.
     */
    public void setOwnerHistory(List<Owner> ownerHistory) {
        this.ownerHistory = ownerHistory;
    }

    /**
     * Method returns geometry of property.
     * @return @see JGeometry geometry for spatial operations.
     */
    public JGeometry getGeometry() {
        return geometry;
    }

    /**
     * Method sets geometry of property.
     * @param geometry @see JGeometry geometry for spatial operations.
     */
    public void setGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Method returns price, which is valid in current date or null.
     * @return @see PropertyPrice object or null
     */
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

    /**
     * Method returns owner, who owns property in current date or null.
     * @return @see Owner object or null
     */
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

    /**
     * Method returns last owner, who owned property in current date or null.
     * @return @see Owner object or null
     */
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
