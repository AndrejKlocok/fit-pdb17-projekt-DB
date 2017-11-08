/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.Person;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;


public class PersonRepository {

    private OracleDataSource dataSource;

    public PersonRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Person> getPersons() {
        String query = "SELECT * FROM person";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Person> persons = new LinkedList<>();
            while (resultSet.next()) {
                Person person = new Person();
                person.setIdPerson(resultSet.getInt("id_person"));
                person.setFirstName(resultSet.getString("firstname"));
                person.setLastName(resultSet.getString("lastname"));
                person.setStreet(resultSet.getString("street"));
                person.setCity(resultSet.getString("city"));
                person.setPsc(resultSet.getString("psc"));
                person.setEmail(resultSet.getString("email"));
                persons.add(person);
            }

            connection.close();
            statement.close();
            return persons;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public Person getPerson(Person person) {
        String query = "SELECT * FROM person WHERE id_person = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, person.getIdPerson());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Person newPerson = new Person();
                newPerson.setIdPerson(resultSet.getInt("id_person"));
                newPerson.setFirstName(resultSet.getString("firstname"));
                newPerson.setLastName(resultSet.getString("lastname"));
                newPerson.setStreet(resultSet.getString("street"));
                newPerson.setCity(resultSet.getString("city"));
                newPerson.setPsc(resultSet.getString("psc"));
                newPerson.setEmail(resultSet.getString("email"));

                connection.close();
                statement.close();
                return newPerson;
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

    public Person getPersonById(int idPerson) {
        String query = "SELECT * FROM person WHERE id_person = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idPerson);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Person person = new Person();
                person.setIdPerson(resultSet.getInt("id_person"));
                person.setFirstName(resultSet.getString("firstname"));
                person.setLastName(resultSet.getString("lastname"));
                person.setStreet(resultSet.getString("street"));
                person.setCity(resultSet.getString("city"));
                person.setPsc(resultSet.getString("psc"));
                person.setEmail(resultSet.getString("email"));

                connection.close();
                statement.close();
                return person;
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

    public boolean createPerson(Person person) {
        String query = "INSERT INTO person (firstname, lastname, street, city, psc, email) VALUES (?,?,?,?,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.setString(3, person.getStreet());
            statement.setString(4, person.getCity());
            statement.setString(5, person.getPsc());
            statement.setString(6, person.getEmail());
            statement.executeQuery();

            connection.close();
            statement.close();
            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean savePerson(Person person) {
        String query = "INSERT INTO person(id_person, firstname, lastname, street, city, psc, email) VALUES(?,?,?,?,?,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, person.getIdPerson());
            statement.setString(2, person.getFirstName());
            statement.setString(3, person.getLastName());
            statement.setString(4, person.getStreet());
            statement.setString(5, person.getCity());
            statement.setString(6, person.getPsc());
            statement.setString(7, person.getEmail());
            statement.executeQuery();

            connection.close();
            statement.close();
            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }
}
