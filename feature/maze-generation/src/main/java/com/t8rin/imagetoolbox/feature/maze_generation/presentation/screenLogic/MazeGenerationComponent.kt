/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.maze_generation.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.maze_generation.data.MazeRecipeStore
import com.t8rin.imagetoolbox.feature.maze_generation.data.MazeSvgFileFactory
import com.t8rin.imagetoolbox.feature.maze_generation.domain.*
import com.t8rin.imagetoolbox.feature.maze_generation.rendering.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class MazeGenerationComponent
@AssistedInject
internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val fileController: FileController,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val settingsManager: SettingsManager,
    private val store: MazeRecipeStore,
    private val svgFileFactory: MazeSvgFileFactory,
) :
    BaseHistoryComponent<MazeGenerationComponent.HistorySnapshot>(
        dispatchersHolder,
        componentContext,
    ) {
    var recipe by mutableStateOf(MazeRecipe())
        private set

    var outputSize by mutableStateOf(IntegerSize(2048, 2048))
        private set

    var imageFormat by mutableStateOf(ImageFormat.Default)
        private set

    var quality by mutableStateOf<Quality>(Quality.Base(100))
        private set

    var svg by mutableStateOf(false)
        private set

    var previewBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var statistics by mutableStateOf<MazeStatistics?>(null)
        private set

    var elapsedMillis by mutableStateOf(0L)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var previewVector by mutableStateOf<MazeSvgPainter?>(null)
        private set

    private var revision = 0
    private var generationJob: Job? = null
    private var savingJob: Job? = null
    private var persistenceJob: Job? = null
    private var cached: Pair<MazeRecipe, MazeResult>? = null

    init {
        resetHistory()
        componentScope.launch {
            val last = store.loadLast()
            if (revision == 0) {
                recipe =
                    last.copy(
                        seed = last.seed.coerceIn(-10000, 10000),
                        style = last.style.withColorAlpha(),
                    )
                resetHistory()
                updatePreview()
            }
        }
    }

    private inline fun change(block: () -> Unit) {
        beginPendingHistoryTransaction()
        block()
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun updateRecipe(value: MazeRecipe) {
        if (value == recipe) return
        if (!value.algorithm.supports(value.topology)) {
            error =
                com.t8rin.imagetoolbox.core.utils.getString(
                    com.t8rin.imagetoolbox.core.resources.R.string.maze_incompatible
                )
            return
        }
        try {
            value.validated()
        } catch (exception: IllegalArgumentException) {
            error = exception.message ?: exception.javaClass.simpleName
            return
        }
        change { recipe = value }
        updatePreview()
        persistenceJob?.cancel()
        persistenceJob =
            componentScope.launch {
                delay(500)
                store.saveLast(recipe)
            }
    }

    fun setTopology(value: MazeTopology) =
        updateRecipe(
            recipe.copy(
                topology = value,
                algorithm =
                    recipe.algorithm.takeIf { it.supports(value) } ?: MazeAlgorithm.Backtracker,
            )
        )

    fun setWidth(value: Int) {
        val width = value.coerceAtMost(8192)
        change { outputSize = outputSize.copy(width = width) }
        updatePreview()
    }

    fun setHeight(value: Int) {
        val height = value.coerceAtMost(8192)
        change { outputSize = outputSize.copy(height = height) }
        updatePreview()
    }

    fun updateImageFormat(value: ImageFormat) {
        if (!svg && value == imageFormat) return
        change {
            imageFormat = value
            svg = false
        }
        updatePreview()
    }

    fun updateQuality(value: Quality) {
        if (value != quality) change { quality = value }
    }

    fun updateSvg(value: Boolean) {
        if (svg == value) return
        change { svg = value }
        updatePreview()
    }

    fun dismissError() {
        error = null
    }

    private fun updatePreview() {
        revision++
        val ticket = revision
        val input = recipe
        val size = outputSize
        generationJob?.cancel()
        // Empty fields are retained while editing, just like Noise Generation.
        if (size.width <= 0 || size.height <= 0) {
            previewBitmap = null
            previewVector = null
            _isImageLoading.value = false
            return
        }
        val vector = svg
        _isImageLoading.value = true
        generationJob =
            componentScope.launch {
                try {
                    delay(200)
                    val cache = cached
                    val result =
                        withContext(defaultDispatcher) {
                            if (cache?.first == input.graphKey()) cache.second
                            else MazeGenerator.generate(input)
                        }
                    val (solution, image) =
                        withContext(defaultDispatcher) {
                            val solution = MazeSolver.solve(result, input)
                            val scene = MazeSceneBuilder.build(result, solution, input)
                            solution to
                                if (vector) {
                                    MazeSvgPainter.create(
                                        MazeSvgRenderer.render(
                                            scene,
                                            size.width,
                                            size.height,
                                            store.encode(input),
                                        ),
                                        size,
                                    )
                                } else preview(scene, size)
                        }
                    ensureActive()
                    if (ticket == revision) {
                        cached = input.graphKey() to result
                        previewBitmap = image as? Bitmap
                        previewVector = image as? MazeSvgPainter
                        statistics = MazeSolver.statistics(result, solution)
                        elapsedMillis = result.elapsedMillis
                        error = null
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (t: Throwable) {
                    if (ticket == revision) {
                        error = t.message ?: t.javaClass.simpleName
                        previewBitmap = null
                        previewVector = null
                        statistics = null
                    }
                } finally {
                    if (ticket == revision) _isImageLoading.value = false
                }
            }
    }

    private suspend fun preview(scene: MazeScene, size: IntegerSize): Bitmap {
        val scale = 768f / max(size.width, size.height)
        return MazeBitmapRenderer.render(
            scene,
            (size.width * scale).roundToInt().coerceAtLeast(1),
            (size.height * scale).roundToInt().coerceAtLeast(1),
        )
    }

    fun cancelGeneration() {
        revision++
        generationJob?.cancel()
        _isImageLoading.value = false
    }

    private data class Export(
        val recipe: MazeRecipe,
        val size: IntegerSize,
        val format: ImageFormat,
        val quality: Quality,
        val svg: Boolean,
    )

    private suspend fun scene(export: Export): MazeScene =
        withContext(defaultDispatcher) {
            val result =
                cached?.takeIf { it.first == export.recipe.graphKey() }?.second
                    ?: MazeGenerator.generate(export.recipe)
            MazeSceneBuilder.build(result, MazeSolver.solve(result, export.recipe), export.recipe)
        }

    private fun export(action: suspend (Export, MazeScene) -> Unit) {
        if (isSaving || outputSize.width <= 0 || outputSize.height <= 0) return
        val snapshot = Export(recipe, outputSize, imageFormat, quality, svg)
        isSaving = true
        savingJob =
            componentScope.launch {
                try {
                    action(snapshot, scene(snapshot))
                } catch (e: CancellationException) {
                    throw e
                } catch (t: Throwable) {
                    error = t.message ?: t.javaClass.simpleName
                    parseSaveResult(SaveResult.Error.Exception(t))
                } finally {
                    isSaving = false
                }
            }
    }

    fun saveMaze(oneTimeSaveLocationUri: String?) = export { e, scene ->
        val target =
            if (e.svg) {
                val data =
                    withContext(defaultDispatcher) {
                        MazeSvgRenderer.render(
                                scene,
                                e.size.width,
                                e.size.height,
                                store.encode(e.recipe),
                            )
                            .toByteArray()
                    }
                svgFileFactory.create(data = data, width = e.size.width, height = e.size.height)
            } else {
                val bitmap =
                    withContext(defaultDispatcher) {
                        MazeBitmapRenderer.render(scene, e.size.width, e.size.height)
                    }
                try {
                    ImageSaveTarget(
                        imageInfo =
                            ImageInfo(
                                width = bitmap.width,
                                height = bitmap.height,
                                quality = e.quality,
                                imageFormat = e.format,
                            ),
                        metadata = null,
                        originalUri = "",
                        sequenceNumber = null,
                        data =
                            imageCompressor.compress(
                                image = bitmap,
                                imageFormat = e.format,
                                quality = e.quality,
                            ),
                    )
                } finally {
                    bitmap.recycle()
                }
            }
        parseSaveResult(
            fileController
                .save(
                    saveTarget = target,
                    keepOriginalMetadata = false,
                    oneTimeSaveLocationUri = oneTimeSaveLocationUri,
                )
                .onSuccess {
                    if (
                        recipe == e.recipe &&
                            outputSize == e.size &&
                            imageFormat == e.format &&
                            quality == e.quality &&
                            svg == e.svg
                    )
                        registerSave()
                }
        )
    }

    fun cacheCurrentMaze(onComplete: (Uri) -> Unit) = export { e, scene ->
        val uri =
            if (e.svg) {
                val bytes =
                    withContext(defaultDispatcher) {
                        MazeSvgRenderer.render(
                                scene,
                                e.size.width,
                                e.size.height,
                                store.encode(e.recipe),
                            )
                            .toByteArray()
                    }
                val target =
                    svgFileFactory.create(
                        data = bytes,
                        width = e.size.width,
                        height = e.size.height,
                    )
                shareProvider.cacheByteArray(target.data, target.filename)
            } else {
                val bitmap =
                    withContext(defaultDispatcher) {
                        MazeBitmapRenderer.render(scene, e.size.width, e.size.height)
                    }
                try {
                    shareProvider.cacheImage(
                        bitmap,
                        ImageInfo(
                            width = bitmap.width,
                            height = bitmap.height,
                            imageFormat = e.format,
                            quality = e.quality,
                        ),
                    )
                } finally {
                    bitmap.recycle()
                }
            }
        uri?.let { onComplete(it.toUri()) }
    }

    fun shareMaze() = cacheCurrentMaze { uri ->
        componentScope.launch {
            shareProvider.shareUri(uri.toString(), onComplete = AppToastHost::showConfetti)
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
    }

    fun importMask(uri: Uri, threshold: Int, invert: Boolean) {
        val input = recipe
        componentScope.launch {
            try {
                val mask = store.sampleMask(uri, input.rows, input.columns, threshold, invert)
                if (recipe != input) return@launch
                updateRecipe(
                    input.copy(
                        topology = MazeTopology.Mask,
                        mask = MazeMask.Image,
                        imageMask = mask,
                    )
                )
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                error = t.message ?: t.javaClass.simpleName
            }
        }
    }

    private fun MazeStyle.withColorAlpha(): MazeStyle =
        if (transparent) {
            copy(backgroundColor = backgroundColor and 0x00ffffff, transparent = false)
        } else this

    override fun currentHistorySnapshot() =
        HistorySnapshot(
            recipe,
            outputSize,
            imageFormat,
            quality,
            svg,
            settingsManager.settingsState.value.backgroundForNoAlphaImageFormats,
        )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        recipe = snapshot.recipe
        outputSize = snapshot.size
        imageFormat = snapshot.format
        quality = snapshot.quality
        svg = snapshot.svg
        restoreBackgroundColorForNoAlphaFormats(settingsManager, snapshot.background)
        updatePreview()
        persistenceJob?.cancel()
        persistenceJob = componentScope.launch { store.saveLast(recipe) }
    }

    data class HistorySnapshot(
        val recipe: MazeRecipe,
        val size: IntegerSize,
        val format: ImageFormat,
        val quality: Quality,
        val svg: Boolean,
        val background: ColorModel,
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): MazeGenerationComponent
    }
}
