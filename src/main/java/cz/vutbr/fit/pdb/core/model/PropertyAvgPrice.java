/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
package cz.vutbr.fit.pdb.core.model;

public class PropertyAvgPrice {

    protected Property property;

    protected  Integer avgPrice;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Integer getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Integer avgPrice) {
        this.avgPrice = avgPrice;
    }

    public PropertyAvgPrice(){
        this.property = new Property();
        this.avgPrice = 0;
    }
}
