/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;


import java.util.Date;

/**
 * Model of database table Property_price.
 */
public class PropertyPrice {

    private int idPropertyPrice;

    private Property property;

    private double price;

    private Date validFrom;

    private Date validTo;

    /**
     * Constructor of @see PropertyPrice.
     */
    public PropertyPrice() {
        idPropertyPrice = 0;
        property = new Property();
        price = 0;
        validFrom = null;
        validTo = null;
    }

    /**
     * Constructor of @see PropertyPrice
     * @param idPrice Integer value, which represents id of property price
     * @param property @see Property
     * @param price Double value, which represents price of property
     * @param validFrom @see Date, Date value from desired time interval
     * @param validTo @see Date, Date value to desired time interval
     */
    public PropertyPrice(int idPrice, Property property, double price, Date validFrom, Date validTo) {
        this.idPropertyPrice = idPrice;
        this.property = property;
        this.price = price;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    /**
     * Method returns id of property price.
     * @return Integer value, which represents id of property price
     */
    public int getIdPropertyPrice() {
        return idPropertyPrice;
    }

    /**
     * Method sets price of property price.
     * @param id Integer value, which represents id of property price
     */
    public void setIdPropertyPrice(int id) {
        this.idPropertyPrice = id;
    }

    /**
     * Method returns property of property price.
     * @return @see Property object of Property price
     */
    public Property getProperty() {
        return property;
    }

    /**
     * Method sets property of property price.
     * @param property @see Property object of Property price
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Method returns price of property price.
     * @return Double value, which represents price of property
     */
    public double getPrice() {
        return price;
    }

    /**
     * Method sets price of property price.
     * @param price Double value, which represents price of property
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Method returns date from in time interval of property price.
     * @return @see Date, Date value from desired time interval
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * Method sets date from in time interval of property price.
     * @param validFrom @see Date, Date value from desired time interval
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Method returns date to in time interval of property price.
     * @return @see Date, Date value to desired time interval
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Method sets date to in time interval of property price.
     * @param validTo
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * Method checks if the price is valid now
     * @return  boolean True is price is valid now otherwise False.
     */
    public boolean isValid() {
        Date today = new Date();
        return this.validTo.after(today);
    }
}
