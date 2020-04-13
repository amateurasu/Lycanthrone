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

    implementation("$springBoot:spring-boot-starter:$springBootV")
    implementation("$springBoot:spring-boot-starter-web:$springBootV")
    implementation("$springBoot:spring-boot-starter-actuator:$springBootV")
    implementation("$springBoot:spring-boot-starter-data-rest:$springBootV")
    implementation("$springBoot:spring-boot-starter-thymeleaf:$springBootV")

    val jacksonV = "2.10.1"
    implementation("com.jayway.jsonpath:json-path:2.4.0")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonV")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonV")

    implementation("com.google.guava:guava:28.1-jre")
    implementation("mysql:mysql-connector-java:8.0.18")

    testImplementation("$springBoot:spring-boot-starter-test:$springBootV")
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
