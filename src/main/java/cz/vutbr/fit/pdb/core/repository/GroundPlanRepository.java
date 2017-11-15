/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.repository;

import cz.vutbr.fit.pdb.core.model.GroundPlan;
import cz.vutbr.fit.pdb.core.model.Property;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ord.im.OrdImage;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class GroundPlanRepository extends Observable {

    private OracleDataSource dataSource;

    public GroundPlanRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<GroundPlan> getGroundPlanListOfProperty(Property property) {
        String query = "SELECT * FROM ground_plan WHERE id_property = ?";

        try {
            Connection connection = dataSource.getConnection();
            OraclePreparedStatement statement = (OraclePreparedStatement) connection.prepareStatement(query);
            statement.setInt(1, property.getIdProperty());

            OracleResultSet resultSet = (OracleResultSet) statement.executeQuery();
            LinkedList<GroundPlan> groundPlanList = new LinkedList<>();
            while (resultSet.next()) {
                GroundPlan groundPlan = new GroundPlan();
                groundPlan.setIdGroundPlan(resultSet.getInt("id_ground_plan"));
                groundPlan.setIdProperty(resultSet.getInt("id_property"));
                OrdImage imgProxy = (OrdImage) resultSet.getORAData("img", OrdImage.getORADataFactory());
                groundPlan.setImage(imgProxy.getDataInByteArray());

                groundPlanList.add(groundPlan);
            }

            connection.close();
            statement.close();
            return groundPlanList;

        } catch (IOException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return new LinkedList<>();
        }
    }

    public GroundPlan getGroundPlan(GroundPlan groundPlan) {
        String query = "SELECT * FROM ground_plan WHERE id_ground_plan = ?";

        try {
            Connection connection = dataSource.getConnection();
            OraclePreparedStatement statement = (OraclePreparedStatement) connection.prepareStatement(query);
            statement.setInt(1, groundPlan.getIdGroundPlan());

            OracleResultSet resultSet = (OracleResultSet) statement.executeQuery();
            if (resultSet.next()) {
                GroundPlan newGroundPlan = new GroundPlan();
                newGroundPlan.setIdGroundPlan(resultSet.getInt("id_ground_plan"));
                newGroundPlan.setIdProperty(resultSet.getInt("id_property"));
                OrdImage imgProxy = (OrdImage) resultSet.getORAData("img", OrdImage.getORADataFactory());
                newGroundPlan.setImage(imgProxy.getDataInByteArray());

                connection.close();
                statement.close();
                return newGroundPlan;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (IOException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public GroundPlan getGroundPlanById(int idGroundPlan) {
        String query = "SELECT * FROM ground_plan WHERE id_ground_plan = ?";

        try {
            Connection connection = dataSource.getConnection();
            OraclePreparedStatement statement = (OraclePreparedStatement) connection.prepareStatement(query);
            statement.setInt(1, idGroundPlan);

            OracleResultSet resultSet = (OracleResultSet) statement.executeQuery();
            if (resultSet.next()) {
                GroundPlan groundPlan = new GroundPlan();
                groundPlan.setIdGroundPlan(resultSet.getInt("id_ground_plan"));
                groundPlan.setIdProperty(resultSet.getInt("id_property"));
                OrdImage imgProxy = (OrdImage) resultSet.getORAData("img", OrdImage.getORADataFactory());
                groundPlan.setImage(imgProxy.getDataInByteArray());

                connection.close();
                statement.close();
                return groundPlan;
            } else {

                connection.close();
                statement.close();
                return null;
            }

        } catch (IOException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return null;
        }
    }

    public boolean createGroundPlan(GroundPlan groundPlan) {
        String query = "BEGIN INSERT INTO ground_plan(id_ground_plan, id_property, img) VALUES(ground_plan_seq.nextval, ? ,ordsys.ordimage.init()) RETURNING id_ground_plan INTO ?; END;";

        try {
            Connection connection = dataSource.getConnection();
            CallableStatement statement = connection.prepareCall(query);
            statement.setInt(1, groundPlan.getIdProperty());
            statement.registerOutParameter(2, Types.NUMERIC);
            statement.execute();

            groundPlan.setIdGroundPlan(statement.getInt(2));

            connection.close();
            statement.close();

            // update created ground plan and update also image
            if (saveGroundPlan(groundPlan)) {

                // notify observers about change is not needed because saveGroundPlan() also notify

                return true;
            } else {
                // ground plan is not update so revert insertion of ground plan
                deleteGroundPlan(groundPlan);

                return false;
            }
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean saveGroundPlan(GroundPlan groundPlan) {

        String query = "UPDATE ground_plan SET id_property = ? WHERE id_ground_plan = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, groundPlan.getIdProperty());
            statement.setInt(2, groundPlan.getIdGroundPlan());

            statement.executeQuery();

            // update metadata
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            OrdImage imgProxy = null;

            // get proxy
            OraclePreparedStatement preparedStatementProxy = (OraclePreparedStatement) connection.prepareStatement(
                    "SELECT img FROM ground_plan WHERE id_ground_plan = ? FOR UPDATE");
            preparedStatementProxy.setInt(1, groundPlan.getIdGroundPlan());
            OracleResultSet resultSet = (OracleResultSet) preparedStatementProxy.executeQuery();
            if (resultSet.next()) {
                imgProxy = (OrdImage) resultSet.getORAData("img", OrdImage.getORADataFactory());
            }

            // use proxy
            if (imgProxy != null) {
                imgProxy.loadDataFromByteArray(groundPlan.getImage());
                imgProxy.setProperties();

                // save changed image
                OraclePreparedStatement preparedStatementUpdate1 = (OraclePreparedStatement) connection.prepareStatement(
                        "UPDATE ground_plan SET img = ?, id_property = ? WHERE id_ground_plan = ?");
                preparedStatementUpdate1.setORAData(1, imgProxy);
                preparedStatementUpdate1.setInt(2, groundPlan.getIdProperty());
                preparedStatementUpdate1.setInt(3, groundPlan.getIdGroundPlan());
                preparedStatementUpdate1.executeUpdate();

                PreparedStatement preparedStatementUpdate2 = connection.prepareStatement(
                        "UPDATE ground_plan g SET g.img_si = SI_StillImage(g.img.getContent()) WHERE g.id_ground_plan = ?");
                preparedStatementUpdate2.setInt(1, groundPlan.getIdGroundPlan());
                preparedStatementUpdate2.executeUpdate();

                PreparedStatement preparedStatementUpdate3 = connection.prepareStatement(
                        "UPDATE ground_plan SET "
                                + "img_ac = SI_AverageColor(img_si), "
                                + "img_ch = SI_ColorHistogram(img_si), "
                                + "img_pc = SI_PositionalColor(img_si), "
                                + "img_tx = SI_Texture(img_si) "
                                + "WHERE id_ground_plan = ?");
                preparedStatementUpdate3.setInt(1, groundPlan.getIdGroundPlan());
                preparedStatementUpdate3.executeUpdate();

                connection.commit();

                preparedStatementUpdate1.close();
                preparedStatementUpdate2.close();
                preparedStatementUpdate3.close();
            }
            connection.setAutoCommit(autoCommit);

            statement.close();
            preparedStatementProxy.close();
            connection.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (IOException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean deleteGroundPlan(GroundPlan groundPlan) {
        String query = "DELETE FROM ground_plan WHERE id_ground_plan = ?";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, groundPlan.getIdGroundPlan());

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

    public boolean rotateGroundPlanLeft(GroundPlan groundPlan) {
        String query = "" +
                "DECLARE" +
                "   obj ORDSYS.ORDImage;" +
                "BEGIN" +
                "   SELECT img INTO obj FROM ground_plan WHERE id_ground_plan = ? FOR UPDATE;" +
                "   obj.process('rotate=-90');" +
                "   UPDATE ground_plan g SET img = obj WHERE id_ground_plan = ?;" +
                "   COMMIT;" +
                "EXCEPTION" +
                "   WHEN ORDSYS.ORDImageExceptions.DATA_NOT_LOCAL THEN" +
                "       DBMS_OUTPUT.PUT_LINE('Data is not local');" +
                "END;";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, groundPlan.getIdGroundPlan());
            statement.setInt(2, groundPlan.getIdGroundPlan());

            statement.executeQuery();

            // update metadata
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            OrdImage imgProxy = null;

            // get proxy
            OraclePreparedStatement preparedStatementProxy = (OraclePreparedStatement) connection.prepareStatement(
                    "SELECT img FROM ground_plan WHERE id_ground_plan = ? FOR UPDATE");
            preparedStatementProxy.setInt(1, groundPlan.getIdGroundPlan());
            OracleResultSet resultSet = (OracleResultSet) preparedStatementProxy.executeQuery();
            if (resultSet.next()) {
                imgProxy = (OrdImage) resultSet.getORAData("img", OrdImage.getORADataFactory());
            }

            // use proxy
            if (imgProxy != null) {
                imgProxy.setProperties();

                // save changed image
                OraclePreparedStatement preparedStatementUpdate1 = (OraclePreparedStatement) connection.prepareStatement(
                        "UPDATE ground_plan SET img = ?, id_property = ? WHERE id_ground_plan = ?");
                preparedStatementUpdate1.setORAData(1, imgProxy);
                preparedStatementUpdate1.setInt(2, groundPlan.getIdProperty());
                preparedStatementUpdate1.setInt(3, groundPlan.getIdGroundPlan());
                preparedStatementUpdate1.executeUpdate();

                PreparedStatement preparedStatementUpdate2 = connection.prepareStatement(
                        "UPDATE ground_plan g SET g.img_si = SI_StillImage(g.img.getContent()) WHERE g.id_ground_plan = ?");
                preparedStatementUpdate2.setInt(1, groundPlan.getIdGroundPlan());
                preparedStatementUpdate2.executeUpdate();

                PreparedStatement preparedStatementUpdate3 = connection.prepareStatement(
                        "UPDATE ground_plan SET "
                                + "img_ac = SI_AverageColor(img_si), "
                                + "img_ch = SI_ColorHistogram(img_si), "
                                + "img_pc = SI_PositionalColor(img_si), "
                                + "img_tx = SI_Texture(img_si) "
                                + "WHERE id_ground_plan = ?");
                preparedStatementUpdate3.setInt(1, groundPlan.getIdGroundPlan());
                preparedStatementUpdate3.executeUpdate();

                connection.commit();

                preparedStatementUpdate1.close();
                preparedStatementUpdate2.close();
                preparedStatementUpdate3.close();
            }
            connection.setAutoCommit(autoCommit);

            statement.close();
            preparedStatementProxy.close();
            connection.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }

    public boolean rotateGroundPlanRight(GroundPlan groundPlan) {
        String query = "" +
                "DECLARE" +
                "   obj ORDSYS.ORDImage;" +
                "BEGIN" +
                "   SELECT img INTO obj FROM ground_plan WHERE id_ground_plan = ? FOR UPDATE;" +
                "   obj.process('rotate=90');" +
                "   UPDATE ground_plan g SET img = obj WHERE id_ground_plan = ?;" +
                "   COMMIT;" +
                "EXCEPTION" +
                "   WHEN ORDSYS.ORDImageExceptions.DATA_NOT_LOCAL THEN" +
                "       DBMS_OUTPUT.PUT_LINE('Data is not local');" +
                "END;";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, groundPlan.getIdGroundPlan());
            statement.setInt(2, groundPlan.getIdGroundPlan());

            statement.executeQuery();

            // update metadata
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            OrdImage imgProxy = null;

            // get proxy
            OraclePreparedStatement preparedStatementProxy = (OraclePreparedStatement) connection.prepareStatement(
                    "SELECT img FROM ground_plan WHERE id_ground_plan = ? FOR UPDATE");
            preparedStatementProxy.setInt(1, groundPlan.getIdGroundPlan());
            OracleResultSet resultSet = (OracleResultSet) preparedStatementProxy.executeQuery();
            if (resultSet.next()) {
                imgProxy = (OrdImage) resultSet.getORAData("img", OrdImage.getORADataFactory());
            }

            // use proxy
            if (imgProxy != null) {
                imgProxy.setProperties();

                // save changed image
                OraclePreparedStatement preparedStatementUpdate1 = (OraclePreparedStatement) connection.prepareStatement(
                        "UPDATE ground_plan SET img = ?, id_property = ? WHERE id_ground_plan = ?");
                preparedStatementUpdate1.setORAData(1, imgProxy);
                preparedStatementUpdate1.setInt(2, groundPlan.getIdProperty());
                preparedStatementUpdate1.setInt(3, groundPlan.getIdGroundPlan());
                preparedStatementUpdate1.executeUpdate();

                PreparedStatement preparedStatementUpdate2 = connection.prepareStatement(
                        "UPDATE ground_plan g SET g.img_si = SI_StillImage(g.img.getContent()) WHERE g.id_ground_plan = ?");
                preparedStatementUpdate2.setInt(1, groundPlan.getIdGroundPlan());
                preparedStatementUpdate2.executeUpdate();

                PreparedStatement preparedStatementUpdate3 = connection.prepareStatement(
                        "UPDATE ground_plan SET "
                                + "img_ac = SI_AverageColor(img_si), "
                                + "img_ch = SI_ColorHistogram(img_si), "
                                + "img_pc = SI_PositionalColor(img_si), "
                                + "img_tx = SI_Texture(img_si) "
                                + "WHERE id_ground_plan = ?");
                preparedStatementUpdate3.setInt(1, groundPlan.getIdGroundPlan());
                preparedStatementUpdate3.executeUpdate();

                connection.commit();

                preparedStatementUpdate1.close();
                preparedStatementUpdate2.close();
                preparedStatementUpdate3.close();
            }
            connection.setAutoCommit(autoCommit);

            statement.close();
            preparedStatementProxy.close();
            connection.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error " + exception.getMessage());

            return false;
        }
    }
}
