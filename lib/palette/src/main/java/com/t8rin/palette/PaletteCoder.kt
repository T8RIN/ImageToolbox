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

package com.t8rin.palette

import android.content.Context
import android.net.Uri
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Protocol for palette coders
 */
interface PaletteCoder {
    /**
     * Decode a palette from input stream
     */
    fun decode(input: InputStream): Palette

    /**
     * Encode a palette to output stream
     */
    fun encode(palette: Palette, output: OutputStream)
}

/**
 * Decode a palette from byte array
 */
fun PaletteCoder.decode(data: ByteArray): Palette = decode(ByteArrayInputStream(data).buffered())

/**
 * Encode a palette to byte array
 */
fun PaletteCoder.encode(palette: Palette): ByteArray = ByteArrayOutputStream().use {
    encode(palette, it)
    it.toByteArray()
}

inline fun <T> PaletteCoder.use(action: PaletteCoder.() -> T): Result<T> = runCatching { action() }

fun PaletteCoder.decode(uri: Uri, context: Context) =
    decode(context.contentResolver.openInputStream(uri)!!)