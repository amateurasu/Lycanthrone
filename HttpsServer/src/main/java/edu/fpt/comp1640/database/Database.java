package edu.fpt.comp1640.database;

import edu.fpt.comp1640.model.user.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;

@Slf4j
public class Database implements AutoCloseable {
    private static final String DB_URL = "jdbc:sqlite:./.db/sqlite/comp1640.sqlite";

    private final Connection connection;
    private static final Security security = new Security();

    public Database() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
    }

    //FIXME
    public User authenticate(String account, String password) {
        val query = "SELECT id, name, username, hashed_password, email, role, role_id FROM Users WHERE username = ? OR email = ? LIMIT 1";
        try (
            val connect = this.connection;
            val pstm = connect.prepareStatement(query)
        ) {
            pstm.setString(1, account);
            pstm.setString(2, account);

            val result = pstm.executeQuery();
            if (result.next()) {
                val name = result.getString("name");
                val username = result.getString("username");
                val hashedPassword = result.getString("hashed_password");
                val email = result.getString("email");
                val role_id = result.getInt("role_id");
                val role = result.getInt("role");

                if (validatePassword(password, hashedPassword)) {
                    return User.getUser(name, username, hashedPassword, email, role, role_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean validatePassword(String password, String code) {
        try {
            return security.validatePassword(password, code);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            log.error("Cannot validate password", ex);
        }
        return false;
    }

    public static String createHashed(String pass) {
        try {
            return security.createPassword(pass);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            log.error("Cannot create password", ex);
        }
        return null;
    }

    public ResultSet query(String sql) throws SQLException {
        return connection.prepareStatement(sql, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY).executeQuery();
    }

    public ResultSet query(String sql, Object... param) throws SQLException {
        try (val pstm = connection.prepareStatement(sql, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)) {
            prepareStatement(pstm, param);
            return pstm.executeQuery();
        }
    }

    public void query(String sql, Object[] param, ResultSetHandler handler) throws Exception {
        try (val pstm = connection.prepareStatement(sql, TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)) {
            prepareStatement(pstm, param);
            ResultSet resultSet = pstm.executeQuery();
            handler.handle(resultSet);
        }
    }

    public ResultSet insert(String sql, Object[] param) throws SQLException {
        try (val pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement(pstm, param);
            pstm.executeUpdate();
            return pstm.getGeneratedKeys();
        }
    }

    public int update(String sql, Object[] param) throws SQLException {
        try (val pstm = connection.prepareStatement(sql)) {
            prepareStatement(pstm, param);
            return pstm.executeUpdate();
        }
    }

    private static void prepareStatement(PreparedStatement pstm, Object... param) throws SQLException {
        int length = param == null ? 0 : param.length;
        for (int i = 0; i < length; i++) {
            pstm.setObject(i + 1, param[i]);
        }
    }

    @Override
    public void finalize() {
        close();
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Throwable ex) {
            log.error("Error", ex);
        }
    }
}
