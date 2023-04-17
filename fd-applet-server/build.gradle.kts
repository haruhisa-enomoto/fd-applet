plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("io.ktor.plugin") version "2.2.4"
    id("com.github.ben-manes.versions") version "0.46.0"
    application
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.ktor:ktor-server-core:2.2.4")
    implementation("io.ktor:ktor-server-netty:2.2.4")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.2.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.2.4")
    implementation("io.ktor:ktor-server-status-pages:2.2.4")
    implementation("ch.qos.logback:logback-classic:1.4.6")
}

val serverClassName = "${group}.server.app.ApplicationKt"

application {
    mainClass.set(serverClassName)
}

kotlin {
    jvmToolchain(17)
}

tasks.register("runWithoutFrontend", JavaExec::class) {
    group = "run"
    description = "Run the server without the frontend"

    dependsOn("classes")
    mainClass.set(serverClassName)
    classpath(sourceSets["main"].runtimeClasspath)
}

tasks.register("copyFrontendBuild", Copy::class) {
    group = "copy"
    description = "Copy frontend build files to the server resources directory"

    mustRunAfter(":fd-applet-front:npmBuild")

    from("${project.rootDir}/fd-applet-front/build")
    into("${project.rootDir}/fd-applet-server/src/main/resources/files")
}

tasks.processResources {
    inputs.files(tasks.named("copyFrontendBuild").map { it.outputs.files })
}

tasks.buildFatJar {
    dependsOn(":fd-applet-front:npmBuild", "copyFrontendBuild")
}


tasks.register("makeNakayamaDataset", Jar::class) {
    group = "jar"
    description = "Run makeNakayamaDataset"

    manifest {
        attributes["Main-Class"] = "${group}.makeNakayamaDataset.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}