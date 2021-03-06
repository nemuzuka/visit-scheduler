import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.3.BUILD-SNAPSHOT"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"

    jacoco
    kotlin("kapt") version "1.3.61"
    id("idea")
}

group = "net.jp.vss"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

val ktlint: Configuration by configurations.creating

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    val ktlintVersion: String by project
    ktlint("com.pinterest:ktlint:$ktlintVersion")

    implementation("com.google.guava:guava:28.1-jre")
    val postgresVersion: String by project
    implementation("org.postgresql:postgresql:$postgresVersion")

    testImplementation ("com.h2database:h2")

    val flywayVersion: String by project
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    testImplementation("org.flywaydb.flyway-test-extensions:flyway-dbunit-test:6.1.0")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter")

    // doma
    val domaVersion: String by project
    implementation("org.seasar.doma.boot:doma-spring-boot-starter:1.1.1")
    implementation("org.seasar.doma:doma:$domaVersion")
    kapt("org.seasar.doma:doma:$domaVersion")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JSONassert
    testImplementation ("org.skyscreamer:jsonassert:1.5.0")

    // spring 関連
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


// bootrun
tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    if (project.hasProperty("personal")) {
        systemProperties["spring.profiles.active"] = "personal"
    }
}

idea.module.inheritOutputDirs = true

// front 側の build
tasks.register<Exec>("npmInstall") {
    workingDir("front")
    commandLine("npm", "install")
}

tasks.register<Exec>("buildFront") {
    workingDir("front")
    commandLine("npm", "run", "build")
}

tasks.register<Exec>("lintFront") {
    workingDir("front")
    commandLine("npm", "run", "lint")
}

tasks.register<Exec>("testFront") {
    workingDir("front")
    commandLine("npm", "run", "test")
}

if(System.getenv("CI") != null) {
    tasks.named("processResources") {
        dependsOn("buildFront")
    }
}

// ktlint
tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

tasks.named("check") {
    dependsOn(ktlint)
}

// ktlint
tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}

// リソースファイルの出力先設定
val compileKotlin: KotlinCompile by tasks
kapt {
    arguments {
        arg("doma.resources.dir", compileKotlin.destinationDir)
    }
}

// doma 関連
tasks.register<Sync>("copyDomaResources") {
    from("src/main/resources")
    into(compileKotlin.destinationDir)
    include("doma.compile.config")
    include("META-INF/**/*.sql")
    include("META-INF/**/*.script")
}


tasks.withType<KotlinCompile> {
    dependsOn(tasks.getByName("copyDomaResources")) // dooma のリソースを移動してから実施
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

// CI の report 用
jacoco {
    toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
    }
}
