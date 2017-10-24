/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.LatLongBounds;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.*;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.gui.MapWindow;
import oracle.spatial.geometry.JGeometry;

public class App {

    private LinkedList<Vozidlo> vozidla;
    private LinkedList<Vozidlo> removedVozidla;

    public App() {
        vozidla = new LinkedList<>();
        removedVozidla = new LinkedList<>();
    }

    public void addVozidlo(String vyrobce, String model) {
        this.vozidla.add(new Vozidlo(vyrobce, model));
    }

    public void delVozidlo(Vozidlo vozidlo) throws KatalogExVozidloNotFound {
        if (this.vozidla.remove(vozidlo)) {
            this.removedVozidla.add(vozidlo);
        } else {
            throw new KatalogExVozidloNotFound("Odstranovane vozidlo neni v katalogu!",
                    vozidlo);
        }
    }

    public Iterator<Vozidlo> getVozidloIterator() {
        return this.vozidla.listIterator();
    }

    public void loadFromDB(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rset = stmt.executeQuery(
                    "select vyrobce, model from vozidlo")) {
                this.vozidla.clear();
                while (rset.next()) {
                    this.vozidla.add(
                            new Vozidlo(rset.getString(1), rset.getString(2)));
                }
            }
        }
    }

    private void removeFromDB(Connection connection) throws SQLException {
        try (PreparedStatement pstmtDelete = connection.prepareStatement(
                "delete from vozidlo where vyrobce = ? and model = ?")) {
            for (Iterator<Vozidlo> i = this.removedVozidla.listIterator(); i.hasNext(); ) {
                Vozidlo v = i.next();
                pstmtDelete.setString(1, v.vyrobce);
                pstmtDelete.setString(2, v.model);
                pstmtDelete.executeUpdate();
                i.remove();
            }
        }
    }

    public void saveToDB(Connection connection) throws SQLException {
        removeFromDB(connection);
        try (PreparedStatement pstmtInsert = connection.prepareStatement(
                "insert into vozidlo(vyrobce, model, foto) values "
                        + "(?,?,ordsys.ordimage.init())")) {
            for (Vozidlo v : this.vozidla) {
                pstmtInsert.setString(1, v.vyrobce);
                pstmtInsert.setString(2, v.model);
                try {
                    pstmtInsert.executeUpdate();
                } catch (SQLException sqlEx) {
                    System.err.println("Error while inserting '" + v
                            + "' - " + sqlEx.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {

        /*
        Properties properties = new Properties(System.getProperties());
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            // TODO
        }
        System.out.println("Login: " + properties.getProperty("login"));
        System.out.println("Password: " + properties.getProperty("password"));

        App katalog = new App();
        // 1. cast cviceni
        katalog.addVozidlo("Skoda", "Felicia Combi");
        katalog.addVozidlo("Fiat", "Uno");
        katalog.addVozidlo("BMW", "x6");
        try {
            katalog.delVozidlo(new Vozidlo("Fiat", "Uno"));
        } catch (KatalogExVozidloNotFound e) {
            System.err.println(e.getMessage() + ", pro vozidlo " + e.getVozidlo());
        }
        for (Iterator<Vozidlo> i = katalog.getVozidloIterator(); i.hasNext(); ) {
            System.out.println(i.next());
        }


        System.out.println("Database test");

        // 2. cast cviceni
        try {
            // create a OracleDataSource instance
            OracleDataSource ods = new OracleDataSource();
            ods.setURL(properties.getProperty("jdbc"));
            ods.setUser(properties.getProperty("login"));
            ods.setPassword(properties.getProperty("password"));

            // connect to the database
            try (Connection conn = ods.getConnection()) {
                // create a Statement
                try (Statement stmt = conn.createStatement()) {
                    // select something from the system's dual table
                    try (ResultSet rset = stmt.executeQuery(
                            "select 1+2 as col1, 3-4 as col2 from dual")) {
                        // iterate through the result and print the values
                        while (rset.next()) {
                            System.out.println("col1: '" + rset.getString(1)
                                    + "'\tcol2: '" + rset.getString(2) + "'");
                        }
                    }
                }
            }
        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }


        System.out.println("Database images test");

        // 3. cast cviceni
        try {
            // create a OracleDataSource instance
            OracleDataSource ods = new OracleDataSource();
            ods.setURL(properties.getProperty("jdbc"));
            ods.setUser(properties.getProperty("login"));
            ods.setPassword(properties.getProperty("password"));

            // connect to the database
            try (Connection conn = ods.getConnection()) {
                // bod 3
                katalog.saveToDB(conn);
                katalog.loadFromDB(conn);
                for (Iterator<Vozidlo> i = katalog.getVozidloIterator(); i.hasNext(); ) {
                    System.out.println(i.next());
                }
                // bod 4
                String directory = "db";
                int cislo = 1;
                for (Iterator<Vozidlo> i = katalog.getVozidloIterator(); i.hasNext(); ) {
                    i.next().loadFotoFromFile(conn, directory + "/car" + cislo + ".jpg");
                    cislo++;
                }
                for (Iterator<Vozidlo> i = katalog.getVozidloIterator(); i.hasNext(); ) {
                    Vozidlo v = i.next();
                    v.saveFotoToFile(conn, directory + "/car." + v.vyrobce + "-" + v.model + ".jpg");
                }
                // bod 5
                Vozidlo vzor = katalog.getVozidloIterator().next();
                System.out.println("Nejpodobnejsi vozidlu '" + vzor
                        + "' je vozidlo '" + vzor.getTheMostSimilar(conn, katalog,
                        0.3, 0.3, 0.3, 0.1));
            }
        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        } catch (IOException ioEx) {
            System.err.println("IOException: " + ioEx.getMessage());
        }*/



        // sample data

        // Polygon
        Property property1 = new Property();
        property1.setId("1");
        property1.setName("Polygon");
        property1.setType(Property.Type.LAND);
        property1.setPriceCurrent(1000000d);
        property1.setDescription("popis 1");
        property1.setGeometry(JGeometry.createLinearPolygon(new double[2],2,2));

        // Rectangle
        Property property2 = new Property();
        property2.setId("2");
        property2.setName("Rectangle");
        property2.setType(Property.Type.HOUSE);
        property2.setPriceCurrent(500000d);
        property2.setDescription("popis 2");
        property2.setGeometry(new JGeometry(2,2,2,2,2));

        // Circle
        Property property3 = new Property();
        property3.setId("3");
        property3.setName("Circle");
        property3.setType(Property.Type.APARTMENT);
        property3.setPriceCurrent(4200000d);
        property3.setDescription("popis 3");
        property3.setGeometry(JGeometry.createCircle(2,2,2, 2));

        // Property list
        List<Property> propertyList = new LinkedList<>();
        propertyList.add(property1);
        propertyList.add(property2);
        propertyList.add(property3);


        // show gui
        MapWindow mapWindow = new MapWindow(propertyList);
        mapWindow.showAsync();
    }
}