plugins {
    id("com.android.application") version "7.3.0"
    id("org.jetbrains.kotlin.android") version "1.9.22"
}

repositories {
    google()
    mavenCentral()
}

android {
    namespace = "com.jn.videoplayer"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.jn.videoplayer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.preference:preference:1.2.0")
    // Older, more stable media3 versions
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-session:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
