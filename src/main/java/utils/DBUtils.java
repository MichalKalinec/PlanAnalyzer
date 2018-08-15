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
public final class DBUtils {

    private static String URL;

    private DBUtils() {
    }

    ;

    /**
     * Estabilish and return connection with DB.
     *
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

    public static LocalDateTime getDateTimeFromResultSet(ResultSet r, int index) throws SQLException {
        LocalDateTime endTime;
        if (r.getString(index) == null) {
            return null;
        }
        if (r.getString(index).length() == 10) {
            endTime = LocalDate.parse(r.getString(index), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } else if (r.getString(index).length() == 23) {
            endTime = LocalDateTime.parse(r.getString(index), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        } else if (r.getString(index).length() == 22) {
            endTime = LocalDateTime.parse(r.getString(index), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));
        } else {
            endTime = LocalDateTime.parse(r.getString(index), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        }
        return endTime;
    }

    public static void setURL(String URL) {
        DBUtils.URL = URL;
    }
}
