/*
 * Copyright 2020 AK2.
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

package org.ak2.android.build.utils

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

inline fun <reified T : Task> TaskContainer.findByNameAndConfigure(targetTaskName: String, crossinline action: T.() -> Unit) {
    val task = this.findByName(targetTaskName)
    if (task != null) {
        (this as T).action()
    } else {
        this.whenTaskAdded {
            if (this.name == targetTaskName) {
                (this as T).action()
            }
        }
    }
}

inline fun <reified T : Task> TaskContainer.findAllAndConfigure(crossinline action: T.() -> Unit) {
    this.filterIsInstance<T>().forEach(action)

    this.whenTaskAdded {
        if (this is T) {
            this.action()
        }
    }
}

