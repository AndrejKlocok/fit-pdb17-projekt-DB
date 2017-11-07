package cz.vutbr.fit.pdb.core.model;

import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;


public class Person {

    private int id;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String psc;
    private String email;


    public Person() {
        id = 0;
        firstName = "";
        lastName = "";
        street = "";
        city = "";
        psc = "";
        email = "";
    }

    public Person(int id, String firstName, String lastName, String street, String city, String psc, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.psc = psc;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPsc() {
        return psc;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void save(Connection connection) throws SQLException {
        String query = "insert into person(id_person, firstname, lastname, street, city, psc, email)"
                + " values(?,?,?,?,?,?,?)";

        try (PreparedStatement insert = connection.prepareStatement(query)) {
            insert.setInt(1, this.getId());
            insert.setString(2, this.getFirstName());
            insert.setString(3, this.getLastName());
            insert.setString(4, this.getStreet());
            insert.setString(5, this.getCity());
            insert.setString(6, this.getPsc());
            insert.setString(7, this.getEmail());

            try {
                insert.executeUpdate();
            } catch (SQLException sqlEx) {
                System.err.println("Error while inserting " + sqlEx.getMessage());
            }
        }
    }

    public void loadById(Connection connection, int id) throws SQLException {
        String query = "select * from person where id_person = " + id;

        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rset = stmt.executeQuery(query)) {
                while (rset.next()) {
                    this.id = rset.getInt("id_person");
                    this.firstName = rset.getString("firstname");
                    this.lastName = rset.getString("lastname");
                    this.street = rset.getString("street");
                    this.city = rset.getString("city");
                    this.psc = rset.getString("psc");
                    this.psc = rset.getString("email");
                }
            }
        }
    }
}
