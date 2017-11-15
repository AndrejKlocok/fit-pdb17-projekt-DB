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

        void deleteOwner();

        void savePropertyName(String name);

        void savePropertyDescription(String description);

        void savePropertyCurrentPrice(String currentPrice);

        void createGroundPlan(String fileName);

        void deleteGroundPlan(GroundPlan groundPlan);

        void rotateGroundPlanRight(GroundPlan groundPlan);

        void rotateGroundPlanLeft(GroundPlan groundPlan);

        void getPropertySimilar(Property property);
    }
}
