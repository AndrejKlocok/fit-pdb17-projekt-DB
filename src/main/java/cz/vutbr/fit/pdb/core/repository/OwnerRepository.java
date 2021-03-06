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

import cz.vutbr.fit.pdb.core.App;
import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.model.Property;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Owner repository creates Owner type objects (@see Owner), queries and calls to Oracle database.
 * Repository works mainly with table Owner.
 * Class extends @see Observable.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class OwnerRepository extends Observable {

    private OracleDataSource dataSource;

    /**
     * Constructor for owner repository @see OwnerRepository.
     *
     * @param dataSource @see OracleDataSource
     */
    public OwnerRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Method calls query to Oracle database, which returns all records from Owner table according to id of owner and initializes all objects.
     *
     * @param person @see Person
     * @return List of @see Owner type objects
     */
    public List<Owner> getOwnerHistory(Person person) {
        String query = "SELECT person.*, owner.valid_from, owner.valid_to, property.* " +
                "FROM owner JOIN person ON(person.ID_PERSON=owner.id_owner) JOIN property ON(property.id_property=owner.id_property) " +
                "WHERE id_owner = ? " +
                "ORDER BY owner.valid_from";
        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, person.getIdPerson());
            LinkedList<Owner> ownerLinkedList = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery();

            PropertyRepository propertyRepository = new PropertyRepository(dataSource);

            while (resultSet.next()) {
                Owner owner = new Owner();
                owner.setPerson(new Person(resultSet.getInt("id_person"), resultSet.getString("firstname"),
                        resultSet.getString("lastname"), resultSet.getString("street"),
                        resultSet.getString("city"), resultSet.getString("psc"),
                        resultSet.getString("email"), new LinkedList<>()));

                owner.setProperty(new Property(resultSet.getInt("id_property"),
                        propertyRepository.toPropertyType(resultSet.getString("property_type")),
                        resultSet.getString("property_name"), resultSet.getString("property_description")
                ));
                owner.setValidFrom(new Date(resultSet.getDate("valid_from").getTime()));
                owner.setValidTo(new Date(resultSet.getDate("valid_to").getTime()));
                ownerLinkedList.add(owner);
            }
            connection.close();
            statement.close();
            return ownerLinkedList;

        } catch (SQLException exception) {
            System.err.println("Error getOwnerHistory " + exception.getMessage());

            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getOwnerHistory " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query to Oracle database, which returns all records from Owner table according to id of property and initializes all objects.
     *
     * @param property @see Property
     * @return List of @see Owner type objects
     */
    public List<Owner> getOwnersListOfProperty(Property property) {
        String query = "SELECT owner.id_owner, owner.valid_from, owner.valid_to, property.*, person.*  " +
                "FROM owner " +
                "JOIN person ON(owner.id_owner=person.id_person) " +
                "JOIN property ON(owner.id_property=property.id_property) " +
                "WHERE owner.id_property = ? " +
                "ORDER BY owner.valid_from";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);

            LinkedList<Owner> ownersList = new LinkedList<>();

            while (resultSet.next()) {
                Owner owner = new Owner();
                owner.setProperty(new Property(resultSet.getInt("id_property"),
                        propertyRepository.toPropertyType(resultSet.getString("property_type")),
                        resultSet.getString("property_name"), resultSet.getString("property_description")
                ));
                owner.setValidFrom(new Date(resultSet.getDate("valid_from").getTime()));
                owner.setValidTo(new Date(resultSet.getDate("valid_to").getTime()));

                Person person = new Person();
                person.setIdPerson(resultSet.getInt("id_person"));
                person.setFirstName(resultSet.getString("firstname"));
                person.setLastName(resultSet.getString("lastname"));
                person.setStreet(resultSet.getString("street"));
                person.setCity(resultSet.getString("city"));
                person.setPsc(resultSet.getString("psc"));
                person.setEmail(resultSet.getString("email"));
                person.setPropertyHistory(getOwnerHistory(person));

                owner.setPerson(person);

                ownersList.add(owner);
            }


            connection.close();
            statement.close();

            return ownersList;

        } catch (SQLException exception) {
            System.err.println("Error getOwnersListOfProperty" + exception.getMessage());

            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getOwnersListOfProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Owner to Oracle database, which returns record, according to given parameters.
     *
     * @param oldOwner @see Owner typed object, which stores attributes for query
     * @return @see Owner
     */
    public Owner getOwner(Owner oldOwner) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = oldOwner.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(oldOwner.getValidFrom().getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = oldOwner.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(oldOwner.getValidTo().getTime());

        String query = "SELECT owner.* " +
                "FROM owner " +
                "WHERE owner.id_owner=? AND owner.id_property=? AND owner.valid_from=? AND owner.valid_to=?";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);

            statement.setInt(1, oldOwner.getPerson().getIdPerson());
            statement.setInt(2, oldOwner.getProperty().getIdProperty());
            statement.setDate(3, sqlDateFrom);
            statement.setDate(4, sqlDateTo);

            ResultSet resultSet = statement.executeQuery();
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);
            PersonRepository personRepository = new PersonRepository(dataSource);


            if (resultSet.next()) {
                Owner owner = new Owner();
                owner.getPerson().setIdPerson(resultSet.getInt("id_owner"));
                owner.getProperty().setIdProperty(resultSet.getInt("id_property"));
                owner.setValidFrom(new Date(resultSet.getDate("valid_from").getTime()));
                owner.setValidTo(new Date(resultSet.getDate("valid_to").getTime()));

                connection.close();
                statement.close();


                //create person and property
                owner.setPerson(personRepository.getPerson(owner.getPerson()));
                owner.setProperty(propertyRepository.getProperty(owner.getProperty()));
                return owner;

            } else {
                connection.close();
                statement.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error getOwner " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getOwner " + exception.getMessage());
            }
        }

    }

    /**
     * Method calls query under table Owner to Oracle database, which creates a record, according to given parameters.
     *
     * @param property property
     * @param person   person
     * @param from     from
     * @param to       to
     * @return boolean True if query was successful otherwise False.
     */
    public boolean createOwner(Property property, Person person, Date from, Date to) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = from == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(from.getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = to == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(to.getTime());

        String query = "CALL temporal_insert('owner', ?, ?, TO_DATE('" + sqlDateFrom + "','yyyy-mm-dd'),TO_DATE('" + sqlDateTo + "','yyyy-mm-dd'))";

        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());
            statement.setInt(2, person.getIdPerson());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error createOwner " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error createOwner " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Owner to Oracle database, which updates a record, according to given parameters.
     *
     * @param property property
     * @param person   person
     * @param from     from
     * @param to       to
     * @return boolean True if query was successful otherwise False.
     */
    public boolean updateOwner(Property property, Person person, Date from, Date to) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = from == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(from.getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = to == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(to.getTime());

        String query = "CALL temporal_update('owner',?,?, TO_DATE('" + sqlDateFrom + "','yyyy-mm-dd'),TO_DATE('" + sqlDateTo + "','yyyy-mm-dd'))";

        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());
            statement.setInt(2, person.getIdPerson());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error updateOwner " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error updateOwner " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Owner to Oracle database, which deletes a record, according to given parameters.
     *
     * @param property property
     * @param from     from
     * @param to       to
     * @return boolean True if query was successful otherwise False.
     */
    public boolean deleteOwner(Property property, Date from, Date to) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = from == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(from.getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = to == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(to.getTime());

        String query = "CALL temporal_delete('owner',?,TO_DATE('" + sqlDateFrom + "','yyyy-mm-dd'),TO_DATE('" + sqlDateTo + "','yyyy-mm-dd')) ";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error deleteOwner " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error deleteOwner " + exception.getMessage());
            }
        }
    }

    /**
     * Method creates @see Owner type object with given parameters and calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @param idProperty Integer value, which represents id property
     * @param from       @see Date value from desired time interval
     * @param to         @see Date value to desired time interval
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersListOfFromToDate(Integer idProperty, Date from, Date to) {
        Owner owner = new Owner();
        owner.getProperty().setIdProperty(idProperty);
        owner.setValidFrom(from);
        owner.setValidTo(to);
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method creates @see Owner type object with given parameters and calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @param from @see Date value from desired time interval
     * @param to   @see Date value to desired time interval
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersListOfFromToDate(Date from, Date to) {
        Owner owner = new Owner();
        owner.setValidFrom(from);
        owner.setValidTo(to);
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method creates @see Owner type object with given parameters and calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @param idProperty Integer value, which represents id property
     * @param from       @see Date value from desired time interval
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersListOfFromDate(Integer idProperty, Date from) {
        Owner owner = new Owner();
        owner.getProperty().setIdProperty(idProperty);
        owner.setValidFrom(from);
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method creates @see Owner type object with given parameters and calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @param from @see Date value from desired time interval
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersListOfFromDate(Date from) {
        Owner owner = new Owner();
        owner.setValidFrom(from);
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method creates @see Owner type object with given parameters and calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @param idProperty Integer value, which represents id property
     * @param to         @see Date value to desired time interval
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersListOfToDate(Integer idProperty, Date to) {
        Owner owner = new Owner();
        owner.getProperty().setIdProperty(idProperty);
        owner.setValidTo(to);
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method creates @see Owner type object with given parameters and calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @param to @see Date value to desired time interval
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersListOfToDate(Date to) {
        Owner owner = new Owner();
        owner.setValidTo(to);
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method creates @see Owner type object with given parameters and calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @param idProperty Integer value, which represents id property
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersList(Integer idProperty) {
        Owner owner = new Owner();
        owner.getProperty().setIdProperty(idProperty);
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method calls @see OwnerRepository#getOwnersListOfDate(Owner owner).
     *
     * @return List of @see Owner typed objects
     */
    public List<Owner> getOwnersList() {
        Owner owner = new Owner();
        return this.getOwnersListOfDate(owner);
    }

    /**
     * Method calls query under table Owner to Oracle database, which returns owners according to given @see Owner object and its attributes
     *
     * @param owner @see Owner
     * @return List of @see Owner objects
     */
    private List<Owner> getOwnersListOfDate(Owner owner) {

        String queryProperty = "SELECT * " +
                "FROM owner WHERE id_property = ? " +
                "ORDER BY valid.from";

        String queryPropertyTime = "SELECT owner.* " +
                "FROM owner " +
                "WHERE \n" +
                "( owner.id_property=? AND (owner.valid_from >= ?) AND (owner.valid_to <= ?) ) OR\n" +
                "( owner.id_property=? AND (? BETWEEN owner.valid_from AND owner.valid_to) OR \n" +
                "(? BETWEEN owner.valid_from AND owner.valid_to)) " +
                "ORDER BY owner.valid_from";

        String queryPropertyTimeFrom = "SELECT owner.* " +
                "FROM owner " +
                "WHERE \n" +
                "( owner.id_property=? AND (owner.valid_from >= ?)) OR\n" +
                "( owner.id_property=? AND (? BETWEEN owner.valid_from AND owner.valid_to)) " +
                "ORDER BY owner.valid_from";

        String queryPropertyTimeTo = "SELECT owner.* " +
                "FROM owner " +
                "WHERE \n" +
                "( owner.id_property=? AND (owner.valid_to <= ?) ) OR\n" +
                "( owner.id_property=? AND (? BETWEEN owner.valid_from AND owner.valid_to)) " +
                "ORDER BY owner.valid_from";

        String queryTime = "SELECT owner.* " +
                "FROM owner " +
                "WHERE \n" +
                "( (owner.valid_from >= ?) AND (owner.valid_to <= ?) ) OR\n" +
                "( ( ? BETWEEN owner.valid_from AND owner.valid_to) OR \n" +
                "( ? BETWEEN owner.valid_from AND owner.valid_to)) " +
                "ORDER BY owner.valid_from";

        String queryTimeFrom = "SELECT owner.* " +
                "FROM owner " +
                "WHERE \n" +
                "( (owner.valid_from >= ?)) OR\n" +
                "( (? BETWEEN owner.valid_from AND owner.valid_to)) " +
                "ORDER BY owner.valid_from";

        String queryTimeTo = "SELECT owner.* " +
                "FROM owner " +
                "WHERE \n" +
                "( (owner.valid_to <= ?)) OR\n" +
                "( (? BETWEEN owner.valid_from AND owner.valid_to)) " +
                "ORDER BY owner.valid_from";

        String query = "SELECT owner.* FROM owner ";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement;
            if (owner.getProperty().getIdProperty() != 0) {
                if (owner.getValidFrom() != null && owner.getValidTo() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = owner.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(owner.getValidFrom().getTime());
                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = owner.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(owner.getValidTo().getTime());

                    statement = connection.prepareStatement(queryPropertyTime);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    statement.setDate(2, sqlDateFrom);
                    statement.setDate(3, sqlDateTo);
                    statement.setInt(4, owner.getProperty().getIdProperty());
                    statement.setDate(5, sqlDateFrom);
                    statement.setDate(6, sqlDateTo);
                    if (App.isDebug()) {
                        System.out.println("queryPropertyTime");
                    }
                } else if (owner.getProperty().getIdProperty() != 0 && owner.getValidFrom() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = owner.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(owner.getValidFrom().getTime());

                    statement = connection.prepareStatement(queryPropertyTimeFrom);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    statement.setDate(2, sqlDateFrom);
                    statement.setInt(3, owner.getProperty().getIdProperty());
                    statement.setDate(4, sqlDateFrom);
                    if (App.isDebug()) {
                        System.out.println("queryPropertyTimeFrom");
                    }
                } else if (owner.getProperty().getIdProperty() != 0 && owner.getValidTo() != null) {

                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = owner.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(owner.getValidTo().getTime());

                    statement = connection.prepareStatement(queryPropertyTimeTo);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    statement.setDate(2, sqlDateTo);
                    statement.setInt(3, owner.getProperty().getIdProperty());
                    statement.setDate(4, sqlDateTo);
                    if (App.isDebug()) {
                        System.out.println("queryPropertyTimeTo");
                    }
                } else {
                    statement = connection.prepareStatement(queryProperty);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    if (App.isDebug()) {
                        System.out.println("queryProperty");
                    }
                }
            } else {
                if (owner.getValidFrom() != null && owner.getValidTo() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = owner.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(owner.getValidFrom().getTime());
                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = owner.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(owner.getValidTo().getTime());

                    statement = connection.prepareStatement(queryTime);
                    statement.setDate(1, sqlDateFrom);
                    statement.setDate(2, sqlDateTo);
                    statement.setDate(3, sqlDateFrom);
                    statement.setDate(4, sqlDateTo);
                    if (App.isDebug()) {
                        System.out.println("queryTime");
                    }
                } else if (owner.getProperty().getIdProperty() != 0 && owner.getValidFrom() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = owner.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(owner.getValidFrom().getTime());

                    statement = connection.prepareStatement(queryTimeFrom);
                    statement.setDate(1, sqlDateFrom);
                    statement.setDate(2, sqlDateFrom);
                    if (App.isDebug()) {
                        System.out.println("queryTimeFrom");
                    }
                } else if (owner.getProperty().getIdProperty() != 0 && owner.getValidTo() != null) {

                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = owner.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(owner.getValidTo().getTime());

                    statement = connection.prepareStatement(queryTimeTo);
                    statement.setDate(1, sqlDateTo);
                    statement.setDate(2, sqlDateTo);
                    if (App.isDebug()) {
                        System.out.println("queryTimeTo");
                    }
                } else {
                    statement = connection.prepareStatement(query);
                    if (App.isDebug()) {
                        System.out.println("query");
                    }
                }
            }

            LinkedList<Owner> ownerLinkedList = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery();

            PersonRepository personRepository = new PersonRepository(dataSource);
            //PropertyRepository propertyRepository = new PropertyRepository(dataSource);

            while (resultSet.next()) {
                Owner o = new Owner();
                o.getPerson().setIdPerson(resultSet.getInt("id_owner"));
                o.getProperty().setIdProperty(resultSet.getInt("id_property"));
                o.setValidFrom(new Date(resultSet.getDate("valid_from").getTime()));
                o.setValidTo(new Date(resultSet.getDate("valid_to").getTime()));
                ownerLinkedList.add(o);
            }
            connection.close();
            statement.close();

            //get persons and properties
            for (Owner o : ownerLinkedList) {
                o.setPerson(personRepository.getPerson(o.getPerson()));
                // currently not necessary
                //o.setProperty(propertyRepository.getProperty(o.getProperty()));
            }

            return ownerLinkedList;

        } catch (SQLException exception) {
            System.err.println("Error getOwnersListOfDate " + exception.getMessage());

            return new LinkedList<>();

        } catch (NullPointerException exception) {
            System.err.println("Error getOwnersListOfDate " + exception.getMessage());
            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getOwnersListOfDate " + exception.getMessage());
            }
        }
    }
}
