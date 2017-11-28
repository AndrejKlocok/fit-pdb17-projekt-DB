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
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Property price repository creates Property price type objects (@see PropertyPrice), queries and calls to Oracle database.
 * Repository works mainly with table Property_Price.
 * Class extends @see Observable.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class PropertyPriceRepository extends Observable {

    private OracleDataSource dataSource;

    /**
     * Constructor for property price repository @see PropertyPriceRepository.
     *
     * @param dataSource @see OracleDataSource
     */
    public PropertyPriceRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     *
     * @param idProperty Integer value, which represents id property
     * @param from       @see Date value from desired time interval
     * @param to         @see Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     */
    public List<PropertyPrice> getPropertyPriceListOfFromToDate(Integer idProperty, Date from, Date to) {
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(idProperty);
        propertyPrice.setValidFrom(from);
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     *
     * @param from @see Date value from desired time interval
     * @param to   @see Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     */
    public List<PropertyPrice> getPropertyPriceListOfFromToDate(Date from, Date to) {
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.setValidFrom(from);
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     *
     * @param idProperty Integer value, which represents id property
     * @param from       @see Date value from desired time interval
     * @return List of @see PropertyPrice typed objects
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     */
    public List<PropertyPrice> getPropertyPriceListOfFromDate(Integer idProperty, Date from) {
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(idProperty);
        propertyPrice.setValidFrom(from);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     *
     * @param from @see Date value from desired time interval
     * @return List of @see PropertyPrice typed objects
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     */
    public List<PropertyPrice> getPropertyPriceListOfFromDate(Date from) {
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.setValidFrom(from);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     *
     * @param idProperty Integer value, which represents id property
     * @param to         @see Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     */
    public List<PropertyPrice> getPropertyPriceListOfToDate(Integer idProperty, Date to) {
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(idProperty);
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     *
     * @param to @see Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     */
    public List<PropertyPrice> getPropertyPriceListOToDate(Date to) {
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     *
     * @param idProperty Integer value, which represents id property
     * @return List of @see PropertyPrice typed objects
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     */
    public List<PropertyPrice> getPropertyPriceList(Integer idProperty) {
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(idProperty);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method calls @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     *
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceList() {
        PropertyPrice propertyPrice = new PropertyPrice();
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method calls query under table Property_price to Oracle database, which returns property_price records according
     * to given @see PropertyPrice object and its attributes
     *
     * @param propertyPrice @see PropertyPrice
     * @return List of @see PropertyPrice objects
     */
    private List<PropertyPrice> getPropertyPriceListOfProperty(PropertyPrice propertyPrice) {

        String queryProperty = "SELECT * " +
                "FROM property_price " +
                "WHERE id_property = ? " +
                "ORDER BY property_price.valid_from";

        String queryPropertyTime = "SELECT property_price.* " +
                "FROM property_price " +
                "WHERE \n" +
                "( property_price.id_property=? AND ( property_price.valid_from >= ? ) AND (property_price.valid_to <= ?) ) OR\n" +
                "( property_price.id_property=? AND ( ? BETWEEN property_price.valid_from AND property_price.valid_to) OR \n" +
                "( ? BETWEEN property_price.valid_from AND property_price.valid_to)) " +
                "ORDER BY property_price.valid_from";

        String queryPropertyTimeFrom = "SELECT property_price.* " +
                "FROM property_price " +
                "WHERE \n" +
                "( property_price.id_property=? AND ( property_price.valid_from >= ? )) OR\n" +
                "( property_price.id_property=? AND ( ? BETWEEN property_price.valid_from AND property_price.valid_to)) " +
                "ORDER BY property_price.valid_from";

        String queryPropertyTimeTo = "SELECT property_price.* " +
                "FROM property_price " +
                "WHERE \n" +
                "( property_price.id_property=? AND ( property_price.valid_to <= ? ) ) OR\n" +
                "( property_price.id_property=? AND ( ? BETWEEN property_price.valid_from AND property_price.valid_to)) " +
                "ORDER BY property_price.valid_from";


        String queryTime = "SELECT property_price.* " +
                "FROM property_price " +
                "WHERE \n" +
                "( ( property_price.valid_from >= ? ) AND (property_price.valid_to <= ? ) ) OR\n" +
                "( ( ? BETWEEN property_price.valid_from AND property_price.valid_to) OR \n" +
                "( ? BETWEEN property_price.valid_from AND property_price.valid_to)) " +
                "ORDER BY property_price.valid_from";

        String queryTimeFrom = "SELECT property_price.* " +
                "FROM property_price " +
                "WHERE \n" +
                "( ( property_price.valid_from >= ? )) OR\n" +
                "( ( ? BETWEEN property_price.valid_from AND property_price.valid_to)) " +
                "ORDER BY property_price.valid_from";

        String queryTimeTo = "SELECT property_price.* " +
                "FROM property_price " +
                "WHERE \n" +
                "( ( property_price.valid_to <= ?) ) OR\n" +
                "( ( ? BETWEEN property_price.valid_from AND property_price.valid_to)) " +
                "ORDER BY property_price.valid_from";

        String query = "SELECT property_price.* " +
                "FROM property_price " +
                "ORDER BY property_price.valid_from";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement;
            if (propertyPrice.getProperty().getIdProperty() != 0) {
                if (propertyPrice.getValidFrom() != null && propertyPrice.getValidTo() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = propertyPrice.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(propertyPrice.getValidFrom().getTime());
                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = propertyPrice.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(propertyPrice.getValidTo().getTime());

                    statement = connection.prepareStatement(queryPropertyTime);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(2, sqlDateFrom);
                    statement.setDate(3, sqlDateTo);
                    statement.setInt(4, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(5, sqlDateFrom);
                    statement.setDate(6, sqlDateTo);
                    if (App.isDebug()) {
                        System.out.println("queryPropertyTime");
                    }
                } else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidFrom() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = propertyPrice.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(propertyPrice.getValidFrom().getTime());

                    statement = connection.prepareStatement(queryPropertyTimeFrom);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(2, sqlDateFrom);
                    statement.setInt(3, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(4, sqlDateFrom);
                    if (App.isDebug()) {
                        System.out.println("queryPropertyTimeFrom");
                    }
                } else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidTo() != null) {

                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = propertyPrice.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(propertyPrice.getValidTo().getTime());

                    statement = connection.prepareStatement(queryPropertyTimeTo);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(2, sqlDateTo);
                    statement.setInt(3, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(4, sqlDateTo);
                    if (App.isDebug()) {
                        System.out.println("queryPropertyTimeTo");
                    }
                } else {
                    statement = connection.prepareStatement(queryProperty);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    if (App.isDebug()) {
                        System.out.println("queryProperty");
                    }
                }
            } else {
                if (propertyPrice.getValidFrom() != null && propertyPrice.getValidTo() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = propertyPrice.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(propertyPrice.getValidFrom().getTime());
                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = propertyPrice.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(propertyPrice.getValidTo().getTime());

                    statement = connection.prepareStatement(queryTime);
                    statement.setDate(1, sqlDateFrom);
                    statement.setDate(2, sqlDateTo);
                    statement.setDate(3, sqlDateFrom);
                    statement.setDate(4, sqlDateTo);
                    if (App.isDebug()) {
                        System.out.println("queryTime");
                    }
                } else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidFrom() != null) {

                    // if from date is not set, than set zero date (1970)
                    java.sql.Date sqlDateFrom = propertyPrice.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(propertyPrice.getValidFrom().getTime());

                    statement = connection.prepareStatement(queryTimeFrom);
                    statement.setDate(1, sqlDateFrom);
                    statement.setDate(2, sqlDateFrom);
                    if (App.isDebug()) {
                        System.out.println("queryTimeFrom");
                    }
                } else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidTo() != null) {

                    // if to date is not set, than set maximum SQL date
                    java.sql.Date sqlDateTo = propertyPrice.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(propertyPrice.getValidTo().getTime());

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


            ResultSet resultSet = statement.executeQuery();
            LinkedList<PropertyPrice> propertyPriceList = new LinkedList<>();

            while (resultSet.next()) {
                PropertyPrice pp = new PropertyPrice();
                pp.setIdPropertyPrice(resultSet.getInt("id_price"));
                pp.getProperty().setIdProperty(resultSet.getInt("id_property"));
                pp.setPrice(resultSet.getDouble("price"));
                pp.setValidFrom(resultSet.getDate("valid_from"));
                pp.setValidTo(resultSet.getDate("valid_to"));
                propertyPriceList.add(pp);
            }

            connection.close();
            statement.close();

            return propertyPriceList;

        } catch (SQLException exception) {
            System.err.println("Error getPropertyPriceListOfProperty " + exception.getMessage());

            return new LinkedList<>();
        } catch (NullPointerException exception) {
            System.err.println("Error getPropertyPriceListOfProperty " + exception.getMessage());
            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyPriceListOfProperty " + exception.getMessage());
            }
        }

    }

    /**
     * Method calls query under table Property_price to Oracle database, which returns record, according to given parameters.
     *
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return @see PropertyPrice
     */
    public PropertyPrice getPropertyPrice(PropertyPrice propertyPrice) {
        String query = "SELECT * " +
                "FROM property_price " +
                "WHERE id_price = ? " +
                "ORDER BY property_price.valid_from";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getIdPropertyPrice());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                PropertyPrice newPropertyPrice = new PropertyPrice();
                newPropertyPrice.setIdPropertyPrice(resultSet.getInt("id_price"));
                newPropertyPrice.getProperty().setIdProperty(resultSet.getInt("id_property"));
                newPropertyPrice.setPrice(resultSet.getDouble("price"));
                newPropertyPrice.setValidFrom(resultSet.getDate("valid_from"));
                newPropertyPrice.setValidTo(resultSet.getDate("valid_to"));

                connection.close();
                statement.close();


                return newPropertyPrice;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error getPropertyPrice " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyPrice " + exception.getMessage());
            }
        }

    }

    /**
     * Method calls query under table Property_price to Oracle database, which inserts record, according to given parameters.
     *
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean createPropertyPrice(PropertyPrice propertyPrice) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = propertyPrice.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(propertyPrice.getValidFrom().getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = propertyPrice.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(propertyPrice.getValidTo().getTime());

        String query = "CALL temporal_insert('property_price', ?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getProperty().getIdProperty());
            statement.setDouble(2, propertyPrice.getPrice());
            statement.setDate(3, sqlDateFrom);
            statement.setDate(4, sqlDateTo);

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error createPropertyPrice " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error createPropertyPrice " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property_price to Oracle database, which deletes record, according to given parameters.
     *
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean deletePropertyPrice(PropertyPrice propertyPrice) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = propertyPrice.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(propertyPrice.getValidFrom().getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = propertyPrice.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(propertyPrice.getValidTo().getTime());

        String query = "CALL temporal_delete('property_price', ?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getProperty().getIdProperty());
            statement.setDate(2, sqlDateFrom);
            statement.setDate(3, sqlDateTo);

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error deletePropertyPrice " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error deletePropertyPrice " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property_price to Oracle database, which updates record, according to given parameters.
     *
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean savePropertyPrice(PropertyPrice propertyPrice) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = propertyPrice.getValidFrom() == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(propertyPrice.getValidFrom().getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = propertyPrice.getValidTo() == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(propertyPrice.getValidTo().getTime());

        String query = "CALL temporal_update('property_price',?,?,?,?)";
        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getProperty().getIdProperty());
            statement.setDouble(2, propertyPrice.getPrice());
            statement.setDate(3, sqlDateFrom);
            statement.setDate(4, sqlDateTo);

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error savePropertyPrice " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error savePropertyPrice " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query to Oracle database, which returns the average price of property in desired time interval.
     *
     * @param idProperty Integer value, id of property
     * @param dateFrom   @see Date, Date value from desired time interval
     * @param dateTo     @see Date, Date value to desired time interval
     * @return Integer value, which represents average price of property
     */
    public Double getAvgPropertyPrice(Integer idProperty, Date dateFrom, Date dateTo) {

        // if from date is not set, than set zero date (1970)
        java.sql.Date sqlDateFrom = dateFrom == null ? new java.sql.Date((new Date(0)).getTime()) : new java.sql.Date(dateFrom.getTime());
        // if to date is not set, than set maximum SQL date
        java.sql.Date sqlDateTo = dateTo == null ? java.sql.Date.valueOf("9999-12-30") : new java.sql.Date(dateTo.getTime());

        String query = "SELECT  ROUND(AVG(PP.price),0) AS AvgPrice" +
                "                FROM property_price PP RIGHT JOIN property P ON(PP.id_property=P.id_property)  WHERE" +
                "                ( P.id_property=? AND (PP.valid_from >= ? ) AND (PP.valid_to <= ?) ) OR" +
                "                ( P.id_property=? AND ( (? BETWEEN PP.valid_from AND PP.valid_to) OR" +
                "                ( ? BETWEEN PP.valid_from AND PP.valid_to)))" +
                "                GROUP BY PP.id_property, P.property_name";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, idProperty);
            statement.setDate(2, sqlDateFrom);
            statement.setDate(3, sqlDateTo);
            statement.setInt(4, idProperty);
            statement.setDate(5, sqlDateFrom);
            statement.setDate(6, sqlDateTo);

            Double avgPrice;
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                avgPrice = resultSet.getDouble("AvgPrice");
            } else {
                avgPrice = 0.0;
            }
            connection.close();
            statement.close();

            return avgPrice;

        } catch (SQLException exception) {
            System.err.println("Error getAvgPropertyPrice " + exception.getMessage());

            return 0.0;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getAvgPropertyPrice " + exception.getMessage());
            }
        }
    }
}
