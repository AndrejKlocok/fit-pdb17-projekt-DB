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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Component displaying image of property ground plan
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see GroundPlan
 */
public class GroundPlanPanel extends JPanel {

    private GroundPlan groundPlan;

    private BufferedImage image;


    /**
     * Getter of ground plan attribute
     *
     * @return ground plan
     */
    public GroundPlan getGroundPlan() {
        return groundPlan;
    }

    /**
     * Setter of ground plan
     *
     * @param groundPlan
     */
    public void setGroundPlan(GroundPlan groundPlan) {
        this.groundPlan = groundPlan;

        if (groundPlan != null) {
            if (groundPlan.getImage().length > 0) {
                try {
                    image = ImageIO.read(new ByteArrayInputStream(groundPlan.getImage()));
                } catch (IOException e) {
                    System.err.println("Error while getting image");
                }
            }
        } else {
            image = null;
        }
    }

    /**
     * Get ground plan converted to image data
     *
     * @return image data
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Set instantly image data without ground plan object
     *
     * @param image image data
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Paints image of ground plan or display "no image" placeholder
     *
     * @param g graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) {
            // draw image placeholder
            Graphics2D g2d = (Graphics2D) g.create();
            int x = 0;
            int y = 0;
            int width = getWidth();
            int height = getHeight();

            g2d.setColor(Color.WHITE);
            g2d.fillRect(x + 1, y + 1, width - 2, height - 2);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + 1, y + 1, width - 2, height - 2);

            g2d.setColor(Color.RED);

            BasicStroke stroke = new BasicStroke(4);
            g2d.setStroke(stroke);
            g2d.drawLine(x + 10, y + 10, x + width - 10, y + height - 10);
            g2d.drawLine(x + 10, y + height - 10, x + width - 10, y + 10);

            g2d.dispose();
        } else {
            // draw image
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }
}