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

package com.t8rin.collages.layout

import android.content.Context
import android.util.JsonReader
import com.t8rin.collages.config.CollageConstants
import com.t8rin.collages.layout.catalog.CollageLayoutDefinition
import com.t8rin.collages.layout.catalog.parseCollageLayout
import com.t8rin.collages.layout.catalog.readCollageLayout
import com.t8rin.collages.layout.catalog.readCollageMasks
import com.t8rin.collages.layout.catalog.readCollageShapes
import com.t8rin.collages.layout.generator.ProceduralCollageLayouts
import com.t8rin.collages.model.CollageDesign
import com.t8rin.collages.model.CollageMask
import com.t8rin.collages.model.CollageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tukaani.xz.XZInputStream
import java.io.File
import java.util.zip.ZipInputStream

object CollageLayoutStore {
    internal const val CATALOG_HASH =
        "e8e98222bc11a77af05507e798d50ab3410a7ffcf23e3691e642bd50c082d911"
    internal val counts = listOf(
        318, 452, 385, 376, 282, 206, 169, 166, 166, 122,
        93, 92, 90, 89, 86, 60, 62, 65, 60, 53,
        31, 27, 29, 31, 25, 22, 23, 25, 23, 25,
        22, 24, 22, 22, 24, 24, 24, 22, 23, 25,
        24, 25, 22, 25, 25, 25, 22, 23, 22, 23,
        17, 17, 17, 19, 19, 19, 17, 17, 17, 19,
        17, 17, 19, 17, 19, 19, 17, 17, 17, 18,
        17, 17, 17, 17, 17, 17, 19, 19, 17, 19,
        17, 17, 17, 19, 17, 17, 17, 17, 17, 17,
        17, 17, 17, 17, 17, 19, 17, 19, 19, 17
    )
    val layoutCount = counts.sum() + ProceduralCollageLayouts.layoutCount

    fun getLayoutCount(imageCount: Int): Int =
        (counts.getOrNull(imageCount - 1) ?: 0) + ProceduralCollageLayouts.count(imageCount)

    @Volatile
    private var catalogs = emptyMap<Int, Catalog>()
    private val mutex = Mutex()
    private var cacheDirectory: File? = null

    suspend fun load(imageCount: Int) = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (imageCount !in 1..counts.size || imageCount in catalogs) return@withLock
            var masks = emptyList<CollageMask>()
            var shapes = emptyList<FloatArray>()
            val entries = mutableMapOf<String, CollageLayoutDefinition>()
            reader("layouts_$imageCount.json").use { reader ->
                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "shapes" -> shapes = reader.readCollageShapes()
                        "masks" -> masks = reader.readCollageMasks().let { entries ->
                            List(entries.length()) { CollageMask(entries.getJSONObject(it)) }
                        }

                        "layouts" -> {
                            reader.beginArray()
                            while (reader.hasNext()) {
                                val entry = reader.readCollageLayout()
                                check(entry.slots.size == imageCount)
                                check(entries.put(entry.id, entry) == null)
                            }
                            reader.endArray()
                        }

                        else -> error("Unknown catalog field")
                    }
                }
                reader.endObject()
            }
            check(entries.size == counts[imageCount - 1])
            val generated = withContext(Dispatchers.Default) {
                ProceduralCollageLayouts.generate(imageCount)
            }
            catalogs = catalogs + (imageCount to Catalog(masks, shapes, entries, generated))
        }
    }

    suspend fun getLayouts(imageCount: Int? = null): List<CollageType> {
        if (imageCount == null) {
            return (1..counts.size).flatMap { getLayouts(it) }
        }
        load(imageCount)
        return withContext(Dispatchers.Default) {
            catalogs[imageCount]?.layouts.orEmpty()
        }
    }

    suspend fun getDefaultLayout(imageCount: Int): CollageType? {
        load(imageCount)
        return withContext(Dispatchers.Default) {
            val catalog = catalogs[imageCount] ?: return@withContext null
            catalog.entries.values.firstOrNull()
                ?.let { CollageType(it.parseCollageLayout(catalog.masks, catalog.shapes)) }
        }
    }

    private fun reader(file: String): JsonReader {
        val context = checkNotNull(CollageConstants.context) {
            "CollageConstants.requestMapper must be initialized before loading layouts"
        }
        val directory = cacheDirectory?.takeIf { it.resolve(file).isFile }
            ?: unpackCatalog(context, file).also { cacheDirectory = it }
        return JsonReader(directory.resolve(file).bufferedReader())
    }

    private fun unpackCatalog(context: Context, requestedFile: String): File {
        val root = context.cacheDir.resolve("collage_layouts")
        val directory = root.resolve(CATALOG_HASH)
        if (directory.resolve(requestedFile).isFile) return directory

        val staging = root.resolve("pending")
        staging.deleteRecursively()
        check(staging.mkdirs())
        try {
            ZipInputStream(
                XZInputStream(context.assets.open("collages/layouts.xz").buffered(), 16 * 1024)
            ).use { archive ->
                counts.indices.forEach { index ->
                    val name = "layouts_${index + 1}.json"
                    check(archive.nextEntry?.name == name)
                    staging.resolve(name).outputStream().buffered().use { archive.copyTo(it) }
                    archive.closeEntry()
                }
                check(archive.nextEntry == null)
            }
            directory.deleteRecursively()
            check(staging.renameTo(directory))
            root.listFiles().orEmpty().filter { it != directory }.forEach { it.deleteRecursively() }
        } finally {
            staging.deleteRecursively()
        }
        return directory
    }

    fun getLayout(
        layoutId: String,
        layoutParams: List<Float>? = null,
        design: CollageDesign? = null
    ): CollageType? {
        val layout = if (design != null) {
            if (!design.isValid()) return null
            design.toLayout(layoutId, custom = true)
        } else {
            val catalog = catalogs.values.firstOrNull {
                layoutId in it.entries || layoutId in it.generated
            } ?: return null
            catalog.generated[layoutId]?.toLayout(layoutId)
                ?: catalog.entries[layoutId]?.parseCollageLayout(catalog.masks, catalog.shapes)
                ?: return null
        }
        if (!layoutParams.isNullOrEmpty()) {
            val manager = layout.paramsManager
            if (layoutParams.size != (manager?.snapshotValues()?.size ?: 0) ||
                layoutParams.any { !it.isFinite() }
            ) return null
            if (runCatching { manager?.restoreValues(layoutParams) }.isFailure) return null
        }
        return CollageType(layout)
    }

    private class Catalog(
        val masks: List<CollageMask>,
        val shapes: List<FloatArray>,
        val entries: Map<String, CollageLayoutDefinition>,
        val generated: Map<String, CollageDesign>
    ) {
        val layouts by lazy {
            entries.values.map { CollageType(it.parseCollageLayout(masks, shapes)) } +
                    generated.map { (id, design) -> CollageType(design.toLayout(id)) }
        }
    }
}