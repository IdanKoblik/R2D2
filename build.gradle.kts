import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.time.LocalDate

plugins {
    id("java")
    id("idea")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.0"
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "dev.idank"
version = figureVersion()

fun figureVersion(): String {
    return (if (System.getenv("VERSION") == null) "dev-SNAPSHOT" else System.getenv("VERSION"))
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(findProperty("idea.c.version").toString())

        bundledPlugin("com.intellij.java")
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("org.jetbrains.plugins.github")
        bundledPlugin("org.jetbrains.plugins.gitlab")

        pluginVerifier()
        zipSigner()

        testFramework(TestFrameworkType.Platform)
    }

    implementation(platform("com.squareup.okhttp3:okhttp-bom:${findProperty("okhttp.version")}"))
    implementation("com.squareup.okhttp3:okhttp")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${findProperty("jackson.version")}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${findProperty("jackson.version")}")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${findProperty("kotlin.version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${findProperty("kotlin.version")}")

    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.junit.jupiter:junit-jupiter:${findProperty("junit.version")}")
    testImplementation("com.squareup.okhttp3:mockwebserver:${findProperty("okhttp.version")}")
    testImplementation(kotlin("test"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("242")
        untilBuild.set("243.*")
    }

    license {
        header = project.resources.text.fromFile(rootProject.file("LICENSE.md"))

        properties {
            "year" to LocalDate.now().year
        }
    }

    check {
        dependsOn("test", "updateLicenses")
    }

    test {
        useJUnitPlatform()

        systemProperty("gitlab.user", project.findProperty("gitlab.user") ?: System.getenv("GIT_USER"))
        systemProperty("gitlab.token", project.findProperty("gitlab.token") ?: System.getenv("GL_AUTH"))

        systemProperty("github.user", project.findProperty("github.user") ?: System.getenv("GIT_USER"))
        systemProperty("github.token", project.findProperty("github.token") ?: System.getenv("GH_AUTH"))
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
