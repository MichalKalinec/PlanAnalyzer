/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend_core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import secondary.NoteCategoryClass;
import utils.DBUtils;

/**
 *
 * @author Michal Kalinec 444505
 */
public class NotesManager {

    public static List<List<Object>> showNotesForRow(String workcen) throws SQLException {
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery("SELECT rtcen_name, orderno, opno, category, note FROM AHP.dbo.u_Poznamky"
                        + " JOIN MAX2ostr.maxmast.mfop ON u_Poznamky.orderno = mfop_orderno AND u_Poznamky.opno = mfop_opno"
                        + " JOIN MAX2ostr.maxmast.rtcen ON mfop_workcen = rtcen_workcen WHERE rtcen_name = '" + workcen + "'", conn)) {
            List<List<Object>> result = new ArrayList<>();
            while (r.next()) {
                List<Object> tmp = new ArrayList<>();
                tmp.add(r.getString(2));
                tmp.add(r.getString(3));
                tmp.add(new Note(r.getInt(4), r.getString(5)));
                result.add(tmp);
            }
            return result;
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri zobrazovaní poznámok pre riadok", ex);
        }
    }

    public static List<List<Object>> showNotesForColumn(int category) throws SQLException {
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery("SELECT * FROM AHP.dbo.u_Poznamky"
                        + " WHERE category = " + category, conn)) {
            List<List<Object>> result = new ArrayList<>();
            while (r.next()) {
                List<Object> tmp = new ArrayList<>();
                tmp.add(r.getString(1));
                tmp.add(r.getString(2));
                tmp.add(new Note(r.getInt(3), r.getString(4)));
                result.add(tmp);
            }
            return result;
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri zobrazovaní poznámok pre stĺpec", ex);
        }
    }

    public static Map<String, int[]> sumNotesForWorkcens(boolean notEmpty) throws SQLException {
        String s;
        if (notEmpty) {
            s = " LEFT";
        } else {
            s = " FULL";
        }
        try (Connection conn = DBUtils.connect();
                ResultSet r = DBUtils.executeQuery("SELECT rtcen_name, category, COUNT(category) FROM AHP.dbo.u_Poznamky"
                        + " LEFT JOIN MAX2ostr.maxmast.mfop ON u_Poznamky.orderno = mfop_orderno AND u_Poznamky.opno = mfop_opno"
                        + s + " JOIN MAX2ostr.maxmast.rtcen ON mfop_workcen = rtcen_workcen GROUP BY rtcen_name, category ORDER BY rtcen_name ASC", conn)) {
            Map<String, int[]> result = new LinkedHashMap<>();
            while (r.next()) {
                result.putIfAbsent(r.getString(1), new int[NoteCategoryClass.getCategoriesCount()]);
                if (r.getInt(2) == 0) {
                    continue;
                }
                result.get(r.getString(1))[r.getInt(2) - 1] = r.getInt(3);
            }
            return result;
        } catch (SQLException ex) {
            throw new SQLException("Chyba pri sčítavaní poznámok", ex);
        }
    }

}
