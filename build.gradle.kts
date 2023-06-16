plugins {
    id("java")
    id("fabric-loom") version "1.2-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.10.0"
}

val javaVersion = getProperty("java_version")
val fabricVersion = getProperty("fabric_version")
val fabricApiVersion = getProperty("fabric_api_version")
val fabricRequirement = getProperty("fabric_requirement")
val mcVersion = getProperty("mc_version")
val mcRequirement = getProperty("mc_requirement")
val yarnVersion = getProperty("yarn_version")

val modId = getProperty("mod_id")
val modName = getProperty("mod_name")
val author = getProperty("mod_author")
val modVersion = getProperty("mod_version")
val modDescription = getProperty("mod_description")
val modLicense = getProperty("mod_license")
val modUrl = getProperty("mod_url")
val modSourceUrl = getProperty("mod_source_url")

version = modVersion
group = "com.spyeic"
base.archivesName.set(modId)

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:$yarnVersion")
    modImplementation("net.fabricmc:fabric-loader:$fabricVersion")
    // modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

tasks.processResources {
    val map = mapOf(
        // mod
        "modId" to modId,
        "modName" to modName,
        "author" to author,
        "modVersion" to modVersion,
        "modDescription" to modDescription,
        "modLicense" to modLicense,
        "modUrl" to modUrl,
        "modSourceUrl" to modSourceUrl,
        // minecraft
        "mcVersion" to mcVersion,
        "mcRequirement" to mcRequirement,
        "fabricVersion" to fabricVersion,
        "fabricApiVersion" to fabricApiVersion,
        "fabricRequirement" to fabricRequirement,
    )
    inputs.properties(map)

    filesMatching("fabric.mod.json") {
        expand(map)
    }

    filesMatching("mixins.json") {
        name = "$modId.mixins.json"
    }

    val file = project.projectDir.resolve("src/main/resources")
    val file2 = file.resolve("assets")
    if (!file2.exists()) {
        file2.resolve(modId).mkdirs()
    } else {
        if (file2.list().size > 1) {
            throw IllegalArgumentException("More than one asset folder found!")
        }
        if (file2.list().isEmpty()) {
            file2.resolve(modId).mkdirs()
        } else {
            file2.listFiles()[0].renameTo(file2.resolve(modId))
        }
    }
}

fun Project.getProperty(name: String) =
    this.findProperty(name)?.toString()
        ?: throw IllegalArgumentException("Property $name not found in gradle.properties file")

fun Logger.show(message: String) = this.warn("\u001B[32m$message\u001B[0m")