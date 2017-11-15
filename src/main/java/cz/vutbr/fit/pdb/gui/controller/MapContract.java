/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.Property;
import oracle.spatial.geometry.JGeometry;

import java.util.List;

public class MapContract {

    public interface View {

        void setController(MapContract.Controller controller);

        void showMessage(String message);

        void showError(String error);

        void showPropertyList(List<Property> propertyList);

        void hide();
    }

    public interface Controller {

        void refresh();

        void resetDatabase();

        void executeSqlFile(String fileName);

        void createProperty(Property property);

        void savePropertyGeometry(Property property, JGeometry geometry);

        void getProperty(Property property);

        void getOwners();

        void searchPropertyList(String name, Double price, boolean hasOwner);

        void findNearestProperty(double lat, double lng);

        void findNearestProperty(Property property);

        void findAdjacentProperty(Property property);

        void calculateArea(Property property);
    }
}
