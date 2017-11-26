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

import java.sql.*;
import java.util.Iterator;
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
                newProperty.setGeometry(JGeometry.loadJS(st));
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

    public List<Property> getPropertyListSimilarByGroundPlans(List<GroundPlan> groundPlans, boolean filterHasOwner) {
        // TODO option to get similar by more ground plans
        // TODO filterHasOwner

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

            if (filterHasOwner) {
                // owner filter is set
                for (Iterator<Property> iterator = propertyList.iterator(); iterator.hasNext(); ) {
                    Property property = iterator.next();
                    // remove from list only property without owner
                    if (property.getOwnerCurrent() == null) {
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
        }
    }

    public LinkedList<Property> searchPropertyList(String name, Double price, boolean hasOwner) {

        String query = "SELECT * FROM property"
                + (!name.isEmpty() ? " WHERE property_name LIKE ? " : "");

        try {
            Connection connection = dataSource.getConnection();
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
                List<PropertyPrice> propertyPriceList = propertyPriceRepository.getPropertyPriceListOfProperty(property);
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
                    if (property.getPriceCurrent() != null) {
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
                    if (property.getOwnerCurrent() == null) {
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
        }
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
