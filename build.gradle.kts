buildscript {
    repositories {
        mavenCentral()
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = "tech.endec"

    tasks.withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}