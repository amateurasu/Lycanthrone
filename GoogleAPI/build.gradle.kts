dependencies {
    implementation("com.google.apis:google-api-services-drive:v3-rev165-1.25.0")

    val googleApiV = "1.30.1"
    implementation("com.google.api-client:google-api-client:$googleApiV")
    implementation("com.google.oauth-client:google-oauth-client-jetty:$googleApiV")

}
