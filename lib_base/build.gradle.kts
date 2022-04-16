plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
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

        kapt {
            arguments {
                arg("AROUTER_MODULE_NAME" , project.name)
            }
        }
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
    //Kotlin基础库
    api(DependenciesConfig.STD_LIB)
    //Android标准库
    api(DependenciesConfig.APP_COMPAT)
    //Kotlin核心库
    api(DependenciesConfig.KTX_CORE)
    //EventBus
    api(DependenciesConfig.EVENT_BUS)
    //ARouter
    api(DependenciesConfig.AROUTER)
    //运行时注解
    kapt(DependenciesConfig.AROUTER_COMPILER)
    //RecyclerView
    api(DependenciesConfig.RECYCLERVIEW)
    //AndPermissions
    api(DependenciesConfig.AND_PERMISSIONS)
    //ViewPager
    api(DependenciesConfig.VIEWPAGER)
    api(DependenciesConfig.MATERIAL)
    //Lottie
    api(DependenciesConfig.LOTTIE)
    //SmartRefreshLayout
    api(DependenciesConfig.REFRESH_KERNEL)
    api(DependenciesConfig.REFRESH_HEADER)
    api(DependenciesConfig.REFRESH_FOOT)
    //Chart
    api(DependenciesConfig.CHART)
    //屏幕适配
    api(DependenciesConfig.AUTO_SIZE)
    //状态栏
    api(DependenciesConfig.ACTION_BAR)
    //波浪
    api(DependenciesConfig.VOICE_LINE)

    //百度地图
    api(files("libs/BaiduLBS_Android.jar"))
    api(files("libs/javapoet-1.9.0.jar"))
    api(files("libs/IndoorscapeAlbumPlugin.jar"))

    api(project(":lib_voice"))
    api(project(":lib_network"))
}