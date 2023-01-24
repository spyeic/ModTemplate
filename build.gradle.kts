import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import net.minecraftforge.gradle.userdev.UserDevExtension
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.format.DateTimeFormatter.ISO_INSTANT

buildscript {
    repositories {
        // These repositories are only for Gradle plugins, put any other repositories in the repository block further below
        maven("https://maven.minecraftforge.net")
        mavenCentral()
    }
    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = "5.1.+")
    }
}
// Only edit below this line, the above code adds and enables the necessary things for Forge to be setup.
plugins {
    id("java")
    id("maven-publish")
}
apply(plugin = "net.minecraftforge.gradle")


val javaVersion = getProperty("java_version")
val forgeVersion = getProperty("forge_version")
val forgeVersionRange = getProperty("forge_version_range")
val mcVersion = getProperty("mc_version")
val mcVersionRange = getProperty("mc_version_range")

val modId = getProperty("mod_id")
val modName = getProperty("mod_name")
val author = getProperty("mod_author")
val modVersion = getProperty("mod_version")
val modShortDescription = getProperty("mod_short_description")
val modDescription = getProperty("mod_description")
val modLicense = getProperty("mod_license")

version = modVersion
group = "com.spyeic"
base.archivesName.set(modId)

// Mojang ships Java 17 to end users in 1.18+, so your mod should target Java 17.
java.toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))


logger.show("Java: ${System.getProperty("java.version")}")
logger.show("JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")})}")
logger.show("Arch: \${System.getProperty(\"os.arch\")")

