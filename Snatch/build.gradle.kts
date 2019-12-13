dependencies {
    implementation(project(":Core"))
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("javax.activation:activation:1.1.1")
    implementation("org.jsoup:jsoup:1.12.1")

    implementation("com.google.code.gson:gson:2.8.5")
    implementation("commons-codec:commons-codec:1.11")
    implementation("commons-io:commons-io:2.6")

    implementation("org.apache.httpcomponents:httpclient:4.5.4")
    implementation("org.apache.httpcomponents:fluent-hc:4.5.9")
    implementation("org.apache.httpcomponents:httpclient-cache:4.5.4")
    implementation("org.apache.httpcomponents:httpclient-win:4.5.4")
    implementation("org.apache.httpcomponents:httpcore:4.4.11")
    implementation("org.apache.httpcomponents:httpmime:4.5.9")

    implementation("net.java.dev.jna:jna:5.4.0")
    implementation("net.java.dev.jna:jna-platform:5.4.0")

    val springV = "5.2.2.RELEASE"
    val springBootV = "2.2.2.RELEASE"

    implementation("cglib:cglib-nodep:3.1")
    implementation("commons-io:commons-io:2.4")
    implementation("com.google.guava:guava:18.0")
    implementation("commons-codec:commons-codec:1.10")
    implementation("org.codehaus.groovy:groovy-all:2.5.8")

    implementation("org.springframework:spring-core:$springV")
    implementation("org.springframework:spring-beans:$springV")
    implementation("org.springframework:spring-context:$springV")
    implementation("org.springframework:spring-expression:$springV")
    implementation("org.springframework:spring-context-support:$springV")
    implementation("org.springframework:spring-webmvc:$springV")
    implementation("org.springframework:spring-web:$springV")
    implementation("org.springframework:spring-aop:$springV")

    implementation("org.springframework.boot:spring-boot-starter-web:$springBootV")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootV")

    testImplementation("org.springframework:spring-test:$springV")
    testImplementation("org.spockframework:spock-spring:1.0-groovy-2.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootV")
    testImplementation("org.spockframework:spock-core:1.0-groovy-2.4")
}
