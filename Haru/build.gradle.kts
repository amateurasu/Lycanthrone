import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.bundling.BootWar

plugins {
    war
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

dependencies {
    val spring = "org.springframework"
    val springBoot = "$spring.boot"

    val springV = "5.2.2.RELEASE"
    val springBootV = "2.2.2.RELEASE"
    val springRetryV = "1.2.5.RELEASE"
    val springSecurityV = "5.2.1.RELEASE"

    // implementation("com.unboundid:unboundid-ldapsdk:4.0.14")
    // implementation("$spring:spring-tx:$springV")
    // implementation("$spring:spring-web:$springV")

    // implementation("$spring.retry:spring-retry:$springRetryV")
    // implementation("$spring.ldap:spring-ldap-core:2.3.2.RELEASE")
    // implementation("$spring.security:spring-security-jwt:1.1.0.RELEASE")
    // implementation("$spring.security:spring-security-ldap:$springSecurityV")
    // implementation("$spring.security:spring-security-core:$springSecurityV")
    // implementation("$spring.security.oauth:spring-security-oauth2:2.4.0.RELEASE")
    // implementation("$springBoot:spring-boot-devtools:$springBootV")
    // implementation("$springBoot:spring-boot-starter-mail:$springBootV")
    // implementation("$springBoot:spring-boot-starter-cache:$springBootV")
    // implementation("$springBoot:spring-boot-starter-data-jpa:$springBootV")

    implementation("$springBoot:spring-boot-starter:$springBootV")
    implementation("$springBoot:spring-boot-starter-web:$springBootV")
    implementation("$springBoot:spring-boot-starter-actuator:$springBootV")
    implementation("$springBoot:spring-boot-starter-data-rest:$springBootV")
    implementation("$springBoot:spring-boot-starter-thymeleaf:$springBootV")
    // annotationProcessor("$springBoot:spring-boot-configuration-processor:$springBootV")
    // implementation("$springBoot:spring-boot-starter-security:$springBootV")

    val jacksonV = "2.10.1"
    implementation("com.jayway.jsonpath:json-path:2.4.0")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonV")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonV")

    // implementation("com.h2database:h2:1.4.199")
    // implementation("com.google.guava:guava:26.0-jre")
    // implementation("com.graphql-java:graphql-java:11.0")
    // implementation("com.graphql-java:graphql-java-spring-boot-starter-webmvc:1.0")
    // implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity4:3.0.4.RELEASE")

    implementation("com.google.guava:guava:28.1-jre")
    // implementation("com.maxmind.geoip2:geoip2:2.13.0")
    implementation("mysql:mysql-connector-java:8.0.18")
    // implementation("com.github.ua-parser:uap-java:1.4.3")
    // implementation("javax.interceptor:javax.interceptor-api:1.2.2")
    // implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:2.4.1")
    // implementation("net.logstash.logback:logstash-logback-encoder:6.3")

    testImplementation("$springBoot:spring-boot-starter-test:$springBootV")
    // testImplementation("$spring.security:spring-security-test:$springSecurityV")
}

buildscript {
    tasks.register<Exec>("buildReact") {
        workingDir = File("$projectDir/src/main/frontend")
        val npm = project.extra["npm"]
        println(npm)
        commandLine(npm, "run", "build", "--scripts-prepend-node-path=auto")
    }

    tasks.register<Copy>("buildFrontend") {
        dependsOn(tasks["buildReact"])
        from("$projectDir/src/main/react/build")
        into("$projectDir/src/main/webapp")
    }
}

tasks.getByName<BootJar>("bootJar") {
    // dependOn(tasks["buildFrontend"])
    mainClassName = "vn.elite.haru.HaruApplication"
}

tasks.getByName<BootWar>("bootWar") {
    mainClassName = "vn.elite.haru.HaruApplication"
}
