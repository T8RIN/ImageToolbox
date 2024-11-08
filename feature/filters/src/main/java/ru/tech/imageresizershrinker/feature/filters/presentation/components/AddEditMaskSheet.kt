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
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.exifinterface.media.ExifInterface
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.t8rin.histogram.ImageHistogram
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.pt
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.widget.AddFilterButton
import ru.tech.imageresizershrinker.core.filters.presentation.widget.AddFiltersSheet
import ru.tech.imageresizershrinker.core.filters.presentation.widget.AddFiltersSheetComponent
import ru.tech.imageresizershrinker.core.filters.presentation.widget.FilterItem
import ru.tech.imageresizershrinker.core.filters.presentation.widget.FilterReorderSheet
import ru.tech.imageresizershrinker.core.filters.presentation.widget.FilterTemplateCreationSheetComponent
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EraseModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageHeaderState
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.saver.PtSaver
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BitmapDrawer
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawColorSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawPathModeSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.LineWidthSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.UiPathPaint
import ru.tech.imageresizershrinker.feature.draw.presentation.components.model.UiDrawPathMode
import ru.tech.imageresizershrinker.feature.draw.presentation.components.model.toDomain
import ru.tech.imageresizershrinker.feature.draw.presentation.components.model.toUi
import ru.tech.imageresizershrinker.feature.draw.presentation.components.toUiPathPaint
import ru.tech.imageresizershrinker.feature.filters.domain.FilterMaskApplier

