apply {
    from("$rootDir/base-feature.gradle.kts")
}

dependencies {

    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.paging)

    implementation(Dependencies.Libraries.circularImage)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.likeButton)
    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.coil)

}