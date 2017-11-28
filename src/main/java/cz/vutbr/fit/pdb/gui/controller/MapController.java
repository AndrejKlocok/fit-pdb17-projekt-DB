/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.GroundPlan;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.ScriptRunner;
import cz.vutbr.fit.pdb.core.repository.GroundPlanRepository;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyPriceRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyRepository;
import cz.vutbr.fit.pdb.gui.view.OwnersWindow;
import cz.vutbr.fit.pdb.gui.view.PropertyWindow;
import oracle.spatial.geometry.JGeometry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;

public class MapController extends Observable implements MapContract.Controller {

    public static final String DATABASE_FOLDER = "db";
    public static final String DATABASE_INIT_FILE = "database-init.sql";
    public static final String DATABASE_INIT_TEMPORAL_FILE = "database-init_temporal.sql";

    private PropertyRepository propertyRepository;

    private GroundPlanRepository groundPlanRepository;

    private PropertyPriceRepository propertyPriceRepository;

    private OwnerRepository ownerRepository;

    private MapContract.View view;

    private List<Property> propertyList;


    public MapController(PropertyRepository propertyRepository,
                         GroundPlanRepository groundPlanRepository,
                         PropertyPriceRepository propertyPriceRepository,
                         OwnerRepository ownerRepository,
                         MapContract.View view) {
        this.propertyRepository = propertyRepository;
        this.groundPlanRepository = groundPlanRepository;
        this.propertyPriceRepository = propertyPriceRepository;
        this.ownerRepository = ownerRepository;
        this.view = view;

        view.setController(this);
        propertyRepository.addObserver((observable, o) -> update());
        groundPlanRepository.addObserver((observable, o) -> update());
        propertyPriceRepository.addObserver((observable, o) -> update());
        ownerRepository.addObserver((observable, o) -> update());

        update();
    }

    public void update() {
        System.out.println("update " + this.getClass().getSimpleName());

        propertyList = propertyRepository.getPropertyList();
        view.showPropertyList(propertyList);
    }

    @Override
    public void refresh() {
        update();
    }

    @Override
    public void resetDatabase() {
        try {
            Connection connection = App.getDataSource().getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
            File file = new File(DATABASE_FOLDER + "/" + DATABASE_INIT_FILE);
            scriptRunner.setDelimiter(";", false);
            scriptRunner.runScript(new FileReader(file));
            scriptRunner.setDelimiter("/", false);
            File fileTemporal = new File(DATABASE_FOLDER + "/" + DATABASE_INIT_TEMPORAL_FILE);
            scriptRunner.runScript(new FileReader(fileTemporal));

            File databaseFolder = new File(DATABASE_FOLDER);
            File[] matchingFiles = databaseFolder.listFiles((dir, name) -> name.startsWith("property_") && name.endsWith("jpg"));

            for (File propertyGroundPlanFile : matchingFiles) {

                System.out.println("uploading file " + propertyGroundPlanFile.getName());

                byte[] fileContent = Files.readAllBytes(propertyGroundPlanFile.toPath());
                int propertyId = Integer.parseInt(propertyGroundPlanFile.getName().replaceAll("\\D+", ""));

                GroundPlan newGroundPlan = new GroundPlan();
                newGroundPlan.setImage(fileContent);
                newGroundPlan.setIdProperty(propertyId);

                if (!groundPlanRepository.createGroundPlan(newGroundPlan)) {
                    System.err.println("Could not update ground plan for " + propertyId);
                }
            }

            view.showMessage("Database initialized");

            // load new property list
            update();
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            view.showError("Could not execute sql file");
        } catch (IOException e) {
            view.showError("Could not load selected file");
        }
    }

    @Override
    public void executeSqlFile(String fileName) {
        File file = new File(fileName);
        try {
            Connection connection = App.getDataSource().getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
            scriptRunner.runScript(new FileReader(file));

            view.showMessage("File executed");

            // load new property list
            update();
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            view.showError("Could not execute sql file");
        } catch (IOException e) {
            view.showError("Could not load selected file");
        }
    }

    @Override
    public void createProperty(Property property) {
        if (!propertyRepository.createProperty(property)) {
            view.showError("Could not create property");
        }
    }

    @Override
    public void savePropertyGeometry(Property property, JGeometry geometry) {
        property.setGeometry(geometry);
        if (!propertyRepository.saveProperty(property)) {
            view.showError("Could not save property");
        }
    }

    @Override
    public void getProperty(Property property) {
        PropertyWindow propertyWindow = new PropertyWindow();
        new PropertyController(propertyRepository, groundPlanRepository, propertyPriceRepository, ownerRepository, propertyWindow, property);
    }

    @Override
    public void getOwners() {
        OwnersWindow ownersWindow = new OwnersWindow();
        new OwnersController(ownerRepository, ownersWindow);
    }

    @Override
    public void searchPropertyList(String name, Double price, boolean hasOwner) {
        propertyList = propertyRepository.searchPropertyList(name, price, hasOwner);
        view.showPropertyList(propertyList);
    }

    @Override
    public void findNearestProperty(double lat, double lng) {
        Property nearestProperty = propertyRepository.getNearestProperty(lat, lng);

        if (nearestProperty == null) {
            view.showError("Could not get nearest property");
            return;
        }
        view.showMessage("nearest of " + lat + ", " + lng + " is " + nearestProperty.getName());

    }

    @Override
    public void findNearestProperty(Property property) {
        Property nearestProperty = propertyRepository.getPropertyNearestProperty(property);
        if (nearestProperty == null) {
            view.showError("Could not get nearest property");
            return;
        }
        view.showMessage("nearest of " + property.getName() + " is " + nearestProperty.getName());
    }

    @Override
    public void findAdjacentProperty(Property property) {
        propertyList = propertyRepository.getPropertyAdjacentPropertyList(property);
        view.showPropertyList(propertyList);
    }

    @Override
    public void calculateArea(Property property) {
        double area = propertyRepository.getPropertyArea(property);
        view.showMessage("area of " + property.getName() + " = " + area);
    }

    @Override
    public int getNewIdForProperty() {
        return propertyRepository.getNewIdForProperty();
    }
}
