/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static cz.vutbr.fit.pdb.core.model.Property.Type.HOUSE;

public class PropertyRepository {

    private OracleDataSource dataSource;

    public PropertyRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Property> getPropertyList() {
        String query = "SELECT * FROM property";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            LinkedList<Property> propertyList = new LinkedList<>();
            while (resultSet.next()) {
                Property property = new Property();
                property.setIdProperty(resultSet.getInt("id_property"));
                property.setType(toPropertyType(resultSet.getString("property_type")));
                STRUCT st = (STRUCT) resultSet.getObject("geometry");
                property.setGeometry(JGeometry.load(st));
                property.setName(resultSet.getString("property_name"));
                property.setDescription(resultSet.getString("property_description"));
                propertyList.add(property);

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
                property.setPriceHistory(propertyPriceList);
            }

            connection.close();
            statement.close();
            return propertyList;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public Property getProperty(Property property) {
        String query = "SELECT * FROM property WHERE id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Property newProperty = new Property();
                newProperty.setIdProperty(resultSet.getInt("id_property"));
                newProperty.setType(toPropertyType(resultSet.getString("property_type")));
                STRUCT st = (STRUCT) resultSet.getObject("geometry");
                newProperty.setGeometry(JGeometry.load(st));
                newProperty.setName(resultSet.getString("property_name"));
                newProperty.setDescription(resultSet.getString("property_description"));

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
                newProperty.setPriceHistory(propertyPriceList);

                connection.close();
                statement.close();
                return newProperty;
            } else {

                statement.close();
                connection.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public Property getPropertyById(int idProperty) {
        String query = "SELECT * FROM property WHERE id_property = ?";

        Property property;

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idProperty);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                property = new Property();
                property.setIdProperty(resultSet.getInt("id_property"));
                property.setType(toPropertyType(resultSet.getString("property_type")));
                STRUCT st = (STRUCT) resultSet.getObject("geometry");
                property.setGeometry(JGeometry.load(st));
                property.setName(resultSet.getString("property_name"));
                property.setDescription(resultSet.getString("property_description"));

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
                property.setPriceHistory(propertyPriceList);

                connection.close();
                statement.close();
                return property;
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

    public boolean createProperty(Property property) {
        String query = "INSERT INTO property(id_property, property_type, geometry, property_name, property_description)"
                + " values(?,?,?,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());
            statement.setString(2, toDbType(property.getType()));
            STRUCT obj = JGeometry.store(property.getGeometry(), connection);
            statement.setObject(3, obj);
            statement.setString(4, property.getName());
            statement.setString(5, property.getDescription());
            statement.executeQuery();

            connection.close();
            statement.close();
            return true;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean saveProperty(Property property) {
        String query = "UPDATE property SET property_type = ?, geometry = ?, property_name = ?, property_description = ? WHERE id_property = ? ";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, toDbType(property.getType()));
            STRUCT obj = JGeometry.store(property.getGeometry(), connection);
            statement.setObject(2, obj);
            statement.setString(3, property.getName());
            statement.setString(4, property.getDescription());
            statement.setInt(5, property.getIdProperty());
            statement.executeQuery();

            connection.close();
            statement.close();
            return true;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean deleteProperty(Property property) {
        // TODO
        return true;
    }

    public boolean rotatePropertyImageLeft(Property property) {
        // TODO
        return true;
    }

    public boolean rotatePropertyImageRight(Property property) {
        // TODO
        return true;
    }

    public List<Property> getPropertyListSimilar(Property property) {
        // sample data

        // Polygon
        Property property1 = new Property();
        property1.setIdProperty(1);
        property1.setName("Polygon");
        property1.setType(Property.Type.APARTMENT);
        property1.setGeometry(JGeometry.createLinearPolygon(new double[2], 2, 2));

        // Rectangle
        Property property2 = new Property();
        property2.setIdProperty(2);
        property2.setName("Rectangle");
        property2.setType(HOUSE);
        property2.setGeometry(new JGeometry(2, 2, 2, 2, 2));

        // Circle
        Property property3 = new Property();
        property3.setIdProperty(3);
        property3.setName("Circle");
        property3.setType(Property.Type.APARTMENT);
        property3.setGeometry(JGeometry.createCircle(2, 2, 2, 2));

        // Property list
        List<Property> propertyList = new LinkedList<>();
        propertyList.add(property1);
        propertyList.add(property2);
        propertyList.add(property3);

        return propertyList;
    }

    public LinkedList<Property> searchPropertyList(String name, Double price, boolean hasOwner) {
        // TODO
        return new LinkedList<>();
    }

    public Property getNearestProperty(double lat, double lng) {
        // TODO
        return null;
    }

    public Property getPropertyNearestProperty(Property property) {
        // TODO
        return null;
    }

    public LinkedList<Property> getPropertyAdjacentPropertyList(Property property) {
        // TODO
        return new LinkedList<>();
    }

    public double getPropertyArea(Property property) {
        // TODO
        return 42;
    }

    public String toDbType(Property.Type type) {
        return type.toString().toLowerCase();
    }

    public Property.Type toPropertyType(String type) {
        return Property.Type.valueOf(type.toUpperCase());
    }
}
