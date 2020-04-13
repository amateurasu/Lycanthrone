import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.bundling.BootWar

plugins {
    war
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

dependencies {
    val springBoot = "org.springframework.boot:spring-boot-starter"
    val springBootV = "2.2.2.RELEASE"

    // implementation("$springBoot:spring-boot-devtools:$springBootV")
    // implementation("$springBoot:spring-boot-starter-mail:$springBootV")
    // implementation("$springBoot:spring-boot-starter-cache:$springBootV")

    implementation("$springBoot:$springBootV")
    implementation("$springBoot-web:$springBootV")
    // implementation("$springBoot-data-jpa:$springBootV")
    implementation("$springBoot-actuator:$springBootV")
    implementation("$springBoot-data-rest:$springBootV")
    implementation("$springBoot-thymeleaf:$springBootV")
    // annotationProcessor("$springBoot:spring-boot-configuration-processor:$springBootV")
    // implementation("$springBoot:spring-boot-starter-security:$springBootV")

    val jacksonV = "2.10.1"
    implementation("com.jayway.jsonpath:json-path:2.4.0")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonV")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonV")

    // implementation("com.h2database:h2:1.4.199")
    // implementation("com.graphql-java:graphql-java:11.0")
    // implementation("com.graphql-java:graphql-java-spring-boot-starter-webmvc:1.0")

    implementation("com.google.guava:guava:28.1-jre")
    implementation("mysql:mysql-connector-java:8.0.19")

    testImplementation("$springBoot-test:$springBootV")
}

buildscript {
    apply("$rootDir/gradle/config.gradle.kts")

    tasks.register<Exec>("buildReact") {
        workingDir = File("$projectDir/src/main/frontend")
        val npm = project.extra["npm"] as String
        println("project.extra: ${project.extra.properties}")
        commandLine(npm, "run", "build", "--scripts-prepend-node-path=auto")

        doLast {}

    }

    tasks.register<Exec>("webpack") {
        inputs.file("package-lock.json")
        inputs.dir("app")
        // NOTE: Add inputs.file("webpack.config.js") for projects that have it
        outputs.dir("$buildDir/js")

        commandLine("$projectDir/node_modules/.bin/webpack", "app/index.js", "$buildDir/js/bundle.js")
    }

    tasks.register<Copy>("buildFrontend") {
        dependsOn(tasks["buildReact"])
        from("$projectDir/src/main/frontend/build")
        into("$projectDir/src/main/resources/public")
        delete("$projectDir/src/main/frontend/build")
    }

    tasks.withType<JavaCompile> {
        doFirst {
            println("Building front-end components first.")
        }
        dependsOn(tasks["buildFrontend"])
    }
}

tasks.getByName<BootJar>("bootJar") {
    dependsOn(tasks["buildFrontend"])
    mainClassName = "vn.elite.haru.HaruApplication"
}

tasks.getByName<BootWar>("bootWar") {
    mainClassName = "vn.elite.haru.HaruApplication"
}
