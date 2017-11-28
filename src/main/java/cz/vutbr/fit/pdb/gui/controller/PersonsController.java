/*
 * Copyright (C) 2017 VUT FIT PDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;
import cz.vutbr.fit.pdb.core.repository.PersonRepository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for list of all persons
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class PersonsController implements PersonsContract.Controller {

    private PersonRepository personRepository;

    private OwnerRepository ownerRepository;

    private PersonsContract.View view;

    private List<Person> personList;

    private Date filterDateFrom;

    private Date filterDateTo;


    /**
     * Construct controller with personRepository and view
     *
     * @param personRepository person repository
     * @param ownerRepository  owner repository
     * @param view             view
     */
    public PersonsController(PersonRepository personRepository,
                             OwnerRepository ownerRepository,
                             PersonsContract.View view) {
        this.personRepository = personRepository;
        this.ownerRepository = ownerRepository;
        this.view = view;

        view.setController(this);
        personRepository.addObserver((observable, o) -> update());
        ownerRepository.addObserver((observable, o) -> update());

        update();
    }

    /**
     * Callback on data change
     */
    public void update() {
        if (App.isDebug()) {
            System.out.println("update " + this.getClass().getSimpleName());
        }

        if (filterDateFrom == null && filterDateTo == null) {
            // get current valid persons
            List<Owner> ownersList = ownerRepository.getOwnersList();
            personList = new LinkedList<>();
            personList.addAll(ownersList.stream().map(Owner::getPerson).collect(Collectors.toList()));
            view.showPersonsList(personList);
        } else {
            // get filtered valid persons
            List<Owner> ownersList = ownerRepository.getOwnersListOfFromToDate(filterDateFrom, filterDateTo);
            personList = new LinkedList<>();
            personList.addAll(ownersList.stream().map(Owner::getPerson).collect(Collectors.toList()));
            view.showPersonsList(personList);
        }
    }

    /**
     * Filter valid persons in specified interval and display them in view
     *
     * @param dateFrom date
     * @param dateTo   date
     */
    @Override
    public void filterPersonsList(Date dateFrom, Date dateTo) {
        filterDateFrom = dateFrom;
        filterDateTo = dateTo;
        update();
    }

    /**
     * Get count of property of one person of specified date
     *
     * @param person   person
     * @param dateFrom date
     * @param dateTo   date
     */
    @Override
    public Integer getPersonsCountOfPropertyDate(Person person, Date dateFrom, Date dateTo) {
        return this.personRepository.getPersonPropertyCount(person.getIdPerson(), dateFrom, dateTo);
        // TODO no return but view method ?
    }

    /**
     * Get are sum of property area of one person of specified date
     *
     * @param person   person
     * @param dateFrom date
     * @param dateTo   date
     * @return sum of property area
     */
    @Override
    public Integer getPersonsSumOfPropertyDate(Person person, Date dateFrom, Date dateTo) {
        return this.personRepository.getPersonPropertySum(person.getIdPerson(), dateFrom, dateTo);
    }

    /**
     * Get duration of property of one person of specified date
     *
     * @param person   person
     * @param dateFrom date
     * @param dateTo   date
     * @return duration of property in date interval
     */
    @Override
    public Integer getPersonsDurationOfPropertyDate(Person person, Date dateFrom, Date dateTo) {
        return this.personRepository.getPersonDuration(person.getIdPerson(), dateFrom, dateTo);
    }

}
