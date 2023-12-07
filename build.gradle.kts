import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
  id("org.springframework.boot") version "3.0.6"
  id("io.spring.dependency-management") version "1.1.0"
  id("com.palantir.docker") version "0.34.0" // 이 플러그인을 추가
  kotlin("jvm") version "1.7.22"
  kotlin("plugin.spring") version "1.7.22"
}

group = "app.klock"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-rsocket")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.security:spring-security-config")
  implementation("io.jsonwebtoken:jjwt-api:0.11.5")
  implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
  implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("io.asyncer:r2dbc-mysql:1.0.4")
  implementation("com.nimbusds:nimbus-jose-jwt:9.31")
  implementation("software.amazon.awssdk:s3:2.17.0")
  implementation("io.github.microutils:kotlin-logging:2.0.11")
  runtimeOnly("io.r2dbc:r2dbc-h2")
  runtimeOnly("com.h2database:h2")
  runtimeOnly("org.mariadb:r2dbc-mariadb:1.1.3")
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("io.mockk:mockk:1.+")
}

tasks.named<BootBuildImage>("bootBuildImage") {
  imageName.set("${System.getenv("ECR_REGISTRY")}/klock-repository:${System.getenv("GITHUB_SHA")}")
  val currentEnvironment = mutableMapOf<String, String>()
  currentEnvironment["SPRING_PROFILES_ACTIVE"] = System.getenv("SPRING_PROFILES_ACTIVE") ?: ""
  currentEnvironment["SPRING_R2DBC_URL"] = System.getenv("DB_URL") ?: ""
  currentEnvironment["SPRING_R2DBC_USERNAME"] = System.getenv("DB_USERNAME") ?: ""
  currentEnvironment["SPRING_R2DBC_PASSWORD"] = System.getenv("DB_PASSWORD") ?: ""
  environment.set(currentEnvironment)
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
