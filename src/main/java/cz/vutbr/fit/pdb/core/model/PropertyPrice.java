/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;


import java.util.Date;

public class PropertyPrice {

    private int idPropertyPrice;

    private Property property;

    private double price;

    private Date validFrom;

    private Date validTo;


    public PropertyPrice() {
        idPropertyPrice = 0;
        property = new Property();
        price = 0;
        validFrom = null;
        validTo = null;
    }

    public PropertyPrice(int idPrice, Property property, double price, Date validFrom, Date validTo) {
        this.idPropertyPrice = idPrice;
        this.property = property;
        this.price = price;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public int getIdPropertyPrice() {
        return idPropertyPrice;
    }

    public void setIdPropertyPrice(int id) {
        this.idPropertyPrice = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public boolean isValid() {
        Date today = new Date();
        return this.validTo.after(today);
    }
}
