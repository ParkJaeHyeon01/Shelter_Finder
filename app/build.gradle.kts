import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
}

val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "com.example.shelterfinder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shelterfinder"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 2. BuildConfig 변수 생성 (괄호와 쉼표 사용 주의)
        buildConfigField("String", "PUBLIC_DATA_KEY", "\"${localProperties["PUBLIC_DATA_KEY"] ?: ""}\"")
        buildConfigField("String", "KAKAO_MAP_KEY", "\"${localProperties["KAKAO_MAP_KEY"] ?: ""}\"")
        buildConfigField("String", "KAKAO_REST_API_KEY", "\"${localProperties["KAKAO_REST_API_KEY"] ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.lifecycle.viewmodel.ktx)

    kapt(libs.room.compiler)

    implementation("com.kakao.sdk:v2-user:2.21.3")
    implementation ("com.kakao.maps.open:android:2.12.8")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //implementation("net.daum.mf.map.api:libDaumMapAndroid:1.4.0")
}