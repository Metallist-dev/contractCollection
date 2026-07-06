plugins {
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "9.5.0"
    id("org.openapi.generator") version "7.14.0"
    id("org.hidetake.swagger.generator") version "2.19.2"
    id("java")
}

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

group = "de.metallistdev"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
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
//    compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
//    annotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)

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

configurations {
    all {
        exclude(group = "com.github.joschi.jackson", module = "jackson-datatype-threetenbp")
        exclude(group = "com.fasterxml.jackson.datatype", module = "jackson-datatype-threetenbp")
    }
}

tasks.withType<Test> {
    useTestNG()
}
