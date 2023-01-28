# ff7-to-collada

Translates Final Fantasy VII models from PC format to COLLADA.

NOTE: This app is buggy and has been abandoned.

If you are interested in translating FF7 models to other formats, please note:

- If you are interested in COLLADA:
  - This project has many bugs and might never be finished.
  - Consider glTF instead. It's a modern open-source friendly format.
  - You can get glTF from the kujata project!

- If you are interested in glTF:
  - Look at the kujata project!

- If you are interested in JSON:
  - Consider glTF instead. glTF is a combination of JSON and binary.
  - If you are instead looking for an "intermediate" format:
    - Option 1: Wait for the kujata project to output intermediate JSON.
      - Option 1b: If this never happens, try forking kujata and doing it yourself.
    - Option 2: You can attempt to use this project, but it's very buggy.

- If you are interested in FBX:
  - Look at the ff7-to-fbx project.

# History

- In the early days, COLLADA and FBX were the two most popular formats.
  - COLLADA was more open source friendly. FBX was more proprietary.

- ff7-field-to-collada was created to translate to COLLADA.

- The first half of the translation was leveraged to help with FBX as well:
  - PC format -> ff7-to-collada (buggy) -> Java/JSON -> ff7-to-collada -> COLLADA
  - PC format -> ff7-to-collada (buggy) -> Java/JSON -> ff7-to-fbx -> FBX
    - See `JsonExporter.java`
      - But, this is buggy and will be replaced by kujata, see below

- glTF emerged as a mature and popular open-source format, which changed everything.
  - The kujata project was re-born with glTF as its mission.
  - The kujata project translates more accurately than this project.
  - The kujata project supports not only field models but battle models.
  - The kujata project is being used in other projects in the FF7 modding community.

- FBX is still very popular, so:
  - The kujata project is being enhanced now so that ff7-to-fbx can leverage it:
  - PC format -> kujata -> JSON -> ff7-to-fbx -> FBX

If after all that, you still want to try to get this project running, read on.

# Environment setup

## Install Java 8 SDK for Windows 64-bit

- Get from: https://www.oracle.com/java/technologies/downloads/#java8-windows
- Install using Windows installer

## Install Maven

- Get from: https://maven.apache.org/download.cgi
- Link: apache-maven-3.8.7-bin.zip
- Unzip to `C:\opt` to create `C:\opt\apache-maven-3.8.7`
- Add `mvn.cmd` to path

## Configure environment vars

- Find where Java JDK was installed:
  - Locate `javac.exe`, which should be located at...
  - `C:\Program Files\Java\jdk1.8.0_361\bin\javac.exe` or similar
  - `C:\Program Files\Java\jdk1.8.0_361` is therefore the JDK home

- Window key, Edit the system environment variables
- Environment variables...
- Under System variables, New...
  - Set the `JAVA_HOME` environment variable:
    - Variable name: `JAVA_HOME`
    - Variable value: `C:\Program Files\Java\jdk1.8.0_361`
- Under System variables, select "Path", Edit...
  - Add `C:\opt\apache-maven-3.8.7\bin` to the end

## Verify Maven and Java versions

- new Command Window
- run: `mvn --version`
- output should include:
  - `Apache Maven 3.8.7 (b89d5959fcde851dcb1c8946a785a163f14e1e29)`
  - `Maven home: C:\opt\apache-maven-3.8.7`
  - `Java version: 1.8.0_361, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk1.8.0_361\jre`

# Build from command line

- go to this project's root folder
- `mvn compile`

# Run

This is left as an exercise for the reader.

- `JsonExporter.java` reads model files and exports JSON
- `ColladaExporter.java` goes further and exports COLLADA
