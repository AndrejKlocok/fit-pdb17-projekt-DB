/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyRepository;
import cz.vutbr.fit.pdb.gui.view.OwnersWindow;
import cz.vutbr.fit.pdb.gui.view.PropertyWindow;
import oracle.spatial.geometry.JGeometry;

import java.io.File;
import java.util.List;

public class MapController implements MapContract.Controller {

    private PropertyRepository repository;

    private MapContract.View view;

    private List<Property> propertyList;


    public MapController(PropertyRepository repository, MapContract.View view) {
        this.repository = repository;
        this.view = view;
        this.propertyList = repository.getPropertyList();

        view.setController(this);
        view.showPropertyList(propertyList);
    }

    @Override
    public void resetDatabase() {
        // TODO executeSqlFile(file);
        view.showMessage("Database initialized");

        // load new property list
        propertyList = repository.getPropertyList();
        view.showPropertyList(propertyList);
    }

    @Override
    public void executeSqlFile(File file) {
        // TODO
    }

    @Override
    public void createProperty(Property property) {
        if(repository.createProperty(property)) {
            // add new property to property list
            propertyList.add(property);

            view.showPropertyList(propertyList);
        } else {
            view.showError("Could not create property");
        }
    }

    @Override
    public void savePropertyGeometry(Property property, JGeometry geometry) {
        // TODO set property geometry

        if (repository.saveProperty(property)) {

            // TODO update property in propertyList
            view.showPropertyList(propertyList);
        } else {
            view.showError("Could not save property");
        }
    }

    @Override
    public void getProperty(Property property) {
        PropertyWindow propertyWindow = new PropertyWindow();
        PropertyRepository propertyRepository = new PropertyRepository(App.getDataSource());
        new PropertyController(propertyRepository, propertyWindow, property.getIdProperty());
    }

    @Override
    public void getOwners() {
        OwnersWindow ownersWindow = new OwnersWindow();
        OwnerRepository ownerRepository = new OwnerRepository(App.getDataSource());
        new OwnersController(ownerRepository, ownersWindow);
    }

    @Override
    public void searchPropertyList(String name, Double price, boolean hasOwner) {
        propertyList = repository.searchPropertyList(name, price, hasOwner);
        view.showPropertyList(propertyList);
    }

    @Override
    public void findNearestProperty(double lat, double lng) {
        Property nearestProperty = repository.getNearestProperty(lat, lng);
        if (nearestProperty == null) {
            view.showError("Could not get nearest property");
            return;
        }
        view.showMessage("nearest of " + lat + ", " + lng + " is " + nearestProperty.getName());

    }

    @Override
    public void findNearestProperty(Property property) {
        Property nearestProperty = repository.getPropertyNearestProperty(property);
        if (nearestProperty == null) {
            view.showError("Could not get nearest property");
            return;
        }
        view.showMessage("nearest of " + property.getName() + " is " + nearestProperty.getName());
    }

    @Override
    public void findAdjacentProperty(Property property) {
        propertyList = repository.getPropertyAdjacentPropertyList(property);
        view.showPropertyList(propertyList);
    }

    @Override
    public void calculateArea(Property property) {
        double area = repository.getPropertyArea(property);
        view.showMessage("area of " + property.getName() + " = " + area);
    }
}
