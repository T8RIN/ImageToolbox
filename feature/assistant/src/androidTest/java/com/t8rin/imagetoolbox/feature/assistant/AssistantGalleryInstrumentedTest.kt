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

package com.t8rin.imagetoolbox.feature.assistant

import android.content.ContentValues
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.key
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.assistant.data.AndroidAssistantGallerySearch
import com.t8rin.imagetoolbox.feature.assistant.di.AssistantEntryPoint
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCommand
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryCommand.Action
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryPhoto
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryQuery
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantGalleryResult.Companion.PREVIEW_COUNT
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantScreenContext
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantSetting
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantTool
import com.t8rin.imagetoolbox.feature.assistant.domain.assistantGalleryPrompt
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchScheduler
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchState
import com.t8rin.neural_tools.assistant.AssistantModels
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssistantGalleryInstrumentedTest {
    private val app = InstrumentationRegistry.getInstrumentation().targetContext
    private val services = app.entryPoint<AssistantEntryPoint>()
    private val resources = services.resources
    private val settings = MutableStateFlow(SettingsState.Default)
    private val state = MutableStateFlow(
        SemanticSearchState(
            modelsReady = true,
            faceModelsReady = true, indexedImages = 30
        )
    )
    private var permission = true
    private var photos = (1..30).map(::photo)
    private var groups = emptyList<SemanticSearchCategoryResult>()
    private var ranked = emptyList<Media>()
    private var searches = 0
    private var afterSearch: () -> Unit = {}

    private fun search(retriever: MediaRetriever? = null) = AndroidAssistantGallerySearch(
        context = object : ContextWrapper(app) {
            override fun checkPermission(permissionName: String, pid: Int, uid: Int): Int =
                if (permission) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
        },
        settings = object : SettingsProvider {
            override val settingsState = settings
            override suspend fun getSettingsState() = settings.value
        },
        mediaRetriever = retriever ?: object : MediaRetriever {
            override fun getAlbumsWithType(allowedMedia: AllowedMedia) =
                flowOf(Result.success(emptyList<Album>()))

            override fun mediaFlowWithType(albumId: Long, allowedMedia: AllowedMedia) =
                getMediaByType(allowedMedia)

            override fun getMediaByAlbumIdWithType(albumId: Long, allowedMedia: AllowedMedia) =
                getMediaByType(allowedMedia)

            override fun getMediaByType(allowedMedia: AllowedMedia) = flowOf(Result.success(photos))
        },
        semantic = object : SemanticSearchRepository by services.semanticSearch {
            override val state = this@AssistantGalleryInstrumentedTest.state
            override suspend fun refresh() = Unit
            override suspend fun people(media: List<Media>) = groups
            override suspend fun search(
                query: String,
                media: List<Media>,
                minimumSimilarity: Float
            ): List<Media> {
                searches++
                ranked = media
                afterSearch()
                return media
            }
        },
        scheduler = object : SemanticSearchScheduler {
            override fun start() = Unit
            override fun refresh() = Unit
            override suspend fun stop() = Unit
        },
        resources = resources,
        dispatchers = object : DispatchersHolder {
            override val uiDispatcher = Dispatchers.Default
            override val ioDispatcher = Dispatchers.IO
            override val defaultDispatcher = Dispatchers.Default
            override val decodingDispatcher = Dispatchers.Default
            override val encodingDispatcher = Dispatchers.Default
        }
    )

    @Test
    fun disabledSearchKeepsOriginalConditionsAndRetryReadsChangedSettings() = runBlocking {
        val search = search()
        val query = AssistantGalleryQuery(semantic = "a beach", people = listOf("Саша"))
        val disabled = search.search(query, false, 1)
        assertEquals(Setting.SemanticSearch.key(), disabled.settings.single().key)
        assertTrue(disabled.gallery!!.photos.isEmpty())
        assertEquals(query, search.currentQuery())
        settings.value = settings.value.copy(isSemanticSearchEnabled = true)
        val peopleDisabled = search.retry(1)
        assertEquals(
            resources.getString(R.string.assistant_gallery_people_disabled),
            peopleDisabled.text
        )
        assertEquals(Screen.Gallery.simpleName, peopleDisabled.tools.single().key)
        assertEquals(0, searches)
        enablePeople()
        groups = listOf(person(1, "Саша", photos.take(2)))
        assertEquals(2, search.retry(1).gallery!!.total)
        assertEquals(query, search.currentQuery())
    }

    @Test
    fun allPeopleAndMetadataAreAppliedBeforeSemanticRankingAndHiddenGroupsAreIgnored() =
        runBlocking {
            enablePeople()
            groups = listOf(
                person(1, "Саша", photos.take(20)), person(2, "Маша", photos.drop(15)),
                person(3, "Саша", photos, hidden = true)
            )
            val search = search()
            val result = search.search(
                AssistantGalleryQuery(
                    semantic = "a beach", people = listOf("Саша", "Маша"),
                    from = "2026-07-01", to = "2026-07-31", album = "Vacation", filename = "IMG*"
                ), false, 1
            )
            assertEquals(photos.subList(15, 20), ranked)
            assertEquals(5, result.gallery!!.total)
            assertTrue(result.gallery.people.isEmpty())
            assertFalse(search.capabilities().contains("hidden"))
        }

    @Test
    fun duplicateNamesRequireSelectionAndHiddenIdentityCannotBeSelected() = runBlocking {
        enablePeople()
        groups = listOf(
            person(1, "Саша", photos.take(3)), person(2, "Саша", photos.drop(3).take(4)),
            person(3, "Саша", photos, hidden = true)
        )
        val search = search()
        val result =
            search.search(AssistantGalleryQuery(people = listOf("Саша")), false, 1).gallery!!
        assertEquals(listOf(1L, 2L), result.people.map { it.id })
        assertEquals(0, searches)
        assertTrue(runCatching { search.choosePerson(1, 3) }.isFailure)
        assertEquals(4, search.choosePerson(1, 2).gallery!!.total)
    }

    @Test
    fun nextPageUsesAllResultsAndRefinementIncludesPhotosOutsideShownPage() = runBlocking {
        val search = search()
        val first = search.search(AssistantGalleryQuery(album = "Vacation"), false, 1).gallery!!
        assertEquals(30, first.total)
        assertEquals(30, first.photos.size)
        val second = search.more(1).gallery!!
        assertEquals(first.photos.drop(PREVIEW_COUNT), second.photos)
        val refined =
            search.search(AssistantGalleryQuery(filename = "IMG_1.jpg"), true, 2).gallery!!
        assertEquals(1, refined.total)
        assertEquals(photos.first().uri, refined.photos.single().uri)
        assertFalse(search.isCurrent(1))
        assertEquals(0, searches)
        val fresh = search.search(AssistantGalleryQuery(), false, 3).gallery!!
        assertEquals(30, fresh.total)
    }

    @Test
    fun revokedPermissionAndDisabledFeatureNeverReturnPhotos() = runBlocking {
        enablePeople()
        val search = search()
        afterSearch = { settings.value = settings.value.copy(isSemanticSearchEnabled = false) }
        val disabled = search.search(AssistantGalleryQuery(semantic = "a cat"), false, 1)
        assertEquals(resources.getString(R.string.assistant_gallery_disabled), disabled.text)
        assertTrue(disabled.gallery!!.photos.isEmpty())
    }

    @Test
    fun noPhotoPermissionDoesNotLookLikeEmptyResults() = runBlocking {
        assumeFalse(Environment.isExternalStorageManager())
        val search = search()
        permission = false
        val denied = search.search(AssistantGalleryQuery(filename = "IMG"), false, 2)
        assertEquals(resources.getString(R.string.assistant_gallery_permission), denied.text)
        assertTrue(denied.gallery!!.photos.isEmpty())
    }

    @Test
    fun indexedPeopleRemainSearchableAfterAnIndexingFailure() = runBlocking {
        enablePeople()
        groups = listOf(person(1, "Саша", photos.take(2)))
        state.value = state.value.copy(error = "indexing failed")
        val result = search().search(AssistantGalleryQuery(people = listOf("Саша")), false, 1)
        assertEquals(2, result.gallery!!.total)
        assertEquals(resources.getString(R.string.assistant_gallery_found, 2), result.text)
    }

    @Test
    fun unindexedPhotosAreDifferentFromAnEmptyLibrary() = runBlocking {
        enablePeople()
        state.value = state.value.copy(indexedImages = 0)
        val search = search()
        val pending = search.search(AssistantGalleryQuery(semantic = "a cat"), false, 1)
        assertEquals(resources.getString(R.string.assistant_gallery_index_pending), pending.text)
        photos = emptyList()
        assertEquals(resources.getString(R.string.assistant_gallery_empty), search.retry(1).text)
    }

    @Test
    fun indexingDoesNotAddStatusTextToSearchResults() = runBlocking {
        enablePeople()
        state.value = state.value.copy(isIndexing = true)
        val search = search()
        val result = search.search(AssistantGalleryQuery(semantic = "a cat"), false, 1)
        assertEquals(30, result.gallery!!.photos.size)
        assertEquals(resources.getString(R.string.assistant_gallery_found, 30), result.text)
    }

    @Test
    fun metadataSearchReturnsARealReadablePhotoWithoutAiSearch() = runBlocking {
        val name = "assistant-gallery-${System.nanoTime()}.png"
        val resolver = app.contentResolver
        val uri = checkNotNull(
            resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AssistantGalleryTest/")
                })
        )
        try {
            val bitmap = createBitmap(32, 32)
            try {
                resolver.openOutputStream(uri)!!
                    .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            } finally {
                bitmap.recycle()
            }
            val result = search(services.mediaRetriever).search(
                AssistantGalleryQuery(filename = name, album = "AssistantGalleryTest"), false, 1
            ).gallery!!
            assertEquals(1, result.total)
            assertEquals(name, result.photos.single().name)
            resolver.openInputStream(result.photos.single().uri.toUri())!!
                .use { assertTrue(it.read() >= 0) }
        } finally {
            resolver.delete(uri, null, null)
        }
    }

    @Test
    fun previewsAllGalleryPhotosWithoutNavigationAndOpensTools() = runBlocking {
        val appSettings = app.entryPoint<SettingsStateEntryPoint>().settingsManager
        val wasEnabled = appSettings.getSettingsState().isAiAssistantEnabled
        val lifecycle = LifecycleRegistry()
        val destinations = Channel<Screen>(Channel.UNLIMITED)
        appSettings.setAiAssistantEnabled(true)
        try {
            appSettings.settingsState.first { it.isAiAssistantEnabled }
            val component = withContext(Dispatchers.Main) {
                services.assistantFactory(
                    componentContext = DefaultComponentContext(lifecycle),
                    route = Screen.Assistant(),
                    onGoBack = {},
                    onNavigate = { destinations.trySend(it) }
                )
            }
            val previews = photos.map { AssistantGalleryPhoto(it.uri, it.label) }
            val uri = previews[PREVIEW_COUNT].uri
            withContext(Dispatchers.Main) { component.openGalleryPhoto(uri, previews) }
            assertEquals(uri, component.galleryPreviewUri)
            assertEquals(previews.map { it.uri }, component.galleryPreviewUris)
            assertTrue(destinations.tryReceive().isFailure)
            withContext(Dispatchers.Main) {
                component.updateGalleryPreviewUri(previews.last().uri)
            }
            assertEquals(previews.last().uri, component.galleryPreviewUri)
            withContext(Dispatchers.Main) { component.updateGalleryPreviewUri(null) }
            assertEquals(null, component.galleryPreviewUri)
            withContext(Dispatchers.Main) { component.openGalleryTool(Screen.Crop(uri.toUri())) }
            assertEquals(Screen.Crop(uri.toUri()), destinations.receive())
            withContext(Dispatchers.Main) {
                component.openSetting(AssistantSetting(Setting.SemanticSearch.key(), "", ""))
            }
            assertEquals(
                Screen.Settings(targetSetting = Setting.SemanticSearch),
                destinations.receive()
            )
            withContext(Dispatchers.Main) {
                component.openTool(
                    AssistantTool(Screen.Gallery.simpleName, "", ""),
                    AssistantScreenContext()
                )
            }
            assertEquals(Screen.Gallery, withTimeout(5_000) { destinations.receive() })
        } finally {
            withContext(Dispatchers.Main) { lifecycle.destroy() }
            appSettings.setAiAssistantEnabled(wasEnabled)
            destinations.close()
        }
    }

    @Test
    fun assistantFindsAndRefinesARealPhotoThenOpensTheViewer() = runBlocking {
        assumeTrue(
            AssistantModels().installations()
                .any { it.variant == AssistantModel.Advanced && it.installed })
        val appSettings = app.entryPoint<SettingsStateEntryPoint>().settingsManager
        val previousSettings = appSettings.getSettingsState()
        val store = services.assistantStore
        val previousMessages = store.load()
        val lifecycle = LifecycleRegistry()
        val destinations = Channel<Screen>(Channel.UNLIMITED)
        val name = "assistant_gallery_${System.nanoTime()}.png"
        val resolver = app.contentResolver
        val uri = checkNotNull(
            resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AssistantGalleryTest/")
                })
        )
        try {
            val bitmap = createBitmap(32, 32)
            try {
                resolver.openOutputStream(uri)!!
                    .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            } finally {
                bitmap.recycle()
            }
            appSettings.setAiAssistantEnabled(true)
            appSettings.setAiAssistantModel(AssistantModel.Advanced)
            appSettings.settingsState.first { it.isAiAssistantEnabled && it.aiAssistantModel == AssistantModel.Advanced }
            services.gallerySearch.clear()
            val component = withContext(Dispatchers.Main) {
                services.assistantFactory(
                    DefaultComponentContext(lifecycle), Screen.Assistant(), {},
                    { destinations.trySend(it) })
            }
            withContext(Dispatchers.Main) {
                withTimeout(30_000) {
                    while (component.isLoading || component.isCheckingModels) delay(20)
                }
                assertTrue(component.send("Найди файл $name"))
                withTimeout(60_000) { while (component.isGenerating) delay(20) }
                val first = component.messages.last()
                assertEquals(first.toString(), 1, first.answer.gallery?.total)
                assertEquals(name, first.answer.gallery!!.photos.single().name)
                assertTrue(component.send("Из этих только из альбома AssistantGalleryTest"))
                withTimeout(60_000) { while (component.isGenerating) delay(20) }
                val refined = component.messages.last()
                assertEquals(refined.toString(), 1, refined.answer.gallery?.total)
                assertEquals(name, refined.answer.gallery!!.query.filename)
                val found = refined.answer.gallery.photos.single().uri
                component.openGalleryPhoto(found, refined.answer.gallery.photos)
                assertEquals(found, component.galleryPreviewUri)
                assertEquals(listOf(found), component.galleryPreviewUris)
            }
            assertTrue(destinations.tryReceive().isFailure)
        } finally {
            withContext(Dispatchers.Main) { lifecycle.destroy() }
            appSettings.setAiAssistantModel(previousSettings.aiAssistantModel)
            appSettings.setAiAssistantEnabled(previousSettings.isAiAssistantEnabled)
            services.gallerySearch.clear()
            store.update { previousMessages }
            store.save()
            resolver.delete(uri, null, null)
            destinations.close()
        }
    }

    @Test
    fun advancedInterpretsGalleryQueriesAndRetainsHelp() = runBlocking {
        val models = AssistantModels()
        assumeTrue(
            models.installations().any { it.variant == AssistantModel.Advanced && it.installed })
        val report = StringBuilder()
        val current = AssistantGalleryQuery(people = listOf("Саша"))
        suspend fun interpret(
            question: String,
            previous: AssistantGalleryQuery? = null
        ): AssistantGalleryCommand {
            val output = StringBuilder()
            withTimeout(90_000) {
                models.generate(
                    AssistantModel.Advanced,
                    assistantGalleryPrompt(
                        question,
                        "photoAccess=true; semanticEnabled=false; groupPeople=false; knownPeople=[\"Саша\",\"Маша\"]",
                        previous
                    ), maxOutputTokens = 256
                ) { output.append(it) }
            }
            report.appendLine("$question: $output")
            File(app.cacheDir, "assistant-gallery-planning.txt").writeText(report.toString())
            return AssistantGalleryCommand.parse(output.toString())
        }
        try {
            val cat = interpret("Найди фотографии кота")
            val person = interpret("Покажи Сашу на пляже")
            val together = interpret("Где Саша и Маша вместе?")
            val august = interpret("Найди закаты за август 2026 года")
            val album = interpret("Покажи фото из альбома Vacation")
            val july = interpret("Из этих только за июль", current)
            val outdoors = interpret("А где он на улице?", current)
            val more = interpret("Покажи ещё", current)
            val fresh = interpret("Найди фотографии кота", current)
            val help = interpret("Как сжать фото?")
            val dog = interpret("Find photos of a dog")
            val mixed = interpret("Покажи Сашу и Машу на пляже за август 2026 из альбома Vacation")
            assertEquals(report.toString(), Action.Search, cat.action)
            assertTrue(report.toString(), cat.query?.semantic?.contains("cat", true) == true)
            assertTrue(report.toString(), cat.query!!.people.isEmpty())
            assertEquals(report.toString(), listOf("Саша"), person.query!!.people)
            assertTrue(report.toString(), person.query!!.semantic!!.contains("beach", true))
            assertEquals(report.toString(), setOf("Саша", "Маша"), together.query!!.people.toSet())
            assertTrue(report.toString(), august.query!!.semantic!!.contains("sunset", true))
            assertEquals(report.toString(), "2026-08-01", august.query!!.from)
            assertEquals(report.toString(), "2026-08-31", august.query!!.to)
            assertEquals(report.toString(), "Vacation", album.query!!.album)
            assertEquals(report.toString(), null, album.query!!.semantic)
            assertEquals(report.toString(), Action.Refine, july.action)
            assertEquals(report.toString(), listOf("Саша"), july.query!!.refine(current).people)
            assertEquals(report.toString(), "${LocalDate.now().year}-07-01", july.query!!.from)
            assertEquals(report.toString(), Action.Refine, outdoors.action)
            assertEquals(report.toString(), listOf("Саша"), outdoors.query!!.refine(current).people)
            assertEquals(report.toString(), Action.More, more.action)
            assertEquals(report.toString(), Action.Search, fresh.action)
            assertTrue(report.toString(), fresh.query!!.people.isEmpty())
            assertEquals(report.toString(), Action.Help, help.action)
            assertTrue(report.toString(), dog.query!!.semantic!!.contains("dog", true))
            assertEquals(report.toString(), setOf("Саша", "Маша"), mixed.query!!.people.toSet())
            assertEquals(report.toString(), "2026-08-01", mixed.query!!.from)
            assertEquals(report.toString(), "2026-08-31", mixed.query!!.to)
            assertEquals(report.toString(), "Vacation", mixed.query!!.album)
            assertTrue(report.toString(), mixed.query!!.semantic!!.contains("beach", true))
        } finally {
            models.release()
            File(app.cacheDir, "assistant-gallery-planning.txt").writeText(report.toString())
        }
    }

    private fun enablePeople() {
        settings.value = settings.value.copy(
            isSemanticSearchEnabled = true,
            semanticSearchSettings = settings.value.semanticSearchSettings.copy(groupPeople = true)
        )
    }

    private fun person(id: Long, name: String, media: List<Media>, hidden: Boolean = false) =
        SemanticSearchCategoryResult(
            SemanticSearchCategory.Person(
                id,
                name,
                "thumbnail:$id",
                hidden = hidden
            ), media
        )

    private fun photo(id: Int) = Media(
        id = id.toLong(),
        label = "IMG_$id.jpg",
        uri = "content://media/$id",
        path = "/Pictures/IMG_$id.jpg",
        relativePath = "Pictures/",
        albumID = 1,
        albumLabel = "Vacation",
        timestamp = 1_784_073_600,
        mimeType = "image/jpeg"
    )
}