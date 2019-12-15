dependencies {
    implementation("org.apache.avro:avro:1.9.1")

    testImplementation("org.slf4j:slf4j-nop")
    testImplementation("org.openjdk.jmh:jmh-core")
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess")
    testImplementation("com.pholser:junit-quickcheck-generators")

}
