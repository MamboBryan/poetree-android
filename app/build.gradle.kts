plugins {
    id(Plugins.application)
    id(Plugins.android)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.hilt)
    id(Plugins.navigation)
    id(Plugins.googleServices)
    id(Plugins.crashlytics)
}

android {
    compileSdkVersion(Configs.compileSdkVersion)
    buildToolsVersion(Configs.buildToolsVersion)

    defaultConfig {
        applicationId = "com.mambo.poetree"
        minSdkVersion(Configs.minSdkVersion)
        targetSdkVersion(Configs.targetSdkVersion)
        versionCode = Configs.versionCode
        versionName = Configs.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

dependencies {

    implementation(project(Modules.Commons.ui))
    implementation(project(Modules.Commons.core))
    implementation(project(Modules.Commons.data))
    implementation(project(Modules.Commons.remote))
    implementation(project(Modules.Commons.local))
    implementation(project(Modules.Commons.navigation))

    implementation(project(Modules.Features.auth))
    implementation(project(Modules.Features.home))
    implementation(project(Modules.Features.poem))
    implementation(project(Modules.Features.search))
    implementation(project(Modules.Features.artist))
    implementation(project(Modules.Features.explore))
    implementation(project(Modules.Features.account))
    implementation(project(Modules.Features.library))
    implementation(project(Modules.Features.compose))
    implementation(project(Modules.Features.profile))
    implementation(project(Modules.Features.comments))
    implementation(project(Modules.Features.bookmarks))
    implementation(project(Modules.Features.onboarding))
    implementation(project(Modules.Features.updatePassword))
    implementation(project(Modules.Features.landing))
    implementation(project(Modules.Features.loading))
    implementation(project(Modules.Features.setup))
    implementation(project(Modules.Features.publish))
    implementation(project(Modules.Features.settings))

    implementation(platform(Dependencies.BOM.okHttp))
    implementation(platform(Dependencies.BOM.firebase))

    // firebase
    implementation(Dependencies.Firebase.analytics)
    implementation(Dependencies.Firebase.crashlytics)
    implementation(Dependencies.Firebase.performance)
    implementation(Dependencies.Firebase.messaging)

    implementation(Dependencies.Libraries.core)
    implementation(Dependencies.Libraries.kotlin)
    implementation(Dependencies.Libraries.appCompat)
    implementation(Dependencies.Libraries.materialDesign)
    implementation(Dependencies.Libraries.constraintLayout)
    implementation(Dependencies.Libraries.fragment)
    implementation(Dependencies.Libraries.legacySupport)
    implementation(Dependencies.Libraries.datastore)
    implementation(Dependencies.Libraries.hilt)
    implementation(Dependencies.Libraries.playServicesAuth)
    implementation(Dependencies.Libraries.navigationFragment)
    implementation(Dependencies.Libraries.navigationUi)
    implementation(Dependencies.Libraries.viewModel)
    implementation(Dependencies.Libraries.liveData)
    implementation(Dependencies.Libraries.lifecycle)
    implementation(Dependencies.Libraries.savedState)
    implementation(Dependencies.Libraries.lifecycleExtensions)
    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.room)
    implementation(Dependencies.Libraries.roomRuntime)
    implementation(Dependencies.Libraries.paging)
    implementation(Dependencies.Libraries.gson)
    implementation(Dependencies.Libraries.coil)
    implementation(Dependencies.Libraries.wysiwyg)
    implementation(Dependencies.Libraries.nativeEditor)
    implementation(Dependencies.Libraries.richEditor)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.hiltWork)
    implementation(Dependencies.Libraries.workManager)
    implementation(Dependencies.Libraries.switch)
    implementation(Dependencies.Libraries.circularImage)

    implementation(Dependencies.Libraries.retrofitConverter)
    implementation(Dependencies.Libraries.retrofit)
    implementation(Dependencies.Libraries.okHttpLogging)
    implementation(Dependencies.Libraries.okHttp)
    implementation(Dependencies.Libraries.androidXStartup)
    implementation ("androidx.core:core-splashscreen:1.0.0-beta01")
    
    debugImplementation(Dependencies.Libraries.chuckDebug)
    releaseImplementation(Dependencies.Libraries.chuckRelease)

    testImplementation(Dependencies.Libraries.junit)

    kapt(Dependencies.Libraries.hiltCompiler)
    kapt(Dependencies.Libraries.roomCompiler)
    kapt(Dependencies.Libraries.hiltWorkCompiler)

}

kapt {
    correctErrorTypes = true
}