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

package ru.tech.imageresizershrinker.feature.crop.presentation


import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.smarttoolfactory.cropper.model.OutlineType
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormatGroup
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.CropSmall
import ru.tech.imageresizershrinker.core.resources.icons.ImageReset
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.isScrollingUp
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AspectRatioSelector
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CoercePointsToImageBoundsToggle
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CropMaskSelection
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CropType
import ru.tech.imageresizershrinker.feature.crop.presentation.components.Cropper
import ru.tech.imageresizershrinker.feature.crop.presentation.components.FreeCornersCropToggle
import ru.tech.imageresizershrinker.feature.crop.presentation.screenLogic.CropComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropContent(
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    component: CropComponent
) {
    val settingsState = LocalSettingsState.current
    val context = LocalComponentActivity.current
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.bitmap != null) showExitDialog = true
        else onGoBack()
    }

    LaunchedEffect(component.bitmap) {
        component.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(it)
            }
        }
    }

    var coercePointsToImageArea by rememberSaveable {
        mutableStateOf(true)
    }
    val rotationState = rememberSaveable {
        mutableFloatStateOf(0f)
    }
    val imagePicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Single)
    ) { uris ->
        uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
            rotationState.floatValue = 0f
            component.setUri(it) { t ->
                scope.launch {
                    toastHostState.showError(context, t)
                }
            }
        }
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = component.initialUri != null
    )

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(it) { saveResult ->
            context.parseSaveResult(
                saveResult = saveResult,
                onSuccess = showConfetti,
                toastHostState = toastHostState,
                scope = scope
            )
        }
    }


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    val isPortrait by isPortraitOrientationAsState()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            confirmValueChange = {
                when (it) {
                    SheetValue.Hidden -> false
                    else -> true
                }
            }
        )
    )

    val focus = LocalFocusManager.current

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
            focus.clearFocus()
        }
    }

    val controls: @Composable () -> Unit = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(16.dp))
            FreeCornersCropToggle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = component.cropType == CropType.FreeCorners,
                onClick = component::toggleFreeCornersCrop
            )
            BoxAnimatedVisibility(
                visible = component.cropType == CropType.FreeCorners,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CoercePointsToImageBoundsToggle(
                    value = coercePointsToImageArea,
                    onValueChange = { coercePointsToImageArea = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            BoxAnimatedVisibility(
                visible = component.cropType != CropType.FreeCorners,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    AspectRatioSelector(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        selectedAspectRatio = component.selectedAspectRatio,
                        onAspectRatioChange = component::setCropAspectRatio
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CropMaskSelection(
                        onCropMaskChange = component::setCropMask,
                        selectedItem = component.cropProperties.cropOutlineProperty,
                        loadImage = {
                            component.loadImage(it)?.asImageBitmap()
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            ImageFormatSelector(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                entries = if (component.cropProperties.cropOutlineProperty.outlineType == OutlineType.Rect) {
                    ImageFormatGroup.entries
                } else ImageFormatGroup.alphaContainedEntries,
                value = component.imageFormat,
                onValueChange = {
                    component.setImageFormat(it)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    var crop by remember { mutableStateOf(false) }
    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (component.bitmap == null) {
                    EnhancedTopAppBar(
                        type = EnhancedTopAppBarType.Large,
                        scrollBehavior = scrollBehavior,
                        title = {
                            Text(
                                text = stringResource(R.string.crop),
                                modifier = Modifier.marquee()
                            )
                        },
                        navigationIcon = {
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = onBack
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.exit)
                                )
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        }
                    )
                } else {
                    EnhancedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.crop),
                                modifier = Modifier.marquee()
                            )
                        },
                        navigationIcon = {
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = onBack
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.exit)
                                )
                            }
                        },
                        actions = {
                            if (isPortrait) {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        scope.launch {
                                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                                scaffoldState.bottomSheetState.partialExpand()
                                            } else {
                                                scaffoldState.bottomSheetState.expand()
                                            }
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tune,
                                        contentDescription = stringResource(R.string.properties)
                                    )
                                }
                            }
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = {
                                    component.resetBitmap()
                                },
                                enabled = component.bitmap != null && component.isBitmapChanged
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ImageReset,
                                    contentDescription = stringResource(R.string.reset_image)
                                )
                            }
                            var editSheetData by remember {
                                mutableStateOf(listOf<Uri>())
                            }
                            ShareButton(
                                enabled = component.bitmap != null,
                                onShare = {
                                    component.shareBitmap(showConfetti)
                                },
                                onEdit = {
                                    component.cacheCurrentImage { uri ->
                                        editSheetData = listOf(uri)
                                    }
                                },
                                onCopy = { manager ->
                                    component.cacheCurrentImage { uri ->
                                        manager.setClip(uri.asClip(context))
                                        showConfetti()
                                    }
                                }
                            )
                            ProcessImagesPreferenceSheet(
                                uris = editSheetData,
                                visible = editSheetData.isNotEmpty(),
                                onDismiss = {
                                    if (!it) {
                                        editSheetData = emptyList()
                                    }
                                },
                                onNavigate = { screen ->
                                    scope.launch {
                                        editSheetData = emptyList()
                                        delay(200)
                                        onNavigate(screen)
                                    }
                                }
                            )
                        }
                    )
                }

                component.bitmap?.let { bitmap ->
                    if (isPortrait) {
                        Cropper(
                            bitmap = bitmap,
                            crop = crop,
                            onImageCropStarted = component::imageCropStarted,
                            onImageCropFinished = {
                                component.imageCropFinished()
                                if (it != null) {
                                    component.updateBitmap(it)
                                }
                                crop = false
                            },
                            rotationState = rotationState,
                            cropProperties = component.cropProperties,
                            cropType = component.cropType,
                            addVerticalInsets = false,
                            coercePointsToImageArea = coercePointsToImageArea
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .zIndex(-100f)
                                    .container(shape = RectangleShape, resultPadding = 0.dp)
                                    .weight(0.8f)
                            ) {
                                Cropper(
                                    bitmap = bitmap,
                                    crop = crop,
                                    onImageCropStarted = component::imageCropStarted,
                                    onImageCropFinished = {
                                        component.imageCropFinished()
                                        if (it != null) {
                                            component.updateBitmap(it)
                                        }
                                        crop = false
                                    },
                                    rotationState = rotationState,
                                    cropType = component.cropType,
                                    cropProperties = component.cropProperties,
                                    addVerticalInsets = true,
                                    coercePointsToImageArea = coercePointsToImageArea
                                )
                            }

                            Column(
                                Modifier
                                    .weight(0.5f)
                                    .pointerInput(Unit) {
                                        detectTapGestures { focus.clearFocus() }
                                    }
                            ) {
                                controls()
                            }
                            val direction = LocalLayoutDirection.current
                            Column(
                                modifier = Modifier
                                    .container(
                                        shape = RectangleShape
                                    )
                                    .padding(horizontal = 20.dp)
                                    .fillMaxHeight()
                                    .navigationBarsPadding()
                                    .padding(
                                        end = WindowInsets.displayCutout
                                            .asPaddingValues()
                                            .calculateEndPadding(direction)
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                EnhancedFloatingActionButton(
                                    onClick = pickImage,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    content = {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = stringResource(R.string.pick_image_alt)
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                var job by remember { mutableStateOf<Job?>(null) }
                                EnhancedFloatingActionButton(
                                    onClick = {
                                        job?.cancel()
                                        job = scope.launch {
                                            delay(500)
                                            crop = true
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CropSmall,
                                        contentDescription = stringResource(R.string.crop)
                                    )
                                }
                                AnimatedVisibility(component.isBitmapChanged) {
                                    Column {
                                        var showFolderSelectionDialog by rememberSaveable {
                                            mutableStateOf(false)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        EnhancedFloatingActionButton(
                                            onClick = {
                                                saveBitmap(null)
                                            },
                                            onLongClick = {
                                                showFolderSelectionDialog = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Save,
                                                contentDescription = stringResource(R.string.save)
                                            )
                                        }
                                        if (showFolderSelectionDialog) {
                                            OneTimeSaveLocationSelectionDialog(
                                                onDismiss = { showFolderSelectionDialog = false },
                                                onSaveRequest = saveBitmap,
                                                formatForFilenameSelection = component.getFormatForFilenameSelection()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } ?: Column(Modifier.verticalScroll(scrollState)) {
                    ImageNotPickedWidget(
                        onPickImage = pickImage,
                        modifier = Modifier
                            .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                            .navigationBarsPadding()
                    )
                }
            }

            if (component.bitmap == null) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .align(settingsState.fabAlignment)
                ) {
                    EnhancedFloatingActionButton(
                        onClick = pickImage,
                        content = {
                            Spacer(Modifier.width(16.dp))
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = stringResource(R.string.pick_image_alt)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(stringResource(R.string.pick_image_alt))
                            Spacer(Modifier.width(16.dp))
                        }
                    )
                }
            }
        }
    }

    if (isPortrait && component.bitmap != null) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                .calculateBottomPadding(),
            sheetDragHandle = null,
            sheetShape = RectangleShape,
            sheetContent = {
                Column(
                    Modifier
                        .heightIn(max = screenHeight * 0.7f)
                        .pointerInput(Unit) {
                            detectTapGestures { focus.clearFocus() }
                        }
                ) {
                    BottomAppBar(
                        modifier = Modifier.drawHorizontalStroke(true),
                        actions = {
                            var job by remember { mutableStateOf<Job?>(null) }
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onClick = {
                                    job?.cancel()
                                    job = scope.launch {
                                        delay(500)
                                        crop = true
                                    }
                                }
                            ) {
                                Text(stringResource(R.string.crop))
                            }
                        },
                        floatingActionButton = {
                            Row {
                                EnhancedFloatingActionButton(
                                    onClick = pickImage,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    val expanded =
                                        scrollState.isScrollingUp() && component.bitmap == null
                                    val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)
                                    Row(
                                        modifier = Modifier.padding(horizontal = horizontalPadding),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = stringResource(R.string.pick_image_alt)
                                        )
                                        AnimatedVisibility(visible = expanded) {
                                            Row {
                                                Spacer(Modifier.width(8.dp))
                                                Text(stringResource(R.string.pick_image_alt))
                                            }
                                        }
                                    }
                                }
                                AnimatedVisibility(component.isBitmapChanged) {
                                    Row {
                                        var showFolderSelectionDialog by rememberSaveable {
                                            mutableStateOf(false)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        EnhancedFloatingActionButton(
                                            onClick = {
                                                saveBitmap(null)
                                            },
                                            onLongClick = {
                                                showFolderSelectionDialog = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Save,
                                                contentDescription = stringResource(R.string.save)
                                            )
                                        }
                                        if (showFolderSelectionDialog) {
                                            OneTimeSaveLocationSelectionDialog(
                                                onDismiss = { showFolderSelectionDialog = false },
                                                onSaveRequest = saveBitmap,
                                                formatForFilenameSelection = component.getFormatForFilenameSelection()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                    ProvideContainerDefaults(
                        color = SimpleSheetDefaults.contentContainerColor
                    ) {
                        controls()
                    }
                }
            },
            content = content
        )
    } else {
        content(PaddingValues())
    }

    if (component.isSaving || component.isImageLoading) {
        LoadingDialog(
            onCancelLoading = component::cancelSaving,
            canCancel = component.isSaving
        )
    }

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(
        enabled = component.bitmap != null,
        onBack = onBack
    )
}