/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polygon;
import cz.vutbr.fit.pdb.core.model.Property;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import netscape.javascript.JSObject;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MapWindow implements MapComponentInitializedListener {

    private List<Property> propertyList;

    // Window components
    private final JFrame mainFrame;
    private final JPanel contentPanePanel;
    private final JMenuBar menuBar;
    private final JFXPanel mapFXPanel;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JScrollPane rightPanel;

    // Top panel components
    private JLabel searchNameLabel;
    private JTextField searchNameInput;
    private JLabel searchPriceLabel;
    private JTextField searchPriceInput;
    private JCheckBox searchHasOwnerCheckbox;
    private JButton searchButton;

    // Right panel components
    private JPanel propertyListPanel;

    // Bottom panel components
    private JLabel statusLabel;

    // Map components
    private Scene scene;
    private GoogleMap map;
    private GoogleMapView mapView;
    private Marker actualPosition;

    public MapWindow() {
        this(new LinkedList<>());
    }

    public MapWindow(List<Property> propertyList) {

        this.propertyList = propertyList;

        // Window components
        mainFrame = new JFrame();
        contentPanePanel = new JPanel();
        menuBar = new JMenuBar();
        mapFXPanel = new JFXPanel();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        rightPanel = new JScrollPane();

        // Top panel components
        searchNameLabel = new JLabel();
        searchNameInput = new JTextField();
        searchPriceLabel = new JLabel();
        searchPriceInput = new JTextField();
        searchHasOwnerCheckbox = new JCheckBox();
        searchButton = new JButton();

        // Right panel components
        propertyListPanel = new JPanel();

        // Bottom panel components
        statusLabel = new JLabel();
    }

    public void showAsync() {
        SwingUtilities.invokeLater(this::initAndShowGUI);
    }

    public void initAndShowGUI() {

        // JMenuBar in the Mac OS X menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        contentPanePanel.setLayout(new BorderLayout());
        contentPanePanel.add(topPanel, BorderLayout.PAGE_START);
        contentPanePanel.add(mapFXPanel, BorderLayout.CENTER);
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
        topPanel.add(searchNameLabel);
        topPanel.add(searchNameInput);
        topPanel.add(searchPriceLabel);
        topPanel.add(searchPriceInput);
        topPanel.add(searchHasOwnerCheckbox);
        topPanel.add(searchButton);

        mapFXPanel.setPreferredSize(new Dimension(800, 600));

        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightPanel.setViewportView(propertyListPanel);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(statusLabel);

        searchNameLabel.setText("Name:");
        searchNameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchNameInput.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPriceLabel.setText("Price:");
        searchPriceLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPriceInput.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchHasOwnerCheckbox.setText("Has owner");
        searchHasOwnerCheckbox.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchButton.setText("Search");
        searchButton.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchButton.setPreferredSize(new Dimension(350, searchButton.getPreferredSize().height));
        searchButton.setMinimumSize(searchButton.getPreferredSize());
        searchButton.setMaximumSize(searchButton.getPreferredSize());
        searchButton.addActionListener(e ->
                Platform.runLater(() -> {
                    propertyList.remove(0);  // TODO
                    this.showPropertyList(propertyList);
                })
        );

        propertyListPanel.setLayout(new BoxLayout(propertyListPanel, BoxLayout.Y_AXIS));

        statusLabel.setText("status");

        initMenuBar();

        mainFrame.setTitle("PDB project");
        mainFrame.setSize(1200, 800);
        mainFrame.setPreferredSize(new Dimension(1200, 800));
        mainFrame.setBounds(100, 100, 500, 400);
        mainFrame.setVisible(true);
        mainFrame.getRootPane().setDefaultButton(searchButton);
        mainFrame.setJMenuBar(menuBar);
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

        Platform.runLater(() -> {
            mapView = new GoogleMapView();
            mapView.addMapInializedListener(this);

            mapView.setPrefSize(600, 600);
            scene = new Scene(mapView);

            mapFXPanel.setScene(scene);
        });
    }

    @Override
    public void mapInitialized() {

        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(49.191423, 16.606715))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(true)
                .zoom(18);

        map = mapView.createMap(mapOptions);

        // Right clicked Point
        map.addMouseEventHandler(UIEventType.rightclick, (GMapMouseEvent event) -> {

            LatLong position = event.getLatLong();
            statusLabel.setText(position.toString());

            if (actualPosition != null) {
                map.removeMarker(actualPosition);
                this.refreshMap();
            }

            MarkerOptions opts = new MarkerOptions();
            opts.title("Position: " + position.toString())
                    .visible(true)
                    .animation(Animation.DROP)
                    .position(position);
            actualPosition = new Marker(opts);
            map.addMarker(actualPosition);

            JPopupMenu markerPopupMenu = new JPopupMenu();

            JMenuItem propertyCreateItem = new JMenuItem("Create new property here");
            JMenuItem findNearestItem = new JMenuItem("Find nearest property");
            markerPopupMenu.add(propertyCreateItem);
            markerPopupMenu.add(findNearestItem);

            ActionListener menuListener = menuEvent -> {
                System.out.println("Popup menu item [" + menuEvent.getActionCommand() + "]");

                if (menuEvent.getActionCommand().equalsIgnoreCase("Create new property here")) {
                    String[] options = new String[]{"Land", "House", "Terrace house", "Prefab", "Apartment"};
                    int response = JOptionPane.showOptionDialog(
                            mainFrame,
                            "Which type of property do you want to create",
                            "Create new property",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    Property newProperty = new Property();

                    switch (response) {
                        case 0:
                            newProperty.setType(Property.Type.LAND);
                            break;
                        case 1:
                            newProperty.setType(Property.Type.HOUSE);
                            break;
                        case 2:
                            newProperty.setType(Property.Type.TERRACE_HOUSE);
                            break;
                        case 3:
                            newProperty.setType(Property.Type.PREFAB);
                            break;
                        case 4:
                            newProperty.setType(Property.Type.APARTMENT);
                            break;
                    }

                    // TODO save geometry with position to database

                    PropertyWindow propertyWindow = new PropertyWindow(newProperty);
                    propertyWindow.showAsync();

                } else if (menuEvent.getActionCommand().equalsIgnoreCase("Find nearest property")) {
                    // TODO
                }
            };

            propertyCreateItem.addActionListener(menuListener);
            findNearestItem.addActionListener(menuListener);

            markerPopupMenu.setLabel("Marker " + position.getLatitude() + ", " + position.getLongitude());
            markerPopupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));

            Point point = MapUtil.latLng2Point(position, map);
            markerPopupMenu.show(mapFXPanel, (int) point.getX(), (int) point.getY());
        });

        // Right clicked Point
        map.addMouseEventHandler(UIEventType.click, (GMapMouseEvent event) -> {
            if (actualPosition != null) {
                map.removeMarker(actualPosition);
                actualPosition = null;
                this.refreshMap();
            }
        });

        showPropertyList(propertyList);
    }


    public void showPropertyList(List<Property> propertyList) {
        // save new property list
        this.propertyList = propertyList;

        System.out.println("show list of size: " + propertyList.size());

        // add list of properties to map
        for (Property property : propertyList) {

            System.out.println("property: " + property.getName());

            final MapShape shape = MapShapeAdapter.jGeometry2MapShape(property.getGeometry());

            JPopupMenu propertyPopupMenu = new JPopupMenu();

            JMenuItem propertyEditItem = new JMenuItem("Edit shape");
            JMenuItem propertyMoveItem = new JMenuItem("Move shape");
            JMenuItem propertySaveItem = new JMenuItem("Save");
            JMenuItem findNearestItem = new JMenuItem("Find nearest property");
            propertyPopupMenu.add(propertyEditItem);
            propertyPopupMenu.add(propertyMoveItem);
            propertyPopupMenu.addSeparator();
            propertyPopupMenu.add(propertySaveItem);
            propertyPopupMenu.addSeparator();
            propertyPopupMenu.add(findNearestItem);

            ActionListener menuListener = event -> {
                System.out.println("Popup menu item [" + event.getActionCommand() + "] was pressed on " + shape.getVariableName());

                if (event.getActionCommand().equalsIgnoreCase("edit shape")) {
                    propertyEditItem.setEnabled(false);
                    propertyMoveItem.setEnabled(false);
                    propertySaveItem.setEnabled(true);
                    Platform.runLater(() -> shape.setEditable(true));
                } else if (event.getActionCommand().equalsIgnoreCase("move shape")) {
                    propertyEditItem.setEnabled(false);
                    propertyMoveItem.setEnabled(false);
                    propertySaveItem.setEnabled(true);
                    Platform.runLater(() -> shape.setDraggable(true));
                } else if (event.getActionCommand().equalsIgnoreCase("save")) {
                    propertyEditItem.setEnabled(true);
                    propertyMoveItem.setEnabled(true);
                    propertySaveItem.setEnabled(false);
                    Platform.runLater(() -> {
                        shape.setEditable(false);
                        shape.setDraggable(false);

                        // TODO
                        System.out.println("save: " + (shape instanceof com.lynden.gmapsfx.shapes.Polygon ? ((Polygon) shape).getPath().getArray().getSlot(0).toString() : shape.getBounds().toString()));
                    });
                } else if (event.getActionCommand().equalsIgnoreCase("Find nearest property")) {
                    // TODO
                } else if (event.getActionCommand().equalsIgnoreCase("Find adjacent property")) {
                    // TODO
                } else if (event.getActionCommand().equalsIgnoreCase("Calculate area")) {
                    // TODO
                }
            };

            if (property.getType() == Property.Type.LAND) {
                // special menu for property of type land
                JMenuItem findAdjacentItem = new JMenuItem("Find adjacent property");
                JMenuItem calculateAreaItem = new JMenuItem("Calculate area");
                propertyPopupMenu.add(findAdjacentItem);
                propertyPopupMenu.add(calculateAreaItem);
            }

            propertyEditItem.addActionListener(menuListener);
            propertyMoveItem.addActionListener(menuListener);
            propertySaveItem.addActionListener(menuListener);
            propertySaveItem.setEnabled(false);

            propertyPopupMenu.setLabel(property.getName());
            propertyPopupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));

            PropertyItem propertyItem = new PropertyItem(property);
            propertyItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                        PropertyWindow propertyWindow = new PropertyWindow(property);
                        propertyWindow.showAsync();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    propertyItem.setActive(true);
                    propertyItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    Platform.runLater(() -> {
                        map.removeMapShape(shape);
                        shape.getJSObject().setMember("fillColor", "blue");
                        map.addMapShape(shape);
                    });
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    propertyItem.setActive(false);
                    propertyItem.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    Platform.runLater(() -> {
                        map.removeMapShape(shape);
                        shape.getJSObject().setMember("fillColor", "gray");
                        map.addMapShape(shape);
                    });
                }
            });
            propertyListPanel.add(propertyItem);
            JSeparator seperator = new JSeparator(SwingConstants.HORIZONTAL);
            seperator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            propertyListPanel.add(seperator);

            map.addMapShape(shape);
            map.addUIEventHandler(shape, UIEventType.rightclick, (JSObject object) -> {
                LatLong latLng = new LatLong((JSObject) object.getMember("latLng"));
                Point point = MapUtil.latLng2Point(latLng, map);

                System.out.println("Point: " + point.getX() + ", " + point.getY());

                propertyPopupMenu.show(mapFXPanel, (int) point.getX(), (int) point.getY());
            });
            map.addUIEventHandler(shape, UIEventType.click, (JSObject) -> {
                System.out.println("Property: " + property.getName());
                if (!propertyPopupMenu.isShowing()) {
                    PropertyWindow propertyWindow = new PropertyWindow(property);
                    propertyWindow.showAsync();
                }
            });
            map.addUIEventHandler(shape, UIEventType.mouseover, (JSObject object) -> {
                propertyItem.setActive(true);
                object.setMember("fillColor", "blue");  // FIXME
            });
            map.addUIEventHandler(shape, UIEventType.mouseout, (JSObject object) -> {
                propertyItem.setActive(false);
                shape.getJSObject().setMember("fillColor", "gray");  // FIXME
            });
        }

        propertyListPanel.revalidate();
    }


    public void initMenuBar() {
        JMenu mMainMenu = new JMenu("Main menu");
        JMenu mHelp = new JMenu("Help");

        JMenuItem mMainMenuOwnerList = new JMenuItem("Owners list");
        mMainMenuOwnerList.addActionListener(actionEvent -> {
            OwnersWindow ownersWindow = new OwnersWindow();
            ownersWindow.showAsync();
        });

        JMenuItem mMainMenuExecuteSQLFile = new JMenuItem("Execute SQL file");
        mMainMenuExecuteSQLFile.addActionListener(actionEvent -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                // TODO run sql file
            }
        });

        JMenuItem mMainMenuResetDB = new JMenuItem("Reset DB");
        mMainMenuResetDB.addActionListener(actionEvent ->
                // TODO
                JOptionPane.showMessageDialog(null, "Project for PDB course", "Reset DB", JOptionPane.INFORMATION_MESSAGE)
        );

        JMenuItem mMainMenuExit = new JMenuItem("Exit");
        mMainMenuExit.addActionListener(actionEvent -> {
            mainFrame.dispose();
            System.exit(0);
        });

        JMenuItem mHelpAbout = new JMenuItem("About");
        mHelpAbout.addActionListener(actionEvent ->
                // TODO
                JOptionPane.showMessageDialog(null, "Project for PDB course", "About PDB app", JOptionPane.INFORMATION_MESSAGE)
        );

        mMainMenu.add(mMainMenuOwnerList);
        mMainMenu.addSeparator();
        mMainMenu.add(mMainMenuExecuteSQLFile);
        mMainMenu.add(mMainMenuResetDB);
        mMainMenu.addSeparator();
        mMainMenu.add(mMainMenuExit);

        mHelp.add(mHelpAbout);

        menuBar.add(mMainMenu);
        menuBar.add(mHelp);
    }

    /**
     * Refresh GoogleMap with zoom
     */
    private void refreshMap() {
        int zoom = map.getZoom();
        map.setZoom(zoom - 1);
        map.setZoom(zoom);
    }

    /**
     * Callback on exit launcher
     */
    private void onExit() {
        mainFrame.dispose();
        System.exit(0);
    }
}
