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
import android.content.ContextWrapper
import android.database.sqlite.SQLiteException
import android.graphics.RectF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

@RunWith(AndroidJUnit4::class)
class SemanticFaceIndexTest {

    private val application = InstrumentationRegistry.getInstrumentation().targetContext
    private val directory = File(application.cacheDir, "semantic-faces-${UUID.randomUUID()}")
    private val context = object : ContextWrapper(application) {
        override fun getNoBackupFilesDir() = directory
    }
    private var index = SemanticFaceIndex(context)

    @After
    fun tearDown() {
        index.close()
        directory.deleteRecursively()
    }

    @Test
    fun upgradingTheOriginalDatabasePreservesPeoplePhotosAndSettings() = runBlocking {
        val images = listOf(media(1), media(2))
        images.forEach { index.replace(it, listOf(face(it.id, 0f))) }
        index.regroup()
        val id = index.people(images).single().person.personId
        index.updatePerson(id, "Alice", 0.65f, images[0].uri)
        index.exclude(id, images[1].uri)
        index.setHidden(id, true)
        val before = index.people(images)
        index.writableDatabase.execSQL("DROP TABLE rejected_pairs")
        index.writableDatabase.version = 1
        reopen()

        assertEquals(before, index.people(images))
        assertEquals(2, index.readableDatabase.version)
        assertTrue(rejectedPairs().isEmpty())
        images.forEach { assertFalse(index.needsScan(it)) }
        index.readableDatabase.rawQuery("SELECT COUNT(*) FROM faces", null).use {
            assertTrue(it.moveToFirst())
            assertEquals(2, it.getInt(0))
        }
    }

    @Test
    fun hiddenGroupsRemainBrowsableAndCanBeRestoredIndividually() = runBlocking {
        val first = media(1)
        val second = media(2)
        index.replace(first, listOf(face(1, 0f)))
        index.replace(second, listOf(face(2, 90f)))
        index.regroup()
        val people = index.people(listOf(first, second))
        val firstId = people.first { first in it.media }.person.personId
        val secondId = people.first { second in it.media }.person.personId
        index.updatePerson(firstId, "Alice", 0.65f, first.uri)
        index.setHidden(firstId, true)
        index.setHidden(secondId, true)
        reopen()

        val hidden = index.people(listOf(first, second))
        assertEquals(2, hidden.size)
        assertTrue(hidden.all { it.person.hidden })
        assertEquals(2, index.hiddenCount())
        index.updatePerson(firstId, "Alice updated", 0.65f, first.uri)
        assertTrue(index.people(listOf(first)).single().person.hidden)
        index.setHidden(firstId, false)
        reopen()

        val restored = index.people(listOf(first, second))
        val alice = restored.first { it.person.personId == firstId }
        assertFalse(alice.person.hidden)
        assertEquals("Alice updated", alice.person.name)
        assertEquals(0.65f, alice.person.threshold, 0f)
        assertEquals(first.uri, alice.person.coverUri)
        assertEquals(listOf(first), alice.media)
        assertTrue(restored.first { it.person.personId == secondId }.person.hidden)
        assertEquals(1, index.hiddenCount())
    }

    @Test
    fun hiddenIdentityAcceptsNewMatchingFacesDuringRegrouping() = runBlocking {
        val images = listOf(media(1), media(2), media(3))
        index.replace(images[0], listOf(face(1, 0f)))
        index.replace(images[1], listOf(face(2, 70f)))
        index.regroup()
        val people = index.people(images)
        val target = people.first { images[0] in it.media }.person.personId
        val source = people.first { images[1] in it.media }.person.personId
        index.merge(setOf(source), target)
        index.setHidden(target, true)
        reopen()
        index.replace(images[2], listOf(face(3, 100f)))
        index.regroup()
        reopen()

        val person = index.people(images).single()
        assertEquals(target, person.person.personId)
        assertTrue(person.person.hidden)
        assertEquals(images.toSet(), person.media.toSet())
        assertEquals(1, index.hiddenCount())
    }

