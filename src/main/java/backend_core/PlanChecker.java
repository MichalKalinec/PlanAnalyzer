package backend_core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import utils.DBUtils;

/**
 *
 * @author Michal Kalinec 444505
 */
public class PlanChecker {

    public static void main(String args[]) throws SQLException, IOException {
        
    }

    public static void startBackend(String URL) throws SQLException, IOException {
        DBUtils.setURL(URL);
        updateRescheduled();
        insertNewIntoDB();
    }

    private static void insertNewIntoDB() throws SQLException {
        try (Connection conn = DBUtils.connect();
                PreparedStatement st = conn.prepareStatement("INSERT INTO AHP.dbo.u_Zmeny"
                        + " SELECT * FROM (SELECT Zakazka, Operacia, MAX(Datum) AS endLatest, MAX(Datum) AS endOriginal, NULL AS manualEnd"
                        + " FROM AHP.dbo.fn_Plan('2010-09-01','2100-01-01') GROUP BY Zakazka, Operacia) AS A"
                        + " WHERE NOT EXISTS"
                        + " (SELECT * FROM AHP.dbo.u_Zmeny AS B"
                        + " WHERE A.Zakazka=B.orderno AND A.Operacia=B.opno)",
                        Statement.RETURN_GENERATED_KEYS)) {
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri vkladaní nových operácií.", ex);
        }
    }

    private static void updateRescheduled() throws SQLException {
        try (Connection con = DBUtils.connect();
                PreparedStatement st = con.prepareStatement("UPDATE AHP.dbo.u_Zmeny"
                        + " SET endLatest = B.endN FROM AHP.dbo.u_Zmeny AS A JOIN (SELECT Zakazka, Operacia, MAX(Datum) AS endN"
                        + " FROM AHP.dbo.fn_Plan('2000-01-01','2200-01-01') GROUP BY Zakazka, Operacia) AS B"
                        + " ON A.orderno = B.Zakazka AND A.opno = B.Operacia WHERE A.endLatest != B.endN",
                        Statement.RETURN_GENERATED_KEYS)) {
            st.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri aktualizovaní operácií.", ex);
        }
    }
}
