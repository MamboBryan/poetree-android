apply {
    from("$rootDir/base.gradle.kts")
    from("$rootDir/base-android.gradle")
}

apply(plugin = Plugins.library)
apply(plugin = Plugins.parcelize)
apply(plugin = Plugins.serialize)

dependencies {

    androidCommon()

    lifecycleComponents()

    test()

}