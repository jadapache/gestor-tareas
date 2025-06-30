// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.androidx.room) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    id("com.google.devtools.ksp") version "2.2.0-2.0.2" apply false
    id("org.sonarqube") version "4.4.1.3373"
}

sonarqube {
    properties {
        property("sonar.projectKey", "jadapache_gestor-tareas") // Reemplaza por tu clave real
        property("sonar.organization", "jadapache") // Reemplaza por tu organización
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", "caaaa3bfacc4199efc7ed2f6bfce2fb3447b9488") // Reemplaza por tu token
        property("sonar.language", "kotlin")
        property("sonar.sources", "app/src/main/java")
        property("sonar.tests", "app/src/test/java,app/src/androidTest/java")
        property("sonar.java.binaries", "app/build")
    }
}