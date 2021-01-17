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

import org.ak2.android.build.*
import org.ak2.android.build.configurators.*
import org.gradle.api.GradleException
import org.gradle.api.Project

fun Project.androidLibrary(block : LibraryConfigurator.() -> Unit) {
    LibraryConfiguratorImpl(this).configure(block)
}

fun Project.androidApp(block : AppConfigurator.() -> Unit) {
    AppConfiguratorImpl(this).configure(block)
}

fun Project.androidAppSet(defaultApp : String? = null, block : AppSetConfigurator.() -> Unit) {
    AppSetConfiguratorImpl(this, defaultApp).configure(block)
}

fun Project.androidRoot(block: RootConfigurator.() -> Unit) {
    RootConfiguratorImpl(this).configure(block)
}

fun Project.studio(block : AndroidStudioConfigurator.() -> Unit) {
    require(this == rootProject) { throw GradleException("Android Studio can be configured only in root project") }
    project.studioConfig.block()
}

