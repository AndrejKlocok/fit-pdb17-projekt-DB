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

    private int idProperty;

    private double price;

    private Date validFrom;

    private Date validTo;


    public PropertyPrice() {
        idPropertyPrice = 0;
        idProperty = 0;
        price = 0;
    }

    public PropertyPrice(int idPrice, int idProperty, double price, Date validFrom, Date validTo) {
        this.idPropertyPrice = idPrice;
        this.idProperty = idProperty;
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

    public int getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
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
