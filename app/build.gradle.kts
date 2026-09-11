import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

/*
 * 正式签名的凭据来源，按优先级：
 * 1. 仓库根目录的 keystore.properties（本地发布用，已在 .gitignore 中）
 * 2. 环境变量（CI 用，例如 GitHub Actions Secrets）
 *
 * 两者都没有时 assembleRelease 回退到 debug 签名，保证 CI 仍能产出可
 * 安装的预览 APK；但 bundleRelease 会被下方守卫拦截。
 */
val releaseKeystoreProperties: Properties? = rootProject.file("keystore.properties")
    .takeIf { it.exists() }
    ?.let { file -> Properties().apply { file.inputStream().use { load(it) } } }

val envKeystoreBase64: String? = System.getenv("KEYSTORE_BASE64")

val hasReleaseSigning: Boolean =
    releaseKeystoreProperties != null || !envKeystoreBase64.isNullOrBlank()

android {
    namespace = "com.bountyos"
    compileSdk = 36

    signingConfigs {
        create("release") {
            val properties = releaseKeystoreProperties
            when {
                // 本地发布：仓库根目录的 keystore.properties。
                properties != null -> {
                    storeFile = file(properties.getProperty("storeFile"))
                    storePassword = properties.getProperty("storePassword")
                    keyAlias = properties.getProperty("keyAlias")
                    keyPassword = properties.getProperty("keyPassword")
                }

                // CI 发布：由 Secrets 注入的 base64 keystore。
                !envKeystoreBase64.isNullOrBlank() -> {
                    val keystoreFile = file("$rootDir/release.keystore")
                    if (!keystoreFile.exists()) {
                        keystoreFile.parentFile?.mkdirs()
                        keystoreFile.writeBytes(Base64.getDecoder().decode(envKeystoreBase64))
                    }
                    storeFile = keystoreFile
                    storePassword = System.getenv("KEYSTORE_PASSWORD")
                    keyAlias = System.getenv("KEY_ALIAS")
                    keyPassword = System.getenv("KEY_PASSWORD")
                }

                // 未配置正式签名：回退 debug keystore，仅供 assembleRelease
                // 产出可安装的预览 APK，不可用于 Play 上架。
                else -> {
                    storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }
            }
        }
    }

    defaultConfig {
        applicationId = "xin.ctkqiang.bountyos"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.lottie.compose)
    implementation(libs.haze)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

/*
 * Google Play 拒收以 debug 证书签名的上架产物。未配置正式签名时让
 * bundleRelease 直接失败，避免把 debug 签名的 AAB 误传到 Play Console。
 */
if (!hasReleaseSigning) {
    tasks.matching { it.name == "bundleRelease" }.configureEach {
        doFirst {
            throw GradleException(
                "bundleRelease 需要正式签名。请在仓库根目录创建 keystore.properties" +
                    "（storeFile / storePassword / keyAlias / keyPassword），" +
                    "或设置 KEYSTORE_BASE64、KEYSTORE_PASSWORD、KEY_ALIAS、" +
                    "KEY_PASSWORD 环境变量。",
            )
        }
    }
}
