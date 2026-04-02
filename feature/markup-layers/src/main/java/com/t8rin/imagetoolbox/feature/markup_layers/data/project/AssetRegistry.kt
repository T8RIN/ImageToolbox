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

package com.t8rin.imagetoolbox.feature.markup_layers.data.project

import androidx.core.net.toUri
import java.io.File

internal class AssetRegistry {
    private val entryBySource = linkedMapOf<String, AssetSource>()

    fun register(
        source: String,
        proposedEntryName: String
    ): String {
        val key = sourceKey(source)
        return entryBySource[key]?.entryName ?: proposedEntryName.also {
            entryBySource[key] = AssetSource(
                entryName = proposedEntryName,
                source = source
            )
        }
    }

    private fun sourceKey(
        source: String
    ): String = when {
        source.startsWith("android.resource://") -> source
        source.startsWith("content://") -> source
        source.startsWith("file://") -> {
            source.toUri().path
                ?.let(::File)
                ?.canonicalPath
                ?.let { "file:$it" }
                ?: source
        }

        else -> runCatching { File(source).canonicalPath }
            .getOrNull()
            ?.let { "path:$it" }
            ?: source
    }

    fun entries(): List<AssetSource> = entryBySource.values.toList()
}

internal data class AssetSource(
    val entryName: String,
    val source: String
)

internal data class ProjectArchive(
    val projectJson: String,
    val assets: List<AssetSource>
)