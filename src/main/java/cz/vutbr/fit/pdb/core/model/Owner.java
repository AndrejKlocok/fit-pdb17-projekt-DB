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

public class Owner {

    //protected int idOwner;

    //protected int idProperty;
    protected Person person;

    protected Property property;

    protected Date validFrom;

    protected Date validTo;

   // protected HashMap<String, Property> propertyHistory;


    public Owner() {
        /*idOwner = 0;
        idProperty = 0;
        */
        person = new Person();
        property = new Property();
    }

    public Owner(Person person, Property property, Date validFrom, Date validTo) {
        this.person = person;
        this.property = property;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
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
