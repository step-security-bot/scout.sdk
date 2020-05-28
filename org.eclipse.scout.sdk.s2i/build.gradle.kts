/*
 * Copyright (c) 2010-2020 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */

import org.jetbrains.intellij.tasks.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.time.Clock
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter

val KOTLIN_VERSION = "1.3.61"
val SCOUT_SDK_VERSION = "10.0.0-SNAPSHOT"
val SCOUT_SDK_PLUGIN_VERSION = SCOUT_SDK_VERSION.replace("-SNAPSHOT", "." + timestamp())
val JAVA_VERSION = JavaVersion.VERSION_1_8

fun timestamp(): String {
    val now = now(Clock.systemUTC())
    // returned number must be a valid integer (not too big)
    return now.format(DateTimeFormatter.ofPattern("yyMMddHHmm"))
}

plugins {
    id("java")
    id("maven-publish")
    id("idea")
    id("org.jetbrains.intellij") version "0.4.21"
    kotlin("jvm") version "1.3.61"
}

group = "org.eclipse.scout.sdk.s2i"
version = SCOUT_SDK_VERSION

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("org.eclipse.scout.sdk", "org.eclipse.scout.sdk.core.s", SCOUT_SDK_VERSION)
    api("org.eclipse.scout.sdk", "org.eclipse.scout.sdk.core.ecj", SCOUT_SDK_VERSION)
    api("org.apache.commons", "commons-csv", "1.8")
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", KOTLIN_VERSION)
    testImplementation("org.mockito", "mockito-core", "3.3.3")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "IU-2019.2.3"
    downloadSources = true

    setPlugins("java", "maven", "copyright", "properties", "CSS", "JavaScriptLanguage")
    updateSinceUntilBuild = false

    tasks {
        withType<PatchPluginXmlTask> {
            version(SCOUT_SDK_PLUGIN_VERSION)
        }
    }
}

allprojects {
    configure<JavaPluginConvention> {
        sourceCompatibility = JAVA_VERSION
        targetCompatibility = JAVA_VERSION
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JAVA_VERSION.toString()
    targetCompatibility = JAVA_VERSION.toString()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JAVA_VERSION.toString()
        apiVersion = "1.3"
        languageVersion = "1.3"
    }
}

// https://docs.gradle.org/current/dsl/org.gradle.plugins.ide.idea.model.IdeaModule.html
idea {
    module {
        // Fix problems caused by separate output directories for classes/resources in IntelliJ IDEA
        inheritOutputDirs = true
    }
}

tasks.jar {
    from("about.html")
    from("epl-v10.html")
}