minecraft {
    // The mappings can be changed at any time and must be in the following format.
    // Channel:   Version:
    // official   MCVersion             Official field/method names from Mojang mapping files
    // parchment  YYYY.MM.DD-MCVersion  Open community-sourced parameter names and javadocs layered on top of official
    //
    // You must be aware of the Mojang license when using the 'official' or 'parchment' mappings.
    // See more information here: https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md
    //
    // Parchment is an unofficial project maintained by ParchmentMC, separate from MinecraftForge
    // Additional setup is needed to use their mappings: https://github.com/ParchmentMC/Parchment/wiki/Getting-Started
    //
    // Use non-default mappings at your own risk. They may not always work.
    // Simply re-run your setup task after changing the mappings to update your workspace.
    mappings("official", mcVersion)
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg")) // Currently, this location cannot be changed from the default.

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        create("client") {
            workingDirectory(project.file("run"))

            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            property("forge.logging.markers", "REGISTRIES")


            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            property("forge.logging.console.level", "debug")

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            property("forge.enabledGameTestNamespaces", modId)

            mods {
                create(modId) {
                    source(sourceSets["main"])
                    source(sourceSets["test"])
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")

            property("forge.logging.console.level", "debug")

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            property("forge.enabledGameTestNamespaces", modId)

            mods {
                create(modId) {
                    source(sourceSets["main"])
                    source(sourceSets["test"])
                }
            }
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        create("gameTestServer") {
            workingDirectory(project.file("run"))

            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            property("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            property("forge.logging.console.level", "debug")

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            property("forge.enabledGameTestNamespaces", modId)

            mods {
                create(modId) {
                    source(sourceSets["main"])
                    source(sourceSets["test"])
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run"))

            property("forge.logging.markers", "REGISTRIES")

            property("forge.logging.console.level", "debug")

            args(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/"),
                "--existing",
                file("src/test/resources/")
            )

            mods {
                create(modId) {
                    source(sourceSets["main"])
                    source(sourceSets["test"])
                }
            }
        }
    }
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
        }
    }
}

repositories {
    // Put repositories for dependencies here
    // ForgeGradle automatically adds the Forge maven and Maven Central for you

    // If you have mod jar dependencies in ./libs, you can declare them as a repository like so:
    // flatDir {
    //    dir("libs")
    // }
}

dependencies {
    // Specify the version of Minecraft to use. If this is any group other than 'net.minecraft', it is assumed
    // that the dep is a ForgeGradle 'patcher' dependency, and its patches will be applied.
    // The userdev artifact is a special name and will get all sorts of transformations applied to it.
    minecraft("net.minecraftforge:forge:${mcVersion}-${forgeVersion}")
    // Real mod deobf dependency examples - these get remapped to your current mappings
    // compileOnly(fg.deobf("mezz.jei:jei-${mcVersion}:${jeiVersion}:api")) // Adds JEI API as a compile dependency
    // runtimeOnly(fg.deobf("mezz.jei:jei-${mcVersion}:${jeiVersion}")) // Adds the full JEI mod as a runtime dependency
    // implementation(fg.deobf("com.tterrag.registrate:Registrate:MC${mcVersion}-1.1.6")) // Adds registrate as a dependency
    // Examples using mod jars from ./libs
    // implementation(fg.deobf("blank:coolmod-${mcVersion}:${coolmodVersion}"))

    // For more info...
    // http://www.gradle.org/docs/current/userguide/artifact_dependencies_tutorial.html
    // http://www.gradle.org/docs/current/userguide/dependency_management.html

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.processResources {
    val map = mapOf(
        // mod
        "modId" to modId,
        "modName" to modName,
        "author" to author,
        "modVersion" to modVersion,
        "modShortDescription" to modShortDescription,
        "modDescription" to modDescription,
        "modLicense" to modLicense,
        // minecraft
        "mcVersion" to mcVersion,
        "mcVersionRange" to mcVersionRange,
        "forgeVersion" to forgeVersion,
        "forgeVersionRange" to forgeVersionRange
    )
    inputs.properties(map)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(map)
    }
}

tasks.jar {
    manifest {
        attributes(
            "Specification-Title" to modName,
            "Specification-Vendor" to author,
            "Specification-Version" to "1",
            "Implementation-Title" to modName,
            "Implementation-Version" to modVersion,
            "Implementation-Vendor" to author,
            "Implementation-Timestamp" to ISO_INSTANT.format(Instant.now()),
            "FMLModType" to "LIBRARY"
        )
    }

    finalizedBy("reobfJar")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

if (System.getProperty("os.arch").equals("aarch64") && System.getProperty("os.name").equals("Mac OS X")) {
    logger.show("Apple Silicon detected, applying Apple Silicon patches")
    val lwjglVersion = if (mcVersion.contains("1.19")) {
        "3.3.1"
    } else {
        "3.3.0"
    }
    ext.set("lwjglVersion", lwjglVersion)
    ext.set("lwjglNatives", "natives-macos-arm64")
    configurations.configureEach {
        resolutionStrategy {
            force("org.lwjgl:lwjgl:${lwjglVersion}")
            force("org.lwjgl:lwjgl:${lwjglVersion}")
            force("org.lwjgl:lwjgl-openal:${lwjglVersion}")
            force("org.lwjgl:lwjgl-opengl:${lwjglVersion}")
            force("org.lwjgl:lwjgl-jemalloc:${lwjglVersion}")
            force("org.lwjgl:lwjgl-glfw:${lwjglVersion}")
            force("org.lwjgl:lwjgl-stb:${lwjglVersion}")
            force("org.lwjgl:lwjgl-tinyfd:${lwjglVersion}")
        }
    }
    logger.show("Using LWJGL version $lwjglVersion for Apple Silicon in Minecraft $mcVersion")
    val path = "caches/forge_gradle/mcp_repo/net/minecraft/client/${mcVersion}/client-${mcVersion}.pom"
    val cache = gradle.gradleUserHomeDir.resolve(path)
    if (cache.exists()) {
        val lines = cache.readLines(StandardCharsets.UTF_8)
        val writer = cache.writer()
        lines.forEach { line ->
            if (!line.contains("arm")) {
                writer.appendLine(line.replace("natives-macos", "natives-macos-arm64"))
            }
        }
        writer.close()
    }
}

val fg = extensions.getByType<DependencyManagementExtension>()

fun DependencyHandlerScope.minecraft(dep: String): Dependency? = "minecraft"(dep)

fun Project.minecraft(block: UserDevExtension.() -> Unit) = configure<UserDevExtension> { block() }

fun Project.getProperty(name: String) = this.findProperty(name)?.toString()
    ?: throw IllegalArgumentException("Property $name not found in gradle.properties file")

fun Logger.show(message: String) = this.warn("\u001B[32m$message\u001B[0m")