    @Test
    fun indexingCacheRefreshesAfterAnExternalMerge() = runBlocking {
        val images = listOf(media(1), media(2), media(3), media(4))
        index.replace(images[0], listOf(face(1, 0f)))
        index.replace(images[1], listOf(face(2, 90f)))
        index.regroup()
        val people = index.people(images)
        val source = people.first { images[0] in it.media }.person.personId
        val target = people.first { images[1] in it.media }.person.personId
        index.updatePerson(source, "First", 0.55f, images[0].uri)
        index.updatePerson(target, "Second", 0.55f, images[1].uri)
        index.invalidatePeopleCache(0)
        index.replace(images[2], listOf(face(3, 0f)))
        SemanticFaceIndex(context).use { editor -> editor.merge(setOf(source), target) }
        index.invalidatePeopleCache(1)
        index.replace(images[3], listOf(face(4, 0f)))
        index.regroup()
        reopen()

        val result = index.people(images).single()
        assertEquals(target, result.person.personId)
        assertEquals("Second", result.person.name)
        assertEquals(images.toSet(), result.media.toSet())
    }

    @Test
    fun savedExclusionsRestorePreviousPhotosBeforeApplyingNewRemovals() = runBlocking {
        val images = listOf(media(1), media(2), media(3))
        images.forEach { index.replace(it, listOf(face(it.id, 0f))) }
        index.regroup()
        val id = index.people(images).single().person.personId
        index.exclude(id, images[0].uri)
        index.setHidden(id, true)

        assertTrue(
            index.updatePerson(
                id = id,
                name = "Alice",
                threshold = 0.7f,
                cover = images[2].uri,
                excludedUris = setOf(images[1].uri),
                restoreExcluded = true
            )
        )
        assertTrue(index.needsRegroup())
        index.regroup()
        reopen()

        val result = index.people(images).single()
        assertEquals(setOf(images[0], images[2]), result.media.toSet())
        assertEquals("Alice", result.person.name)
        assertEquals(0.7f, result.person.threshold, 0f)
        assertEquals(images[2].uri, result.person.coverUri)
        assertEquals(1, result.person.excludedPhotos)
        assertTrue(result.person.hidden)
        assertFalse(index.needsRegroup())
    }

    @Test
    fun restoringEveryExcludedPhotoKeepsTheOriginalGroupIdentity() = runBlocking {
        val images = listOf(media(1), media(2))
        images.forEach { index.replace(it, listOf(face(it.id, 0f))) }
        index.regroup()
        val id = index.people(images).single().person.personId
        index.updatePerson(
            id = id,
            name = "Alice",
            threshold = 0.55f,
            cover = null,
            excludedUris = images.map(Media::uri).toSet()
        )
        index.regroup()
        reopen()

        val empty = index.people(images).single()
        assertEquals(id, empty.person.personId)
        assertTrue(empty.media.isEmpty())
        assertEquals(2, empty.person.excludedPhotos)
        index.updatePerson(id, "Alice", 0.55f, cover = null, restoreExcluded = true)
        index.regroup()
        reopen()

        val restored = index.people(images).single()
        assertEquals(id, restored.person.personId)
        assertEquals("Alice", restored.person.name)
        assertEquals(images.toSet(), restored.media.toSet())
        assertEquals(0, restored.person.excludedPhotos)
    }

    @Test
    fun failedExclusionSaveRollsBackNameThresholdCoverAndRestoration() = runBlocking {
        val images = listOf(media(1), media(2))
        images.forEach { index.replace(it, listOf(face(it.id, 0f))) }
        index.regroup()
        val id = index.people(images).single().person.personId
        index.updatePerson(id, "Original", 0.55f, images[0].uri)
        index.exclude(id, images[1].uri)
        index.writableDatabase.execSQL(
            "CREATE TRIGGER reject_exclusion BEFORE INSERT ON exclusions " +
                    "BEGIN SELECT RAISE(ABORT, 'test failure'); END"
        )

        assertThrows(SQLiteException::class.java) {
            index.updatePerson(
                id = id,
                name = "Changed",
                threshold = 0.8f,
                cover = images[1].uri,
                excludedUris = setOf(images[0].uri),
                restoreExcluded = true
            )
        }
        reopen()

        val result = index.people(images).single()
        assertEquals("Original", result.person.name)
        assertEquals(0.55f, result.person.threshold, 0f)
        assertEquals(images[0].uri, result.person.coverUri)
        assertEquals(listOf(images[0]), result.media)
        assertEquals(1, result.person.excludedPhotos)
        assertFalse(index.needsRegroup())
    }

