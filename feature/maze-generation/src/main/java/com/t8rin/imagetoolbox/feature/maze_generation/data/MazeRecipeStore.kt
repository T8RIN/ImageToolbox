/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.maze_generation.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.feature.maze_generation.domain.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MazeRecipeStore
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersHolder,
) {
    private val preferences =
        context.getSharedPreferences("maze_generation_v1", Context.MODE_PRIVATE)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encode(recipe: MazeRecipe): String = json.encodeToString(recipe)

    fun decode(text: String): MazeRecipe {
        require(text.length <= 500_000)
        return json.decodeFromString<MazeRecipe>(text).validated()
    }

    suspend fun loadLast(): MazeRecipe =
        withContext(dispatchers.ioDispatcher) {
            runCatching { decode(preferences.getString("last", null) ?: "") }
                .getOrDefault(MazeRecipe())
        }

    suspend fun saveLast(recipe: MazeRecipe) =
        withContext(dispatchers.ioDispatcher) {
            preferences.edit().putString("last", encode(recipe)).apply()
        }

    suspend fun sampleMask(
        uri: Uri,
        rows: Int,
        columns: Int,
        threshold: Int,
        invert: Boolean,
    ): List<Boolean> =
        withContext(dispatchers.ioDispatcher) {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)!!.use {
                BitmapFactory.decodeStream(it, null, bounds)
            }
            require(bounds.outWidth > 0 && bounds.outHeight > 0)
            var sample = 1
            while (bounds.outWidth / sample > 1024 || bounds.outHeight / sample > 1024) sample *= 2
            val options = BitmapFactory.Options().apply { inSampleSize = sample }
            val bitmap =
                context.contentResolver.openInputStream(uri)!!.use {
                    BitmapFactory.decodeStream(it, null, options)
                }
            requireNotNull(bitmap)
            try {
                List(rows * columns) { i ->
                        val x =
                            ((i % columns + 0.5) * bitmap.width / columns)
                                .toInt()
                                .coerceAtMost(bitmap.width - 1)
                        val y =
                            ((i / columns + 0.5) * bitmap.height / rows)
                                .toInt()
                                .coerceAtMost(bitmap.height - 1)
                        val color = bitmap.getPixel(x, y)
                        val luminance =
                            ((color shr 16 and 255) * 299 +
                                (color shr 8 and 255) * 587 +
                                (color and 255) * 114) / 1000
                        (color ushr 24) > 127 && ((luminance < threshold) xor invert)
                    }
                    .also { require(it.any { pixel -> pixel }) { "Empty image mask" } }
            } finally {
                bitmap.recycle()
            }
        }
}
