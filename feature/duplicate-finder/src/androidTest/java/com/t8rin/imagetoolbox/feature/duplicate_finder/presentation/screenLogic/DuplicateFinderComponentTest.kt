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

package com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.screenLogic

import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FileControllerEventEmitter
import com.t8rin.imagetoolbox.core.domain.saving.model.FileDeletionResult
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.DuplicateFinder
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper.DuplicateGrouping
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisProgress
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisResult
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateItem
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateKeepStrategy
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateScanMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext

class DuplicateFinderComponentTest {

    private val lifecycle = LifecycleRegistry()
    private val pending = ConcurrentLinkedQueue<Runnable>()
    private var scans = 0
    private var deletion: ((FileDeletionResult) -> Unit)? = null
    private val items = List(8) { index ->
        DuplicateItem(
            uri = "file:///duplicate-$index.png", name = "$index.png",
            width = 100 + index, height = 100 + index, sizeBytes = 1000L - index,
            format = "png", lastModified = index.toLong(), sourceIndex = index,
            sha256 = if (index < 3) "exact" else "$index",
            dHash = (1L shl index) - 1
        )
    }
    private val dispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            pending.add(block)
        }
    }
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Main
        override val ioDispatcher = Dispatchers.IO
        override val encodingDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
        override val defaultDispatcher = dispatcher
    }

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { lifecycle.destroy() }
        drain()
    }

    @Test
    fun rapidSensitivityAndRecommendationChangesKeepLatestResultWithoutRescanning() = runBlocking {
        val component = component()
        withContext(Dispatchers.Main) {
            component.updateSensitivity(40)
            component.updateSensitivity(95)
            component.updateSensitivity(60)
            component.updateKeepStrategy(DuplicateKeepStrategy.SmallestFile)
        }
        val expected = DuplicateGrouping.regroup(items, 60, DuplicateKeepStrategy.SmallestFile)
        withTimeout(5_000) {
            while (component.groups != expected) {
                drain()
                delay(10)
            }
        }
        assertEquals(1, scans)
        assertEquals(items, component.analysisResult?.items)
    }

    @Test
    fun clearingSourcesCancelsPendingRegroup() = runBlocking {
        val component = component()
        withContext(Dispatchers.Main) {
            component.updateSensitivity(40)
            component.setUris(emptyList())
        }
        await { component.sourceUris.isEmpty() }
        drain()
        withContext(Dispatchers.Main) {
            assertNull(component.analysisResult)
            assertTrue(component.groups.isEmpty())
        }
    }

    @Test
    fun deletionAndConcurrentSettingsDoNotRestoreRemovedItems() = runBlocking {
        val component = component()
        withContext(Dispatchers.Main) {
            component.selectAllExceptRecommended()
            component.updateSensitivity(40)
            component.deleteSelected()
        }
        await { deletion != null }
        val deleted = items.first().uri
        withContext(Dispatchers.Main) {
            requireNotNull(deletion)(FileDeletionResult(listOf(deleted), emptyList()))
        }
        await { component.sourceUris.none { it.toString() == deleted } }
        withContext(Dispatchers.Main) {
            component.updateSensitivity(90)
            component.updateKeepStrategy(DuplicateKeepStrategy.Newest)
        }
        val remaining = items.filterNot { it.uri == deleted }
        val expected = DuplicateGrouping.regroup(remaining, 90, DuplicateKeepStrategy.Newest)
        withTimeout(5_000) {
            while (component.groups != expected || component.isDeleting) {
                drain()
                delay(10)
            }
        }
        assertEquals(remaining, component.analysisResult?.items)
        assertFalse(component.isDeleting)
        assertFalse(deleted in component.selectedUris)
        assertEquals(1, scans)
    }

    private suspend fun component(): DuplicateFinderComponent {
        val finder = object : DuplicateFinder {
            override suspend fun findDuplicates(
                uris: List<String>, sensitivity: Int, scanMode: DuplicateScanMode,
                onProgress: (DuplicateAnalysisProgress) -> Unit
            ): DuplicateAnalysisResult {
                scans++
                return DuplicateAnalysisResult(
                    items, DuplicateGrouping.regroup(items, sensitivity, scanMode = scanMode),
                    emptyList(), items.size
                )
            }
        }
        val files = proxy<FileController> { name, _ ->
            when (name) {
                "restoreObject" -> null
                "saveObject" -> true
                else -> error("Unexpected call: $name")
            }
        }
        val events = proxy<FileControllerEventEmitter> { name, args ->
            check(name == "deleteFiles")
            @Suppress("UNCHECKED_CAST")
            deletion = args[1] as (FileDeletionResult) -> Unit
        }
        val component = withContext(Dispatchers.Main) {
            DuplicateFinderComponent(
                DefaultComponentContext(lifecycle), null, {}, {}, finder, files, events, dispatchers
            ).also { it.setUris(items.map { item -> Uri.parse(item.uri) }) }
        }
        await { component.analysisResult != null && !component.isAnalyzing }
        return component
    }

    private fun drain() {
        while (true) (pending.poll() ?: break).run()
    }

    private suspend fun await(condition: () -> Boolean) = withTimeout(5_000) {
        while (!condition()) delay(10)
    }

    private inline fun <reified T> proxy(crossinline call: (String, Array<out Any?>) -> Any?): T =
        Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java)
        ) { _, method, args ->
            call(method.name, args.orEmpty())
        } as T
}
