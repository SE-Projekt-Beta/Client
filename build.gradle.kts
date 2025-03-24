// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.sonarqube") version "6.0.1.5171"
}

sonar {
  properties {
    property("sonar.projectKey", "SE-Projekt-Beta_Client")
    property("sonar.organization", "se-projekt-beta")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}
