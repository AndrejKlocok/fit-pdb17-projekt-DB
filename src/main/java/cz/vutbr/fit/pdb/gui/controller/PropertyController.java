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
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import cz.vutbr.fit.pdb.core.repository.GroundPlanRepository;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyPriceRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyRepository;
import cz.vutbr.fit.pdb.gui.view.PropertyWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

/**
 * Controller for detail of property
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class PropertyController implements PropertyContract.Controller {

    private PropertyRepository propertyRepository;

    private GroundPlanRepository groundPlanRepository;

    private PropertyPriceRepository propertyPriceRepository;

    private OwnerRepository ownerRepository;

    private PropertyContract.View view;

    private Property property;

    private boolean filterHasOwner;


    /**
     * Construct controller with repository and view
     *
     * @param propertyRepository      property repository
     * @param groundPlanRepository    ground plan repository
     * @param propertyPriceRepository property price repository
     * @param ownerRepository         owners repository
     * @param view                    view
     * @param property                property
     */
    public PropertyController(PropertyRepository propertyRepository,
                              GroundPlanRepository groundPlanRepository,
                              PropertyPriceRepository propertyPriceRepository,
                              OwnerRepository ownerRepository,
                              PropertyContract.View view, Property property) {
        this.propertyRepository = propertyRepository;
        this.groundPlanRepository = groundPlanRepository;
        this.propertyPriceRepository = propertyPriceRepository;
        this.ownerRepository = ownerRepository;
        this.view = view;
        this.property = property;
        this.filterHasOwner = false;

        view.setController(this);
        propertyRepository.addObserver((observable, o) -> {
            if (!(o instanceof String && o.equals("deleteProperty"))) {
                // only update when notified from other than deleteProperty method
                update();
            }
        });
        groundPlanRepository.addObserver((observable, o) -> update());
        propertyPriceRepository.addObserver(((observable1, o1) -> update()));
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

        property = propertyRepository.getProperty(property);

        if (property == null) {
            view.showError("Could not load property");
        } else {
            List<Property> propertyListSimilar = propertyRepository.getPropertyListSimilarByGroundPlans(property.getGroundPlans(), filterHasOwner);
            view.showProperty(property);
            view.showPropertyListSimilar(propertyListSimilar);
        }
    }

    /**
     * Delete whole property
     */
    @Override
    public void deleteProperty() {
        if (propertyRepository.deleteProperty(property)) {
            view.showMessage("Property deleted");
            view.hide();
        } else {
            view.showError("Could not delete property");
        }
    }

    /**
     * Delete current owner of property (only from property, not whole person)
     */
    @Override
    public void deleteCurrentOwner() {
        if (!ownerRepository.deleteOwner(property.getOwnerCurrent())) {
            view.showError("Could not delete owner from property");
        }
    }

    /**
     * Save owner of property from and to specified date
     *
     * @param person person (owner)
     * @param from   from date
     * @param to     to date
     */
    @Override
    public void saveOwnerFromDateToDate(Person person, Date from, Date to) {
        if (!ownerRepository.saveOwnerOfPropertyFromDateToDate(property, person, from, to)) {
            view.showError("Could not save owner from date to date");
        }
    }

    /**
     * Delete owner of property from and to specified date (only from property, not whole person)
     *
     * @param from from date
     * @param to   to date
     */
    @Override
    public void deleteOwnerFromDateToDate(Date from, Date to) {
        if (!ownerRepository.deleteOwnerOfPropertyFromDateToDate(property, from, to)) {
            view.showError("Could not delete owner from date to date");
        }
    }

    /**
     * Save new name of property
     *
     * @param name new name
     */
    @Override
    public void savePropertyName(String name) {
        property.setName(name);

        if (!propertyRepository.saveProperty(property)) {
            view.showError("Could not save property");
        }
    }

    /**
     * Save new description of property
     *
     * @param description new description
     */
    @Override
    public void savePropertyDescription(String description) {
        property.setDescription(description);

        if (!propertyRepository.saveProperty(property)) {
            view.showError("Could not save property");
        }
    }

    /**
     * Save current property price
     *
     * @param currentPrice new current property price
     */
    @Override
    public void savePropertyCurrentPrice(double currentPrice) {
        PropertyPrice propertyPrice = new PropertyPrice(42, property, currentPrice, new Date(), new Date()); // TODO
        if (!propertyPriceRepository.savePropertyPrice(propertyPrice)) {
            view.showError("Could not save property price");
        }
    }

    /**
     * Save new ground plan of property
     *
     * @param fileName path to file with file name
     */
    @Override
    public void createGroundPlan(String fileName) {
        File file = new File(fileName);
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            GroundPlan newGroundPlan = new GroundPlan();
            newGroundPlan.setImage(fileContent);
            newGroundPlan.setIdProperty(property.getIdProperty());

            if (!groundPlanRepository.createGroundPlan(newGroundPlan)) {
                view.showError("Could not update ground plan");
            }
        } catch (IOException e) {
            view.showError("Could not load selected file");
        }
    }

    /**
     * Delete ground plan of property
     *
     * @param groundPlan ground plan
     */
    @Override
    public void deleteGroundPlan(GroundPlan groundPlan) {
        if (!groundPlanRepository.deleteGroundPlan(groundPlan)) {
            view.showError("Could not delete ground plan");
        }
    }

    /**
     * Rotate ground plan of property to right by 90 degree
     *
     * @param groundPlan ground plan
     */
    @Override
    public void rotateGroundPlanRight(GroundPlan groundPlan) {
        if (!groundPlanRepository.rotateGroundPlanRight(groundPlan)) {
            view.showError("Could not rotate ground plan");
        }
    }

    /**
     * Rotate ground plan of property to left by 90 degree
     *
     * @param groundPlan ground plan
     */
    @Override
    public void rotateGroundPlanLeft(GroundPlan groundPlan) {
        if (!groundPlanRepository.rotateGroundPlanLeft(groundPlan)) {
            view.showError("Could not rotate ground plan");
        }
    }

    /**
     * Get similar property detail
     *
     * @param property property
     */
    @Override
    public void getPropertySimilar(Property property) {
        PropertyWindow propertyWindow = new PropertyWindow();
        new PropertyController(propertyRepository, groundPlanRepository, propertyPriceRepository, ownerRepository, propertyWindow, property);
    }

    /**
     * Filter property list similar
     *
     * @param hasOwner get only property which currently has owner
     */
    @Override
    public void filterPropertyListSimilar(boolean hasOwner) {
        filterHasOwner = hasOwner;
        update();
    }

    /**
     * Calculate average price in given date interval
     *
     * @param from date from
     * @param to   date to
     */
    @Override
    public void calculateAveragePriceFromDateToDate(Date from, Date to) {
        double averagePrice = propertyPriceRepository.getAvgPropertyPrice(property.getIdProperty(), from, to);
        view.showMessage("Average price from " + from + " to " + to + " is " + averagePrice);
    }
}
