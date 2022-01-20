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

    implementation(Dependencies.Libraries.core)
    implementation(Dependencies.Libraries.kotlin)

    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.hilt)

    implementation(Dependencies.Libraries.retrofitConverter)
    implementation(Dependencies.Libraries.retrofit)
    implementation(platform(Dependencies.Libraries.okHttpBOM))
    implementation(Dependencies.Libraries.okHttpLogging)
    implementation(Dependencies.Libraries.okHttp)
    implementation(Dependencies.Libraries.timber)
    implementation(Dependencies.Libraries.gson)

    kapt(Dependencies.Libraries.hiltCompiler)
}

kapt {
    correctErrorTypes = true
}