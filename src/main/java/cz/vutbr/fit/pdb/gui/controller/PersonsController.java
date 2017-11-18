/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.model.PersonDuration;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;
import cz.vutbr.fit.pdb.core.repository.PersonRepository;

import java.util.*;

public class PersonsController implements PersonsContract.Controller {

    private PersonRepository repository;


    private PersonsContract.View view;

    private List<Person> personList;

    private List<PersonDuration> personDurationList;

    public PersonsController(PersonRepository repository, PersonsContract.View view) {
        this.repository = repository;
        this.view = view;

        view.setController(this);
        repository.addObserver((observable, o) -> update());
        update();
    }

    public void update() {
        //TODO Update zavisly na case vyberu dat
        System.out.println("update " + this.getClass().getSimpleName());
        Date from = new GregorianCalendar(2010, Calendar.JANUARY, 1).getTime();
        Date to = new GregorianCalendar(2017, Calendar.AUGUST, 1).getTime();
        java.sql.Date sql_from = new java.sql.Date(from.getTime());
        java.sql.Date sql_to = new java.sql.Date(to.getTime());

        HashMap<Integer, ArrayList<Integer>> data = repository.getPersonsMapCountSum(sql_from, sql_to);
        personList = repository.getPersonsList();
        view.showOwnersList(personList, data);
    }

    @Override
    public void getOwnersListOfDate(Date date_from, Date date_to) {
        //TODO dva kalendare
        Date from = new GregorianCalendar(2010, Calendar.JANUARY, 1).getTime();
        Date to = new GregorianCalendar(2017, Calendar.AUGUST, 1).getTime();
        java.sql.Date sql_from = new java.sql.Date(from.getTime());
        java.sql.Date sql_to = new java.sql.Date(to.getTime());

        HashMap<Integer, ArrayList<Integer>> data = repository.getPersonsMapCountSum(sql_from, sql_to);
        personList = repository.getPersonsList();
        view.showOwnersList(personList, data);
    }
    @Override
    public void getPersonDurationList(){
        personDurationList = repository.getPersonDurationList();
    }
}
