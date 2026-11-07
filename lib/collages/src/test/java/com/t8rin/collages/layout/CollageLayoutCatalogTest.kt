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

import com.t8rin.collages.model.CollageDesign
import com.t8rin.collages.model.CollagePoint
import com.t8rin.collages.model.CollageSlot
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.tukaani.xz.XZInputStream
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipInputStream

class CollageLayoutCatalogTest {
    private val counts = CollageLayoutStore.counts
    private val catalogs by lazy {
        val archive = File("src/main/assets/collages/layouts.xz")
        val hash = MessageDigest.getInstance("SHA-256").digest(archive.readBytes())
            .joinToString("") { "%02x".format(it) }
        assertEquals(CollageLayoutStore.CATALOG_HASH, hash)
        ZipInputStream(XZInputStream(archive.inputStream().buffered(), 16 * 1024)).use { input ->
            counts.indices.map { index ->
                val name = "layouts_${index + 1}.json"
                assertEquals(name, input.nextEntry?.name)
                val bytes = input.readBytes()
                Json.parseToJsonElement(bytes.decodeToString()).jsonObject.also {
                    input.closeEntry()
                }
            }.also {
                assertEquals(null, input.nextEntry)
            }
        }
    }
    private val layouts
        get() = catalogs.flatMap { catalog ->
            catalog.getValue("layouts").jsonArray.map { it.jsonObject }
        }

    @Test
    fun catalogContainsEveryLayoutAndSupportsOneToOneHundredPhotos() {
        assertEquals(5000, layouts.size)
        assertEquals(layouts.size, counts.sum())
        catalogs.forEachIndexed { index, catalog ->
            val entries = catalog.getValue("layouts").jsonArray
            assertEquals(counts[index], entries.size)
            assertTrue(entries.all { it.jsonObject.getValue("slots").jsonArray.size == index + 1 })
        }
        assertEquals(layouts.size, layouts.map { it.getValue("id") }.toSet().size)
        val counts = layouts.groupingBy { it.getValue("slots").jsonArray.size }.eachCount()
        assertEquals((1..100).toSet(), counts.keys)
        assertTrue(counts.values.all { it >= 10 })
    }

    @Test
    fun basicLayoutsComeFirstForEveryPhotoCount() {
        layouts.groupBy { it.getValue("slots").jsonArray.size }.values.forEach { entries ->
            val legacy =
                entries.takeWhile { it.getValue("id").jsonPrimitive.content.startsWith("collage_") }
            assertEquals(
                entries.count { it.getValue("id").jsonPrimitive.content.startsWith("collage_") },
                legacy.size
            )
        }
    }

    @Test
    fun slotsKeepValidBoundsMasksAndParameterBindings() {
        catalogs.forEach { catalog ->
            val masks = catalog.getValue("masks").jsonArray
            val shapes = catalog.getValue("shapes").jsonArray
            catalog.getValue("layouts").jsonArray.map { it.jsonObject }.forEach { layout ->
                val id = layout.getValue("id").jsonPrimitive.content
                val parameters =
                    layout["parameters"]?.jsonArray.orEmpty().map { it.jsonPrimitive.float }
                assertTrue(id, parameters.all { it.isFinite() && it in 0f..1f })
                val slots = layout.getValue("slots").jsonArray
                assertEquals(
                    id, slots.indices.toSet(),
                    slots.mapIndexed { index, value ->
                        value.jsonObject["index"]?.jsonPrimitive?.int ?: index
                    }.toSet()
                )
                slots.forEach { value ->
                    val slot = value.jsonObject
                    val bounds = slot.getValue("bound").jsonArray.map { it.jsonPrimitive.float }
                    assertEquals(id, 4, bounds.size)
                    assertTrue(id, bounds.all { it.isFinite() && it in 0f..1f })
                    assertTrue(id, bounds[2] > bounds[0] && bounds[3] > bounds[1])
                    slot["shape"]?.jsonPrimitive?.int?.let { index ->
                        assertTrue(id, index in shapes.indices)
                        val points = shapes[index].jsonArray.map { it.jsonPrimitive.float }
                        assertEquals(id, 0, points.size % 2)
                        val polygon =
                            CollageSlot(points.chunked(2).map { (x, y) -> CollagePoint(x, y) })
                        assertTrue(id, CollageDesign(listOf(polygon)).isValid())
                    }
                    slot["mask"]?.jsonPrimitive?.int?.let { assertTrue(id, it in masks.indices) }
                    if (parameters.isNotEmpty()) {
                        val edges = slot.getValue("edges").jsonArray.map { it.jsonPrimitive.int }
                        assertEquals(id, 4, edges.size)
                        edges.forEachIndexed { edge, parameter ->
                            assertTrue(id, parameter == -1 || parameter in parameters.indices)
                            if (parameter >= 0) assertEquals(
                                id,
                                bounds[edge],
                                parameters[parameter],
                                0f
                            )
                        }
                        listOf("xHandles", "yHandles").forEach { key ->
                            slot[key]?.jsonArray?.forEach {
                                val parameter = it.jsonPrimitive.int
                                assertTrue(id, parameter in parameters.indices)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun catalogPolygonsRemainDistinctWithinEachGeneratedCollection() {
        listOf("creative_", "artistic_", "decorative_", "pattern_", "gallery_").forEach { prefix ->
            val geometry = catalogs.flatMap { catalog ->
                val shapes = catalog.getValue("shapes").jsonArray
                catalog.getValue("layouts").jsonArray.map { it.jsonObject }
                    .filter { it.getValue("id").jsonPrimitive.content.startsWith(prefix) }
                    .map { entry ->
                        entry.getValue("slots").jsonArray.map {
                            shapes[it.jsonObject.getValue("shape").jsonPrimitive.int]
                        }
                    }
            }
            assertEquals(prefix, geometry.size, geometry.toSet().size)
        }
    }
}