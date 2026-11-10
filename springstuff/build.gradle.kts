plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.spring") version "2.4.20"
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jetbrains.exposed.plugin") // Exposed Gradle plugin, resolved from the composite jb-exposed build
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "springstuff"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

// CXF wsdl2java tooling, only used at build time by the wsdl2java task below.
val cxf by configurations.creating {
    isCanBeConsumed = false
}

val generatedCxfDir = layout.buildDirectory.dir("generated/cxf")

dependencies {
    implementation("org.springframework.boot:spring-boot-h2console")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    // Exposed — resolved from the local jb-exposed checkout via the composite build in settings.gradle.kts.
    // 1.5.1 is only the dependency notation; the included build substitutes these with its current sources.
    implementation("org.jetbrains.exposed:exposed-spring-boot4-data-starter:1.5.1")
    implementation("org.jetbrains.exposed:exposed-java-time:1.5.1")

    // Runtime for the CXF-generated SOAP client classes (jakarta.xml.ws/jaxb), versions from the Boot BOM.
    implementation("jakarta.xml.bind:jakarta.xml.bind-api")
    implementation("com.sun.xml.bind:jaxb-impl")
    implementation("jakarta.xml.ws:jakarta.xml.ws-api")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    cxf("org.apache.cxf:cxf-tools-wsdlto-core:4.0.0")
    cxf("org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:4.0.0")
    cxf("org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb:4.0.0")
}

// Exposed migration generator (./gradlew generateMigrations):
// scans the table objects in the given packages and diffs them against the reference database,
// writing versioned SQL scripts to src/main/resources/db/migration (Flyway's default location).
exposed {
    migrations {
        tablesPackages.set(listOf("org.example.springstuff.domain"))
        // The reference "current schema" is the same file-based H2 the app runs on: Flyway applies the
        // generated scripts to it at startup, so the next run produces exactly the incremental diff.
        // Workflow: change a Table object -> ./gradlew generateMigrations -> restart the app.
        databaseUrl.set("jdbc:h2:file:./data/springstuff;AUTO_SERVER=TRUE")
        databaseUser.set("sa")
        databasePassword.set("")
    }
}

val wsdl2java by tasks.registering(JavaExec::class) {
    group = "build"
    description = "Generates SOAP client classes from src/main/resources/countries.wsdl (Apache CXF wsdl2java)."
    mainClass.set("org.apache.cxf.tools.wsdlto.WSDLToJava")
    val wsdlFile = layout.projectDirectory.file("src/main/resources/countries.wsdl")
    args(
        "-p", "com.example.soap.countries",
        "-wsdlLocation", "classpath:countries.wsdl",
        "-d", generatedCxfDir.get().asFile.absolutePath,
        wsdlFile.asFile.absolutePath,
    )
    classpath = cxf
    inputs.file(wsdlFile)
    outputs.dir(generatedCxfDir)
}

sourceSets {
    main {
        java {
            srcDir(generatedCxfDir)
        }
    }
}

tasks.named("compileKotlin") { dependsOn(wsdl2java) }
tasks.named("compileJava") { dependsOn(wsdl2java) }

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
