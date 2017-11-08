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
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import cz.vutbr.fit.pdb.core.repository.PropertyRepository;
import cz.vutbr.fit.pdb.gui.view.PropertyWindow;

import java.io.File;
import java.util.Date;
import java.util.List;

public class PropertyController implements PropertyContract.Controller {

    private PropertyRepository repository;

    private PropertyContract.View view;

    private Property property;

    private List<Property> propertyListSimilar;


    public PropertyController(PropertyRepository repository, PropertyContract.View view, int idProperty) {
        this.repository = repository;
        this.view = view;
        this.property = repository.getPropertyById(idProperty);
        this.propertyListSimilar = repository.getPropertyListSimilar(property);

        view.setController(this);

        if (property == null) {
            view.showError("Could not load property");
        } else {
            view.showProperty(property);
            view.showPropertyListSimilar(propertyListSimilar);
        }
    }

    @Override
    public void deleteProperty() {
        if (repository.deleteProperty(property)) {
            view.showMessage("Property deleted");
            view.hide();
        } else {
            view.showError("Could not delete property");
        }
    }

    @Override
    public void savePropertyName(String name) {
        property.setName(name);
        if (repository.saveProperty(property)) {
            view.showProperty(property);
        } else {
            view.showError("Could not save property");
        }
    }

    @Override
    public void savePropertyDescription(String description) {
        property.setDescription(description);
        if (repository.saveProperty(property)) {
            view.showProperty(property);
        } else {
            view.showError("Could not save property");
        }
    }

    @Override
    public void savePropertyCurrentPrice(String currentPrice) {
        List<PropertyPrice> priceHistory = property.getPriceHistory();
        priceHistory.add(new PropertyPrice(42, property.getIdProperty(), Double.parseDouble(currentPrice), new Date(), new Date())); // TODO
        if (repository.saveProperty(property)) {
            view.showProperty(property);
        } else {
            view.showError("Could not save property");
        }
    }

    @Override
    public void savePropertyImage(File file) {
        // TODO set property image
        if (repository.saveProperty(property)) {
            view.showProperty(property);

            // load new property
            property = repository.getProperty(property);
            if (property == null) {
                view.showError("Could not load property");
                return;
            }

            // load new similar property list
            propertyListSimilar = repository.getPropertyListSimilar(property);
            view.showPropertyListSimilar(propertyListSimilar);
        } else {
            view.showError("Could not save property");
        }
    }

    @Override
    public void rotatePropertyImageRight() {
        if (repository.rotatePropertyImageRight(property)) {
            view.showProperty(property);

            // load new property
            property = repository.getProperty(property);
            if (property == null) {
                view.showError("Could not load property");
                return;
            }

            // load new similar property list
            propertyListSimilar = repository.getPropertyListSimilar(property);
            view.showPropertyListSimilar(propertyListSimilar);
        } else {
            view.showError("Could not rotate image");
        }
    }

    @Override
    public void rotatePropertyImageLeft() {
        if (repository.rotatePropertyImageLeft(property)) {
            view.showProperty(property);

            // load new property
            property = repository.getProperty(property);
            if (property == null) {
                view.showError("Could not load property");
                return;
            }

            // load new similar property list
            propertyListSimilar = repository.getPropertyListSimilar(property);
            view.showPropertyListSimilar(propertyListSimilar);
        } else {
            view.showError("Could not rotate image");
        }
    }

    @Override
    public void getPropertySimilar(Property property) {
        PropertyWindow propertyWindow = new PropertyWindow();
        PropertyRepository propertyRepository = new PropertyRepository(App.getDataSource());
        new PropertyController(propertyRepository, propertyWindow, property.getIdProperty());
    }
}
