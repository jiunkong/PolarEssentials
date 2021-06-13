plugins {
    kotlin("jvm") version "1.5.10"
}

group = "dev.bukgeuk.polaressentials"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")

    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
}

val shade = configurations.create("shade")
shade.extendsFrom(configurations.implementation.get())

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    jar {
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
        exclude("**/module-info.class")


        from (
            shade.map {
                if (it.isDirectory)
                    it
                else
                    zipTree(it)
            }
        )
    }
}