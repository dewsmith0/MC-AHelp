allprojects {
    repositories {
        mavenCentral()
    }
}
plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.16-SNAPSHOT" apply false
}