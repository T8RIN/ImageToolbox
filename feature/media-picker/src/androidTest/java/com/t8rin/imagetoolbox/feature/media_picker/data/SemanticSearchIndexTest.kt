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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID
import kotlin.math.sqrt

@RunWith(AndroidJUnit4::class)
class SemanticSearchIndexTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "semantic-search-test-${UUID.randomUUID()}.db"
    private var index = SemanticSearchIndex(context, name)

    @After
    fun tearDown() {
        index.close()
        listOf("", "-journal", "-wal", "-shm").forEach { suffix ->
            File(context.noBackupFilesDir, name + suffix).delete()
        }
    }

    @Test
    fun embeddingsSurviveReopeningTheDatabase() {
        val media = media(1)
        val embedding = embedding(0.8f)
        index.put(media, embedding)
        index.close()
        index = SemanticSearchIndex(context, name)

        assertEquals(1, index.count())
        assertArrayEquals(embedding, index.get(media), 0f)
    }

    @Test
    fun changedImageMetadataInvalidatesItsCachedEmbedding() {
        val media = media(1)
        index.put(media, embedding(1f))

        listOf(
            media.copy(timestamp = media.timestamp + 1),
            media.copy(size = requireNotNull(media.size) + 1),
            media.copy(width = requireNotNull(media.width) + 1),
            media.copy(height = requireNotNull(media.height) + 1),
            media.copy(mimeType = "image/jpeg")
        ).forEach { changed ->
            assertNull(
                "Changed metadata must not reuse the embedding: $changed",
                index.get(changed)
            )
        }

        assertArrayEquals(embedding(1f), index.get(media), 0f)
    }

    @Test
    fun reindexingTheSameUriReplacesItsEmbeddingAndMetadata() {
        val old = media(1)
        val updated = old.copy(timestamp = old.timestamp + 1)
        index.put(old, embedding(1f))
        index.put(updated, embedding(0.6f))

        assertEquals(1, index.count())
        assertNull(index.get(old))
        assertArrayEquals(embedding(0.6f), index.get(updated), 0f)
    }

    @Test
    fun invalidEmbeddingsDoNotOverwriteExistingEntries() {
        val media = media(1)
        val original = embedding(1f)
        index.put(media, original)

        listOf(
            FloatArray(0),
            FloatArray(511),
            FloatArray(513),
            original.copyOf().apply { this[1] = Float.NaN },
            original.copyOf().apply { this[1] = Float.POSITIVE_INFINITY }
        ).forEach { invalid ->
            assertThrows(IllegalArgumentException::class.java) { index.put(media, invalid) }
        }

        assertEquals(1, index.count())
        assertArrayEquals(original, index.get(media), 0f)
    }

    @Test
    fun searchRanksCurrentImagesAndPreservesInputOrderForEqualScores() = runBlocking {
        val best = media(1)
        val firstTie = media(3)
        val secondTie = media(2)
        val unrelated = media(4)
        val removed = media(5)
        val changed = media(6)
        val notIndexed = media(7)
        index.put(secondTie, embedding(0.8f))
        index.put(best, embedding(1f))
        index.put(firstTie, embedding(0.8f))
        index.put(unrelated, embedding(0.19f))
        index.put(removed, embedding(1f))
        index.put(changed, embedding(1f))

        val results = index.search(
            embedding = embedding(1f),
            media = listOf(
                firstTie,
                unrelated,
                changed.copy(timestamp = changed.timestamp + 1),
                best,
                notIndexed,
                secondTie
            )
        )

        assertEquals(listOf(best, firstTie, secondTie), results)
    }

    @Test
    fun resultsAreLimitedToTheMostRelevantHundredImagesAcrossQueryChunks() = runBlocking {
        val rankedMedia = List(1205) { position ->
            media(position.toLong() + 1) to (position * 37 % 1205)
        }
        rankedMedia.forEach { (item, rank) -> index.put(item, embedding(0.21f + rank / 1600f)) }

        val results = index.search(embedding(1f), rankedMedia.map { it.first })

        assertEquals(
            rankedMedia.sortedByDescending { it.second }.take(100).map { it.first },
            results
        )
    }

    @Test
    fun categoriesExcludeUnavailableAndChangedImagesAndKeepEmptyCategories() = runBlocking {
        val person = media(1)
        val animal = media(2)
        val shared = media(3)
        val changed = media(4)
        val unavailable = media(5)
        val notIndexed = media(6)
        index.put(person, embedding(1f))
        index.put(animal, embedding(0f))
        index.put(shared, embedding(0.6f))
        index.put(changed, embedding(1f))
        index.put(unavailable, embedding(1f))

        val results = index.categories(
            classifier = classifier(),
            media = listOf(
                shared,
                animal,
                changed.copy(timestamp = changed.timestamp + 1),
                notIndexed,
                person,
                person
            )
        ).associate { it.category to it.media }

        assertEquals(
            setOf(
                SemanticSearchCategory.BuiltIn.People,
                SemanticSearchCategory.BuiltIn.Animals,
                SemanticSearchCategory.BuiltIn.Art
            ),
            results.keys
        )
        assertEquals(
            setOf(person, shared),
            results.getValue(SemanticSearchCategory.BuiltIn.People).toSet()
        )
        assertEquals(
            setOf(animal, shared),
            results.getValue(SemanticSearchCategory.BuiltIn.Animals).toSet()
        )
        assertEquals(2, results.getValue(SemanticSearchCategory.BuiltIn.People).size)
        assertTrue(results.getValue(SemanticSearchCategory.BuiltIn.Art).isEmpty())
    }

    @Test
    fun categoriesIncludeAllMatchesAcrossDatabaseChunksWithoutTheSearchResultsLimit() =
        runBlocking {
            val media = List(1205) { media(it.toLong() + 1) }
            media.forEach { index.put(it, embedding(1f)) }

            val results = index.categories(classifier(), media)
                .single { it.category == SemanticSearchCategory.BuiltIn.People }

            assertEquals(1205, results.media.size)
            assertEquals(media.toSet(), results.media.toSet())
            assertEquals(100, index.search(embedding(1f), media).size)
        }

    @Test
    fun customThresholdChangesReclassifyAllCachedMatchesWithoutReindexing() = runBlocking {
        val category = SemanticSearchCategory.Custom("custom", "My photos", "a colorful pattern")
        val media = List(1205) { media(it.toLong() + 1) }
        media.forEach { index.put(it, embedding(0.35f)) }
        fun classifier(threshold: Float) = SemanticSearchClassifier(
            categoryEmbeddings = mapOf(category to listOf(embedding(1f))),
            backgroundEmbeddings = listOf(embedding(0f)),
            thresholds = mapOf(category.id to threshold)
        )

        val broad = index.categories(classifier(0.3f), media).single()
        val strict = index.categories(classifier(0.4f), media).single()
        val restored = index.categories(classifier(0.3f), media).single()

        assertEquals(category, broad.category)
        assertEquals(media.toSet(), broad.media.toSet())
        assertEquals(category, strict.category)
        assertTrue(strict.media.isEmpty())
        assertEquals(broad, restored)
        assertEquals(1205, index.count())
        assertArrayEquals(embedding(0.35f), index.get(media.first()), 0f)
    }

    @Test
    fun cancelledClassificationDoesNotReturnPartialCategories() = runBlocking {
        val media = listOf(media(1), media(2))
        media.forEach { index.put(it, embedding(1f)) }
        val job = Job()

        try {
            withContext(job) {
                job.cancel()
                index.categories(classifier(), media)
                fail("Cancelled classification must stop before returning categories")
            }
            fail("Classification cancellation must be propagated")
        } catch (_: CancellationException) {
            assertTrue(job.isCancelled)
        }
    }

    @Test
    fun searchExcludesWeakMatchesAtTheConfidenceThreshold() = runBlocking {
        val weak = media(1)
        val matching = media(2)
        index.put(weak, embedding(0.25f))
        index.put(matching, embedding(0.26f))

        val results = index.search(
            embeddings = listOf(embedding(1f)),
            media = listOf(weak, matching),
            minimumSimilarity = 0.26f,
            limit = Int.MAX_VALUE
        )

        assertEquals(listOf(matching), results.single())
        assertEquals(listOf(matching), index.search(embedding(1f), listOf(weak, matching)))
    }

    @Test
    fun cancelledSearchDoesNotReturnPartialResults() = runBlocking {
        val media = listOf(media(1), media(2))
        media.forEach { index.put(it, embedding(1f)) }
        val job = Job()

        try {
            withContext(job) {
                job.cancel()
                index.search(embedding(1f), media)
                fail("Cancelled search must stop before returning results")
            }
            fail("Search cancellation must be propagated")
        } catch (_: CancellationException) {
            assertTrue(job.isCancelled)
        }
    }

    @Test
    fun clearingTheIndexPersistsAndAllowsIndexingAgain() = runBlocking {
        val media = media(1)
        index.put(media, embedding(1f))
        index.clear()
        index.close()
        index = SemanticSearchIndex(context, name)

        assertEquals(0, index.count())
        assertTrue(index.search(embedding(1f), listOf(media)).isEmpty())
        index.put(media, embedding(0.5f))
        assertArrayEquals(embedding(0.5f), index.get(media), 0f)
    }

    private fun classifier() = SemanticSearchClassifier(
        categoryEmbeddings = mapOf(
            SemanticSearchCategory.BuiltIn.People to listOf(embedding(1f)),
            SemanticSearchCategory.BuiltIn.Animals to listOf(embedding(0f)),
            SemanticSearchCategory.BuiltIn.Art to listOf(FloatArray(512).apply { this[2] = 1f })
        ),
        backgroundEmbeddings = listOf(FloatArray(512).apply { this[3] = 1f })
    )

    private fun embedding(similarity: Float) = FloatArray(512).apply {
        this[0] = similarity
        this[1] = sqrt(1f - similarity * similarity)
    }

    private fun media(id: Long) = Media(
        id = id,
        label = "$id.png",
        uri = "content://media/external/images/media/$id",
        path = "/Pictures/$id.png",
        relativePath = "Pictures/",
        albumID = 1,
        albumLabel = "Pictures",
        timestamp = 1_700_000_000,
        mimeType = "image/png",
        width = 640,
        height = 480,
        size = 1024
    )
}