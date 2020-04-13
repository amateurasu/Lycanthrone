// package vn.elite.haru.jdbc;
//
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.stereotype.Component;
//
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
//
// @Slf4j
// @Component
// public class RelationalDataAccess implements CommandLineRunner {
//     private final JdbcTemplate jdbc;
//
//     @Autowired
//     public RelationalDataAccess(JdbcTemplate jdbcTemplate) {
//         this.jdbc = jdbcTemplate;
//     }
//
//     @Override
//     public void run(String... strings) {
//         log.info("Creating tables");
//
//         // jdbcTemplate.execute("DROP TABLE customers ");
//         jdbc.execute("CREATE TABLE IF NOT EXISTS customers(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
//
//         // Split up the array of whole names into an array of first/last names
//         List<Object[]> splitUpNames = Stream.of("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
//             .map(name -> name.split(" "))
//             .collect(Collectors.toList());
//
//         // Use a Java 8 stream to print out each tuple of the list
//         splitUpNames.forEach(name -> log.info("Inserting customer record for {} {}", name[0], name[1]));
//
//         // Uses JdbcTemplate's batchUpdate operation to bulk load data
//         jdbc.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?, ?)", splitUpNames);
//
//         log.info("Querying for customer records where first_name = 'Josh':");
//         jdbc.query(
//             "SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
//             new Object[]{"Josh"},
//             (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
//         ).forEach(customer -> log.info(customer.toString()));
//     }
// }
