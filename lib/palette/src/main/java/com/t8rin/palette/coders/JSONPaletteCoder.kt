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

package com.t8rin.palette.coders

import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoder
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * JSON palette coder
 */
class JSONPaletteCoder : PaletteCoder {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    override fun decode(input: InputStream): Palette {
        val text = input.bufferedReader().use { it.readText() }
        return json.decodeFromString(Palette.serializer(), text)
    }

    override fun encode(palette: Palette, output: OutputStream) {
        val text = json.encodeToString(Palette.serializer(), palette)
        output.bufferedWriter().use { it.write(text) }
    }
}


