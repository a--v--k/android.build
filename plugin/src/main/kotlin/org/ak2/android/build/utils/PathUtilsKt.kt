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

package org.ak2.android.build.utils

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Predicate
import java.util.stream.Stream

fun Path.listDirectories(filter: Predicate<Path>): Stream<Path> {
    return Files.walk(this).filter { f -> Files.isDirectory(f) }.filter(filter)
}

fun Path.listFiles(filter: Predicate<Path>): Stream<Path> {
    return Files.walk(this).filter { f -> Files.isRegularFile(f) }.filter(filter)
}

fun Path.getLastSegment(): Path {
    return this.getName(this.nameCount - 1)
}

fun Path.getLastName(): String {
    return this.getLastSegment().toString()
}
