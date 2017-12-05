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

import cz.vutbr.fit.pdb.core.model.GroundPlan;
import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;

import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Property repository creates property type objects (@see Property), queries and calls to Oracle database.
 * Repository works mainly with table Property.
 * Class extends @see Observable.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class PropertyRepository extends Observable {

    private OracleDataSource dataSource;

    /**
     * Constructor for property repository @see PropertyRepository.
     *
     * @param dataSource @see OracleDataSource
     */
    public PropertyRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Method calls query under table Property to Oracle database, which returns all records from table and initializes all objects.
     *
     * @return List of @see Property objects
     */
    public List<Property> getPropertyList() {
        String query = "SELECT * " +
                "FROM property " +
                "ORDER BY property_name";
        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Property> propertyList = new LinkedList<>();
            while (resultSet.next()) {
                Property property = new Property();
                property.setIdProperty(resultSet.getInt("id_property"));
                property.setType(toPropertyType(resultSet.getString("property_type")));
                Struct st = (Struct) resultSet.getObject("geometry");
                property.setGeometry(JGeometry.loadJS(st));
                property.setName(resultSet.getString("property_name"));
                property.setDescription(resultSet.getString("property_description"));
                propertyList.add(property);

                // load property ground plans
                GroundPlanRepository groundPlanRepository = new GroundPlanRepository(dataSource);
                List<GroundPlan> groundPlanList = groundPlanRepository.getGroundPlanListOfProperty(property);
                property.setGroundPlans(groundPlanList);

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceList(property.getIdProperty());
                property.setPriceHistory(propertyPriceList);

                // load property owner history
                OwnerRepository ownerRepository = new OwnerRepository(dataSource);
                List<Owner> ownerList = ownerRepository.getOwnersListOfProperty(property);
                property.setOwnerHistory(ownerList);
            }

            connection.close();
            statement.close();
            return propertyList;

        } catch (SQLException exception) {
            System.err.println("Error getPropertyList " + exception.getMessage());

            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyList " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property to Oracle database, which returns record, according to given parameters.
     *
     * @param property @see Property, which stores attributes for query (id_property)
     * @return @see Property object
     */
    public Property getProperty(Property property) {
        String query = "SELECT * " +
                "FROM property " +
                "WHERE id_property = ?";
        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Property newProperty = new Property();
                newProperty.setIdProperty(resultSet.getInt("id_property"));
                newProperty.setType(toPropertyType(resultSet.getString("property_type")));
                Struct st = (Struct) resultSet.getObject("geometry");
                newProperty.setGeometry(JGeometry.loadJS(st));
                newProperty.setName(resultSet.getString("property_name"));
                newProperty.setDescription(resultSet.getString("property_description"));

                // load property ground plans
                GroundPlanRepository groundPlanRepository = new GroundPlanRepository(dataSource);
                List<GroundPlan> groundPlanList = groundPlanRepository.getGroundPlanListOfProperty(property);
                newProperty.setGroundPlans(groundPlanList);

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceList(property.getIdProperty());
                newProperty.setPriceHistory(propertyPriceList);

                // load property owner history
                OwnerRepository ownerRepository = new OwnerRepository(dataSource);
                List<Owner> ownerList = ownerRepository.getOwnersListOfProperty(property);
                newProperty.setOwnerHistory(ownerList);

                connection.close();
                statement.close();
                return newProperty;
            } else {

                statement.close();
                connection.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error getProperty " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Owner to Oracle database, which returns record, according to given parameter.
     *
     * @param idProperty Integer value, which represents id of property
     * @return @see Property object
     */
    public Property getPropertyById(int idProperty) {
        String query = "SELECT * " +
                "FROM property " +
                "WHERE id_property = ?";

        Property property;
        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, idProperty);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                property = new Property();
                property.setIdProperty(resultSet.getInt("id_property"));
                property.setType(toPropertyType(resultSet.getString("property_type")));
                Struct st = (Struct) resultSet.getObject("geometry");
                property.setGeometry(JGeometry.loadJS(st));
                property.setName(resultSet.getString("property_name"));
                property.setDescription(resultSet.getString("property_description"));

                // load property ground plans
                GroundPlanRepository groundPlanRepository = new GroundPlanRepository(dataSource);
                List<GroundPlan> groundPlanList = groundPlanRepository.getGroundPlanListOfProperty(property);
                property.setGroundPlans(groundPlanList);

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceList(property.getIdProperty());
                property.setPriceHistory(propertyPriceList);

                // load property owner history
                OwnerRepository ownerRepository = new OwnerRepository(dataSource);
                List<Owner> ownerList = ownerRepository.getOwnersListOfProperty(property);
                property.setOwnerHistory(ownerList);

                connection.close();
                statement.close();
                return property;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error getPropertyById" + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyById " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property to Oracle database, which creates a record, according to given parameters.
     *
     * @param property @see Property typed object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean createProperty(Property property) {
        String query = "INSERT INTO property(id_property, property_type, geometry, property_name, property_description) "
                + "VALUES(property_seq.nextval, ?,?,?,?)";
        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, toDbType(property.getType()));
            Struct obj = JGeometry.storeJS(property.getGeometry(), connection);
            statement.setObject(2, obj);
            statement.setString(3, property.getName());
            statement.setString(4, property.getDescription());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;

        } catch (SQLException exception) {
            System.err.println("Error createProperty " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error createProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property to Oracle database, which updates a record, according to given parameters.
     *
     * @param property @see Property typed object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean saveProperty(Property property) {
        String query = "UPDATE property " +
                "SET property_type = ?, geometry = ?, property_name = ?, property_description = ? " +
                "WHERE id_property = ? ";

        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);

            statement.setString(1, toDbType(property.getType()));
            Struct obj = JGeometry.storeJS(property.getGeometry(), connection);
            statement.setObject(2, obj);
            statement.setString(3, property.getName());
            statement.setString(4, property.getDescription());
            statement.setInt(5, property.getIdProperty());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;

        } catch (SQLException exception) {
            System.err.println("Error saveProperty" + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error saveProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property to Oracle database, which deletes a record, according to given parameters.
     *
     * @param property @see Property typed object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean deleteProperty(Property property) {
        String query = "DELETE " +
                "FROM property " +
                "WHERE id_property = ?";
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
            // give observers name of method
            notifyObservers(new Object(){}.getClass().getEnclosingMethod().getName());

            return true;
        } catch (SQLException exception) {
            System.err.println("Error deleteProperty " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error deleteProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground_Plan to Oracle database, which finds most similar ground plans of all properties .
     *
     * @param groundPlans List of @GroundPlan
     * @return List of @see Property typed objects
     */
    public List<Property> getPropertyListSimilarByGroundPlans(List<GroundPlan> groundPlans, boolean filterHasNotOwner) {
        // TODO option to get similar by more ground plans

        GroundPlan groundPlan;
        if (groundPlans.size() > 0) {
            groundPlan = groundPlans.get(0);
        } else {
            // no ground plan, no similar property list

            return new LinkedList<>();
        }

        String query = "" +
                "SELECT " +
                "dst.*, " +
                "   SI_ScoreByFtrList(new SI_FeatureList(src.img_ac, ?, src.img_ch, ?, src.img_pc, ?, src.img_tx, ?), dst.img_si) AS similarity " +
                "FROM" +
                "   (SELECT property.*, id_ground_plan, img, img_ac, img_ch, img_pc, img_tx, img_si FROM ground_plan LEFT OUTER JOIN property ON(property.id_property=ground_plan.id_property)) src, " +
                "   (SELECT property.*, id_ground_plan, img, img_ac, img_ch, img_pc, img_tx, img_si FROM ground_plan LEFT OUTER JOIN property ON(property.id_property=ground_plan.id_property)) dst " +
                "WHERE " +
                "   (src.id_ground_plan <> dst.id_ground_plan) AND src.id_ground_plan = ? AND dst.id_property <> ? " +
                "ORDER BY " +
                "   similarity ASC";

        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setDouble(1, 0.1);    // average image color
            statement.setDouble(2, 0.1);    // color (distribution) histogram
            statement.setDouble(3, 0.4);    // color (most significant) position
            statement.setDouble(4, 0.4);    // repeated patterns, variation of contrast and directions in image
            statement.setInt(5, groundPlan.getIdGroundPlan());
            statement.setInt(6, groundPlan.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Property> propertyList = new LinkedList<>();
            while (resultSet.next()) {
                Property property = new Property();
                property.setIdProperty(resultSet.getInt("id_property"));
                property.setType(toPropertyType(resultSet.getString("property_type")));
                Struct st = (Struct) resultSet.getObject("geometry");
                property.setGeometry(JGeometry.loadJS(st));
                property.setName(resultSet.getString("property_name"));
                property.setDescription(resultSet.getString("property_description"));
                propertyList.add(property);

                // load property ground plans
                GroundPlanRepository groundPlanRepository = new GroundPlanRepository(dataSource);
                List<GroundPlan> groundPlanList = groundPlanRepository.getGroundPlanListOfProperty(property);
                property.setGroundPlans(groundPlanList);

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceList(property.getIdProperty());
                property.setPriceHistory(propertyPriceList);

                // load property owner history
                OwnerRepository ownerRepository = new OwnerRepository(dataSource);
                List<Owner> ownerList = ownerRepository.getOwnersListOfProperty(property);
                property.setOwnerHistory(ownerList);
            }

            if (filterHasNotOwner) {
                // owner filter is set
                for (Iterator<Property> iterator = propertyList.iterator(); iterator.hasNext(); ) {
                    Property property = iterator.next();
                    // remove from list only property with owner
                    if (property.hasOwner()) {
                        iterator.remove();
                    }
                }
            }

            connection.close();
            statement.close();
            return propertyList;

        } catch (SQLException exception) {
            System.err.println("Error getPropertyListSimilarByGroundPlans" + exception.getMessage());

            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyListSimilarByGroundPlans " + exception.getMessage());
            }
        }
    }

    /**
     * Search property list by given search parameters
     *
     * @param name     name of property
     * @param price    current price of property
     * @param hasOwner if property might has currently owner
     * @return found property list
     */
    public LinkedList<Property> searchPropertyList(String name, Double price, boolean hasOwner) {
        String query = "SELECT * " +
                "FROM property "
                + (!name.isEmpty() ? "WHERE property_name LIKE ? " : "") +
                "ORDER BY property_name";

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            if (!name.isEmpty()) {
                statement.setString(1, "%" + name + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Property> propertyList = new LinkedList<>();
            while (resultSet.next()) {
                Property property = new Property();
                property.setIdProperty(resultSet.getInt("id_property"));
                property.setType(toPropertyType(resultSet.getString("property_type")));
                Struct st = (Struct) resultSet.getObject("geometry");
                property.setGeometry(JGeometry.loadJS(st));
                property.setName(resultSet.getString("property_name"));
                property.setDescription(resultSet.getString("property_description"));

                // load property ground plans
                GroundPlanRepository groundPlanRepository = new GroundPlanRepository(dataSource);
                List<GroundPlan> groundPlanList = groundPlanRepository.getGroundPlanListOfProperty(property);
                property.setGroundPlans(groundPlanList);

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceList(property.getIdProperty());
                property.setPriceHistory(propertyPriceList);

                // load property owner history
                OwnerRepository ownerRepository = new OwnerRepository(dataSource);
                List<Owner> ownerList = ownerRepository.getOwnersListOfProperty(property);
                property.setOwnerHistory(ownerList);

                propertyList.add(property);
            }

            if (price != 0d) {
                // price filter is set
                for (Iterator<Property> iterator = propertyList.iterator(); iterator.hasNext(); ) {
                    Property property = iterator.next();
                    // remove from list only property with price greater than given price
                    if (property.hasPrice()) {
                        if (property.getPriceCurrent().getPrice() > price) {
                            iterator.remove();
                        }
                    }
                }
            }

            if (hasOwner) {
                // owner filter is set
                for (Iterator<Property> iterator = propertyList.iterator(); iterator.hasNext(); ) {
                    Property property = iterator.next();
                    // remove from list only property without owner
                    if (!property.hasOwner()) {
                        iterator.remove();
                    }
                }
            }

            connection.close();
            statement.close();
            return propertyList;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error searchPropertyList " + exception.getMessage());
            }
        }
    }

    /**
     * Search nearest property by given latitude and longitude
     *
     * @param lat latitude
     * @param lng longitude
     * @return found property
     */
    public Property getNearestProperty(double lat, double lng) {
        String query = "SELECT P.id_property, P.distance " +
                "FROM (SELECT PR2.id_property AS id_property, MDSYS.SDO_NN_DISTANCE(1) as distance " +
                "FROM property PR2 " +
                "WHERE MDSYS.SDO_NN(PR2.geometry,SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(?,?, NULL), " +
                " NULL, NULL ) , 'UNIT=meter', 1) = 'TRUE' " +
                "ORDER BY distance) P, " +
                "(SELECT DISTINCT PR.id_property " +
                "FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) " +
                "WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND " +
                "CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR O.id_owner IS NULL ) p_available " +
                "WHERE P.id_property=p_available.id_property AND ROWNUM = 1 ORDER BY P.distance";

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, lng);
            statement.setDouble(2, lat);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int propertyId = resultSet.getInt("id_property");

                Property nearestProperty = getPropertyById(propertyId);
                connection.close();
                statement.close();

                return nearestProperty;
            } else {
                connection.close();
                statement.close();

                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getNearestProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Search nearest property to given property
     *
     * @param property property which will be used to searching
     * @return found nearest property
     */
    public Property getPropertyNearestProperty(Property property) {
        String query = "SELECT P.id_property, ROUND(P.distance,1) as PropertyDistance " +
                "FROM (SELECT PR2.id_property AS id_property, MDSYS.SDO_NN_DISTANCE(1) as distance " +
                "FROM property PR1, property PR2 " +
                "WHERE PR1.id_property=? AND PR1.id_property <> PR2.id_property AND PR2.property_type <> 'land' AND " +
                "MDSYS.SDO_NN(PR2.geometry, PR1.geometry, 'UNIT=meter', 1) = 'TRUE' " +
                "ORDER BY distance) P, " +
                "(SELECT DISTINCT PR.id_property " +
                "FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND " +
                "CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR O.id_owner IS NULL ) p_available " +
                "WHERE P.id_property=p_available.id_property AND ROWNUM = 1 " +
                "ORDER BY P.distance";

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Property nearestProperty = getPropertyById(resultSet.getInt("id_property"));

                connection.close();
                statement.close();
                return nearestProperty;
            } else {

                statement.close();
                connection.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyNearestProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Search adjacent property of given property
     *
     * @param property property which will be used for searching
     * @return found adjacent property list
     */
    public LinkedList<Property> getPropertyAdjacentPropertyList(Property property) {
        LinkedList<Property> adjacentPropertyList = new LinkedList<>();
        String query = "SELECT PR2.id_property " +
                "FROM property PR1, property PR2 " +
                "WHERE PR1.id_property <> PR2.id_property AND PR1.id_property = ? AND sdo_relate(PR1.geometry, PR2.geometry, 'mask=anyinteract') = 'TRUE' " +
                "ORDER BY PR2.property_name";

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                adjacentPropertyList.add(getPropertyById(resultSet.getInt("id_property")));
            }

            connection.close();
            statement.close();

            return adjacentPropertyList;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyAdjacentPropertyList " + exception.getMessage());
            }
        }
    }

    /**
     * Calculates area of property shape
     *
     * @param property property
     * @return area in square metres
     */
    public double getPropertyArea(Property property) {
        String query = "SELECT SDO_GEOM.SDO_AREA(geometry, 0.005, 'unit=sq_m') AS area " +
                "FROM property " +
                "WHERE id_property = ?";

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double propertyArea = resultSet.getDouble("area");

                connection.close();
                statement.close();
                return propertyArea;
            } else {

                statement.close();
                connection.close();
                return 0;
            }

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return 0;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertyArea " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query to Oracle database, which returns properties which are available in current date.
     *
     * @return List of @see Property objects
     */
    public List<Property> getPropertyListWithoutOwnerInCurrentDate() {

        String query = "SELECT P.id_property FROM property P where p.id_property IN(\n" +
                "SELECT DISTINCT PR.id_property\n" +
                "    FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND\n" +
                "        CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR (O.id_owner IS NULL))";

        Connection connection = null;
        PreparedStatement statement;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);

            LinkedList<Integer> idProperties = new LinkedList<>();
            LinkedList<Property> propertyLinkedList = new LinkedList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                idProperties.add(resultSet.getInt("id_property"));
            }
            connection.close();
            statement.close();

            //properties
            propertyLinkedList.addAll(idProperties.stream().map(this::getPropertyById).collect(Collectors.toList()));

            return propertyLinkedList;

        } catch (SQLException exception) {
            System.err.println("Error getPropertiesWithoutOwner " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getPropertiesWithoutOwner " + exception.getMessage());
            }
        }
    }

    /**
    * Method calls query to Oracle database, which returns last inserted ID of property.
     *
     * @return int last inserted ID of property
    * */
    public int lastInsertedId() {
        int lastInsertedId;
        String query = "select id_property from property where id_property = (select max(id_property) from property)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                lastInsertedId = resultSet.getInt("id_property");

                connection.close();
                statement.close();
                return lastInsertedId;
            } else {

                connection.close();
                statement.close();
                return 0;
            }

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return 0;
        }

    }


    /**
     * Method converts Property type (enum) to lowercase.
     *
     * @param type @see Property type (enum)
     * @return lowercase string
     */
    public String toDbType(Property.Type type) {
        return type.toString().toLowerCase();
    }

    /**
     * Method converts String to Property type enum (@see Property)
     *
     * @param type String, which represents type of property
     * @return enum type of @see Property
     */
    public Property.Type toPropertyType(String type) {
        return Property.Type.valueOf(type.toUpperCase());
    }
}
