/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.Owner;

import java.util.Date;
import java.util.List;

public class OwnersContract {

    public interface View {

        void setController(OwnersContract.Controller controller);

        void showMessage(String message);

        void showError(String error);

        void showOwnersList(List<Owner> ownerList);

        void hide();
    }

    public interface Controller {
        void getOwnersListOfDate(Date date);
    }
}
