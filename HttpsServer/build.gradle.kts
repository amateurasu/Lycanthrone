plugins { war }

val tomcatV = "9.0.24"
dependencies {
    implementation(project(":Core"))
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.apache.tomcat.embed:tomcat-embed-logging-juli:9.0.0.M6")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:$tomcatV")
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper:$tomcatV")
    implementation("org.apache.tomcat:tomcat-jasper:$tomcatV")
    implementation("org.apache.tomcat:tomcat-jasper-el:$tomcatV")
    implementation("org.apache.tomcat:tomcat-jsp-api:$tomcatV")
    implementation("org.apache.tomcat:tomcat-jdbc:$tomcatV")

    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("commons-fileupload:commons-fileupload:1.4")
    implementation("commons-io:commons-io:2.6")
    implementation("org.xerial:sqlite-jdbc:3.28.0")
}
