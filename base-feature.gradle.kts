apply {
    from("$rootDir/base.gradle.kts")
    from("$rootDir/base-android.gradle")
}

apply(plugin = Plugins.android)
apply(plugin = Plugins.navigation)
apply(plugin = Plugins.hilt)
apply(plugin = Plugins.googleServices)

dependencies {

    commonModules()

    androidCommon()

    lifecycleComponents()

    test()

    navigation()

    hilt()

    alert()

}