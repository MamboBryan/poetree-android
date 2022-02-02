
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

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

val props = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "github.properties")))
}
val githubUserId: String? = props.getProperty("gpr.user")
val githubApiKey:String? = props.getProperty("gpr.key")

repositories {
    maven(url = uri("https://maven.pkg.github.com/Cuberto/liquid-swipe-android")) {
        name = "GitHubPackages"
        credentials {
            username = githubUserId ?: System.getenv("GPR_USER")
            password = githubApiKey ?: System.getenv("GPR_API_KEY")
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

    implementation(Dependencies.Libraries.viewModel)
    implementation(Dependencies.Libraries.liveData)
    implementation(Dependencies.Libraries.lifecycle)
    implementation(Dependencies.Libraries.lifecycleExtensions)
    implementation(Dependencies.Libraries.savedState)
    implementation(Dependencies.Libraries.fragment)
    implementation(Dependencies.Libraries.navigationFragment)
    implementation(Dependencies.Libraries.navigationUi)
    implementation(Dependencies.Libraries.coroutines)
    implementation(Dependencies.Libraries.hilt)

    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.circularImage)
    implementation(Dependencies.Libraries.liquidSwipe)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.coil)
    implementation(Dependencies.Libraries.switch)

    kapt(Dependencies.Libraries.hiltCompiler)

}

kapt {
    correctErrorTypes = true
}