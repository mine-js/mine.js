plugins {
    kotlin("jvm") version "1.5.21"
    `maven-publish`
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin="org.jetbrains.kotlin.jvm")
    apply(plugin="maven-publish")

    group = "xyz.minejs"
    version = "1.0.0-SNAPSHOT"

    dependencies {
        implementation(kotlin("stdlib"))
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                //artifact(tasks["shadowJar"])
            }
        }
        repositories {
            maven {
                credentials {
                    username = rootProject.property("username") as String
                    password = rootProject.property("password") as String
                }
                url = uri("https://repo.projecttl.net/repository/maven-snapshots/")
            }
        }
    }
}