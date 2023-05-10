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

@Suppress("UnstableApiUsage")
android {
    compileSdk = 33
    buildToolsVersion = "31.0.0"

    defaultConfig {
        applicationId = "eamato.funn.r6companion"
        minSdk = 21
        targetSdk = 33
        multiDexEnabled = true
        versionCode = 19
        versionName = "19"
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
            isShrinkResources = true
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
        create("benchmark") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
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

    namespace = "eamato.funn.r6companion"

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

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21") // error?

    implementation("com.google.android.play:core:1.10.3")
    implementation("com.google.android.material:material:1.7.0")

    /* AndroidX dependencies */
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.profileinstaller:profileinstaller:1.3.0")

    /* Lifecycle dependencies */
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    /* GSON dependencies */
    implementation("com.google.code.gson:gson:2.9.0")

    /* Glide dependencies */
    implementation("com.github.bumptech.glide:annotations:4.12.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.10.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    /* Navigation dependencies */
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    /* Rx dependencies */
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.12")

    /* Retrofit dependencies */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    /* Firebase dependencies */
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-crashlytics:18.3.6")
    implementation("com.google.firebase:firebase-analytics:21.2.2")
    implementation("com.google.firebase:firebase-config-ktx:21.3.0")
    implementation("com.google.firebase:firebase-messaging:23.1.2")
    implementation("com.google.firebase:firebase-perf:20.3.1")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.3.1")
    implementation("com.google.firebase:firebase-ads:22.0.0")
    implementation("com.google.firebase:firebase-dynamic-links-ktx:21.1.0")

    implementation("com.github.beksomega:loopinglayout:0.4.1")

    /* Test dependencies */
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /* Debug */
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.8.1")
}