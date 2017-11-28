/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.GroundPlan;
import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Property;
import cz.vutbr.fit.pdb.core.model.PropertyPrice;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class PropertyRepository extends Observable {

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
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
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
                Struct st = (Struct) resultSet.getObject("geometry");
                property.setGeometry(JGeometry.loadJS(st));
                newProperty.setName(resultSet.getString("property_name"));
                newProperty.setDescription(resultSet.getString("property_description"));

                // load property ground plans
                GroundPlanRepository groundPlanRepository = new GroundPlanRepository(dataSource);
                List<GroundPlan> groundPlanList = groundPlanRepository.getGroundPlanListOfProperty(property);
                newProperty.setGroundPlans(groundPlanList);

                // load property price history
                PropertyPriceRepository propertyPriceRepository = new PropertyPriceRepository(dataSource);
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
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
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public int getNewIdForProperty() {
        int lastInsertedId;
        String query = "select id_property from property where id_property = (select max(id_property) from property)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                lastInsertedId = resultSet.getInt("id_property") +1;

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
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
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
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public boolean createProperty(Property property) {
        String query = "INSERT INTO property(id_property, property_type, geometry, property_name, property_description)"
                + " values(property_seq.nextval, ?,?,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
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
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean deleteProperty(Property property) {
        String query = "DELETE FROM property WHERE id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            System.out.println(property.getIdProperty());
            statement.setInt(1, property.getIdProperty());

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

    public List<Property> getPropertyListSimilarByGroundPlans(List<GroundPlan> groundPlans) {
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
                "   similarity ASC " +
                "FETCH FIRST 5 ROWS ONLY";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
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
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
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
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public LinkedList<Property> searchPropertyList(String name, Double price, boolean hasOwner) {
        LinkedList<Property> propertyList = new LinkedList<>();

        String query = "";

        return propertyList;
    }

    public Property getNearestProperty(double lat, double lng) {
        String query = "SELECT P.id_property, P.distance "+
                "FROM (SELECT PR2.id_property AS id_property, MDSYS.SDO_NN_DISTANCE(1) as distance "+
                "FROM property PR2 "+
                "WHERE MDSYS.SDO_NN(PR2.geometry,SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(?,?, NULL), "+
                                    " NULL, NULL ) , 'UNIT=meter', 1) = 'TRUE' "+
                "ORDER BY distance) P, "+
                "(SELECT DISTINCT PR.id_property "+
                "FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) " +
                "WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND "+
                "CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR O.id_owner IS NULL ) p_available "+
                "WHERE P.id_property=p_available.id_property AND ROWNUM = 1 ORDER BY P.distance";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, lng);
            statement.setDouble(2,lat);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
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
        }
    }

    public Property getPropertyNearestProperty(Property property) {
        String query = "SELECT P.id_property, ROUND(P.distance,1) as PropertyDistance "+
        "FROM (SELECT PR2.id_property AS id_property, MDSYS.SDO_NN_DISTANCE(1) as distance "+
                "FROM property PR1, property PR2 "+
                "WHERE PR1.id_property=? AND PR1.id_property <> PR2.id_property AND PR2.property_type <> 'land' AND "+
        "MDSYS.SDO_NN(PR2.geometry, PR1.geometry, 'UNIT=meter', 1) = 'TRUE' "+
        "ORDER BY distance) P, "+
              "(SELECT DISTINCT PR.id_property "+
        "FROM property PR LEFT OUTER JOIN owner O ON (PR.id_property=O.id_property) WHERE (CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to AND "+
                "CURRENT_DATE  NOT BETWEEN O.valid_from AND O.valid_to) OR O.id_owner IS NULL ) p_available "+
        "WHERE P.id_property=p_available.id_property AND ROWNUM = 1 "+
        "ORDER BY P.distance";

        try {
            Connection connection = dataSource.getConnection();
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
        }
    }

    public LinkedList<Property> getPropertyAdjacentPropertyList(Property property) {
        LinkedList<Property> adjacentPropertyList = new LinkedList<>();
        String query = "select PR2.id_property FROM property PR1, property PR2 WHERE PR1.id_property <> PR2.id_property " +
                "AND PR1.id_property = ? AND sdo_relate(PR1.geometry, PR2.geometry, 'mask=anyinteract') = 'TRUE'";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int propertyId = resultSet.getInt("id_property");
                System.out.println("PROPERTY ID " + propertyId);
                adjacentPropertyList.add(getPropertyById(propertyId));
            }

            connection.close();
            statement.close();

        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        }
        return adjacentPropertyList;
    }

    public double getPropertyArea(Property property) {
        String query = "SELECT SDO_GEOM.SDO_AREA(geometry, 0.005, 'unit=sq_m') AS area FROM property where id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
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
        }
    }

    public String toDbType(Property.Type type) {
        return type.toString().toLowerCase();
    }

    public Property.Type toPropertyType(String type) {
        return Property.Type.valueOf(type.toUpperCase());
    }
}
