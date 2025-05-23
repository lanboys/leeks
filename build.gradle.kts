plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.huage2580"
version = "2.1.0"


repositories {
    mavenCentral()
    mavenLocal()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2024.2.5")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }

    testImplementation("junit:junit:4.13.1")
    implementation("com.github.promeg:tinypinyin:2.0.3") // TinyPinyin核心包，约80KB
    implementation("org.quartz-scheduler:quartz:2.3.2") {
        exclude(group = "com.zaxxer")
        exclude(group = "org.slf4j", module = "slf4j-api") // 明确指定 group 和 module
        exclude(group = "com.mchange")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "242"
        }

        changeNotes = """
        v1.1 增加了股票的tab，采用腾讯的行情接口，股票轮询间隔10s  <br>
        v1.2 支持了港股和美股 示例代码：（sh000001,sh600519,sz000001,hk00700,usAAPL）代码一般可以在各网页端看得到  <br>
        v1.3   支持了IDEA 2020.1.3,兼容到`IDEA 2017.3`，修复macOS 行高问题（不确定 <br>
        v1.4 增加了隐蔽模式（全拼音和无色涨跌幅
        v1.5 增加了股票界面的排序~，可按净值和涨跌幅等列排序
        v1.6 样式修改，增加精确净值（当日，上一交易日
        v1.7 设置界面样式调整，增加新浪股票接口备选
    """.trimIndent()
    }
}

tasks {

    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
        options.encoding = "UTF-8" // 设置编码格式，否则代码中使用中文报错
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
}
