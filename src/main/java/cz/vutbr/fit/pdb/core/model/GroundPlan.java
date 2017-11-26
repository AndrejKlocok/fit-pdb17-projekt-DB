/*
 * Copyright (C) 2017 VUT FIT PDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.vutbr.fit.pdb.core.model;

/**
 * Model of database table Ground_Plan.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class GroundPlan {

    private int idGroundPlan;

    private int idProperty;

    private byte[] image;

    /**
     * Method returns id.
     *
     * @return Integer value, which represents id of ground plan
     */
    public int getIdGroundPlan() {
        return idGroundPlan;
    }

    /**
     * Method sets id.
     *
     * @param idGroundPlan Integer value, which represents id of ground plan
     */
    public void setIdGroundPlan(int idGroundPlan) {
        this.idGroundPlan = idGroundPlan;
    }

    /**
     * Method gets id of property.
     *
     * @return Integer value, which represents id of property
     */
    public int getIdProperty() {
        return idProperty;
    }

    /**
     * Method sets id of property.
     *
     * @param idProperty Integer value, which represents id of property
     */
    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    /**
     * Method return image of ground plan.
     *
     * @return Byre array
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Method sets image of ground plan.
     *
     * @param image Byre array
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Convert ground plan to string
     *
     * @return ground plan string representation
     */
    public String toString() {
        return "ground plan " + idGroundPlan + " of property " + idProperty;
    }
}
