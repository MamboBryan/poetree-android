apply {
    from("$rootDir/base-feature.gradle.kts")
}

dependencies {

    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.cascade)
    implementation(Dependencies.Libraries.paging)

}