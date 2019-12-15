// package viettel.com.sf.utils.db;
//
// import com.viettel.ems.agent.utils.config.AesEcb;
// import com.viettel.ems.agent.utils.config.Ini;
// import com.zaxxer.hikari.HikariConfig;
// import com.zaxxer.hikari.HikariDataSource;
// import lombok.val;
//
// import java.sql.Connection;
// import java.sql.SQLException;
//
// public class DataSource {
//
//     private static HikariConfig config = new HikariConfig();
//     private static HikariDataSource ds;
//     private static boolean configured = false;
//
//     private DataSource() {}
//
//     public static boolean setConfig(Ini ini) {
//         if (configured) return false;
//         return configured = config(ini);
//     }
//
//     private static boolean config(Ini ini) {
//         val dbOptional = ini.get("DATABASE");
//         if (!dbOptional.isPresent()) return false;
//
//         val db = dbOptional.get();
//
//         val address = ("jdbc:postgresql://" + db.get("host") + ":" + db.get("port") + "/" + db.get("name"));
//         config.setJdbcUrl(address);
//         config.setUsername(db.get("username"));
//         String password = AesEcb.decrypt(db.get("password"));
//         if (!password.isEmpty()) config.setPassword(password);
//
//         config.addDataSourceProperty("cachePrepStmts", "true");
//         config.addDataSourceProperty("prepStmtCacheSize", "250");
//         config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//
//         ds = new HikariDataSource(config);
//         return true;
//     }
//
//     public static Connection getConnection() throws SQLException {
//         return ds.getConnection();
//     }
// }
