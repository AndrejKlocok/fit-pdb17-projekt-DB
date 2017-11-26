/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.view;

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

public class OwnersWindow implements PersonsContract.View {

    private PersonsContract.Controller controller;

    // Window components
    private final JFrame mainFrame;
    private final JPanel contentPanePanel;
    private final JPanel centerPanel;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel rightPanel;

    // Top panel components
    private JDatePicker datePickerFrom;
    private JDatePicker datePickerTo;

    // Center panel components
    private JTable ownersTable;
    private JTable ownersDurationTable;
    private JScrollPane ownersTableScrollPane;
    private JScrollPane ownersDurationTableScrollPane;

    // Right panel components

    // Bottom panel components
    private JLabel statusLabel;

    public OwnersWindow() {

        // Window components
        mainFrame = new JFrame();
        contentPanePanel = new JPanel();
        centerPanel = new JPanel();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        rightPanel = new JPanel();

        // Top bar components

        // Center panel components
        ownersTable = new JTable();
        ownersTableScrollPane = new JScrollPane();

        ownersDurationTable = new JTable();
        ownersDurationTableScrollPane = new JScrollPane();

        // Right panel components

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

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setPreferredSize(new Dimension(0, 50));
        topPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        centerPanel.setLayout(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(800, 500));
        centerPanel.add(ownersTable.getTableHeader(), BorderLayout.PAGE_START);
        centerPanel.add(ownersTableScrollPane, BorderLayout.CENTER);

        //centerPanel.add(ownersDurationTable.getTableHeader(), BorderLayout.PAGE_END);
        //centerPanel.add(ownersDurationTableScrollPane, BorderLayout.CENTER);

        rightPanel.setVisible(false);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(statusLabel);

        UtilDateModel datePickerModelFrom = new UtilDateModel();
        datePickerModelFrom.setValue(new Date());
        datePickerModelFrom.setSelected(true);
        JDatePanelImpl datePanelFrom = new JDatePanelImpl(datePickerModelFrom);
        datePickerFrom = new JDatePickerImpl(datePanelFrom);
        datePickerFrom.addActionListener(actionEvent -> {
            Date selectedDate = (Date) datePickerFrom.getModel().getValue();
            runSwingWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.getOwnersListOfDate(selectedDate, (Date) datePickerTo.getModel().getValue());
                    return null;
                }
            });
        });


        UtilDateModel datePickerModelTo = new UtilDateModel();
        datePickerModelTo.setValue(new Date());
        datePickerModelTo.setSelected(true);
        JDatePanelImpl datePanelTo = new JDatePanelImpl(datePickerModelTo);
        datePickerTo = new JDatePickerImpl(datePanelTo);
        datePickerTo.addActionListener(actionEvent -> {
            Date selectedDate = (Date) datePickerTo.getModel().getValue();
            runSwingWorker(new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    controller.getOwnersListOfDate((Date) datePickerFrom.getModel().getValue(), selectedDate);
                    return null;
                }
            });
        });
        topPanel.add((JComponent) (datePickerFrom));
        topPanel.add((JComponent) (datePickerTo));


        ownersTableScrollPane.setViewportView(ownersTable);
        ownersDurationTableScrollPane.setViewportView(ownersDurationTable);

        statusLabel.setText("status");

        mainFrame.setTitle("Owners list");
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
    public void setController(PersonsContract.Controller controller) {
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
    public void showOwnersList(List<Person> personList) {
        SwingUtilities.invokeLater(() -> {
            String[] columnNames = {"First Name",
                    "Last Name",
                    "Street",
                    "City",
                    "Postcode",
                    "Email",
                    "Property count",
                    "Land property area sum",
                    "Duration [days]"};

            Object[][] data = new Object[personList.size()][9];

            System.out.println("There are " + personList.size() + " owners");

            for (int i = 0; i < personList.size(); i++) {
                data[i][0] = personList.get(i).getFirstName();
                data[i][1] = personList.get(i).getLastName();
                data[i][2] = personList.get(i).getStreet();
                data[i][3] = personList.get(i).getCity();
                data[i][4] = personList.get(i).getPsc();
                data[i][5] = personList.get(i).getEmail();
                data[i][6] = this.controller.getOwnersCountOfPropertyDate(personList.get(i).getIdPerson(), (Date) datePickerFrom.getModel().getValue(), (Date) datePickerTo.getModel().getValue());
                data[i][7] = this.controller.getOwnersSumOfPropertyDate(personList.get(i).getIdPerson(), (Date) datePickerFrom.getModel().getValue(), (Date) datePickerTo.getModel().getValue());
                data[i][8] = this.controller.getOwnersDurationOfPropertyDate(personList.get(i).getIdPerson(), (Date) datePickerFrom.getModel().getValue(), (Date) datePickerTo.getModel().getValue());
            }
            ownersTable.setFillsViewportHeight(true);
            ownersTable.setModel(new DefaultTableModel(data, columnNames) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            });
        });
    }

    @Override
    public void hide() {
        onExit();
    }
}
