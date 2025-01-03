/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Base64

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform.cocoapods")
     id("maven-publish") 
    id("dev.icerock.mobile.multiplatform.android-manifest")
}

group = "dev.icerock.moko"
version = libs.versions.mokoSocketIoVersion.get()

kotlin {
    jvmToolchain(11)
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    ios()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        val commonMain by getting

        val commonJvm = create("commonJvm") {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(commonJvm)
        }

        val jvmMain by getting {
            dependsOn(commonJvm)
        }

        val iosMain by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.configureEach {
            cinterops.configureEach {
                extraOpts("-compiler-option", "-fmodules")
            }
        }
    }
}

dependencies {
    commonMainApi(libs.serialization)
    "androidMainImplementation"(libs.appCompat)
    "commonJvmImplementation"(libs.socketIo) {
        exclude(group = "org.json", module = "json")
    }
    "jvmMainImplementation"(libs.socketIo)
}
android {
    namespace = "dev.icerock.moko.socket"
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

}

cocoaPods {
    pod("mokoSocketIo")
}