    @Test
    fun mergingSeveralGroupsPreservesTargetSettingsExclusionsAndIdentity() = runBlocking {
        val images = (1L..6L).map(::media)
        images.forEachIndexed { position, image ->
            index.replace(image, listOf(face(image.id, (position / 2) * 90f)))
        }
        index.regroup()
        val people = index.people(images)
        val target = people.first { images[0] in it.media }.person.personId
        val sources = listOf(2, 4).map { position ->
            people.first { images[position] in it.media }.person.personId
        }
        index.updatePerson(target, "Current", 0.7f, images[1].uri)
        index.exclude(target, images[0].uri)
        sources.forEachIndexed { position, id ->
            index.updatePerson(id, "Source $position", 0.6f, images[position * 2 + 2].uri)
            index.exclude(id, images[position * 2 + 2].uri)
            index.setHidden(id, true)
        }

        index.merge((sources + target).toSet(), target)
        index.regroup()
        reopen()

        val result = index.people(images).single()
        assertEquals(target, result.person.personId)
        assertEquals("Current", result.person.name)
        assertEquals(0.7f, result.person.threshold, 0f)
        assertEquals(images[1].uri, result.person.coverUri)
        assertEquals(setOf(images[1], images[3], images[5]), result.media.toSet())
        assertEquals(3, result.person.excludedPhotos)
        assertFalse(result.person.hidden)
        assertEquals(0, index.hiddenCount())
        index.readableDatabase.rawQuery("SELECT DISTINCT person_id FROM face_links", null).use {
            assertTrue(it.moveToFirst())
            assertEquals(target, it.getLong(0))
            assertFalse(it.moveToNext())
        }
    }

    @Test
    fun failureInTheSecondSourceRollsBackTheEntireMerge() = runBlocking {
        val images = (1L..3L).map(::media)
        images.forEachIndexed { position, image ->
            index.replace(image, listOf(face(image.id, position * 90f)))
        }
        index.regroup()
        val people = index.people(images)
        val ids = images.map { image -> people.first { image in it.media }.person.personId }
        ids.forEachIndexed { position, id ->
            index.updatePerson(id, "Person $position", 0.6f, images[position].uri)
        }
        index.exclude(ids[1], images[1].uri)
        index.rejectPair(ids[1], ids[2])
        val rejectedBefore = rejectedPairs()
        val before = index.people(images)
        index.writableDatabase.execSQL(
            "CREATE TRIGGER reject_second_merge BEFORE UPDATE OF person_id ON faces " +
                    "WHEN OLD.person_id = ${ids[2]} BEGIN SELECT RAISE(ABORT, 'test failure'); END"
        )

        assertThrows(SQLiteException::class.java) {
            index.merge(linkedSetOf(ids[1], ids[2]), ids[0])
        }
        reopen()

        assertEquals(before, index.people(images))
        assertEquals(rejectedBefore, rejectedPairs())
        assertEquals(0, index.hiddenCount())
        index.readableDatabase.rawQuery("SELECT COUNT(*) FROM face_links", null).use {
            assertTrue(it.moveToFirst())
            assertEquals(2, it.getInt(0))
        }
        index.readableDatabase.rawQuery("SELECT SUM(pinned) FROM people", null).use {
            assertTrue(it.moveToFirst())
            assertEquals(2, it.getInt(0))
        }
    }

    @Test
    fun similarGroupsRespectScoreOrderVisibilityAndPhotosWithSeveralPeople() = runBlocking {
        val images = (1L..6L).map(::media)
        index.replace(images[0], listOf(face(1, 0f), face(4, 20f)))
        listOf(2L to 50f, 3L to 64f, 5L to 10f, 6L to 40f).forEach { (id, angle) ->
            index.replace(images[(id - 1).toInt()], listOf(face(id, angle)))
        }
        val ids = (1L..6L).associateWith(::separateFace)
        index.setHidden(ids.getValue(5), true)

        val people = index.people(images).associateBy { it.person.personId }
        val first = people.getValue(ids.getValue(1)).person
        assertEquals(listOf(ids.getValue(6), ids.getValue(2)), first.similarPeople.keys.toList())
        assertEquals(
            cos(Math.toRadians(40.0)).toFloat(),
            first.similarPeople.getValue(ids.getValue(6)),
            0.00001f
        )
        assertEquals(
            cos(Math.toRadians(50.0)).toFloat(),
            first.similarPeople.getValue(ids.getValue(2)),
            0.00001f
        )
        assertEquals(
            first.similarPeople.getValue(ids.getValue(2)),
            people.getValue(ids.getValue(2)).person.similarPeople.getValue(first.personId),
            0f
        )
        assertTrue(ids.getValue(1) in people.getValue(ids.getValue(2)).person.similarPeople)
        assertFalse(ids.getValue(1) in people.getValue(ids.getValue(3)).person.similarPeople)
        assertFalse(ids.getValue(1) in people.getValue(ids.getValue(4)).person.similarPeople)
        assertTrue(people.getValue(ids.getValue(5)).person.similarPeople.isEmpty())
        val limited = index.people(images.take(2)).first { it.person.personId == first.personId }
        assertEquals(listOf(ids.getValue(2)), limited.person.similarPeople.keys.toList())
    }

