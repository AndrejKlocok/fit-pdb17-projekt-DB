/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

/**
 * Model of database table Ground_Plan.
 */
public class GroundPlan {

    private int idGroundPlan;

    private int idProperty;

    private byte[] image;

    /**
     * Method returns id.
     * @return Integer value, which represents id of ground plan
     */
    public int getIdGroundPlan() {
        return idGroundPlan;
    }

    /**
     * Method sets id.
     * @param idGroundPlan Integer value, which represents id of ground plan
     */
    public void setIdGroundPlan(int idGroundPlan) {
        this.idGroundPlan = idGroundPlan;
    }

    /**
     * Method gets id of property.
     * @return Integer value, which represents id of property
     */
    public int getIdProperty() {
        return idProperty;
    }

    /**
     * Method sets id of property.
     * @param idProperty Integer value, which represents id of property
     */
    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    /**
     * Method return image of ground plan.
     * @return Byre array
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Method sets image of ground plan.
     * @param image Byre array
     */
    public void setImage(byte[] image) {
        this.image = image;
    }
}
