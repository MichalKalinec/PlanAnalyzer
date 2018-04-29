package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.DBUtils;

/**
 * List of operations and map of their notes with methods creating them.
 *
 * @author Michal Kalinec 444505
 */
public class OperationsList {

    private Map<Operation, List<Note>> operations;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    //Mutual core of SQL scripts creating instances of Operation from DBs.
    private static final String BASIC_SQL = "SELECT A.orderno, A.opno, A.endOriginal, B.mfop_planqty, B.rtcen_name, F.goodinc, B.imast_descext,"
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
            + " FROM MAX2ostr.maxmast.mftrn GROUP BY mftrn_orderno, mftrn_opno) AS F ON A.orderno = F.mftrn_orderno AND A.opno = F.mftrn_opno";

    public OperationsList() {
        this.operations = new LinkedHashMap<>();
    }

    public static OperationsList showRescheduled(Date from, Date to) throws SQLException {
        OperationsList filtered = new OperationsList();
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery(BASIC_SQL
                        + " WHERE A.endOriginal != A.endLatest"
                        + " AND ((A.endOriginal BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "')"
                        + " OR (F.startreal BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "')"
                        + " OR (A.endLatest BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "')"
                        + " OR (F.endreal BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "'))", conn)) {
            while (r.next()) {
                filtered.createOp(r);
            }
        } catch (SQLException ex) {
            String msg = "Chyba pri filtrovaní preplánovaných operácií.";
            Logger.getLogger(OperationsList.class.getName()).log(Level.SEVERE, msg, ex);
            throw new SQLException(msg, ex);
        }
        return filtered;
    }

    public static OperationsList showOrder(String orderNo) throws SQLException {
        OperationsList filtered = new OperationsList();
        try (Connection conn = DBUtils.connect();
                PreparedStatement st = conn.prepareStatement(BASIC_SQL
                        + " WHERE A.orderno = ?")) {
            st.setString(1, orderNo);
            try (ResultSet r = st.executeQuery()) {
                while (r.next()) {
                    filtered.createOp(r);
                }

            }
        } catch (SQLException ex) {
            String msg = "Chyba pri filtrovaní operácií podľa zákazky.";
            Logger.getLogger(OperationsList.class.getName()).log(Level.SEVERE, msg, ex);
            throw new SQLException(msg, ex);
        }
        return filtered;
    }

    public static OperationsList filterCurrentOps(List<String> workcens, Date from, Date to, boolean unfinishedOnly, boolean lateOnly) throws SQLException {
        OperationsList filtered = new OperationsList();
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery(BASIC_SQL
                        + " WHERE (A.endOriginal BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "')"
                        + " OR (F.startreal BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "')"
                        + " OR (A.endLatest BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "')"
                        + " OR (F.endreal BETWEEN '" + formatter.format(from) + "' AND '" + formatter.format(to) + "')"
                        + " OR (((B.mfop_planqty > F.goodinc AND C.futurePlanned IS NULL)"
                        + " OR (C.futurePlanned IS NULL AND F.goodinc IS NULL)"
                        + " OR (B.mfop_planqty > C.futurePlanned AND F.goodinc < B.mfop_planqty)"
                        + " OR (B.mfop_planqty > C.futurePlanned AND F.goodinc IS NULL)) AND (A.manualEnd IS NULL))", conn)) {
            while (r.next()) {
                System.err.println(1);
                if (unfinishedOnly && r.getBoolean(16) == true) {
                    continue;
                }
                if (Double.compare(r.getDouble(6), r.getDouble(4)) >= 0 && r.getDouble(4) != 0) {
                    if (unfinishedOnly || (!DBUtils.getDateTime(r, 11).isAfter(DBUtils.getDateTime(r, 3)) && lateOnly)) {
                        continue;
                    }
                }
                if (Double.compare(r.getDouble(6), (r.getDouble(4)) - r.getDouble(13)) >= 0 && lateOnly) {
                    continue;
                }
                if (r.getString(9) == null) {
                    filtered.createOp(r);
                    continue;
                }
                if (workcens.contains(r.getString(9).replaceAll("\\s+", ""))) {
                    filtered.createOp(r);
                }
            }
        } catch (SQLException ex) {
            String msg = "Chyba pri filtrovaní operácií podľa stredisiek.";
            Logger.getLogger(OperationsList.class.getName()).log(Level.SEVERE, msg, ex);
            throw new SQLException(msg, ex);
        }
        return filtered;
    }

    /**
     * Auxiliary method for building operations from ResultSet.
     *
     * @param r
     * @throws SQLException
     */
    private void createOp(ResultSet r) throws SQLException {
        OperationBuilder builder = new OperationBuilder();
        if (Double.compare(r.getDouble(6), r.getDouble(4)) >= 0) {
            builder.endReal(DBUtils.getDateTime(r, 11));
        }
        Operation op = builder.orderNo(r.getString(1))
                .opNo(r.getString(2))
                .quantityPlan(r.getDouble(17))
                .endPlan(DBUtils.getDateTime(r, 3))
                .quantityTotal(r.getDouble(4))
                .workcen(r.getString(5))
                .quantityReal(r.getDouble(6))
                .itemDescription(r.getString(7))
                .opDescription(r.getString(8))
                .delstore(r.getString(9))
                .startReal(DBUtils.getDateTime(r, 10))
                .endRescheduled(DBUtils.getDateTime(r, 12))
                .manuallyEnded(r.getBoolean(16))
                .itemNo(r.getString(18))
                .endRequired(DBUtils.getDateTime(r, 19))
                .build();
        if (!operations.keySet().contains(op)) {
            operations.put(op, null);
        }
        if (r.getInt(14) > 0) {
            operations.putIfAbsent(op, new ArrayList<>());
            operations.get(op).add(new Note(r.getInt(14), r.getString(15)));
        }
    }

    public Map<Operation, List<Note>> getOperations() {
        return operations;
    }
}
