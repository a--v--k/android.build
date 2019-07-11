/*
 * Copyright 2019 AK2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ak2.android.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project.name == "buildScr" && project.parent == null) {
            println("Add required plugins...")
            project.plugins.apply("kotlin-dsl")
            project.plugins.apply("java-gradle-plugin")

            println("Add required repositories...")
            project.repositories.apply {
                google()
                jcenter()
                mavenCentral()
                gradlePluginPortal()
            }

            println("Add required dependencies...")
            project.dependencies.apply {
                add("compile", "gradle.plugin.org.ak2:android.build:3.4.1-rc4")
            }
        }
    }
}