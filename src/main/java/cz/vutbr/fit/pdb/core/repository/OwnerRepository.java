/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.Owner;
import cz.vutbr.fit.pdb.core.model.Person;
import cz.vutbr.fit.pdb.core.model.Property;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;


public class OwnerRepository extends Observable {

    private OracleDataSource dataSource;

    public OwnerRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<Owner> getOwnerHistory(Person person){
        String query = "SELECT person.*,  owner.VALID_FROM, owner.VALID_TO, property.* FROM owner JOIN person ON(person.ID_PERSON=owner.id_owner)JOIN property ON(property.id_property=owner.id_property)  WHERE id_owner = ?";
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, person.getIdPerson());
            LinkedList<Owner> ownerLinkedList = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery();

            PropertyRepository propertyRepository = new PropertyRepository(dataSource);

            while (resultSet.next()) {
                Owner owner = new Owner();
                owner.setPerson(new Person(resultSet.getInt("id_person"), resultSet.getString("firstname"),
                        resultSet.getString("lastname"), resultSet.getString("street"),
                        resultSet.getString("city"),resultSet.getString("psc"),
                        resultSet.getString("email"), new LinkedList<>()));

                owner.setProperty(new Property(resultSet.getInt("id_property"),
                        propertyRepository.toPropertyType(resultSet.getString("property_type")),
                        resultSet.getString("property_name"), resultSet.getString("property_description")
                ));
                owner.setValidFrom(resultSet.getDate("valid_from"));
                owner.setValidTo(resultSet.getDate("valid_to"));
                ownerLinkedList.add(owner);
            }
            connection.close();
            statement.close();
            return ownerLinkedList;

        } catch (SQLException exception) {
            System.err.println("Error getOwnerHistoryById " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public List<Owner> getOwnersListOfProperty(Property property) {
        String query = "SELECT owner.id_owner, owner.VALID_FROM, owner.VALID_TO, property.*  FROM owner JOIN property ON(property.id_property=owner.id_property) WHERE owner.id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            ResultSet resultSet = statement.executeQuery();
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);
            PersonRepository personRepository = new PersonRepository(dataSource);

            LinkedList<Owner> ownersList = new LinkedList<>();
            while (resultSet.next()) {
                Owner owner = new Owner();
                owner.getPerson().setIdPerson(resultSet.getInt("id_owner"));
                owner.setProperty(new Property(resultSet.getInt("id_property"),
                        propertyRepository.toPropertyType(resultSet.getString("property_type")),
                        resultSet.getString("property_name"), resultSet.getString("property_description")
                        ));
                owner.setValidFrom(resultSet.getDate("valid_from"));
                owner.setValidTo(resultSet.getDate("valid_to"));

                ownersList.add(owner);
            }

            connection.close();
            statement.close();

            //TODO vymysliet propertu lepsie -> cyklicke volanie

            //create persons
            for (Owner o:ownersList) {
                Person p = personRepository.getPerson(o.getPerson());
                o.setPerson(p);
            }
            return ownersList;

        } catch (SQLException exception) {
            System.err.println("Error getOwnersListOfProperty" + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public Owner getOwner(Owner oldOwner){
        String query="Select owner.* from owner where owner.id_owner=? and owner.id_property=? and owner.valid_from=? and owner.valid_to=?";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, oldOwner.getPerson().getIdPerson());
            statement.setInt(2, oldOwner.getProperty().getIdProperty());
            statement.setDate(3, new java.sql.Date(oldOwner.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(oldOwner.getValidTo().getTime()));

            ResultSet resultSet = statement.executeQuery();
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);
            PersonRepository personRepository = new PersonRepository(dataSource);


            if (resultSet.next()) {
                Owner owner = new Owner();
                owner.getPerson().setIdPerson(resultSet.getInt("id_owner"));
                owner.getProperty().setIdProperty(resultSet.getInt("id_property"));
                owner.setValidFrom(resultSet.getDate("valid_from"));
                owner.setValidTo(resultSet.getDate("valid_to"));

                connection.close();
                statement.close();


                //create person and property
                owner.setPerson(personRepository.getPerson(owner.getPerson()));
                owner.setProperty(propertyRepository.getProperty(owner.getProperty()));
                return owner;

            }else {
                    connection.close();
                    statement.close();
                    return null;
            }

        }catch (SQLException exception) {
            System.err.println("Error getOwner " + exception.getMessage());

            return null;
        }

    }
    public boolean createOwner(Owner owner) {
        String query = "CALL temporal_insert('owner', ?, ?, ?, ?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, owner.getProperty().getIdProperty());
            statement.setInt(2, owner.getPerson().getIdPerson());
            statement.setDate(3, new java.sql.Date(owner.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(owner.getValidTo().getTime()));

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error createOwner " + exception.getMessage());

            return false;
        }
    }

    public boolean updateOwner(Owner owner) {
        String query = "CALL temporal_update('owner',1,2,?,?)";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, owner.getProperty().getIdProperty());
            statement.setInt(2, owner.getPerson().getIdPerson());
            statement.setDate(3, new java.sql.Date(owner.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(owner.getValidTo().getTime()));

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error updateOwner " + exception.getMessage());

            return false;
        }
    }

    public boolean deleteOwner(Owner owner) {
        String query = "CALL temporal_delete('owner',?,?,?) ";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, owner.getProperty().getIdProperty());
            statement.setInt(2, owner.getPerson().getIdPerson());
            statement.setDate(3, new java.sql.Date(owner.getValidFrom().getTime()));
            statement.setDate(4, new java.sql.Date(owner.getValidTo().getTime()));

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error deleteOwner " + exception.getMessage());

            return false;
        }
    }

    /**
     * Method getOwnersListOfDate returns List of Owner objects according to given Owner object and its attributes
     * @param owner
     * @return List<Owner>
     */
    public List<Owner> getOwnersListOfDate(Owner owner) {

        String queryProperty = "SELECT * FROM owner WHERE id_property = ?";

        String queryPropertyTime = "SELECT owner.* FROM owner WHERE \n" +
                "( owner.id_property=? AND (owner.valid_from >= ?) AND (owner.valid_to <= ?) ) OR\n" +
                "( owner.id_property=? AND (? BETWEEN owner.valid_from AND owner.valid_to) OR \n" +
                "(? BETWEEN owner.valid_from AND owner.valid_to))";

        String queryPropertyTimeFrom = "SELECT owner.* FROM owner WHERE \n" +
                "( owner.id_property=? AND (owner.valid_from >= ?)) OR\n" +
                "( owner.id_property=? AND (? BETWEEN owner.valid_from AND owner.valid_to) )";

        String queryPropertyTimeTo = "SELECT owner.* FROM owner WHERE \n" +
                "( owner.id_property=? AND (owner.valid_to <= ?) ) OR\n" +
                "( owner.id_property=? AND (? BETWEEN owner.valid_from AND owner.valid_to));";


        String queryTime = "SELECT owner.* FROM owner WHERE \n" +
                "( (owner.valid_from >= ?) AND (owner.valid_to <= ?) ) OR\n" +
                "( ( ? BETWEEN owner.valid_from AND owner.valid_to) OR \n" +
                "( ? BETWEEN owner.valid_from AND owner.valid_to))";

        String queryTimeFrom = "SELECT owner.* FROM owner WHERE \n" +
                "( (owner.valid_from >= ?)) OR\n" +
                "( (? BETWEEN owner.valid_from AND owner.valid_to) );";

        String queryTimeTo = "SELECT owner.* FROM owner WHERE \n" +
                "( (owner.valid_to <= ?)) OR\n" +
                "( (? BETWEEN owner.valid_from AND owner.valid_to) )";

        String query = "SELECT owner.* FROM owner ";
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement statement;
            if (owner.getProperty().getIdProperty() != 0){
                if(owner.getValidFrom() != null && owner.getValidTo() != null) {
                    statement = connection.prepareStatement(queryPropertyTime);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    statement.setDate(2, new java.sql.Date(owner.getValidFrom().getTime()));
                    statement.setDate(3, new java.sql.Date(owner.getValidTo().getTime()));
                    statement.setInt(4, owner.getProperty().getIdProperty());
                    statement.setDate(5, new java.sql.Date(owner.getValidFrom().getTime()));
                    statement.setDate(6, new java.sql.Date(owner.getValidTo().getTime()));
                    System.out.println("queryPropertyTime");
                }
                else if (owner.getProperty().getIdProperty() != 0 && owner.getValidFrom() != null ){
                    statement = connection.prepareStatement(queryPropertyTimeFrom);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    statement.setDate(2, new java.sql.Date(owner.getValidFrom().getTime()));
                    statement.setInt(3, owner.getProperty().getIdProperty());
                    statement.setDate(4, new java.sql.Date(owner.getValidFrom().getTime()));
                    System.out.println("queryPropertyTimeFrom");
                }
                else if (owner.getProperty().getIdProperty() != 0 && owner.getValidTo() != null){
                    statement = connection.prepareStatement(queryPropertyTimeTo);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    statement.setDate(2, new java.sql.Date(owner.getValidTo().getTime()));
                    statement.setInt(3, owner.getProperty().getIdProperty());
                    statement.setDate(4, new java.sql.Date(owner.getValidTo().getTime()));
                    System.out.println("queryPropertyTimeTo");
                }
                else {
                    statement = connection.prepareStatement(queryProperty);
                    statement.setInt(1, owner.getProperty().getIdProperty());
                    System.out.println("queryProperty");
                }
            }
            else{
                if(owner.getValidFrom() != null && owner.getValidTo() != null) {
                    statement = connection.prepareStatement(queryTime);
                    statement.setDate(1, new java.sql.Date(owner.getValidFrom().getTime()));
                    statement.setDate(2, new java.sql.Date(owner.getValidTo().getTime()));
                    statement.setDate(3, new java.sql.Date(owner.getValidFrom().getTime()));
                    statement.setDate(4, new java.sql.Date(owner.getValidTo().getTime()));
                    System.out.println("queryTime");
                }
                else if (owner.getProperty().getIdProperty() != 0 && owner.getValidFrom() != null ){
                    statement = connection.prepareStatement(queryTimeFrom);
                    statement.setDate(1, new java.sql.Date(owner.getValidFrom().getTime()));
                    statement.setDate(2, new java.sql.Date(owner.getValidFrom().getTime()));
                    System.out.println("queryTimeFrom");
                }
                else if (owner.getProperty().getIdProperty() != 0 && owner.getValidTo() != null){
                    statement = connection.prepareStatement(queryTimeTo);
                    statement.setDate(1, new java.sql.Date(owner.getValidTo().getTime()));
                    statement.setDate(2, new java.sql.Date(owner.getValidTo().getTime()));
                    System.out.println("queryTimeTo");
                }
                else {
                    statement = connection.prepareStatement(query);
                    System.out.println("query");
                }
            }

            LinkedList<Owner> ownerLinkedList = new LinkedList<>();
            ResultSet resultSet = statement.executeQuery();

            PersonRepository personRepository = new PersonRepository(dataSource);
            PropertyRepository propertyRepository = new PropertyRepository(dataSource);

            while (resultSet.next()) {
                Owner o = new Owner();
                o.getPerson().setIdPerson(resultSet.getInt("id_owner"));
                o.getProperty().setIdProperty(resultSet.getInt("id_property"));
                o.setValidFrom(resultSet.getDate("valid_from"));
                o.setValidTo(resultSet.getDate("valid_to"));
                ownerLinkedList.add(o);
            }
            connection.close();
            statement.close();

            //get persons and properties
            for (Owner o:ownerLinkedList) {
                o.setPerson(personRepository.getPerson(o.getPerson()));
                o.setProperty(propertyRepository.getProperty(o.getProperty()));
            }

            return ownerLinkedList;

        } catch (SQLException exception) {
            System.err.println("Error getOwnersListOfDate " + exception.getMessage());

            return new LinkedList<>();
        }
    }
}
