/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.GroundPlan;
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

public class PropertyController implements PropertyContract.Controller {

    private PropertyRepository propertyRepository;

    private GroundPlanRepository groundPlanRepository;

    private PropertyPriceRepository propertyPriceRepository;

    private OwnerRepository ownerRepository;

    private PropertyContract.View view;

    private Property property;


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

        view.setController(this);
        propertyRepository.addObserver((observable, o) -> update());
        groundPlanRepository.addObserver((observable, o) -> update());
        propertyPriceRepository.addObserver(((observable1, o1) -> update()));
        ownerRepository.addObserver((observable, o) -> update());

        update();
    }

    public void update() {
        System.out.println("update " + this.getClass().getSimpleName());

        property = propertyRepository.getProperty(property);
        List<Property> propertyListSimilar = propertyRepository.getPropertyListSimilarByGroundPlans(property.getGroundPlans());

        if (property == null) {
            view.showError("Could not load property");
        } else {
            view.showProperty(property);
            view.showPropertyListSimilar(propertyListSimilar);
        }
    }

    @Override
    public void deleteProperty() {
        if (propertyRepository.deleteProperty(property)) {
            view.showMessage("Property deleted");
            view.hide();
        } else {
            view.showError("Could not delete property");
        }
    }

    @Override
    public void deleteOwner() {
        if (!ownerRepository.deleteOwner(property.getOwnerCurrent())) {
            view.showError("Could not delete owner from property");
        }
    }

    @Override
    public void savePropertyName(String name) {
        if (!propertyRepository.saveProperty(property)) {
            view.showError("Could not save property");
        }
    }

    @Override
    public void savePropertyDescription(String description) {
        if (!propertyRepository.saveProperty(property)) {
            view.showError("Could not save property");
        }
    }

    @Override
    public void savePropertyCurrentPrice(String currentPrice) {
        PropertyPrice propertyPrice = new PropertyPrice(42, property, Double.parseDouble(currentPrice), new Date(), new Date()); // TODO
        if (!propertyPriceRepository.savePropertyPrice(propertyPrice)) {
            view.showError("Could not save property price");
        }
    }

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

    @Override
    public void deleteGroundPlan(GroundPlan groundPlan) {
        if (!groundPlanRepository.deleteGroundPlan(groundPlan)) {
            view.showError("Could not delete ground plan");
        }
    }

    @Override
    public void rotateGroundPlanRight(GroundPlan groundPlan) {
        if (!groundPlanRepository.rotateGroundPlanRight(groundPlan)) {
            view.showError("Could not rotate ground plan");
        }
    }

    @Override
    public void rotateGroundPlanLeft(GroundPlan groundPlan) {
        if (!groundPlanRepository.rotateGroundPlanLeft(groundPlan)) {
            view.showError("Could not rotate ground plan");
        }
    }

    @Override
    public void getPropertySimilar(Property property) {
        PropertyWindow propertyWindow = new PropertyWindow();
        new PropertyController(propertyRepository, groundPlanRepository, propertyPriceRepository, ownerRepository, propertyWindow, property);
    }
}
