dependencies {
    implementation(project(":bookmaru-application"))

    // persistence
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    runtimeOnly("org.postgresql:postgresql")

    // security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    // web
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // aop
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // config
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // test
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-test")
}