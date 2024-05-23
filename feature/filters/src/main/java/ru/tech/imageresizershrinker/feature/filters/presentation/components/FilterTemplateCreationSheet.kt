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

package ru.tech.imageresizershrinker.feature.filters.presentation.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import coil.transform.Transformation
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.tech.imageresizershrinker.core.data.utils.toCoil
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageHeaderState
import ru.tech.imageresizershrinker.core.ui.widget.image.SimplePicture
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.ScopedViewModelContainer
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import javax.inject.Inject

@Composable
internal fun FilterTemplateCreationSheet(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    ScopedViewModelContainer<FilterTemplateCreationSheetViewModel> { disposable ->
        val viewModel = this

        var selectedUri by rememberSaveable {
            mutableStateOf<Uri?>(null)
        }

        LaunchedEffect(selectedUri) {
            viewModel.setUri(selectedUri)
        }

        val isPortrait by isPortraitOrientationAsState()

        val showAddFilterSheet = rememberSaveable { mutableStateOf(false) }

        val context = LocalContext.current as ComponentActivity
        val toastHostState = LocalToastHostState.current
        val scope = rememberCoroutineScope()

        var showExitDialog by remember { mutableStateOf(false) }

        val showReorderSheet = rememberSaveable { mutableStateOf(false) }

        val canSave = viewModel.filterList.isNotEmpty()

        SimpleSheet(
            visible = visible,
            onDismiss = {
                if (!canSave) onDismiss()
                else showExitDialog = true
            },
            cancelable = false,
            title = {
                TitleItem(
                    text = stringResource(id = R.string.create_template),
                    icon = Icons.Outlined.Extension
                )
            },
            confirmButton = {
                EnhancedButton(
                    enabled = canSave && viewModel.templateName.isNotEmpty(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        viewModel.saveTemplate()
                        onDismiss()
                    }
                ) {
                    Text(stringResource(id = R.string.save))
                }
            },
            dragHandle = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawHorizontalStroke(autoElevation = 3.dp)
                        .zIndex(Float.MAX_VALUE)
                        .background(SimpleSheetDefaults.barContainerColor)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (!canSave) onDismiss()
                            else showExitDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                }
            },
            enableBackHandler = false
        ) {
            var imageState by remember { mutableStateOf(ImageHeaderState(2)) }

            disposable()
            if (visible) {
                BackHandler {
                    if (!canSave) onDismiss()
                    else showExitDialog = true
                }
            }
            val preview: @Composable () -> Unit = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            if (isPortrait) RoundedCornerShape(
                                bottomStart = 24.dp,
                                bottomEnd = 24.dp
                            )
                            else RectangleShape
                        )
                        .background(
                            color = MaterialTheme.colorScheme
                                .surfaceContainer
                                .copy(0.8f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SimplePicture(
                        enableContainer = false,
                        boxModifier = Modifier.padding(24.dp),
                        bitmap = viewModel.previewBitmap,
                        loading = viewModel.previewLoading
                    )
                }
            }
            Row {
                val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
                if (!isPortrait) {
                    Box(modifier = Modifier.weight(1.3f)) {
                        preview()
                    }
                }
                val internalHeight = rememberAvailableHeight(imageState = imageState)
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    imageStickyHeader(
                        visible = isPortrait,
                        internalHeight = internalHeight,
                        imageState = imageState,
                        onStateChange = {
                            imageState = it
                        },
                        padding = 0.dp,
                        imageModifier = Modifier.padding(bottom = 24.dp),
                        backgroundColor = backgroundColor,
                        imageBlock = preview
                    )
                    item {
                        AnimatedContent(
                            targetState = viewModel.filterList.isNotEmpty(),
                            transitionSpec = {
                                fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                            }
                        ) { notEmpty ->
                            if (notEmpty) {
                                Column {
                                    RoundedTextField(
                                        modifier = Modifier
                                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                                            .container(MaterialTheme.shapes.large),
                                        onValueChange = viewModel::updateTemplateName,
                                        value = viewModel.templateName,
                                        label = stringResource(R.string.template_name)
                                    )
                                    Column(
                                        Modifier
                                            .padding(16.dp)
                                            .container(MaterialTheme.shapes.extraLarge)
                                    ) {
                                        TitleItem(text = stringResource(R.string.filters))
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            viewModel.filterList.forEachIndexed { index, filter ->
                                                FilterItem(
                                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                                    filter = filter,
                                                    onFilterChange = {
                                                        viewModel.updateFilter(
                                                            value = it,
                                                            index = index,
                                                            showError = {
                                                                scope.launch {
                                                                    toastHostState.showError(
                                                                        context = context,
                                                                        error = it
                                                                    )
                                                                }
                                                            }
                                                        )
                                                    },
                                                    onLongPress = {
                                                        showReorderSheet.value = true
                                                    },
                                                    showDragHandle = false,
                                                    onRemove = {
                                                        viewModel.removeFilterAtIndex(
                                                            index
                                                        )
                                                    }
                                                )
                                            }
                                            AddFilterButton(
                                                onClick = {
                                                    showAddFilterSheet.value = true
                                                },
                                                modifier = Modifier.padding(
                                                    horizontal = 16.dp
                                                )
                                            )
                                        }
                                    }
                                }
                            } else {
                                AddFilterButton(
                                    onClick = {
                                        showAddFilterSheet.value = true
                                    },
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        AddFiltersSheet(
            visible = showAddFilterSheet,
            canAddTemplates = false,
            previewBitmap = viewModel.previewBitmap,
            onFilterPicked = { viewModel.addFilter(it.newInstance()) },
            onFilterPickedWithParams = { viewModel.addFilter(it) },
            onRequestFilterMapping = viewModel::filterToTransformation,
            onRequestPreview = viewModel::filter
        )
        FilterReorderSheet(
            filterList = viewModel.filterList,
            visible = showReorderSheet,
            updateOrder = viewModel::updateFiltersOrder
        )

        ExitWithoutSavingDialog(
            onExit = onDismiss,
            onDismiss = { showExitDialog = false },
            visible = showExitDialog
        )
    }
}

@HiltViewModel
private class FilterTemplateCreationSheetViewModel @Inject constructor(
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val favoriteFiltersInteractor: FavoriteFiltersInteractor<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder) {
    private val _filterList: MutableState<List<UiFilter<*>>> = mutableStateOf(emptyList())
    val filterList by _filterList

    private val _templateName: MutableState<String> = mutableStateOf("")
    val templateName by _templateName

    private var bitmapUri: Uri? by mutableStateOf(null)

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap by _previewBitmap

    private val _previewLoading: MutableState<Boolean> = mutableStateOf(false)
    val previewLoading by _previewLoading

    fun updateTemplateName(newName: String) {
        _templateName.update { newName }
    }

    private fun updatePreview() {
        viewModelScope.launch {
            _previewLoading.update { true }
            _previewBitmap.update {
                imageGetter.getImageWithTransformations(
                    data = bitmapUri ?: R.drawable.filter_preview_source,
                    transformations = filterList.map { filterProvider.filterToTransformation(it) },
                    originalSize = false
                )
            }
            _previewLoading.update { false }
        }
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
        showError: (Throwable) -> Unit
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
        updatePreview()
    }

    fun addFilter(filter: UiFilter<*>) {
        _filterList.update {
            it + filter
        }
        updatePreview()
    }

    suspend fun filter(
        bitmap: Bitmap,
        filters: List<UiFilter<*>>,
        size: IntegerSize? = null
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

    fun filterToTransformation(
        uiFilter: UiFilter<*>
    ): Transformation = filterProvider.filterToTransformation(uiFilter).toCoil()

    fun saveTemplate() {
        viewModelScope.launch {
            favoriteFiltersInteractor.addTemplateFilter(
                TemplateFilter(
                    name = templateName,
                    filters = filterList
                )
            )
        }
    }

    fun setUri(selectedUri: Uri?) {
        bitmapUri = selectedUri
        updatePreview()
    }
}