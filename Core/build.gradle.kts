dependencies {
    implementation("org.apache.avro:avro:1.9.1")
    implementation("org.ow2.asm:asm:7.2")

    val jmhV = "1.22"
    testImplementation("org.slf4j:slf4j-nop:1.7.29")
    testImplementation("org.openjdk.jmh:jmh-core:$jmhV")
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:$jmhV")
    testImplementation("com.pholser:junit-quickcheck-generators:0.9")

}
