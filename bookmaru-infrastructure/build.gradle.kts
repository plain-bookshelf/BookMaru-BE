dependencies {
    implementation(project(":bookmaru-application"))

    // aws
    implementation(platform("software.amazon.awssdk:bom:2.42.41"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:url-connection-client")

    // persistence
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("org.apache.httpcomponents.core5:httpcore5")
    runtimeOnly("org.postgresql:postgresql")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // query dsl
    implementation("io.github.openfeign.querydsl:querydsl-jpa:6.12")

    ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:6.12")
    compileOnly("jakarta.persistence:jakarta.persistence-api")
    compileOnly("jakarta.annotation:jakarta.annotation-api")

    // security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // web
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // aop
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // firebase
    implementation("com.google.firebase:firebase-admin:9.5.0") {
        exclude(group = "org.slf4j", module = "slf4j-nop")
    }

    // config
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")

    // test
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}
kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
