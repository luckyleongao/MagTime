plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.leongao.magtime"
    compileSdk = 34

    viewBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.leongao.magtime"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Room export schema
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] =
                    "$projectDir/schemas"
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            buildConfigField("Boolean", "DEBUG_MODE", "false")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
//            buildConfigField("Boolean", "DEBUG_MODE", "true")
        }
    }
    compileOptions {

        // Fix "Call requires API level 26 (current min is 25) " error in Android
        // https://developer.android.com/studio/write/java8-support#library-desugaring
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    val room_version = "2.6.1"

    // Glide
    implementation ("com.github.bumptech.glide:recyclerview-integration:4.14.2") {
        // Excludes the support library because it's already included by Glide.
        isTransitive = false
    }
    implementation ("com.github.bumptech.glide:glide:5.0.0-rc01")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.11.0")
    // Room
    implementation ("androidx.room:room-runtime:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")
    // RxJava 3
    implementation ("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2")
    // RxJava with Retrofit
    implementation ("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    // RxJava with Room
    implementation ("androidx.room:room-rxjava3:2.6.1")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // Bridging RxJava and LiveData
    implementation ("androidx.lifecycle:lifecycle-reactivestreams:2.7.0")
    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-runtime:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //noinspection LifecycleAnnotationProcessorWithJava8
    annotationProcessor ("androidx.lifecycle:lifecycle-compiler:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.7.0")
    // Timber Log Print
    implementation ("com.jakewharton.timber:timber:4.7.1")

    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.android.material:material:1.13.0-alpha03")
    // DataStore instead of SharedPreferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.1.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs_nio:2.0.4")
}