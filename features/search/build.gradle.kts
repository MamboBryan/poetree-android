apply {
    from("$rootDir/base-feature.gradle.kts")
}

dependencies {

    implementation(project(Modules.Libraries.searchbar))

    implementation(Dependencies.Libraries.paging)
    implementation(Dependencies.Libraries.prettyTime)

}