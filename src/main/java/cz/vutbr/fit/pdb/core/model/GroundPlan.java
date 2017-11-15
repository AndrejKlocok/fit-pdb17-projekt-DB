/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

public class GroundPlan {

    private int idGroundPlan;

    private int idProperty;

    private byte[] image;


    public int getIdGroundPlan() {
        return idGroundPlan;
    }

    public void setIdGroundPlan(int idGroundPlan) {
        this.idGroundPlan = idGroundPlan;
    }

    public int getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
