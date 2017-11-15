/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.gui.controller;

import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;

import java.util.Date;
import java.util.List;

public class OwnersController implements OwnersContract.Controller {

    private OwnerRepository repository;

    private OwnersContract.View view;

    private List<Owner> ownersList;


    public OwnersController(OwnerRepository repository, OwnersContract.View view) {
        this.repository = repository;
        this.view = view;

        view.setController(this);
        repository.addObserver((observable, o) -> update());

        update();
    }

    public void update() {
        System.out.println("update " + this.getClass().getSimpleName());

        ownersList = repository.getOwnersList();
        view.showOwnersList(ownersList);
    }

    @Override
    public void getOwnersListOfDate(Date date) {
        ownersList = repository.getOwnersListOfDate(date);
        view.showOwnersList(ownersList);
    }
}
