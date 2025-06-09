plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("jacoco")
    id("org.sonarqube") version "5.1.0.4882"
}

android {
    namespace = "at.aau.serg.websocketbrokerdemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "at.aau.serg.websocketbrokerdemo"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            all {
                it.useJUnitPlatform()
                it.finalizedBy(tasks.named("jacocoTestReport"))
            }
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates code coverage report for the test task."
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        xml.outputLocation.set(file("${project.projectDir}/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    val debugTree =
        fileTree("${project.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
            exclude(fileFilter)
        }

    val javaDebugTree =
        fileTree("${project.layout.buildDirectory.get().asFile}/intermediates/javac/debug") {
            exclude(fileFilter)
        }

    val mainSrc = listOf(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    )

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree, javaDebugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get().asFile) {
        include("jacoco/testDebugUnitTest.exec")
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

sonar {
    properties {
        property("sonar.projectKey", "SE-Projekt-Beta_Client")
        property("sonar.organization", "se-projekt-beta")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.java.coveragePlugin", "jacoco")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project.projectDir}/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
        )
        property(
            "sonar.coverage.exclusions",
            "**/MainActivity.kt," +
                    "**/LobbyActivity.kt," +
                    "**/UsernameActivity.kt," +
                    "**/CreateLobbyActivity.kt," +
                    "**/ListLobbyActivity.kt," +
                    "**/TileInfoDialog.kt," +
                    "**/Theme.kt," +
                    "**/Color.kt," +
                    "**/TileType.kt," +
                    "**/Tile.kt," +
                    "**/Type.kt," +
                    "**/MyStomp.kt," +
                    "**/Callbacks.kt," +
                    "**/ui/**," +
                    "**/*Activity.kt," +
                    "**/*Adapter.kt," +
                    "**/network/**," +
                    "**/game/dialog/**," +
                    "**/dto/**"
        )
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation(libs.krossbow.websocket.okhttp)
    implementation(libs.krossbow.stomp.core)
    implementation(libs.krossbow.websocket.builtin)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.json)
    implementation(libs.gson)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.gson)
    implementation(libs.gson)
    implementation(kotlin("test"))
    implementation(libs.androidx.gridlayout)

// Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api.v582)
    testRuntimeOnly(libs.junit.jupiter.engine.v582)

    // MockK for Kotlin-friendly mocking
    testImplementation(libs.mockk)

    testImplementation(libs.androidx.junit.v115)
    testImplementation(libs.androidx.core)
    testImplementation(libs.robolectric)



}