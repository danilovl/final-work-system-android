import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.finalworksystem"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.finalworksystem"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }

        val baseUrl = localProperties.getProperty("BASE_URL") ?: (project.findProperty("BASE_URL") as? String) ?: "http://localhost:8080/"
        val apiKey = localProperties.getProperty("API_KEY") ?: (project.findProperty("API_KEY") as? String) ?: "default_api_key"
        val apiGoogleKey = localProperties.getProperty("API_GOOGLE_KEY") ?: (project.findProperty("API_GOOGLE_KEY") as? String) ?: "default_google_key"
        val apiCacheRequest = localProperties.getProperty("API_CACHE_REQUEST") ?: (project.findProperty("API_CACHE_REQUEST") as? String) ?: "true"
        val apiCacheRequestBoolean = apiCacheRequest.toBoolean() || apiCacheRequest == "1"

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "API_GOOGLE_KEY", "\"$apiGoogleKey\"")
        buildConfigField("boolean", "API_CACHE_REQUEST", "$apiCacheRequestBoolean")

        manifestPlaceholders["API_GOOGLE_KEY"] = apiGoogleKey
        // Release allows plain HTTP only when the configured server itself is HTTP.
        manifestPlaceholders["usesCleartextTraffic"] = baseUrl.startsWith("http://").toString()
    }

    buildTypes {
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = "true"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        // Existing compose-lints findings are recorded here; new code must not add any.
        baseline = file("lint-baseline.xml")
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.datastore)
    implementation(libs.datastore.preferences)
    implementation(libs.navigation.compose)
    implementation(libs.viewmodel.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.timber)

    implementation(libs.maps.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.koin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.paging.testing)
    testImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    lintChecks(libs.compose.lint.checks)
}
