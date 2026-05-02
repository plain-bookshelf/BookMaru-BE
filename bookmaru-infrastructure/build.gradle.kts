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
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("jakarta.annotation:jakarta.annotation-api")

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
    implementation("com.google.firebase:firebase-admin:9.4.0") {
        exclude(group = "org.slf4j", module = "slf4j-nop")
    }

    // config
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // test
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}

val generated = file("src/main/generated")
// querydsl QClass 파일 생성 위치를 지정
tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generated)
}
// kotlin source set 에 querydsl QClass 위치 추가
sourceSets {
    main {
        kotlin.srcDirs += generated
    }
}
// gradle clean 시에 QClass 디렉토리 삭제
tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
    }
}
// (Querydsl 설정부 추가 - end)

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
