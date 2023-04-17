# FD Applet - an applet for Finite-Dimensional algebras

[**Online Demo**](https://fd-applet.dt.r.appspot.com/)

FD Applet is a web application composed of:

- a frontend (`fd-applet-front`) built with React, TypeScript, and Material-UI, and
- a server (`fd-applet-server`) developed using Kotlin and Ktor, functioning as a calculation and API server.

The project is organized as a Gradle multi-project.

See [FD Applet](https://haruhisa-enomoto.github.io/fd-applet/) for details about usage and installation for users.

## Prerequisites

- [Java JDK 17](https://adoptium.net/)

## Getting Started

1. Clone the repository.

2. In the project root, run the appropriate commands based on your platform:

   - Windows: `gradlew.bat <task>`
   - Others: `./gradlew <task>`

Replace `<task>` with one of the following (which are in the `Main` group):

- `runFrontend`: Run the frontend
- `runServer`: Run the server (without building the frontend)
- `buildServerJar`: Build the server fat JAR in `build/fd-applet-fat.jar`
- `distAll`: Create distribution archives for all platforms

During development, you'll typically run `runFrontend` and `runServer` simultaneously.
