plugins {
    id(Plugins.library)
    id(Plugins.android)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.hilt)
}

android {
    compileSdkVersion(Android.compileSdkVersion)

    defaultConfig {
        minSdkVersion(Android.minSdkVersion)
        targetSdkVersion(Android.targetSdkVersion)
        versionCode = Android.versionCode
        versionName = Android.versionName

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

    implementation(project(Modules.Commons.data))

    implementation(Dependencies.Libraries.core)
    implementation(Dependencies.Libraries.kotlin)
    implementation(Dependencies.Libraries.datastore)
    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.room)
    implementation(Dependencies.Libraries.roomRuntime)
    implementation(Dependencies.Libraries.gson)
    implementation(Dependencies.Libraries.hilt)
    implementation(Dependencies.Libraries.paging)

    kapt(Dependencies.Libraries.roomCompiler)
    kapt(Dependencies.Libraries.hiltCompiler)

    annotationProcessor(Dependencies.Libraries.roomCompiler)

}

kapt {
    correctErrorTypes = true
}