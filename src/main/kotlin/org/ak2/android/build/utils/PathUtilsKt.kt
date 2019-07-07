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
