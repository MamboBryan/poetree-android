apply {
    from("$rootDir/base-library.gradle.kts")
}

plugins {
    id(Plugins.googleServices)
    id(Plugins.crashlytics)
}

dependencies {

    implementation(project(Modules.Commons.ui))
    implementation(project(Modules.Commons.data))
    implementation(project(Modules.Commons.local))
    implementation(project(Modules.Commons.remote))

    androidCommon()

    lifecycleComponents()

    firebase()

    okHttp()

    hiltWork()

    hilt()

    implementation(Dependencies.Libraries.recyclerviewSelection)

    implementation(Dependencies.Libraries.playCoroutines)
    implementation(Dependencies.Libraries.workManager)
    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.datastore)
    implementation(Dependencies.Libraries.paging)

    implementation(Dependencies.Libraries.retrofitConverter)
    implementation(Dependencies.Libraries.okHttpLogging)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.retrofit)
    implementation(Dependencies.Libraries.cascade)

}