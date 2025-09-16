import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
}

fun getApiKeyFromLocalProperties(projectRootDir: File, propertyName: String): String? {
    val localPropertiesFile = File(projectRootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        val properties = Properties()
        try {
            FileInputStream(localPropertiesFile).use { input ->
                properties.load(input)
            }
            return properties.getProperty(propertyName)
        } catch (e: Exception) {
            // Log error or handle appropriately
            project.logger.warn("Could not load properties from local.properties: ${e.message}")
        }
    }
    return null
}

android {
    namespace = "com.example.feature_places"
    compileSdk = 36

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        minSdk = 27
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val mapsApiKey = getApiKeyFromLocalProperties(project.rootDir, "MAPS_API_KEY")
            ?: run {
                // Option 1: Provide a dummy key for developers without the file (for basic builds)
                // This is useful if the key isn't strictly needed for all debug scenarios.
                // Consider logging a warning so developers are aware.
                project.logger.warn(
                    "MAPS_API_KEY not found in local.properties. " +
                            "Using a dummy key or empty string. Some features may not work."
                )
                "YOUR_DUMMY_API_KEY" // Or an empty string "" if that's more appropriate

                // Option 2: Throw an error if the key is absolutely required for the build.
                // This is safer if the app cannot function without the key.
                // throw GradleException(
                //    "MAPS_API_KEY not found in local.properties. " +
                //    "Please create the file in the project root and add the key (e.g., MAPS_API_KEY=\"YOUR_KEY\")."
                // )
            }

        // Make it available in BuildConfig.java/kt
        // The first parameter is the type (String), the second is the name of the constant,
        // and the third is the value (must be properly escaped if it's a string).
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")

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

    packagingOptions {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md" // Common one to also exclude
            excludes += "/META-INF/LICENSE"           // Sometimes just "LICENSE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/AL2.0"
            excludes += "/META-INF/LGPL2.1"
            // Add any other specific duplicate files reported in your error message
            // For JUnit 5, these are the most common:
            excludes += "META-INF/junit/jupiter/extensions.properties"
            excludes += "META-INF/junit/platform/extensions.properties"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(project(":core-domain"))
    implementation(project(":core-data"))
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)

// Unit tests
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

// Android tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.kotlinx.coroutines.test)

}