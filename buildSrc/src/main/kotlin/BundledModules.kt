import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.commonModules() {
    implementation(project(Modules.Commons.ui))
    implementation(project(Modules.Commons.core))
    implementation(project(Modules.Commons.data))
    implementation(project(Modules.Commons.navigation))
}

fun DependencyHandler.featureModules() {
    implementation(project(Modules.Features.auth))
    implementation(project(Modules.Features.feed))
    implementation(project(Modules.Features.poem))
    implementation(project(Modules.Features.search))
    implementation(project(Modules.Features.artist))
    implementation(project(Modules.Features.explore))
    implementation(project(Modules.Features.account))
    implementation(project(Modules.Features.library))
    implementation(project(Modules.Features.compose))
    implementation(project(Modules.Features.profile))
    implementation(project(Modules.Features.comments))
    implementation(project(Modules.Features.bookmarks))
    implementation(project(Modules.Features.onboarding))
    implementation(project(Modules.Features.updatePassword))
    implementation(project(Modules.Features.landing))
    implementation(project(Modules.Features.loading))
    implementation(project(Modules.Features.setup))
    implementation(project(Modules.Features.publish))
    implementation(project(Modules.Features.settings))
    implementation(project(Modules.Features.topic))
}