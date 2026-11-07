/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.collages.layout.catalog

import android.util.JsonReader
import android.util.JsonToken
import org.json.JSONArray
import org.json.JSONObject

internal fun JsonReader.readCollageLayout(): CollageLayoutDefinition {
    var id: String? = null
    val slots = mutableListOf<CollageLayoutDefinition.Slot>()
    var parameters: FloatArray? = null
    var inset = Float.POSITIVE_INFINITY
    beginObject()
    while (hasNext()) {
        when (nextName()) {
            "id" -> id = nextString()
            "slots" -> {
                beginArray()
                while (hasNext()) slots.add(readSlot())
                endArray()
            }

            "parameters" -> parameters = floats()
            "inset" -> inset = nextDouble().toFloat()
            else -> error("Unknown layout field")
        }
    }
    endObject()
    return CollageLayoutDefinition(checkNotNull(id), slots, parameters, inset)
}

internal fun JsonReader.readCollageMasks(): JSONArray = value() as JSONArray

internal fun JsonReader.readCollageShapes(): List<FloatArray> {
    val shapes = mutableListOf<FloatArray>()
    beginArray()
    while (hasNext()) shapes.add(floats())
    endArray()
    return shapes
}

private fun JsonReader.readSlot(): CollageLayoutDefinition.Slot {
    var bounds: FloatArray? = null
    var shape = -1
    var edges: IntArray? = null
    var xHandles = emptyList<Int>()
    var yHandles = emptyList<Int>()
    var mask = -1
    var properties: JSONObject? = null
    beginObject()
    while (hasNext()) {
        when (val name = nextName()) {
            "bound" -> bounds = floats()
            "shape" -> shape = nextInt()
            "edges" -> edges = integers().toIntArray()
            "xHandles" -> xHandles = integers()
            "yHandles" -> yHandles = integers()
            "mask" -> mask = nextInt()
            else -> {
                val target = properties ?: JSONObject().also { properties = it }
                target.put(name, value())
            }
        }
    }
    endObject()
    return CollageLayoutDefinition.Slot(
        bounds = checkNotNull(bounds), shape = shape, edges = edges,
        xHandles = xHandles, yHandles = yHandles, mask = mask, properties = properties
    )
}

private fun JsonReader.floats(): FloatArray {
    val result = mutableListOf<Float>()
    beginArray()
    while (hasNext()) result.add(nextDouble().toFloat())
    endArray()
    return result.toFloatArray()
}

private fun JsonReader.integers(): List<Int> {
    val result = mutableListOf<Int>()
    beginArray()
    while (hasNext()) result.add(nextInt())
    endArray()
    return result
}

private fun JsonReader.value(): Any = when (peek()) {
    JsonToken.BEGIN_OBJECT -> JSONObject().apply {
        beginObject()
        while (hasNext()) put(nextName(), value())
        endObject()
    }

    JsonToken.BEGIN_ARRAY -> JSONArray().apply {
        beginArray()
        while (hasNext()) put(value())
        endArray()
    }

    JsonToken.STRING -> nextString()
    JsonToken.NUMBER -> nextDouble()
    JsonToken.BOOLEAN -> nextBoolean()
    JsonToken.NULL -> {
        nextNull()
        JSONObject.NULL
    }

    else -> error("Unexpected catalog token: ${peek()}")
}