plugins {
    id("java")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "core.smp"
version = "0.0.40"

repositories {
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    mavenCentral()
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }

    assemble {
        dependsOn(shadowJar)
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.brigadier:4.0.0-rc.12")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

paper {
    main = "core.smp.main.Main"
    apiVersion = "1.21"
}