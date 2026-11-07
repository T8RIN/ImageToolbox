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

package com.t8rin.collages

import android.util.JsonReader
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.model.CollageLayoutDefinition
import com.t8rin.collages.public.CollageConstants
import com.t8rin.collages.utils.parseCollageLayout
import com.t8rin.collages.utils.readCollageLayout
import com.t8rin.collages.utils.readCollageMasks
import com.t8rin.collages.utils.readCollageShapes
import com.t8rin.collages.utils.withRectangularHandles
import com.t8rin.collages.view.PhotoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray

object CollageLayoutStore {
    @Volatile
    private var catalog: Catalog? = null
    private val mutex = Mutex()

    private val _layoutCount = mutableIntStateOf(0)
    val layoutCount by _layoutCount

    suspend fun load() = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (catalog != null) return@withLock
            val context = checkNotNull(CollageConstants.context) {
                "CollageConstants.requestMapper must be initialized before loading layouts"
            }
            var masks = JSONArray()
            var shapes = emptyList<FloatArray>()
            val entries = mutableMapOf<String, CollageLayoutDefinition>()
            context.assets.open("collages/layouts.json")
                .reader().let(::JsonReader).use { reader ->
                    reader.beginObject()
                    while (reader.hasNext()) {
                        when (reader.nextName()) {
                            "shapes" -> shapes = reader.readCollageShapes()
                            "masks" -> masks = reader.readCollageMasks()
                            "layouts" -> {
                                reader.beginArray()
                                while (reader.hasNext()) {
                                    val entry = reader.readCollageLayout()
                                    check(entries.put(entry.id, entry) == null)
                                }
                                reader.endArray()
                            }

                            else -> error("Unknown catalog field")
                        }
                    }
                    reader.endObject()
                }
            catalog = Catalog(
                masks = masks,
                shapes = shapes,
                entries = entries,
                byImageCount = entries.values.groupBy { it.slots.size }
            )
            _layoutCount.intValue = entries.size
        }
    }

    suspend fun getLayouts(imageCount: Int? = null): List<CollageType> {
        load()
        return withContext(Dispatchers.Default) {
            val catalog = catalog ?: return@withContext emptyList()
            val entries = if (imageCount == null) catalog.entries.values
            else catalog.byImageCount[imageCount].orEmpty()
            entries.map { CollageType(it.parseCollageLayout(catalog.masks, catalog.shapes)) }
        }
    }

    suspend fun getDefaultLayout(imageCount: Int): CollageType? {
        load()
        return withContext(Dispatchers.Default) {
            val catalog = catalog ?: return@withContext null
            catalog.byImageCount[imageCount]?.firstOrNull()
                ?.let { CollageType(it.parseCollageLayout(catalog.masks, catalog.shapes)) }
        }
    }

    fun getLayout(
        layoutId: String,
        layoutParams: List<Float>? = null,
        design: CollageDesign? = null
    ): CollageType? {
        val layout = if (design != null) {
            if (!design.isValid()) return null
            CollageLayout(
                title = layoutId,
                design = design,
                photoItemList = design.slots.mapIndexed { index, slot ->
                    PhotoItem(index = index, shape = slot.points).apply {
                        bound.set(slot.left, slot.top, slot.right, slot.bottom)
                    }
                }
            ).withRectangularHandles()
        } else {
            val catalog = catalog ?: return null
            catalog.entries[layoutId]?.parseCollageLayout(catalog.masks, catalog.shapes)
                ?: return null
        }
        if (layoutParams != null) {
            val manager = layout.paramsManager
            if (layoutParams.size != (manager?.snapshotValues()?.size ?: 0) ||
                layoutParams.any { !it.isFinite() }
            ) return null
            if (runCatching { manager?.restoreValues(layoutParams) }.isFailure) return null
        }
        return CollageType(layout)
    }

    private data class Catalog(
        val masks: JSONArray,
        val shapes: List<FloatArray>,
        val entries: Map<String, CollageLayoutDefinition>,
        val byImageCount: Map<Int, List<CollageLayoutDefinition>>
    )
}