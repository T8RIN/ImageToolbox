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

package com.t8rin.imagetoolbox.feature.checksum_tools.presentation.screenLogic

import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.feature.checksum_tools.domain.ChecksumManager
import com.t8rin.imagetoolbox.feature.checksum_tools.domain.ChecksumSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy

class ChecksumToolsComponentTest {
    private val lifecycle = LifecycleRegistry()
    private val requests = Channel<CompletableDeferred<String>>(Channel.UNLIMITED)
    private val types = mutableListOf<HashingType>()
    private val manager = object : ChecksumManager {
        override suspend fun calculateChecksum(type: HashingType, source: ChecksumSource): String {
            types += type
            val result = CompletableDeferred<String>()
            requests.send(result)
            return result.await()
        }
    }
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Main
        override val ioDispatcher = Dispatchers.IO
        override val defaultDispatcher = Dispatchers.Default
        override val encodingDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
    }

    @After
    fun tearDown() =
        InstrumentationRegistry.getInstrumentation().runOnMainSync { lifecycle.destroy() }

    @Test
    fun comparisonReadsOnceAndUsesTheLatestTarget() = runBlocking {
        val component = component()
        withContext(Dispatchers.Main) { component.setDataForComparison(Uri.parse("file:///first")) }
        val request = nextRequest()
        withContext(Dispatchers.Main) {
            component.setTargetChecksum("old")
            component.setTargetChecksum("expected")
        }
        request.complete("expected")
        await { component.compareWithUriPage.isCorrect }
        withContext(Dispatchers.Main) { component.setTargetChecksum("different") }
        assertFalse(component.compareWithUriPage.isCorrect)
        delay(150)
        assertTrue(requests.tryReceive().isFailure)
        assertEquals("expected", component.compareWithUriPage.checksum)
    }

    @Test
    fun olderTextResultCannotReplaceNewInput() = runBlocking {
        val component = component()
        withContext(Dispatchers.Main) { component.setText("old") }
        val old = nextRequest()
        withContext(Dispatchers.Main) { component.setText("new") }
        val current = nextRequest()
        current.complete("new hash")
        await { component.calculateFromTextPage.checksum == "new hash" }
        old.complete("old hash")
        delay(100)
        assertEquals("new", component.calculateFromTextPage.text)
        assertEquals("new hash", component.calculateFromTextPage.checksum)
    }

    @Test
    fun changingAlgorithmRecalculatesAndClearingUriCancelsComparison() = runBlocking {
        val component = component()
        withContext(Dispatchers.Main) { component.setDataForComparison(Uri.parse("file:///first")) }
        nextRequest().complete("old hash")
        await { component.compareWithUriPage.checksum == "old hash" }
        withContext(Dispatchers.Main) { component.updateChecksumType(HashingType.SHA_512) }
        val current = nextRequest()
        assertEquals(listOf(HashingType.MD5, HashingType.SHA_512), types)
        withContext(Dispatchers.Main) { component.setDataForComparison(null) }
        current.complete("new hash")
        delay(100)
        assertEquals(null, component.compareWithUriPage.uri)
        assertEquals("", component.compareWithUriPage.checksum)
        assertFalse(component.compareWithUriPage.isCorrect)
    }

    @Test
    fun batchCompletionKeepsTheEditedTarget() = runBlocking {
        val component = component()
        val uri = Uri.parse("file:///batch")
        withContext(Dispatchers.Main) { component.setDataForBatchComparison(listOf(uri), "old") }
        val request = nextRequest()
        withContext(Dispatchers.Main) { component.setDataForBatchComparison(targetChecksum = "new") }
        request.complete("hash")
        await { component.compareWithUrisPage.uris.isNotEmpty() }
        assertEquals("new", component.compareWithUrisPage.targetChecksum)
        assertEquals("hash", component.compareWithUrisPage.uris.single().checksum)
    }

    private suspend fun component() = withContext(Dispatchers.Main) {
        val files = Proxy.newProxyInstance(
            FileController::class.java.classLoader,
            arrayOf(FileController::class.java)
        ) { _, method, _ ->
            when (method.name) {
                "restoreObject" -> null
                "saveObject" -> true
                else -> error("Unexpected call: ${method.name}")
            }
        } as FileController
        ChecksumToolsComponent(
            DefaultComponentContext(lifecycle),
            null,
            {},
            manager,
            files,
            dispatchers
        )
    }

    private suspend fun nextRequest() = withTimeout(5_000) { requests.receive() }
    private suspend fun await(condition: () -> Boolean) = withTimeout(5_000) {
        while (!condition()) delay(10)
    }
}