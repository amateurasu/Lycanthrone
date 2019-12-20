import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

dependencies {
    val springV = "5.2.2.RELEASE"
    val springBootV = "2.2.2.RELEASE"
    val springRetryV = "1.2.5.RELEASE"
    val springSecurityV = "5.2.1.RELEASE"

    implementation("org.springframework.boot:spring-boot-starter:$springBootV")
    implementation("org.springframework.boot:spring-boot-devtools:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-mail:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-cache:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-security:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:$springBootV")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootV")

    // implementation("com.unboundid:unboundid-ldapsdk:4.0.14")
    implementation("org.springframework:spring-tx:$springV")
    implementation("org.springframework:spring-web:$springV")
    implementation("org.springframework.retry:spring-retry:$springRetryV")
    // implementation("org.springframework.ldap:spring-ldap-core:2.3.2.RELEASE")
    // implementation("org.springframework.security:spring-security-ldap:$springSecurityV")
    implementation("org.springframework.security:spring-security-core:$springSecurityV")

    val jacksonV = "2.10.1"
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonV")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonV")

    implementation("com.h2database:h2:1.4.199")
    implementation("com.google.guava:guava:26.0-jre")
    implementation("com.graphql-java:graphql-java:11.0")
    implementation("com.graphql-java:graphql-java-spring-boot-starter-webmvc:1.0")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity4:3.0.4.RELEASE")
    implementation("com.github.tomakehurst:wiremock:2.24.1")

    runtime("javax.servlet:jstl:1.2")
    compileOnly("org.jboss.aerogear:aerogear-otp-java:1.0.0")

    implementation("org.passay:passay:1.0")
    implementation("org.apache.tomcat:el-api:6.0.53")
    implementation("com.google.guava:guava:28.1-jre")
    implementation("com.maxmind.geoip2:geoip2:2.13.0")
    implementation("mysql:mysql-connector-java:8.0.18")
    implementation("com.github.ua-parser:uap-java:1.4.3")
    implementation("javax.interceptor:javax.interceptor-api:1.2.2")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:2.4.1")
    implementation("net.logstash.logback:logstash-logback-encoder:6.3")

    testImplementation("io.rest-assured:rest-assured:4.1.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootV")
    testImplementation("org.springframework.security:spring-security-test:$springSecurityV")
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "vn.elite.haru.HaruApplication"
}
