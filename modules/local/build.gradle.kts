apply {
    from("$rootDir/base-library.gradle.kts")
}

dependencies {

    implementation(project(Modules.Commons.data))

    hilt()
    room()

    implementation(Dependencies.Libraries.gson)
    implementation(Dependencies.Libraries.paging)
    implementation(Dependencies.Libraries.datastore)
    implementation(Dependencies.Libraries.coroutines)

}