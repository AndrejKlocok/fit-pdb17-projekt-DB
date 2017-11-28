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

package cz.vutbr.fit.pdb.gui.view;

import cz.vutbr.fit.pdb.core.model.PropertyPrice;

import javax.swing.*;

/**
 * Component to display and format property price with currency, but still holding original value.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see PropertyPrice
 */
public class PropertyPriceTextField extends JTextField {

    private static final String CURRENCY = "Kč";

    private PropertyPrice propertyPrice;

    /**
     * Default constructor
     */
    public PropertyPriceTextField() {
        setBorder(null);
        setEditable(false);
    }

    /**
     * Creates instance right from the PropertyPrice object
     *
     * @param propertyPrice property price object
     */
    public PropertyPriceTextField(PropertyPrice propertyPrice) {
        this();
        setPropertyPrice(propertyPrice);
    }

    /**
     * Getter for property price attribute
     *
     * @return property price attribute
     */
    public PropertyPrice getPropertyPrice() {
        return propertyPrice;
    }

    /**
     * Setter for property price attribute
     *
     * @param propertyPrice property price object
     */
    public void setPropertyPrice(PropertyPrice propertyPrice) {
        this.propertyPrice = propertyPrice;
        setText(propertyPrice != null ? propertyPrice.getPrice() + " " + CURRENCY : "no price");
    }

    /**
     * Get original unformatted value of property price
     *
     * @return original unformatted price
     */
    public double getPrice() {
        return propertyPrice.getPrice();
    }
}
