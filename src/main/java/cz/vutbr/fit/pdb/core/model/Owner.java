/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.HashMap;

public class Owner {

    private int idOwner;

    private int idProperty;

    private Date validFrom;

    private Date validTo;

    private HashMap<String, Property> propertyHistory;

    private Person person;

    // TODO

    public Owner() {
        idOwner = 0;
        idProperty = 0;
        propertyHistory = new HashMap<>();
    }

    public Owner(int idOwner, int idProperty) {
        this.idOwner = idOwner;
        this.idProperty = idProperty;
    }

    public Owner(int idOwner, int idProperty, Date validFrom, Date validTo) {
        this.idOwner = idOwner;
        this.idProperty = idProperty;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public int getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(int idProperty) {
        this.idProperty = idProperty;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public HashMap<String, Property> getPropertyHistory() {
        return propertyHistory;
    }

    public void setPropertyHistory(HashMap<String, Property> propertyHistory) {
        this.propertyHistory = propertyHistory;
    }

    public Property getPropertyCurrent() {
        // TODO
        return new Property();
    }

    public void setPropertyCurrent(Property property) {
        // TODO
    }

    public Double getPropertyCurrentCount() {
        // TODO
        return 42d;
    }

    public Double getPropertyCurrentLandAreaSum() {
        // TODO
        return 42d;
    }

    public void save(Connection connection) throws SQLException {
        String query = "insert into owner(id_owner, id_property, valid_from, valid_to) values(?,?,?,?)";

        try (PreparedStatement insert = connection.prepareStatement(query)) {
            insert.setInt(1, this.getIdOwner());
            insert.setInt(2, this.getIdProperty());
            insert.setDate(3, this.getValidFrom());
            insert.setDate(4, this.getValidTo());
            try {
                insert.executeUpdate();
            } catch (SQLException sqlEx) {
                System.err.println("Error while inserting " + sqlEx.getMessage());
            }
        }
    }

    public void loadByIdOwner(Connection connection, int idOwner) throws SQLException {
        String query = "select * from owner where id_owner =" + idOwner;

        try (Statement select = connection.createStatement()) {
            try (ResultSet rset = select.executeQuery(query)) {
                while (rset.next()) {
                    this.idOwner = rset.getInt("id_owner");
                    this.idProperty = rset.getInt("id_property");
                    this.validFrom = rset.getDate("valid_from");
                    this.validTo = rset.getDate("valid_to");
                }
            }
        }
    }

    public void loadByIdProperty(Connection connection, int idProperty) throws SQLException {
        String query = "select * from owner where id_owner =" + idProperty;

        try (Statement select = connection.createStatement()) {
            try (ResultSet rset = select.executeQuery(query)) {
                while (rset.next()) {
                    this.idOwner = rset.getInt("id_owner");
                    this.idProperty = rset.getInt("id_property");
                    this.validFrom = rset.getDate("valid_from");
                    this.validTo = rset.getDate("valid_to");
                }
            }
        }
    }

    public void loadPersonInfo(Connection connection, int idOwner) throws SQLException {
        String query  =  "select * from owner join person on id_owner = id_person where id_owner ="+ idOwner;
        this.person = new Person();
        try (Statement select = connection.createStatement()) {
            try (ResultSet rset = select.executeQuery(query)) {
                while (rset.next()) {
                    this.idOwner = rset.getInt("id_owner");
                    this.idProperty = rset.getInt("id_property");
                    this.validFrom = rset.getDate("valid_from");
                    this.validTo = rset.getDate("valid_to");
                    this.person.setId(rset.getInt("id_person"));
                    this.person.setFirstName(rset.getString("firstname"));
                    this.person.setLastName(rset.getString("lastname"));
                    this.person.setLastName(rset.getString("street"));
                    this.person.setLastName(rset.getString("city"));
                    this.person.setLastName(rset.getString("psc"));
                    this.person.setLastName(rset.getString("email"));
                }
            }
        }
    }


}
