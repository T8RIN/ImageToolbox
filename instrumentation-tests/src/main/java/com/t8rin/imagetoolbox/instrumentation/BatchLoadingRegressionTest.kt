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

package com.t8rin.imagetoolbox.instrumentation

import android.net.Uri
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.domain.AudioCoverRetriever
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.domain.model.AudioCoverResult
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.ui.screenLogic.AudioCoverExtractorComponent
import com.t8rin.imagetoolbox.feature.batchrename.domain.RenameManager
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameFile
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameResult
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameTarget
import com.t8rin.imagetoolbox.feature.batchrename.presentation.screenLogic.BatchRenameComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class BatchLoadingRegressionTest {
    private val lifecycle = LifecycleRegistry()
    private val reads = mutableListOf<List<String>>()
    private var loadFiles: suspend (List<String>) -> List<RenameFile> = { uris -> uris.map(::file) }
    private val manager = object : RenameManager {
        override suspend fun readFiles(uris: Iterable<String>): List<RenameFile> {
            reads += uris.toList()
            return loadFiles(uris.toList())
        }

        override suspend fun rename(
            files: List<RenameTarget>,
            writableUris: Set<String>
        ): RenameResult = error("Unexpected rename")
    }
    private val settings = object : SettingsProvider {
        override val settingsState = MutableStateFlow(SettingsState.Default)
        override suspend fun getSettingsState() = settingsState.value
    }

    @After
    fun tearDown() = runBlocking {
        withContext(Dispatchers.Main) { lifecycle.destroy() }
    }

    @Test
    fun addingFilesOnlyReadsNewMetadataAndKeepsHistoryAndOrder() = runBlocking {
        val component = renameComponent()
        withContext(Dispatchers.Main) { component.setUris(listOf(uri("a"), uri("b"))) }
        await { component.files.size == 2 }
        val first = component.files.first()
        withContext(Dispatchers.Main) { component.addUris(listOf(uri("b"), uri("c"), uri("c"))) }
        await { component.files.size == 3 }
        assertEquals(listOf(listOf("file:///a", "file:///b"), listOf("file:///c")), reads)
        assertSame(first, component.files.first())
        assertEquals(listOf("a", "b", "c"), component.files.map { it.originalName })
        withContext(Dispatchers.Main) { component.undo() }
        assertEquals(listOf("a", "b"), component.files.map { it.originalName })
        withContext(Dispatchers.Main) { component.addUris(listOf(uri("a"))) }
        delay(150)
        assertEquals(2, reads.size)
    }

    @Test
    fun replacingSelectionRejectsLateMetadataFromCancelledLoad() = runBlocking {
        val gate = CompletableDeferred<Unit>()
        loadFiles = { uris ->
            if (uris == listOf("file:///a")) withContext(NonCancellable) { gate.await() }
            uris.map(::file)
        }
        val component = renameComponent()
        withContext(Dispatchers.Main) { component.setUris(listOf(uri("a"))) }
        await { reads.isNotEmpty() }
        withContext(Dispatchers.Main) { component.setUris(listOf(uri("b"))) }
        await { component.files.map { it.originalName } == listOf("b") }
        gate.complete(Unit)
        delay(150)
        assertEquals(listOf("b"), component.files.map { it.originalName })
        assertFalse(component.isLoading)
    }

    @Test
    fun overlappingAddsKeepPendingUrisAndRemovalDoesNotRestoreFile() = runBlocking {
        val gate = CompletableDeferred<Unit>()
        loadFiles = { uris -> gate.await(); uris.map(::file) }
        val component = renameComponent()
        withContext(Dispatchers.Main) {
            component.setUris(listOf(uri("a")))
            component.addUris(listOf(uri("b")))
            component.addUris(listOf(uri("c")))
            component.removeFile(uri("b"))
        }
        gate.complete(Unit)
        await { component.files.size == 2 }
        assertEquals(listOf("a", "c"), component.files.map { it.originalName })
    }

    @Test
    fun audioLoadsAreBoundedDeduplicatedAndCannotRestoreRemovedItems() = runBlocking {
        val gate = CompletableDeferred<Unit>()
        val calls = mutableListOf<String>()
        var active = 0
        var maximum = 0
        val retriever = object : AudioCoverRetriever {
            override suspend fun loadCover(audioUri: String): AudioCoverResult {
                calls += audioUri
                active++
                maximum = maxOf(maximum, active)
                try {
                    gate.await()
                    return AudioCoverResult.Success("$audioUri.png")
                } finally {
                    active--
                }
            }
        }
        val component = audioComponent(retriever)
        withContext(Dispatchers.Main) {
            component.updateCovers(
                listOf(
                    uri("a"),
                    uri("b"),
                    uri("c")
                )
            )
        }
        await { calls.size == 2 }
        withContext(Dispatchers.Main) {
            component.addCovers(listOf(uri("b"), uri("d")))
            component.removeCover(uri("a"))
        }
        gate.complete(Unit)
        await { component.covers.none { it.isLoading } }
        assertEquals(
            listOf("file:///b", "file:///c", "file:///d"),
            component.covers.map { it.audioUri.toString() })
        assertEquals(calls.distinct(), calls)
        assertTrue(maximum <= 2)
    }

    @Test
    fun replacingAudioWithSameUriDoesNotLoseNewJobOrPublishOldCover() = runBlocking {
        val old = CompletableDeferred<Unit>()
        val fresh = CompletableDeferred<Unit>()
        var calls = 0
        val retriever = object : AudioCoverRetriever {
            override suspend fun loadCover(audioUri: String): AudioCoverResult {
                val attempt = ++calls
                if (attempt == 1) withContext(NonCancellable) { old.await() } else fresh.await()
                return AudioCoverResult.Success("file:///cover-$attempt.png")
            }
        }
        val component = audioComponent(retriever)
        withContext(Dispatchers.Main) { component.updateCovers(listOf(uri("a"))) }
        await { calls == 1 }
        withContext(Dispatchers.Main) { component.updateCovers(listOf(uri("a"))) }
        await { calls == 2 }
        old.complete(Unit)
        delay(100)
        withContext(Dispatchers.Main) { component.addCovers(listOf(uri("a"))) }
        delay(100)
        assertEquals(2, calls)
        fresh.complete(Unit)
        await { component.covers.none { it.isLoading } }
        assertEquals("file:///cover-2.png", component.covers.single().imageCoverUri.toString())
    }

    @Test
    fun replacingSeveralAudioJobsCancelsAllPreviousLoads() = runBlocking {
        val gate = CompletableDeferred<Unit>()
        var started = 0
        val component = audioComponent(object : AudioCoverRetriever {
            override suspend fun loadCover(audioUri: String): AudioCoverResult {
                started++
                gate.await()
                return AudioCoverResult.Success("$audioUri.png")
            }
        })
        withContext(Dispatchers.Main) {
            component.updateCovers(
                listOf(
                    uri("a"),
                    uri("b"),
                    uri("c")
                )
            )
        }
        await { started == 2 }
        withContext(Dispatchers.Main) { component.updateCovers(listOf(uri("d"), uri("e"))) }
        gate.complete(Unit)
        await { component.covers.none { it.isLoading } }
        assertEquals(listOf(uri("d"), uri("e")), component.covers.map { it.audioUri })
    }

    private suspend fun renameComponent() = withContext(Dispatchers.Main) {
        BatchRenameComponent(
            DefaultComponentContext(lifecycle),
            null,
            {},
            manager,
            settings,
            TestDispatchers
        )
    }

    private suspend fun audioComponent(retriever: AudioCoverRetriever) =
        withContext(Dispatchers.Main) {
            AudioCoverExtractorComponent(
                DefaultComponentContext(lifecycle), null, {}, {},
                testProxy { _, _ -> error("Unexpected file access") },
                testProxy { _, _ -> error("Unexpected compression") },
                testProxy { _, _ -> error("Unexpected decode") },
                testProxy { _, _ -> error("Unexpected sharing") }, retriever, TestDispatchers
            )
        }

    private fun file(uri: String) =
        RenameFile(uri, uri.substringAfterLast('/'), width = 123, exifDateTaken = 456L)

    private fun uri(name: String) = Uri.parse("file:///$name")
}

internal suspend fun await(condition: () -> Boolean) = withTimeout(5_000) {
    while (!withContext(Dispatchers.Main) { condition() }) delay(10)
}