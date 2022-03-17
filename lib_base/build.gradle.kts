plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}
android {
    compileSdkVersion(AppConfig.compileSdkVersion)
    buildToolsVersion(AppConfig.buildToolsVersion)

    defaultConfig {
        minSdkVersion(AppConfig.minSdkVersion)
        targetSdkVersion(AppConfig.targetSdkVersion)
        versionCode=AppConfig.versionCode
        versionName=AppConfig.versionName

        consumerProguardFiles("consumer-rules.pro")
    }

    //编译类型
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    //依赖操作
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(DependenciesConfig.STD_LIB)
    api(DependenciesConfig.APP_COMPAT)
    api(DependenciesConfig.KTX_CORE)
    api(DependenciesConfig.EVENT_BUS)

    api(project(":lib_voice"))
    api(project(":lib_network"))
}