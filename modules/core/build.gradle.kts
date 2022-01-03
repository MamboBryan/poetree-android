plugins {
    id(Plugins.library)
    id(Plugins.android)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.hilt)
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

    implementation(project(BuildModules.Commons.data))
    implementation(project(BuildModules.Commons.local))
    implementation(project(BuildModules.Commons.remote))

    implementation(Dependencies.Libraries.core)
    implementation(Dependencies.Libraries.kotlin)
    implementation(Dependencies.Libraries.appCompat)
    implementation(Dependencies.Libraries.materialDesign)
    implementation(Dependencies.Libraries.constraintLayout)
    implementation(Dependencies.Libraries.legacySupport)

    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.hilt)

    kapt(Dependencies.Libraries.hiltCompiler)
}

kapt {
    correctErrorTypes = true
}