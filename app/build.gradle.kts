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

dependencies {

    implementation(project(BuildModules.Commons.ui))
    implementation(project(BuildModules.Commons.core))
    implementation(project(BuildModules.Commons.data))
    implementation(project(BuildModules.Commons.remote))
    implementation(project(BuildModules.Commons.local))
    implementation(project(BuildModules.Commons.navigation))

    implementation(project(BuildModules.Features.auth))
    implementation(project(BuildModules.Features.home))
    implementation(project(BuildModules.Features.poem))
    implementation(project(BuildModules.Features.search))
    implementation(project(BuildModules.Features.artist))
    implementation(project(BuildModules.Features.explore))
    implementation(project(BuildModules.Features.account))
    implementation(project(BuildModules.Features.library))
    implementation(project(BuildModules.Features.compose))
    implementation(project(BuildModules.Features.profile))
    implementation(project(BuildModules.Features.comments))
    implementation(project(BuildModules.Features.bookmarks))
    implementation(project(BuildModules.Features.onboarding))
    implementation(project(BuildModules.Features.updatePassword))
    implementation(project(BuildModules.Features.landing))
    implementation(project(BuildModules.Features.loading))
    implementation(project(BuildModules.Features.setup))
    implementation(project(BuildModules.Features.publish))

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
    implementation(Dependencies.Libraries.paging)
    implementation(Dependencies.Libraries.gson)
    implementation(Dependencies.Libraries.coil)
    implementation(Dependencies.Libraries.wysiwyg)
    implementation(Dependencies.Libraries.nativeEditor)
    implementation(Dependencies.Libraries.richEditor)
    implementation(Dependencies.Libraries.prettyTime)
    implementation(Dependencies.Libraries.delegate)
    implementation(Dependencies.Libraries.hiltWork)
    implementation(Dependencies.Libraries.workManager)
    implementation(Dependencies.Libraries.switch)
    implementation(Dependencies.Libraries.circularImage)

    implementation(Dependencies.Libraries.retrofitConverter)
    implementation(Dependencies.Libraries.retrofit)
    implementation(platform(Dependencies.Libraries.okHttpBOM))
    implementation(Dependencies.Libraries.okHttpLogging)
    implementation(Dependencies.Libraries.okHttp)
    implementation(Dependencies.Libraries.androidXStartup)

    testImplementation(Dependencies.Libraries.junit)

    kapt(Dependencies.Libraries.hiltCompiler)
    kapt(Dependencies.Libraries.roomCompiler)
    kapt(Dependencies.Libraries.hiltWorkCompiler)

}

kapt {
    correctErrorTypes = true
}