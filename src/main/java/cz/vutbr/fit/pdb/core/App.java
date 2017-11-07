/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.LatLongBounds;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.*;
import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.gui.MapWindow;
import oracle.spatial.geometry.JGeometry;
import oracle.jdbc.pool.OracleDataSource;

import javax.sound.midi.SysexMessage;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

import static cz.vutbr.fit.pdb.core.model.Property.Type.HOUSE;


public class App {

    private LinkedList<Vozidlo> vozidla;
    private LinkedList<Vozidlo> removedVozidla;
    private LinkedList<Property> properties;

    public App() {
        vozidla = new LinkedList<>();
        removedVozidla = new LinkedList<>();
        properties = new LinkedList<>();
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

    public static OracleDataSource connection() {
        Properties properties = new Properties(System.getProperties());
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            // TODO
        }
        System.out.println("Login: " + properties.getProperty("login"));
        System.out.println("Password: " + properties.getProperty("password"));
        System.out.println("Jdbc: " + properties.getProperty("jdbc"));

        OracleDataSource ods = null;
        try {
            ods = new OracleDataSource();
            ods.setURL(properties.getProperty("jdbc"));
            ods.setUser(properties.getProperty("login"));
            ods.setPassword(properties.getProperty("password"));

            return ods;

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }

        return ods;
    }


    public static void main(String[] args) throws SQLException, ParseException {
        OracleDataSource ods = null;

        /*Person person1 = new Person(1, "Jozef", "Mak", "street", "city", "psc", "email");
        Person person2 = new Person(2, "Vladimir", "Pes", "street", "city", "psc", "email");
        Person person3 = new Person(3, "Milos", "Milos", "street", "city", "psc", "email");

        String dateFrom = "19/07/2001";
        DateFormat df =  new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date dtt = df.parse(dateFrom);
        java.sql.Date ds = new java.sql.Date(dtt.getTime());


        Owner owner1 = new Owner(1, 1, ds, ds);
        Owner owner2 = new Owner(2, 2, ds, ds);
        Owner owner3 = new Owner(3,3, ds ,ds);
        Property property1 = new Property(1, HOUSE,"dom1", "desc1");
        double [] coords1 = {16.607206, 49.191432, 16.607436, 49.191344, 16.607542, 49.191457, 16.607310, 49.191550};
        property1.setGeometry(JGeometry.createLinearPolygon(coords1, 2, 8307));

        Property property2 = new Property(2, HOUSE,"dom2", "desc2");
        double [] coords2 = {16.603125, 49.203747, 16.603033, 49.203700, 16.603319, 49.203454, 16.603418, 49.203500};
        property2.setGeometry(JGeometry.createLinearPolygon(coords2, 2, 8307));

        Property property3 = new Property(3, HOUSE, "dom3", "desc3");
        double [] coords3 = {16.606089, 49.191362, 16.606155, 49.191373, 16.606149, 49.191385, 16.606294, 49.191429, 16.606284, 49.191449,
                            16.606266, 49.191457, 16.606235, 49.191536, 16.606241, 49.191543, 16.606256, 49.191543, 16.606284, 49.191571,
                            16.606243, 49.191595, 16.606342, 49.191633, 16.606344, 49.191604, 16.606400, 49.191604, 16.606405, 49.191644,
                            16.606500, 49.191605, 16.606456, 49.191563, 16.606534, 49.191533, 16.606580, 49.191566, 16.606637, 49.191545,
                            16.606727, 49.191647, 16.606717, 49.191673, 16.606400, 49.191805, 16.606376, 49.191805, 16.605984, 49.191663,
                            16.605984, 49.191622};
        property3.setGeometry(JGeometry.createLinearPolygon(coords3, 2, 8307));

        Property property4 = new Property(4, HOUSE, "dom4", "diera");
        double outer4 [] = {16.605299, 49.192204, 16.605467, 49.191726, 16.605920, 49.191791, 16.605744, 49.192274, 16.605299, 49.192204};
        double inner4 [] = {16.605500, 49.192087, 16.605647, 49.192108, 16.605722, 49.191899, 16.605573, 49.191874, 16.605500, 49.192087};
        Object coords4 [] = {outer4, inner4};
        property4.setGeometry(JGeometry.createLinearPolygon(coords4, 2, 8307));*/

        Property property1 = new Property();
        Property property2 = new Property();
        Property property3 = new Property();
        Property property4 = new Property();

        Person person1 = new Person();
        Person person2 = new Person();
        Person person3 = new Person();



        try {
            ods = connection();
            Connection connection = ods.getConnection();
            //property1.save(connection);
//            property2.save(connection);
//            property3.save(connection);
            //property4.save(connection);

            person1.loadById(connection, 1);
            person2.loadById(connection, 2);
            person3.loadById(connection, 3);
            System.out.println(person1.getFirstName());
            System.out.println(person2.getFirstName());
            System.out.println(person3.getFirstName());

            property1.loadById(connection,1);
            property2.loadById(connection,2);
            property3.loadById(connection,3);
            property4.loadById(connection,4);

            System.out.println(property1.getName());
            System.out.println(property3.getName());
            System.out.println(property2.getName());
            System.out.println(property4.getName());


        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }
        List<Property> propertyList = new LinkedList<>();
        propertyList.add(property1);
        propertyList.add(property2);
        propertyList.add(property3);
        propertyList.add(property4);
        //System.out.println(coords[0]);
        // show gui
        MapWindow mapWindow = new MapWindow(propertyList);
        mapWindow.showAsync();
    }

}
