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

package com.t8rin.archive

object ArchivePath {

    fun safeSegments(path: String): List<String>? {
        if (path.isBlank() || path.indexOf('\u0000') >= 0) return null

        val normalized = path.replace('\\', '/')
        if (normalized.length > MaxPathLength) return null
        if (normalized.startsWith('/') || DrivePrefix.containsMatchIn(normalized)) return null

        val segments = normalized
            .split('/')
            .filter(String::isNotBlank)

        if (segments.isEmpty()) return null
        if (segments.size > MaxSegments) return null
        if (segments.any { it == "." || it == ".." }) return null

        return segments.map { it.take(MaxSegmentLength) }
    }

    private val DrivePrefix = Regex("^[A-Za-z]:/")
    private const val MaxSegmentLength = 240
    private const val MaxPathLength = 4096
    private const val MaxSegments = 256
}
