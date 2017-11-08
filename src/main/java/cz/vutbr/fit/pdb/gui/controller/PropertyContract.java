/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.Property;

import java.io.File;
import java.util.List;

public class PropertyContract {

    public interface View {

        void setController(PropertyContract.Controller controller);

        void showMessage(String message);

        void showError(String error);

        void showProperty(Property property);

        void showPropertyListSimilar(List<Property> propertyList);

        void hide();
    }

    public interface Controller {

        void deleteProperty();

        void savePropertyName(String name);

        void savePropertyDescription(String description);

        void savePropertyCurrentPrice(String currentPrice);

        void savePropertyImage(File file); // TODO image instead of file

        void rotatePropertyImageRight();

        void rotatePropertyImageLeft();

        void getPropertySimilar(Property property);
    }
}
