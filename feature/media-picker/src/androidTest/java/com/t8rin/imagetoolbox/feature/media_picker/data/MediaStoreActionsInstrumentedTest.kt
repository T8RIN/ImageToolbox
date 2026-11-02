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
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.Query
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getMediaFromCursor
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MediaStoreActionsInstrumentedTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val resolver get() = context.contentResolver
    private val prefix = "codex_media_${UUID.randomUUID().toString().replace("-", "")}"
    private val fixtures = mutableListOf<Media>()
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Main
        override val ioDispatcher = Dispatchers.IO
        override val defaultDispatcher = Dispatchers.Default
        override val encodingDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
    }
    private val manager get() = AndroidMediaActionsManager(context, dispatchers)
    private val retriever get() = AndroidMediaRetriever(context, dispatchers)

    @After
    fun tearDown() = runBlocking {
        if (Build.VERSION.SDK_INT >= 30) manager.perform(fixtures, MediaAction.Restore)
        fixtures.forEach { media -> shell("content delete --uri ${media.uri}") }
    }

    @Test
    fun ownedJpegAndJxlKeepTheirBytesThroughFavoriteTrashAndRestore() = runBlocking {
        assumeTrue(Build.VERSION.SDK_INT >= 30)
        val items = listOf(fixture("jpg"), fixture("jxl"))
        val bytes = items.associate {
            it.uri to resolver.openInputStream(android.net.Uri.parse(it.uri))!!
                .use { stream -> stream.readBytes() }
        }
        for (action in listOf(MediaAction.Favorite, MediaAction.Trash)) {
            val result = manager.perform(items, action)
            assertEquals(items.mapTo(hashSetOf(), Media::uri), result.successfulUris)
            assertTrue(result.failedUris.isEmpty())
            assertNull(result.permissionRequest)
        }
        val visible = withTimeout(10_000) {
            retriever.getMediaByType(AllowedMedia.Photos(null)).first().getOrThrow()
        }
        assertTrue(visible.none { it.uri in bytes })
        val trash = withTimeout(10_000) {
            retriever.mediaFlowWithType(
                Album.TRASH_ID,
                AllowedMedia.Photos(null)
            ).first().getOrThrow()
        }
        val ours = trash.filter { it.uri in bytes }
        assertEquals(2, ours.size)
        assertTrue(ours.all { it.isTrashed && it.isFavorite && (it.expiryTimestamp ?: 0) > 0 })
        val trashAlbums = withTimeout(10_000) {
            retriever.getAlbumsWithType(AllowedMedia.Photos(null)).first().getOrThrow()
        }
        assertEquals(trash.size.toLong(), trashAlbums.single { it.id == Album.TRASH_ID }.count)
        assertEquals(2, manager.perform(ours, MediaAction.Restore).successfulUris.size)
        items.forEach { media ->
            assertArrayEquals(
                bytes.getValue(media.uri),
                resolver.openInputStream(android.net.Uri.parse(media.uri))!!.use { it.readBytes() })
        }
        val albums = withTimeout(10_000) {
            retriever.getAlbumsWithType(AllowedMedia.Photos(null)).first().getOrThrow()
        }
        assertEquals(Album.FAVORITES_ID, albums.first().id)
        assertTrue(albums.none { it.id == Album.TRASH_ID && it.count == 0L })
        assertTrue(albums.first().count >= 2)
        assertEquals(2, manager.perform(items, MediaAction.Unfavorite).successfulUris.size)
    }

    @Test
    fun foreignImageUsesSystemRequestWithoutChangingTheFile() = runBlocking {
        assumeTrue(Build.VERSION.SDK_INT >= 30)
        assumeTrue(!Environment.isExternalStorageManager())
        val item = fixture("jpg", foreign = true)
        val favorite = manager.perform(listOf(item), MediaAction.Favorite)
        assertNotNull(favorite.permissionRequest)
        assertEquals(setOf(item.uri), favorite.permissionUris)
        assertFalse(read(item.uri).isFavorite)
        val trash = manager.perform(listOf(item), MediaAction.Trash)
        assertNotNull(trash.permissionRequest)
        assertFalse(read(item.uri).isTrashed)
        assertEquals(item.uri, read(item.uri).uri)
    }

    @Test
    fun allFilesAccessHandlesMixedForeignJpegAndJxlDirectly() = runBlocking {
        assumeTrue(Build.VERSION.SDK_INT >= 30)
        assumeTrue(Environment.isExternalStorageManager())
        val items = listOf(fixture("jpg", foreign = true), fixture("jxl", foreign = true))
        for (action in listOf(
            MediaAction.Favorite,
            MediaAction.Trash,
            MediaAction.Restore,
            MediaAction.Unfavorite
        )) {
            val result = manager.perform(items, action)
            assertEquals("$action: $result", 2, result.successfulUris.size)
            assertTrue(result.failedUris.isEmpty())
            assertNull(result.permissionRequest)
        }
    }

    @Test
    fun permanentDeleteRequiresTheFileToStillBeInTrash() = runBlocking {
        assumeTrue(Build.VERSION.SDK_INT >= 30)
        val item = fixture("jpg")
        val trashed = manager.perform(listOf(item), MediaAction.Trash)
        assertEquals(trashed.toString(), 1, trashed.successfulUris.size)
        val stale = read(item.uri)
        assertEquals(1, manager.perform(listOf(stale), MediaAction.Restore).successfulUris.size)
        val refused = manager.perform(listOf(stale), MediaAction.Delete)
        assertEquals(setOf(item.uri), refused.failedUris)
        assertNull(refused.permissionRequest)
        assertFalse(read(item.uri).isTrashed)
        manager.perform(listOf(item), MediaAction.Trash)
        assertEquals(
            setOf(item.uri),
            manager.perform(listOf(item), MediaAction.Delete).successfulUris
        )
    }

    @Test
    fun android10HasNoFavoriteOrTrashOperationsOrColumns() = runBlocking {
        assumeTrue(Build.VERSION.SDK_INT < 30)
        val item = fixture("jpg")
        assertFalse(manager.isAvailable)
        assertFalse(Query.MediaQuery().projection.contains("is_favorite"))
        assertFalse(Query.MediaQuery().projection.contains("is_trashed"))
        for (action in MediaAction.entries) {
            val result = manager.perform(listOf(item), action)
            assertEquals(setOf(item.uri), result.failedUris)
            assertNull(result.permissionRequest)
        }
        assertTrue(withTimeout(10_000) {
            retriever.mediaFlowWithType(
                Album.FAVORITES_ID,
                AllowedMedia.Photos(null)
            ).first().getOrThrow()
        }.isEmpty())
        assertTrue(withTimeout(10_000) {
            retriever.mediaFlowWithType(
                Album.TRASH_ID,
                AllowedMedia.Photos(null)
            ).first().getOrThrow()
        }.isEmpty())
        assertTrue(
            resolver.openInputStream(android.net.Uri.parse(item.uri))!!
                .use { it.readBytes().isNotEmpty() })
    }

    private fun fixture(extension: String, foreign: Boolean = false): Media {
        val collection =
            if (extension == "jpg") MediaStore.Images.Media.getContentUri("external_primary")
            else MediaStore.Files.getContentUri("external_primary")
        val mime = if (extension == "jpg") "image/jpeg" else "application/octet-stream"
        val path = if (extension == "jpg") "Pictures/$prefix/" else "Download/$prefix/"
        val name = "${prefix}_${fixtures.size}.$extension"
        val uri = if (foreign) {
            shell("content insert --uri $collection --bind _display_name:s:$name --bind mime_type:s:$mime --bind relative_path:s:$path")
            shell("touch /sdcard/$path$name")
            resolver.query(
                collection, arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME}=?", arrayOf(name), null
            )!!.use { cursor ->
                check(cursor.moveToFirst()) { name }
                android.content.ContentUris.withAppendedId(collection, cursor.getLong(0))
            }
        } else resolver.insert(collection, ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            put(MediaStore.MediaColumns.RELATIVE_PATH, path)
        })!!
        val bytes = if (extension == "jpg") ByteArrayOutputStream().use { output ->
            createBitmap(8, 8).apply { eraseColor(android.graphics.Color.RED) }
                .compress(Bitmap.CompressFormat.JPEG, 90, output)
            output.toByteArray()
        } else "MediaStore JXL storage fixture".toByteArray()
        if (!foreign) {
            resolver.openOutputStream(uri)!!.use { it.write(bytes) }
            if (extension == "jpg") {
                val scanned = CountDownLatch(1)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(
                        Environment.getExternalStorageDirectory().resolve(path + name).absolutePath
                    ),
                    arrayOf(mime)
                ) { _, _ -> scanned.countDown() }
                check(scanned.await(5, TimeUnit.SECONDS))
            }
        }
        val item = read(uri.toString())
        fixtures += item
        return item
    }

    private fun read(uri: String): Media = resolver.query(
        android.net.Uri.parse(uri), Query.MediaQuery().projection,
        Bundle().apply {
            if (Build.VERSION.SDK_INT >= 30) putInt(
                MediaStore.QUERY_ARG_MATCH_TRASHED,
                MediaStore.MATCH_INCLUDE
            )
        }, null
    )!!.use { cursor -> check(cursor.moveToFirst()) { uri }; cursor.getMediaFromCursor() }

    private fun shell(command: String): String =
        android.os.ParcelFileDescriptor.AutoCloseInputStream(
            instrumentation.uiAutomation.executeShellCommand(
                command
            )
        )
            .bufferedReader().use { it.readText() }
}