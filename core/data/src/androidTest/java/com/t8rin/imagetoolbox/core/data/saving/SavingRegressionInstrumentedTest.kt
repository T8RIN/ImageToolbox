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

package com.t8rin.imagetoolbox.core.data.saving

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.RandomStringGenerator
import com.t8rin.imagetoolbox.core.domain.saving.model.FileControllerEvent
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class SavingRegressionInstrumentedTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val directory = File(context.cacheDir, "saving_regression_${System.nanoTime()}")
        .apply { check(mkdirs()) }
    private val appJob = SupervisorJob()
    private val dispatchers = object : DispatchersHolder {
        override val uiDispatcher = Dispatchers.Main
        override val ioDispatcher = Dispatchers.IO
        override val encodingDispatcher = Dispatchers.Default
        override val decodingDispatcher = Dispatchers.Default
        override val defaultDispatcher = Dispatchers.Default
    }
    private val deletionHelper = OriginalFileDeletionHelper(
        context = context,
        appScope = object : AppScope {
            override val coroutineContext = appJob + Dispatchers.Default
        },
        dispatchersHolder = dispatchers
    )

    @After
    fun tearDown() {
        appJob.cancel()
        directory.deleteRecursively()
    }

    @Test
    fun originalFilenameKeepsExistingNumberWhenSequenceIsDisabled() {
        val settings = MutableStateFlow(
            SettingsState.Default.copy(
                addOriginalFilename = true,
                addSequenceNumber = false,
                addSizeInFilename = false,
                addTimestampToFilename = false,
                addPresetInfoToFilename = false,
                addImageScaleModeInfoToFilename = false,
                filenamePrefix = "",
                filenameSuffix = "",
                filenamePattern = null,
                filenameBehavior = FilenameBehavior.None()
            )
        )
        val creator = AndroidFilenameCreator(
            randomStringGenerator = object : RandomStringGenerator {
                override fun generate(length: Int) = "a".repeat(length)
            },
            context = context,
            settingsManager = object : SettingsManager by context
                .entryPoint<SettingsStateEntryPoint>().settingsManager {
                override val settingsState = settings
            },
            dispatchersHolder = dispatchers,
            resourceManager = object : ResourceManager {
                override fun getString(resId: Int) = context.getString(resId)
                override fun getString(resId: Int, vararg formatArgs: Any) =
                    context.getString(resId, *formatArgs)

                override fun getStringLocalized(resId: Int, language: String) = getString(resId)
                override fun getStringLocalized(
                    resId: Int,
                    language: String,
                    vararg formatArgs: Any
                ) =
                    getString(resId, *formatArgs)
            }
        )
        val target = ImageSaveTarget(
            imageInfo = ImageInfo(width = 16, height = 12, imageFormat = ImageFormat.Jpg),
            originalUri = File(directory, "Инструкция (4).webp").toUri().toString(),
            sequenceNumber = 3,
            data = byteArrayOf()
        )

        assertEquals("Инструкция (4).jpg", creator.constructImageFilename(target))
        assertEquals(
            "Инструкция (4).jpg",
            creator.constructImageFilename(target, pattern = "\\o[\\c{10:2:3}].\\e")
        )
        settings.value = settings.value.copy(addSequenceNumber = true)
        assertEquals("Инструкция (4)[3].jpg", creator.constructImageFilename(target))
        assertEquals(
            "Инструкция (4)[014].jpg",
            creator.constructImageFilename(target, pattern = "\\o[\\c{10:2:3}].\\e")
        )
    }

    @Test
    fun reportsAllNineOriginalsAcrossFoldersAndResetsForNextBatch() = runBlocking {
        repeat(2) { batchIndex ->
            val count = if (batchIndex == 0) 9 else 1
            KeepAliveService.EMPTY.track {
                repeat(count) { index ->
                    val parent = File(directory, "$batchIndex/${index / 3}").apply { mkdirs() }
                    val original = File(parent, "source_$index.jpg").apply { writeText("original") }
                    val output = File(parent, "saved_$index.jpg").apply { writeText("saved") }
                    deletionHelper.deleteAfterSuccessfulSave(
                        original.toUri().toString(),
                        output.toUri()
                    )
                    assertFalse(original.exists())
                    assertEquals("saved", output.readText())
                    assertNull(withTimeoutOrNull(350) { deletionHelper.events.first() })
                }
            }
            assertEquals(
                FileControllerEvent.OriginalFilesDeleteResult(deleted = count, failed = 0),
                withTimeout(5_000) { deletionHelper.events.first() }
            )
        }
    }

    @Test
    fun reportsCompletedDeletionsWhenBatchIsCancelled() = runBlocking {
        val job = launch {
            KeepAliveService.EMPTY.track {
                repeat(3) { index ->
                    val original =
                        File(directory, "source_$index.jpg").apply { writeText("original") }
                    val output = File(directory, "saved_$index.jpg").apply { writeText("saved") }
                    deletionHelper.deleteAfterSuccessfulSave(
                        original.toUri().toString(),
                        output.toUri()
                    )
                }
                throw CancellationException()
            }
        }
        job.join()
        assertEquals(
            FileControllerEvent.OriginalFilesDeleteResult(deleted = 3, failed = 0),
            withTimeout(5_000) { deletionHelper.events.first() }
        )
    }
}