package edu.fpt.comp1640;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            PoolProperties p = new PoolProperties() {{
                setUrl("jdbc:mysql://localhost:3306/mysql");
                setDriverClassName("com.mysql.jdbc.Driver");
                setUsername("root");
                setPassword("password");
                setJmxEnabled(true);
                setTestWhileIdle(false);
                setTestOnBorrow(true);
                setValidationQuery("SELECT 1");
                setTestOnReturn(false);
                setValidationInterval(30000);
                setTimeBetweenEvictionRunsMillis(30000);
                setMaxActive(100);
                setInitialSize(10);
                setMaxWait(10000);
                setRemoveAbandonedTimeout(60);
                setMinEvictableIdleTimeMillis(30000);
                setMinIdle(10);
                setLogAbandoned(true);
                setRemoveAbandoned(true);
                setJdbcInterceptors(
                    "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
            }};

            DataSource datasource = new DataSource();
            datasource.setPoolProperties(p);

            try (
                Connection con = datasource.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from user")
            ) {
                int cnt = 1;
                while (rs.next()) {
                    System.out.println((cnt++) + ". Host:" + rs.getString("Host") +
                        " User:" + rs.getString("User") + " Password:" + rs.getString("Password"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}