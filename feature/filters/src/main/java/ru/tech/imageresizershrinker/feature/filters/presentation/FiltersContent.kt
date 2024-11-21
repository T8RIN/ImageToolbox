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

package ru.tech.imageresizershrinker.feature.filters.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.histogram.ImageHistogram
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.widget.AddFilterButton
import ru.tech.imageresizershrinker.core.filters.presentation.widget.FilterItem
import ru.tech.imageresizershrinker.core.filters.presentation.widget.FilterReorderSheet
import ru.tech.imageresizershrinker.core.filters.presentation.widget.addFilters.AddFiltersSheet
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.CompareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShowOriginalButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageCounter
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.detectSwipes
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareSheet
import ru.tech.imageresizershrinker.feature.filters.presentation.components.AddEditMaskSheet
import ru.tech.imageresizershrinker.feature.filters.presentation.components.BasicFilterPreference
import ru.tech.imageresizershrinker.feature.filters.presentation.components.MaskFilterPreference
import ru.tech.imageresizershrinker.feature.filters.presentation.components.MaskItem
import ru.tech.imageresizershrinker.feature.filters.presentation.components.MaskReorderSheet
import ru.tech.imageresizershrinker.feature.filters.presentation.screenLogic.FiltersComponent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersContent(
    component: FiltersComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    AutoContentBasedColors(component.previewBitmap)

    val imagePicker = rememberImagePicker(onSuccess = component::setBasicFilter)

    val pickSingleImageLauncher = rememberImagePicker(onSuccess = component::setMaskFilter)

    var showAddMaskSheet by rememberSaveable { mutableStateOf(false) }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showAddFilterSheet by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else if (component.filterType != null) {
            component.clearType()
        } else component.onGoBack()
    }

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }
    var showCompareSheet by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    var showPickImageFromUrisSheet by rememberSaveable { mutableStateOf(false) }

    var showOriginal by remember { mutableStateOf(false) }

    var showReorderSheet by rememberSaveable { mutableStateOf(false) }
    val actions: @Composable RowScope.() -> Unit = {
        Spacer(modifier = Modifier.width(8.dp))
        if (component.bitmap != null) {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.canSave,
                onShare = {
                    component.performSharing(showConfetti)
                },
                onCopy = { manager ->
                    component.cacheCurrentImage { uri ->
                        manager.setClip(uri.asClip(context))
                        showConfetti()
                    }
                },
                onEdit = {
                    component.cacheImages {
                        editSheetData = it
                    }
                }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = {
                    editSheetData = emptyList()
                },
                onNavigate = component.onNavigate
            )
            ShowOriginalButton(
                canShow = component.canShow(),
                onStateChange = {
                    showOriginal = it
                }
            )
        }
        CompareButton(
            onClick = { showCompareSheet = true },
            visible = component.previewBitmap != null
        )
        if (component.bitmap != null && (component.basicFilterState.filters.size >= 2 || component.maskingFilterState.masks.size >= 2)) {
            EnhancedIconButton(
                onClick = { showReorderSheet = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = stringResource(R.string.properties)
                )
            }
        }
    }

    var showColorPicker by rememberSaveable { mutableStateOf(false) }
    var tempColor by rememberSaveable(
        showColorPicker,
        stateSaver = ColorSaver
    ) { mutableStateOf(Color.Black) }

    var tempSelectionUris by rememberSaveable {
        mutableStateOf<List<Uri>?>(
            null
        )
    }
    var showSelectionFilterPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showSelectionFilterPicker) {
        if (!showSelectionFilterPicker) tempSelectionUris = null
    }

    val selectionFilterPicker = rememberImagePicker { uris: List<Uri> ->
        tempSelectionUris = uris
        if (uris.size > 1) {
            component.setBasicFilter(tempSelectionUris)
        } else {
            showSelectionFilterPicker = true
        }
    }

    AutoFilePicker(
        onAutoPick = selectionFilterPicker::pickImage,
        isPickedAlready = component.initialType != null
    )

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !(component.haveChanges || component.filterType != null),
        onGoBack = onBack,
        title = {
            AnimatedContent(
                targetState = component.filterType?.let {
                    stringResource(it.title)
                }
            ) { title ->
                if (title == null) {
                    val text by remember {
                        derivedStateOf {
                            UiFilter.groupedEntries.flatten().size.toString()
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.marquee()
                    ) {
                        Text(
                            text = stringResource(R.string.filter)
                        )
                        Badge(
                            content = {
                                Text(
                                    text = text
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .padding(bottom = 12.dp)
                                .scaleOnTap {
                                    showConfetti()
                                }
                        )
                    }
                } else {
                    TopAppBarTitle(
                        title = title,
                        input = component.bitmap,
                        isLoading = component.isImageLoading,
                        size = component.imageInfo.sizeInBytes.toLong()
                    )
                }
            }
        },
        topAppBarPersistentActions = {
            if (component.previewBitmap != null) {
                EnhancedIconButton(
                    onClick = {
                        showColorPicker = true
                    },
                    enabled = component.previewBitmap != null
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Colorize,
                        contentDescription = stringResource(R.string.pipette)
                    )
                }
                ZoomButton(
                    onClick = { showZoomSheet = true },
                    visible = component.bitmap != null,
                )
            }
            if (component.bitmap == null) {
                TopAppBarEmoji()
            }
            if (component.bitmap != null && !isPortrait) actions()
            if (component.bitmap != null && isPortrait) {
                when (component.filterType) {
                    is Screen.Filter.Type.Basic -> {
                        EnhancedIconButton(
                            containerColor = MaterialTheme.colorScheme.mixedContainer,
                            onClick = { showAddFilterSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoFixHigh,
                                contentDescription = stringResource(R.string.add_filter)
                            )
                        }
                    }

                    is Screen.Filter.Type.Masking -> {
                        EnhancedIconButton(
                            containerColor = MaterialTheme.colorScheme.mixedContainer,
                            onClick = { showAddMaskSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Texture,
                                contentDescription = stringResource(R.string.add_mask)
                            )
                        }
                    }

                    null -> Unit
                }
            }
        },
        actions = actions,
        showActionsInTopAppBar = false,
        canShowScreenData = component.filterType != null,
        imagePreview = {
            ImageContainer(
                modifier = Modifier
                    .detectSwipes(
                        onSwipeRight = component::selectLeftUri,
                        onSwipeLeft = component::selectRightUri
                    ),
                imageInside = isPortrait,
                showOriginal = showOriginal,
                previewBitmap = component.previewBitmap,
                originalBitmap = component.bitmap,
                isLoading = component.isImageLoading,
                shouldShowPreview = true,
                animatePreviewChange = false
            )
        },
        forceImagePreviewToMax = showOriginal,
        isPortrait = isPortrait,
        controls = {
            val filterType = component.filterType

            val histogramItem = @Composable {
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
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.size(8.dp))
            }

            when (filterType) {
                is Screen.Filter.Type.Basic -> {
                    val filterList = component.basicFilterState.filters
                    if (component.bitmap != null) {
                        ImageCounter(
                            imageCount = component.basicFilterState.uris?.size?.takeIf { it > 1 },
                            onRepick = {
                                showPickImageFromUrisSheet = true
                            }
                        )
                        if (filterList.isNotEmpty()) histogramItem()
                        AnimatedContent(
                            targetState = filterList.isNotEmpty(),
                            transitionSpec = {
                                fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                            }
                        ) { notEmpty ->
                            if (notEmpty) {
                                Column(Modifier.container(MaterialTheme.shapes.extraLarge)) {
                                    TitleItem(text = stringResource(R.string.filters))
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(
                                            8.dp
                                        ),
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        filterList.forEachIndexed { index, filter ->
                                            FilterItem(
                                                backgroundColor = MaterialTheme.colorScheme.surface,
                                                filter = filter,
                                                onFilterChange = { newValue ->
                                                    component.updateFilter(
                                                        value = newValue,
                                                        index = index,
                                                        onFailure = essentials::showFailureToast
                                                    )
                                                },
                                                onLongPress = {
                                                    showReorderSheet = true
                                                },
                                                showDragHandle = false,
                                                onRemove = {
                                                    component.removeFilterAtIndex(index)
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
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.size(8.dp))
                        if (filterList.isEmpty()) histogramItem()
                        SaveExifWidget(
                            imageFormat = component.imageInfo.imageFormat,
                            checked = component.keepExif,
                            onCheckedChange = component::setKeepExif
                        )
                        if (component.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                            Modifier.size(8.dp)
                        )
                        QualitySelector(
                            imageFormat = component.imageInfo.imageFormat,
                            enabled = component.bitmap != null,
                            quality = component.imageInfo.quality,
                            onQualityChange = component::setQuality
                        )
                        Spacer(Modifier.size(8.dp))
                        ImageFormatSelector(
                            value = component.imageInfo.imageFormat,
                            onValueChange = {
                                component.setImageFormat(it)
                            }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                is Screen.Filter.Type.Masking -> {
                    val maskList = component.maskingFilterState.masks
                    if (component.bitmap != null) {
                        if (maskList.isNotEmpty()) histogramItem()
                        AnimatedContent(
                            targetState = maskList.isNotEmpty(),
                            transitionSpec = {
                                fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                            }
                        ) { notEmpty ->
                            if (notEmpty) {
                                Column(Modifier.container(MaterialTheme.shapes.extraLarge)) {
                                    TitleItem(text = stringResource(R.string.masks))
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(
                                            4.dp
                                        ),
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        maskList.forEachIndexed { index, mask ->
                                            MaskItem(
                                                backgroundColor = MaterialTheme.colorScheme.surface,
                                                imageUri = component.maskingFilterState.uri,
                                                previousMasks = maskList.take(index),
                                                mask = mask,
                                                titleText = stringResource(
                                                    R.string.mask_indexed,
                                                    index + 1
                                                ),
                                                onMaskChange = { filterMask ->
                                                    component.updateMask(
                                                        value = filterMask,
                                                        index = index,
                                                        showError = essentials::showFailureToast
                                                    )
                                                },
                                                onLongPress = {
                                                    showReorderSheet = true
                                                },
                                                showDragHandle = false,
                                                onRemove = {
                                                    component.removeMaskAtIndex(index)
                                                },
                                                addMaskSheetComponent = component.addMaskSheetComponent
                                            )
                                        }
                                        EnhancedButton(
                                            containerColor = MaterialTheme.colorScheme.mixedContainer,
                                            onClick = {
                                                showAddMaskSheet = true
                                            },
                                            modifier = Modifier.padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 4.dp
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Texture,
                                                contentDescription = stringResource(R.string.add_mask)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(stringResource(id = R.string.add_mask))
                                        }
                                    }
                                }
                            } else {
                                EnhancedButton(
                                    containerColor = MaterialTheme.colorScheme.mixedContainer,
                                    onClick = {
                                        showAddMaskSheet = true
                                    },
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Texture,
                                        contentDescription = stringResource(R.string.add_mask)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(id = R.string.add_mask))
                                }
                            }
                        }
                        Spacer(Modifier.size(8.dp))
                        if (maskList.isEmpty()) histogramItem()
                        SaveExifWidget(
                            imageFormat = component.imageInfo.imageFormat,
                            checked = component.keepExif,
                            onCheckedChange = component::setKeepExif
                        )
                        if (component.imageInfo.imageFormat.canChangeCompressionValue) Spacer(
                            Modifier.size(8.dp)
                        )
                        QualitySelector(
                            imageFormat = component.imageInfo.imageFormat,
                            enabled = component.bitmap != null,
                            quality = component.imageInfo.quality,
                            onQualityChange = component::setQuality
                        )
                        Spacer(Modifier.size(8.dp))
                        ImageFormatSelector(
                            value = component.imageInfo.imageFormat,
                            onValueChange = {
                                component.setImageFormat(it)
                            }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                else -> null
            }
        },
        buttons = {
            val filterType = component.filterType

            val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
                when (filterType) {
                    is Screen.Filter.Type.Basic -> {
                        component.saveBitmaps(
                            oneTimeSaveLocationUri = it,
                            onResult = essentials::parseSaveResults
                        )
                    }

                    is Screen.Filter.Type.Masking -> {
                        component.saveMaskedBitmap(
                            oneTimeSaveLocationUri = it,
                            onComplete = essentials::parseSaveResult
                        )
                    }

                    else -> Unit
                }
            }
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (component.basicFilterState.uris.isNullOrEmpty() && component.maskingFilterState.uri == null) to isPortrait,
                onSecondaryButtonClick = {
                    when (filterType) {
                        is Screen.Filter.Type.Basic -> imagePicker.pickImage()
                        is Screen.Filter.Type.Masking -> pickSingleImageLauncher.pickImage()
                        null -> selectionFilterPicker.pickImage()
                    }
                },
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                isPrimaryButtonVisible = component.canSave,
                columnarFab = {
                    EnhancedFloatingActionButton(
                        onClick = {
                            when (filterType) {
                                is Screen.Filter.Type.Basic -> showAddFilterSheet = true
                                is Screen.Filter.Type.Masking -> showAddMaskSheet = true
                                null -> Unit
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.mixedContainer
                    ) {
                        when (filterType) {
                            is Screen.Filter.Type.Basic -> {
                                Icon(
                                    imageVector = Icons.Rounded.AutoFixHigh,
                                    contentDescription = null
                                )
                            }

                            is Screen.Filter.Type.Masking -> {
                                Icon(
                                    imageVector = Icons.Rounded.Texture,
                                    contentDescription = null
                                )
                            }

                            null -> Unit
                        }
                    }

                },
                actions = {
                    if (isPortrait) actions()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                showNullDataButtonAsContainer = true
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmaps,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = if (filterType !is Screen.Filter.Type.Masking) {
                    Picker.Multiple
                } else {
                    Picker.Single
                },
                imagePicker = selectionFilterPicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        insetsForNoData = WindowInsets(0),
        noDataControls = {
            val preference1 = @Composable {
                BasicFilterPreference(
                    onClick = imagePicker::pickImage,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            val preference2 = @Composable {
                MaskFilterPreference(
                    onClick = pickSingleImageLauncher::pickImage,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (isPortrait) {
                Column {
                    preference1()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference2()
                }
            } else {
                val direction = LocalLayoutDirection.current
                Row(
                    modifier = Modifier.padding(
                        WindowInsets.displayCutout.asPaddingValues()
                            .let {
                                PaddingValues(
                                    start = it.calculateStartPadding(direction),
                                    end = it.calculateEndPadding(direction)
                                )
                            }
                    )
                ) {
                    preference1.withModifier(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    preference2.withModifier(modifier = Modifier.weight(1f))
                }
            }

            EnhancedModalBottomSheet(
                visible = showSelectionFilterPicker,
                onDismiss = {
                    showSelectionFilterPicker = it
                },
                confirmButton = {
                    EnhancedButton(
                        onClick = {
                            showSelectionFilterPicker = false
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(stringResource(id = R.string.close))
                    }
                },
                sheetContent = {
                    if (tempSelectionUris == null) {
                        showSelectionFilterPicker = false
                    }

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(250.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 12.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalItemSpacing = 12.dp,
                        contentPadding = PaddingValues(12.dp),
                    ) {
                        item {
                            BasicFilterPreference(
                                onClick = {
                                    component.setBasicFilter(tempSelectionUris)
                                    showSelectionFilterPicker = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            MaskFilterPreference(
                                onClick = {
                                    component.setMaskFilter(tempSelectionUris?.firstOrNull())
                                    showSelectionFilterPicker = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                title = {
                    TitleItem(
                        text = stringResource(id = R.string.pick_file),
                        icon = Icons.Rounded.FileOpen
                    )
                }
            )
        },
        contentPadding = animateDpAsState(
            if (component.filterType == null) 12.dp
            else 20.dp
        ).value,
    )

    PickColorFromImageSheet(
        visible = showColorPicker,
        onDismiss = {
            showColorPicker = false
        },
        bitmap = component.previewBitmap,
        onColorChange = { tempColor = it },
        color = tempColor
    )

    ZoomModalSheet(
        data = component.previewBitmap,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        }
    )

    CompareSheet(
        data = component.bitmap to component.previewBitmap,
        visible = showCompareSheet,
        onDismiss = {
            showCompareSheet = false
        }
    )

    if (component.filterType is Screen.Filter.Type.Basic) {
        PickImageFromUrisSheet(
            transformations = listOf(
                component.imageInfoTransformationFactory(
                    imageInfo = component.imageInfo,
                    transformations = component.basicFilterState.filters.map(
                        component.filterProvider::filterToTransformation
                    )
                )
            ),
            visible = showPickImageFromUrisSheet,
            onDismiss = {
                showPickImageFromUrisSheet = false
            },
            uris = component.basicFilterState.uris,
            selectedUri = component.basicFilterState.selectedUri,
            onUriPicked = { uri ->
                component.updateSelectedUri(
                    uri = uri,
                    onFailure = essentials::showFailureToast
                )
            },
            onUriRemoved = { uri ->
                component.updateUrisSilently(removedUri = uri)
            },
            columns = if (isPortrait) 2 else 4,
        )

        AddFiltersSheet(
            visible = showAddFilterSheet,
            onVisibleChange = { showAddFilterSheet = it },
            previewBitmap = component.previewBitmap,
            onFilterPicked = { component.addFilter(it.newInstance()) },
            onFilterPickedWithParams = { component.addFilter(it) },
            component = component.addFiltersSheetComponent,
            filterTemplateCreationSheetComponent = component.filterTemplateCreationSheetComponent
        )

        FilterReorderSheet(
            filterList = component.basicFilterState.filters,
            visible = showReorderSheet,
            onDismiss = {
                showReorderSheet = false
            },
            onReorder = component::updateFiltersOrder
        )
    } else if (component.filterType is Screen.Filter.Type.Masking) {
        AddEditMaskSheet(
            visible = showAddMaskSheet,
            targetBitmapUri = component.maskingFilterState.uri,
            onMaskPicked = component::addMask,
            onDismiss = {
                showAddMaskSheet = false
            },
            masks = component.maskingFilterState.masks,
            component = component.addMaskSheetComponent
        )

        MaskReorderSheet(
            maskList = component.maskingFilterState.masks,
            visible = showReorderSheet,
            onDismiss = {
                showReorderSheet = false
            },
            onReorder = component::updateMasksOrder
        )
    }

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving
    )

    ExitWithoutSavingDialog(
        onExit = {
            if (component.filterType != null) {
                component.clearType()
            } else component.onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}