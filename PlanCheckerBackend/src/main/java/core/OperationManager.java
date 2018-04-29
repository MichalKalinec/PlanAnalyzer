/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.DBUtils;

/**
 *
 * @author Michal Kalinec 444505
 */
public class OperationManager {

    public static void insertNoteInfo(Operation op, Note note) throws SQLException {
        try (Connection conn = DBUtils.connect();
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO AHP.dbo.u_Poznamky VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, op.getOrderNo());
            st.setString(2, op.getOpNo());
            st.setInt(3, note.getCategory());
            st.setString(4, note.getText());
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OperationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new SQLException("Chyba pri vkladani poznamky", ex);
        }
    }
    
    public static void manualEndOp(Operation op) throws SQLException {
        try (Connection con = DBUtils.connect();
                PreparedStatement st = con.prepareStatement("UPDATE AHP.dbo.u_Zmeny"
                        + " SET manualEnd = 1 WHERE orderno = ? AND opno = ?",
                        Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, op.getOrderNo());
            st.setString(2, op.getOpNo());
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OperationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new SQLException("Chyba pri rucnom ukonceni operacie", ex);
        }
    }
}
