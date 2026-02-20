import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.10" // Upgraded to a modern, stable version compatible with JDK 25
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases/") }
}

val gdxVersion = "1.12.1"

dependencies {
    // Core
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    
    // Desktop
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin", "src/desktop/kotlin")
        resources.srcDirs("src/main/resources", "src/desktop/resources")
    }
}

application {
    mainClass.set("com.dw1demo.DesktopLauncherKt")
}

// Set the Kotlin bytecode version to a safe, compatible target
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21) // Targeting JVM 17 is safe and compatible with JDK 25
    }
}
