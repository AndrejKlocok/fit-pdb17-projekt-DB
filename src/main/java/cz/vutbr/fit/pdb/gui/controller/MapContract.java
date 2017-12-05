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

import cz.vutbr.fit.pdb.core.model.Property;
import oracle.spatial.geometry.JGeometry;

import java.util.List;

/**
 * Contract specifying interface between map view and controller
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class MapContract {

    public interface View {

        void setController(MapContract.Controller controller);

        void showMessage(String message);

        void showError(String error);

        void showPropertyList(List<Property> propertyList);

        void hide();
    }

    public interface Controller {

        void refresh();

        void resetDatabase();

        void executeSqlFile(String fileName);

        void createProperty(Property property);

        void savePropertyGeometry(Property property, JGeometry geometry);

        void getProperty(Property property);

        void getPersons();

        void filterPropertyList(String name, double maxPrice, boolean hasOwner);

        void findNearestProperty(double lat, double lng);

        void findNearestProperty(Property property);

        void findAdjacentProperty(Property property);

        void calculateArea(Property property);

        void calculateLength(Property property);
    }
}
