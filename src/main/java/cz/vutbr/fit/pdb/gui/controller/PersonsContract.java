/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.Person;

import java.util.Date;
import java.util.List;

public class PersonsContract {

    public interface View {

        void setController(PersonsContract.Controller controller);

        void showMessage(String message);

        void showError(String error);

        void showOwnersList(List<Person> ownerList);

        void hide();
    }

    public interface Controller {
        void getOwnersListOfDate(Date date_from, Date date_to);

        Integer getOwnersCountOfPropertyDate(Integer id_person,Date date_from, Date date_to);

        Integer getOwnersSumOfPropertyDate(Integer id_person, Date date_from, Date date_to);

        Integer getOwnersDurationOfPropertyDate(Integer id_person,Date date_from, Date date_to);

    }
}
