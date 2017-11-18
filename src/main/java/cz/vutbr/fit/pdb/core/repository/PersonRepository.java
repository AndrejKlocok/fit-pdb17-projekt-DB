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
import cz.vutbr.fit.pdb.core.model.PersonDuration;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;
import java.sql.Date;
import java.util.*;



public class PersonRepository extends Observable {

    private OracleDataSource dataSource;

    public PersonRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Person> getPersonsList() {
        String query = "SELECT * FROM person";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Person> persons = new LinkedList<>();
            OwnerRepository ownerRepository = new OwnerRepository(dataSource);
            while (resultSet.next()) {
                Person person = new Person();
                person.setIdPerson(resultSet.getInt("id_person"));
                person.setFirstName(resultSet.getString("firstname"));
                person.setLastName(resultSet.getString("lastname"));
                person.setStreet(resultSet.getString("street"));
                person.setCity(resultSet.getString("city"));
                person.setPsc(resultSet.getString("psc"));
                person.setEmail(resultSet.getString("email"));
                person.setPropertyHistory(ownerRepository.getOwnerHistory(person));
                persons.add(person);
            }

            connection.close();
            statement.close();
            return persons;

        } catch (SQLException exception) {
            System.err.println("Error getPersonsList " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public Person getPerson(Person p) {
        String query = "SELECT * FROM person WHERE id_person = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, p.getIdPerson());
            OwnerRepository ownerRepository = new OwnerRepository(dataSource);

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
                person.setPropertyHistory(ownerRepository.getOwnerHistory(person));

                connection.close();
                statement.close();
                return person;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error getPersonById " + exception.getMessage());

            return null;
        }
    }


    public boolean createPerson(Person person) {
        String query = "INSERT INTO person(id_person, firstname, lastname, street, city, psc, email) VALUES(person_seq.nextval,?,?,?,?,?,?)";

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

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean savePerson(Person person) {
        String query = "UPDATE person SET firstname = ?, lastname = ?, street = ?, city = ?, psc = ?, email =? WHERE id_person = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.setString(3, person.getStreet());
            statement.setString(4, person.getCity());
            statement.setString(5, person.getPsc());
            statement.setString(6, person.getEmail());
            statement.setInt(7, person.getIdPerson());

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
    public  HashMap<Integer, ArrayList<Integer>> getPersonsMapCountSum(java.sql.Date date_from, java.sql.Date date_to) {
        String query = "SELECT P.id_person, COUNT(*) as PropertiesCount, ROUND(SUM(SDO_GEOM.SDO_AREA(PR.geometry,1,'unit=SQ_M')), 0) as Area\n" +
                "FROM property PR JOIN owner O ON (PR.id_property=O.id_property) JOIN person P ON (P.id_person=O.id_owner) \n" +
                "WHERE (? <= O.valid_from) AND (? >= O.valid_to)\n" +
                "GROUP BY P.id_person\n" +
                "ORDER BY Area DESC, PropertiesCount DESC";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1, date_from);
            statement.setDate(2, date_to);

            ResultSet resultSet = statement.executeQuery();
            HashMap<Integer, ArrayList<Integer>> data = new HashMap<>();

            while (resultSet.next()) {
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(0, resultSet.getInt("propertiescount"));
                tmp.add(1, resultSet.getInt("area"));
                data.put(resultSet.getInt("id_person"), tmp);
            }

            connection.close();
            statement.close();
            return data;

        } catch (SQLException exception) {
            System.err.println("Error getPersonsMapCountSum " + exception.getMessage());

            return new HashMap<>();
        }
    }

    public List<PersonDuration> getPersonDurationList(){
        String query = "SELECT O.id_owner , SUM(trunc(O.valid_to-O.valid_from))AS DurationInDays, COUNT(*) AS PropertiesCount \n" +
                "FROM owner O GROUP BY O.ID_OWNER ORDER BY DurationInDays Desc";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            LinkedList<PersonDuration> personDurationLinkedList = new LinkedList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PersonDuration personDuration = new PersonDuration();
                personDuration.getPerson().setIdPerson(resultSet.getInt("id_owner"));
                personDuration.setDuration(resultSet.getInt("DurationInDays"));
                personDuration.setPropertyCount(resultSet.getInt("PropertiesCount"));
                personDurationLinkedList.add(personDuration);
            }
            connection.close();
            statement.close();

            //create persons
            for (PersonDuration personDuration:personDurationLinkedList) {
                personDuration.setPerson(this.getPerson(personDuration.getPerson()));
            }
        return  personDurationLinkedList;

        } catch (SQLException exception) {
            System.err.println("Error getPersonDurationList " + exception.getMessage());

            return null;
        }
    }
}