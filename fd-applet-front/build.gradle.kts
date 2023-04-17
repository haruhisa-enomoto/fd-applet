import com.github.gradle.node.npm.proxy.ProxySettings
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "3.5.1" 
}

node {
    version.set("18.16.0")
    npmVersion.set("")
    yarnVersion.set("")
    npmInstallCommand.set("install")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    workDir.set(file("${project.projectDir}/.cache/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/.cache/npm"))
    yarnWorkDir.set(file("${project.projectDir}/.cache/yarn"))
    nodeProjectDir.set(file("${project.projectDir}"))
    nodeProxySettings.set(ProxySettings.SMART)
}


tasks.register("updatePackageJsonVersion") {
    description = "Update package.json version to match the project version"

    doLast {
        val packageJsonFile = java.nio.file.Paths.get(project.projectDir.path, "package.json").toFile()
        val packageJson = packageJsonFile.readText()
        val updatedPackageJson = packageJson.replace(Regex("\"version\":\\s*\"(.*?)\""), "\"version\": \"${project.version}\"")
        packageJsonFile.writeText(updatedPackageJson)
    }
}

tasks.npmInstall {
    dependsOn("updatePackageJsonVersion")
}

tasks {
    register<NpmTask>("npmStart") {
        dependsOn("npmInstall")
        npmCommand.set(listOf("run", "start"))
    }
    register<NpmTask>("npmBuild") {
        dependsOn("npmInstall")
        npmCommand.set(listOf("run", "build"))
    }
    register<NpmTask>("npmLint") {
        dependsOn("npmInstall")
        npmCommand.set(listOf("run", "lint"))
    }
    register<NpmTask>("npmLintFix") {
        dependsOn("npmInstall")
        npmCommand.set(listOf("run", "lint:fix"))
    }
}