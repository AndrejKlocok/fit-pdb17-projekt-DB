/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;
import cz.vutbr.fit.pdb.core.repository.PersonRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyPriceRepository;

import java.util.*;

public class PersonsController implements PersonsContract.Controller {

    private PersonRepository repository;


    private PersonsContract.View view;

    private List<Person> personList;


    public PersonsController(PersonRepository repository, PersonsContract.View view) {
        this.repository = repository;
        this.view = view;

        view.setController(this);
        repository.addObserver((observable, o) -> update());
        update();
    }

    public void update() {
        //TODO Update zavisly na case vyberu dat -> spravit property binding s get/set na kalendaroch
        System.out.println("update " + this.getClass().getSimpleName());
        personList = repository.getPersonsList();
        view.showOwnersList(personList);

    }

    @Override
    public void getOwnersListOfDate(Date date_from, Date date_to) {
        personList = repository.getPersonsList();
        view.showOwnersList(personList);
    }

    @Override
    public Integer getOwnersCountOfPropertyDate(Integer id_person, Date date_from, Date date_to) {
        return this.repository.getPersonPropertyCount(id_person, date_from, date_to);

    }

    @Override
    public Integer getOwnersSumOfPropertyDate(Integer id_person, Date date_from, Date date_to) {
        return this.repository.getPersonPropertySum(id_person, date_from, date_to);
    }

    @Override
    public Integer getOwnersDurationOfPropertyDate(Integer id_person, Date date_from, Date date_to) {
        return this.repository.getPersonDuration(id_person, date_from, date_to);
    }

}
