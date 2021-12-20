import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("kotlin-parcelize")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply { load(FileInputStream(keystorePropertiesFile)) }

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {
        applicationId = "eamato.funn.r6companion"
        minSdk = 21
        targetSdk = 31
        multiDexEnabled = true
        versionCode = 10
        versionName = "10.0"
        testInstrumentationRunner= "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] ?: "")
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storePassword = keystoreProperties["storePassword"] as String?
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        create("alpha") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        create("beta") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    applicationVariants.all {
        val appName: String = if (project.hasProperty("applicationName")) {
            project.property("applicationName") as String
        } else {
            parent?.name ?: "no_app_name"
        }

        outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = when {
                    name.contains("release") -> "${appName}.apk"
                    name.contains("debug") -> "${appName}_debug.apk"
                    name.contains("alpha") -> "${appName}_alpha.apk"
                    name.contains("beta") -> "${appName}_beta.apk"
                    else -> "${appName}_unknown.apk"
                }
            }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")

    implementation("com.google.android.material:material:1.4.0")

    /* AndroidX dependencies */
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.paging:paging-runtime-ktx:3.1.0")

    /* Lifecycle dependencies */
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    /* GSON dependencies */
    implementation("com.google.code.gson:gson:2.8.6")

    /* Glide dependencies */
    implementation("com.github.bumptech.glide:annotations:4.11.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.10.0")
    kapt("com.github.bumptech.glide:compiler:4.10.0")

    /* Navigation dependencies */
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    /* Rx dependencies */
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.12")

    /* Retrofit dependencies */
    implementation("com.squareup.retrofit2:retrofit:2.6.2")
    implementation("com.squareup.retrofit2:converter-gson:2.6.2")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    /* Firebase dependencies */
    implementation("com.google.firebase:firebase-core:20.0.2")
    implementation("com.google.firebase:firebase-crashlytics:18.2.6")
    implementation("com.google.firebase:firebase-analytics:20.0.2")
    implementation("com.google.firebase:firebase-config:21.0.1")
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.google.firebase:firebase-perf:20.0.4")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.1.1")
    implementation("com.google.firebase:firebase-ads:20.5.0")

    /* Test dependencies */
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}