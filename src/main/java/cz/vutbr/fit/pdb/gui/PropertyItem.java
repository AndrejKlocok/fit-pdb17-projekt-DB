/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui;

import cz.vutbr.fit.pdb.core.model.Property;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PropertyItem extends JPanel {

    private static final int DEFAULT_HEIGHT = 150;
    private static final int DEFAULT_WIDTH = 350;

    private Property property;
    private boolean active;

    private JPanel image;
    private JPanel info;
    private JLabel name;
    private JLabel price;

    public PropertyItem(Property property) {

        this.property = property;

        name = new JLabel();
        info = new JPanel();
        image = new JPanel();
        price = new JLabel();

        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setActive(false);

        initLayout();
    }

    public void initLayout() {
        setLayout(new BorderLayout());

        image.setBackground(Color.CYAN);  // TODO load image
        image.setPreferredSize(new Dimension(DEFAULT_HEIGHT, DEFAULT_HEIGHT));

        name.setText(property.getName());
        name.setFont(new Font("sans-serif", Font.BOLD, 14));
        name.setPreferredSize(new Dimension(name.getPreferredSize().width, 25));
        name.setMaximumSize(name.getPreferredSize());
        name.setMinimumSize(name.getPreferredSize());

        price.setText(property.getPriceCurrent().toString() + " Kč");
        price.setFont(new Font("sans-Serif", Font.PLAIN, 12));

        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        info.add(name);
        info.add(price);

        add(image, BorderLayout.LINE_START);
        add(info, BorderLayout.CENTER);
    }

    public void setActive(boolean active) {
        this.active = active;
        info.setBackground(active ? Color.LIGHT_GRAY : Color.WHITE);
    }

    public boolean getActive() {
        return this.active;
    }
}
