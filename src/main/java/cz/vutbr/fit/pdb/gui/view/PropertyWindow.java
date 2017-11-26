/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.view;

import cz.vutbr.fit.pdb.core.model.GroundPlan;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import cz.vutbr.fit.pdb.gui.controller.PropertyContract;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PropertyWindow implements PropertyContract.View {

    private PropertyContract.Controller controller;

    // Window components
    private final JFrame mainFrame;
    private final JPanel contentPanePanel;
    private final JPanel centerPanel;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel rightPanel;

    // Top panel components

    // Center panel components
    private JPanel propertyPanel;
    private GroundPlanPanel groundPlanPanel;
    private JPanel infoPanel;
    private JTextField nameLabel;
    private JTextField priceCurrentLabel;
    private JTextField ownerLabel;
    private JTextPane descriptionLabel;
    private JPanel editInfoPanel;
    private JButton editPropertyButton;
    private JButton deletePropertyButton;
    private JButton deleteOwnerButton;
    private JPanel editGroundPlanPanel;
    private JButton createGroundPlanButton;
    private JButton deleteGroundPlanButton;
    private JButton rotateLeftGroundPlanButton;
    private JButton rotateRightGroundPlanButton;
    private JPanel priceHistoryPanel;

    // Right panel components
    private JLabel similarLabel;
    private JCheckBox hasOwnerCheckbox;
    private JScrollPane propertyListScrollPane;
    private JPanel propertyListPanel;

    // Bottom panel components
    private JLabel statusLabel;

    public PropertyWindow() {

        // Window components
        mainFrame = new JFrame();
        contentPanePanel = new JPanel();
        centerPanel = new JPanel();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        rightPanel = new JPanel();

        // Top bar components

        // Center panel components
        propertyPanel = new JPanel();
        groundPlanPanel = new GroundPlanPanel();
        infoPanel = new JPanel();
        nameLabel = new JTextField();
        priceCurrentLabel = new JTextField();
        ownerLabel = new JTextField();
        descriptionLabel = new JTextPane();
        editInfoPanel = new JPanel();
        editPropertyButton = new JButton();
        deletePropertyButton = new JButton();
        deleteOwnerButton = new JButton();
        editGroundPlanPanel = new JPanel();
        createGroundPlanButton = new JButton();
        deleteGroundPlanButton = new JButton();
        rotateLeftGroundPlanButton = new JButton();
        rotateRightGroundPlanButton = new JButton();
        priceHistoryPanel = new JPanel();

        // Right panel components
        similarLabel = new JLabel();
        hasOwnerCheckbox = new JCheckBox();
        propertyListScrollPane = new JScrollPane();
        propertyListPanel = new JPanel();

        // Bottom bar components
        statusLabel = new JLabel();

        showAsync();
    }

    public void showAsync() {
        SwingUtilities.invokeLater(this::initAndShowGUI);
    }

    public void initAndShowGUI() {

        // JMenuBar in the Mac OS X menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        contentPanePanel.setLayout(new BorderLayout());
        contentPanePanel.add(topPanel, BorderLayout.PAGE_START);
        contentPanePanel.add(centerPanel, BorderLayout.CENTER);
        contentPanePanel.add(rightPanel, BorderLayout.LINE_END);
        contentPanePanel.add(bottomPanel, BorderLayout.PAGE_END);
        // call dispose() on ESCAPE
        contentPanePanel.registerKeyboardAction(e -> onExit(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        topPanel.setVisible(false);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setPreferredSize(new Dimension(800, 500));
        centerPanel.add(propertyPanel);
        centerPanel.add(priceHistoryPanel);

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.add(similarLabel);
        rightPanel.add(hasOwnerCheckbox);
        rightPanel.add(propertyListScrollPane);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(statusLabel);

        propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.X_AXIS));
        propertyPanel.setPreferredSize(new Dimension(propertyPanel.getPreferredSize().width, 400));
        propertyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        propertyPanel.add(groundPlanPanel);
        propertyPanel.add(infoPanel);
        groundPlanPanel.setPreferredSize(new Dimension(400, 400));
        groundPlanPanel.setMaximumSize(groundPlanPanel.getPreferredSize());
        groundPlanPanel.setMinimumSize(groundPlanPanel.getPreferredSize());
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.add(nameLabel);
        infoPanel.add(priceCurrentLabel);
        infoPanel.add(ownerLabel);
        infoPanel.add(descriptionLabel);
        infoPanel.add(editInfoPanel);
        groundPlanPanel.setLayout(new BorderLayout());
        groundPlanPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        groundPlanPanel.add(editGroundPlanPanel, BorderLayout.PAGE_END);

        nameLabel.setFont(new Font("sans-serif", Font.BOLD, 18));
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        nameLabel.setEditable(false);
        // TODO add option to specify price date
        priceCurrentLabel.setFont(new Font("sans-Serif", Font.PLAIN, 12));
        priceCurrentLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        priceCurrentLabel.setEditable(false);
        ownerLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        ownerLabel.setEditable(false);
        descriptionLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        descriptionLabel.setEditable(false);
        editInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        editInfoPanel.setLayout(new BoxLayout(editInfoPanel, BoxLayout.X_AXIS));
        editInfoPanel.add(editPropertyButton);
        editInfoPanel.add(deletePropertyButton);
        editInfoPanel.add(deleteOwnerButton);
        editGroundPlanPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        editGroundPlanPanel.setLayout(new BoxLayout(editGroundPlanPanel, BoxLayout.X_AXIS));
        editGroundPlanPanel.setOpaque(false);
        editGroundPlanPanel.add(rotateLeftGroundPlanButton);
        editGroundPlanPanel.add(createGroundPlanButton);
        editGroundPlanPanel.add(deleteGroundPlanButton);
        editGroundPlanPanel.add(rotateRightGroundPlanButton);

        editPropertyButton.setText("Edit");
        editPropertyButton.addActionListener(e -> {
            if (editPropertyButton.getText().equalsIgnoreCase("edit")) {
                editPropertyButton.setText("Save");
                nameLabel.setEditable(true);
                priceCurrentLabel.setEditable(true);
                descriptionLabel.setEditable(true);
            } else {
                editPropertyButton.setText("Edit");
                nameLabel.setEditable(false);
                priceCurrentLabel.setEditable(false);
                descriptionLabel.setEditable(false);
                runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.savePropertyName(nameLabel.getText());
                        controller.savePropertyDescription(descriptionLabel.getText());
                        controller.savePropertyCurrentPrice(priceCurrentLabel.getText());

                        return null;
                    }
                });
            }
        });
        deletePropertyButton.setText("Delete");
        deletePropertyButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Do you want delete this property?",
                    "Delete property " + nameLabel.getText(),
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.deleteProperty();

                        return null;
                    }
                });
            }
        });
        deleteOwnerButton.setText("Delete owner");
        deleteOwnerButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Do you want delete owner from this property?",
                    "Delete owner of property " + nameLabel.getText(),
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.deleteOwner();

                        return null;
                    }
                });
            }
        });
        createGroundPlanButton.setText("Upload image");
        createGroundPlanButton.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            int returnVal = fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println(fc.getSelectedFile().getAbsolutePath());
                runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.createGroundPlan(fc.getSelectedFile().getAbsolutePath());

                        return null;
                    }
                });
            }
        });
        deleteGroundPlanButton.setText("Delete image");
        deleteGroundPlanButton.setEnabled(false);
        rotateLeftGroundPlanButton.setText("\u21B6");
        rotateLeftGroundPlanButton.setEnabled(false);
        rotateRightGroundPlanButton.setText("\u21B7");
        rotateRightGroundPlanButton.setEnabled(false);

        priceHistoryPanel.setLayout(new BorderLayout());

        similarLabel.setText("Property with similar ground plan");
        similarLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        similarLabel.setFont(new Font("sans-serif", Font.PLAIN, 14));
        similarLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        hasOwnerCheckbox.setText("Filter property which has not owner");
        propertyListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        propertyListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        propertyListScrollPane.setViewportView(propertyListPanel);
        propertyListPanel.setLayout(new BoxLayout(propertyListPanel, BoxLayout.Y_AXIS));

        statusLabel.setText("status");

        mainFrame.setSize(1200, 800);
        mainFrame.setPreferredSize(new Dimension(1200, 800));
        mainFrame.setBounds(100, 100, 500, 400);
        mainFrame.setContentPane(contentPanePanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
        mainFrame.setVisible(true);
        int state = mainFrame.getExtendedState();
        state &= ~JFrame.ICONIFIED;
        mainFrame.setExtendedState(state);
        mainFrame.setAlwaysOnTop(true);
        mainFrame.toFront();
        mainFrame.requestFocus();
        mainFrame.setAlwaysOnTop(false);
    }

    /**
     * Callback on exit launcher
     */
    private void onExit() {
        mainFrame.dispose();
    }

    private void runSwingWorker(SwingWorker<Void, Void> swingWorker) {
        final JDialog dialog = new JDialog(mainFrame, "Dialog", Dialog.ModalityType.APPLICATION_MODAL);

        swingWorker.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("state")) {
                if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                    dialog.dispose();
                }
            }
        });
        swingWorker.execute();

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(new JLabel("Please wait......."), BorderLayout.PAGE_START);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    @Override
    public void setController(PropertyContract.Controller controller) {
        this.controller = controller;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String error) {
        JOptionPane.showMessageDialog(
                mainFrame,
                error,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showProperty(Property property) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setTitle("Detail of property " + property.getName());
            nameLabel.setText(property.getName());
            priceCurrentLabel.setText(property.getPriceCurrent() != null ? String.valueOf(property.getPriceCurrent().getPrice()) : "no price");
            ownerLabel.setText(property.getOwnerCurrent() != null ? property.getOwnerCurrent().getPerson().getFirstName() + " " + property.getOwnerCurrent().getPerson().getLastName() : "no owner");
            descriptionLabel.setText(property.getDescription());

            if (property.getGroundPlans().size() > 0) {
                System.out.println("There are " + property.getGroundPlans().size() + " ground plans");
                for (GroundPlan groundPlan : property.getGroundPlans()) {
                    // TODO option to view other ground plans (something like gallery)
                    groundPlanPanel.setGroundPlan(groundPlan);
                    groundPlanPanel.repaint();

                    deleteGroundPlanButton.setEnabled(true);
                    rotateLeftGroundPlanButton.setEnabled(true);
                    rotateRightGroundPlanButton.setEnabled(true);
                    for (ActionListener act : deleteGroundPlanButton.getActionListeners()) {
                        deleteGroundPlanButton.removeActionListener(act);
                    }
                    for (ActionListener act : rotateLeftGroundPlanButton.getActionListeners()) {
                        rotateLeftGroundPlanButton.removeActionListener(act);
                    }
                    for (ActionListener act : rotateRightGroundPlanButton.getActionListeners()) {
                        rotateRightGroundPlanButton.removeActionListener(act);
                    }
                    deleteGroundPlanButton.addActionListener(e -> runSwingWorker(new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            controller.deleteGroundPlan(groundPlan);

                            return null;
                        }
                    }));
                    rotateLeftGroundPlanButton.addActionListener(e -> runSwingWorker(new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            controller.rotateGroundPlanLeft(groundPlan);

                            return null;
                        }
                    }));
                    rotateRightGroundPlanButton.addActionListener(e -> runSwingWorker(new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            controller.rotateGroundPlanRight(groundPlan);

                            return null;
                        }
                    }));
                }
            } else {// disable rotate buttons
                groundPlanPanel.setGroundPlan(null);
                groundPlanPanel.repaint();
                deleteGroundPlanButton.setEnabled(false);
                rotateLeftGroundPlanButton.setEnabled(false);
                rotateRightGroundPlanButton.setEnabled(false);
            }

            DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

            System.out.println("price count " + property.getPriceHistory().size());

            for (PropertyPrice propertyPrice : property.getPriceHistory()) {
                dataSet.addValue(propertyPrice.getPrice(), "price", propertyPrice.getValidFrom() + "-" + propertyPrice.getValidTo());
            }

            JFreeChart lineChart = ChartFactory.createLineChart(
                    "Price history",
                    "Time",
                    "Price",
                    dataSet,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
            ChartPanel chartPanel = new ChartPanel(lineChart);

            priceHistoryPanel.removeAll();
            priceHistoryPanel.add(chartPanel, BorderLayout.CENTER);
            priceHistoryPanel.revalidate();
        });
    }

    @Override
    public void showPropertyListSimilar(List<Property> propertyList) {
        SwingUtilities.invokeLater(() -> {
            // remove old property items
            propertyListPanel.removeAll();

            // add list of properties to map
            for (Property property : propertyList) {

                System.out.println("property: " + property.getName());

                PropertyItem propertyItem = new PropertyItem(property);
                propertyItem.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                            runSwingWorker(new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    controller.getPropertySimilar(property);

                                    return null;
                                }
                            });
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent mouseEvent) {
                        propertyItem.setActive(true);
                        propertyItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }

                    @Override
                    public void mouseExited(MouseEvent mouseEvent) {
                        propertyItem.setActive(false);
                        propertyItem.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                });

                propertyListPanel.add(propertyItem);
                JSeparator seperator = new JSeparator(SwingConstants.HORIZONTAL);
                seperator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                propertyListPanel.add(seperator);
            }

            propertyListPanel.revalidate();
            propertyListPanel.repaint();
        });
    }

    @Override
    public void hide() {
        onExit();
    }
}
