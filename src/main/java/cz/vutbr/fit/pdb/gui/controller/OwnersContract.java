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

import cz.vutbr.fit.pdb.core.model.Owner;

import java.util.Date;
import java.util.List;

/**
 * Contract specifying interface between owners view and controller
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
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
