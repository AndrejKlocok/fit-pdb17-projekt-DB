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

package cz.vutbr.fit.pdb.gui.view;

import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.repository.PersonRepository;

import javax.swing.*;
import java.util.List;

/**
 * Implementation of combo box with persons items.
 * Combo box loads itself all persons from database and holds list of all Person objects.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 * @see Person
 * @see JComboBox
 */
public class PersonComboBox extends JComboBox {

    /**
     * Default constructor
     */
    public PersonComboBox() {
        setModel(new PersonComboBoxModel());
    }
}

class PersonComboBoxModel extends AbstractListModel implements ComboBoxModel {

    private List<Person> personList;

    Person selection = null;


    /**
     * Default constructor
     */
    public PersonComboBoxModel() {

        PersonRepository personRepository = new PersonRepository(App.getDataSource());
        personList = personRepository.getPersonsList();
    }

    /**
     * Get person from persons items at specified index
     *
     * @param index index
     * @return person at given index
     */
    public Object getElementAt(int index) {
        return personList.get(index);
    }

    /**
     * Get count of persons items
     *
     * @return count of persons items
     */
    public int getSize() {
        return personList.size();
    }

    /**
     * To select and register an item from the pull-down list
     *
     * @param anItem item
     */
    public void setSelectedItem(Object anItem) {
        selection = (Person) anItem;
    }

    /**
     * To add the selection to the combo box
     * Methods implemented from the interface ComboBoxModel
     */
    public Object getSelectedItem() {
        return selection;
    }
}
