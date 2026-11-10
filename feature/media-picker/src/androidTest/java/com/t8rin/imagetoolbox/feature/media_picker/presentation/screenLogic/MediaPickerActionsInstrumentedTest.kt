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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic

import android.app.PendingIntent
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.utils.initAppContext
import com.t8rin.imagetoolbox.feature.media_picker.domain.GalleryIndexScheduler
import com.t8rin.imagetoolbox.feature.media_picker.domain.GalleryOcrRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaActionsManager
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.GalleryOcrState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaAction
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaActionResult
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDateGroup
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaDisplaySettings
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Proxy

@RunWith(AndroidJUnit4::class)
class MediaPickerActionsInstrumentedTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext.apply {
        initAppContext()
    }
    private val lifecycles = mutableListOf<LifecycleRegistry>()
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Main
        override val ioDispatcher = Dispatchers.IO
        override val defaultDispatcher = Dispatchers.Default
        override val encodingDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
    }

    @After
    fun tearDown() = InstrumentationRegistry.getInstrumentation().runOnMainSync {
        lifecycles.forEach { it.destroy() }
    }

    @Test
    fun mixedSelectionAddsOnlyMissingFavoritesThenRemovesAllFavorites() = runBlocking {
        val items = listOf(photo(1).copy(isFavorite = true), photo(2))
        val requestedActions = mutableListOf<MediaAction>()
        val actions = Actions { media, action ->
            requestedActions += action
            MediaActionResult(media.mapTo(hashSetOf(), Media::uri), emptySet())
        }
        val component = component(items, Album.ALL_ID, actions)
        withContext(Dispatchers.Main) { component.toggleSelectedFavorites() }
        await { !component.isMediaActionRunning }
        assertEquals(listOf(items.last()), actions.calls.single())
        assertTrue(component.selectedMedia.all { it.isFavorite })
        assertEquals(
            2L,
            component.albumsState.value.albums.single { it.id == Album.FAVORITES_ID }.count
        )

        withContext(Dispatchers.Main) { component.toggleSelectedFavorites() }
        await { !component.isMediaActionRunning }
        assertEquals(listOf(MediaAction.Favorite, MediaAction.Unfavorite), requestedActions)
        assertEquals(items.map(Media::uri).toSet(), actions.calls.last().map(Media::uri).toSet())
        assertTrue(component.selectedMedia.none { it.isFavorite })
        assertFalse(component.albumsState.value.albums.any { it.id == Album.FAVORITES_ID })
    }

    @Test
    fun movingSelectionToTrashRequiresConfirmationAndUpdatesBothAlbums() = runBlocking {
        val items = listOf(photo(1).copy(isFavorite = true), photo(2))
        val actions = Actions()
        val component = component(items, items.first().albumID, actions)
        withContext(Dispatchers.Main) {
            component.requestMediaActions(MediaAction.Trash, component.selectedMedia.toList())
            assertEquals(items, component.mediaActionConfirmation?.media)
            component.dismissMediaAction()
        }
        assertTrue(actions.calls.isEmpty())
        assertEquals(items, component.selectedMedia.toList())
        withContext(Dispatchers.Main) {
            component.requestMediaActions(MediaAction.Trash, component.selectedMedia.toList())
            component.confirmMediaAction()
        }
        await { !component.isMediaActionRunning }
        assertTrue(component.selectedMedia.isEmpty())
        assertTrue(component.filteredMediaState.value.mediaState.media.isEmpty())
        assertEquals(Album.ALL_ID, component.selectedAlbumId)
        assertEquals(
            2L,
            component.albumsState.value.albums.single { it.id == Album.TRASH_ID }.count
        )
        assertFalse(component.albumsState.value.albums.any { it.id == Album.FAVORITES_ID })
    }

    @Test
    fun lastFavoriteClosesWithoutWaitingForAnAlbumQuery() = runBlocking {
        val item = photo(1).copy(isFavorite = true)
        val component = component(listOf(item), Album.FAVORITES_ID)
        withContext(Dispatchers.Main) {
            component.requestMediaAction(MediaAction.Unfavorite, item)
        }
        await { !component.isMediaActionRunning }
        assertEquals(Album.ALL_ID, component.selectedAlbumId)
        assertFalse(component.albumsState.value.albums.any { it.id == Album.FAVORITES_ID })
    }

    @Test
    fun lastTrashItemClosesAndHidesTheButtonForRestoreAndDelete() = runBlocking {
        for (action in listOf(MediaAction.Restore, MediaAction.Delete)) {
            val item = photo(1).copy(isTrashed = true)
            val component = component(listOf(item), Album.TRASH_ID)
            withContext(Dispatchers.Main) {
                component.requestMediaAction(action, item)
                assertNotNull(component.mediaActionConfirmation)
                component.confirmMediaAction()
            }
            await { !component.isMediaActionRunning }
            assertEquals(Album.ALL_ID, component.selectedAlbumId)
            assertTrue(component.selectedMedia.isEmpty())
            assertFalse(component.albumsState.value.albums.any { it.id == Album.TRASH_ID })
        }
    }

    @Test
    fun partialBulkRestoreKeepsFailedItemsSelectedAndUpdatesTheTrashCount() = runBlocking {
        val items = (1..3).map { photo(it).copy(isTrashed = true) }
        val actions = Actions { media, _ ->
            MediaActionResult(media.take(2).mapTo(hashSetOf(), Media::uri), setOf(media.last().uri))
        }
        val component = component(items, Album.TRASH_ID, actions)
        withContext(Dispatchers.Main) {
            component.requestMediaActions(MediaAction.Restore, component.selectedMedia.toList())
            assertEquals(items, component.mediaActionConfirmation?.media)
            component.confirmMediaAction()
        }
        await { !component.isMediaActionRunning }
        assertEquals(Album.TRASH_ID, component.selectedAlbumId)
        assertEquals(listOf(items.last()), component.selectedMedia.toList())
        assertEquals(listOf(items.last()), component.filteredMediaState.value.mediaState.media)
        assertEquals(
            1L,
            component.albumsState.value.albums.single { it.id == Album.TRASH_ID }.count
        )
    }

    @Test
    fun restoringMixedSelectionUsesAFavoriteForTheNewAlbumPreview() = runBlocking {
        val items = listOf(
            photo(1).copy(isTrashed = true),
            photo(2).copy(isTrashed = true, isFavorite = true)
        )
        val component = component(items, Album.TRASH_ID)
        withContext(Dispatchers.Main) {
            component.requestMediaActions(MediaAction.Restore, component.selectedMedia.toList())
            component.confirmMediaAction()
        }
        await { !component.isMediaActionRunning }
        val favorite = component.albumsState.value.albums.single { it.id == Album.FAVORITES_ID }
        assertEquals(1L, favorite.count)
        assertEquals(items.last().uri, favorite.uri)
    }

    @Test
    fun bulkDeleteUsesBoundedBatchesAndClosesAfterTheLastSuccess() = runBlocking {
        val items =
            (1..(MediaActionsManager.BATCH_SIZE + 1)).map { photo(it).copy(isTrashed = true) }
        val actions = Actions()
        val component = component(items, Album.TRASH_ID, actions)
        withContext(Dispatchers.Main) {
            component.requestMediaActions(MediaAction.Delete, component.selectedMedia.toList())
            component.confirmMediaAction()
        }
        await { !component.isMediaActionRunning }
        assertEquals(listOf(MediaActionsManager.BATCH_SIZE, 1), actions.calls.map { it.size })
        assertEquals(Album.ALL_ID, component.selectedAlbumId)
        assertFalse(component.albumsState.value.albums.any { it.id == Album.TRASH_ID })
        assertTrue(component.selectedMedia.isEmpty())
    }

    @Test
    fun denyingPermissionStopsRemainingBatchesAndKeepsTheUnprocessedSelection() = runBlocking {
        val items =
            (1..(MediaActionsManager.BATCH_SIZE + 1)).map { photo(it).copy(isTrashed = true) }
        val sender = PendingIntent.getBroadcast(
            context, 1, Intent("media.bulk.test"), PendingIntent.FLAG_IMMUTABLE
        ).intentSender
        val actions = Actions { media, _ ->
            MediaActionResult(
                successfulUris = setOf(media.first().uri),
                failedUris = emptySet(),
                permissionRequest = sender,
                permissionUris = media.drop(1).mapTo(hashSetOf(), Media::uri)
            )
        }
        val component = component(items, Album.TRASH_ID, actions)
        withContext(Dispatchers.Main) {
            component.requestMediaActions(MediaAction.Restore, component.selectedMedia.toList())
            component.confirmMediaAction()
        }
        await { component.pendingMediaPermission != null }
        assertEquals(items.drop(1), component.selectedMedia.toList())
        withContext(Dispatchers.Main) { component.onMediaPermissionResult(false) }
        await { !component.isMediaActionRunning }
        assertEquals(1, actions.calls.size)
        assertEquals(items.drop(1), component.selectedMedia.toList())
        assertEquals(Album.TRASH_ID, component.selectedAlbumId)
        assertEquals(
            MediaActionsManager.BATCH_SIZE.toLong(),
            component.albumsState.value.albums.single { it.id == Album.TRASH_ID }.count
        )
    }

    @Test
    fun grantedPermissionClosesTheLastTrashSelectionWithoutARefresh() = runBlocking {
        val items = (1..2).map { photo(it).copy(isTrashed = true) }
        val sender = PendingIntent.getBroadcast(
            context, 2, Intent("media.bulk.test"), PendingIntent.FLAG_IMMUTABLE
        ).intentSender
        val component = component(items, Album.TRASH_ID, Actions { media, _ ->
            MediaActionResult(emptySet(), emptySet(), sender, media.mapTo(hashSetOf(), Media::uri))
        })
        withContext(Dispatchers.Main) {
            component.requestMediaActions(MediaAction.Restore, component.selectedMedia.toList())
            component.confirmMediaAction()
        }
        await { component.pendingMediaPermission != null }
        withContext(Dispatchers.Main) { component.onMediaPermissionResult(true) }
        await { !component.isMediaActionRunning }
        assertEquals(Album.ALL_ID, component.selectedAlbumId)
        assertFalse(component.albumsState.value.albums.any { it.id == Album.TRASH_ID })
        assertTrue(component.selectedMedia.isEmpty())
    }

    private suspend fun component(
        items: List<Media>,
        albumId: Long,
        actions: Actions = Actions()
    ): MediaPickerComponent {
        val retriever = object : MediaRetriever {
            private val albums = MutableStateFlow(Result.success(buildList {
                val favorites = items.count { it.isFavorite && !it.isTrashed }
                val trash = items.count(Media::isTrashed)
                if (favorites > 0) add(Album.Favorites.copy(count = favorites.toLong()))
                if (trash > 0) add(Album.Trash.copy(count = trash.toLong()))
                items.filterNot { it.isTrashed }.groupBy(Media::albumID).values.forEach { media ->
                    val first = media.first()
                    add(
                        Album(
                            id = first.albumID,
                            label = first.albumLabel,
                            uri = first.uri,
                            pathToThumbnail = first.path,
                            relativePath = first.relativePath,
                            timestamp = first.timestamp,
                            count = media.size.toLong()
                        )
                    )
                }
            }))

            override fun invalidate() = Unit
            override fun getAlbumsWithType(allowedMedia: AllowedMedia) = albums
            override fun mediaFlowWithType(id: Long, allowedMedia: AllowedMedia) = flow {
                if (id == albumId) emit(Result.success(items))
                awaitCancellation()
            }

            override fun getMediaByAlbumIdWithType(albumId: Long, allowedMedia: AllowedMedia) =
                mediaFlowWithType(albumId, allowedMedia)

            override fun getMediaByType(allowedMedia: AllowedMedia) =
                mediaFlowWithType(Album.ALL_ID, allowedMedia)
        }
        val component = withContext(Dispatchers.Main) {
            MediaPickerComponent(
                componentContext = DefaultComponentContext(LifecycleRegistry().also(lifecycles::add)),
                onGoBack = {},
                onPicked = {},
                isGalleryMode = false,
                settingsManager = dependency<SettingsManager> { name, args ->
                    check(name == "getSettingsState")
                    val settings = SettingsState.Default.copy(isSemanticSearchEnabled = false)
                    if (args.isEmpty()) MutableStateFlow(settings) else settings
                },
                mediaRetriever = retriever,
                mediaActionsManager = actions,
                semanticSearchRepository = dependency<SemanticSearchRepository> { name, _ ->
                    when (name) {
                        "getState" -> MutableStateFlow(SemanticSearchState())
                        "refresh" -> Unit
                        else -> error(name)
                    }
                },
                galleryOcrRepository = dependency<GalleryOcrRepository> { name, _ ->
                    when (name) {
                        "getState" -> MutableStateFlow(GalleryOcrState())
                        else -> error(name)
                    }
                },
                galleryIndexScheduler = object : GalleryIndexScheduler {
                    override fun start() = Unit
                    override fun refresh() = Unit
                    override suspend fun stopSemanticSearch() = Unit
                },
                dispatchersHolder = dispatchers
            ).also {
                it.updateDisplaySettings(MediaDisplaySettings(dateGroup = MediaDateGroup.None))
                it.init(AllowedMedia.Photos(null))
                it.getAlbum(albumId)
                it.selectedMedia.addAll(items)
            }
        }
        withTimeout(5_000) {
            component.filteredMediaState.first {
                !it.mediaState.isLoading && it.mediaState.media.map(Media::uri)
                    .toSet() == items.map(Media::uri).toSet()
            }
        }
        withTimeout(5_000) { component.albumsState.first { state -> state.albums.any { it.id == albumId } } }
        return component
    }

    private class Actions(
        private val action: suspend (List<Media>, MediaAction) -> MediaActionResult = { media, _ ->
            MediaActionResult(media.mapTo(hashSetOf(), Media::uri), emptySet())
        }
    ) : MediaActionsManager {
        override val isAvailable = true
        val calls = mutableListOf<List<Media>>()

        override suspend fun perform(media: List<Media>, action: MediaAction): MediaActionResult {
            calls.add(media)
            return this.action(media, action)
        }
    }

    private suspend fun await(condition: () -> Boolean) = withTimeout(5_000) {
        while (!condition()) delay(10)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> dependency(
        crossinline call: (String, Array<out Any?>) -> Any?
    ): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, args ->
        call(method.name, args ?: emptyArray())
    } as T

    private fun photo(id: Int) = Media(
        id = id.toLong(),
        label = "photo_$id.jpg",
        uri = "content://media/external_primary/images/media/$id",
        path = "/Pictures/photo_$id.jpg",
        relativePath = "Pictures/",
        albumID = 1,
        albumLabel = "Pictures",
        timestamp = id.toLong(),
        mimeType = "image/jpeg",
        size = 12
    )
}
