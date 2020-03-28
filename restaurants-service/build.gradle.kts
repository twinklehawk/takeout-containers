import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(enforcedPlatform(project(":platform")))
    implementation(project(":restaurants-api"))
    implementation("net.plshark.users:users-client")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("io.projectreactor:reactor-test")
}

java { sourceCompatibility = JavaVersion.VERSION_1_8 }
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
tasks.withType<Test> {
    useJUnitPlatform()

    val props = mutableMapOf<String, String>()
    if (System.getProperties().getProperty("runIntTests") == "true")
        props["runIntTests"] = "true"
    options {
        systemProperties(props)
    }
}
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
        csv.isEnabled = false
    }
}
