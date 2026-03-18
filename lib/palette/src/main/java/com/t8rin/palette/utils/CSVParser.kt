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

package com.t8rin.palette.utils

/**
 * Simple CSV parser
 */
internal object CSVParser {
    /**
     * Parse CSV text into records
     */
    fun parse(text: String): List<List<String>> {
        val records = mutableListOf<List<String>>()
        val lines = text.lines()

        for (line in lines) {
            if (line.isBlank()) continue
            val fields = line.split(',').map { it.trim() }
            if (fields.isNotEmpty()) {
                records.add(fields)
            }
        }

        return records
    }
}


