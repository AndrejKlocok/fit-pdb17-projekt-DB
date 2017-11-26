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
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;

import java.util.Date;
import java.util.List;

/**
 * Controller for list of all owners
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class OwnersController implements OwnersContract.Controller {

    private OwnerRepository repository;

    private OwnersContract.View view;

    private List<Owner> ownersList;


    /**
     * Construct controller with repository and view
     *
     * @param repository repository
     * @param view       view
     */
    public OwnersController(OwnerRepository repository, OwnersContract.View view) {
        this.repository = repository;
        this.view = view;

        view.setController(this);
        repository.addObserver((observable, o) -> update());

        update();
    }

    /**
     * Callback on data change
     */
    public void update() {
        if (App.isDebug()) {
            System.out.println("update " + this.getClass().getSimpleName());
        }

        ownersList = repository.getOwnersList();
        view.showOwnersList(ownersList);
    }

    /**
     * Get valid owners of specified date and display them in view
     *
     * @param date date
     */
    @Override
    public void getOwnersListOfDate(Date date) {
        ownersList = repository.getOwnersListOfDate(date);
        view.showOwnersList(ownersList);
    }
}