    @Test
    fun rejectedPeopleRemainSeparateAfterReopeningRegroupingAndRescanning() = runBlocking {
        val images = listOf(media(1), media(2))
        index.replace(images[0], listOf(face(1, 0f)))
        index.replace(images[1], listOf(face(2, 40f)))
        val first = separateFace(1)
        val second = separateFace(2)
        val similar =
            index.people(images).first { it.person.personId == first }.person.similarPeople
        assertEquals(listOf(second), similar.keys.toList())
        index.rejectPair(second, first)
        reopen()
        index.regroup()
        reopen()
        index.replace(images[0], listOf(face(1, 0f)))
        index.replace(images[1], listOf(face(2, 40f)))
        index.regroup()
        reopen()

        val people = index.people(images)
        assertEquals(setOf(first, second), people.map { it.person.personId }.toSet())
        assertTrue(people.all { it.person.similarPeople.isEmpty() })
        assertEquals(setOf(minOf(first, second) to maxOf(first, second)), rejectedPairs())
    }

    @Test
    fun mergingGroupsTransfersThirdPartyRejectionsAndOverridesInternalPairs() = runBlocking {
        val images = (1L..4L).map(::media)
        images.forEachIndexed { position, image ->
            index.replace(image, listOf(face(image.id, position * 30f)))
        }
        val ids = (1L..4L).map(::separateFace)
        index.rejectPair(ids[1], ids[3])
        index.rejectPair(ids[2], ids[3])
        index.rejectPair(ids[0], ids[1])
        index.rejectPair(ids[1], ids[2])

        index.merge(setOf(ids[1], ids[2]), ids[0])
        index.regroup()
        reopen()

        assertEquals(setOf(minOf(ids[0], ids[3]) to maxOf(ids[0], ids[3])), rejectedPairs())
        val people = index.people(images)
        assertEquals(setOf(ids[0], ids[3]), people.map { it.person.personId }.toSet())
        assertTrue(people.all { it.person.similarPeople.isEmpty() })
        index.merge(setOf(ids[3]), ids[0])
        index.regroup()
        reopen()

        assertTrue(rejectedPairs().isEmpty())
        assertEquals(ids[0], index.people(images).single().person.personId)
    }

    private fun separateFace(id: Long): Long {
        val person = index.writableDatabase.insertOrThrow(
            "people",
            null,
            ContentValues().apply { put("name", "") })
        index.writableDatabase.update(
            "faces",
            ContentValues().apply { put("person_id", person) },
            "id = ?",
            arrayOf(id.toString())
        )
        return person
    }

    private fun rejectedPairs(): Set<Pair<Long, Long>> = index.readableDatabase.rawQuery(
        "SELECT first_id, second_id FROM rejected_pairs",
        null
    ).use { cursor ->
        buildSet {
            while (cursor.moveToNext()) add(cursor.getLong(0) to cursor.getLong(1))
        }
    }

    private fun reopen() {
        index.close()
        index = SemanticFaceIndex(context)
    }

    private val SemanticSearchCategoryResult.person: SemanticSearchCategory.Person
        get() = category as SemanticSearchCategory.Person

    private fun face(id: Long, degrees: Float): SemanticFaceIndex.Face {
        val angle = Math.toRadians(degrees.toDouble())
        val embedding = FloatArray(512).apply {
            this[0] = cos(angle).toFloat()
            this[1] = sin(angle).toFloat()
        }
        return SemanticFaceIndex.Face(
            id = id.toString(),
            vector = embedding,
            thumbnail = File(index.thumbnails, "$id.webp").absolutePath,
            bounds = RectF(0.1f, 0.1f, 0.5f, 0.5f),
            confidence = 1f
        )
    }

    private fun media(id: Long) = Media(
        id = id,
        label = "$id.jpg",
        uri = "content://media/external/images/media/$id",
        path = "/Pictures/$id.jpg",
        relativePath = "Pictures/",
        albumID = 1,
        albumLabel = "Pictures",
        timestamp = 1,
        mimeType = "image/jpeg",
        width = 100,
        height = 100,
        size = 100
    )
}