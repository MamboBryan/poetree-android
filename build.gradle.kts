// Top-level build file where you can add configuration options common to all sub-projects/modules.
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

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.pkg.github.com/Cuberto/liquid-swipe-android") }
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}