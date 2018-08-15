package backend_core;

import utils.OperationBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import utils.DBUtils;

/**
 * List of operations and map of their notes with methods creating them.
 *
 * @author Michal Kalinec 444505
 */
public class OperationsMap {

    private Map<Operation, List<Note>> operations;
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    //Mutual core of SQL scripts creating instances of Operation from DBs.
    private static final String BASIC_SQL = "SELECT A.orderno, A.opno, A.endOriginal, B.mfop_planqty, B.rtcen_name, F.goodinc, B.imast_descext, B.mfop_remarks, B.mford_delstore,"
            + " F.startreal, F.endreal, A.endLatest, C.futurePlanned, E.category, E.note, A.manualEnd, D.pastPlanned, B.mford_item, B.mford_duedate, B.mford_dueqty FROM"
            + " (SELECT orderno, opno, endLatest, endOriginal, manualEnd FROM AHP.dbo.u_Zmeny) AS A"
            + " LEFT JOIN"
            + " (SELECT imast_descext, mfop_remarks, mford_delstore, mfop_orderno, mfop_opno, mfop_planqty, rtcen_name, mford_item, mford_duedate, mford_dueqty FROM MAX2ostr.maxmast.mfop"
            + " JOIN"
            + " MAX2ostr.maxmast.rtcen ON mfop_workcen = rtcen_workcen"
            + " JOIN"
            + " MAX2ostr.maxmast.mford ON mford_orderno = mfop_orderno"
            + " JOIN"
            + " MAX2ostr.maxmast.imast ON imast_item = mford_item GROUP BY mfop_orderno, mfop_opno, imast_descext, mfop_remarks,"
            + " mford_delstore, rtcen_name, mford_item, mfop_planqty, mford_duedate, mford_dueqty) AS B ON A.orderno = B.mfop_orderno AND A.opno = B.mfop_opno"
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
            + " FROM (SELECT mftrn_orderno, mftrn_opno, mftrn_date, mftrn_goodinc FROM MAX2ostr.maxmast.mftrn"
            + " UNION ALL"
            + " SELECT uvop_orderno, uvop_opno, uvop_datestrt, uvop_qtygood FROM MAX2ostr.maxmast.uvop"
            + " WHERE uvop_status<2) AS odpisane GROUP BY mftrn_orderno, mftrn_opno) AS F ON A.orderno = F.mftrn_orderno AND A.opno = F.mftrn_opno";

    public OperationsMap() {
        this.operations = new LinkedHashMap<>();
    }

    public static OperationsMap filterCurrentOps(List<String> workcens, Date from, Date to, boolean unfinishedOnly, boolean lateOnly) throws SQLException {
        OperationsMap filtered = new OperationsMap();
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery(BASIC_SQL
                        + " WHERE (A.endOriginal BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "')"
                        + " OR (F.startreal BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "')"
                        + " OR (A.endLatest BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "')"
                        + " OR (F.endreal BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "')"
                        + " OR (((B.mfop_planqty > F.goodinc AND C.futurePlanned IS NULL)"
                        + " OR (C.futurePlanned IS NULL AND F.goodinc IS NULL)"
                        + " OR (B.mfop_planqty > C.futurePlanned AND F.goodinc < B.mfop_planqty)"
                        + " OR (B.mfop_planqty > C.futurePlanned AND F.goodinc IS NULL)) AND (A.manualEnd IS NULL))"
                        + " ORDER BY A.orderno, A.opno", conn)) {
            while (r.next()) {
                if (unfinishedOnly && r.getBoolean(16) == true) {
                    continue;
                }
                if (Double.compare(r.getDouble(6), r.getDouble(20)) >= 0 && r.getDouble(4) != 0) {
                    if (unfinishedOnly || (!DBUtils.getDateTimeFromResultSet(r, 11).isAfter(DBUtils.getDateTimeFromResultSet(r, 3)) && lateOnly)) {
                        continue;
                    }
                }
                if (Double.compare(r.getDouble(6), r.getDouble(4) - r.getDouble(13)) >= 0 && lateOnly) {
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
            throw new SQLException("Chyba pri filtrovaní operácií podľa stredisiek.", ex);
        }
        return filtered;
    }

    public static OperationsMap showRescheduled(Date from, Date to) throws SQLException {
        OperationsMap filtered = new OperationsMap();
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery(BASIC_SQL
                        + " WHERE A.endOriginal != A.endLatest"
                        + " AND ((A.endOriginal BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "')"
                        + " OR (F.startreal BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "')"
                        + " OR (A.endLatest BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "')"
                        + " OR (F.endreal BETWEEN '" + FORMATTER.format(from) + "' AND '" + FORMATTER.format(to) + "'))", conn)) {
            while (r.next()) {
                filtered.createOp(r);
            }
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri filtrovaní preplánovaných operácií.", ex);
        }
        return filtered;
    }

