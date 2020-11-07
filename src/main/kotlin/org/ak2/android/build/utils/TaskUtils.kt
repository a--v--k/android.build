package org.ak2.android.build.utils

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

inline fun <reified T : Task> TaskContainer.findByNameAndConfigure(
    name: String,
    crossinline action: T.() -> Unit
) {
    val task = this.findByName(name)
    if (task != null) {
        (this as T).action()
    } else {
        this.whenTaskAdded {
            if (this.name == name) {
                (this as T).action()
            }
        }
    }
}