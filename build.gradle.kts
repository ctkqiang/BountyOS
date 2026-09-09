/*
 * Root build configuration.
 *
 * Plugins are declared here (apply false) so their versions are resolved
 * through the Gradle version catalog in gradle/libs.versions.toml.
 * Modules apply them individually.
 */
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
