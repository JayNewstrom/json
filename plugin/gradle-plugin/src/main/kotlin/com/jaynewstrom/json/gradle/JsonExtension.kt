package com.jaynewstrom.json.gradle

import java.io.Serializable

open class JsonExtension : Serializable {
    var createSerializerByDefault: Boolean = false
    var createDeserializerByDefault: Boolean = false
    var useAutoValueByDefault: Boolean = false
}
