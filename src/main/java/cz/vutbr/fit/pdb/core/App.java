/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core;

import cz.vutbr.fit.pdb.core.repository.GroundPlanRepository;
import cz.vutbr.fit.pdb.core.repository.OwnerRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyPriceRepository;
import cz.vutbr.fit.pdb.core.repository.PropertyRepository;
import cz.vutbr.fit.pdb.gui.controller.MapController;
import cz.vutbr.fit.pdb.gui.view.MapWindow;
import oracle.jdbc.pool.OracleDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;


public class App {

    private static OracleDataSource dataSource;

    public static synchronized OracleDataSource getDataSource() {

        if (dataSource == null) {

            Properties properties = new Properties(System.getProperties());
            try {
                properties.load(new FileInputStream("config.properties"));
            } catch (IOException e) {
                // TODO
            }
            System.out.println("Login: " + properties.getProperty("login"));
            System.out.println("Password: " + properties.getProperty("password"));
            System.out.println("Jdbc: " + properties.getProperty("jdbc"));

            try {
                OracleDataSource oracleDataSource;
                oracleDataSource = new OracleDataSource();
                oracleDataSource.setURL(properties.getProperty("jdbc"));
                oracleDataSource.setUser(properties.getProperty("login"));
                oracleDataSource.setPassword(properties.getProperty("password"));

                dataSource = oracleDataSource;

            } catch (SQLException exception) {
                System.err.println("SQLException: " + exception.getMessage());
            }
        }

        return dataSource;
    }

    public static void main(String[] args) {

        // show gui
        PropertyRepository propertyRepository = new PropertyRepository(App.getDataSource());
        GroundPlanRepository groundPlanRepository = new GroundPlanRepository(App.getDataSource());
        PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(App.getDataSource());
        OwnerRepository ownerRepository = new OwnerRepository(App.getDataSource());
        MapWindow mapWindow = new MapWindow();
        new MapController(propertyRepository, groundPlanRepository, propertyPriceRepository, ownerRepository, mapWindow);
    }
}
