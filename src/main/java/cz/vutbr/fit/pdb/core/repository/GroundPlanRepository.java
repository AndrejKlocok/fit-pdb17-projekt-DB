/*
 * Copyright (C) 2017 VUT FIT PDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

/**
 * Repository creates ground plan typed object (@see GroundPlan), queries and calls to Oracle database.
 * Repository works mainly with table Ground_plan.
 * Class extends @see Observable.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class GroundPlanRepository extends Observable {

    private OracleDataSource dataSource;

    /**
     * Constructor for ground plan repository @see GroundPlanRepository.
     *
     * @param dataSource @see OracleDataSource
     */
    public GroundPlanRepository(OracleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Method calls query under table Ground plan and returns ground plan with desired property.
     *
     * @param property @see Property object
     * @return List of @see GroundPlan objects.
     */
    public List<GroundPlan> getGroundPlanListOfProperty(Property property) {
        String query = "SELECT * " +
                "FROM ground_plan " +
                "WHERE id_property = ? " +
                "ORDER BY id_ground_plan";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
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
            System.err.println("Error getGroundPlanListOfProperty " + exception.getMessage());

            return null;
        } catch (SQLException exception) {
            System.err.println("Error getGroundPlanListOfProperty " + exception.getMessage());

            return new LinkedList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getGroundPlanListOfProperty " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground plan and returns ground plan with desired id.
     *
     * @param groundPlan @see GroundPlan
     * @return @see GroundPlan object.
     */
    public GroundPlan getGroundPlan(GroundPlan groundPlan) {
        String query = "SELECT * " +
                "FROM ground_plan " +
                "WHERE id_ground_plan = ?";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
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
            System.err.println("Error getGroundPlan " + exception.getMessage());

            return null;
        } catch (SQLException exception) {
            System.err.println("Error getGroundPlan " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getGroundPlan " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground plan and returns ground plan with desired id.
     *
     * @param idGroundPlan Integer value, which represents id of ground plan
     * @return @see GroundPlan object.
     */
    public GroundPlan getGroundPlanById(int idGroundPlan) {
        String query = "SELECT * " +
                "FROM ground_plan " +
                "WHERE id_ground_plan = ?";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
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
            System.err.println("Error getGroundPlanById " + exception.getMessage());

            return null;
        } catch (SQLException exception) {
            System.err.println("Error getGroundPlanById " + exception.getMessage());

            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error getGroundPlanById " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground plan and inserts ground plan according to given parameter.
     *
     * @param groundPlan @see GroundPlan
     * @return boolean True if query was successful otherwise False.
     */
    public boolean createGroundPlan(GroundPlan groundPlan) {
        String query = "BEGIN " +
                "INSERT INTO ground_plan(id_ground_plan, id_property, img) " +
                "VALUES(ground_plan_seq.nextval, ? ,ordsys.ordimage.init()) " +
                "RETURNING id_ground_plan INTO ?; " +
                "END;";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
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
            System.err.println("Error createGroundPlan " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error createGroundPlan " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground plan and updates ground plan according to given parameter.
     *
     * @param groundPlan ground plan
     * @return boolean True if query was successful otherwise False.
     */
    public boolean saveGroundPlan(GroundPlan groundPlan) {

        String query = "UPDATE ground_plan " +
                "SET id_property = ? " +
                "WHERE id_ground_plan = ?";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
            System.err.println("Error saveGroundPlan " + exception.getMessage());

            return false;
        } catch (SQLException exception) {
            System.err.println("Error saveGroundPlan " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error saveGroundPlan " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground plan and deletes ground plan according to given parameter.
     *
     * @param groundPlan ground plan
     * @return boolean True if query was successful otherwise False.
     */
    public boolean deleteGroundPlan(GroundPlan groundPlan) {
        String query = "DELETE " +
                "FROM ground_plan " +
                "WHERE id_ground_plan = ?";

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, groundPlan.getIdGroundPlan());

            statement.executeQuery();

            connection.close();
            statement.close();

            // notify observers about change
            setChanged();
            notifyObservers();

            return true;
        } catch (SQLException exception) {
            System.err.println("Error deleteGroundPlan " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error deleteGroundPlan " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground plan and rotates left ground plan according to given parameter.
     *
     * @param groundPlan ground plan
     * @return boolean True if query was successful otherwise False.
     */
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

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
            System.err.println("Error rotateGroundPlanLeft " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error rotateGroundPlanLeft " + exception.getMessage());
            }
        }
    }

    /**
     * Method calls query under table Ground plan and rotates right ground plan according to given parameter.
     *
     * @param groundPlan ground plan
     * @return boolean True if query was successful otherwise False.
     */
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

        Connection connection = null;
        PreparedStatement statement;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
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
            System.err.println("Error rotateGroundPlanRight " + exception.getMessage());

            return false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exception) {
                System.err.println("Error rotateGroundPlanRight " + exception.getMessage());
            }
        }
    }
}
