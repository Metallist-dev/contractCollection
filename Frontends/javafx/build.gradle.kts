plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.5"
}

// VARIABLES
val controlsFXVersion = "11.2.3"
val formsFXVersion = "11.6.0"
val ikonliVersion = "12.4.0"
val bootstrapFxCoreVersion = "0.4.0"
val kotlinVersion = "2.4.0"
val jacksonVersion = "3.2.0"
val okhttpVersion = "5.4.0"
val slf4jVersion = "2.0.18"
val testNGVersion = "7.12.0"
val mockitoVersion = "0.5.4"
val jacocoVersion = "0.8.15"
// VARIABLES

group = "de.metallistdev"
version = "0.0.1-SNAPSHOT"

application {
    mainModule.set("de.metallistdev.contractcollection.javafx")
    mainClass.set("de.metallistdev.contractcollection.javafx.ContractCollectionApplication")
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
    implementation("org.controlsfx:controlsfx:$controlsFXVersion")

    // Apache 2.0
    // https://mvnrepository.com/artifact/com.dlsc.formsfx/formsfx-core
    implementation("com.dlsc.formsfx:formsfx-core:$formsFXVersion"){
        exclude(group = "org.openjfx")
    }

    // https://mvnrepository.com/artifact/org.kordamp.ikonli/ikonli-javafx
    implementation("org.kordamp.ikonli:ikonli-javafx:$ikonliVersion")

    // https://mvnrepository.com/artifact/org.kordamp.bootstrapfx/bootstrapfx-core
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:$bootstrapFxCoreVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/tools.jackson.core/jackson-core
    implementation("tools.jackson.core:jackson-core:$jacksonVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/tools.jackson.core/jackson-databind
    implementation("tools.jackson.core:jackson-databind:$jacksonVersion")

    // MIT License
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
//    compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
//    annotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")

    // MIT License
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")


    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.testng/testng
    testImplementation("org.testng:testng:$testNGVersion")

    // MIT License
    // https://mvnrepository.com/artifact/org.mockito/mockito-testng
    testImplementation("org.mockito:mockito-testng:$mockitoVersion")

    // EPL 2.0
    // https://mvnrepository.com/artifact/org.jacoco/org.jacoco.agent
    testImplementation("org.jacoco:org.jacoco.agent:$jacocoVersion")
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
