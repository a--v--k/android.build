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

package org.ak2.android.build.properties

import com.google.common.base.Charsets
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.PropertyDelegate
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class LocalProperties(val project: Project) : PropertyDelegate {

    private val properties = Properties()

    init {
        project.file("local.properties")
            .takeIf { it.isFile }
            ?.let { InputStreamReader(FileInputStream(it), Charsets.UTF_8) }
            ?.use { properties.load(it) }
    }

    override fun <T> getValue(receiver: Any?, property: KProperty<*>): T {
        val value = properties.getProperty(property.name)

        if (!property.returnType.isMarkedNullable && value == null) {
            throw GradleException("${project.path}: Property [${property.name}] missed")
        }

        if (value == null) {
            return null as T
        }

        if (property.returnType.classifier is KClass<*>) {
            val clazz = property.returnType.classifier as KClass<*>

            try {
                return when (clazz.java) {
                    String::class.java -> value as T
                    Boolean::class.java -> value.toBoolean() as T
                    Byte::class.java -> value.toByte() as T
                    Short::class.java -> value.toShort() as T
                    Int::class.java -> value.toInt() as T
                    Long::class.java -> value.toLong() as T
                    Float::class.java -> value.toFloat() as T
                    Double::class.java -> value.toDouble() as T
                    else -> {
                        val c = clazz.java.getConstructor(String::class.java)
                        c.newInstance(value) as T
                    }
                }
            } catch (ex: Exception) {
                throw GradleException("${project.path}: Cannot convert property [${property.name}] to ${property.returnType.classifier}", ex)
            }
        }

        throw GradleException("${project.path}: Cannot convert property [${property.name}] to ${property.returnType.classifier}")
    }
}