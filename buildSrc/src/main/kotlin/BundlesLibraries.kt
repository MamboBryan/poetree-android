import org.gradle.api.artifacts.dsl.DependencyHandler

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
    implementation(Hilt.hiltWork)
    kapt(Hilt.hiltKapt)
    kapt(Hilt.hiltWorkKapt)
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
    implementation(OkHttp.bom)
    implementation(OkHttp.okHttp)
    implementation(OkHttp.logging)
}