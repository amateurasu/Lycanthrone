plugins {
    scala
}

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/releases/")
    }
}

val scalaVersion = "2.12.8"
val scalaV = "2.12"
dependencies {
    implementation("org.scala-lang:scala-library:$scalaVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9")
    implementation("org.scala-lang.modules:scala-parser-combinators_$scalaV:1.1.2")

    testImplementation("org.scalatest:scalatest_$scalaV:3.0.5")
    testImplementation("org.scalacheck:scalacheck_$scalaV:1.14.0")
}
