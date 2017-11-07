/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import oracle.jdbc.OracleStruct;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static cz.vutbr.fit.pdb.core.App.connection;
import static cz.vutbr.fit.pdb.core.model.Property.Type.HOUSE;

public class Property {

    public enum Type {
        PREFAB,
        APARTMENT,
        HOUSE,
        TERRACE_HOUSE,
        LAND
    }

    private int id;

    private Type type;

    private String name;

    private String description;

    private HashMap<String, Double> priceHistory;

    private JGeometry geometry;

    // TODO

    public Property() {
        id = 0;
        type = null;
        name = "";
        description = "";
        priceHistory = new HashMap<>();
        geometry = null;
    }

    public Property(int id, Type type, String name, String description, JGeometry geometry) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.geometry = geometry;
    }

    public Property(int id, Type type ,String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Double> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(HashMap<String, Double> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public JGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(JGeometry geometry) {
        this.geometry = geometry;
    }

    public String toDbType(Type type) { return type.toString().toLowerCase();}

    public Type toPropertyType(String type) { return Type.valueOf(type.toUpperCase()) ; }

    public void save(Connection connection) throws SQLException {
        String query = "insert into property(id_property, property_type, geometry, property_name, property_description)"
                + " values(?,?,?,?,?)";

        try (PreparedStatement insert = connection.prepareStatement(query)) {
            insert.setInt(1, this.getId());
            insert.setString(2, toDbType(this.getType()));
            STRUCT obj = JGeometry.store(this.geometry, connection);
            insert.setObject(3, obj);
            insert.setString(4, this.getName());
            insert.setString(5, this.getDescription());

            try {
                insert.executeUpdate();
            } catch (SQLException sqlEx) {
                System.err.println("Error while inserting " + sqlEx.getMessage());
            }
        }
    }

    public void loadById(Connection connection,int id) throws SQLException {
        String query = "select * from property where id_property =" + id;

        try (Statement select = connection.createStatement()) {
            try (ResultSet rset = select.executeQuery(query)) {
                while (rset.next()) {
                    this.id = rset.getInt("id_property");
                    this.type = toPropertyType(rset.getString("property_type"));
                    STRUCT st = (oracle.sql.STRUCT) rset.getObject("geometry");
                    this.geometry = JGeometry.load(st);
                    this.name = rset.getString("property_name");
                    this.description = rset.getString("property_description");
                }
            }
        }
    }

    public List<Property> getSimilar() {
        // sample data

        // Polygon
        Property property1 = new Property();
        property1.setId(1);
        property1.setName("Polygon");
        property1.setType(Property.Type.APARTMENT);
        property1.setGeometry(JGeometry.createLinearPolygon(new double[2], 2, 2));

        // Rectangle
        Property property2 = new Property();
        property2.setId(2);
        property2.setName("Rectangle");
        property2.setType(HOUSE);
        property2.setGeometry(new JGeometry(2, 2, 2, 2, 2));

        // Circle
        Property property3 = new Property();
        property3.setId(3);
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
}
