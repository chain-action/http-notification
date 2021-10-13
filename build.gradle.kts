import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.5.0"
    application
}

group = "su.update"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven(url = "https://jitpack.io")
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    google()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation("com.github.uchuhimo:konf:master-SNAPSHOT")
    implementation ("com.xenomachina:kotlin-argparser:2.0.7")

    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation( group="org.json", name="json", version= "20200518")
    implementation("io.github.microutils:kotlin-logging:2.0.11")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("org.jetbrains.exposed", "exposed-core", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.28.1")
//    implementation("org.jetbrains.exposed:exposed:0.17.7")
    implementation( group="mysql", name="mysql-connector-java", version= "8.0.22")

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:1.6.4")
    implementation("io.ktor:ktor-server-netty:1.6.4")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    implementation ("io.ktor:ktor-serialization:1.6.4")
    implementation("io.ktor:ktor-gson:1.6.4")

    implementation ("com.github.javafaker:javafaker:1.0.2")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation ("org.apache.kafka:kafka-clients:2.8.0")

    implementation ("com.github.papsign:Ktor-OpenAPI-Generator:-SNAPSHOT")

    implementation ("com.influxdb:influxdb-client-kotlin:2.1.0")
    implementation ("com.influxdb:influxdb-client-flux:2.1.0")

//    implementation ("redis.clients:jedis:2.9.0")
//    implementation ("redis.clients:jedis:2.10.2")
    implementation ("redis.clients:jedis:3.6.0")

    implementation("it.skrape:skrapeit:1.1.5")

//    implementation ("com.localebro:okhttpprofiler:1.0.8")
//    implementation ("com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor")
//    implementation ("com.localebro.okhttpprofiler.OkHttpProfilerInterceptor")
//    implementation(project(":ktor-swagger"))
//    implementation("io.ktor:ktor-gson:$ktor_version")
}

//kotlin.sourceSets["main"].kotlin.srcDirs("src")
//kotlin.sourceSets["test"].kotlin.srcDirs("test")

//sourceSets["main"].resources.srcDirs("resources")
//sourceSets["test"].resources.srcDirs("testresources")

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "MainKt"
}