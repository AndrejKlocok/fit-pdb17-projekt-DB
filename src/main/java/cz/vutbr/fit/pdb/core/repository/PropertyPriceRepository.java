/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.PropertyAvgPrice;
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class PropertyPriceRepository extends Observable {

    private OracleDataSource dataSource;

    public PropertyPriceRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*
    public List<PropertyPrice> getPropertyPriceListOfProperty(Property property) {
        String query = "SELECT * FROM property_price WHERE id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);
            LinkedList<PropertyPrice> propertyPriceList = new LinkedList<>();

            while (resultSet.next()) {
                PropertyPrice propertyPrice = new PropertyPrice();
                propertyPrice.setIdPropertyPrice(resultSet.getInt("id_price"));
                propertyPrice.getProperty().setIdProperty(resultSet.getInt("id_property"));
                propertyPrice.setPrice(resultSet.getDouble("price"));
                propertyPrice.setValidFrom(resultSet.getDate("valid_from"));
                propertyPrice.setValidTo(resultSet.getDate("valid_to"));
                propertyPriceList.add(propertyPrice);
            }

            connection.close();
            statement.close();

            //TODO vyplnit aj propertu?
            //load properties to list
            //for (PropertyPrice pp:propertyPriceList) {
            //    pp.setProperty(propertyRepository.getProperty(pp.getProperty()));
            //}

            return propertyPriceList;

        } catch (SQLException exception) {
            System.err.println("Error getPropertyPriceListOfProperty " + exception.getMessage());

            return new LinkedList<>();
        }
    }
    */

    /**
     * Method getPropertyPriceListOfProperty returns List of PropertyPrice objects according to given PropertyPrice object and its attributes.
     * @param propertyPrice
     * @return  List<PropertyPrice>
     */
    public List<PropertyPrice> getPropertyPriceListOfProperty(PropertyPrice propertyPrice) {

        String queryProperty = "SELECT * FROM property_price WHERE id_property = ?";

        String queryPropertyTime = "SELECT property_price.* FROM property_price WHERE \n" +
                "( property_price.id_property=? AND ( property_price.valid_from >= ? ) AND (property_price.valid_to <= ?) ) OR\n" +
                "( property_price.id_property=? AND ( ? BETWEEN property_price.valid_from AND property_price.valid_to) OR \n" +
                "( ? BETWEEN property_price.valid_from AND property_price.valid_to))";

        String queryPropertyTimeFrom = "SELECT property_price.* FROM property_price WHERE \n" +
                "( property_price.id_property=? AND ( property_price.valid_from >= ? )) OR\n" +
                "( property_price.id_property=? AND ( ? BETWEEN property_price.valid_from AND property_price.valid_to) )";

        String queryPropertyTimeTo = "SELECT property_price.* FROM property_price WHERE \n" +
                "( property_price.id_property=? AND ( property_price.valid_to <= ? ) ) OR\n" +
                "( property_price.id_property=? AND ( ? BETWEEN property_price.valid_from AND property_price.valid_to))";


        String queryTime = "SELECT property_price.* FROM property_price WHERE \n" +
                "( ( property_price.valid_from >= ? ) AND (property_price.valid_to <= ? ) ) OR\n" +
                "( ( ? BETWEEN property_price.valid_from AND property_price.valid_to) OR \n" +
                "( ? BETWEEN property_price.valid_from AND property_price.valid_to))";

        String queryTimeFrom = "SELECT property_price.* FROM property_price WHERE \n" +
                "( ( property_price.valid_from >= ? )) OR\n" +
                "( ( ? BETWEEN property_price.valid_from AND property_price.valid_to) )";

        String queryTimeTo = "SELECT property_price.* FROM property_price WHERE \n" +
                "( ( property_price.valid_to <= ?) ) OR\n" +
                "( ( ? BETWEEN property_price.valid_from AND property_price.valid_to))";

        String query = "SELECT property_price.* FROM property_price ";

        try {
            Connection connection = dataSource.getConnection();
            //TODO lepsi navrh
            PreparedStatement statement;
            if (propertyPrice.getProperty().getIdProperty() != 0){
                if(propertyPrice.getValidFrom() != null && propertyPrice.getValidTo() != null) {
                    statement = connection.prepareStatement(queryPropertyTime);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(2, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    statement.setDate(3, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    statement.setInt(4, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(5, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    statement.setDate(6, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    System.out.println("queryPropertyTime");
                }
                else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidFrom() != null ){
                    statement = connection.prepareStatement(queryPropertyTimeFrom);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(2, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    statement.setInt(3, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(4, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    System.out.println("queryPropertyTimeFrom");
                }
                else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidTo() != null){
                    statement = connection.prepareStatement(queryPropertyTimeTo);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(2, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    statement.setInt(3, propertyPrice.getProperty().getIdProperty());
                    statement.setDate(4, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    System.out.println("queryPropertyTimeTo");
                }
                else {
                    statement = connection.prepareStatement(queryProperty);
                    statement.setInt(1, propertyPrice.getProperty().getIdProperty());
                    System.out.println("queryProperty");
                }
            }
            else{
                if(propertyPrice.getValidFrom() != null && propertyPrice.getValidTo() != null) {
                    statement = connection.prepareStatement(queryTime);
                    statement.setDate(1, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    statement.setDate(2, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    statement.setDate(3, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    statement.setDate(4, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    System.out.println("queryTime");
                }
                else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidFrom() != null ){
                    statement = connection.prepareStatement(queryTimeFrom);
                    statement.setDate(1, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    statement.setDate(2, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
                    System.out.println("queryTimeFrom");
                }
                else if (propertyPrice.getProperty().getIdProperty() != 0 && propertyPrice.getValidTo() != null){
                    statement = connection.prepareStatement(queryTimeTo);
                    statement.setDate(1, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    statement.setDate(2, new java.sql.Date(propertyPrice.getValidTo().getTime()));
                    System.out.println("queryTimeTo");
                }
                else {
                    statement = connection.prepareStatement(query);
                    System.out.println("query");
                }
            }


            ResultSet resultSet = statement.executeQuery();
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);
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

            //TODO vyplnit aj propertu? -> cyklicke volanie
            //load properties to list
            /*for (PropertyPrice pp:propertyPriceList) {
                pp.setProperty(propertyRepository.getProperty(pp.getProperty()));
            }*/

            return propertyPriceList;

        } catch (SQLException exception) {
            System.err.println("Error getPropertyPriceListOfPropertyByIdOrTime " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public PropertyPrice getPropertyPrice(PropertyPrice propertyPrice) {
        String query = "SELECT * FROM property_price WHERE id_price = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getIdPropertyPrice());
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);

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

                //TODO vyplnit aj propertu?
                //newPropertyPrice.setProperty(propertyRepository.getProperty(newPropertyPrice.getProperty()));

                return newPropertyPrice;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (SQLException exception) {
            System.err.println("Error getPropertyPrice " + exception.getMessage());

            return null;
        }
    }

    public boolean createPropertyPrice(PropertyPrice propertyPrice) {
        String query = "CALL temporal_insert('property_price', ?, ?, ?, ?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getProperty().getIdProperty());
            statement.setDouble(2, propertyPrice.getPrice());
            statement.setDate(3, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(propertyPrice.getValidTo().getTime()));

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
        }
    }

    public boolean deletePropertyPrice(PropertyPrice propertyPrice) {
        String query = "CALL temporal_delete('property_price', ?, ?, ?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getProperty().getIdProperty());
            statement.setDate(2, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
            statement.setDate(3, new java.sql.Date(propertyPrice.getValidTo().getTime()));

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
        }
    }

    public boolean savePropertyPrice(PropertyPrice propertyPrice) {
        String query = "CALL temporal_update('property_price',?,?,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, propertyPrice.getProperty().getIdProperty());
            statement.setDouble(2, propertyPrice.getPrice());
            statement.setDate(3, new java.sql.Date(propertyPrice.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(propertyPrice.getValidTo().getTime()));

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
        }
    }

    public List<PropertyAvgPrice> getAvgPropertyPrice(java.sql.Date date_from, java.sql.Date date_to){
        String query ="SELECT  PP.id_property, ROUND(AVG(PP.price),0) AS AvgPrice \n" +
                "FROM property_price PP \n" +
                "WHERE (? < PP.valid_from) AND (? > PP.valid_to)\n" +
                "GROUP BY PP.id_property\n" +
                "ORDER BY AvgPrice;";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1, date_from);
            statement.setDate(2, date_to);
            PropertyRepository propertyRepository=new PropertyRepository(dataSource);
            LinkedList<PropertyAvgPrice> propertyAvgPriceLinkedList = new LinkedList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PropertyAvgPrice propertyAvgPrice = new PropertyAvgPrice();
                propertyAvgPrice.getProperty().setIdProperty(resultSet.getInt("id_property"));
                propertyAvgPrice.setAvgPrice(resultSet.getInt("AvgPrice"));
                propertyAvgPriceLinkedList.add( propertyAvgPrice);
            }
            connection.close();
            statement.close();

            //properties
            for (PropertyAvgPrice p :propertyAvgPriceLinkedList) {
                p.setProperty(propertyRepository.getProperty(p.getProperty()));
            }

            return  propertyAvgPriceLinkedList;

        } catch (SQLException exception) {
            System.err.println("Error getAvgPropertyPrice " + exception.getMessage());

            return null;
        }
    }
}
