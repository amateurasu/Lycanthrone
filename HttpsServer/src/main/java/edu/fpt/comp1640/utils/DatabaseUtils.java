package edu.fpt.comp1640.utils;

import edu.fpt.comp1640.database.Database;
import edu.fpt.comp1640.database.ResultSetHandler;
import lombok.val;
import lombok.var;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DatabaseUtils {
    public static String getJSON(String sql, String[] fields, Object[] param) throws Exception {
        val result = new JSONArray();
        each(sql, param, rs -> {
            val object = new JSONObject();
            val columnCount = fields.length;
            for (var i = 1; i <= columnCount; i++) {
                object.put(fields[i - 1], rs.getObject(i));
            }
            result.add(object);
        });
        return result.toJSONString();
    }

    public static void each(String sql, Object[] param, ResultSetHandler handler) throws Exception {
        try (val db = new Database()) {
            db.query(sql, param, rs -> {
                while (rs.next()) {
                    handler.handle(rs);
                }
            });
        }
    }

    public static void getResult(String sql, Object[] param, ResultSetHandler handler) throws Exception {
        try (val db = new Database()) {
            db.query(sql, param, handler);
        }
    }
}
