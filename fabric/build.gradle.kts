import org.gradle.api.attributes.Attribute

plugins {
    id("multiloader-loader")
    id("net.fabricmc.fabric-loom")
}

val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_version: String by project
val mod_id: String by project

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)

dependencies {
    add("minecraft", "com.mojang:minecraft:$minecraft_version")
    add("implementation", "net.fabricmc:fabric-loader:$fabric_loader_version")
    add("implementation", "net.fabricmc.fabric-api:fabric-api:$fabric_version")
}

loom {
    val aw = project(":common").file("src/main/resources/${mod_id}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
}

// Implement mcgradleconventions loader attribute
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements", "includeInternal", "modCompileClasspath").forEach { variant ->
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "fabric")
        }
    }
}
sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "fabric")
            }
        }
    }
}