@Composable
fun AddEditMaskSheet(
    component: AddMaskSheetComponent,
    mask: UiFilterMask? = null,
    visible: Boolean,
    onDismiss: () -> Unit,
    targetBitmapUri: Uri? = null,
    masks: List<UiFilterMask> = emptyList(),
    onMaskPicked: (UiFilterMask) -> Unit,
) {
    var invalidations by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(mask, masks, targetBitmapUri, invalidations) {
        component.setMask(
            mask = mask,
            bitmapUri = targetBitmapUri,
            masks = masks
        )
    }

    val isPortrait by isPortraitOrientationAsState()

    var showAddFilterSheet by rememberSaveable { mutableStateOf(false) }

    val context = LocalComponentActivity.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    var showExitDialog by remember { mutableStateOf(false) }

    var showReorderSheet by rememberSaveable { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    var isEraserOn by rememberSaveable { mutableStateOf(false) }
    var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(settingsState.defaultDrawLineWidth.pt) }
    var brushSoftness by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(20.pt) }
    var panEnabled by rememberSaveable { mutableStateOf(false) }
    var drawPathMode by rememberSaveable {
        mutableStateOf<UiDrawPathMode>(UiDrawPathMode.Free)
    }
    val domainDrawPathMode by remember(drawPathMode) {
        derivedStateOf {
            drawPathMode.toDomain()
        }
    }

    val canSave = component.paths.isNotEmpty() && component.filterList.isNotEmpty()
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (component.paths.isEmpty() && component.filterList.isEmpty()) onDismiss()
            else showExitDialog = true
        },
        cancelable = false,
        title = {
            TitleItem(
                text = stringResource(id = R.string.add_mask),
                icon = Icons.Rounded.Texture
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = canSave,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onMaskPicked(component.getUiMask())
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
                    .background(EnhancedBottomSheetDefaults.barContainerColor)
                    .padding(8.dp)
            ) {
                EnhancedIconButton(
                    onClick = {
                        if (component.paths.isEmpty() && component.filterList.isEmpty()) onDismiss()
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
        enableBackHandler = component.paths.isEmpty() && component.filterList.isEmpty()
    ) {
        DisposableEffect(Unit) {
            invalidations++
            onDispose {
                component.resetState()
            }
        }
        var imageState by remember { mutableStateOf(ImageHeaderState(2)) }
        val zoomState = rememberZoomState(maxScale = 30f, key = imageState)

        if (visible) {
            BackHandler(
                enabled = !(component.paths.isEmpty() && component.filterList.isEmpty())
            ) {
                showExitDialog = true
            }
        }
        val switch = @Composable {
            PanModeButton(
                selected = panEnabled,
                onClick = {
                    panEnabled = !panEnabled
                }
            )
        }
        val drawPreview: @Composable () -> Unit = {
            AnimatedContent(
                targetState = Triple(
                    first = remember(component.previewBitmap) {
                        derivedStateOf {
                            component.previewBitmap?.asImageBitmap()
                        }
                    }.value,
                    second = component.maskPreviewModeEnabled,
                    third = component.isImageLoading
                ),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
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
                    )
            ) { (imageBitmap, preview, loading) ->
                if (loading || imageBitmap == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                } else {
                    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                    var drawing by remember { mutableStateOf(false) }
                    BitmapDrawer(
                        zoomState = zoomState,
                        imageBitmap = imageBitmap,
                        paths = if (!preview || drawing || component.isImageLoading) component.paths else emptyList(),
                        strokeWidth = strokeWidth,
                        brushSoftness = brushSoftness,
                        drawColor = component.maskColor,
                        onAddPath = component::addPath,
                        isEraserOn = isEraserOn,
                        drawMode = DrawMode.Pen,
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(aspectRatio, isPortrait)
                            .fillMaxSize(),
                        panEnabled = panEnabled,
                        onDrawStart = {
                            drawing = true
                        },
                        onDrawFinish = {
                            drawing = false
                        },
                        onRequestFiltering = component::filter,
                        drawPathMode = domainDrawPathMode,
                        backgroundColor = Color.Transparent
                    )
                }
            }
        }
        Row {
            val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
            if (!isPortrait) {
                Box(modifier = Modifier.weight(1.3f)) {
                    drawPreview()
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
                    imageBlock = drawPreview
                )
                item {
                    Row(
                        Modifier
                            .then(
                                if (imageState.isBlocked && isPortrait) {
                                    Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )
                                } else Modifier.padding(16.dp)
                            )
                            .container(shape = CircleShape)
                    ) {
                        switch()
                        Spacer(Modifier.width(4.dp))
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            borderColor = MaterialTheme.colorScheme.outlineVariant(
                                luminance = 0.1f
                            ),
                            onClick = component::undo,
                            enabled = (component.lastPaths.isNotEmpty() || component.paths.isNotEmpty())
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Undo,
                                contentDescription = "Undo"
                            )
                        }
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            borderColor = MaterialTheme.colorScheme.outlineVariant(
                                luminance = 0.1f
                            ),
                            onClick = component::redo,
                            enabled = component.undonePaths.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Redo,
                                contentDescription = "Redo"
                            )
                        }
                        EraseModeButton(
                            selected = isEraserOn,
                            enabled = !panEnabled,
                            onClick = {
                                isEraserOn = !isEraserOn
                            }
                        )
                    }

                    AnimatedVisibility(visible = canSave) {
                        Column {
                            BoxAnimatedVisibility(component.maskPreviewModeEnabled) {
                                PreferenceItemOverload(
                                    title = stringResource(R.string.histogram),
                                    subtitle = stringResource(R.string.histogram_sub),
                                    endIcon = {
                                        AnimatedContent(component.previewBitmap != null) {
                                            if (it) {
                                                ImageHistogram(
                                                    image = component.previewBitmap,
                                                    modifier = Modifier
                                                        .width(100.dp)
                                                        .height(65.dp),
                                                    bordersColor = Color.White
                                                )
                                            } else {
                                                Box(modifier = Modifier.size(56.dp)) {
                                                    LoadingIndicator()
                                                }
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 8.dp)
                                )
                            }
                            PreferenceRowSwitch(
                                title = stringResource(id = R.string.mask_preview),
                                subtitle = stringResource(id = R.string.mask_preview_sub),
                                color = animateColorAsState(
                                    if (component.maskPreviewModeEnabled) MaterialTheme.colorScheme.onPrimary
                                    else Color.Unspecified,
                                ).value,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(24.dp),
                                contentColor = animateColorAsState(
                                    if (component.maskPreviewModeEnabled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                ).value,
                                onClick = component::togglePreviewMode,
                                checked = component.maskPreviewModeEnabled,
                                startIcon = Icons.Rounded.Preview
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DrawColorSelector(
                            color = Color.Unspecified,
                            titleText = stringResource(id = R.string.mask_color),
                            defaultColors = remember {
                                listOf(
                                    Color.Red,
                                    Color.Green,
                                    Color.Blue,
                                    Color.Yellow,
                                    Color.Cyan,
                                    Color.Magenta
                                )
                            },
                            value = component.maskColor,
                            onValueChange = component::updateMaskColor,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp
                            )
                        )
                        DrawPathModeSelector(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp
                            ),
                            values = remember {
                                listOf(
                                    DrawPathMode.Free,
                                    DrawPathMode.Lasso,
                                    DrawPathMode.Rect(),
                                    DrawPathMode.Oval,
                                    DrawPathMode.Triangle,
                                    DrawPathMode.Polygon(),
                                    DrawPathMode.Star()
                                )
                            },
                            value = domainDrawPathMode,
                            onValueChange = { drawPathMode = it.toUi() }
                        )
                        LineWidthSelector(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp
                            ),
                            color = Color.Unspecified,
                            value = strokeWidth.value,
                            onValueChange = { strokeWidth = it.pt }
                        )
                        BrushSoftnessSelector(
                            modifier = Modifier
                                .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                            color = Color.Unspecified,
                            value = brushSoftness.value,
                            onValueChange = { brushSoftness = it.pt }
                        )
                    }

                    PreferenceRowSwitch(
                        title = stringResource(id = R.string.inverse_fill_type),
                        subtitle = stringResource(id = R.string.inverse_fill_type_sub),
                        checked = component.isInverseFillType,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp
                        ),
                        color = Color.Unspecified,
                        resultModifier = Modifier.padding(16.dp),
                        applyHorizontalPadding = false,
                        shape = RoundedCornerShape(24.dp),
                        onClick = {
                            component.toggleIsInverseFillType()
                        }
                    )
                    AnimatedContent(
                        targetState = component.filterList.isNotEmpty(),
                        transitionSpec = {
                            fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                        }
                    ) { notEmpty ->
                        if (notEmpty) {
                            Column(
                                Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .container(MaterialTheme.shapes.extraLarge)
                            ) {
                                TitleItem(text = stringResource(R.string.filters))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    component.filterList.forEachIndexed { index, filter ->
                                        FilterItem(
                                            backgroundColor = MaterialTheme.colorScheme.surface,
                                            filter = filter,
                                            onFilterChange = {
                                                component.updateFilter(
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
                                                showReorderSheet = true
                                            },
                                            showDragHandle = false,
                                            onRemove = {
                                                component.removeFilterAtIndex(
                                                    index
                                                )
                                            }
                                        )
                                    }
                                    AddFilterButton(
                                        onClick = {
                                            showAddFilterSheet = true
                                        },
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp
                                        )
                                    )
                                }
                            }
                        } else {
                            AddFilterButton(
                                onClick = {
                                    showAddFilterSheet = true
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    AddFiltersSheet(
        visible = showAddFilterSheet,
        onVisibleChange = { showAddFilterSheet = it },
        previewBitmap = null,
        onFilterPicked = { component.addFilter(it.newInstance()) },
        onFilterPickedWithParams = { component.addFilter(it) },
        component = component.addFiltersSheetComponent,
        filterTemplateCreationSheetComponent = component.filterTemplateCreationSheetComponent
    )
    FilterReorderSheet(
        filterList = component.filterList,
        visible = showReorderSheet,
        onDismiss = {
            showReorderSheet = false
        },
        onReorder = component::updateFiltersOrder
    )

    ExitWithoutSavingDialog(
        onExit = onDismiss,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}

class AddMaskSheetComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
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

    private var bitmapUri: Uri? by mutableStateOf(null)

    private var initialMasks: List<UiFilterMask> by mutableStateOf(emptyList())

    private var initialMask: UiFilterMask? by mutableStateOf(null)

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