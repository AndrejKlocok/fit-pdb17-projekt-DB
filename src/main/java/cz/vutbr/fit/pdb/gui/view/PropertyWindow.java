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

import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.*;
import cz.vutbr.fit.pdb.gui.controller.PropertyContract;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;

/**
 * Window showing detail of property
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see Property
 */
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
    private PropertyPriceTextField priceCurrentTextField;
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
    private JLabel priceHistoryLabel;
    private JPanel priceHistoryPanel;
    private JPanel editPriceHistoryPanel;
    private JLabel editPriceHistoryDatePickerFromLabel;
    private JDatePicker editPriceHistoryDatePickerFrom;
    private JLabel editPriceHistoryDatePickerToLabel;
    private JDatePicker editPriceHistoryDatePickerTo;
    private JButton editPriceHistoryCountAverageButton;

    // Right panel components
    private JLabel propertyListSimilarLabel;
    private JCheckBox propertyListSimilarHasOwnerCheckbox;
    private JScrollPane propertyListSimilarScrollPane;
    private JPanel propertyListSimilarPanel;

    // Bottom panel components
    private JLabel ownerHistoryLabel;
    private JTable ownersHistoryTable;
    private JScrollPane ownersHistoryTableScrollPane;
    private JLabel editOwnersHistoryLabel;
    private JPanel editOwnersHistoryPanel;
    private JLabel editOwnersHistoryDatePickerFromLabel;
    private JDatePicker editOwnersHistoryDatePickerFrom;
    private JLabel editOwnersHistoryDatePickerToLabel;
    private JDatePicker editOwnersHistoryDatePickerTo;
    private JLabel editOwnersHistoryPersonComboBoxLabel;
    private PersonComboBox editOwnersHistoryPersonComboBox;
    private JButton editOwnersHistoryEditButton;
    private JButton editOwnersHistoryDeleteButton;
    private JLabel statusLabel;


    /**
     * Default constructor
     */
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
        priceCurrentTextField = new PropertyPriceTextField();
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
        priceHistoryLabel = new JLabel();
        priceHistoryPanel = new JPanel();
        editPriceHistoryPanel = new JPanel();
        editPriceHistoryDatePickerFromLabel = new JLabel();
        UtilDateModel editPriceHistoryDatePickerFromModel = new UtilDateModel();
        editPriceHistoryDatePickerFromModel.setValue(new Date());
        editPriceHistoryDatePickerFromModel.setSelected(true);
        JDatePanelImpl editPriceHistoryDatePickerFromPanel = new JDatePanelImpl(editPriceHistoryDatePickerFromModel);
        editPriceHistoryDatePickerFrom = new JDatePickerImpl(editPriceHistoryDatePickerFromPanel);
        editPriceHistoryDatePickerToLabel = new JLabel();
        UtilDateModel editPriceHistoryDatePickerToModel = new UtilDateModel();
        editPriceHistoryDatePickerToModel.setValue(new Date());
        editPriceHistoryDatePickerToModel.setSelected(true);
        JDatePanelImpl editPriceHistoryDatePickerToPanel = new JDatePanelImpl(editPriceHistoryDatePickerToModel);
        editPriceHistoryDatePickerTo = new JDatePickerImpl(editPriceHistoryDatePickerToPanel);
        editPriceHistoryCountAverageButton = new JButton();

        // Right panel components
        propertyListSimilarLabel = new JLabel();
        propertyListSimilarHasOwnerCheckbox = new JCheckBox();
        propertyListSimilarScrollPane = new JScrollPane();
        propertyListSimilarPanel = new JPanel();

        // Bottom bar components
        ownerHistoryLabel = new JLabel();
        ownersHistoryTable = new JTable();
        ownersHistoryTableScrollPane = new JScrollPane();
        editOwnersHistoryLabel = new JLabel();
        editOwnersHistoryPanel = new JPanel();
        editOwnersHistoryDatePickerFromLabel = new JLabel();
        UtilDateModel editOwnersHistoryDatePickerFromModel = new UtilDateModel();
        editOwnersHistoryDatePickerFromModel.setValue(new Date());
        editOwnersHistoryDatePickerFromModel.setSelected(true);
        JDatePanelImpl editOwnersHistoryDatePickerFromPanel = new JDatePanelImpl(editOwnersHistoryDatePickerFromModel);
        editOwnersHistoryDatePickerFrom = new JDatePickerImpl(editOwnersHistoryDatePickerFromPanel);
        editOwnersHistoryDatePickerToLabel = new JLabel();
        UtilDateModel editOwnersHistoryDatePickerToModel = new UtilDateModel();
        editOwnersHistoryDatePickerToModel.setValue(new Date());
        editOwnersHistoryDatePickerToModel.setSelected(true);
        JDatePanelImpl editOwnersHistoryDatePickerToPanel = new JDatePanelImpl(editOwnersHistoryDatePickerToModel);
        editOwnersHistoryDatePickerTo = new JDatePickerImpl(editOwnersHistoryDatePickerToPanel);
        editOwnersHistoryPersonComboBoxLabel = new JLabel();
        editOwnersHistoryPersonComboBox = new PersonComboBox();
        editOwnersHistoryEditButton = new JButton();
        editOwnersHistoryDeleteButton = new JButton();
        statusLabel = new JLabel();

        showAsync();
    }

    /**
     * Show window on asynchronous on UI thread
     */
    public void showAsync() {
        SwingUtilities.invokeLater(this::initAndShowGUI);
    }

    /**
     * Initialize window components and show window
     */
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
        centerPanel.add(priceHistoryLabel);
        centerPanel.add(priceHistoryPanel);
        centerPanel.add(editPriceHistoryPanel);

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.add(propertyListSimilarLabel);
        rightPanel.add(propertyListSimilarHasOwnerCheckbox);
        rightPanel.add(propertyListSimilarScrollPane);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(ownerHistoryLabel);
        bottomPanel.add(ownersHistoryTable.getTableHeader());
        bottomPanel.add(ownersHistoryTableScrollPane);
        bottomPanel.add(editOwnersHistoryLabel);
        bottomPanel.add(editOwnersHistoryPanel);
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
        infoPanel.add(priceCurrentTextField);
        infoPanel.add(ownerLabel);
        infoPanel.add(descriptionLabel);
        infoPanel.add(editInfoPanel);
        groundPlanPanel.setLayout(new BorderLayout());
        groundPlanPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        groundPlanPanel.add(editGroundPlanPanel, BorderLayout.PAGE_END);

        nameLabel.setFont(new Font("sans-serif", Font.BOLD, 18));
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        nameLabel.setEditable(false);
        nameLabel.setBorder(null);
        priceCurrentTextField.setFont(new Font("sans-Serif", Font.PLAIN, 12));
        priceCurrentTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        priceCurrentTextField.setEditable(false);
        ownerLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        ownerLabel.setEditable(false);
        ownerLabel.setBorder(null);
        descriptionLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        descriptionLabel.setEditable(false);
        descriptionLabel.setBorder(null);
        descriptionLabel.setBackground(null);
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
                priceCurrentTextField.setEditable(true);
                descriptionLabel.setEditable(true);
                descriptionLabel.setBackground(Color.WHITE);
            } else {
                editPropertyButton.setText("Edit");
                nameLabel.setEditable(false);
                priceCurrentTextField.setEditable(false);
                descriptionLabel.setEditable(false);
                descriptionLabel.setBackground(null);
                runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.savePropertyName(nameLabel.getText());
                        controller.savePropertyDescription(descriptionLabel.getText());
                        controller.savePropertyCurrentPrice(priceCurrentTextField.getPrice());

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
                        controller.deleteCurrentOwner();

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
                if (App.isDebug()) {
                    System.out.println(fc.getSelectedFile().getAbsolutePath());
                }
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

        propertyListSimilarLabel.setText("Property with similar ground plan");
        propertyListSimilarLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        propertyListSimilarLabel.setFont(new Font("sans-serif", Font.BOLD, 16));
        propertyListSimilarLabel.setBorder(new EmptyBorder(10, 0, 10, 10));
        propertyListSimilarHasOwnerCheckbox.setText("Filter property which has not owner");
        propertyListSimilarHasOwnerCheckbox.addActionListener(e -> runSwingWorker(new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.filterPropertyListSimilar(propertyListSimilarHasOwnerCheckbox.isSelected());

                return null;
            }
        }));
        propertyListSimilarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        propertyListSimilarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        propertyListSimilarScrollPane.setViewportView(propertyListSimilarPanel);
        propertyListSimilarPanel.setLayout(new BoxLayout(propertyListSimilarPanel, BoxLayout.Y_AXIS));

        priceHistoryLabel.setText("Price history");
        priceHistoryLabel.setBorder(new EmptyBorder(10, 10, 0, 10));
        priceHistoryLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, priceHistoryLabel.getPreferredSize().height));
        priceHistoryLabel.setFont(new Font("sans-serif", Font.BOLD, 16));
        priceHistoryPanel.setLayout(new BorderLayout());
        editPriceHistoryPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        editPriceHistoryPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        editPriceHistoryPanel.setLayout(new BoxLayout(editPriceHistoryPanel, BoxLayout.X_AXIS));
        editPriceHistoryPanel.add(editPriceHistoryDatePickerFromLabel);
        editPriceHistoryPanel.add((JComponent) (editPriceHistoryDatePickerFrom));
        editPriceHistoryPanel.add(editPriceHistoryDatePickerToLabel);
        editPriceHistoryPanel.add((JComponent) (editPriceHistoryDatePickerTo));
        editPriceHistoryPanel.add(editPriceHistoryCountAverageButton);
        editPriceHistoryDatePickerFromLabel.setText("Select date from");
        editPriceHistoryDatePickerFromLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        editPriceHistoryDatePickerToLabel.setText("Select date to");
        editPriceHistoryDatePickerToLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        editPriceHistoryCountAverageButton.setText("Count average price");
        editPriceHistoryCountAverageButton.addActionListener(e -> {

            Date selectedDateFrom = (Date) editPriceHistoryDatePickerFrom.getModel().getValue();
            Date selectedDateTo = (Date) editPriceHistoryDatePickerTo.getModel().getValue();

            runSwingWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.calculateAveragePriceFromDateToDate(selectedDateFrom, selectedDateTo);

                    return null;
                }
            });
        });

        ownerHistoryLabel.setText("Owners history");
        ownerHistoryLabel.setBorder(new EmptyBorder(10, 0, 0, 10));
        ownerHistoryLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, priceHistoryLabel.getPreferredSize().height));
        ownerHistoryLabel.setFont(new Font("sans-serif", Font.BOLD, 16));
        ownersHistoryTableScrollPane.setViewportView(ownersHistoryTable);
        ownersHistoryTableScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        ownersHistoryTable.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        editOwnersHistoryLabel.setText("Edit owners history");
        editOwnersHistoryLabel.setBorder(new EmptyBorder(10, 0, 0, 10));
        editOwnersHistoryLabel.setMinimumSize(new Dimension(Integer.MAX_VALUE, priceHistoryLabel.getPreferredSize().height));
        editOwnersHistoryLabel.setFont(new Font("sans-serif", Font.BOLD, 16));
        editOwnersHistoryPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        editOwnersHistoryPanel.setLayout(new BoxLayout(editOwnersHistoryPanel, BoxLayout.X_AXIS));
        editOwnersHistoryPanel.add(editOwnersHistoryDatePickerFromLabel);
        editOwnersHistoryPanel.add((JComponent) (editOwnersHistoryDatePickerFrom));
        editOwnersHistoryPanel.add(editOwnersHistoryDatePickerToLabel);
        editOwnersHistoryPanel.add((JComponent) (editOwnersHistoryDatePickerTo));
        editOwnersHistoryPanel.add(editOwnersHistoryPersonComboBoxLabel);
        editOwnersHistoryPanel.add(editOwnersHistoryPersonComboBox);
        editOwnersHistoryPanel.add(editOwnersHistoryEditButton);
        editOwnersHistoryPanel.add(editOwnersHistoryDeleteButton);
        editOwnersHistoryDatePickerFromLabel.setText("Select date from");
        editOwnersHistoryDatePickerFromLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        editOwnersHistoryDatePickerToLabel.setText("Select date to");
        editOwnersHistoryDatePickerToLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        editOwnersHistoryPersonComboBoxLabel.setText("Select owner");
        editOwnersHistoryPersonComboBoxLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        editOwnersHistoryPersonComboBox.setBorder(new EmptyBorder(10, 0, 10, 10));
        editOwnersHistoryEditButton.setText("Save");
        editOwnersHistoryEditButton.addActionListener(e -> {

            Date selectedDateFrom = (Date) editOwnersHistoryDatePickerFrom.getModel().getValue();
            Date selectedDateTo = (Date) editOwnersHistoryDatePickerTo.getModel().getValue();
            Person selectedPerson = (Person) editOwnersHistoryPersonComboBox.getSelectedItem();

            runSwingWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.saveOwnerFromDateToDate(selectedPerson, selectedDateFrom, selectedDateTo);

                    return null;
                }
            });
        });
        editOwnersHistoryDeleteButton.setText("Delete");
        editOwnersHistoryDeleteButton.addActionListener(e -> {

            Date selectedDateFrom = (Date) editOwnersHistoryDatePickerFrom.getModel().getValue();
            Date selectedDateTo = (Date) editOwnersHistoryDatePickerTo.getModel().getValue();

            runSwingWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.deleteOwnerFromDateToDate(selectedDateFrom, selectedDateTo);

                    return null;
                }
            });
        });
        statusLabel.setText("status");
        if (!App.isDebug()) {
            statusLabel.setVisible(false);
        }

        mainFrame.setSize(new Dimension(1200, 900));
        mainFrame.setContentPane(contentPanePanel);
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
     * Callback on exit window
     */
    private void onExit() {
        mainFrame.dispose();
    }

    /**
     * Shows loading dialog
     *
     * @param swingWorker runnable which will be executed while is dialog showed
     */
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

    /**
     * Set controller of this view
     *
     * @param controller instance of controller
     */
    @Override
    public void setController(PropertyContract.Controller controller) {
        this.controller = controller;
    }

    /**
     * Shows dialog with information message
     *
     * @param message message
     */
    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows dialog with error message
     *
     * @param error message
     */
    @Override
    public void showError(String error) {
        JOptionPane.showMessageDialog(
                mainFrame,
                error,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Fills all window components relating to property with property data
     *
     * @param property property which will be showed
     */
    @Override
    public void showProperty(Property property) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setTitle("Detail of property " + property.getName());
            nameLabel.setText(property.getName());
            priceCurrentTextField.setPropertyPrice(property.getPriceCurrent());
            ownerLabel.setText(property.hasOwner() ? property.getOwnerCurrent().getPerson().getFirstName() + " " + property.getOwnerCurrent().getPerson().getLastName() : "no owner");
            descriptionLabel.setText(property.getDescription());

            if (property.getGroundPlans().size() > 0) {
                if (App.isDebug()) {
                    System.out.println("There are " + property.getGroundPlans().size() + " ground plans");
                }

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
            } else {
                // disable rotate buttons
                groundPlanPanel.setGroundPlan(null);
                groundPlanPanel.repaint();
                deleteGroundPlanButton.setEnabled(false);
                rotateLeftGroundPlanButton.setEnabled(false);
                rotateRightGroundPlanButton.setEnabled(false);
            }

            DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

            if (App.isDebug()) {
                System.out.println("price count " + property.getPriceHistory().size());
            }

            for (PropertyPrice propertyPrice : property.getPriceHistory()) {
                dataSet.addValue(propertyPrice.getPrice(), "price", propertyPrice.getValidFrom());
            }

            JFreeChart lineChart = ChartFactory.createLineChart3D(
                    "",
                    "Time",
                    "Price",
                    dataSet,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );
            lineChart.setBackgroundPaint(null);
            CategoryAxis axis = lineChart.getCategoryPlot().getDomainAxis();
            axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            ChartPanel chartPanel = new ChartPanel(lineChart);

            priceHistoryPanel.removeAll();
            priceHistoryPanel.add(chartPanel, BorderLayout.CENTER);
            priceHistoryPanel.revalidate();

            String[] columnNames = {"First Name",
                    "Last Name",
                    "Owner from",
                    "Owner to"};

            Object[][] data = new Object[property.getOwnerHistory().size()][columnNames.length];

            if (App.isDebug()) {
                System.out.println("There are " + property.getOwnerHistory().size() + " owners");
            }

            for (int i = 0; i < property.getOwnerHistory().size(); i++) {
                data[i][0] = property.getOwnerHistory().get(i).getPerson().getFirstName();
                data[i][1] = property.getOwnerHistory().get(i).getPerson().getLastName();
                data[i][2] = property.getOwnerHistory().get(i).getValidFrom();
                data[i][3] = property.getOwnerHistory().get(i).getValidTo();
            }

            ownersHistoryTable.setModel(new DefaultTableModel(data, columnNames) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            });
        });
    }

    /**
     * Show similar property list in right panel
     *
     * @param propertyList list of property which will be showed
     */
    @Override
    public void showPropertyListSimilar(List<Property> propertyList) {
        SwingUtilities.invokeLater(() -> {
            // remove old property items
            propertyListSimilarPanel.removeAll();

            // add list of properties to map
            for (Property property : propertyList) {

                if (App.isDebug()) {
                    System.out.println("property: " + property.getName());
                }

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

                propertyListSimilarPanel.add(propertyItem);
                JSeparator seperator = new JSeparator(SwingConstants.HORIZONTAL);
                seperator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                propertyListSimilarPanel.add(seperator);
            }

            propertyListSimilarPanel.revalidate();
            propertyListSimilarPanel.repaint();
        });
    }

    /**
     * Hide window
     */
    @Override
    public void hide() {
        onExit();
    }
}
