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

import android.content.Context
import android.graphics.Matrix
import android.graphics.Path
import androidx.core.graphics.PathParser
import com.t8rin.collages.model.CollageLayout
import com.t8rin.collages.view.PhotoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.zip.GZIPInputStream

internal object AdditionalCollages {
    const val count = 512

    @Volatile
    private var layouts: Catalog? = null
    private val mutex = Mutex()

    suspend fun load(context: Context) = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (layouts != null) return@withLock
            val source = context.assets.open("collages/layouts.json.gzip")
                .let(::GZIPInputStream).bufferedReader().use { it.readText() }
            val catalog = JSONObject(source)
            val array = catalog.getJSONArray("layouts")
            layouts = Catalog(
                masks = catalog.getJSONArray("masks"),
                entries = buildMap {
                    repeat(array.length()) {
                        val layout = array.getJSONObject(it)
                        put(layout.getString("id"), layout.getJSONArray("slots"))
                    }
                }
            )
        }
    }

    private data class Catalog(val masks: JSONArray, val entries: Map<String, JSONArray>)

    val ids: Set<String> get() = layouts?.entries?.keys.orEmpty()

    fun create(id: String): CollageLayout? {
        val catalog = layouts ?: return null
        val slots = catalog.entries[id] ?: return null
        return CollageLayout(
            title = id,
            photoItemList = List(slots.length()) { index ->
                val slot = slots.getJSONObject(index)
                val vertices = slot.getJSONArray("points")
                val points = List(vertices.length()) {
                    val point = vertices.getJSONArray(it)
                    CollagePoint(point.getDouble(0).toFloat(), point.getDouble(1).toFloat())
                }
                val bounds = CollageSlot(points)
                PhotoItem(
                    index = index,
                    shape = points,
                    normalizedPath = slot.optInt("mask", -1).takeIf { it >= 0 }
                        ?.let { catalog.masks.getJSONObject(it).toPath() }
                ).apply {
                    bound.set(bounds.left, bounds.top, bounds.right, bounds.bottom)
                }
            }
        )
    }

    private fun JSONObject.toPath(): Path = checkNotNull(
        PathParser.createPathFromPathData(getString("path"))
    ).apply {
        if (optBoolean("evenOdd")) fillType = Path.FillType.EVEN_ODD
        op(this, this, Path.Op.UNION)
        transform(Matrix().apply {
            setScale(1f / getDouble("width").toFloat(), 1f / getDouble("height").toFloat())
        })
    }
}