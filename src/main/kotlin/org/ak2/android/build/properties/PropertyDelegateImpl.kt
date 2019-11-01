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

import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.PropertyDelegate
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
open class PropertyDelegateImpl : PropertyDelegate {

    protected val properties = Properties()

    override fun <T> getValue(receiver: Any?, property: KProperty<*>): T {
        return getValue(property.name, property.returnType.classifier, property.returnType.isMarkedNullable);
    }

    inline fun <reified T> get(name: String): T {
        return getValue(name, T::class, false)
    }

    inline fun <reified T> get(name: String, defValue : T): T {
        return getValue(name, T::class, true, defValue)
    }

    inline fun <reified T> opt(name: String): T? {
        return getValue(name, T::class, true)
    }

    fun <T> getValue(name: String, classifier: KClassifier?, nullable: Boolean, defValue : T? = null): T {
        val value = properties.getProperty(name)

        if (!nullable && value == null) {
            throw GradleException("Property [${name}] missed")
        }

        if (value == null) {
            return defValue as T
        }

        if (classifier is KClass<*>) {
            try {
                return when (classifier.java) {
                    String::class.java -> value as T
                    Boolean::class.java -> value.toBoolean() as T
                    Byte::class.java -> value.toByte() as T
                    Short::class.java -> value.toShort() as T
                    Int::class.java -> value.toInt() as T
                    Long::class.java -> value.toLong() as T
                    Float::class.java -> value.toFloat() as T
                    Double::class.java -> value.toDouble() as T
                    else -> {
                        val c = classifier.java.getConstructor(String::class.java)
                        c.newInstance(value) as T
                    }
                }
            } catch (ex: Exception) {
                throw GradleException("Cannot convert property [${name}] to ${classifier}", ex)
            }
        }

        throw GradleException("Cannot convert property [${name}] to ${classifier}")
    }
}