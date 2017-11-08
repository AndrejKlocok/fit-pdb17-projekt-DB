/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.view;

import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.gui.controller.OwnersContract;
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

public class OwnersWindow implements OwnersContract.View {

    private OwnersContract.Controller controller;

    // Window components
    private final JFrame mainFrame;
    private final JPanel contentPanePanel;
    private final JPanel centerPanel;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel rightPanel;

    // Top panel components
    private JDatePicker datePicker;

    // Center panel components
    private JTable ownersTable;
    private JScrollPane ownersTableScrollPane;

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

        rightPanel.setVisible(false);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(statusLabel);

        UtilDateModel datePickerModel = new UtilDateModel();
        datePickerModel.setValue(new Date());
        datePickerModel.setSelected(true);
        JDatePanelImpl datePanel = new JDatePanelImpl(datePickerModel);
        datePicker = new JDatePickerImpl(datePanel);
        datePicker.addActionListener(actionEvent -> {
            Date selectedDate = (Date) datePicker.getModel().getValue();
            controller.getOwnersListOfDate(selectedDate);
        });
        topPanel.add((JComponent) (datePicker));

        ownersTableScrollPane.setViewportView(ownersTable);

        statusLabel.setText("status");

        mainFrame.setTitle("Owners list");
        mainFrame.setSize(1200, 800);
        mainFrame.setPreferredSize(new Dimension(1200, 800));
        mainFrame.setBounds(100, 100, 500, 400);
        mainFrame.setVisible(true);
        mainFrame.setContentPane(contentPanePanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }

    /**
     * Callback on exit launcher
     */
    private void onExit() {
        mainFrame.dispose();
    }

    @Override
    public void setController(OwnersContract.Controller controller) {
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
    public void showOwnersList(List<Owner> ownersList) {
        String[] columnNames = {"First Name",
                "Last Name",
                "Street",
                "City",
                "Postcode",
                "Email",
                "Property count",
                "Land property area sum"};

        Object[][] data = new Object[ownersList.size()][8];
        for (int i = 0; i < ownersList.size(); i++) {
            data[i][0] = ownersList.get(i).getFirstName();
            data[i][1] = ownersList.get(i).getLastName();
            data[i][2] = ownersList.get(i).getStreet();
            data[i][3] = ownersList.get(i).getCity();
            data[i][4] = ownersList.get(i).getPsc();
            data[i][5] = ownersList.get(i).getEmail();
            data[i][6] = ownersList.get(i).getPropertyCurrentCount();
            data[i][7] = ownersList.get(i).getPropertyCurrentLandAreaSum() + " m\u00B2";
        }

        ownersTable.setFillsViewportHeight(true);
        ownersTable.setModel(new DefaultTableModel(data, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        });
    }

    @Override
    public void hide() {
        onExit();
    }
}
