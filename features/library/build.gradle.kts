apply {
    from("$rootDir/base-feature.gradle.kts")
}

dependencies {

    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.paging)

    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.circularImage)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.coil)

    implementation("androidx.palette:palette:1.0.0")

}