package core;

import java.io.IOException;
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
public class PlanChecker {

    public static void main(String args[]) throws SQLException, IOException {
        System.err.println("SELECT A.orderno, A.opno, A.endOriginal, B.mfop_planqty, B.rtcen_name, F.goodinc, B.imast_descext,"
            + " B.mfop_remarks, B.mford_delstore, F.startreal, F.endreal, A.endLatest, C.futurePlanned, E.category, E.note, A.manualEnd, D.pastPlanned, B.mford_item, B.mford_duedate FROM"
            + " (SELECT orderno, opno, endLatest, endOriginal, manualEnd FROM AHP.dbo.u_Zmeny) AS A"
            + " LEFT JOIN"
            + " (SELECT imast_descext, mfop_remarks, mford_delstore, mfop_orderno, mfop_opno, mfop_planqty, rtcen_name, mford_item, mford_duedate FROM MAX2ostr.maxmast.mfop"
            + " JOIN"
            + " MAX2ostr.maxmast.rtcen ON mfop_workcen = rtcen_workcen"
            + " JOIN"
            + " MAX2ostr.maxmast.mford ON mford_orderno = mfop_orderno"
            + " JOIN"
            + " MAX2ostr.maxmast.imast ON imast_item = mford_item GROUP BY mfop_orderno, mfop_opno, imast_descext, mfop_remarks,"
            + " mford_delstore, rtcen_name, mford_item, mfop_planqty, mford_duedate) AS B ON A.orderno = B.mfop_orderno AND A.opno = B.mfop_opno"
            + " LEFT JOIN"
            + " (SELECT Zakazka, Operacia, SUM(Mnozstvo) AS futurePlanned"
            + " FROM AHP.dbo.fn_Plan(CURRENT_TIMESTAMP,'2200-01-01') GROUP BY Zakazka, Operacia) AS C ON C.Zakazka = A.orderno AND C.Operacia = A.opno"
            + " LEFT JOIN "
            + " (SELECT Zakazka, Operacia, SUM(Mnozstvo) AS pastPlanned"
            + " FROM AHP.dbo.fn_Plan('2017-01-01', DATEADD(day, -1, CURRENT_TIMESTAMP)) GROUP BY Zakazka, Operacia) AS D ON D.Zakazka = A.orderno AND D.Operacia = A.opno"
            + " LEFT JOIN"
            + " AHP.dbo.u_Poznamky AS E ON A.orderno = E.orderno AND A.opno = E.opno"
            + " LEFT JOIN"
            + " (SELECT mftrn_orderno, mftrn_opno, MIN(mftrn_date) AS startreal, MAX(mftrn_date) AS endreal, SUM(mftrn_goodinc) AS goodinc"
            + " FROM MAX2ostr.maxmast.mftrn GROUP BY mftrn_orderno, mftrn_opno) AS F ON A.orderno = F.mftrn_orderno AND A.opno = F.mftrn_opno");
    }

    public static void startBackend(String URL) throws SQLException, IOException {
        DBUtils.setURL(URL);
        updateRescheduled();
        insertNewIntoDB();
    }

    public static void insertNewIntoDB() throws SQLException {
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
            Logger.getLogger(PlanChecker.class.getName()).log(Level.SEVERE, null, ex);
            throw new SQLException("Chyba pri vkladaní nových operacií", ex);
        }
    }

    public static void updateRescheduled() throws SQLException {
        try (Connection con = DBUtils.connect();
                PreparedStatement st = con.prepareStatement("UPDATE AHP.dbo.u_Zmeny"
                        + " SET endLatest = B.endN FROM AHP.dbo.u_Zmeny AS A JOIN (SELECT Zakazka, Operacia, MAX(Datum) AS endN"
                        + " FROM AHP.dbo.fn_Plan('2000-01-01','2200-01-01') GROUP BY Zakazka, Operacia) AS B"
                        + " ON A.orderno = B.Zakazka AND A.opno = B.Operacia WHERE A.endLatest != B.endN",
                        Statement.RETURN_GENERATED_KEYS)) {
            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PlanChecker.class.getName()).log(Level.SEVERE, null, ex);
            throw new SQLException("Chyba pri vkladaní nových operacií", ex);
        }
    }
}
