import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "1.9.20"
    application
}

kotlin {
    jvmToolchain(21)
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
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
