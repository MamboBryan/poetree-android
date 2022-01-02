import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id(Plugins.application)
    id(Plugins.android)
    id(Plugins.kapt)
    id(Plugins.parcelize)
    id(Plugins.hilt)
    id(Plugins.navigation)
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

val githubUser = gradleLocalProperties(rootDir).getProperty("gpr.user")
val githubKey = gradleLocalProperties(rootDir).getProperty("gpr.key")

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Cuberto/liquid-swipe-android")
        credentials {
            username = githubUser ?: System.getenv("GPR_USER")
            password = githubKey ?: System.getenv("GPR_API_KEY")
        }
    }
}


dependencies {

    implementation(project(BuildModules.Features.home))
    implementation(project(BuildModules.Commons.core))

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
    implementation(Dependencies.Libraries.gson)
    implementation(Dependencies.Libraries.coil)
    implementation(Dependencies.Libraries.wysiwyg)
    implementation(Dependencies.Libraries.nativeEditor)
    implementation(Dependencies.Libraries.richEditor)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.switch)
    implementation(Dependencies.Libraries.circularImage)
    implementation(Dependencies.Libraries.liquidSwipe)

    testImplementation(Dependencies.Libraries.junit)

    kapt(Dependencies.Libraries.hiltKapt)
    kapt(Dependencies.Libraries.roomKapt)

}

kapt {
    correctErrorTypes = true
}