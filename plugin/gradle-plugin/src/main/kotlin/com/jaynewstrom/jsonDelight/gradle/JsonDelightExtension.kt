package com.jaynewstrom.jsonDelight.gradle

import java.io.Serializable

open class JsonDelightExtension : Serializable {
    var createSerializerByDefault: Boolean = false
    var createDeserializerByDefault: Boolean = false
}
