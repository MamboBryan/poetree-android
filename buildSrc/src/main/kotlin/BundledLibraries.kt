import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * ANDROID COMMON
 */
object AndroidCommon {
    val appCompat by lazy { "androidx.appcompat:appcompat:1.3.0" }
    val materialDesign by lazy { "com.google.android.material:material:1.3.0" }
    val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:1.1.3" }
    val fragment by lazy { "androidx.fragment:fragment-ktx:1.5.2" }
    val core by lazy { "androidx.core:core-ktx:1.8.0" }
    val legacy by lazy {"androidx.legacy:legacy-support-v4:1.0.0"}
}

fun DependencyHandler.androidCommon() {
    implementation(AndroidCommon.core)
    implementation(AndroidCommon.legacy)
    implementation(AndroidCommon.fragment)
    implementation(AndroidCommon.appCompat)
    implementation(AndroidCommon.materialDesign)
    implementation(AndroidCommon.constraintLayout)
}

/**
 * NAVIGATION
 */
object JetpackNavigation {
    val navigationCommon by lazy { "androidx.navigation:navigation-common-ktx:2.3.5" }
    val navigationFragment by lazy { "androidx.navigation:navigation-fragment-ktx:2.3.5" }
    val navigationUi by lazy { "androidx.navigation:navigation-ui-ktx:2.3.5" }
}

fun DependencyHandler.navigation() {
    implementation(JetpackNavigation.navigationFragment)
    implementation(JetpackNavigation.navigationCommon)
    implementation(JetpackNavigation.navigationUi)
}

/**
 * LIFECYCLE
 */

object JetpackLifecycle {
    val viewModel by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0" }
    val liveData by lazy { "androidx.lifecycle:lifecycle-livedata-ktx:2.5.0" }
    val savedState by lazy { "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.5.0" }
    val lifecycle by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:2.5.0" }
    val lifecycleExtensions by lazy { "androidx.lifecycle:lifecycle-extensions:2.2.0" }
}

fun DependencyHandler.lifecycleComponents() {
    implementation(JetpackLifecycle.viewModel)
    implementation(JetpackLifecycle.liveData)
    implementation(JetpackLifecycle.savedState)
    implementation(JetpackLifecycle.lifecycle)
    implementation(JetpackLifecycle.lifecycleExtensions)
}

/**
 * KOTLIN COMMON
 */

object Kotlin {
    val stdlib by lazy { "org.jetbrains.kotlin:kotlin-stdlib:1.5.10" }
}

fun DependencyHandler.kotlinCommon() {
    implementation(Kotlin.stdlib)
}

/**
 * KOTLINX
 */
object Kotlinx {
    val serialization by lazy { "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2" }
}

fun DependencyHandler.kotlinx() {
    implementation(Kotlinx.serialization)
}

/**
 * KTOR
 */
object Ktor {

    private const val VERSION = "2.1.0"

    val core by lazy { "io.ktor:ktor-client-core:$VERSION" }
    val serialization by lazy { "io.ktor:ktor-serialization-kotlinx-json:$VERSION" }
    val logging by lazy { "io.ktor:ktor-client-logging-jvm:$VERSION" }
    val contentNegotiation by lazy { "io.ktor:ktor-client-content-negotiation:$VERSION" }
    val okHttp by lazy { "io.ktor:ktor-client-okhttp:$VERSION" }

}

fun DependencyHandler.ktor() {
    implementation(Ktor.core)
    implementation(Ktor.okHttp)
    implementation(Ktor.logging)
    implementation(Ktor.serialization)
    implementation(Ktor.contentNegotiation)
}

/**
 * HILT
 */
object Hilt {
    val hilt by lazy { "com.google.dagger:hilt-android:${Versions.hilt}" }
    val hiltKapt by lazy { "com.google.dagger:hilt-android-compiler:${Versions.hilt}" }
    val hiltWork by lazy { "androidx.hilt:hilt-work:${Versions.hiltWork}" }
    val hiltWorkKapt by lazy { "androidx.hilt:hilt-compiler:${Versions.hiltWork}" }
}

fun DependencyHandler.hilt() {
    implementation(Hilt.hilt)
    kapt(Hilt.hiltKapt)
}

fun DependencyHandler.hiltWork() {
    implementation(Hilt.hiltWork)
    kapt(Hilt.hiltWorkKapt)
}

/**
 * ROOM
 */
object Room {
    val room by lazy { "androidx.room:room-ktx:2.3.0-beta03" }
    val roomRuntime by lazy { "androidx.room:room-runtime:2.3.0-beta03" }
    val roomCompiler by lazy { "androidx.room:room-compiler:2.3.0-beta03" }
}

fun DependencyHandler.room(){
    implementation(Room.room)
    implementation(Room.roomRuntime)
    kapt(Room.roomCompiler)
}

/**
 * OkHttp
 */
object OkHttp {

    private const val VERSION = "4.7.2"

    val bom by lazy { "com.squareup.okhttp3:okhttp-bom:$VERSION" }
    val okHttp by lazy { "com.squareup.okhttp3:okhttp" }
    val logging by lazy { "com.squareup.okhttp3:logging-interceptor" }

}

fun DependencyHandler.okHttp() {
    implementation(platform(OkHttp.bom))
    implementation(OkHttp.okHttp)
    implementation(OkHttp.logging)
}

/**
 * FIREBASE
 */
object Firebase {
    val bom by lazy { "com.google.firebase:firebase-bom:29.0.4" }
    val analytics by lazy { "com.google.firebase:firebase-analytics-ktx" }
    val messaging by lazy { "com.google.firebase:firebase-messaging-ktx" }
    val storage by lazy { "com.google.firebase:firebase-storage-ktx" }
    val crashlytics by lazy { "com.google.firebase:firebase-crashlytics-ktx" }
    val performance by lazy { "com.google.firebase:firebase-perf-ktx" }
}

fun DependencyHandler.firebase() {
    implementation(platform(Firebase.bom))
    implementation(Firebase.storage)
    implementation(Firebase.analytics)
    implementation(Firebase.messaging)
    implementation(Firebase.performance)
    implementation(Firebase.crashlytics)
}

/**
 * TEST
 */

object TestDependencies {
    val junit by lazy { "junit:junit:4.12" }
}

fun DependencyHandler.test() {
    testImplementation(TestDependencies.junit)
}

/**
 * ALERT
 */
object Alert {
    val sneaker by lazy { "com.irozon.sneaker:sneaker:${Versions.sneaker}" }
    val alert by lazy { "com.irozon.alertview:alertview:${Versions.alert}" }
    val alerter by lazy { "com.github.tapadoo:alerter:${Versions.alerter}" }
}

fun DependencyHandler.alert() {
    implementation(Alert.alert)
    implementation(Alert.alerter)
    implementation(Alert.sneaker)
}