plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.8.0"
}

group "com.huage2580"
version "2.1.0"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.1")
    implementation("com.github.promeg:tinypinyin:2.0.3") // TinyPinyin核心包，约80KB
    implementation("org.quartz-scheduler:quartz:2.3.2") {
        exclude(group = "com.zaxxer")
        exclude(group = "org.slf4j", module = "slf4j-api") // 明确指定 group 和 module
        exclude(group = "com.mchange")
    }
}
// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2021.3.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {

    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8" // 设置编码格式，否则代码中使用中文报错
    }

    patchPluginXml {
        changeNotes.set("""
        v1.1 增加了股票的tab，采用腾讯的行情接口，股票轮询间隔10s  <br>
        v1.2 支持了港股和美股 示例代码：（sh000001,sh600519,sz000001,hk00700,usAAPL）代码一般可以在各网页端看得到  <br>
        v1.3   支持了IDEA 2020.1.3,兼容到`IDEA 2017.3`，修复macOS 行高问题（不确定 <br>
        v1.4 增加了隐蔽模式（全拼音和无色涨跌幅
        v1.5 增加了股票界面的排序~，可按净值和涨跌幅等列排序
        v1.6 样式修改，增加精确净值（当日，上一交易日
        v1.7 设置界面样式调整，增加新浪股票接口备选
      """)
        sinceBuild.set("213")
        untilBuild.set("223.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
