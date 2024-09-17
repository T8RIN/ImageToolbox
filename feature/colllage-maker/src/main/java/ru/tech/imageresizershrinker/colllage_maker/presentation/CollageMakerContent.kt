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

package ru.tech.imageresizershrinker.colllage_maker.presentation

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.rounded.FormatLineSpacing
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.collages.Collage
import com.t8rin.collages.CollageTypeSelection
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.colllage_maker.presentation.viewModel.CollageMakerViewModel
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormatGroup
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.BackgroundColorSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.LockScreenOrientation
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle

@Composable
fun CollageMakerContent(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    viewModel: CollageMakerViewModel = hiltViewModel()
) {
    LockScreenOrientation()
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let {
            if (it.size in 2..10) {
                viewModel.updateUris(it)
            } else {
                scope.launch {
                    toastHostState.showToast(
                        message = if (it.size > 10) context.getString(R.string.pick_up_to_ten_images)
                        else context.getString(R.string.pick_at_least_two_images),
                        icon = Icons.Outlined.AutoAwesomeMosaic
                    )
                }
            }
        }
    }

    val pickImageLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let {
            if (list.size in 2..10) {
                viewModel.updateUris(list)
            } else {
                scope.launch {
                    toastHostState.showToast(
                        message = if (list.size > 10) context.getString(R.string.pick_up_to_ten_images)
                        else context.getString(R.string.pick_at_least_two_images),
                        icon = Icons.Outlined.AutoAwesomeMosaic
                    )
                }
            }
        }
    }

    val pickImage = pickImageLauncher::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !uriState.isNullOrEmpty()
    )

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        viewModel.saveBitmap(it) { saveResult ->
            context.parseSaveResult(
                scope = scope,
                saveResult = saveResult,
                toastHostState = toastHostState,
                onSuccess = showConfetti
            )
        }
    }

    val isPortrait by isPortraitOrientationAsState()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.haveChanges) showExitDialog = true
        else onGoBack()
    }

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

    val collagePreview: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .container(
                    shape = RoundedCornerShape(4.dp),
                    resultPadding = 0.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Collage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .transparencyChecker(),
                images = viewModel.uris ?: emptyList(),
                collageType = viewModel.collageType,
                collageCreationTrigger = viewModel.collageCreationTrigger,
                onCollageCreated = viewModel::updateCollageBitmap,
                backgroundColor = viewModel.backgroundColor,
                spacing = viewModel.spacing,
                cornerRadius = viewModel.cornerRadius
            )
        }
    }

    val controls: @Composable () -> Unit = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.container(
                resultPadding = 0.dp,
                shape = RoundedCornerShape(24.dp)
            )
        ) {
            Text(
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.collage_type),
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp
            )
            val state = rememberLazyListState()
            CollageTypeSelection(
                state = state,
                imagesCount = viewModel.uris?.size ?: 0,
                value = viewModel.collageType,
                onValueChange = viewModel::setCollageType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .fadingEdges(state),
                contentPadding = PaddingValues(10.dp),
                shape = RoundedCornerShape(12.dp),
                itemModifierFactory = { isSelected ->
                    Modifier
                        .container(
                            resultPadding = 0.dp,
                            color = animateColorAsState(
                                targetValue = if (isSelected) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else MaterialTheme.colorScheme.surfaceContainerLowest,
                            ).value,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                }
            )
        }
        Spacer(Modifier.height(8.dp))
        BackgroundColorSelector(
            modifier = Modifier
                .fillMaxWidth()
                .container(
                    shape = RoundedCornerShape(24.dp),
                    resultPadding = 0.dp
                ),
            value = viewModel.backgroundColor,
            onValueChange = viewModel::setBackgroundColor
        )
        Spacer(Modifier.height(8.dp))
        EnhancedSliderItem(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.spacing,
            title = stringResource(R.string.spacing),
            valueRange = 0f..50f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = viewModel::setSpacing,
            sliderModifier = Modifier
                .padding(
                    top = 14.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                ),
            icon = Icons.Rounded.FormatLineSpacing,
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(Modifier.height(8.dp))
        EnhancedSliderItem(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.cornerRadius,
            title = stringResource(R.string.corners),
            valueRange = 0f..50f,
            internalStateTransformation = {
                it.roundToTwoDigits()
            },
            onValueChange = viewModel::setCornerRadius,
            sliderModifier = Modifier
                .padding(
                    top = 14.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                ),
            icon = Icons.Rounded.RoundedCorner,
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(Modifier.height(8.dp))
        QualitySelector(
            imageFormat = viewModel.imageFormat,
            quality = viewModel.quality,
            onQualityChange = viewModel::setQuality
        )
        Spacer(Modifier.height(8.dp))
        ImageFormatSelector(
            value = viewModel.imageFormat,
            onValueChange = viewModel::setImageFormat,
            entries = if (viewModel.backgroundColor.alpha != 1f) {
                ImageFormatGroup.alphaContainedEntries
            } else ImageFormatGroup.entries,
            forceEnabled = true
        )
    }

    val actions: @Composable RowScope.() -> Unit = {
        var editSheetData by remember {
            mutableStateOf(listOf<Uri>())
        }
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
        ShareButton(
            onShare = {
                viewModel.performSharing(showConfetti)
            },
            onCopy = { manager ->
                viewModel.cacheImage { uri ->
                    manager.setClip(uri.asClip(context))
                    showConfetti()
                }
            },
            onEdit = {
                viewModel.cacheImage {
                    editSheetData = listOf(it)
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

    val buttons: @Composable () -> Unit = {
        var showFolderSelectionDialog by rememberSaveable {
            mutableStateOf(false)
        }
        BottomButtonsBlock(
            targetState = (viewModel.uris.isNullOrEmpty()) to isPortrait,
            onSecondaryButtonClick = pickImage,
            onPrimaryButtonClick = {
                saveBitmaps(null)
            },
            onPrimaryButtonLongClick = {
                showFolderSelectionDialog = true
            },
            actions = {
                if (isPortrait) actions()
            }
        )
        if (showFolderSelectionDialog) {
            OneTimeSaveLocationSelectionDialog(
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmaps,
                formatForFilenameSelection = viewModel.getFormatForFilenameSelection()
            )
        }
    }

    val noDataControls: @Composable () -> Unit = {
        if (!viewModel.isImageLoading) {
            ImageNotPickedWidget(onPickImage = pickImage)
        }
    }

    val topAppBar: @Composable () -> Unit = {
        EnhancedTopAppBar(
            title = {
                TopAppBarTitle(
                    title = stringResource(R.string.collage_maker),
                    input = viewModel.uris,
                    isLoading = viewModel.isImageLoading,
                    size = null
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
            type = if (viewModel.uris.isNullOrEmpty()) EnhancedTopAppBarType.Large
            else EnhancedTopAppBarType.Normal
        )
    }

    AnimatedContent(viewModel.uris.isNullOrEmpty()) { noData ->
        if (noData) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    topAppBar()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        noDataControls()
                    }
                }
                val settingsState = LocalSettingsState.current
                if (isPortrait) {
                    Box(
                        modifier = Modifier.align(settingsState.fabAlignment)
                    ) {
                        buttons()
                    }
                }
            }
        } else {
            BottomSheetScaffold(
                sheetContent = {
                    Column(
                        Modifier
                            .fillMaxHeight(0.6f)
                            .pointerInput(Unit) {
                                detectTapGestures { focus.clearFocus() }
                            }
                    ) {
                        buttons()
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                                .navigationBarsPadding()
                        ) {
                            ProvideContainerDefaults(
                                color = SimpleSheetDefaults.contentContainerColor
                            ) {
                                controls()
                            }
                        }
                    }
                },
                sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding(),
                sheetDragHandle = null,
                sheetShape = RectangleShape,
                scaffoldState = scaffoldState
            ) {
                Column(modifier = Modifier.padding(it)) {
                    topAppBar()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        collagePreview()
                    }
                }
            }
        }
    }

    BackHandler(onBack = onBack)



    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    if (viewModel.isSaving || viewModel.isImageLoading) {
        LoadingDialog(
            onCancelLoading = viewModel::cancelSaving
        )
    }
}