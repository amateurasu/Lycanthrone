plugins { war }

apply {
    from("$rootDir/gradle/config.gradle.kts")
}

dependencies {
    val vertxV = "3.8.1"
    implementation("io.vertx:vertx-web:$vertxV")
    implementation("io.vertx:vertx-core:$vertxV")
    implementation("io.vertx:vertx-config:$vertxV")
    implementation("io.vertx:vertx-redis-client:$vertxV")
    implementation("io.vertx:vertx-auth-jwt:$vertxV")
    implementation("io.vertx:vertx-codegen:$vertxV")

    implementation("org.mindrot:jbcrypt:0.4")
    implementation("commons-io:commons-io:2.6")
    implementation("org.ajbrown:name-machine:1.0.0")
    implementation("org.apache.commons:commons-lang3:3.7")
    implementation("org.apache.commons:commons-pool2:2.5.0")
    implementation("se.emirbuc.randomsentence:random-sentence-generator:0.0.7")

    implementation("org.elasticsearch.client:transport:7.5.0")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.12.1")

    testImplementation("io.vertx:vertx-unit:$vertxV")
    testImplementation("io.vertx:vertx-web-client:$vertxV")

    testImplementation("org.zeromq:jeromq:0.4.3")
    testImplementation("com.lmax:disruptor:3.3.7")
    testImplementation("org.msgpack:msgpack-core:0.8.13")
    testImplementation("com.google.guava:guava:28.1-jre")
    testImplementation("org.mockito:mockito-core:2.21.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.1.1")
    testImplementation("com.bigsonata.swarm:locust-swarm:0.1.2")
    testImplementation("org.java-websocket:Java-WebSocket:1.3.9")
}

buildscript {

    tasks.register<Exec>("buildReact") {
        workingDir = File("$projectDir/src/main/js")

        commandLine(project.extra["npm"], "run", "build", "--scripts-prepend-node-path=auto")
    }

    tasks.register<Copy>("copyReact") {
        this.dependsOn(tasks["buildReact"])
        from("$projectDir/src/main/react/build")
        into("$projectDir/src/main/webapp")
    }
}
