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
        //TODO Update zavisly na case vyberu dat -> spravit property binding s get/set na kalendaroch
        System.out.println("update " + this.getClass().getSimpleName());
        Date from = new GregorianCalendar(2010, Calendar.JANUARY, 1).getTime();
        Date to = new GregorianCalendar(2017, Calendar.AUGUST, 1).getTime();

        HashMap<Integer, ArrayList<Integer>> data = repository.getPersonsMapCountSum(from, to);
        personList = repository.getPersonsList();
        view.showOwnersList(personList, data);

        personDurationList = repository.getPersonDurationList();
        view.showOwnersDurationList(personDurationList);
    }

    @Override
    public void getOwnersListOfDate(Date date_from, Date date_to) {
        //TODO dva kalendare
        Date from = new GregorianCalendar(2010, Calendar.JANUARY, 1).getTime();
        Date to = new GregorianCalendar(2017, Calendar.AUGUST, 1).getTime();

        HashMap<Integer, ArrayList<Integer>> data = repository.getPersonsMapCountSum(from, to);
        personList = repository.getPersonsList();
        view.showOwnersList(personList, data);
    }
    @Override
    public void getPersonDurationList(){
        //Tests
        System.out.println("\n Size" + personDurationList.size());
        for (PersonDuration pd:personDurationList) {
            System.out.println(pd.getPerson().getLastName() + " " + pd.getDuration() + " " + pd.getPropertyCount() );
        }
        OwnerRepository ownerRepository = new OwnerRepository(repository.getDataSource());

        //all
        List<Owner> ownerList = ownerRepository.getOwnersList(1);
        System.out.println("\n Size" + ownerList.size());
        for (Owner o:ownerList) {
            System.out.println(o.getPerson().getLastName() + " " + o.getProperty().getName() +" " + o.getValidFrom() + " " + o.getValidTo() );
        }
        //time
        Date from = new GregorianCalendar(2017, Calendar.JANUARY, 1).getTime();
        Date to = new GregorianCalendar(2017, Calendar.NOVEMBER, 12).getTime();
        ownerList = ownerRepository.getOwnersListOfFromToDate(1, from, to);
        System.out.println("\n Size" + ownerList.size());
        for (Owner o:ownerList) {
            System.out.println(o.getPerson().getLastName() + " " + o.getProperty().getName() +" " + o.getValidFrom() + " " + o.getValidTo() );
        }
        //from
        ownerList = ownerRepository.getOwnersListOfFromDate(1, from);
        System.out.println("\n FROM_Size" + ownerList.size());
        for (Owner o:ownerList) {
            System.out.println(o.getPerson().getLastName() + " " + o.getProperty().getName() +" " + o.getValidFrom() + " " + o.getValidTo() );
        }
        //to
        Date to_1 = new GregorianCalendar(2017, Calendar.JUNE, 10).getTime();
        ownerList = ownerRepository.getOwnersListOfToDate(1,to_1);
        System.out.println("\n TO_Size" + ownerList.size());
        for (Owner o:ownerList) {
            System.out.println(o.getPerson().getLastName() + " " + o.getProperty().getName() +" " + o.getValidFrom() + " " + o.getValidTo() );
        }
    }
}
