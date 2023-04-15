import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.Copy

group = "io.github.haruhisa_enomoto"
version = "0.2.1"

allprojects {
    repositories {
        mavenCentral()
    }
}

val platforms = listOf("win", "mac", "others")

tasks {
    val copyFrontendBuild by registering(Copy::class) {
        group = "root"
        description = "Copy frontend build files to the server resources directory"

        dependsOn(":fd-applet-front:npmBuild")
        from("${project.rootDir}/fd-applet-front/build")
        into("${project.rootDir}/fd-applet-server/src/main/resources/files")
    }

    val buildServerJar by registering(Copy::class) {
        group = "root"
        description = "Build the fat jar for the server"

        dependsOn(copyFrontendBuild, ":fd-applet-server:buildFatJar")

        from("fd-applet-server/build/libs/fd-applet-server-all.jar")
        into("${project.rootDir}/build")
        rename { "fd-applet-fat.jar" }
    }

    platforms.forEach { platform ->
        val copyJarToDist = register<Copy>("copyJarTo${platform.capitalize()}Dist") {
            description = "Copy the fat jar to the $platform distribution directory"

            dependsOn(buildServerJar)
            from("build/fd-applet-fat.jar")
            into("dist/fd-applet-$platform/lib")
        }

        register("update${platform.capitalize()}DistVer") {
            description = "Update the version file for the $platform distribution"

            doLast {
                file("dist/fd-applet-$platform/lib/fd-applet-version.txt").writeText("$version\n")
            }
        }

        register<Zip>("zip${platform.capitalize()}") {
            description = "Create a zip archive for the $platform distribution"
            dependsOn(copyJarToDist, "update${platform.capitalize()}DistVer")

            archiveFileName.set("fd-applet-$platform.zip")
            from("dist/fd-applet-$platform")
            destinationDirectory.set(file("dist"))
        }
    }

    val distTask by registering {
        group = "root"
        description = "Create zip archives for all distributions: Windows, macOS, and others"

        dependsOn(platforms.map { "zip${it.capitalize()}" })
    }
}