    public static OperationsMap searchWithOrderNo(String orderNo) throws SQLException {
        OperationsMap filtered = new OperationsMap();
        orderNo = orderNo
                    .replace("!", "!!")
                    .replace("%", "!%")
                    .replace("_", "!_")
                    .replace("[", "![");
        try (Connection conn = DBUtils.connect();
                PreparedStatement st = conn.prepareStatement(BASIC_SQL
                        + " WHERE A.orderno LIKE ? ESCAPE '!'"
                        + " ORDER BY B.mford_item DESC, A.orderno DESC, A.opno ASC")) {
            st.setString(1, "%" + orderNo + "%");
            try (ResultSet r = st.executeQuery()) {
                while (r.next()) {
                    filtered.createOp(r);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri filtrovaní operácií podľa zákazky. (W)", ex);
        }
        return filtered;
    }
    
    public static OperationsMap searchWithItemNo(String itemNo) throws SQLException {
        OperationsMap filtered = new OperationsMap();
        itemNo = itemNo
                    .replace("!", "!!")
                    .replace("%", "!%")
                    .replace("_", "!_")
                    .replace("[", "![");
        try (Connection conn = DBUtils.connect();
                PreparedStatement st = conn.prepareStatement(BASIC_SQL
                        + " WHERE B.mford_item LIKE ? ESCAPE '!'"
                        + " ORDER BY B.mford_item DESC, A.orderno DESC, A.opno ASC")) {
            st.setString(1, "%" + itemNo + "%");
            try (ResultSet r = st.executeQuery()) {
                while (r.next()) {
                    filtered.createOp(r);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri filtrovaní operácií podľa zákazky. (ČP)", ex);
        }
        return filtered;
    }
    
    public static OperationsMap showAllOps() throws SQLException {
        OperationsMap result = new OperationsMap();
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery(BASIC_SQL
                        + " ORDER BY B.mford_item DESC, A.orderno DESC, A.opno ASC", conn)) {
            while (r.next()) {
                result.createOp(r);
            }
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri zobrazovaní všetkých operácií.", ex);
        }
        return result;
    }

    /**
     * Auxiliary method for building operations from ResultSet.
     *
     * @param r
     * @throws SQLException
     */
    private void createOp(ResultSet r) throws SQLException {
        OperationBuilder builder = new OperationBuilder();
        if (Double.compare(r.getDouble(6), r.getDouble(20)) >= 0) {
            builder.endReal(DBUtils.getDateTimeFromResultSet(r, 11));
        }
        Operation op = builder.orderNo(r.getString(1))
                .opNo(r.getString(2))
                .quantityPlan(r.getDouble(17))
                .endPlan(DBUtils.getDateTimeFromResultSet(r, 3))
                .quantityTotal(r.getDouble(4))
                .workcen(r.getString(5))
                .quantityReal(r.getDouble(6))
                .itemDescription(r.getString(7))
                .opDescription(r.getString(8))
                .delstore(r.getString(9))
                .startReal(DBUtils.getDateTimeFromResultSet(r, 10))
                .endRescheduled(DBUtils.getDateTimeFromResultSet(r, 12))
                .manuallyEnded(r.getBoolean(16))
                .itemNo(r.getString(18))
                .endRequired(DBUtils.getDateTimeFromResultSet(r, 19))
                .quantityRequired(r.getDouble(20))
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
