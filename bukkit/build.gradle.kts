plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation(project(":common"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("net.projecttl:InventoryGUI-api:4.2.0")
    implementation("mysql:mysql-connector-java:8.0.27")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("com.eclipsesource.j2v8:j2v8:6.2.1")
    implementation("com.eclipsesource.j2v8:j2v8_win32_x86_64:4.6.0")
    implementation("com.eclipsesource.j2v8:j2v8_linux_x86_64:4.6.0")
    implementation("com.eclipsesource.j2v8:j2v8_macosx_x86_64:4.6.0")
    implementation("com.google.code.gson:gson:2.8.0")
    implementation("io.alicorn.v8:v8-adapter:1.59")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "15"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "15"
    }

    create<Jar>("sourceJar") {
        archiveClassifier.set("source")
        from(sourceSets["main"].allSource)
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        dependencies {
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.5.21"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:1.5.21"))
        }
    }
}