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

package ru.tech.imageresizershrinker.feature.erase_background.presentation

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.observeAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormatGroup
import ru.tech.imageresizershrinker.core.domain.model.pt
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.HelperGridParamsSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.saver.PtSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawPathModeSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.LineWidthSelector
import ru.tech.imageresizershrinker.feature.erase_background.presentation.components.AutoEraseBackgroundCard
import ru.tech.imageresizershrinker.feature.erase_background.presentation.components.BitmapEraser
import ru.tech.imageresizershrinker.feature.erase_background.presentation.components.OriginalImagePreviewAlphaSelector
import ru.tech.imageresizershrinker.feature.erase_background.presentation.components.RecoverModeButton
import ru.tech.imageresizershrinker.feature.erase_background.presentation.components.RecoverModeCard
import ru.tech.imageresizershrinker.feature.erase_background.presentation.components.TrimImageToggle
import ru.tech.imageresizershrinker.feature.erase_background.presentation.screenLogic.EraseBackgroundComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EraseBackgroundContent(
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    component: EraseBackgroundComponent,
) {
    val settingsState = LocalSettingsState.current
    val context = LocalComponentActivity.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope
    val showConfetti: () -> Unit = essentials::showConfetti

    LaunchedEffect(component.bitmap) {
        component.bitmap?.let {
            if (allowChangeColor) {
                themeState.updateColorByImage(it)
            }
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }


    val imagePicker =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let { uri ->
                component.setUri(
                    uri = uri,
                    onError = essentials::showErrorToast
                )
            }
        }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = component.initialUri != null
    )

    val scaffoldState = rememberBottomSheetScaffoldState()

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState
    )

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else onGoBack()
    }

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    var strokeWidth by rememberSaveable(stateSaver = PtSaver) {
        mutableStateOf(
            settingsState.defaultDrawLineWidth.pt
        )
    }
    var brushSoftness by rememberSaveable(stateSaver = PtSaver) {
        mutableStateOf(
            0.pt
        )
    }

    val drawPathMode = component.drawPathMode

    var originalImagePreviewAlpha by rememberSaveable {
        mutableFloatStateOf(0.2f)
    }

    val configuration = LocalConfiguration.current
    val sizeClass = LocalWindowSizeClass.current.widthSizeClass
    val portrait =
        remember(
            LocalLifecycleOwner.current.lifecycle.observeAsState().value,
            sizeClass,
            configuration
        ) {
            derivedStateOf {
                configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || sizeClass == WindowWidthSizeClass.Compact
            }
        }.value

    var panEnabled by rememberSaveable { mutableStateOf(false) }

    val secondaryControls = @Composable {
        PanModeButton(
            selected = panEnabled,
            onClick = {
                panEnabled = !panEnabled
            }
        )
        EnhancedIconButton(
            containerColor = Color.Transparent,
            borderColor = MaterialTheme.colorScheme.outlineVariant(
                luminance = 0.1f
            ),
            onClick = { component.undo() },
            enabled = component.lastPaths.isNotEmpty() || component.paths.isNotEmpty()
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
            onClick = { component.redo() },
            enabled = component.undonePaths.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Redo,
                contentDescription = "Redo"
            )
        }
    }

    val image: @Composable () -> Unit = @Composable {
        AnimatedContent(
            targetState = remember(component.bitmap) {
                derivedStateOf {
                    component.bitmap?.copy(
                        Bitmap.Config.ARGB_8888,
                        true
                    )?.asImageBitmap() ?: ImageBitmap(
                        configuration.screenWidthDp,
                        configuration.screenHeightDp
                    )
                }
            }.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { imageBitmap ->
            val direction = LocalLayoutDirection.current
            val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
            BitmapEraser(
                imageBitmapForShader = component.internalBitmap?.asImageBitmap(),
                imageBitmap = imageBitmap,
                paths = component.paths,
                strokeWidth = strokeWidth,
                brushSoftness = brushSoftness,
                onAddPath = component::addPath,
                isRecoveryOn = component.isRecoveryOn,
                modifier = Modifier
                    .padding(
                        start = WindowInsets
                            .displayCutout
                            .asPaddingValues()
                            .calculateStartPadding(direction)
                    )
                    .padding(16.dp)
                    .aspectRatio(aspectRatio, portrait)
                    .fillMaxSize(),
                panEnabled = panEnabled,
                originalImagePreviewAlpha = originalImagePreviewAlpha,
                drawPathMode = drawPathMode,
                helperGridParams = component.helperGridParams
            )
        }
    }

    val topAppBar = @Composable {
        AnimatedContent(
            targetState = component.bitmap == null,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(false)
            }
        ) { noBitmap ->
            if (noBitmap) {
                EnhancedTopAppBar(
                    type = EnhancedTopAppBarType.Large,
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = stringResource(R.string.background_remover),
                            modifier = Modifier.marquee()
                        )
                    },
                    navigationIcon = {
                        EnhancedIconButton(
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
                            text = stringResource(R.string.background_remover),
                            modifier = Modifier.marquee()
                        )
                    },
                    actions = {
                        if (portrait) {
                            EnhancedIconButton(
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
                        var editSheetData by remember {
                            mutableStateOf(listOf<Uri>())
                        }
                        ShareButton(
                            enabled = component.bitmap != null,
                            onShare = {
                                component.shareBitmap(showConfetti)
                            },
                            onCopy = { manager ->
                                component.cacheCurrentImage { uri ->
                                    manager.setClip(uri.asClip(context))
                                    showConfetti()
                                }
                            },
                            onEdit = {
                                component.cacheCurrentImage { uri ->
                                    editSheetData = listOf(uri)
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
                        EnhancedIconButton(
                            onClick = { component.clearDrawing() },
                            enabled = component.paths.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    },
                    navigationIcon = {
                        EnhancedIconButton(
                            onClick = onBack
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.exit)
                            )
                        }
                    }
                )
            }
        }
    }

    val controls = @Composable {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!portrait) {
                Row(
                    Modifier
                        .padding(top = 8.dp)
                        .container(CircleShape)
                ) {
                    secondaryControls()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            RecoverModeCard(
                selected = component.isRecoveryOn,
                enabled = !panEnabled,
                onClick = component::toggleEraser
            )
            AutoEraseBackgroundCard(
                onClick = {
                    scope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                        component.autoEraseBackground(
                            onSuccess = showConfetti,
                            onFailure = essentials::showErrorToast
                        )
                    }
                },
                onReset = component::resetImage
            )
            OriginalImagePreviewAlphaSelector(
                value = originalImagePreviewAlpha,
                onValueChange = {
                    originalImagePreviewAlpha = it
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            DrawPathModeSelector(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp
                ),
                value = drawPathMode,
                onValueChange = component::updateDrawPathMode,
                values = remember {
                    listOf(
                        DrawPathMode.Free,
                        DrawPathMode.Line,
                        DrawPathMode.Lasso,
                        DrawPathMode.Rect(),
                        DrawPathMode.Oval
                    )
                }
            )
            BoxAnimatedVisibility(drawPathMode.isStroke) {
                LineWidthSelector(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    value = strokeWidth.value,
                    onValueChange = { strokeWidth = it.pt }
                )
            }
            BrushSoftnessSelector(
                modifier = Modifier
                    .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                value = brushSoftness.value,
                onValueChange = { brushSoftness = it.pt }
            )
            TrimImageToggle(
                checked = component.trimImage,
                onCheckedChange = { component.setTrimImage(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            HelperGridParamsSelector(
                value = component.helperGridParams,
                onValueChange = component::updateHelperGridParams,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            val settingsInteractor = LocalSimpleSettingsInteractor.current
            PreferenceRowSwitch(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp
                    ),
                shape = RoundedCornerShape(24.dp),
                title = stringResource(R.string.magnifier),
                subtitle = stringResource(R.string.magnifier_sub),
                checked = settingsState.magnifierEnabled,
                onClick = {
                    scope.launch {
                        settingsInteractor.toggleMagnifierEnabled()
                    }
                },
                startIcon = Icons.Outlined.ZoomIn
            )
            SaveExifWidget(
                imageFormat = component.imageFormat,
                checked = component.saveExif,
                onCheckedChange = component::setSaveExif,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            ImageFormatSelector(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    .navigationBarsPadding(),
                entries = ImageFormatGroup.alphaContainedEntries,
                value = component.imageFormat,
                onValueChange = {
                    component.setImageFormat(it)
                }
            )
        }
    }

    AnimatedContent(
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        targetState = component.bitmap == null
    ) { noBitmap ->
        if (noBitmap) {
            Box(Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    topAppBar()
                    Column(
                        Modifier.verticalScroll(rememberScrollState())
                    ) {
                        ImageNotPickedWidget(
                            onPickImage = pickImage,
                            modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(108.dp))
                    }
                }
                EnhancedFloatingActionButton(
                    onClick = pickImage,
                    modifier = Modifier
                        .align(settingsState.fabAlignment)
                        .navigationBarsPadding()
                        .padding(16.dp),
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
        } else {
            if (portrait) {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding(),
                    sheetDragHandle = null,
                    sheetShape = RectangleShape,
                    sheetContent = {
                        Column(Modifier.fillMaxHeight(0.8f)) {
                            BottomAppBar(
                                modifier = Modifier.drawHorizontalStroke(true),
                                actions = {
                                    secondaryControls()
                                    RecoverModeButton(
                                        selected = component.isRecoveryOn,
                                        enabled = !panEnabled,
                                        onClick = component::toggleEraser
                                    )
                                },
                                floatingActionButton = {
                                    Row {
                                        var showFolderSelectionDialog by rememberSaveable {
                                            mutableStateOf(false)
                                        }
                                        EnhancedFloatingActionButton(
                                            onClick = pickImage,
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                                contentDescription = stringResource(R.string.pick_image_alt)
                                            )
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
                                        OneTimeSaveLocationSelectionDialog(
                                            visible = showFolderSelectionDialog,
                                            onDismiss = { showFolderSelectionDialog = false },
                                            onSaveRequest = saveBitmap,
                                            formatForFilenameSelection = component.getFormatForFilenameSelection()
                                        )
                                    }
                                }
                            )
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                ProvideContainerDefaults(
                                    color = EnhancedBottomSheetDefaults.contentContainerColor
                                ) {
                                    controls()
                                }
                            }
                        }
                    },
                    content = {
                        Column(
                            Modifier.padding(it),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            topAppBar()
                            image()
                        }
                    }
                )
            } else {
                Column {
                    topAppBar()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            Modifier
                                .container(
                                    shape = RectangleShape,
                                    resultPadding = 0.dp
                                )
                                .weight(1.2f)
                                .clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            image()
                        }
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(
                                bottom = WindowInsets
                                    .navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding() + WindowInsets.ime
                                    .asPaddingValues()
                                    .calculateBottomPadding(),
                            ),
                            modifier = Modifier
                                .weight(0.7f)
                                .clipToBounds()
                        ) {
                            item {
                                controls()
                            }
                        }
                        val direction = LocalLayoutDirection.current
                        Column(
                            Modifier
                                .container(RectangleShape)
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
                            var showFolderSelectionDialog by rememberSaveable {
                                mutableStateOf(false)
                            }
                            EnhancedFloatingActionButton(
                                onClick = pickImage,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddPhotoAlternate,
                                    contentDescription = stringResource(R.string.pick_image_alt)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
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
                            OneTimeSaveLocationSelectionDialog(
                                visible = showFolderSelectionDialog,
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


    LoadingDialog(
        visible = component.isSaving || component.isImageLoading || component.isErasingBG,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving
    )

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(
        enabled = component.haveChanges,
        onBack = onBack
    )

    DrawLockScreenOrientation()
}