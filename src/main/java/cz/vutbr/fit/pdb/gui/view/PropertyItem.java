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

import cz.vutbr.fit.pdb.core.model.GroundPlan;
import cz.vutbr.fit.pdb.core.model.Property;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Component representing one item in property items list.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see Property
 */
public class PropertyItem extends JPanel {

    private static final int DEFAULT_HEIGHT = 150;
    private static final int DEFAULT_WIDTH = 350;

    private Property property;
    private boolean active;

    private GroundPlanPanel image;
    private JPanel info;
    private JLabel name;
    private JLabel price;

    /**
     * Constructor of item by given property
     *
     * @param property property which will be displayed
     */
    public PropertyItem(Property property) {

        this.property = property;

        name = new JLabel();
        info = new JPanel();
        image = new GroundPlanPanel();
        price = new JLabel();

        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setActive(false);

        initLayout();
    }

    /**
     * Initialize and sets all visual elements in item layout
     */
    public void initLayout() {
        setLayout(new BorderLayout());

        if (property.getGroundPlans().size() > 0) {
            for (GroundPlan groundPlan : property.getGroundPlans()) {
                // show only one ground plan
                image.setGroundPlan(groundPlan);
                image.repaint();
            }
        } else {
            image.setGroundPlan(null);
            image.repaint();
        }

        image.setPreferredSize(new Dimension(DEFAULT_HEIGHT, DEFAULT_HEIGHT));

        name.setText(property.getName());
        name.setFont(new Font("sans-serif", Font.BOLD, 14));
        name.setPreferredSize(new Dimension(name.getPreferredSize().width, 25));
        name.setMaximumSize(name.getPreferredSize());
        name.setMinimumSize(name.getPreferredSize());

        price.setText(property.getPriceCurrent() != null ? String.valueOf(property.getPriceCurrent().getPrice()) + " Kč" : "no price");
        price.setFont(new Font("sans-Serif", Font.PLAIN, 12));

        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        info.add(name);
        info.add(price);

        add(image, BorderLayout.LINE_START);
        add(info, BorderLayout.CENTER);
    }

    /**
     * Active or deactivate item by setting background of item to blue or white color
     *
     * @param active whether might be item active or not
     */
    public void setActive(boolean active) {
        this.active = active;
        info.setBackground(active ? Color.LIGHT_GRAY : Color.WHITE);
    }

    /**
     * Check if item is currently active
     *
     * @return true if item is active, false otherwise
     */
    public boolean getActive() {
        return this.active;
    }
}
