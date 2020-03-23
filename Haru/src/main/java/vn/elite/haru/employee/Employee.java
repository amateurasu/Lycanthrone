// package vn.elite.haru.employee;
//
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import lombok.Data;
// import lombok.NoArgsConstructor;
//
// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.Id;
// import javax.persistence.Version;
//
// @Data
// @Entity
// @NoArgsConstructor
// @JsonIgnoreProperties(ignoreUnknown = true)
// public class Employee {
//     @Id
//     @GeneratedValue
//     private Long id;
//
//     private String firstName;
//     private String lastName;
//     private String description;
//
//     @Version
//     @JsonIgnore
//     private Long version;
//
//     public Employee(String firstName, String lastName, String description) {
//         this.firstName = firstName;
//         this.lastName = lastName;
//         this.description = description;
//     }
// }
