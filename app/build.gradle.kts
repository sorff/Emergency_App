plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.emergencyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.emergencyapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    // Firebase Phone Auth — actif une fois google-services.json ajouté + plugin google-services appliqué.
    // Sans config, l'app bascule automatiquement sur un mode OTP simulé (voir AuthHelper).
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
