/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import java.util.HashMap;
import java.util.Date;

public class Owner extends Person {

    protected int idOwner;

    protected int idProperty;

    protected Date validFrom;

    protected Date validTo;

    protected HashMap<String, Property> propertyHistory;


    public Owner() {
        idOwner = 0;
        idProperty = 0;
        propertyHistory = new HashMap<>();
    }

    public Owner(int idOwner, int idProperty, Date validFrom, Date validTo) {
        this.idOwner = idOwner;
        this.idProperty = idProperty;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public int getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public HashMap<String, Property> getPropertyHistory() {
        return propertyHistory;
    }

    public void setPropertyHistory(HashMap<String, Property> propertyHistory) {
        this.propertyHistory = propertyHistory;
    }

    public Property getPropertyCurrent() {
        // TODO
        return new Property();
    }

    public Double getPropertyCurrentCount() {
        // TODO
        return 42d;
    }

    public Double getPropertyCurrentLandAreaSum() {
        // TODO
        return 42d;
    }
}
