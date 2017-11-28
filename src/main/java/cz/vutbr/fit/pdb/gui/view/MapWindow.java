/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.view;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polygon;
import com.lynden.gmapsfx.shapes.Polyline;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.gui.adapters.ShapePointsAdapter;
import cz.vutbr.fit.pdb.gui.controller.MapContract;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import netscape.javascript.JSObject;
import oracle.spatial.geometry.JGeometry;
import sun.awt.Symbol;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private JTextField searchPriceInput;
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

    public String actualPositionString;

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
        searchPriceInput = new JTextField();
        searchHasOwnerCheckbox = new JCheckBox();
        searchButton = new JButton();

        // Right panel components
        propertyListPanel = new JPanel();
        propertyItems = new LinkedList<>();

        // Bottom panel components
        statusLabel = new JLabel();

        showAsync();
    }

    public String getActualPositionString() {
        return this.actualPositionString;
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
                runSwingWorker(new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.searchPropertyList(searchNameInput.getText(), Double.parseDouble(searchPriceInput.getText()), searchHasOwnerCheckbox.isSelected());

                        return null;
                    }
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
            mapShapes = new LinkedList<>();
            mapInitialized = false;
        });
    }

    public void initMenuBar() {
        JMenu mMainMenu = new JMenu("Main menu");
        JMenu mHelp = new JMenu("Help");

        JMenuItem mMainMenuOwnerList = new JMenuItem("Owners list");
        mMainMenuOwnerList.addActionListener(actionEvent -> runSwingWorker(new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.getOwners();

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

        mMainMenu.add(mMainMenuOwnerList);
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

            this.actualPositionString = event.getLatLong().toString();

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
                    newProperty.setName("new property");
                    newProperty.setDescription("new property description");

                    String actualMarkerPosition = getActualPositionString();
                    System.out.println("actual marker postion "+ actualMarkerPosition);

                    Pattern pattern = Pattern.compile("[+-]?([0-9]*[.][0-9]*)+");
                    Matcher matcher = pattern.matcher(actualMarkerPosition);
                    Double currentLat = 0.0;
                    Double currentLng = 0.0;

                    int count = 0;
                    while(matcher.find()) {
                        if(count == 0) {
                            currentLat = Double.parseDouble(matcher.group(0));
                        } else if(count == 1)
                        currentLng = Double.parseDouble(matcher.group(1));
                        count++;
                    }
                    System.out.println("Lat " + currentLat);
                    System.out.println("Lng " + currentLng);

                    switch (response) {
                        case 0:
                            newProperty.setType(Property.Type.LAND);
                            double coordsLand [] = {currentLng, currentLat, currentLng + 0.0001, currentLat,
                                    currentLng + 0.0001, currentLat + 0.0001, currentLng,
                                    currentLat + 0.0001, currentLng, currentLat};
                            newProperty.setGeometry(JGeometry.createLinearPolygon(coordsLand, 2, 8307 ));
                            break;
                        case 1:
                            newProperty.setType(Property.Type.HOUSE);
                            double coordsHouse [] = {currentLng, currentLat, currentLng + 0.0001, currentLat,
                                                currentLng + 0.0001, currentLat + 0.0001, currentLng,
                                                currentLat + 0.0001, currentLng, currentLat};
                            newProperty.setGeometry(JGeometry.createLinearPolygon(coordsHouse, 2, 8307 ));
                            System.out.println(newProperty.getGeometry().getType());
                            break;
                        case 2:
                            newProperty.setType(Property.Type.TERRACE_HOUSE);
                            double coordsTerrace [] = {currentLng, currentLat, currentLng+0.0001, currentLat+0.0001};
                            newProperty.setGeometry(JGeometry.createLinearLineString(coordsTerrace, 2, 8307 ));
                            System.out.println(newProperty.getGeometry().getType());
                            break;
                        case 3:
                            newProperty.setType(Property.Type.PREFAB);
                            //double coordsPrefab [] = {currentLng, currentLat, currentLng + 0.0001, currentLat+0.0001};
                            newProperty.setGeometry(new JGeometry(currentLng, currentLat, currentLng + 0.0001, currentLat+0.0001, 8307 ));
                            break;
                        case 4:
                            newProperty.setType(Property.Type.APARTMENT);
                            //49.192384, 16.608537
                            //currentLng, currentLat+0.004
                            //currentLng, currentLat-0.004
                            //currentLng+0.004, currentLat
                            JGeometry j = JGeometry.createCircle(16.608989, 49.187766, 16.612989, 49.191766,
                                    16.608989, 49.195766, 8307);

                            for(int i =0; i < j.getOrdinatesArray().length; i++)
                            {
                                System.out.println("ORD " + j.getOrdinatesArray()[i]);
                            }
                            for(int i =0; i < j.getElemInfo().length; i++)
                            {
                                System.out.println("ELEM " + j.getElemInfo()[i]);
                            }
                            newProperty.setGeometry(JGeometry.createCircle(currentLng, currentLat,
                                    20, 8307));
                            break;
                    }

                    runSwingWorker(new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Platform.runLater(() -> {
                                newProperty.setIdProperty(controller.getNewIdForProperty());
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



    @Override
    public void setController(MapContract.Controller controller) {
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

            System.out.println("show list of size: " + propertyList.size());

            // add list of properties to map
            for (Property property : propertyList) {

                System.out.println("property: " + property.getName());
                final MapShape shape = MapShapeAdapter.jGeometry2MapShape(property.getGeometry(), property.getType());

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

                            double [] newShapeCoordinates;
                            ShapePointsAdapter shapePointsAdapter =  new ShapePointsAdapter();
                            if(shape instanceof com.lynden.gmapsfx.shapes.Polygon) {
                                newShapeCoordinates = shapePointsAdapter.getNewCoordinates(((Polygon) shape).getPath());
                            } else if(shape instanceof com.lynden.gmapsfx.shapes.Rectangle) {
                                newShapeCoordinates = shapePointsAdapter.getNewCoordinates(shape.getBounds());
                            } else if((shape instanceof com.lynden.gmapsfx.shapes.Polyline)) {
                                newShapeCoordinates =  shapePointsAdapter.getNewCoordinates(((Polyline) shape).getPath());
                            } else {
                                newShapeCoordinates = shapePointsAdapter.getNewCoordinates(shape.getBounds());
                            }

                            runSwingWorker(new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    JGeometry newGeometry = JGeometry.createLinearPolygon(newShapeCoordinates, 2, 8307);;
                                    if(property.getType() == Property.Type.PREFAB) {
                                        newGeometry = new JGeometry(newShapeCoordinates[0], newShapeCoordinates[1],
                                                newShapeCoordinates[2], newShapeCoordinates[3], 8307);
                                    } else if(property.getType() == Property.Type.APARTMENT) {

                                    } else if(property.getType() == Property.Type.HOUSE) {
                                        newGeometry = JGeometry.createLinearPolygon(newShapeCoordinates, 2, 8307);
                                    } else if(property.getType() == Property.Type.TERRACE_HOUSE) {
                                        newGeometry = JGeometry.createLinearLineString(newShapeCoordinates, 2,8307);
                                    } else if(property.getType() == Property.Type.LAND) {
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

                    System.out.println("Point: " + point.getX() + ", " + point.getY());

                    propertyPopupMenu.show(mapFXPanel, (int) point.getX(), (int) point.getY());
                });
                map.addUIEventHandler(shape, UIEventType.click, (JSObject) -> {
                    System.out.println("Property: " + property.getName());
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

    @Override
    public void hide() {
        onExit();
    }
}
