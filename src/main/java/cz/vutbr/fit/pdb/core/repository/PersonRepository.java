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

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.Person;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;

import java.util.*;
import java.util.Date;

/**
 * Repository creates  person type objects (@see Person), queries and calls to Oracle database.
 * Repository works mainly with table Person.
 * Class extends @see Observable.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class PersonRepository extends Observable {

    private OracleDataSource dataSource;

    /**
     * Constructor for PersonRepository @see PersonRepository.
     *
     * @param dataSource @see OracleDataSource
     */
    public PersonRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Method calls query under table person to Oracle database, which returns all persons from table.
     *
     * @return List of Person type objects(@see Person)
     */
    public List<Person> getPersonsList() {
        String query = "SELECT * FROM person";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);

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
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPersonsList " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table person to Oracle database, which returns  person from table with desired id.
     *
     * @param p @see Person person type object
     * @return @see Person
     */
    public Person getPerson(Person p) {
        String query = "SELECT * FROM person WHERE id_person = ?";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
            System.err.println("Error getPerson " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPersonById " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table person to Oracle database, which creates person record in database.
     *
     * @param person @see Person
     * @return boolean True if query was successful otherwise False.
     */
    public boolean createPerson(Person person) {
        String query = "INSERT INTO person(id_person, firstname, lastname, street, city, psc, email) VALUES(person_seq.nextval,?,?,?,?,?,?)";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
            System.err.println("Error createPerson " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error createPerson " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table person to Oracle database, which updates person record in table.
     *
     * @param person @see Person
     * @return boolean True if query was successful otherwise False.
     */
    public boolean savePerson(Person person) {
        String query = "UPDATE person SET firstname = ?, lastname = ?, street = ?, city = ?, psc = ?, email =? WHERE id_person = ?";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
            System.err.println("Error savePerson " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error savePerson " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query to Oracle database, which returns count of properties, in which person lived, for desired time interval.
     *
     * @param id_person Integer value, id of person
     * @param date_from @see Date, Date value from desired time interval
     * @param date_to   @see Date, Date value to desired time interval
     * @return Integer value, which represents count of properties.
     */
    public Integer getPersonPropertyCount(Integer id_person, Date date_from, Date date_to) {
        String query = "SELECT COUNT(*) as PropertiesCount " +
                "FROM property PR JOIN owner O ON (PR.id_property=O.id_property) JOIN person P ON (P.id_person=O.id_owner) WHERE" +
                "                ( P.id_person=? AND (O.valid_from >= ? ) AND (O.valid_to <= ?) ) OR" +
                "                ( P.id_person=? AND ( (? BETWEEN O.valid_from AND O.valid_to) OR" +
                "                (? BETWEEN O.valid_from AND O.valid_to)))" +
                "                GROUP BY P.id_person";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id_person);
            statement.setDate(2, new java.sql.Date(date_from.getTime()));
            statement.setDate(3, new java.sql.Date(date_to.getTime()));
            statement.setInt(4, id_person);
            statement.setDate(5, new java.sql.Date(date_from.getTime()));
            statement.setDate(6, new java.sql.Date(date_to.getTime()));

            ResultSet resultSet = statement.executeQuery();
            Integer count;

            if (resultSet.next()) {
                count = resultSet.getInt("propertiescount");

            } else {
                count = 0;
            }
            resultSet.close();
            statement.close();
            connection.close();
            return count;

        } catch (SQLException exception) {
            System.err.println("Error getPersonPropertyCount " + exception.getMessage());

            return 0;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPersonPropertyCount " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query to Oracle database, which returns summary of geometry area of properties, in which person lived, for desired time interval.
     *
     * @param id_person Integer value, id of person
     * @param date_from @see Date, Date value from desired time interval
     * @param date_to   @see Date, Date value to desired time interval
     * @return Integer value, which represents summary of geometry area
     */
    public Integer getPersonPropertySum(Integer id_person, Date date_from, Date date_to) {
        String query = "SELECT ROUND(SUM(SDO_GEOM.SDO_AREA(PR.geometry,1,'unit=SQ_M')), 0) as Area " +
                "FROM property PR JOIN owner O ON (PR.id_property=O.id_property) JOIN person P ON (P.id_person=O.id_owner) WHERE" +
                "                ( P.id_person=? AND (O.valid_from >= ? ) AND (O.valid_to <= ?) ) OR" +
                "                ( P.id_person=? AND ( (? BETWEEN O.valid_from AND O.valid_to) OR" +
                "                (? BETWEEN O.valid_from AND O.valid_to)))" +
                "                GROUP BY P.id_person";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id_person);
            statement.setDate(2, new java.sql.Date(date_from.getTime()));
            statement.setDate(3, new java.sql.Date(date_to.getTime()));
            statement.setInt(4, id_person);
            statement.setDate(5, new java.sql.Date(date_from.getTime()));
            statement.setDate(6, new java.sql.Date(date_to.getTime()));

            Integer sum;
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                sum = resultSet.getInt("area");
            } else {
                sum = 0;
            }
            resultSet.close();
            statement.close();
            connection.close();
            return sum;

        } catch (SQLException exception) {
            System.err.println("Error getPersonPropertySum " + exception.getMessage());

            return 0;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPersonPropertySum " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query to Oracle database, which returns the length of stay in days of person in desired time interval.
     *
     * @param id_person Integer value, id of person
     * @param date_from @see Date, Date value from desired time interval
     * @param date_to   @see Date, Date value to desired time interval
     * @return Integer value, which represents days in which person lived in properties
     */
    public Integer getPersonDuration(Integer id_person, Date date_from, Date date_to) {

        String query = "SELECT nvl(SUM(trunc( (CASE when O.valid_to > ? THEN ? ELSE O.valid_to END)-(CASE WHEN O.valid_from < ? THEN ? ELSE O.valid_from END) )), 0) AS DurationInDays" +
                "                FROM owner O RIGHT  JOIN person P ON(O.id_owner=P.id_person) WHERE" +
                "                ( P.id_person=? AND (O.valid_from >= ? ) AND (O.valid_to <= ?) ) OR" +
                "                ( P.id_person=? AND ( (? BETWEEN O.valid_from AND O.valid_to) OR" +
                "                (? BETWEEN O.valid_from AND O.valid_to)))" +
                "                GROUP BY P.id_person";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setDate(1, new java.sql.Date(date_to.getTime()));
            statement.setDate(2, new java.sql.Date(date_to.getTime()));
            statement.setDate(3, new java.sql.Date(date_from.getTime()));
            statement.setDate(4, new java.sql.Date(date_from.getTime()));
            statement.setInt(5, id_person);
            statement.setDate(6, new java.sql.Date(date_from.getTime()));
            statement.setDate(7, new java.sql.Date(date_to.getTime()));
            statement.setInt(8, id_person);
            statement.setDate(9, new java.sql.Date(date_from.getTime()));
            statement.setDate(10, new java.sql.Date(date_to.getTime()));

            Integer durationInDays;
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                durationInDays = resultSet.getInt("DurationInDays");
            } else {
                durationInDays = 0;
            }

            resultSet.close();
            statement.close();
            connection.close();

            return durationInDays;

        } catch (SQLException exception) {
            System.err.println("Error getPersonDuration " + exception.getMessage());
            return 0;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPersonDuration " + exception.getMessage());
            }
        }
    }
}