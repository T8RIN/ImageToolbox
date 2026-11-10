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
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class GalleryOcrIndexTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "gallery-ocr-test-${UUID.randomUUID()}.db"
    private var index = GalleryOcrIndex(context, name)

    @After
    fun tearDown() {
        index.close()
        listOf("", "-journal", "-wal", "-shm").forEach { suffix ->
            File(context.noBackupFilesDir, name + suffix).delete()
        }
    }

    @Test
    fun anyTextMatchesOnlyCurrentRecognizedTextAndPendingUsesTheRequestedPhotos() = runBlocking {
        val recognized = media(1)
        val blank = media(2)
        val partial = media(3)
        val missing = media(4)
        index.put(GalleryOcrIndex.Pending(recognized), GalleryOcrIndex.Status.Text, "OPEN")
        index.put(GalleryOcrIndex.Pending(blank), GalleryOcrIndex.Status.NoText, "")
        index.put(GalleryOcrIndex.Pending(partial), GalleryOcrIndex.Status.Partial, "Station")

        val photos = listOf(recognized, blank, partial, missing)
        assertEquals(listOf(recognized, partial), index.search(null, photos))
        assertTrue(index.search("", photos).isEmpty())
        assertTrue(
            index.search(
                null,
                listOf(recognized.copy(timestamp = 2), partial.copy(isTrashed = true))
            ).isEmpty()
        )
        assertTrue(index.hasPending(photos))
        assertTrue(index.hasPending(listOf(recognized.copy(timestamp = 2))))
        assertEquals(
            false,
            index.hasPending(listOf(recognized, blank, missing.copy(isTrashed = true)))
        )

        val retry = index.pending(listOf(partial), pruneMissing = false).single()
        index.put(retry, GalleryOcrIndex.Status.Partial, "Station")
        assertEquals(false, index.hasPending(photos - missing))
    }

    @Test
    fun textSurvivesReopeningAndMatchesUnicodeCaseAndWhitespace() = runBlocking {
        val photo = media(1)
        index.put(
            GalleryOcrIndex.Pending(photo),
            GalleryOcrIndex.Status.Text,
            "ВСТРЕЧА\nВ ПЯТНИЦУ １２"
        )
        index.close()
        index = GalleryOcrIndex(context, name)

        assertEquals(listOf(photo), index.search("встреча  в пятницу 12", listOf(photo)))
        assertTrue(index.pending(listOf(photo)).isEmpty())
        assertTrue(index.search("другая надпись", listOf(photo)).isEmpty())
    }

    @Test
    fun changedPhotosAndPipelineAreReindexedWithoutServingOldText() = runBlocking {
        val photo = media(1)
        val pending = GalleryOcrIndex.Pending(photo)
        index.put(pending, GalleryOcrIndex.Status.Text, "old text")
        listOf(
            photo.copy(generationModified = 2),
            photo.copy(timestamp = 2),
            photo.copy(size = 2),
            photo.copy(width = 2),
            photo.copy(height = 2),
            photo.copy(mimeType = "image/jpeg")
        ).forEach { changed ->
            assertTrue(index.search("old text", listOf(changed)).isEmpty())
        }
        index.writableDatabase.execSQL("UPDATE photos SET pipeline = -1")
        assertTrue(index.search("old text", listOf(photo)).isEmpty())
        assertEquals(listOf(pending), index.pending(listOf(photo)))
        index.put(pending, GalleryOcrIndex.Status.Text, "new text")
        assertTrue(index.search("old text", listOf(photo)).isEmpty())
        assertEquals(listOf(photo), index.search("new text", listOf(photo)))
    }

    @Test
    fun partialTextIsSearchableWhileRetriesAreBounded() = runBlocking {
        val photo = media(1)
        index.put(GalleryOcrIndex.Pending(photo), GalleryOcrIndex.Status.Partial, "Central Station")
        val retry = index.pending(listOf(photo)).single()
        assertEquals(1, retry.attempts)
        assertEquals("Central Station", retry.text)
        assertEquals(listOf(photo), index.search("station", listOf(photo)))

        index.put(retry, GalleryOcrIndex.Status.Partial, retry.text)
        assertTrue(index.pending(listOf(photo)).isEmpty())
        assertEquals(listOf(photo), index.search("station", listOf(photo)))
    }

    @Test
    fun deletedAndTrashedPhotosAreExcludedAndPruned() = runBlocking {
        val photos = listOf(media(1), media(2))
        photos.forEach {
            index.put(
                GalleryOcrIndex.Pending(it),
                GalleryOcrIndex.Status.Text,
                "match"
            )
        }
        assertEquals(photos.take(1), index.search("match", photos.take(1)))
        assertTrue(index.search("match", photos.map { it.copy(isTrashed = true) }).isEmpty())
        assertTrue(index.pending(photos.take(1)).isEmpty())
        assertTrue(index.search("match", photos.drop(1)).isEmpty())
    }

    @Test
    fun selectedPhotoAccessKeepsRowsOutsideTheCurrentSelection() = runBlocking {
        val photo = media(1)
        index.put(GalleryOcrIndex.Pending(photo), GalleryOcrIndex.Status.Text, "ticket")
        val selected = media(2)

        assertEquals(
            listOf(selected),
            index.pending(listOf(selected), pruneMissing = false).map { it.media })
        assertEquals(listOf(photo), index.search("ticket", listOf(photo)))
    }

    @Test
    fun newPhotosAreProcessedNewestFirstBeforeRetries() = runBlocking {
        val retry = media(4).copy(timestamp = 4)
        val older = media(3).copy(timestamp = 1)
        val recent = listOf(media(1), media(2)).map { it.copy(timestamp = 3) }
        index.put(GalleryOcrIndex.Pending(retry), GalleryOcrIndex.Status.Partial, "ticket")

        assertEquals(
            recent + older + retry,
            index.pending(listOf(retry, older) + recent.reversed()).map { it.media })
    }

    @Test
    fun blankPhotosAreFinishedAndUnreadablePhotosRetryOnce() = runBlocking {
        val blank = media(1)
        val unreadable = media(2)
        index.put(GalleryOcrIndex.Pending(blank), GalleryOcrIndex.Status.NoText, "")
        index.put(GalleryOcrIndex.Pending(unreadable), GalleryOcrIndex.Status.Unreadable, "")
        val retry = index.pending(listOf(blank, unreadable)).single()
        assertEquals(unreadable, retry.media)
        index.put(retry, GalleryOcrIndex.Status.Unreadable, "")
        assertTrue(index.pending(listOf(blank, unreadable)).isEmpty())
    }

    private fun media(id: Long) = Media(
        id = id,
        label = "$id.png",
        uri = "content://media/external/images/media/$id",
        path = "/pictures/$id.png",
        relativePath = "pictures/",
        albumID = 1,
        albumLabel = "Pictures",
        timestamp = 1,
        mimeType = "image/png",
        width = 100,
        height = 100,
        size = 100,
        generationModified = 1
    )
}
