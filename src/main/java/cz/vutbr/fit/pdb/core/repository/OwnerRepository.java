/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.model.Property;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Observable;


public class OwnerRepository extends Observable {

    private OracleDataSource dataSource;

    public OwnerRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Owner> getOwnersList() {
        String query = "SELECT * FROM owner LEFT OUTER JOIN person ON(owner.id_owner=person.id_person)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Owner> ownersList = new LinkedList<>();
            while (resultSet.next()) {
                Owner owner = new Owner();
                owner.setIdPerson(resultSet.getInt("id_owner"));
                owner.setIdProperty(resultSet.getInt("id_property"));
                owner.setValidFrom(resultSet.getDate("valid_from"));
                owner.setValidTo(resultSet.getDate("valid_to"));
                owner.setIdPerson(resultSet.getInt("id_person"));
                owner.setFirstName(resultSet.getString("firstname"));
                owner.setLastName(resultSet.getString("lastname"));
                owner.setStreet(resultSet.getString("street"));
                owner.setCity(resultSet.getString("city"));
                owner.setPsc(resultSet.getString("psc"));
                owner.setEmail(resultSet.getString("email"));
                ownersList.add(owner);
                // TODO load property history
            }

            connection.close();
            statement.close();
            return ownersList;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public List<Owner> getOwnersListOfProperty(Property property) {
        String query = "SELECT * FROM owner LEFT OUTER JOIN person ON(owner.id_owner=person.id_person) WHERE id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Owner> ownersList = new LinkedList<>();
            while (resultSet.next()) {
                Owner owner = new Owner();
                owner.setIdPerson(resultSet.getInt("id_owner"));
                owner.setIdProperty(resultSet.getInt("id_property"));
                owner.setValidFrom(resultSet.getDate("valid_from"));
                owner.setValidTo(resultSet.getDate("valid_to"));
                owner.setIdPerson(resultSet.getInt("id_person"));
                owner.setFirstName(resultSet.getString("firstname"));
                owner.setLastName(resultSet.getString("lastname"));
                owner.setStreet(resultSet.getString("street"));
                owner.setCity(resultSet.getString("city"));
                owner.setPsc(resultSet.getString("psc"));
                owner.setEmail(resultSet.getString("email"));
                ownersList.add(owner);
                // TODO load property history
            }

            connection.close();
            statement.close();
            return ownersList;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public Person getOwner(Owner owner) {
        String query = "SELECT * FROM owner LEFT OUTER JOIN person ON(owner.id_owner=person.id_person) WHERE id_owner = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, owner.getIdOwner());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Owner newOwner = new Owner();
                newOwner.setIdPerson(resultSet.getInt("id_owner"));
                newOwner.setIdProperty(resultSet.getInt("id_property"));
                newOwner.setValidFrom(resultSet.getDate("valid_from"));
                newOwner.setValidTo(resultSet.getDate("valid_to"));
                newOwner.setIdPerson(resultSet.getInt("id_person"));
                newOwner.setFirstName(resultSet.getString("firstname"));
                newOwner.setLastName(resultSet.getString("lastname"));
                newOwner.setStreet(resultSet.getString("street"));
                newOwner.setCity(resultSet.getString("city"));
                newOwner.setPsc(resultSet.getString("psc"));
                newOwner.setEmail(resultSet.getString("email"));
                // TODO load property history

                connection.close();
                statement.close();
                return newOwner;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public Person getOwnerById(int idOwner) {
        String query = "SELECT * FROM owner LEFT OUTER JOIN person ON(owner.id_owner=person.id_person) WHERE id_owner = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idOwner);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Owner newOwner = new Owner();
                newOwner.setIdPerson(resultSet.getInt("id_owner"));
                newOwner.setIdProperty(resultSet.getInt("id_property"));
                newOwner.setValidFrom(resultSet.getDate("valid_from"));
                newOwner.setValidTo(resultSet.getDate("valid_to"));
                newOwner.setIdPerson(resultSet.getInt("id_person"));
                newOwner.setFirstName(resultSet.getString("firstname"));
                newOwner.setLastName(resultSet.getString("lastname"));
                newOwner.setStreet(resultSet.getString("street"));
                newOwner.setCity(resultSet.getString("city"));
                newOwner.setPsc(resultSet.getString("psc"));
                newOwner.setEmail(resultSet.getString("email"));
                // TODO load property history

                connection.close();
                statement.close();
                return newOwner;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public boolean createOwner(Owner owner) {
        String query = "INSERT INTO owner (id_owner, id_property, valid_from, valid_to, id_person) VALUES (owner_seq.nextval, ?,?,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, owner.getIdProperty());
            statement.setDate(2, new java.sql.Date(owner.getValidFrom().getTime()));
            statement.setDate(3, new java.sql.Date(owner.getValidTo().getTime()));
            statement.setInt(4, owner.getIdPerson());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean saveOwner(Owner owner) {
        String query = "UPDATE owner SET id_property = ?, valid_from = ?, valid_to = ?, id_person = ? WHERE id_owner = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, owner.getIdProperty());
            statement.setDate(2, new java.sql.Date(owner.getValidFrom().getTime()));
            statement.setDate(3, new java.sql.Date(owner.getValidTo().getTime()));
            statement.setInt(4, owner.getIdPerson());
            statement.setInt(5, owner.getIdOwner());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean deleteOwner(Owner owner) {
        // TODO
        return true;
    }

    public boolean deleteOwnerOfProperty(Owner owner, Property property) {
        // TODO
        return true;
    }

    public List<Owner> getOwnersListOfDate(Date date) {
        // TODO
        return new LinkedList<>();
    }
}
