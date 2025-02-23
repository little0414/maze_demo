plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
    java
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starter for building web applications
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Boot Starter for Spring Data JPA (for database persistence)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // H2 Database for development/testing (embedded)
    runtimeOnly("com.h2database:h2")

    // Testing support
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
