package viettel.com.sf.utils.db;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DbUtils {

    public static final int MAX_RETRY = 3;

    public static <T> List<T> query(DbMapper<T> mapper, String sqlQuery, Object... inputParams) {
        return query(mapper, sqlQuery, inputParams, MAX_RETRY);
    }

    private static <T> List<T> query(DbMapper<T> mapper, String sqlQuery, Object[] inputParams, int retry) {
        List<T> result = new ArrayList<>();
        try (
            val connection = DataSource.getConnection();
            val statement = preparedStatement(connection, sqlQuery, inputParams);
            val rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                result.add(mapper.map(rs));
            }
            return result;
        } catch (Exception e) {
            log.error("got exception in retry = " + retry + ", sql = " + sqlQuery, e);

            if (retry > 0) {
                return query(mapper, sqlQuery, inputParams, retry - 1);
            } else {
                return result;
            }
        }
    }

    private static PreparedStatement preparedStatement(Connection connection, String sql, Object[] inputParams) throws SQLException {
        val statement = connection.prepareStatement(sql);

        if (inputParams == null || inputParams.length <= 0) return statement;

        for (int idx = 0; idx < inputParams.length; idx++) {
            statement.setObject(idx + 1, inputParams[idx]);
        }

        return statement;
    }

    public int update(String sqlQuery, Object... inputParams) {
        return update(sqlQuery, inputParams, MAX_RETRY);
    }

    public int update(String sqlQuery, Object[] inputParams, int retry) {
        try (
            val connection = DataSource.getConnection();
            val statement = preparedStatement(connection, sqlQuery, inputParams)
        ) {
            return statement.executeUpdate();
        } catch (Exception e) {
            log.error("got exception in retry = " + retry + ", sql = " + sqlQuery, e);

            if (retry > 0) {
                return update(sqlQuery, inputParams, retry - 1);
            } else {
                return 0;
            }
        }
    }
}
