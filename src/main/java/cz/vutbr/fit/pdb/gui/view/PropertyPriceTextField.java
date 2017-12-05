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
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Component to display and format property price with currency, but still holding original value.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see PropertyPrice
 */
public class PropertyPriceTextField extends JFormattedTextField {

    private static final String CURRENCY = "Kč";


    /**
     * Default constructor
     */
    public PropertyPriceTextField() {
        setBorder(null);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("###,### " + CURRENCY);
        format.setGroupingSize(3);
        format.setDecimalFormatSymbols(otherSymbols);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setCommitsOnValidEdit(true);
        formatter.setAllowsInvalid(false);
        setFormatterFactory(new DefaultFormatterFactory(formatter));
        setValue(0);
    }

    /**
     * Creates instance right from the PropertyPrice object
     *
     * @param propertyPrice property price object
     */
    public PropertyPriceTextField(PropertyPrice propertyPrice) {
        this();
        setValue(propertyPrice.getPrice());
    }

    /**
     * Display placeholder when there is no price
     *
     * @param pG graphics
     */
    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (getText().equals("0 Kč")) {

            final Graphics2D g = (Graphics2D) pG;
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawString("no price", getInsets().left + 40, pG.getFontMetrics()
                    .getMaxAscent() + getInsets().top);
        }
    }
}
