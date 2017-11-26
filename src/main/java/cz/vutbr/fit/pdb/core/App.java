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

package cz.vutbr.fit.pdb.core;

import cz.vutbr.fit.pdb.core.repository.*;
import cz.vutbr.fit.pdb.gui.controller.MapController;
import cz.vutbr.fit.pdb.gui.view.MapWindow;
import oracle.jdbc.pool.OracleDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Main application launcher
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class App {

    private static final String CONFIGURATION_FILE = "config.properties";

    private static boolean DEBUG = false;

    private static OracleDataSource dataSource;


    /**
     * Check whether is debug turned on
     *
     * @return true if debug is turned on
     */
    public static boolean isDebug() {
        return DEBUG;
    }

    /**
     * Static method holding data source with credentials for connections to database
     *
     * @return data source singleton instance
     */
    public static synchronized OracleDataSource getDataSource() {

        if (dataSource == null) {

            Properties properties = new Properties(System.getProperties());
            try {
                properties.load(new FileInputStream(CONFIGURATION_FILE));
            } catch (IOException exception) {
                System.err.println("properties exception: " + exception.getMessage());
                System.exit(1);
            }

            String jdbc = properties.getProperty("jdbc");
            String login = properties.getProperty("login");
            String password = properties.getProperty("password");

            if (jdbc == null || login == null || password == null) {
                System.err.println("Some required configuration property is missing");
                System.exit(1);
            }

            String debug = properties.getProperty("debug");

            if (debug == null) {
                DEBUG = false;
            } else {
                if (debug.equalsIgnoreCase("true")) {
                    DEBUG = true;
                }
            }

            if (isDebug()) {
                System.out.println("Jdbc: " + jdbc);
                System.out.println("Login: " + login);
                System.out.println("Password: " + password);
            }

            try {
                OracleDataSource oracleDataSource;
                oracleDataSource = new OracleDataSource();
                oracleDataSource.setURL(jdbc);
                oracleDataSource.setUser(login);
                oracleDataSource.setPassword(password);

                dataSource = oracleDataSource;

            } catch (SQLException exception) {
                System.err.println("SQLException: " + exception.getMessage());
                System.exit(1);
            }
        }

        return dataSource;
    }

    /**
     * Main application launching method
     *
     * @param args application CLI arguments
     */
    public static void main(String[] args) {

        // show gui
        PropertyRepository propertyRepository = new PropertyRepository(App.getDataSource());
        GroundPlanRepository groundPlanRepository = new GroundPlanRepository(App.getDataSource());
        PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(App.getDataSource());
        OwnerRepository ownerRepository = new OwnerRepository(App.getDataSource());
        PersonRepository personRepository = new PersonRepository(App.getDataSource());
        MapWindow mapWindow = new MapWindow();
        new MapController(propertyRepository, groundPlanRepository, propertyPriceRepository, ownerRepository, personRepository, mapWindow);
    }
}
