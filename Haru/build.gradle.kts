dependencies {
    implementation("com.graphql-java:graphql-java:11.0")
    implementation("com.google.guava:guava:26.0-jre")

//    implementation("com.graphql-java:graphql-spring-boot-starter-webmvc:5.0.2")

    val springBootV = "2.1.8.RELEASE"
    implementation("org.springframework.boot:spring-boot-starter:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-cache:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:$springBootV")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootV")

    implementation("org.springframework.boot:spring-boot-devtools:$springBootV")
    implementation("com.h2database:h2:*")

    val springV = "5.1.9.RELEASE"
    implementation("org.springframework:spring-web:$springV")
    implementation("org.springframework.retry:spring-retry:1.2.4.RELEASE")

    val jacksonV = "2.9.9"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonV")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonV")

//    implementation("org.functionaljava:functionaljava:4.8.1")
    implementation("com.h2database:h2:1.4.199")
    implementation("com.github.tomakehurst:wiremock:2.24.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootV")
}
