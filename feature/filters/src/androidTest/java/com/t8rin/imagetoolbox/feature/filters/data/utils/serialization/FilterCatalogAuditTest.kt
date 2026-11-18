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

package com.t8rin.imagetoolbox.feature.filters.data.utils.serialization

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.AndroidFilterProvider
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.json.JSONTokener
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FilterCatalogAuditTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val provider = AndroidFilterProvider()

    @Test
    fun registryAndRendering() = runBlocking {
        val args = InstrumentationRegistry.getArguments()
        val phase = args.getString("phase") ?: "candidate"
        val start = args.getString("start")?.toInt() ?: 0
        val stop = args.getString("stop")?.toInt() ?: 633
        val skip = args.getString("skip")?.split(',').orEmpty()
        val groups = UiFilter.groups.flatMap { group ->
            group.filters(canAddTemplates = true).map { group.title to it }
        }.sortedBy { it.second.serializationKey }
        assertEquals(633, groups.size)
        assertEquals(625, groups.count { it.second.canUseTemplate })
        assertEquals(groups.size, groups.map { it.second.serializationKey }.toSet().size)
        val cacheKeys = mutableSetOf<String>()
        val legacy = instrumentation.context.assets.open("filter-values-before.jsonl")
            .bufferedReader().use { reader ->
                val tokens = JSONTokener(reader.readText())
                buildList {
                    while (tokens.nextClean() != '\u0000') {
                        tokens.back()
                        add(tokens.nextValue() as JSONObject)
                    }
                }.associateBy { it.getString("transformTemplate").substringBefore(':') }
            }
        val currentKeys = groups.map { (_, ui) ->
            listOf(provider.filterToTransformation(ui) as Filter<*>)
                .toDatastoreString(true).substringBefore(':')
        }.toSet()
        assertTrue("A released filter disappeared", currentKeys.containsAll(legacy.keys))
        val snapshot = File(context.cacheDir, "filters-$phase.jsonl")
        java.io.FileOutputStream(snapshot, start > 0).bufferedWriter().use { writer ->
            groups.subList(start, stop).forEachIndexed { localIndex, (group, ui) ->
                val index = start + localIndex
                val bitmapTransformation = provider.filterToTransformation(ui)
                val transformation = bitmapTransformation as Filter<*>
                val cacheKey = bitmapTransformation.cacheKey
                assertTrue(
                    "Duplicate cache key for ${ui.serializationKey}",
                    cacheKeys.add(cacheKey)
                )
                assertEquals(cacheKey, provider.filterToTransformation(ui).cacheKey)
                val uiTemplate = listOf(ui).toDatastoreString(includeValue = true)
                val transformTemplate =
                    listOf(transformation).toDatastoreString(includeValue = true)
                val data = JSONObject()
                    .put("key", ui.serializationKey)
                    .put("group", context.getString(group))
                    .put("title", context.getString(ui.title))
                    .put("help", context.getString(ui.helpDescription))
                    .put("params", ui.schema.parameters?.controls.orEmpty().joinToString { param ->
                        "${param.title?.let(context::getString)} ${param.valueRange} ${param.roundTo} " +
                                param.options.map(context::getString)
                    })
                    .put(
                        "newInstance",
                        listOf(ui.newInstance()).toDatastoreString(includeValue = true)
                    )
                    .put("copy", listOf(ui.copy(ui.value)).toDatastoreString(includeValue = true))
                    .put("favorite", listOf(ui).toDatastoreString())
                    .put(
                        "favoriteRestored", listOf(ui).toDatastoreString()
                            .toFiltersList(includeValue = false).single()
                            .toUiFilter().serializationKey
                    )
                    .put("uiTemplate", uiTemplate)
                    .put("transformTemplate", transformTemplate)
                    .put("canUseTemplate", ui.canUseTemplate)
                    .put(
                        "fromTransformation", listOf(transformation.toUiFilter())
                            .toDatastoreString(includeValue = true)
                    )
                    .put(
                        "coreRestore",
                        UiFilter.fromSerializationKey(ui.serializationKey)?.serializationKey
                    )
                val saved = legacy[transformTemplate.substringBefore(':')]
                assertEquals(
                    ui.serializationKey,
                    saved?.getBoolean("canUseTemplate") ?: ui.canUseTemplate,
                    ui.canUseTemplate
                )
                assertEquals(
                    ui.serializationKey, listOf(ui).toDatastoreString(),
                    (saved?.getString("favorite") ?: listOf(ui).toDatastoreString())
                        .toFiltersList(false).toDatastoreString()
                )
                if (ui.canUseTemplate) {
                    assertEquals(
                        ui.serializationKey, uiTemplate,
                        (saved?.getString("uiTemplate") ?: uiTemplate)
                            .toFiltersList(true).toDatastoreString(true)
                    )
                    assertEquals(
                        ui.serializationKey, transformTemplate,
                        (saved?.getString("transformTemplate") ?: transformTemplate).toFiltersList(
                            true
                        )
                            .toDatastoreString(true)
                    )
                    data.put("uiRestored", uiTemplate.toFiltersList(true).toDatastoreString(true))
                    data.put(
                        "transformRestored",
                        transformTemplate.toFiltersList(true).toDatastoreString(true)
                    )
                }
                val name = ui.javaClass.simpleName
                File(context.cacheDir, "filters-$phase.current").writeText("$index $name")
                val rendered = if (name in skip) "skipped" else render(ui)
                data.put("render", rendered)
                writer.appendLine(data.toString())
                writer.flush()
                assertTrue("$name: $rendered", name in skip || rendered.startsWith("ok "))
                if (index % 30 == 0) {
                    instrumentation.sendStatus(
                        2,
                        Bundle().apply {
                            putString(
                                "stream",
                                "$phase ${index + 1}/${groups.size}\n"
                            )
                        })
                }
            }
        }
    }

    @Test
    fun histogramGridFitsSmallBitmaps() = runBlocking {
        val key =
            "com.t8rin.imagetoolbox.core.filters.presentation.model.UiEqualizeHistogramPixelationFilter"
        val filter = requireNotNull(UiFilter.fromSerializationKey(key))
        val result = render(filter, 33, 31)
        assertTrue(result, result.startsWith("ok 33x31 "))
    }

    @Test
    fun gmicRecipesOnPhoto() = runBlocking {
        val phase = InstrumentationRegistry.getArguments().getString("phase") ?: "candidate"
        val bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.filter_preview_source)
        val image = Bitmap.createScaledBitmap(bitmap, 640, 512, true)
        File(context.cacheDir, "gmic-$phase.txt").bufferedWriter().use { writer ->
            for (name in listOf("CinematicDream", "DreamPainting", "MightyDetails")) {
                val key = "com.t8rin.imagetoolbox.core.filters.presentation.model.Ui${name}Filter"
                val filter = requireNotNull(UiFilter.fromSerializationKey(key))
                val input = image.copy(Bitmap.Config.ARGB_8888, false)
                val result = render(filter, input)
                writer.appendLine("$name $result")
                writer.flush()
                if (phase != "baseline") assertTrue(
                    "$name: $result",
                    result.startsWith("ok ")
                )
            }
        }
        bitmap.recycle()
        if (image !== bitmap) image.recycle()
    }

    private suspend fun render(filter: UiFilter<*>, width: Int = 97, height: Int = 83): String {
        val input = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val colors = IntArray(width * height) { index ->
                val x = index % width
                val y = index / width
                Color.argb(
                    255,
                    (x * 17 + y * 5) and 255,
                    (x * 3 + y * 11) and 255,
                    (x * 7 + y * 13) and 255
                )
            }
            setPixels(colors, 0, width, 0, 0, width, height)
        }
        return render(filter, input)
    }

    private suspend fun render(filter: UiFilter<*>, input: Bitmap): String {
        return try {
            val result = provider.filterToTransformation(filter)
                .transform(input, IntegerSize(input.width, input.height))
            val pixels = IntArray(result.width * result.height)
            result.getPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
            val rendered = "ok ${result.width}x${result.height} ${pixels.contentHashCode()}"
            if (result !== input) result.recycle()
            rendered
        } catch (error: Exception) {
            "${error.javaClass.simpleName}: ${error.message?.take(180)}"
        } finally {
            input.recycle()
        }
    }

}
