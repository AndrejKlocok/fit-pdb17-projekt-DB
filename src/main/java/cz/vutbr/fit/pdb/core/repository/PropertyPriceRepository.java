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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class PropertyPriceRepository {

    private OracleDataSource dataSource;

    public PropertyPriceRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<PropertyPrice> getPropertyPriceListOfProperty(Property property) {
        String query = "SELECT * FROM property_price WHERE id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            LinkedList<PropertyPrice> propertyPriceList = new LinkedList<>();
            while (resultSet.next()) {
                PropertyPrice propertyPrice = new PropertyPrice();
                propertyPrice.setIdPropertyPrice(resultSet.getInt("id_price"));
                propertyPrice.setIdProperty(resultSet.getInt("id_property"));
                propertyPrice.setPrice(resultSet.getDouble("price"));
                propertyPrice.setValidFrom(resultSet.getDate("valid_from"));
                propertyPrice.setValidTo(resultSet.getDate("valid_to"));
                propertyPriceList.add(propertyPrice);
            }

            connection.close();
            statement.close();
            return propertyPriceList;

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public PropertyPrice getPropertyPrice(PropertyPrice propertyPrice) {
        String query = "SELECT * FROM property_price WHERE id_price = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getIdPropertyPrice());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                PropertyPrice newPropertyPrice = new PropertyPrice();
                newPropertyPrice.setIdPropertyPrice(resultSet.getInt("id_price"));
                newPropertyPrice.setIdProperty(resultSet.getInt("id_property"));
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
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public PropertyPrice getPropertyPriceById(int idPropertyPrice) {
        String query = "SELECT * FROM property_price WHERE id_price = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idPropertyPrice);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                PropertyPrice propertyPrice = new PropertyPrice();
                propertyPrice.setIdPropertyPrice(resultSet.getInt("id_price"));
                propertyPrice.setIdProperty(resultSet.getInt("id_property"));
                propertyPrice.setPrice(resultSet.getDouble("price"));
                propertyPrice.setValidFrom(resultSet.getDate("valid_from"));
                propertyPrice.setValidTo(resultSet.getDate("valid_to"));

                connection.close();
                statement.close();
                return propertyPrice;
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

    public boolean createPropertyPrice(PropertyPrice propertyPrice) {
        String query = "INSERT INTO property_price(id_property, price, valid_from, valid_to)"
                + " values(?,?,?,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getIdProperty());
            statement.setDouble(2, propertyPrice.getPrice());
            statement.setDate(3, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(propertyPrice.getValidTo().getTime()));
            statement.executeQuery();

            connection.close();
            statement.close();
            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean savePropertyPrice(PropertyPrice propertyPrice) {
        String query = "UPDATE property_price SET id_property = ?, price = ?, valid_from = ?, valid_to = ? WHERE id_price = ? ";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getIdProperty());
            statement.setDouble(2, propertyPrice.getPrice());
            statement.setDate(3, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(propertyPrice.getValidTo().getTime()));
            statement.setInt(5, propertyPrice.getIdPropertyPrice());
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
