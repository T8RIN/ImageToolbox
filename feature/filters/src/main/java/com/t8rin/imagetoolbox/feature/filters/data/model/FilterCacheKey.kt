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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.toItShaderJson
import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.WeakHashMap

internal fun filterCacheKey(value: Any?): String = digest { writeValue(value) }

private fun digest(write: DataOutputStream.() -> Unit): String {
    val digest = MessageDigest.getInstance("SHA-256")
    DataOutputStream(DigestOutputStream(object : OutputStream() {
        override fun write(value: Int) = Unit
        override fun write(value: ByteArray, offset: Int, length: Int) = Unit
    }, digest)).use { it.write() }
    return digest.digest().toHexString()
}

private fun DataOutputStream.writeText(value: String) {
    val bytes = value.encodeToByteArray()
    writeInt(bytes.size)
    write(bytes)
}

private fun DataOutputStream.writeValue(value: Any?) {
    when (value) {
        null -> writeByte(0)
        is String -> {
            writeByte(1)
            writeText(value)
        }

        is Boolean -> {
            writeByte(2)
            writeBoolean(value)
        }

        is Int -> {
            writeByte(3)
            writeInt(value)
        }

        is Long -> {
            writeByte(4)
            writeLong(value)
        }

        is Float -> {
            writeByte(5)
            writeInt(value.toRawBits())
        }

        is Double -> {
            writeByte(6)
            writeLong(value.toRawBits())
        }

        is ByteArray -> {
            writeByte(7)
            writeInt(value.size)
            write(value)
        }

        is IntArray -> {
            writeByte(8)
            writeInt(value.size)
            value.forEach(::writeInt)
        }

        is FloatArray -> {
            writeByte(9)
            writeInt(value.size)
            value.forEach { writeInt(it.toRawBits()) }
        }

        is Enum<*> -> {
            writeByte(10)
            writeText(value.declaringJavaClass.name)
            writeText(value.name)
        }

        is List<*> -> {
            writeByte(11)
            writeInt(value.size)
            value.forEach(::writeValue)
        }

        is Map<*, *> -> {
            writeByte(12)
            writeInt(value.size)
            value.entries
                .map { filterCacheKey(it.key) to it }
                .sortedBy { it.first }
                .forEach { (_, entry) ->
                    writeValue(entry.key)
                    writeValue(entry.value)
                }
        }

        is Pair<*, *> -> {
            writeByte(13)
            writeValue(value.first)
            writeValue(value.second)
        }

        is ImageModel -> {
            writeByte(14)
            writeValue(value.data)
        }

        is FileModel -> {
            writeByte(15)
            writeText(value.uri)
        }

        is Uri -> {
            writeByte(16)
            writeText(value.toString())
        }

        is Bitmap -> {
            writeByte(17)
            writeText(value.contentKey())
        }

        is ShaderPreset -> {
            writeByte(18)
            writeText(value.toItShaderJson())
        }

        is ShaderValue -> {
            writeByte(19)
            writeText(value.type.name)
            when (value) {
                is ShaderValue.FloatValue -> writeValue(value.value)
                is ShaderValue.IntValue -> writeValue(value.value)
                is ShaderValue.BoolValue -> writeValue(value.value)
                is ShaderValue.ColorValue -> {
                    writeInt(value.red)
                    writeInt(value.green)
                    writeInt(value.blue)
                    writeInt(value.alpha)
                }

                is ShaderValue.Vec2Value -> {
                    writeValue(value.x)
                    writeValue(value.y)
                }
            }
        }

        is IntegerSize -> {
            writeByte(20)
            writeInt(value.width)
            writeInt(value.height)
        }

        else -> error("Provide a cache value for filter parameter ${value::class.qualifiedName}")
    }
}

private val bitmapKeys = WeakHashMap<Bitmap, BitmapCacheEntry>()

private class BitmapCacheEntry(
    val generation: Int,
    val metadata: String,
    val key: String
)

private fun Bitmap.contentKey(): String = synchronized(bitmapKeys) {
    val generation = generationId
    val space = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) colorSpace?.name else null
    val metadata = "$width:$height:${config?.name}:$space:${hasAlpha()}"
    bitmapKeys[this]?.takeIf { it.generation == generation && it.metadata == metadata }?.key
        ?: run {
            val readable =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.HARDWARE) {
                    val target =
                        if (colorSpace?.isWideGamut == true) Bitmap.Config.RGBA_F16 else Bitmap.Config.ARGB_8888
                    requireNotNull(copy(target, false))
                } else this
            try {
                digest {
                    writeText(metadata)
                    val pixels = ByteBuffer.allocate(readable.byteCount)
                    readable.copyPixelsToBuffer(pixels)
                    write(pixels.array())
                }.also { bitmapKeys[this] = BitmapCacheEntry(generation, metadata, it) }
            } finally {
                if (readable !== this) readable.recycle()
            }
        }
}
