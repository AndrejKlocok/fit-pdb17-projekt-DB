/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
/**
 *  Property price repository creates Property price type objects (@see PropertyPrice), queries and calls to Oracle database.
 *  Repository works mainly with table Property_Price.
 *  Class extends @see Observable.
 */
public class PropertyPriceRepository extends Observable {

    private OracleDataSource dataSource;

    /**
     * Constructor for property price repository @see PropertyPriceRepository.
     * @param dataSource  @see OracleDataSource
     */
    public PropertyPriceRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @param id_property Integer value, which represents id property
     * @param from @see java.util.Date value from desired time interval
     * @param to @see java.util.Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceListOfFromToDate(Integer id_property, Date from, Date to){
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(id_property);
        propertyPrice.setValidFrom(from);
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @param from @see java.util.Date value from desired time interval
     * @param to @see java.util.Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceListOfFromToDate(Date from, Date to){
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.setValidFrom(from);
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @param id_property Integer value, which represents id property
     * @param from @see java.util.Date value from desired time interval
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceListOfFromDate(Integer id_property, Date from){
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(id_property);
        propertyPrice.setValidFrom(from);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @param from @see java.util.Date value from desired time interval
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceListOfFromDate(Date from){
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.setValidFrom(from);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @param id_property Integer value, which represents id property
     * @param to @see java.util.Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceListOfToDate(Integer id_property, Date to){
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(id_property);
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @param to @see java.util.Date value to desired time interval
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceListOToDate(Date to){
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.setValidTo(to);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method creates @see PropertyPrice type object with given parameters and calls
     * @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @param id_property Integer value, which represents id property
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceList(Integer id_property){
        PropertyPrice propertyPrice = new PropertyPrice();
        propertyPrice.getProperty().setIdProperty(id_property);
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method calls @see PropertyPriceRepository#getPropertyPriceListOfProperty(PropertyPrice propertyPrice).
     * @return List of @see PropertyPrice typed objects
     */
    public List<PropertyPrice> getPropertyPriceList(){
        PropertyPrice propertyPrice = new PropertyPrice();
        return this.getPropertyPriceListOfProperty(propertyPrice);
    }

    /**
     * Method calls query under table Property_price to Oracle database, which returns property_price records according
     * to given @see PropertyPrice object and its attributes
     * @throws  @see SQLException if occurs
     * @throws  @see NullPointerException if occurs
     * @param propertyPrice @see PropertyPrice
     * @return  List of @see PropertyPrice objects
     */
    private List<PropertyPrice> getPropertyPriceListOfProperty(PropertyPrice propertyPrice) {

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

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
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

            return propertyPriceList;

        } catch (SQLException exception) {
            System.err.println("Error getPropertyPriceListOfProperty " + exception.getMessage());

            return new LinkedList<>();
        }
        catch (NullPointerException exception){
            System.err.println("Error getPropertyPriceListOfProperty " + exception.getMessage());
            return  new LinkedList<>();
        }
        finally {
            try {
                if(connection!= null)
                    connection.close();
            }catch (SQLException exception) {
                System.err.println("Error getPropertyPriceListOfProperty " + exception.getMessage());
            }
        }

    }

    /**
     * Method calls query under table Property_price to Oracle database, which returns record, according to given parameters.
     * @throws  @see SQLException if occurs
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return @see PropertyPrice
     */
    public PropertyPrice getPropertyPrice(PropertyPrice propertyPrice) {
        String query = "SELECT * FROM property_price WHERE id_price = ?";

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
        finally {
            try {
                if(connection!= null)
                    connection.close();
            }catch (SQLException exception) {
                System.err.println("Error getPropertyPrice " + exception.getMessage());
            }
        }

    }

    /**
     * Method calls query under table Property_price to Oracle database, which inserts record, according to given parameters.
     * @throws  @see SQLException if occurs
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean createPropertyPrice(PropertyPrice propertyPrice) {
        String query = "CALL temporal_insert('property_price', ?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
        finally {
            try {
                if(connection!= null)
                    connection.close();
            }catch (SQLException exception) {
                System.err.println("Error createPropertyPrice " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property_price to Oracle database, which deletes record, according to given parameters.
     * @throws  @see SQLException if occurs
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean deletePropertyPrice(PropertyPrice propertyPrice) {
        String query = "CALL temporal_delete('property_price', ?, ?, ?)";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
        finally {
            try {
                if(connection!= null)
                    connection.close();
            }catch (SQLException exception) {
                System.err.println("Error deletePropertyPrice " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Property_price to Oracle database, which updates record, according to given parameters.
     * @throws  @see SQLException if occurs
     * @param propertyPrice @see PropertyPrice object, which stores attributes for query
     * @return boolean True if query was successful otherwise False.
     */
    public boolean savePropertyPrice(PropertyPrice propertyPrice) {
        String query = "CALL temporal_update('property_price',?,?,?,?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
        finally {
            try {
                if(connection!= null)
                    connection.close();
            }catch (SQLException exception) {
                System.err.println("Error savePropertyPrice " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query to Oracle database, which returns the average price of property in desired time interval.
     * @throws  @see SQLException if occurs
     * @param id_property Integer value, id of property
     * @param date_from @see Date, Date value from desired time interval
     * @param date_to @see Date, Date value to desired time interval
     * @return Integer value, which represents average price of property
     */
    public Double getAvgPropertyPrice(Integer id_property, Date date_from, Date date_to){
        String query ="SELECT  ROUND(AVG(PP.price),0) AS AvgPrice" +
                "                FROM property_price PP RIGHT JOIN property P ON(PP.id_property=P.id_property)  WHERE" +
                "                ( P.id_property=? AND (PP.valid_from >= ? ) AND (PP.valid_to <= ?) ) OR" +
                "                ( P.id_property=? AND ( (? BETWEEN PP.valid_from AND PP.valid_to) OR" +
                "                ( ? BETWEEN PP.valid_from AND PP.valid_to)))" +
                "                GROUP BY PP.id_property, P.property_name";

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id_property);
            statement.setDate(2, new java.sql.Date(date_from.getTime()));
            statement.setDate(3, new java.sql.Date(date_to.getTime()));
            statement.setInt(4, id_property);
            statement.setDate(5, new java.sql.Date(date_from.getTime()));
            statement.setDate(6, new java.sql.Date(date_to.getTime()));

            Double avgPrice;
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                avgPrice = resultSet.getDouble("AvgPrice");
            }
            else {
                avgPrice = 0.0;
            }
            connection.close();
            statement.close();

            return  avgPrice;

        } catch (SQLException exception) {
            System.err.println("Error getAvgPropertyPrice " + exception.getMessage());

            return 0.0;
        } finally {
            try {
                if(connection!= null)
                    connection.close();
            }catch (SQLException exception) {
                System.err.println("Error getAvgPropertyPrice " + exception.getMessage());
            }
        }
    }
}
