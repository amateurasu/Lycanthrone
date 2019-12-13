import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.2.2.RELEASE"
}

dependencies {
    implementation("com.graphql-java:graphql-java:11.0")
    implementation("com.google.guava:guava:26.0-jre")

//    implementation("com.graphql-java:graphql-spring-boot-starter-webmvc:5.0.2")

    val springV = "5.2.2.RELEASE"
    val springBootV = "2.2.2.RELEASE"
    val springRetryV = "1.2.5.RELEASE"

    implementation("org.springframework.boot:spring-boot-starter:$springBootV")
    implementation("org.springframework.boot:spring-boot-devtools:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-cache:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:$springBootV")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootV")

    implementation("org.springframework:spring-web:$springV")
    implementation("org.springframework.retry:spring-retry:$springRetryV")

    val jacksonV = "2.9.9"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonV")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonV")

    implementation("com.h2database:h2:1.4.199")
    implementation("com.github.tomakehurst:wiremock:2.24.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootV")
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "vn.elite.haru.book.Application"
}
