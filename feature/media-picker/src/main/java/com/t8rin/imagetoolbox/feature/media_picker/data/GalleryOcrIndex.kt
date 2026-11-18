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
import androidx.core.database.sqlite.transaction
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.neural_tools.ocr.GalleryOcr
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.File
import java.text.Normalizer
import java.util.Locale

internal class GalleryOcrIndex(
    context: Context,
    name: String = "gallery-ocr.db"
) : SQLiteOpenHelper(context, File(context.noBackupFilesDir, name).absolutePath, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE photos (
                uri TEXT PRIMARY KEY,
                fingerprint TEXT NOT NULL,
                pipeline INTEGER NOT NULL,
                status INTEGER NOT NULL,
                attempts INTEGER NOT NULL,
                text TEXT NOT NULL,
                searchable_text TEXT NOT NULL
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE photos")
        onCreate(db)
    }

    suspend fun pending(media: List<Media>, pruneMissing: Boolean = true): List<Pending> {
        val coroutine = currentCoroutineContext()
        val available = media.associateBy(Media::uri)
        val current = mutableMapOf<String, Pending?>()
        val stale = mutableListOf<String>()
        val db = writableDatabase
        db.query(
            "photos", arrayOf("uri", "fingerprint", "pipeline", "status", "attempts", "text"),
            null, null, null, null, null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                coroutine.ensureActive()
                val uri = cursor.getString(0)
                val photo = available[uri]
                if (photo == null) {
                    if (pruneMissing) stale.add(uri)
                    continue
                }
                if (photo.fingerprint != cursor.getString(1) ||
                    cursor.getInt(2) != GalleryOcr.PIPELINE_VERSION
                ) {
                    stale.add(uri)
                    continue
                }
                val attempts = cursor.getInt(4)
                current[uri] = if (attempts < MAX_ATTEMPTS && cursor.getInt(3) in RetryStatuses) {
                    Pending(photo, attempts, cursor.getString(5))
                } else null
            }
        }
        if (stale.isNotEmpty()) db.transaction {
            stale.chunked(BATCH_SIZE).forEach { uris ->
                coroutine.ensureActive()
                delete("photos", "uri IN (${uris.joinToString { "?" }})", uris.toTypedArray())
            }
        }
        return media.mapNotNull { photo ->
            if (current.containsKey(photo.uri)) current[photo.uri] else Pending(photo)
        }.sortedWith(
            compareBy<Pending> { it.attempts }
                .thenByDescending { it.media.timestamp }
                .thenBy { it.media.uri }
        )
    }

    fun put(pending: Pending, status: Status, text: String) {
        val photo = pending.media
        writableDatabase.insertWithOnConflict(
            "photos", null,
            ContentValues().apply {
                put("uri", photo.uri)
                put("fingerprint", photo.fingerprint)
                put("pipeline", GalleryOcr.PIPELINE_VERSION)
                put("status", status.ordinal)
                put("attempts", pending.attempts + 1)
                put("text", text)
                put("searchable_text", text.searchable())
            },
            SQLiteDatabase.CONFLICT_REPLACE
        ).also { check(it != -1L) { "Could not save gallery OCR result" } }
    }

    suspend fun search(query: String?, media: List<Media>): List<Media> {
        val phrase = query?.searchable()
        if (phrase?.isEmpty() == true || media.isEmpty()) return emptyList()
        val coroutine = currentCoroutineContext()
        val available = media.filterNot { it.isTrashed }.associateBy(Media::uri)
        val matches = hashSetOf<String>()
        val condition =
            if (phrase == null) "searchable_text <> ''" else "instr(searchable_text, ?) > 0"
        available.keys.chunked(BATCH_SIZE).forEach { uris ->
            coroutine.ensureActive()
            readableDatabase.query(
                "photos", arrayOf("uri", "fingerprint"),
                "pipeline = ? AND uri IN (${uris.joinToString { "?" }}) AND $condition",
                (listOf(GalleryOcr.PIPELINE_VERSION.toString()) + uris + listOfNotNull(phrase)).toTypedArray(),
                null, null, null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val uri = cursor.getString(0)
                    if (available[uri]?.fingerprint == cursor.getString(1)) matches.add(uri)
                }
            }
        }
        return available.values.filter { it.uri in matches }
    }

    suspend fun hasPending(media: List<Media>): Boolean {
        val coroutine = currentCoroutineContext()
        val available = media.filterNot { it.isTrashed }.associateBy(Media::uri)
        return available.keys.chunked(BATCH_SIZE).any { uris ->
            coroutine.ensureActive()
            readableDatabase.query(
                "photos", arrayOf("uri", "fingerprint"),
                "pipeline = ? AND uri IN (${uris.joinToString { "?" }}) " +
                        "AND (attempts >= $MAX_ATTEMPTS OR status NOT IN (${RetryStatuses.joinToString()}))",
                arrayOf(GalleryOcr.PIPELINE_VERSION.toString(), *uris.toTypedArray()),
                null, null, null
            ).use { cursor ->
                cursor.count != uris.size || generateSequence {
                    cursor.takeIf { it.moveToNext() }
                }.any { available[it.getString(0)]?.fingerprint != it.getString(1) }
            }
        }
    }

    enum class Status { Text, NoText, Partial, Unreadable }

    data class Pending(val media: Media, val attempts: Int = 0, val text: String = "")

    companion object {
        const val MAX_ATTEMPTS = 2
        private const val BATCH_SIZE = 400
        private val RetryStatuses = setOf(Status.Partial.ordinal, Status.Unreadable.ordinal)
        private val Whitespace = Regex("[\\s\\p{Z}]+")
        private val Media.fingerprint
            get() = "$generationModified:$timestamp:$size:$width:$height:$mimeType"

        private fun String.searchable() =
            Normalizer.normalize(this, Normalizer.Form.NFKC).lowercase(Locale.ROOT)
                .replace(Whitespace, " ").trim()
    }
}
