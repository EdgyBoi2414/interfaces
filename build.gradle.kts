import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.diffplug.gradle.spotless.SpotlessTask
import net.kyori.indra.IndraCheckstylePlugin
import net.kyori.indra.IndraPlugin
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
plugins {
    alias(libs.plugins.indra)
    alias(libs.plugins.indra.publishing) apply false
    alias(libs.plugins.indra.checkstyle) apply false

    // Kotlin plugin prefers to be applied to parent when it's used in multiple sub-modules.
    kotlin("jvm") version "1.9.25" apply false
    id("com.diffplug.spotless") version "6.18.0"
    `maven-publish`
}

group = "org.incendo.interfaces"
version = "1.1.0"
description = "A builder-style user interface library."

subprojects {
    version = rootProject.version

    apply<IndraPlugin>()
    apply<IndraCheckstylePlugin>()
    apply<SpotlessPlugin>()
    apply<MavenPublishPlugin>()

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnlyApi(rootProject.libs.checker.qual)
    }

    indra {
        mitLicense()

        javaVersions {
            minimumToolchain(25)
            target(25)
        }

        github("incendo", "interfaces") {
            ci(true)
        }

        configurePublications {
            pom {
                developers {
                    developer {
                        id.set("kadenscott")
                        email.set("kscottdev@gmail.com")
                    }
                }
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("interfaces") {
                groupId = "org.incendo.interfaces"
                artifactId = project.name
                version = project.version.toString()

                from(components["java"])
            }
        }
    }

    configure<SpotlessExtension> {
        kotlin {
            ktlint("0.47.1")
        }
    }

    // Disable GPG signing for local builds
    extensions.findByType<SigningExtension>()?.apply {
        isRequired = false
    }
    tasks.withType<Sign>().configureEach { enabled = false }


}
