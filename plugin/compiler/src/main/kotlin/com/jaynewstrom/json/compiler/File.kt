package com.jaynewstrom.json.compiler

fun String.relativePath(separatorChar: Char): List<String> {
    val parts = split(separatorChar)
    for (i in 2..parts.size) {
        if (parts[i - 2] == "src" && parts[i] == "json") {
            return parts.subList(i + 1, parts.size)
        }
    }
    throw IllegalStateException("Files must be organized like src/main/json/...")
}
