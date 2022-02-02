plugins {
    id(Plugins.library)
    id(Plugins.android)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.hilt)
    id(Plugins.navigation)
    id("org.jetbrains.kotlin.android")
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
    implementation(project(Modules.Commons.core))
    implementation(project(Modules.Commons.data))
    implementation(project(Modules.Commons.navigation))

    implementation(Dependencies.Libraries.core)
    implementation(Dependencies.Libraries.kotlin)
    implementation(Dependencies.Libraries.appCompat)
    implementation(Dependencies.Libraries.materialDesign)
    implementation(Dependencies.Libraries.constraintLayout)
    implementation(Dependencies.Libraries.legacySupport)

    implementation(Dependencies.Libraries.lifecycleExtensions)
    implementation(Dependencies.Libraries.navigationFragment)
    implementation(Dependencies.Libraries.navigationUi)
    implementation(Dependencies.Libraries.savedState)
    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.viewModel)
    implementation(Dependencies.Libraries.lifecycle)
    implementation(Dependencies.Libraries.liveData)
    implementation(Dependencies.Libraries.fragment)
    implementation(Dependencies.Libraries.hilt)

    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.timber)
    implementation(Dependencies.Libraries.alert)

    kapt(Dependencies.Libraries.hiltCompiler)

}

kapt {
    correctErrorTypes = true
}