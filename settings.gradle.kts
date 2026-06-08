
include("server")
include("proto")
include("client")

//project("client").name = "mc-ahelp-client";
pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
    }

}

rootProject.name = "MC-AHelp"
