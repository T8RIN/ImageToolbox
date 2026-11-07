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

import org.json.JSONObject

internal data class CollageLayoutDefinition(
    val id: String,
    val slots: List<Slot>,
    val parameters: FloatArray?,
    val inset: Float
) {
    data class Slot(
        val bounds: FloatArray,
        val shape: Int,
        val edges: IntArray?,
        val xHandles: List<Int>,
        val yHandles: List<Int>,
        val mask: Int,
        val properties: JSONObject?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Slot

            if (shape != other.shape) return false
            if (mask != other.mask) return false
            if (!bounds.contentEquals(other.bounds)) return false
            if (!edges.contentEquals(other.edges)) return false
            if (xHandles != other.xHandles) return false
            if (yHandles != other.yHandles) return false
            if (properties != other.properties) return false

            return true
        }

        override fun hashCode(): Int {
            var result = shape
            result = 31 * result + mask
            result = 31 * result + bounds.contentHashCode()
            result = 31 * result + (edges?.contentHashCode() ?: 0)
            result = 31 * result + xHandles.hashCode()
            result = 31 * result + yHandles.hashCode()
            result = 31 * result + properties.hashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollageLayoutDefinition

        if (inset != other.inset) return false
        if (id != other.id) return false
        if (slots != other.slots) return false
        if (!parameters.contentEquals(other.parameters)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inset.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + slots.hashCode()
        result = 31 * result + (parameters?.contentHashCode() ?: 0)
        return result
    }
}