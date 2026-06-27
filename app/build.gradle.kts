plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.cusbuy.fxyrtw"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  testOptions { unitTests { isIncludeAndroidResources = true } }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

dependencies {
  // Compose BOM + Firebase BOM
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))

  // Core Android
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)

  // Compose UI
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)

  // Navigation
  implementation(libs.androidx.navigation.compose)

  // Room Database
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  "ksp"(libs.androidx.room.compiler)

  // Coroutines
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  // Retrofit + Moshi + OkHttp
  implementation(libs.retrofit)
  implementation(libs.converter.moshi)
  implementation(libs.okhttp)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  "ksp"(libs.moshi.kotlin.codegen)

  // Coil (images)
  implementation(libs.coil.compose)

  // DataStore
  implementation(libs.androidx.datastore.preferences)

  // Permissions
  implementation(libs.accompanist.permissions)

  // GPS / Location
  implementation(libs.play.services.location)

  // CameraX
  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)

  // ===== NOUVELLES INTEGRATIONS =====

  // ExoPlayer / Media3 (lecteur vidéo)
  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.ui)
  implementation(libs.androidx.media3.session)

  // Firebase Auth
  implementation(libs.firebase.auth)
  // Firebase Firestore (base de données temps réel)
  implementation(libs.firebase.firestore)
  // Firebase Storage (upload images/vidéos)
  implementation(libs.firebase.storage)
  // Firebase Cloud Messaging (notifications push)
  implementation(libs.firebase.messaging)
  // Firebase AI (Gemini)
  implementation(libs.firebase.ai)

  // Google Sign-In
  implementation(libs.play.services.auth)

  // Agora RTC (Live Streaming)
  implementation(libs.agora.rtc)

  // Google Maps
  implementation(libs.maps.compose)
  implementation(libs.play.services.maps)

  // Gson
  implementation(libs.gson)

  // Tests
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
