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
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.gui.controller.PersonsContract;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.List;

/**
 * Window showing list of all persons
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see Person
 */
public class PersonsWindow implements PersonsContract.View {

    private PersonsContract.Controller controller;

    // Window components
    private final JFrame mainFrame;
    private final JPanel contentPanePanel;
    private final JPanel centerPanel;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel rightPanel;

    // Top panel components
    private JLabel datePickerFromLabel;
    private JDatePicker datePickerFrom;
    private JLabel datePickerToLabel;
    private JDatePicker datePickerTo;

    // Center panel components
    private JTable personsTable;
    private JScrollPane personsTableScrollPane;

    // Right panel components

    // Bottom panel components
    private JLabel statusLabel;


    /**
     * Default constructor
     */
    public PersonsWindow() {

        // Window components
        mainFrame = new JFrame();
        contentPanePanel = new JPanel();
        centerPanel = new JPanel();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        rightPanel = new JPanel();

        // Top bar components
        datePickerFromLabel = new JLabel();
        UtilDateModel datePickerModelFrom = new UtilDateModel();
        JDatePanelImpl datePanelFrom = new JDatePanelImpl(datePickerModelFrom);
        datePickerFrom = new JDatePickerImpl(datePanelFrom);
        datePickerToLabel = new JLabel();
        UtilDateModel datePickerModelTo = new UtilDateModel();
        JDatePanelImpl datePanelTo = new JDatePanelImpl(datePickerModelTo);
        datePickerTo = new JDatePickerImpl(datePanelTo);

        // Center panel components
        personsTable = new JTable();
        personsTableScrollPane = new JScrollPane();

        // Right panel components

        // Bottom bar components
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

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setPreferredSize(new Dimension(0, 50));
        topPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        centerPanel.setLayout(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(800, 500));
        centerPanel.add(personsTable.getTableHeader(), BorderLayout.PAGE_START);
        centerPanel.add(personsTableScrollPane, BorderLayout.CENTER);

        rightPanel.setVisible(false);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(statusLabel);

        datePickerFromLabel.setText("Select date from");
        datePickerFrom.addActionListener(actionEvent -> {
            Date selectedDateFrom = (Date) datePickerFrom.getModel().getValue();
            Date selectedDateTo = (Date) datePickerTo.getModel().getValue();
            runSwingWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.filterPersonsList(selectedDateFrom, selectedDateTo);
                    return null;
                }
            });
        });
        datePickerToLabel.setText("Select date to");
        datePickerTo.addActionListener(actionEvent -> {
            Date selectedDateFrom = (Date) datePickerFrom.getModel().getValue();
            Date selectedDateTo = (Date) datePickerTo.getModel().getValue();
            runSwingWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.filterPersonsList(selectedDateFrom, selectedDateTo);
                    return null;
                }
            });
        });
        topPanel.add(datePickerFromLabel);
        topPanel.add((JComponent) (datePickerFrom));
        topPanel.add(datePickerToLabel);
        topPanel.add((JComponent) (datePickerTo));

        personsTableScrollPane.setViewportView(personsTable);

        statusLabel.setText("status");
        if (!App.isDebug()) {
            statusLabel.setVisible(false);
        }

        mainFrame.setTitle("Persons list");
        mainFrame.setSize(1200, 800);
        mainFrame.setPreferredSize(new Dimension(1200, 800));
        mainFrame.setBounds(100, 100, 500, 400);
        mainFrame.setVisible(true);
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
    public void setController(PersonsContract.Controller controller) {
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
     * Show list of all persons in table
     *
     * @param personList owners list
     */
    @Override
    public void showPersonsList(List<Person> personList) {
        SwingUtilities.invokeLater(() -> {
            String[] columnNames = {"First Name",
                    "Last Name",
                    "Street",
                    "City",
                    "Postcode",
                    "Email",
                    "Property count",
                    "Land property area sum",
                    "Duration [days]"
            };

            Object[][] data = new Object[personList.size()][9];

            if (App.isDebug()) {
                System.out.println("There are " + personList.size() + " persons");
            }

            for (int i = 0; i < personList.size(); i++) {
                data[i][0] = personList.get(i).getFirstName();
                data[i][1] = personList.get(i).getLastName();
                data[i][2] = personList.get(i).getStreet();
                data[i][3] = personList.get(i).getCity();
                data[i][4] = personList.get(i).getPsc();
                data[i][5] = personList.get(i).getEmail();
                data[i][6] = this.controller.getPersonsCountOfProperty(personList.get(i));
                data[i][7] = this.controller.getPersonsSumOfProperty(personList.get(i)) + " m\u00B2";
                data[i][8] = this.controller.getPersonsDurationOfProperty(personList.get(i));
            }
            personsTable.setFillsViewportHeight(true);
            personsTable.setModel(new DefaultTableModel(data, columnNames) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            });
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
