package backend_core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import utils.DBUtils;

/**
 *
 * @author Michal Kalinec 444505
 */
public class OperationManager {

    public void insertNoteInfo(Operation op, int cat, String text) throws SQLException {
        try (Connection conn = DBUtils.connect();
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO AHP.dbo.u_Poznamky VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, op.getOrderNo());
            st.setString(2, op.getOpNo());
            st.setInt(3, cat);
            st.setString(4, text);
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri vkladaní poznámky.", ex);
        }
    }

    public void manuallyEndOp(Operation op) throws SQLException {
        try (Connection con = DBUtils.connect();
                PreparedStatement st = con.prepareStatement("UPDATE AHP.dbo.u_Zmeny"
                        + " SET manualEnd = 1 WHERE orderno = ? AND opno = ?",
                        Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, op.getOrderNo());
            st.setString(2, op.getOpNo());
            st.executeUpdate();
            op.setIsManuallyEnded(true);
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri ručnom ukončení operácie.", ex);
        }
    }
}
