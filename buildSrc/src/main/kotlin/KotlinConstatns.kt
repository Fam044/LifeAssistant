/**
 * FileName: KotlinConstants
 * Profile: Kotlin常量
 */

object KotlinConstants{
    // Gradle版本
    const val gradle_version = "4.0.0"
    // Kotlin版本
    const val kotlin_version = "1.3.71"
}

object AppConfig{
    // 依赖版本
    const val compileSdkVersion = 29
    // 编译工具版本
    const val buildToolsVersion = "29.0.2"
    // 包名
    const val applicationId = "com.imooc.aivoiceapp"
    // 最小支持SDK
    const val minSdkVersion = 21
    // 当前基于SDK
    const val targetSdkVersion = 29
    // 版本编码
    const val versionCode = 1
    // 版本名称
    const val versionName = "1.0"
}

// 依赖配置
object DependenciesConfig{
    // Kotlin基础库
    const val STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${KotlinConstants.kotlin_version}"
    // Android标准库
    const val APP_COMPAT = "androidx.appcompat:appcompat:1.1.0"
    // Kotlin核心库
    const val KTX_CORE = "androidx.core:core-ktx:1.2.0"
    // EventBus
    const val EVENT_BUS = "org.greenrobot:eventbus:3.2.0"
}

// Module配置
object ModuleConfig{
    // 模块是否为App
    var isApp = true

    // 包名
    const val MODULE_APP_MANAGER = "com.imooc.module_app_manager"
    const val MODULE_CONSTELLATION = "com.imooc.module_constellation"
    const val MODULE_DEVELOPER = "com.imooc.module_developer"
    const val MODULE_JOKE = "com.imooc.module_joke"
    const val MODULE_MAP = "com.imooc.module_map"
    const val MODULE_SETTING = "com.imooc.module_setting"
    const val MODULE_VOICE_SETTING = "com.imooc.module_setting"
    const val MODULE_WEATHER = "com.imooc.module_weather"
}