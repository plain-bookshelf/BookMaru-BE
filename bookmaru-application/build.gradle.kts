dependencies {
    implementation ("org.apache.commons:commons-lang3:3.18.0")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
