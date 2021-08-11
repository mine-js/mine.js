plugins {
}

group = "org.example"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":common"))
    implementation("com.eclipsesource.j2v8:j2v8:6.2.0")
    implementation("com.eclipsesource.j2v8:j2v8_win32_x86_64:4.6.0")
    implementation("com.eclipsesource.j2v8:j2v8_linux_x86_64:4.6.0")
    implementation("com.eclipsesource.j2v8:j2v8_macosx_x86_64:4.6.0")
}
