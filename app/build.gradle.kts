plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.finalworksystem"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.finalworksystem"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = mutableMapOf<String, String>()

        if (localPropertiesFile.exists()) {
            val lines = localPropertiesFile.readLines()
            for (line in lines) {
                if (line.contains("=") && !line.startsWith("#")) {
                    val parts = line.split("=", limit = 2)
                    if (parts.size == 2) {
                        localProperties[parts[0].trim()] = parts[1].trim()
                    }
                }
            }
        }

        val baseUrl = localProperties["BASE_URL"] ?: (project.findProperty("BASE_URL") as? String) ?: "http://localhost:8080/"
        val apiKey = localProperties["API_KEY"] ?: (project.findProperty("API_KEY") as? String) ?: "default_api_key"
        val apiGoogleKey = localProperties["API_GOOGLE_KEY"] ?: (project.findProperty("API_GOOGLE_KEY") as? String) ?: "default_google_key"
        val apiCacheRequest = localProperties["API_CACHE_REQUEST"] ?: (project.findProperty("API_CACHE_REQUEST") as? String) ?: "true"
        val apiCacheRequestBoolean = apiCacheRequest.toBoolean() || apiCacheRequest == "1"

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "API_GOOGLE_KEY", "\"$apiGoogleKey\"")
        buildConfigField("boolean", "API_CACHE_REQUEST", "$apiCacheRequestBoolean")

        manifestPlaceholders["API_GOOGLE_KEY"] = apiGoogleKey
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
        buildConfig = true
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.datastore.preferences)
    implementation(libs.navigation.compose)
    implementation(libs.viewmodel.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.maps.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
