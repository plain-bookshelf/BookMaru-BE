dependencies {
    implementation ("org.springframework:spring-tx:6.2.11")
    implementation ("org.apache.commons:commons-lang3:3.18.0")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}