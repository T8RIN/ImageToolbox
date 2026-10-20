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
import android.graphics.RectF
import androidx.core.database.sqlite.transaction
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

internal class SemanticFaceIndex(context: Context) : SQLiteOpenHelper(
    context,
    File(context.noBackupFilesDir, "semantic-search-faces-v1.db").absolutePath,
    null,
    3
) {

    val thumbnails = File(context.noBackupFilesDir, "semantic-faces").apply(File::mkdirs)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE people (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL DEFAULT '', " +
                    "threshold REAL NOT NULL DEFAULT 0.55, hidden INTEGER NOT NULL DEFAULT 0, cover TEXT, " +
                    "pinned INTEGER NOT NULL DEFAULT 0, cover_face TEXT)"
        )
        db.execSQL(
            "CREATE TABLE faces (id TEXT PRIMARY KEY, uri TEXT NOT NULL, fingerprint TEXT NOT NULL, " +
                    "person_id INTEGER NOT NULL, vector BLOB NOT NULL, thumbnail TEXT NOT NULL, " +
                    "media_id INTEGER NOT NULL, left REAL NOT NULL, top REAL NOT NULL, " +
                    "right REAL NOT NULL, bottom REAL NOT NULL, confidence REAL NOT NULL)"
        )
        db.execSQL("CREATE INDEX faces_person ON faces(person_id)")
        db.execSQL("CREATE INDEX faces_uri ON faces(uri)")
        db.execSQL(
            "CREATE TABLE face_links (media_id INTEGER NOT NULL, left REAL NOT NULL, top REAL NOT NULL, " +
                    "right REAL NOT NULL, bottom REAL NOT NULL, person_id INTEGER NOT NULL)"
        )
        db.createRejectedPairs()
        db.createFaceExclusions()
        db.execSQL("CREATE TABLE metadata (key TEXT PRIMARY KEY, value INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE scans (uri TEXT PRIMARY KEY, fingerprint TEXT NOT NULL)")
        db.execSQL(
            "CREATE TABLE exclusions (person_id INTEGER NOT NULL, uri TEXT NOT NULL, " +
                    "PRIMARY KEY(person_id, uri))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) db.createRejectedPairs()
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE people ADD COLUMN cover_face TEXT")
            db.createFaceExclusions()
        }
    }

    private fun SQLiteDatabase.createRejectedPairs() {
        execSQL(
            "CREATE TABLE IF NOT EXISTS rejected_pairs (first_id INTEGER NOT NULL, second_id INTEGER NOT NULL, " +
                    "PRIMARY KEY(first_id, second_id), CHECK(first_id < second_id))"
        )
    }

    private fun SQLiteDatabase.createFaceExclusions() {
        execSQL(
            "CREATE TABLE IF NOT EXISTS face_exclusions (person_id INTEGER NOT NULL, uri TEXT NOT NULL, " +
                    "media_id INTEGER NOT NULL, left REAL NOT NULL, top REAL NOT NULL, right REAL NOT NULL, " +
                    "bottom REAL NOT NULL, PRIMARY KEY(person_id, media_id, left, top, right, bottom))"
        )
    }

    fun needsScan(media: Media): Boolean = readableDatabase.query(
        "scans",
        arrayOf("fingerprint"),
        "uri = ?",
        arrayOf(media.uri),
        null,
        null,
        null
    ).use { !it.moveToFirst() || it.getString(0) != media.fingerprint }

    class Face(
        val id: String,
        val vector: FloatArray,
        val thumbnail: String,
        val bounds: RectF,
        val confidence: Float
    )

    private class Group(
        val id: Long,
        val threshold: Float,
        val vector: FloatArray,
        val mediaIds: Set<Long> = emptySet()
    )

    private class FacePosition(val mediaId: Long, val bounds: RectF) {
        val id: String get() = "$mediaId:${bounds.left}:${bounds.top}:${bounds.right}:${bounds.bottom}"

        fun matches(other: FacePosition): Boolean {
            if (mediaId != other.mediaId) return false
            val width =
                (minOf(bounds.right, other.bounds.right) - maxOf(bounds.left, other.bounds.left))
                    .coerceAtLeast(0f)
            val height =
                (minOf(bounds.bottom, other.bounds.bottom) - maxOf(bounds.top, other.bounds.top))
                    .coerceAtLeast(0f)
            val intersection = width * height
            val union =
                bounds.width() * bounds.height() + other.bounds.width() * other.bounds.height() - intersection
            return union > 0f && intersection / union >= 0.5f
        }
    }

    private class IndexedFace(
        val media: Media,
        val face: SemanticSearchCategory.PersonFace,
        val position: FacePosition,
        val excluded: Boolean
    )

    private fun String.toFacePosition(): FacePosition? {
        val parts = split(':')
        if (parts.size != 5) return null
        val mediaId = parts[0].toLongOrNull()
        val values = parts.drop(1).mapNotNull { it.toFloatOrNull()?.takeIf(Float::isFinite) }
        return if (mediaId != null && values.size == 4 && values[0] < values[2] && values[1] < values[3]) {
            FacePosition(mediaId, RectF(values[0], values[1], values[2], values[3]))
        } else {
            null
        }
    }

    private fun faceExclusions(): Map<Long, List<FacePosition>> {
        val result = mutableMapOf<Long, MutableList<FacePosition>>()
        readableDatabase.query("face_exclusions", null, null, null, null, null, null)
            .use { cursor ->
                while (cursor.moveToNext()) {
                    result.getOrPut(cursor.getLong(cursor.getColumnIndexOrThrow("person_id"))) { mutableListOf() }
                        .add(
                            FacePosition(
                                cursor.getLong(cursor.getColumnIndexOrThrow("media_id")),
                                RectF(
                                    cursor.getFloat(cursor.getColumnIndexOrThrow("left")),
                                    cursor.getFloat(cursor.getColumnIndexOrThrow("top")),
                                    cursor.getFloat(cursor.getColumnIndexOrThrow("right")),
                                    cursor.getFloat(cursor.getColumnIndexOrThrow("bottom"))
                                )
                            )
                        )
                }
            }
        return result
    }

    private var groupRepresentatives: MutableList<Group>? = null
    private var peopleRevision = -1L

    fun invalidatePeopleCache(revision: Long) {
        if (peopleRevision != revision) {
            groupRepresentatives = null
            peopleRevision = revision
        }
    }

    private fun representatives(): MutableList<Group> {
        val groups = mutableListOf<Group>()
        val excludedFaces = faceExclusions()
        readableDatabase.rawQuery(
            "SELECT p.id, p.threshold, f.vector, f.media_id, f.left, f.top, f.right, f.bottom FROM people p " +
                    "JOIN faces f ON f.person_id = p.id WHERE NOT EXISTS " +
                    "(SELECT 1 FROM exclusions e WHERE e.person_id = p.id AND e.uri = f.uri) " +
                    "ORDER BY p.id, f.id",
            null
        ).use { cursor ->
            var id = -1L
            var count = 0
            var sum = FloatArray(512)
            var threshold = 0.55f
            var mediaIds = hashSetOf<Long>()
            fun append() {
                if (count > 0) groups += Group(id, threshold, normalize(sum), mediaIds)
            }
            while (cursor.moveToNext()) {
                val next = cursor.getLong(0)
                val position = FacePosition(
                    cursor.getLong(3),
                    RectF(
                        cursor.getFloat(4),
                        cursor.getFloat(5),
                        cursor.getFloat(6),
                        cursor.getFloat(7)
                    )
                )
                if (excludedFaces[next].orEmpty().any { it.matches(position) }) continue
                if (next != id) {
                    append()
                    id = next
                    threshold = cursor.getFloat(1)
                    sum = FloatArray(512)
                    count = 0
                    mediaIds = hashSetOf()
                }
                mediaIds += cursor.getLong(3)
                val vector = decode(cursor.getBlob(2))
                for (i in sum.indices) sum[i] += vector[i]
                count++
            }
            append()
        }
        return groups
    }

    private fun faceLinks(mediaId: Long): List<Pair<Long, FacePosition>> = readableDatabase.query(
        "face_links",
        null,
        "media_id = ?",
        arrayOf(mediaId.toString()),
        null,
        null,
        null
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                add(
                    cursor.getLong(cursor.getColumnIndexOrThrow("person_id")) to FacePosition(
                        mediaId,
                        RectF(
                            cursor.getFloat(cursor.getColumnIndexOrThrow("left")),
                            cursor.getFloat(cursor.getColumnIndexOrThrow("top")),
                            cursor.getFloat(cursor.getColumnIndexOrThrow("right")),
                            cursor.getFloat(cursor.getColumnIndexOrThrow("bottom"))
                        )
                    )
                )
            }
        }
    }

    fun replace(media: Media, faces: List<Face>) {
        val groups = groupRepresentatives ?: representatives().also { groupRepresentatives = it }
        val links = faceLinks(media.id)
        val db = writableDatabase
        val oldThumbnails = db.query(
            "faces",
            arrayOf("thumbnail"),
            "uri = ?",
            arrayOf(media.uri),
            null,
            null,
            null
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.getString(0))
            }
        }
        db.transaction {
            db.delete("faces", "uri = ?", arrayOf(media.uri))
            for (face in faces) {
                require(face.vector.size == 512 && face.vector.all(Float::isFinite))
                val group = groups.map { it to similarity(it.vector, face.vector) }
                    .filter { (group, score) -> score >= group.threshold }
                    .maxByOrNull { it.second }?.first
                val position = FacePosition(media.id, face.bounds)
                val person = links.firstOrNull { it.second.matches(position) }?.first ?: group?.id
                ?: db.insertOrThrow(
                    "people", null, ContentValues().apply { put("name", "") }
                ).also { groups += Group(it, 0.55f, face.vector) }
                db.insertOrThrow(
                    "faces",
                    null,
                    ContentValues().apply {
                        put("id", face.id)
                        put("uri", media.uri)
                        put("fingerprint", media.fingerprint)
                        put("person_id", person)
                        put("vector", encode(face.vector))
                        put("thumbnail", face.thumbnail)
                        put("media_id", media.id)
                        put("left", face.bounds.left)
                        put("top", face.bounds.top)
                        put("right", face.bounds.right)
                        put("bottom", face.bounds.bottom)
                        put("confidence", face.confidence)
                    }
                )
            }
            db.insertWithOnConflict(
                "scans",
                null,
                ContentValues().apply {
                    put("uri", media.uri)
                    put("fingerprint", media.fingerprint)
                },
                SQLiteDatabase.CONFLICT_REPLACE
            )
            db.insertWithOnConflict(
                "metadata",
                null,
                ContentValues().apply {
                    put("key", "groups_dirty")
                    put("value", 1)
                },
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
        oldThumbnails.filterNot { old -> faces.any { it.thumbnail == old } }
            .forEach { File(it).delete() }
    }

    fun needsRegroup(): Boolean = readableDatabase.rawQuery(
        "SELECT key, value FROM metadata WHERE key IN ('groups_dirty', 'grouping_version')",
        null
    ).use { cursor ->
        var dirty = false
        var version = 0
        while (cursor.moveToNext()) {
            when (cursor.getString(0)) {
                "groups_dirty" -> dirty = cursor.getInt(1) != 0
                "grouping_version" -> version = cursor.getInt(1)
            }
        }
        dirty || version != FaceClusterer.VERSION
    }

    suspend fun regroup() {
        val faces = readableDatabase.rawQuery(
            "SELECT rowid, * FROM faces ORDER BY uri, id",
            null
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        FaceClusterer.Face(
                            id = cursor.getLong(0),
                            mediaId = cursor.getLong(cursor.getColumnIndexOrThrow("media_id")),
                            embedding = decode(cursor.getBlob(cursor.getColumnIndexOrThrow("vector"))),
                            left = cursor.getFloat(cursor.getColumnIndexOrThrow("left")),
                            top = cursor.getFloat(cursor.getColumnIndexOrThrow("top")),
                            right = cursor.getFloat(cursor.getColumnIndexOrThrow("right")),
                            bottom = cursor.getFloat(cursor.getColumnIndexOrThrow("bottom")),
                            confidence = cursor.getFloat(cursor.getColumnIndexOrThrow("confidence")),
                            personId = cursor.getLong(cursor.getColumnIndexOrThrow("person_id"))
                                .takeIf { it > 0 }?.toString()
                        )
                    )
                }
            }
        }
        val seeds = mutableListOf<FaceClusterer.PersonSeed>()
        val pins = mutableListOf<FaceClusterer.FaceAssertion>()
        readableDatabase.query("people", null, null, null, null, null, "id").use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id")).toString()
                val members = faces.filter { it.personId == id }
                val hidden = cursor.getInt(cursor.getColumnIndexOrThrow("hidden")) != 0
                val threshold = cursor.getFloat(cursor.getColumnIndexOrThrow("threshold"))
                seeds += FaceClusterer.PersonSeed(
                    id = id,
                    curated = hidden || cursor.getInt(cursor.getColumnIndexOrThrow("pinned")) != 0 ||
                            cursor.getString(cursor.getColumnIndexOrThrow("name")).isNotBlank() ||
                            threshold != 0.55f || !cursor.isNull(cursor.getColumnIndexOrThrow("cover")) ||
                            !cursor.isNull(cursor.getColumnIndexOrThrow("cover_face")),
                    faceCount = members.size,
                    threshold = threshold
                )
            }
        }
        readableDatabase.query("face_links", null, null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                pins += FaceClusterer.FaceAssertion(
                    cursor.getLong(cursor.getColumnIndexOrThrow("media_id")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("left")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("top")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("right")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("bottom")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("person_id")).toString(), true
                )
            }
        }
        faceExclusions().forEach { (person, positions) ->
            positions.forEach { position ->
                val bounds = position.bounds
                pins += FaceClusterer.FaceAssertion(
                    position.mediaId,
                    bounds.left,
                    bounds.top,
                    bounds.right,
                    bounds.bottom,
                    person.toString(),
                    false
                )
            }
        }
        val exclusions = readableDatabase.rawQuery(
            "SELECT DISTINCT e.person_id, f.media_id FROM exclusions e JOIN faces f ON f.uri = e.uri",
            null
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        FaceClusterer.MediaAssertion(
                            cursor.getLong(1),
                            cursor.getLong(0).toString()
                        )
                    )
                }
            }
        }
        val result = FaceClusterer.cluster(faces, seeds, pins, exclusions)
        currentCoroutineContext().ensureActive()
        val db = writableDatabase
        db.transaction {
            fun assign(id: Long, component: FaceClusterer.Component) {
                component.faceIds.forEach { face ->
                    db.update(
                        "faces",
                        ContentValues().apply { put("person_id", id) },
                        "rowid = ?",
                        arrayOf(face.toString())
                    )
                }
            }
            result.resolved.forEach { (id, component) -> assign(id.toLong(), component) }
            result.fresh.forEach { component ->
                val id = db.insertOrThrow("people", null, ContentValues().apply { put("name", "") })
                assign(id, component)
            }
            result.unassignedFaceIds.forEach { id ->
                val old = faces.first { it.id == id }
                val excluded = exclusions.any {
                    it.mediaId == old.mediaId && it.personId == old.personId
                } || pins.any {
                    !it.include && it.personId == old.personId &&
                            FacePosition(
                                it.mediaId,
                                RectF(it.left, it.top, it.right, it.bottom)
                            ).matches(
                                FacePosition(
                                    old.mediaId,
                                    RectF(old.left, old.top, old.right, old.bottom)
                                )
                            )
                }
                if (!excluded) {
                    db.update(
                        "faces",
                        ContentValues().apply { put("person_id", 0) },
                        "rowid = ?",
                        arrayOf(id.toString())
                    )
                }
            }
            db.delete("metadata", "key = ?", arrayOf("groups_dirty"))
            db.insertWithOnConflict(
                "metadata",
                null,
                ContentValues().apply {
                    put("key", "grouping_version")
                    put("value", FaceClusterer.VERSION)
                },
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
        groupRepresentatives = null
    }

    fun embeddings(media: Media): List<FloatArray> = readableDatabase.query(
        "faces",
        arrayOf("vector"),
        "uri = ? AND fingerprint = ?",
        arrayOf(media.uri, media.fingerprint),
        null,
        null,
        null
    ).use { cursor ->
        buildList { while (cursor.moveToNext()) add(decode(cursor.getBlob(0))) }
    }

    suspend fun search(
        vectors: List<FloatArray>,
        media: List<Media>,
        threshold: Float
    ): List<Media> {
        if (vectors.isEmpty() || media.isEmpty()) return emptyList()
        val available = media.associateBy(Media::uri)
        val scores = mutableMapOf<String, Float>()
        readableDatabase.query(
            "faces",
            arrayOf("uri", "fingerprint", "vector"),
            null,
            null,
            null,
            null,
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                currentCoroutineContext().ensureActive()
                val image = available[cursor.getString(0)] ?: continue
                if (image.fingerprint != cursor.getString(1)) continue
                val vector = decode(cursor.getBlob(2))
                val score = vectors.maxOf { similarity(it, vector) }
                if (score >= threshold && score > (scores[image.uri] ?: -1f)) {
                    scores[image.uri] = score
                }
            }
        }
        return scores.entries.sortedByDescending { it.value }.map { available.getValue(it.key) }
    }

    fun people(media: List<Media>): List<SemanticSearchCategoryResult> {
        val available = media.associateBy(Media::uri)
        val excludedFaces = faceExclusions()
        val matches = linkedMapOf<Long, MutableList<IndexedFace>>()
        readableDatabase.rawQuery(
            "SELECT f.person_id, f.uri, f.fingerprint, f.thumbnail, f.media_id, f.left, f.top, f.right, f.bottom, " +
                    "EXISTS (SELECT 1 FROM exclusions e WHERE e.person_id = f.person_id AND e.uri = f.uri) " +
                    "FROM faces f ORDER BY f.uri, f.id",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val image = available[cursor.getString(1)] ?: continue
                if (image.fingerprint != cursor.getString(2)) continue
                val person = cursor.getLong(0)
                val position = FacePosition(
                    cursor.getLong(4),
                    RectF(
                        cursor.getFloat(5),
                        cursor.getFloat(6),
                        cursor.getFloat(7),
                        cursor.getFloat(8)
                    )
                )
                val match = IndexedFace(
                    media = image,
                    face = SemanticSearchCategory.PersonFace(
                        id = position.id,
                        uri = image.uri,
                        thumbnail = File(cursor.getString(3)).toUri().toString()
                    ),
                    position = position,
                    excluded = cursor.getInt(9) != 0 || excludedFaces[person].orEmpty()
                        .any { it.matches(position) }
                )
                matches.getOrPut(person) { mutableListOf() }.add(match)
            }
        }
        val result = readableDatabase.rawQuery(
            "SELECT p.*, (SELECT COUNT(DISTINCT uri) FROM (SELECT person_id, uri FROM exclusions UNION ALL " +
                    "SELECT person_id, uri FROM face_exclusions) e WHERE e.person_id = p.id) AS excluded_count " +
                    "FROM people p ORDER BY p.id",
            null
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                    val availableFaces = matches[id].orEmpty()
                    if (availableFaces.isEmpty()) continue
                    val faces = availableFaces.filterNot { it.excluded }
                    val cover = cursor.getString(cursor.getColumnIndexOrThrow("cover"))
                    val coverPosition = cursor.getString(cursor.getColumnIndexOrThrow("cover_face"))
                        ?.toFacePosition()
                    val coverFace =
                        faces.firstOrNull { coverPosition?.matches(it.position) == true }
                            ?: faces.firstOrNull { it.media.uri == cover }
                            ?: faces.firstOrNull() ?: availableFaces.first()
                    add(
                        SemanticSearchCategoryResult(
                            SemanticSearchCategory.Person(
                                personId = id,
                                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                thumbnail = coverFace.face.thumbnail,
                                threshold = cursor.getFloat(cursor.getColumnIndexOrThrow("threshold")),
                                excludedPhotos = cursor.getInt(cursor.getColumnIndexOrThrow("excluded_count")),
                                coverUri = coverFace.media.uri,
                                hidden = cursor.getInt(cursor.getColumnIndexOrThrow("hidden")) != 0,
                                faces = faces.map { it.face },
                                coverFaceId = coverFace.face.id
                            ),
                            faces.map { it.media }.distinctBy(Media::uri)
                        )
                    )
                }
            }
        }
        return similarPeople(result)
    }

    private fun similarPeople(
        people: List<SemanticSearchCategoryResult>
    ): List<SemanticSearchCategoryResult> {
        val visible = people.map { it.category as SemanticSearchCategory.Person }
            .filterNot { it.hidden }.mapTo(hashSetOf()) { it.personId }
        if (visible.size < 2) return people
        val groups = representatives().filter { it.id in visible }
        val rejected =
            readableDatabase.rawQuery("SELECT first_id, second_id FROM rejected_pairs", null).use {
                buildSet {
                    while (it.moveToNext()) add(it.getLong(0) to it.getLong(1))
                }
            }
        val matches = mutableMapOf<Long, MutableList<Pair<Long, Float>>>()
        for (first in groups.indices) {
            val a = groups[first]
            for (second in first + 1 until groups.size) {
                val b = groups[second]
                val pair = minOf(a.id, b.id) to maxOf(a.id, b.id)
                if (pair in rejected || a.mediaIds.any(b.mediaIds::contains)) continue
                val score = similarity(a.vector, b.vector)
                if (score >= 0.45f) {
                    matches.getOrPut(a.id) { mutableListOf() }.add(b.id to score)
                    matches.getOrPut(b.id) { mutableListOf() }.add(a.id to score)
                }
            }
        }
        return people.map { result ->
            val person = result.category as SemanticSearchCategory.Person
            val similar = matches[person.personId].orEmpty().sortedWith(
                compareByDescending<Pair<Long, Float>> { it.second }.thenBy { it.first }
            ).toMap()
            result.copy(category = person.copy(similarPeople = similar))
        }
    }

    fun updatePerson(
        id: Long,
        name: String,
        threshold: Float,
        cover: String?,
        excludedUris: Set<String> = emptySet(),
        restoreExcluded: Boolean = false,
        excludedFaces: Set<String> = emptySet(),
        coverFaceId: String? = null
    ): Boolean {
        val newThreshold = threshold.coerceIn(0.5f, 0.85f)
        val db = writableDatabase
        return db.transaction {
            val oldThreshold = db.query(
                "people",
                arrayOf("threshold"),
                "id = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.getFloat(0) else newThreshold
            }
            db.update(
                "people",
                ContentValues().apply {
                    put("name", name.trim())
                    put("threshold", newThreshold)
                    if (cover != null) {
                        put("cover", cover)
                        if (coverFaceId == null) putNull("cover_face")
                    }
                    if (coverFaceId != null) {
                        requireNotNull(coverFaceId.toFacePosition())
                        put("cover_face", coverFaceId)
                    }
                },
                "id = ?",
                arrayOf(id.toString())
            )
            var changed = oldThreshold != newThreshold
            if (restoreExcluded) {
                val photos = db.delete("exclusions", "person_id = ?", arrayOf(id.toString()))
                val faces = db.delete("face_exclusions", "person_id = ?", arrayOf(id.toString()))
                if (photos + faces > 0) changed = true
            }
            excludedFaces.forEach { face ->
                if (db.excludeFace(id, face)) changed = true
            }
            excludedUris.forEach { uri ->
                val added = db.insertWithOnConflict(
                    "exclusions",
                    null,
                    ContentValues().apply {
                        put("person_id", id)
                        put("uri", uri)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
                if (added != -1L) changed = true
            }
            if (changed) {
                db.insertWithOnConflict(
                    "metadata",
                    null,
                    ContentValues().apply {
                        put("key", "groups_dirty")
                        put("value", 1)
                    },
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            changed
        }
    }

    private fun SQLiteDatabase.excludeFace(person: Long, id: String): Boolean {
        val position = requireNotNull(id.toFacePosition())
        val uri = query(
            "faces",
            arrayOf("uri", "left", "top", "right", "bottom"),
            "person_id = ? AND media_id = ?",
            arrayOf(person.toString(), position.mediaId.toString()),
            null,
            null,
            null
        ).use { cursor ->
            var result: String? = null
            while (cursor.moveToNext()) {
                val candidate = FacePosition(
                    position.mediaId,
                    RectF(
                        cursor.getFloat(1),
                        cursor.getFloat(2),
                        cursor.getFloat(3),
                        cursor.getFloat(4)
                    )
                )
                if (position.matches(candidate)) result = cursor.getString(0)
            }
            result
        } ?: return false
        val added = insertWithOnConflict(
            "face_exclusions",
            null,
            ContentValues().apply {
                put("person_id", person)
                put("uri", uri)
                put("media_id", position.mediaId)
                put("left", position.bounds.left)
                put("top", position.bounds.top)
                put("right", position.bounds.right)
                put("bottom", position.bounds.bottom)
            },
            SQLiteDatabase.CONFLICT_IGNORE
        ) != -1L
        if (added) {
            execSQL(
                "INSERT INTO face_links(media_id, left, top, right, bottom, person_id) VALUES (?, ?, ?, ?, ?, ?)",
                arrayOf<Any>(
                    position.mediaId,
                    position.bounds.left,
                    position.bounds.top,
                    position.bounds.right,
                    position.bounds.bottom,
                    person
                )
            )
            update(
                "people",
                ContentValues().apply { put("pinned", 1) },
                "id = ?",
                arrayOf(person.toString())
            )
        }
        return added
    }

    fun rejectPair(first: Long, second: Long) {
        require(first != second)
        val db = writableDatabase
        db.transaction {
            listOf(first, second).forEach { id ->
                require(
                    db.update(
                        "people",
                        ContentValues().apply { put("pinned", 1) },
                        "id = ?",
                        arrayOf(id.toString())
                    ) == 1
                )
                db.execSQL(
                    "INSERT INTO face_links(media_id, left, top, right, bottom, person_id) SELECT media_id, " +
                            "left, top, right, bottom, person_id FROM faces WHERE person_id = ?",
                    arrayOf(id)
                )
            }
            db.insertWithOnConflict(
                "rejected_pairs",
                null,
                ContentValues().apply {
                    put("first_id", minOf(first, second))
                    put("second_id", maxOf(first, second))
                },
                SQLiteDatabase.CONFLICT_IGNORE
            )
        }
    }

    fun merge(from: Set<Long>, into: Long) {
        val sources = from - into
        if (sources.isEmpty()) return
        val db = writableDatabase
        db.transaction {
            require(
                db.update(
                    "people",
                    ContentValues().apply { put("pinned", 1) },
                    "id = ?",
                    arrayOf(into.toString())
                ) == 1
            )
            db.execSQL(
                "INSERT INTO face_links(media_id, left, top, right, bottom, person_id) SELECT media_id, left, " +
                        "top, right, bottom, ? FROM faces WHERE person_id = ?",
                arrayOf(into, into)
            )
            sources.forEach { source ->
                db.execSQL(
                    "INSERT INTO face_links(media_id, left, top, right, bottom, person_id) SELECT media_id, " +
                            "left, top, right, bottom, ? FROM faces WHERE person_id = ?",
                    arrayOf(into, source)
                )
                db.update(
                    "face_links",
                    ContentValues().apply { put("person_id", into) },
                    "person_id = ?",
                    arrayOf(source.toString())
                )
                db.update(
                    "faces",
                    ContentValues().apply { put("person_id", into) },
                    "person_id = ?",
                    arrayOf(source.toString())
                )
                db.execSQL(
                    "INSERT OR IGNORE INTO exclusions(person_id, uri) " +
                            "SELECT ?, uri FROM exclusions WHERE person_id = ?",
                    arrayOf(into, source)
                )
                db.execSQL(
                    "INSERT OR IGNORE INTO face_exclusions(person_id, uri, media_id, left, top, right, bottom) " +
                            "SELECT ?, uri, media_id, left, top, right, bottom FROM face_exclusions WHERE person_id = ?",
                    arrayOf(into, source)
                )
                db.mergeRejectedPairs(source, into)
                require(
                    db.update(
                        "people",
                        ContentValues().apply { put("hidden", 1) },
                        "id = ?",
                        arrayOf(source.toString())
                    ) == 1
                )
            }
        }
    }

    private fun SQLiteDatabase.mergeRejectedPairs(from: Long, into: Long) {
        execSQL(
            "INSERT OR IGNORE INTO rejected_pairs(first_id, second_id) " +
                    "SELECT MIN(?, partner), MAX(?, partner) FROM " +
                    "(SELECT CASE WHEN first_id = ? THEN second_id ELSE first_id END AS partner " +
                    "FROM rejected_pairs WHERE first_id = ? OR second_id = ?) WHERE partner != ?",
            arrayOf(into, into, from, from, from, into)
        )
        delete(
            "rejected_pairs",
            "first_id = ? OR second_id = ?",
            arrayOf(from.toString(), from.toString())
        )
    }

    fun exclude(person: Long, uri: String) {
        writableDatabase.insertWithOnConflict(
            "exclusions",
            null,
            ContentValues().apply {
                put("person_id", person)
                put("uri", uri)
            },
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    fun hiddenCount(): Int = readableDatabase.rawQuery(
        "SELECT COUNT(*) FROM people WHERE hidden = 1 AND id IN (SELECT person_id FROM faces)",
        null
    ).use { cursor ->
        cursor.moveToFirst()
        cursor.getInt(0)
    }

    fun setHidden(id: Long, hidden: Boolean) {
        writableDatabase.update(
            "people",
            ContentValues().apply { put("hidden", if (hidden) 1 else 0) },
            "id = ?",
            arrayOf(id.toString())
        )
    }

    fun clear() {
        val db = writableDatabase
        db.transaction {
            listOf(
                "faces",
                "scans",
                "people",
                "exclusions",
                "metadata",
                "face_links",
                "rejected_pairs",
                "face_exclusions"
            ).forEach {
                db.delete(it, null, null)
            }
        }
        thumbnails.listFiles()?.forEach(File::delete)
    }

    private val Media.fingerprint: String
        get() = "$timestamp:$size:$width:$height:$mimeType"

    private fun encode(vector: FloatArray): ByteArray = ByteBuffer.allocate(512 * Float.SIZE_BYTES)
        .order(ByteOrder.LITTLE_ENDIAN).apply { vector.forEach(::putFloat) }.array()

    private fun decode(bytes: ByteArray): FloatArray = FloatArray(512).also {
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(it)
    }

    private fun normalize(vector: FloatArray): FloatArray {
        val norm = sqrt(vector.sumOf { it.toDouble() * it }).toFloat().coerceAtLeast(1e-10f)
        return FloatArray(vector.size) { vector[it] / norm }
    }

    private fun similarity(a: FloatArray, b: FloatArray): Float {
        var score = 0f
        for (i in a.indices) score += a[i] * b[i]
        return score
    }
}