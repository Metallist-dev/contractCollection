plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.0.1"
}

// VARIABLES
val controlsFXVersion = "11.2.1"
val formsFXVersion = "11.6.0"
val ikonliVersion = "12.3.1"
val bootsrapFxCoreVersion = "0.4.0"
val kotlinVersion = "1.9.10"
val jacksonVersion = "2.17.0"
//val lombokVersion = "1.18.32"
val okhttpVersion = "4.12.0"
val slf4jVersion = "2.0.13"
val testNGVersion = "7.9.0"
val mockitoVersion = "0.5.2"
val jacocoVersion = "0.8.12"
// VARIABLES

group = "de.metallistdev"
version = "0.0.1-SNAPSHOT"

application {
    mainModule = "de.metallistdev.contractcollection.javafx"
    mainClass = "de.metallistdev.contractcollection.javafx.ContractCollectionApplication"
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}

jlink {
    launcher {
        name = "ContractCollectionFX"
    }
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Commons"))

    // BSD 3-clause
    // https://mvnrepository.com/artifact/org.controlsfx/controlsfx
    implementation(group = "org.controlsfx", name = "controlsfx", version = controlsFXVersion)

    // Apache 2.0
    // https://mvnrepository.com/artifact/com.dlsc.formsfx/formsfx-core
    implementation(group = "com.dlsc.formsfx", name = "formsfx-core", version = formsFXVersion){
        exclude(group = "org.openjfx")
    }

    // https://mvnrepository.com/artifact/org.kordamp.ikonli/ikonli-javafx
    implementation(group = "org.kordamp.ikonli", name = "ikonli-javafx", version = ikonliVersion)

    // https://mvnrepository.com/artifact/org.kordamp.bootstrapfx/bootstrapfx-core
    implementation(group = "org.kordamp.bootstrapfx", name = "bootstrapfx-core", version = bootsrapFxCoreVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version = kotlinVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core", version = jacksonVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)

    // MIT License
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
//    compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
//    annotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = okhttpVersion)

    // MIT License
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    runtimeOnly(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = kotlinVersion)


    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.testng/testng
    testImplementation(group = "org.testng", name = "testng", version = testNGVersion)

    // MIT License
    // https://mvnrepository.com/artifact/org.mockito/mockito-testng
    testImplementation(group = "org.mockito", name = "mockito-testng", version = mockitoVersion)

    // EPL 2.0
    // https://mvnrepository.com/artifact/org.jacoco/org.jacoco.agent
    testImplementation(group = "org.jacoco", name = "org.jacoco.agent", version = jacocoVersion)
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
    }

    useTestNG {
        failFast = false
        useDefaultListeners = true
        preserveOrder = true
        groupByInstances = true
    }
}
