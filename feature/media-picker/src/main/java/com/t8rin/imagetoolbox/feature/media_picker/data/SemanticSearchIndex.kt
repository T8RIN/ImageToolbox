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

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.yield
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.PriorityQueue

internal class SemanticSearchIndex(
    context: Context,
    name: String = "semantic-search-clip-v2.db"
) : SQLiteOpenHelper(context, File(context.noBackupFilesDir, name).absolutePath, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE embeddings (uri TEXT PRIMARY KEY, fingerprint TEXT NOT NULL, vector BLOB NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS embeddings")
        onCreate(db)
    }

    fun get(media: Media): FloatArray? = readableDatabase.query(
        "embeddings", arrayOf("vector"), "uri = ? AND fingerprint = ?",
        arrayOf(media.uri, media.fingerprint), null, null, null
    ).use { cursor ->
        if (cursor.moveToFirst()) cursor.getBlob(0).toEmbedding() else null
    }

    fun put(media: Media, embedding: FloatArray) {
        require(embedding.size == EMBEDDING_SIZE && embedding.all(Float::isFinite))
        val buffer = ByteBuffer.allocate(EMBEDDING_SIZE * Float.SIZE_BYTES)
            .order(ByteOrder.LITTLE_ENDIAN)
        embedding.forEach(buffer::putFloat)
        writableDatabase.insertWithOnConflict(
            "embeddings", null,
            ContentValues().apply {
                put("uri", media.uri)
                put("fingerprint", media.fingerprint)
                put("vector", buffer.array())
            },
            SQLiteDatabase.CONFLICT_REPLACE
        ).also { check(it != -1L) { "Could not save image index" } }
    }

    fun count(): Int = readableDatabase.rawQuery(
        "SELECT COUNT(*) FROM embeddings", null
    ).use { cursor ->
        cursor.moveToFirst()
        cursor.getInt(0)
    }

    fun clear() {
        writableDatabase.delete("embeddings", null, null)
    }

    suspend fun search(
        embedding: FloatArray,
        media: List<Media>,
        minimumSimilarity: Float = SIMILARITY_THRESHOLD
    ): List<Media> = search(listOf(embedding), media, minimumSimilarity).single()

    suspend fun search(
        embeddings: List<FloatArray>,
        media: List<Media>,
        minimumSimilarity: Float = SIMILARITY_THRESHOLD,
        limit: Int = RESULT_LIMIT
    ): List<List<Media>> {
        require(embeddings.all { it.size == EMBEDDING_SIZE && it.all(Float::isFinite) })
        require(limit > 0 && minimumSimilarity.isFinite())
        currentCoroutineContext().ensureActive()
        val bestFirst = compareByDescending<Match> { it.score }.thenBy { it.order }
        val matches = embeddings.map {
            PriorityQueue(minOf(limit, RESULT_LIMIT), bestFirst.reversed())
        }

        forEachEmbedding(media) { item, vector ->
            embeddings.forEachIndexed { position, embedding ->
                var score = 0f
                for (index in embedding.indices) score += embedding[index] * vector[index]
                if (score >= minimumSimilarity) {
                    matches[position].apply {
                        add(Match(item.value, score, item.index))
                        if (size > limit) poll()
                    }
                }
            }
        }

        return matches.map { category -> category.sortedWith(bestFirst).map(Match::media) }
    }

    suspend fun categories(
        classifier: SemanticSearchClassifier,
        media: List<Media>
    ): List<SemanticSearchCategoryResult> {
        val matches = classifier.categories.associateWith { mutableListOf<Media>() }
        forEachEmbedding(media) { item, vector ->
            classifier.classify(vector).forEach { category ->
                matches.getValue(category).add(item.value)
            }
        }
        return matches.map { (category, images) ->
            SemanticSearchCategoryResult(category, images)
        }
    }

    private suspend fun forEachEmbedding(
        media: List<Media>,
        action: (IndexedValue<Media>, FloatArray) -> Unit
    ) {
        currentCoroutineContext().ensureActive()
        val available = media.withIndex().associateBy { it.value.uri }
        // Read each current image vector once, including when classifying several categories.
        available.keys.chunked(500).forEach { uris ->
            currentCoroutineContext().ensureActive()
            readableDatabase.query(
                "embeddings", arrayOf("uri", "fingerprint", "vector"),
                "uri IN (${uris.joinToString { "?" }})", uris.toTypedArray(), null, null, null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    currentCoroutineContext().ensureActive()
                    if (cursor.position % 64 == 0) yield()
                    val item = available[cursor.getString(0)] ?: continue
                    if (cursor.getString(1) != item.value.fingerprint) continue
                    val vector = cursor.getBlob(2).toEmbedding() ?: continue
                    action(item, vector)
                }
            }
        }
    }

    private fun ByteArray.toEmbedding(): FloatArray? {
        if (size != EMBEDDING_SIZE * Float.SIZE_BYTES) return null
        val buffer = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()
        return FloatArray(EMBEDDING_SIZE).also(buffer::get).takeIf { it.all(Float::isFinite) }
    }

    private val Media.fingerprint: String
        get() = "$timestamp:$size:$width:$height:$mimeType"

    private data class Match(val media: Media, val score: Float, val order: Int)

    private companion object {
        const val EMBEDDING_SIZE = 512
        const val RESULT_LIMIT = 100
        const val SIMILARITY_THRESHOLD = 0.26f
    }

}