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

package com.t8rin.imagetoolbox.feature.media_picker.data

import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.neural_tools.search.ClipImageSearch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClipSemanticSearchInstrumentedTest {

    @Test
    fun imageAndTextEmbeddingsMatchByContent() = runBlocking {
        assumeTrue(
            "Download the semantic search models before running real inference tests",
            ClipImageSearch.isDownloaded()
        )
        val colors = listOf(Color.RED, Color.BLUE, Color.GREEN)
        val images = ClipImageSearch.openImageEncoder().use { encoder ->
            colors.map { color ->
                val bitmap = createBitmap(224, 224).apply { eraseColor(color) }
                try {
                    encoder.encode(bitmap)
                } finally {
                    bitmap.recycle()
                }
            }
        }
        val descriptions = listOf(
            listOf("a red square", "a blue square", "a green square"),
            listOf("красный квадрат", "синий квадрат", "зелёный квадрат")
        )
        val queriesByLanguage = ClipImageSearch.openTextEncoder().use { encoder ->
            descriptions.map { language ->
                language.map { text -> encoder.encode(text) }
            }
        }

        (images + queriesByLanguage.flatten()).forEach { embedding ->
            assertEquals(512, embedding.size)
            assertTrue(embedding.all(Float::isFinite))
            assertEquals(1f, similarity(embedding, embedding), 0.0001f)
        }
        queriesByLanguage.forEachIndexed { languageIndex, queries ->
            images.forEachIndexed { imageIndex, image ->
                val scores = queries.map { query -> similarity(image, query) }
                assertEquals(
                    "The image must match ${descriptions[languageIndex][imageIndex]}: $scores",
                    imageIndex,
                    scores.indices.maxBy(scores::get)
                )
            }
            queries.forEachIndexed { queryIndex, query ->
                val scores = images.map { image -> similarity(image, query) }
                assertEquals(
                    "${descriptions[languageIndex][queryIndex]} must rank its image first: $scores",
                    queryIndex,
                    scores.indices.maxBy(scores::get)
                )
            }
        }
    }

    private fun similarity(first: FloatArray, second: FloatArray): Float =
        first.indices.sumOf { index -> (first[index] * second[index]).toDouble() }.toFloat()
}