import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.Copy


allprojects {
    group = "io.github.haruhisa_enomoto"
    version = "0.3.2"

    repositories {
        mavenCentral()
    }
}

tasks.register("runServer") {
    group = "main"
    description = "Run the server without the frontend"

    dependsOn(":fd-applet-server:runWithoutFrontend")
}

tasks.register("runFront") {
    group = "main"
    description = "Run the frontend"

    dependsOn(":fd-applet-front:npmStart")
}

tasks.register("buildServerJar", Copy::class) {
    group = "main"
    description = "Build the fat jar for the server"

    dependsOn(":fd-applet-server:buildFatJar")
    from("fd-applet-server/build/libs/fd-applet-server-all.jar")
    into("${project.rootDir}/build")
    rename { "fd-applet-fat.jar" }
}

val platforms = listOf("win", "mac", "others")

platforms.forEach { platform ->
    tasks.register("copy${platform.replaceFirstChar(Char::titlecase)}ServerJar", Copy::class) {
        group = "copy"
        description = "Copy the fat jar to the $platform distribution directory"

        dependsOn("buildServerJar")
        from("build/fd-applet-fat.jar")
        into("dist/fd-applet-$platform/lib")
    }

    tasks.register("update${platform.replaceFirstChar(Char::titlecase)}DistVer") {
        description = "Update the version file for the $platform distribution"

        doLast {
            file("dist/fd-applet-$platform/lib/fd-applet-version.txt").writeText("$version\n")
        }
    }

    tasks.register("zip${platform.replaceFirstChar(Char::titlecase)}", Zip::class) {
        description = "Create a zip archive for the $platform distribution"
        dependsOn("copy${platform.replaceFirstChar(Char::titlecase)}ServerJar", "update${platform.replaceFirstChar(Char::titlecase)}DistVer")

        archiveFileName.set("fd-applet-$platform.zip")
        from("dist/fd-applet-$platform")
        destinationDirectory.set(file("dist"))
    }
}

tasks.register("distAll", Copy::class) {
    group = "main"
    description = "Create all distribution archives"

    dependsOn(platforms.map { "zip${it.replaceFirstChar(Char::titlecase)}" })
}
