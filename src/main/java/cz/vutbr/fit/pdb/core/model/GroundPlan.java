/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import oracle.ord.im.OrdImage;

public class GroundPlan {

    private int idGroundPlan;

    private int idProperty;

    private OrdImage ordImage;


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

    public OrdImage getOrdImage() {
        return ordImage;
    }

    public void setOrdImage(OrdImage ordImage) {
        this.ordImage = ordImage;
    }
}
