import java.util.Properties
import java.io.File
import java.io.ByteArrayOutputStream

// Function to load properties from a .env file
fun loadEnv(fileName: String = ".env"): Properties {
    val props = Properties()
    val envFile = File(fileName)
    if (envFile.exists()) {
        props.load(envFile.reader())
    }
    return props
}

abstract class BuildScriptExtension {
    @get:Inject
    abstract val execOperations: ExecOperations
}

fun getGitShortSha(execOps: ExecOperations): String {
    val env = loadEnv()
    val gitSha: String = env.getProperty("GIT_SHA", "null")

    val output = ByteArrayOutputStream()
    execOps.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = output
    }
    return if (gitSha !== "null") gitSha else output.toString().trim()
}

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.3"
}

extensions.create("buildScriptExtension", BuildScriptExtension::class)

group = "cz.studioart"
version = getGitShortSha(extensions.getByType<BuildScriptExtension>().execOperations)

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("cz.studioart.GodaddyBotApplication")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.konghq:unirest-java:3.14.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("com.jakewharton.picnic:picnic:0.7.0")
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// disable build without dependencies
tasks.jar {
    enabled = false
}
