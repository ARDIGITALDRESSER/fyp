plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fyp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fyp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        viewBinding = true  // ✅ Keep view binding
        dataBinding = true  // ✅ Enable data binding here
    }
}
repositories {
    google()          // ✅ Required for Google libraries
    mavenCentral()    // ✅ Required for most libraries
    maven { url = uri("https://jitpack.io") }  // ✅ Required for SceneView dependencies
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")
    implementation ("androidx.cardview:cardview:1.0.0")




    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.recyclerview)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // ✅ ARCore
    implementation("com.google.ar:core:1.40.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ✅ Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // ✅ Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.activity)

    // ✅ Sceneform (Make sure these are correctly configured in your project)
    // implementation("com.github.SceneView:core:1.1.0")
    // implementation("com.github.SceneView:sceneform-ux:1.1.0")

    // ✅ Cloudinary
    implementation("com.cloudinary:cloudinary-android:2.0.0")

    // ✅ Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
