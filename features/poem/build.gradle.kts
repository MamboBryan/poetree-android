plugins {
    id(Plugins.library)
    id(Plugins.android)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.hilt)
    id(Plugins.navigation)
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

    implementation(project(BuildModules.Commons.ui))
    implementation(project(BuildModules.Commons.core))
    implementation(project(BuildModules.Commons.data))
    implementation(project(BuildModules.Commons.navigation))

    implementation(project(BuildModules.Libraries.editor))

    implementation(Dependencies.Libraries.core)
    implementation(Dependencies.Libraries.kotlin)
    implementation(Dependencies.Libraries.support)
    implementation(Dependencies.Libraries.appCompat)
    implementation(Dependencies.Libraries.legacySupport)
    implementation(Dependencies.Libraries.materialDesign)
    implementation(Dependencies.Libraries.constraintLayout)

    implementation(Dependencies.Libraries.lifecycleExtensions)
    implementation(Dependencies.Libraries.navigationFragment)
    implementation(Dependencies.Libraries.navigationUi)
    implementation(Dependencies.Libraries.savedState)
    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.lifecycle)
    implementation(Dependencies.Libraries.viewModel)
    implementation(Dependencies.Libraries.liveData)
    implementation(Dependencies.Libraries.fragment)
    implementation(Dependencies.Libraries.hilt)

    implementation(Dependencies.Libraries.circularImage)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.sneaker)
    implementation(Dependencies.Libraries.alert)
    implementation(Dependencies.Libraries.coil)

    kapt(Dependencies.Libraries.hiltCompiler)

}

kapt {
    correctErrorTypes = true
}