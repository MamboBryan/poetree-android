plugins {
    id(Plugins.library)
    id(Plugins.android)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.hilt)
    id(Plugins.googleServices)
    id(Plugins.crashlytics)
}

android {
    compileSdkVersion(Configs.compileSdkVersion)

    defaultConfig {
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

    buildFeatures{
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
    implementation(project(Modules.Commons.data))
    implementation(project(Modules.Commons.local))
    implementation(project(Modules.Commons.remote))

    implementation(platform(Dependencies.BOM.okHttp))
    implementation(platform(Dependencies.BOM.firebase))

    implementation(Dependencies.Libraries.core)
    implementation(Dependencies.Libraries.kotlin)
    implementation(Dependencies.Libraries.appCompat)
    implementation(Dependencies.Libraries.legacySupport)
    implementation(Dependencies.Libraries.materialDesign)
    implementation(Dependencies.Libraries.constraintLayout)
    implementation(Dependencies.Libraries.recyclerviewSelection)

    implementation(Dependencies.Libraries.playCoroutines)
    implementation(Dependencies.Libraries.workManager)
    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.datastore)
    implementation(Dependencies.Libraries.hiltWork)
    implementation(Dependencies.Libraries.paging)
    implementation(Dependencies.Libraries.hilt)

    implementation(Dependencies.Firebase.analytics)
    implementation(Dependencies.Firebase.crashlytics)
    implementation(Dependencies.Firebase.performance)
    implementation(Dependencies.Firebase.messaging)
    implementation(Dependencies.Firebase.storage)

    implementation(Dependencies.Libraries.retrofitConverter)
    implementation(Dependencies.Libraries.okHttpLogging)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.retrofit)
    implementation(Dependencies.Libraries.cascade)
    implementation(Dependencies.Libraries.okHttp)

    kapt(Dependencies.Libraries.hiltCompiler)
    kapt(Dependencies.Libraries.hiltWorkCompiler)
}

kapt {
    correctErrorTypes = true
}