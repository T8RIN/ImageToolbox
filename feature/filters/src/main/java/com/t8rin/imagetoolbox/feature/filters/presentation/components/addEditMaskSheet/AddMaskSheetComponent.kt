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

package com.t8rin.imagetoolbox.feature.filters.presentation.components.addEditMaskSheet

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters.AddFiltersSheetComponent
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.toUiPathPaint
import com.t8rin.imagetoolbox.feature.filters.domain.FilterMaskApplier
import com.t8rin.imagetoolbox.feature.filters.presentation.components.UiFilterMask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AddMaskSheetComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val filterMaskApplier: FilterMaskApplier<Bitmap, Path, Color>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder,
    addFiltersSheetComponentFactory: AddFiltersSheetComponent.Factory,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent.Factory
) : BaseComponent(dispatchersHolder, componentContext) {

    val addFiltersSheetComponent: AddFiltersSheetComponent = addFiltersSheetComponentFactory(
        componentContext = componentContext.childContext(
            key = "addFiltersMask"
        )
    )

    val filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent =
        filterTemplateCreationSheetComponent(
            componentContext = componentContext.childContext(
                key = "filterTemplateCreationSheetComponentMask"
            )
        )

    private val _maskColor = mutableStateOf(Color.Red)
    val maskColor by _maskColor

    private val _paths: MutableState<List<UiPathPaint>> = mutableStateOf(emptyList())
    val paths by _paths

    private val _lastPaths = mutableStateOf(listOf<UiPathPaint>())
    val lastPaths: List<UiPathPaint> by _lastPaths

    private val _undonePaths = mutableStateOf(listOf<UiPathPaint>())
    val undonePaths: List<UiPathPaint> by _undonePaths

    private val _filterList: MutableState<List<UiFilter<*>>> = mutableStateOf(emptyList())
    val filterList by _filterList

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap by _previewBitmap

    private val _maskPreviewModeEnabled: MutableState<Boolean> = mutableStateOf(false)
    val maskPreviewModeEnabled by _maskPreviewModeEnabled

    private val _isInverseFillType: MutableState<Boolean> = mutableStateOf(false)
    val isInverseFillType by _isInverseFillType

    private val _drawPathMode: MutableState<DrawPathMode> = mutableStateOf(DrawPathMode.Line)
    val drawPathMode by _drawPathMode

    private var bitmapUri: Uri? = null

    private var initialMasks: List<UiFilterMask> = emptyList()

    private var initialMask: UiFilterMask? = null

    private fun updatePreview() {
        debouncedImageCalculation {
            if (filterList.isEmpty() || paths.isEmpty()) {
                _maskPreviewModeEnabled.update { false }
            }
            if (maskPreviewModeEnabled) {
                imageGetter.getImage(
                    data = bitmapUri.toString(),
                    originalSize = false
                )?.let { bmp ->
                    _previewBitmap.value = filterMaskApplier.filterByMasks(
                        filterMasks = initialMasks.takeWhile {
                            it != initialMask
                        }.let {
                            it + getUiMask()
                        },
                        image = bmp
                    )?.let {
                        imagePreviewCreator.createPreview(
                            image = it,
                            imageInfo = ImageInfo(
                                width = it.width,
                                height = it.height,
                                imageFormat = ImageFormat.Png.Lossless
                            ),
                            onGetByteCount = {}
                        )
                    }
                }
            } else {
                _previewBitmap.value = imageGetter.getImage(
                    data = bitmapUri.toString(),
                    originalSize = false
                )?.let { bmp ->
                    filterMaskApplier.filterByMasks(
                        filterMasks = initialMasks.takeWhile { it != initialMask },
                        image = bmp
                    )
                }
            }
        }
    }

    fun togglePreviewMode(value: Boolean) {
        _maskPreviewModeEnabled.update { value }
        updatePreview()
    }

    fun removeFilterAtIndex(index: Int) {
        _filterList.update {
            it.toMutableList().apply {
                removeAt(index)
            }
        }
        updatePreview()
    }

    fun <T : Any> updateFilter(
        value: T,
        index: Int,
        showError: (Throwable) -> Unit,
    ) {
        val list = _filterList.value.toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _filterList.update { list }
        }.exceptionOrNull()?.let { throwable ->
            showError(throwable)
            list[index] = list[index].newInstance()
            _filterList.update { list }
        }
        updatePreview()
    }

    fun updateFiltersOrder(uiFilters: List<UiFilter<*>>) {
        _filterList.update { uiFilters }
    }

    fun addFilter(filter: UiFilter<*>) {
        _filterList.update {
            it + filter
        }
        updatePreview()
    }

    fun getUiMask(): UiFilterMask = UiFilterMask(
        filters = filterList,
        maskPaints = paths,
        isInverseFillType = isInverseFillType
    )

    fun addPath(pathPaint: UiPathPaint) {
        _paths.update { it + pathPaint }
        _undonePaths.value = listOf()
        if (maskPreviewModeEnabled) updatePreview()
    }

    fun undo() {
        if (paths.isEmpty() && lastPaths.isNotEmpty()) {
            _paths.value = lastPaths
            _lastPaths.value = listOf()
            if (maskPreviewModeEnabled) updatePreview()
            return
        }
        if (paths.isEmpty()) return

        val lastPath = paths.last()

        _paths.update { it - lastPath }
        _undonePaths.update { it + lastPath }
        if (maskPreviewModeEnabled || paths.isEmpty()) updatePreview()
    }

    fun redo() {
        if (undonePaths.isEmpty()) return

        val lastPath = undonePaths.last()
        _paths.update { it + lastPath }
        _undonePaths.update { it - lastPath }
        if (maskPreviewModeEnabled) updatePreview()
    }

    fun updateMaskColor(color: Color) {
        _maskColor.update { color }
        _paths.update { paintList ->
            paintList.map {
                it.copy(drawColor = color)
            }
        }
    }

    fun setMask(
        mask: UiFilterMask?,
        bitmapUri: Uri?,
        masks: List<UiFilterMask>,
    ) {
        mask?.let {
            _paths.update { mask.maskPaints.map { it.toUiPathPaint() } }
            _filterList.update { mask.filters.map { it.toUiFilter() } }
            _maskColor.update { initial ->
                val color = mask.maskPaints.map { it.drawColor }.toSet().firstOrNull()
                color ?: initial
            }
            _isInverseFillType.update { mask.isInverseFillType }
        }
        this.initialMask = mask
        this.bitmapUri = bitmapUri
        this.initialMasks = masks
        updatePreview()
    }

    fun toggleIsInverseFillType() {
        _isInverseFillType.update { !it }
        updatePreview()
    }

    suspend fun filter(
        bitmap: Bitmap,
        filters: List<Filter<*>>,
        size: IntegerSize? = null,
    ): Bitmap? = size?.let { intSize ->
        imageTransformer.transform(
            image = bitmap,
            transformations = filters.map { filterProvider.filterToTransformation(it) },
            size = intSize
        )
    } ?: imageTransformer.transform(
        image = bitmap,
        transformations = filters.map { filterProvider.filterToTransformation(it) }
    )

    fun setDrawPathMode(mode: DrawPathMode) {
        _drawPathMode.update { mode }
    }

    override fun resetState() {
        _maskColor.update { Color.Red }
        _paths.update { emptyList() }
        _undonePaths.update { emptyList() }
        _lastPaths.update { emptyList() }
        _filterList.update { emptyList() }
        cancelImageLoading()
        _previewBitmap.update { null }
        filterTemplateCreationSheetComponent.resetState()
        addFiltersSheetComponent.resetState()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): AddMaskSheetComponent
    }

}