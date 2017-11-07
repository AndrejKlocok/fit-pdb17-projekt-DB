package cz.vutbr.fit.pdb.core.model;


import java.sql.*;
import java.util.Calendar;

public class PropertyPrice {

    private int idPrice;

    private int idProperty;

    private double price;

    private Date validFrom;

    private Date validTo;

    public PropertyPrice() {
        idPrice = 0;
        idProperty = 0;
        price = 0;
    }

     public PropertyPrice(int idPrice, int idProperty, double price, Date validFrom, Date validTo) {
        this.idPrice = idPrice;
        this.idProperty = idProperty;
        this.price = price;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public int getIdPrice(){return idPrice;}
    public void setIdPrice(int id){this.idPrice = id;}
    public int getIdProperty(){return idProperty;}
    public void setIdProperty(int idProperty){this.idProperty = idProperty;}
    public double getPrice(){return price;}
    public void setPrice(double price){this.price = price;}
    public Date getValidFrom(){return validFrom;}
    public void setValidFrom(Date validFrom){this.validFrom = validFrom;}
    public Date getValidTo(){return validTo;}
    public void setValidTo(Date validTo){this.validTo = validTo;}

    public boolean isValid() {
        Date today = new Date(Calendar.getInstance().getTime().getTime());
        if(this.validTo.after(today)) {
            return true;
        } else return false;
    }

    public void save(Connection connection) throws SQLException {
        String query = "insert into property_price(id_price, id_property, price, valid_from, valid_to) values(?,?,?,?,?)";

        try (PreparedStatement insert = connection.prepareStatement(query)) {
            insert.setInt(1, this.getIdPrice());
            insert.setInt(2, this.getIdProperty());
            insert.setDouble(3, this.getPrice());
            insert.setDate(4, this.getValidFrom());
            insert.setDate(5, this.getValidTo());
            try {
                insert.executeUpdate();
            } catch (SQLException sqlEx) {
                System.err.println("Error while inserting " + sqlEx.getMessage());
            }
        }
    }

    public void loadByIdPrice(Connection connection, int idPrice) throws SQLException {
        String query = "select * from owner where id_owner =" + idPrice;

        try (Statement select = connection.createStatement()) {
            try (ResultSet rset = select.executeQuery(query)) {
                while (rset.next()) {
                    this.idPrice = rset.getInt("id_price");
                    this.idProperty = rset.getInt("id_property");
                    this.price = rset.getDouble("price");
                    this.validFrom = rset.getDate("valid_from");
                    this.validTo = rset.getDate("valid_to");
                }
            }
        }
    }

    public void loadByIdProperty(Connection connection, int idProperty) throws SQLException {
        String query = "select * from owner where id_owner =" + idProperty;

        try (Statement select = connection.createStatement()) {
            try (ResultSet rset = select.executeQuery(query)) {
                while (rset.next()) {
                    this.idPrice = rset.getInt("id_price");
                    this.idProperty = rset.getInt("id_property");
                    this.price = rset.getDouble("price");
                    this.validFrom = rset.getDate("valid_from");
                    this.validTo = rset.getDate("valid_to");
                }
            }
        }
    }
}
