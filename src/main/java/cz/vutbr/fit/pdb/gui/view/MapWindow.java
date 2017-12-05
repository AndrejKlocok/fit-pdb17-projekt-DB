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

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polygon;
import com.lynden.gmapsfx.shapes.Polyline;
import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.gui.adapters.ShapePointsAdapter;
import cz.vutbr.fit.pdb.gui.controller.MapContract;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import netscape.javascript.JSObject;
import oracle.spatial.geometry.JGeometry;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Window showing list of all property on map
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see Property
 */
public class MapWindow implements MapContract.View, MapComponentInitializedListener {

    private MapContract.Controller controller;

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
    private JFormattedTextField searchPriceInput;
    private JCheckBox searchHasOwnerCheckbox;
    private JButton searchButton;

    // Right panel components
    private JPanel propertyListPanel;
    private List<PropertyItem> propertyItems;

    // Bottom panel components
    private JLabel statusLabel;

    // Map components
    private Scene scene;
    private GoogleMap map;
    private GoogleMapView mapView;
    private Marker actualPosition;
    private List<MapShape> mapShapes;
    private boolean mapInitialized;

    /**
     * Default constructor
     */
    public MapWindow() {

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
        searchPriceInput = new JFormattedTextField();
        searchHasOwnerCheckbox = new JCheckBox();
        searchButton = new JButton();

        // Right panel components
        propertyListPanel = new JPanel();
        propertyItems = new LinkedList<>();

        // Bottom panel components
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
        searchPriceLabel.setText("Maximal price:");
        searchPriceLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPriceInput.setBorder(new EmptyBorder(10, 10, 10, 10));
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        searchPriceInput.setValue(0);
        searchPriceInput.setFormatterFactory(new DefaultFormatterFactory(formatter));
        searchHasOwnerCheckbox.setText("Only which has owner");
        searchHasOwnerCheckbox.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchButton.setText("Search");
        searchButton.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchButton.setPreferredSize(new Dimension(350, searchButton.getPreferredSize().height));
        searchButton.setMinimumSize(searchButton.getPreferredSize());
        searchButton.setMaximumSize(searchButton.getPreferredSize());
        searchButton.addActionListener(e -> runSwingWorker(new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.filterPropertyList(searchNameInput.getText(), (Integer) searchPriceInput.getValue(), searchHasOwnerCheckbox.isSelected());

                return null;
            }
        }));

        propertyListPanel.setLayout(new BoxLayout(propertyListPanel, BoxLayout.Y_AXIS));

        statusLabel.setText("status");
        if (!App.isDebug()) {
            statusLabel.setVisible(false);
        }

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
            mapShapes = new LinkedList<>();
            mapInitialized = false;
        });
    }

    /**
     * Initialize window menu bar components
     */
    public void initMenuBar() {
        JMenu mMainMenu = new JMenu("Main menu");
        JMenu mHelp = new JMenu("Help");

        JMenuItem mMainMenuPersonsList = new JMenuItem("Persons list");
        mMainMenuPersonsList.addActionListener(actionEvent -> runSwingWorker(new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.getPersons();

                return null;
            }
        }));

        JMenuItem mMainMenuRefresh = new JMenuItem("Refresh");
        mMainMenuRefresh.addActionListener(actionEvent -> runSwingWorker(new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.refresh();

                return null;
            }
        }));

        JMenuItem mMainMenuExecuteSQLFile = new JMenuItem("Execute SQL file");
        mMainMenuExecuteSQLFile.addActionListener(actionEvent -> {
            final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            int returnVal = fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {

                runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.executeSqlFile(fc.getSelectedFile().getAbsolutePath());

                        return null;
                    }
                });
            }
        });

        JMenuItem mMainMenuResetDB = new JMenuItem("Reset database");
        mMainMenuResetDB.addActionListener(actionEvent -> runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.resetDatabase();

                        return null;
                    }
                })
        );

        JMenuItem mMainMenuExit = new JMenuItem("Exit");
        mMainMenuExit.addActionListener(actionEvent -> {
            mainFrame.dispose();
            System.exit(0);
        });

        JMenuItem mHelpAbout = new JMenuItem("About");
        mHelpAbout.addActionListener(actionEvent ->
                // TODO create help message
                JOptionPane.showMessageDialog(null, "Project for PDB course", "About PDB app", JOptionPane.INFORMATION_MESSAGE)
        );

        mMainMenu.add(mMainMenuPersonsList);
        mMainMenu.addSeparator();
        mMainMenu.add(mMainMenuRefresh);
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
     * Callback when map is initialized and ready to use
     */
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
                if (App.isDebug()) {
                    System.out.println("Popup menu item [" + menuEvent.getActionCommand() + "]");
                }

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

                    if (response == JOptionPane.CLOSED_OPTION) {
                        // closed window by cross
                        return;
                    }

                    runSwingWorker(new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Platform.runLater(() -> {

                                Property newProperty = new Property();
                                newProperty.setName("new property");
                                newProperty.setDescription("new property description");

                                Double currentLat = event.getLatLong().getLatitude();
                                Double currentLng = event.getLatLong().getLongitude();

                                switch (response) {
                                    case 0:
                                        newProperty.setType(Property.Type.LAND);
                                        double coordsLand[] = {currentLng, currentLat, currentLng + 0.0001, currentLat,
                                                currentLng + 0.0001, currentLat + 0.0001, currentLng,
                                                currentLat + 0.0001, currentLng, currentLat};
                                        newProperty.setGeometry(JGeometry.createLinearPolygon(coordsLand, 2, 8307));
                                        break;
                                    case 1:
                                        newProperty.setType(Property.Type.HOUSE);
                                        double coordsHouse[] = {currentLng, currentLat, currentLng + 0.0001, currentLat,
                                                currentLng + 0.0001, currentLat + 0.0001, currentLng,
                                                currentLat + 0.0001, currentLng, currentLat};
                                        newProperty.setGeometry(JGeometry.createLinearPolygon(coordsHouse, 2, 8307));
                                        if (App.isDebug()) {
                                            System.out.println(newProperty.getGeometry().getType());
                                        }
                                        break;
                                    case 2:
                                        newProperty.setType(Property.Type.TERRACE_HOUSE);
                                        double coordsTerrace[] = {currentLng, currentLat, currentLng + 0.0001, currentLat + 0.0001};
                                        newProperty.setGeometry(JGeometry.createLinearLineString(coordsTerrace, 2, 8307));
                                        if (App.isDebug()) {
                                            System.out.println(newProperty.getGeometry().getType());
                                        }
                                        break;
                                    case 3:
                                        newProperty.setType(Property.Type.PREFAB);
                                        newProperty.setGeometry(new JGeometry(currentLng, currentLat, currentLng + 0.0001, currentLat + 0.0001, 8307));
                                        break;
                                    case 4:
                                        newProperty.setType(Property.Type.APARTMENT);
                                        newProperty.setGeometry(JGeometry.circle_polygon(currentLng, currentLat, 2, 0.2));
                                        if (App.isDebug()) {
                                            System.out.println(newProperty.getGeometry().getType());
                                            System.out.println(currentLat +" " + currentLng);
                                        }
                                        break;
                                }

                                controller.createProperty(newProperty);
                                controller.getProperty(newProperty);

                            });

                            return null;
                        }
                    });

                } else if (menuEvent.getActionCommand().equalsIgnoreCase("Find nearest property")) {
                    runSwingWorker(new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Platform.runLater(() -> controller.findNearestProperty(position.getLatitude(), position.getLongitude()));

                            return null;
                        }
                    });
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

        mapInitialized = true;

        if (propertyList != null) {
            // show defer property list
            showPropertyList(propertyList);
        }
    }

    /**
     * Set controller of this view
     *
     * @param controller instance of controller
     */
    @Override

    public void setController(MapContract.Controller controller) {
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
     * Show list of all property
     *
     * @param propertyList list of property
     */
    @Override
    public void showPropertyList(List<Property> propertyList) {

        this.propertyList = propertyList;

        if (!mapInitialized) {
            // defer showing of property list if map is not initialized
            // mapInitialized() listener is calling showPropertyList()
            return;
        }

        Platform.runLater(() -> {
            // remove old map shapes
            for (MapShape shape : mapShapes) {
                map.removeMapShape(shape);
            }
            mapShapes.clear();
            // remove old propertyItems
            propertyListPanel.removeAll();
            propertyItems.clear();

            if (App.isDebug()) {
                System.out.println("show list of size: " + propertyList.size());
            }

            // add list of properties to map
            for (Property property : propertyList) {

                final MapShape shape = MapShapeAdapter.jGeometry2MapShape(property.getGeometry(), property.getType());

                if (App.isDebug()) {
                    System.out.println("property: " + property.getName());
                }

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
                    if (App.isDebug()) {
                        System.out.println("Popup menu item [" + event.getActionCommand() + "] was pressed on " + shape.getVariableName());
                    }

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

                            double[] newShapeCoordinates;
                            ShapePointsAdapter shapePointsAdapter = new ShapePointsAdapter();
                            if (shape instanceof com.lynden.gmapsfx.shapes.Polygon) {
                                newShapeCoordinates = shapePointsAdapter.getNewCoordinates(((Polygon) shape).getPath());
                            } else if (shape instanceof com.lynden.gmapsfx.shapes.Rectangle) {
                                newShapeCoordinates = shapePointsAdapter.getNewCoordinates(shape.getBounds());
                            } else if ((shape instanceof com.lynden.gmapsfx.shapes.Polyline)) {
                                newShapeCoordinates = shapePointsAdapter.getNewCoordinates(((Polyline) shape).getPath());
                            } else {
                                newShapeCoordinates = shapePointsAdapter.getNewCoordinates(shape.getBounds());
                            }

                            runSwingWorker(new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    JGeometry newGeometry = JGeometry.createLinearPolygon(newShapeCoordinates, 2, 8307);

                                    if (property.getType() == Property.Type.PREFAB) {
                                        newGeometry = new JGeometry(newShapeCoordinates[0], newShapeCoordinates[1],
                                                newShapeCoordinates[2], newShapeCoordinates[3], 8307);
                                    } else if (property.getType() == Property.Type.HOUSE) {
                                        newGeometry = JGeometry.createLinearPolygon(newShapeCoordinates, 2, 8307);
                                    } else if (property.getType() == Property.Type.TERRACE_HOUSE) {
                                        newGeometry = JGeometry.createLinearLineString(newShapeCoordinates, 2, 8307);
                                    } else if (property.getType() == Property.Type.LAND) {
                                        newGeometry = JGeometry.createLinearPolygon(newShapeCoordinates, 2, 8307);
                                    }

                                    controller.savePropertyGeometry(property, newGeometry);
                                    return null;
                                }
                            });
                        });
                    } else if (event.getActionCommand().equalsIgnoreCase("Find nearest property")) {
                        runSwingWorker(new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                controller.findNearestProperty(property);

                                return null;
                            }
                        });
                    } else if (event.getActionCommand().equalsIgnoreCase("Find adjacent property")) {
                        runSwingWorker(new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                controller.findAdjacentProperty(property);

                                return null;
                            }
                        });
                    } else if (event.getActionCommand().equalsIgnoreCase("Calculate area")) {
                        runSwingWorker(new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                controller.calculateArea(property);

                                return null;
                            }
                        });
                    }
                };

                if (property.getType() == Property.Type.LAND) {
                    // special menu for property of type land
                    JMenuItem findAdjacentItem = new JMenuItem("Find adjacent property");
                    JMenuItem calculateAreaItem = new JMenuItem("Calculate area");
                    propertyPopupMenu.add(findAdjacentItem);
                    propertyPopupMenu.add(calculateAreaItem);
                    findAdjacentItem.addActionListener(menuListener);
                    calculateAreaItem.addActionListener(menuListener);
                }

                propertyEditItem.addActionListener(menuListener);
                propertyMoveItem.addActionListener(menuListener);
                propertySaveItem.addActionListener(menuListener);
                findNearestItem.addActionListener(menuListener);
                propertySaveItem.setEnabled(false);

                propertyPopupMenu.setLabel(property.getName());
                propertyPopupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));

                PropertyItem propertyItem = new PropertyItem(property);
                propertyItem.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                            runSwingWorker(new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    controller.getProperty(property);

                                    return null;
                                }
                            });
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
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                propertyListPanel.add(separator);

                map.addMapShape(shape);
                map.addUIEventHandler(shape, UIEventType.rightclick, (JSObject object) -> {
                    LatLong latLng = new LatLong((JSObject) object.getMember("latLng"));
                    Point point = MapUtil.latLng2Point(latLng, map);

                    if (App.isDebug()) {
                        System.out.println("Point: " + point.getX() + ", " + point.getY());
                    }

                    propertyPopupMenu.show(mapFXPanel, (int) point.getX(), (int) point.getY());
                });
                map.addUIEventHandler(shape, UIEventType.click, (JSObject) -> {
                    if (App.isDebug()) {
                        System.out.println("Property: " + property.getName());
                    }
                    if (!propertyPopupMenu.isShowing()) {
                        runSwingWorker(new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                controller.getProperty(property);

                                return null;
                            }
                        });
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


                // save shapes
                mapShapes.add(shape);
                // save property items
                propertyItems.add(propertyItem);
            }

            propertyListPanel.revalidate();
            propertyListPanel.repaint();
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
