// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

buildscript {

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Dependencies.BuildPlugins.androidGradle)
        classpath(Dependencies.BuildPlugins.kotlinGradle)
        classpath(Dependencies.BuildPlugins.hiltGradle)
        classpath(Dependencies.BuildPlugins.crashlyticsGradle)
        classpath(Dependencies.BuildPlugins.googleServicesGradle)
        classpath(Dependencies.BuildPlugins.navigationSafeArgsGradle)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

val props = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "github.properties")))
}
val githubUserId: String? = props.getProperty("gpr.user")
val githubApiKey: String? = props.getProperty("gpr.key")


allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven(url = uri("https://maven.pkg.github.com/Cuberto/liquid-swipe-android")) {
            name = "GitHubPackages"
            credentials {
                username = githubUserId ?: System.getenv("GPR_USER")
                password = githubApiKey ?: System.getenv("GPR_API_KEY")
            }
        }
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}