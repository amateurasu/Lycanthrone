plugins { war }

dependencies {
    val vertxV = "3.8.1"
    implementation("io.vertx:vertx-core:$vertxV")
    implementation("io.vertx:vertx-config:$vertxV")
    implementation("io.vertx:vertx-web:$vertxV")
    implementation("io.vertx:vertx-redis-client:$vertxV")
    implementation("io.vertx:vertx-auth-jwt:$vertxV")
    implementation("io.vertx:vertx-codegen:$vertxV")

    implementation("org.apache.commons:commons-pool2:2.5.0")
    implementation("org.apache.commons:commons-lang3:3.7")
    implementation("commons-io:commons-io:2.6")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.ajbrown:name-machine:1.0.0")
    implementation("se.emirbuc.randomsentence:random-sentence-generator:0.0.7")

    testImplementation("io.vertx:vertx-unit:$vertxV")
    testImplementation("io.vertx:vertx-web-client:$vertxV")
    testImplementation("io.vertx:vertx-core:$vertxV")

    testImplementation("org.mockito:mockito-core:2.21.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.1.1")
    testImplementation("org.java-websocket:Java-WebSocket:1.3.9")
    testImplementation("com.bigsonata.swarm:locust-swarm:0.1.2")
    testImplementation("org.msgpack:msgpack-core:0.8.13")
    testImplementation("org.zeromq:jeromq:0.4.3")
    testImplementation("com.lmax:disruptor:3.3.7")
    testImplementation("com.google.guava:guava:28.1-jre")
}

tasks.register<GradleBuild>("buildReact") {

}

tasks.register<Copy>("copyReact") {
    from("$projectDir/src/main/react/build")
    into("$projectDir/src/main/webapp")
}
