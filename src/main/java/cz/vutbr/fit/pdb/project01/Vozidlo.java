/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.project01;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

public class Vozidlo {

    public String vyrobce;
    public String model;

    public Vozidlo(String vyrobce, String model) {
        this.vyrobce = vyrobce;
        this.model = model;
    }

    @Override
    public String toString() {
        return vyrobce + ": " + model;
    }

    public void loadFotoFromFile(Connection connection, String filename) throws SQLException, IOException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            OrdImage imgProxy = null;
            // ziskame proxy
            try (OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) connection.prepareStatement(
                    "select foto from vozidlo "
                            + "where vyrobce = ? and model = ? for update")) {
                pstmtSelect.setString(1, this.vyrobce);
                pstmtSelect.setString(2, this.model);
                try (OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery()) {
                    if (rset.next()) {
                        imgProxy = (OrdImage) rset.getORAData("foto", OrdImage.getORADataFactory());
                    }
                }
            }
            // pouzijeme proxy
            if (imgProxy != null) {
                imgProxy.loadDataFromFile(filename);
                imgProxy.setProperties();

                // ulozime zmenene obrazky
                try (OraclePreparedStatement pstmtUpdate1 = (OraclePreparedStatement) connection.prepareStatement(
                        "update vozidlo set foto = ? "
                                + "where vyrobce = ? and model = ?")) {
                    pstmtUpdate1.setORAData(1, imgProxy);
                    pstmtUpdate1.setString(2, this.vyrobce);
                    pstmtUpdate1.setString(3, this.model);
                    pstmtUpdate1.executeUpdate();
                }
                try (PreparedStatement pstmtUpdate2 = connection.prepareStatement(
                        "update vozidlo v set v.foto_si = SI_StillImage(v.foto.getContent()) "
                                + "where vyrobce = ? and model = ?")) {
                    pstmtUpdate2.setString(1, this.vyrobce);
                    pstmtUpdate2.setString(2, this.model);
                    pstmtUpdate2.executeUpdate();
                }
                try (PreparedStatement pstmtUpdate3 = connection.prepareStatement(
                        "update vozidlo set "
                                + "foto_ac = SI_AverageColor(foto_si), "
                                + "foto_ch = SI_ColorHistogram(foto_si), "
                                + "foto_pc = SI_PositionalColor(foto_si), "
                                + "foto_tx = SI_Texture(foto_si) "
                                + "where vyrobce = ? and model = ?")) {
                    pstmtUpdate3.setString(1, this.vyrobce);
                    pstmtUpdate3.setString(2, this.model);
                    pstmtUpdate3.executeUpdate();
                }
                connection.commit();
            }
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    public void saveFotoToFile(Connection connection, String filename) throws SQLException, IOException {
        OrdImage imgProxy = null;
        // ziskame proxy
        try (OraclePreparedStatement pstmtSelect = (OraclePreparedStatement) connection.prepareStatement(
                "select foto from vozidlo where vyrobce = ? and model = ?")) {
            pstmtSelect.setString(1, this.vyrobce);
            pstmtSelect.setString(2, this.model);
            try (OracleResultSet rset = (OracleResultSet) pstmtSelect.executeQuery()) {
                if (rset.next()) {
                    imgProxy = (OrdImage) rset.getORAData("foto", OrdImage.getORADataFactory());
                }
            }
        }
        // ulozime do souboru
        if (imgProxy != null) {
            imgProxy.getDataInFile(filename);
        }
    }

    public Vozidlo getTheMostSimilar(Connection connection, App katalog, double weightAC, double weightCH, double weightPC, double weightTX) throws SQLException {
        String simVyrobce = null;
        String simModel = null;
        // najdeme zaznam podobneho fota
        try (PreparedStatement pstmtSelect = connection.prepareStatement(
                "SELECT dst.vyrobce, dst.model, SI_ScoreByFtrList("
                        + "new SI_FeatureList(src.foto_ac,?,src.foto_ch,?,src.foto_pc,?,src.foto_tx,?),dst.foto_si)"
                        + " as similarity FROM vozidlo src, vozidlo dst "
                        + "WHERE (src.vyrobce <> dst.vyrobce OR src.model <> dst.model) "
                        + "AND src.vyrobce = ? and src.model = ? ORDER BY similarity ASC")) {
            pstmtSelect.setDouble(1, weightAC);
            pstmtSelect.setDouble(2, weightCH);
            pstmtSelect.setDouble(3, weightPC);
            pstmtSelect.setDouble(4, weightTX);
            pstmtSelect.setString(5, this.vyrobce);
            pstmtSelect.setString(6, this.model);
            try (ResultSet rset = pstmtSelect.executeQuery()) {
                if (rset.next()) {
                    simVyrobce = rset.getString(1);
                    simModel = rset.getString(2);
                }
            }
        }
        // nalezneme ziskane vozidlo v katalogu (tam je "jeho" objekt)
        for (Iterator<Vozidlo> i = katalog.getVozidloIterator(); i.hasNext(); ) {
            Vozidlo v = i.next();
            if (v.vyrobce.equals(simVyrobce) && v.model.equals(simModel)) {
                return v;
            }
        }
        // pokud nenalezneme, tak null
        return null;
    }

    /* generated
     * needed for removing */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Vozidlo)) {
            return false;
        }
        final Vozidlo other = (Vozidlo) object;
        return (vyrobce == null ? other.vyrobce == null : vyrobce.equals(other.vyrobce)) && (model == null ? other.model == null : model.equals(other.model));
    }

    /* generated */
    @Override
    public int hashCode() {
        final int PRIME = 37;
        int result = 1;
        result = PRIME * result + ((vyrobce == null) ? 0 : vyrobce.hashCode());
        result = PRIME * result + ((model == null) ? 0 : model.hashCode());
        return result;
    }
}
