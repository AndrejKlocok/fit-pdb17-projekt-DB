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

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.GroundPlan;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.ScriptRunner;
import cz.vutbr.fit.pdb.core.repository.*;
import cz.vutbr.fit.pdb.gui.view.PersonsWindow;
import cz.vutbr.fit.pdb.gui.view.PropertyWindow;
import oracle.spatial.geometry.JGeometry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;

/**
 * Controller for list of all property
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class MapController extends Observable implements MapContract.Controller {

    public static final String DATABASE_FOLDER = "db";
    public static final String DATABASE_INIT_FILE = "database-init.sql";
    public static final String DATABASE_INIT_TEMPORAL_FILE = "database-init_temporal.sql";

    private PropertyRepository propertyRepository;

    private GroundPlanRepository groundPlanRepository;

    private PropertyPriceRepository propertyPriceRepository;

    private OwnerRepository ownerRepository;

    private PersonRepository personRepository;

    private MapContract.View view;

    private List<Property> propertyList;

    private String filterName;

    private double filterMaxPrice;

    private boolean filterHasOwner;


    /**
     * Construct controller with repository and view
     *
     * @param propertyRepository      property repository
     * @param groundPlanRepository    ground plan repository
     * @param propertyPriceRepository property price repository
     * @param ownerRepository         owner repository
     * @param view                    view
     */
    public MapController(PropertyRepository propertyRepository,
                         GroundPlanRepository groundPlanRepository,
                         PropertyPriceRepository propertyPriceRepository,
                         OwnerRepository ownerRepository,
                         PersonRepository personRepository,
                         MapContract.View view) {
        this.propertyRepository = propertyRepository;
        this.groundPlanRepository = groundPlanRepository;
        this.propertyPriceRepository = propertyPriceRepository;
        this.ownerRepository = ownerRepository;
        this.personRepository = personRepository;
        this.view = view;
        this.filterName = "";
        this.filterMaxPrice = 0d;
        this.filterHasOwner = false;

        view.setController(this);
        propertyRepository.addObserver((observable, o) -> update());
        groundPlanRepository.addObserver((observable, o) -> update());
        propertyPriceRepository.addObserver((observable, o) -> update());
        ownerRepository.addObserver((observable, o) -> update());

        update();
    }

    /**
     * Callback on data change
     */
    public void update() {
        if (App.isDebug()) {
            System.out.println("update " + this.getClass().getSimpleName());
        }

        if (filterName.isEmpty() && filterMaxPrice == 0d && !filterHasOwner) {
            // there is no filter set, so load all property
            if (App.isDebug()) {
                System.out.println("no filter");
            }
            propertyList = propertyRepository.getPropertyList();
        } else {
            // filter is set
            if (App.isDebug()) {
                System.out.println("filter " + filterName + " , " + filterMaxPrice + " , " + filterHasOwner);
            }
            propertyList = propertyRepository.searchPropertyList(filterName, filterMaxPrice, filterHasOwner);
        }
        view.showPropertyList(propertyList);
    }

    /**
     * Reload data and redisplay them in view
     */
    @Override
    public void refresh() {
        update();
    }

    /**
     * Reinitialize database by project default SQL file
     */
    @Override
    public void resetDatabase() {
        try {
            Connection connection = App.getDataSource().getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
            if (App.isDebug()) {
                scriptRunner.setLogWriter(new PrintWriter(System.out));
            } else {
                scriptRunner.setLogWriter(null);
            }
            File file = new File(DATABASE_FOLDER + "/" + DATABASE_INIT_FILE);
            scriptRunner.setDelimiter(";", false);
            scriptRunner.runScript(new FileReader(file));
            scriptRunner.setDelimiter("/", false);
            File fileTemporal = new File(DATABASE_FOLDER + "/" + DATABASE_INIT_TEMPORAL_FILE);
            scriptRunner.runScript(new FileReader(fileTemporal));

            File databaseFolder = new File(DATABASE_FOLDER);
            File[] matchingFiles = databaseFolder.listFiles((dir, name) -> name.startsWith("property_") && name.endsWith("jpg"));

            for (File propertyGroundPlanFile : matchingFiles) {

                if (App.isDebug()) {
                    System.out.println("uploading file " + propertyGroundPlanFile.getName());
                }

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

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            view.showError("Could not execute sql file");
        } catch (IOException exception) {
            System.err.println("Error " + exception.getMessage());

            view.showError("Could not load selected file");
        }
    }

    /**
     * Execute SQL file
     * Note in file must be SQL commands separated by ";"
     *
     * @param fileName path to file with file name
     */
    @Override
    public void executeSqlFile(String fileName) {
        File file = new File(fileName);
        try {
            Connection connection = App.getDataSource().getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
            if (App.isDebug()) {
                scriptRunner.setLogWriter(new PrintWriter(System.out));
            } else {
                scriptRunner.setLogWriter(null);
            }
            scriptRunner.runScript(new FileReader(file));

            view.showMessage("File executed");

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            view.showError("Could not execute sql file");
        } catch (IOException e) {
            view.showError("Could not load selected file");
        }
    }

    /**
     * Save new property
     *
     * @param property property
     */
    @Override
    public void createProperty(Property property) {
        if (!propertyRepository.createProperty(property)) {
            view.showError("Could not create property");
        }
    }

    /**
     * Save property geometry
     *
     * @param property property
     * @param geometry JGeometry
     */
    @Override
    public void savePropertyGeometry(Property property, JGeometry geometry) {
        property.setGeometry(geometry);
        if (!propertyRepository.saveProperty(property)) {
            view.showError("Could not save property");
        }
    }

    /**
     * Get detail of property
     *
     * @param property property
     */
    @Override
    public void getProperty(Property property) {
        PropertyWindow propertyWindow = new PropertyWindow();

        //FIXME after delete property and than create new property there is random error message
        //FIXME: when property id = 0
        if(property.getIdProperty() == 0) {
            property.setIdProperty(propertyRepository.lastInsertedId());
        }
        new PropertyController(propertyRepository, groundPlanRepository, propertyPriceRepository, ownerRepository, propertyWindow, property);
    }

    /**
     * Get all owners
     */
    @Override
    public void getPersons() {
        PersonsWindow personsWindow = new PersonsWindow();
        new PersonsController(personRepository, personsWindow);
    }

    /**
     * Filter property list
     *
     * @param name     name
     * @param maxPrice maximal price
     * @param hasOwner get only property which currently has owner
     */
    @Override
    public void filterPropertyList(String name, double maxPrice, boolean hasOwner) {
        filterName = name;
        filterMaxPrice = maxPrice;
        filterHasOwner = hasOwner;
        update();
    }

    /**
     * Find nearest property to given latitude and longitude
     *
     * @param lat latitude
     * @param lng longitude
     */
    @Override
    public void findNearestProperty(double lat, double lng) {
        Property nearestProperty = propertyRepository.getNearestProperty(lat, lng);

        if (nearestProperty == null) {
            view.showError("Could not get nearest property");
            return;
        }
        view.showMessage("Nearest property of " + lat + ", " + lng + " is " + nearestProperty.getName());
    }

    /**
     * Find nearest property of given property
     *
     * @param property property
     */
    @Override
    public void findNearestProperty(Property property) {
        Property nearestProperty = propertyRepository.getPropertyNearestProperty(property);
        if (nearestProperty == null) {
            view.showError("Could not get nearest property");
            return;
        }
        view.showMessage("nearest of " + property.getName() + " is " + nearestProperty.getName());
    }

    /**
     * Find adjacent property of given property
     *
     * @param property property
     */
    @Override
    public void findAdjacentProperty(Property property) {
        propertyList = propertyRepository.getPropertyAdjacentPropertyList(property);
        view.showPropertyList(propertyList);
    }

    /**
     * Calculate area of given property
     *
     * @param property property
     */
    @Override
    public void calculateArea(Property property) {
        if (property.getType() == Property.Type.LAND) {
            double area = propertyRepository.getPropertyArea(property);
            view.showMessage("Area of " + property.getName() + " = " + area);
        } else {
            view.showError("You cannot calculate area of property of this type");
        }
    }

    @Override
    public void calculateLength(Property property) {
        if (property.getType() == Property.Type.LAND) {
            double area = propertyRepository.getPropertyLength(property);
            view.showMessage("Length of " + property.getName() + " = " + area);
        } else {
            view.showError("You cannot calculate Length of property of this type");
        }
    }
}
