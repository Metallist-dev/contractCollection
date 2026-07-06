plugins {
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.14.0"
    id("org.hidetake.swagger.generator") version "2.19.2"
    id("java")
    id("jacoco")
}

group = "de.metallist"
version = "0.0.3-SNAPSHOT"

// VARIABLES
val springVersion = "4.1.0"
val testNGVersion = "7.12.0"
val jacksonVersion = "3.2.0"
val openAPIUIVersion = "1.8.0"
val openApiGeneratorVersion ="7.14.0"
val lombokVersion = "1.18.46"
val swaggerUiVersion = "5.32.8"
val mockitoVersion = "0.5.4"
val jacocoVersion = "0.8.15"
// VARIABLES

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks.bootRun.configure {
    systemProperty("spring.profiles.active", "dev")
}

repositories {
    mavenCentral()
}

configurations {
    all {
        exclude(module = "spring-boot-starter-logging")
        exclude(module = "logback-classic")
    }
}

openApiGenerate {
    generatorName.set("html2")
    inputSpec.set("$rootDir/Backend/src/main/resources/static/contractcollection-api.yaml")
    outputDir.set("${layout.buildDirectory.get()}/docs/openapi")
}

openApiValidate {
    inputSpec.set("$rootDir/Backend/src/main/resources/static/contractcollection-api.yaml")
}

tasks.generateSwaggerUI.configure {
    inputFile = file("$rootDir/Backend/src/main/resources/static/contractcollection-api.yaml")
    outputDir = file("${layout.buildDirectory.get()}/docs/swaggerUI-Backend")
    doLast {
        copy {
            from("$rootDir/Backend/src/main/resources/static/swaggerUI.html")
            into(outputDir)
        }
    }
}

tasks.test.configure {
    systemProperty("spring.profiles.active", "prod")

    testLogging {
        showStandardStreams = true
    }

    useTestNG() {
        failFast = false
        useDefaultListeners = true
        preserveOrder = true
        groupByInstances = true
        suites("src/test/java/de/metallistdev/contractcollection/application/testsuite.xml")
    }

    reports.html.required.set(false)

    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = jacocoVersion
    reportsDirectory.set(layout.buildDirectory.dir("docs/jacoco"))
}

tasks.jacocoTestReport {
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation.set(layout.buildDirectory.dir("docs/jacoco/jacocoHtml"))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.3".toBigDecimal()
            }
        }
    }
}



dependencies {
    implementation(project(":Commons"))

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools:$springVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot
    implementation("org.springframework.boot:spring-boot:$springVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-ui
    implementation("org.springdoc:springdoc-openapi-ui:$openAPIUIVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.openapitools/openapi-generator-gradle-plugin
    implementation ("org.openapitools:openapi-generator-gradle-plugin:$openApiGeneratorVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/tools.jackson.core/jackson-core
    implementation("tools.jackson.core:jackson-core:$jacksonVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/tools.jackson.core/jackson-databind
    implementation("tools.jackson.core:jackson-databind:$jacksonVersion")

    // MIT License
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-configuration-processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springVersion")


    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.webjars/swagger-ui
    swaggerUI("org.webjars:swagger-ui:$swaggerUiVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    {
        exclude(group = "junit", module = "junit")
    }

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