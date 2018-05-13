package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class for executing SQL scripts.
 *
 * @author Michal Kalinec
 */
public class DBUtils {

    private static String URL;

    /**
     * Estabilish and return connection with DB.
     *
     * @param url
     * @return
     */
    public static Connection connect() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL);
            return connection;
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri pripájaní do databázy.", ex);
        }
    }

    public static ResultSet executeQuery(String sql, Connection conn) throws SQLException {
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri vykonávaní SQL skriptu. " + System.lineSeparator() + sql, ex);
        }
    }

    public static LocalDateTime getDateTime(ResultSet r, int index) throws SQLException {
        LocalDateTime endTime;
        if (r.getString(index) == null) {
            return null;
        }
        if (r.getString(index).length() == 10) {
            endTime = LocalDate.parse(r.getString(index), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } else {
            endTime = LocalDateTime.parse(r.getString(index), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        }
        return endTime;
    }

    public static Map<String, int[]> sumNotesForWorkcens(boolean notEmpty) throws SQLException {
        String s;
        if (notEmpty) {
            s = " LEFT";
        } else {
            s = " FULL";
        }
        try (Connection conn = connect();
                ResultSet r = executeQuery("SELECT rtcen_name, category, COUNT(category) FROM AHP.dbo.u_Poznamky"
                        + " LEFT JOIN MAX2ostr.maxmast.mfop ON u_Poznamky.orderno = mfop_orderno AND u_Poznamky.opno = mfop_opno"
                        + s + " JOIN MAX2ostr.maxmast.rtcen ON mfop_workcen = rtcen_workcen GROUP BY rtcen_name, category ORDER BY rtcen_name ASC", conn)) {
            Map<String, int[]> result = new LinkedHashMap<>();
            while (r.next()) {
                result.putIfAbsent(r.getString(1), new int[12]);
                if(r.getInt(2) == 0){
                    continue;
                }
                result.get(r.getString(1))[r.getInt(2) - 1] = r.getInt(3);
            }
            return result;
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri sčítavaní poznámok", ex);
        }
    }

    public static void setURL(String URL) {
        DBUtils.URL = URL;
    }
}
