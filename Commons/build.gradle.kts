plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("io.freefair.lombok") version "8.6"
    id("org.openapi.generator") version "6.3.0"
    id("org.hidetake.swagger.generator") version "2.19.2"
    id("java")
}

// VARIABLES
val springVersion = "3.2.5"
val testNGVersion = "7.10.2"
val jacksonVersion = "2.17.0"
val openAPIUIVersion = "1.8.0"
val openApiGeneratorVersion ="7.5.0"
val lombokVersion = "1.18.32"
val swaggerUiVersion = "5.17.2"
val mockitoVersion = "0.5.2"
val jacocoVersion = "0.8.12"
// VARIABLES

group = "de.metallistdev"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
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
    developmentOnly(group = "org.springframework.boot", name = "spring-boot-devtools", version = springVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot
    implementation(group = "org.springframework.boot", name = "spring-boot", version = springVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-actuator", version = springVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-web", version = springVersion)
    {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-ui
    implementation(group = "org.springdoc", name ="springdoc-openapi-ui", version = openAPIUIVersion)

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.openapitools/openapi-generator-gradle-plugin
    implementation (group = "org.openapitools", name = "openapi-generator-gradle-plugin", version = openApiGeneratorVersion)

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
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-configuration-processor
    annotationProcessor(group = "org.springframework.boot", name = "spring-boot-configuration-processor", version = springVersion)


    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.webjars/swagger-ui
    swaggerUI("org.webjars:swagger-ui:$swaggerUiVersion")

    // Apache License 2.0
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test", version = springVersion)
    {
        exclude(group = "junit", module = "junit")
    }

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
    useTestNG()
}
