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

package ru.tech.imageresizershrinker.feature.draw.presentation


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.coerceIn
import ru.tech.imageresizershrinker.core.domain.model.pt
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageTooltip
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.settings.presentation.provider.rememberAppColorTuple
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EraseModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ColorRowSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.HelperGridParamsSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.saver.PtSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.draw.domain.DrawBehavior
import ru.tech.imageresizershrinker.feature.draw.domain.DrawLineStyle
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BitmapDrawer
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawColorSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawLineStyleSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawModeSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawPathModeSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.LineWidthSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.OpenColorPickerCard
import ru.tech.imageresizershrinker.feature.draw.presentation.screenLogic.DrawComponent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@Composable
fun DrawContent(
    component: DrawComponent,
) {
    val settingsState = LocalSettingsState.current
    val context = LocalComponentActivity.current

    val themeState = LocalDynamicThemeState.current

    val appColorTuple = rememberAppColorTuple()

    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.drawBehavior !is DrawBehavior.None && component.haveChanges) {
            showExitDialog = true
        } else if (component.drawBehavior !is DrawBehavior.None) {
            component.resetDrawBehavior()
            themeState.updateColorTuple(appColorTuple)
        } else {
            component.onGoBack()
        }
    }

    AutoContentBasedColors(component.bitmap)

    val imagePicker = rememberImagePicker { uri: Uri ->
        component.setUri(
            uri = uri,
            onFailure = essentials::showFailureToast
        )
    }

    val pickImage = imagePicker::pickImage

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    val configuration = LocalConfiguration.current
    val isPortrait by isPortraitOrientationAsState()

    var showPickColorSheet by rememberSaveable { mutableStateOf(false) }

    var panEnabled by rememberSaveable(component.drawBehavior) { mutableStateOf(false) }

    var strokeWidth by rememberSaveable(
        component.drawBehavior,
        stateSaver = PtSaver
    ) { mutableStateOf(settingsState.defaultDrawLineWidth.pt) }

    var drawColor by rememberSaveable(
        component.drawBehavior,
        stateSaver = ColorSaver
    ) { mutableStateOf(settingsState.defaultDrawColor) }

    var isEraserOn by rememberSaveable(component.drawBehavior) { mutableStateOf(false) }

    val drawMode = component.drawMode

    var alpha by rememberSaveable(component.drawBehavior, drawMode) {
        mutableFloatStateOf(if (drawMode is DrawMode.Highlighter) 0.4f else 1f)
    }

    var brushSoftness by rememberSaveable(component.drawBehavior, drawMode, stateSaver = PtSaver) {
        mutableStateOf(if (drawMode is DrawMode.Neon) 35.pt else 0.pt)
    }

    val drawPathMode = component.drawPathMode

    val drawLineStyle = component.drawLineStyle

    LaunchedEffect(drawMode, strokeWidth) {
        strokeWidth = if (drawMode is DrawMode.Image) {
            strokeWidth.coerceIn(10.pt, 120.pt)
        } else {
            strokeWidth.coerceIn(1.pt, 100.pt)
        }
    }

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
            onClick = component::undo,
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

    val bitmap = component.bitmap ?: (component.drawBehavior as? DrawBehavior.Background)?.run {
        remember { ImageBitmap(width, height).asAndroidBitmap() }
    } ?: remember {
        ImageBitmap(
            configuration.screenWidthDp,
            configuration.screenHeightDp
        ).asAndroidBitmap()
    }

    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    AdaptiveBottomScaffoldLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.draw),
                input = component.drawBehavior.takeIf { it !is DrawBehavior.None },
                isLoading = component.isImageLoading,
                size = null,
                originalSize = null
            )
        },
        onGoBack = onBack,
        isPortrait = isPortrait,
        shouldDisableBackHandler = component.drawBehavior is DrawBehavior.None,
        actions = {
            secondaryControls()
        },
        topAppBarPersistentActions = { scaffoldState ->
            if (component.drawBehavior == DrawBehavior.None) TopAppBarEmoji()
            else {
                if (isPortrait) {
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
                    enabled = component.drawBehavior !is DrawBehavior.None,
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
                        editSheetData = emptyList()
                    },
                    onNavigate = component.onNavigate
                )
                EnhancedIconButton(
                    onClick = component::clearDrawing,
                    enabled = component.drawBehavior !is DrawBehavior.None && component.havePaths
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
        },
        mainContent = {
            AnimatedContent(
                targetState = remember(bitmap) {
                    derivedStateOf {
                        bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                    }
                }.value,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { imageBitmap ->
                val direction = LocalLayoutDirection.current
                val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                BitmapDrawer(
                    imageBitmap = imageBitmap,
                    paths = component.paths,
                    strokeWidth = strokeWidth,
                    brushSoftness = brushSoftness,
                    drawColor = drawColor.copy(alpha),
                    onAddPath = component::addPath,
                    isEraserOn = isEraserOn,
                    drawMode = drawMode,
                    modifier = Modifier
                        .padding(
                            start = WindowInsets
                                .displayCutout
                                .asPaddingValues()
                                .calculateStartPadding(direction)
                        )
                        .padding(16.dp)
                        .aspectRatio(aspectRatio, isPortrait)
                        .fillMaxSize(),
                    panEnabled = panEnabled,
                    onDraw = {},
                    onRequestFiltering = component::filter,
                    drawPathMode = drawPathMode,
                    backgroundColor = component.backgroundColor,
                    drawLineStyle = drawLineStyle,
                    helperGridParams = component.helperGridParams
                )
            }
        },
        controls = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isPortrait) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .container(shape = CircleShape)
                    ) {
                        secondaryControls()
                    }
                }
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.SpotHeal,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OpenColorPickerCard(
                        modifier = Modifier.fillMaxWidth(),
                        onOpen = {
                            component.openColorPicker()
                            showPickColorSheet = true
                        }
                    )
                }
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.Image && drawMode !is DrawMode.SpotHeal,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    DrawColorSelector(
                        modifier = Modifier.fillMaxWidth(),
                        value = drawColor,
                        onValueChange = { drawColor = it }
                    )
                }
                AnimatedVisibility(
                    visible = drawPathMode.isStroke,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    LineWidthSelector(
                        modifier = Modifier.fillMaxWidth(),
                        title = if (drawMode is DrawMode.Text) {
                            stringResource(R.string.font_size)
                        } else stringResource(R.string.line_width),
                        valueRange = if (drawMode is DrawMode.Image) {
                            10f..120f
                        } else 1f..100f,
                        value = strokeWidth.value,
                        onValueChange = { strokeWidth = it.pt }
                    )
                }
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.Highlighter && drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.SpotHeal,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    BrushSoftnessSelector(
                        modifier = Modifier.fillMaxWidth(),
                        value = brushSoftness.value,
                        onValueChange = { brushSoftness = it.pt }
                    )
                }
                if (component.drawBehavior is DrawBehavior.Background) {
                    ColorRowSelector(
                        value = component.backgroundColor,
                        onValueChange = component::updateBackgroundColor,
                        icon = Icons.Rounded.FormatColorFill,
                        modifier = Modifier
                            .fillMaxWidth()
                            .container(
                                shape = RoundedCornerShape(24.dp)
                            )
                    )
                }
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.Neon && drawMode !is DrawMode.PathEffect && drawMode !is DrawMode.SpotHeal,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AlphaSelector(
                        value = alpha,
                        onValueChange = { alpha = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                DrawModeSelector(
                    modifier = Modifier.fillMaxWidth(),
                    value = drawMode,
                    strokeWidth = strokeWidth,
                    onValueChange = component::updateDrawMode,
                    values = remember(drawLineStyle) {
                        derivedStateOf {
                            if (drawLineStyle == DrawLineStyle.None) {
                                DrawMode.entries
                            } else {
                                listOf(
                                    DrawMode.Pen,
                                    DrawMode.Highlighter,
                                    DrawMode.Neon
                                )
                            }
                        }
                    }.value,
                    addFiltersSheetComponent = component.addFiltersSheetComponent,
                    filterTemplateCreationSheetComponent = component.filterTemplateCreationSheetComponent
                )
                DrawPathModeSelector(
                    modifier = Modifier.fillMaxWidth(),
                    value = drawPathMode,
                    onValueChange = component::updateDrawPathMode,
                    values = remember(drawMode, drawLineStyle) {
                        derivedStateOf {
                            val outlinedModes = listOf(
                                DrawPathMode.OutlinedRect(),
                                DrawPathMode.OutlinedOval,
                                DrawPathMode.OutlinedTriangle,
                                DrawPathMode.OutlinedPolygon(),
                                DrawPathMode.OutlinedStar()
                            )
                            if (drawMode !is DrawMode.Text && drawMode !is DrawMode.Image) {
                                when (drawLineStyle) {
                                    DrawLineStyle.None -> DrawPathMode.entries

                                    !is DrawLineStyle.Stamped<*> -> listOf(
                                        DrawPathMode.Free,
                                        DrawPathMode.Line,
                                        DrawPathMode.LinePointingArrow,
                                        DrawPathMode.PointingArrow,
                                        DrawPathMode.DoublePointingArrow,
                                        DrawPathMode.DoubleLinePointingArrow,
                                    ) + outlinedModes

                                    else -> listOf(
                                        DrawPathMode.Free,
                                        DrawPathMode.Line
                                    ) + outlinedModes
                                }
                            } else {
                                listOf(
                                    DrawPathMode.Free,
                                    DrawPathMode.Line
                                ) + outlinedModes
                            }
                        }
                    }.value
                )
                DrawLineStyleSelector(
                    modifier = Modifier.fillMaxWidth(),
                    value = drawLineStyle,
                    onValueChange = component::updateDrawLineStyle
                )
                HelperGridParamsSelector(
                    value = component.helperGridParams,
                    onValueChange = component::updateHelperGridParams,
                    modifier = Modifier.fillMaxWidth()
                )
                val settingsInteractor = LocalSimpleSettingsInteractor.current
                PreferenceRowSwitch(
                    modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth(),
                    checked = component.saveExif,
                    imageFormat = component.imageFormat,
                    onCheckedChange = component::setSaveExif
                )
                ImageFormatSelector(
                    modifier = Modifier.navigationBarsPadding(),
                    forceEnabled = component.drawBehavior is DrawBehavior.Background,
                    value = component.imageFormat,
                    onValueChange = component::setImageFormat
                )
            }
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (component.drawBehavior is DrawBehavior.None) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                isSecondaryButtonVisible = component.drawBehavior !is DrawBehavior.Background,
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                isPrimaryButtonVisible = component.drawBehavior !is DrawBehavior.None,
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                },
                showNullDataButtonAsContainer = true
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        enableNoDataScroll = false,
        noDataControls = {
            var showBackgroundDrawingSetup by rememberSaveable { mutableStateOf(false) }

            val cutout = WindowInsets.displayCutout.asPaddingValues()
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxHeight(),
                columns = StaggeredGridCells.Adaptive(300.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(
                    bottom = 12.dp + WindowInsets
                        .navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding(),
                    top = 12.dp,
                    end = 12.dp + cutout.calculateEndPadding(
                        LocalLayoutDirection.current
                    ),
                    start = 12.dp + cutout.calculateStartPadding(
                        LocalLayoutDirection.current
                    )
                ),
            ) {
                item {
                    PreferenceItem(
                        onClick = pickImage,
                        startIcon = Icons.Outlined.ImageTooltip,
                        title = stringResource(R.string.draw_on_image),
                        subtitle = stringResource(R.string.draw_on_image_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PreferenceItem(
                        onClick = { showBackgroundDrawingSetup = true },
                        startIcon = Icons.Outlined.FormatPaint,
                        title = stringResource(R.string.draw_on_background),
                        subtitle = stringResource(R.string.draw_on_background_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            val drawOnBackgroundParams = component.drawOnBackgroundParams
            val density = LocalDensity.current
            val screenWidth = with(density) { configuration.screenWidthDp.dp.roundToPx() }
            val screenHeight = with(density) { configuration.screenHeightDp.dp.roundToPx() }

            var width by remember(
                showBackgroundDrawingSetup,
                screenWidth,
                drawOnBackgroundParams
            ) {
                mutableIntStateOf(drawOnBackgroundParams?.width ?: screenWidth)
            }
            var height by remember(
                showBackgroundDrawingSetup,
                screenHeight,
                drawOnBackgroundParams
            ) {
                mutableIntStateOf(drawOnBackgroundParams?.height ?: screenHeight)
            }
            var sheetBackgroundColor by rememberSaveable(
                showBackgroundDrawingSetup,
                drawOnBackgroundParams,
                stateSaver = ColorSaver
            ) {
                mutableStateOf(drawOnBackgroundParams?.color?.toColor() ?: Color.White)
            }
            EnhancedModalBottomSheet(
                title = {
                    TitleItem(
                        text = stringResource(R.string.draw),
                        icon = Icons.Rounded.FormatPaint
                    )
                },
                confirmButton = {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            showBackgroundDrawingSetup = false
                            component.startDrawOnBackground(
                                reqWidth = width,
                                reqHeight = height,
                                color = sheetBackgroundColor
                            )
                        }
                    ) {
                        AutoSizeText(stringResource(R.string.ok))
                    }
                },
                sheetContent = {
                    Box {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            Row(
                                Modifier
                                    .padding(16.dp)
                                    .container(shape = RoundedCornerShape(24.dp))
                            ) {
                                RoundedTextField(
                                    value = width.takeIf { it != 0 }?.toString() ?: "",
                                    onValueChange = {
                                        width = it.restrict(8192).toIntOrNull() ?: 0
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    label = {
                                        Text(stringResource(R.string.width, " "))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            start = 8.dp,
                                            top = 8.dp,
                                            bottom = 4.dp,
                                            end = 4.dp
                                        )
                                )
                                RoundedTextField(
                                    value = height.takeIf { it != 0 }?.toString() ?: "",
                                    onValueChange = {
                                        height = it.restrict(8192).toIntOrNull() ?: 0
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    label = {
                                        Text(stringResource(R.string.height, " "))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            start = 4.dp,
                                            top = 8.dp,
                                            bottom = 4.dp,
                                            end = 8.dp
                                        ),
                                )
                            }
                            ColorRowSelector(
                                value = sheetBackgroundColor,
                                onValueChange = { sheetBackgroundColor = it },
                                icon = Icons.Rounded.FormatColorFill,
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )
                                    .container(RoundedCornerShape(24.dp))
                            )
                        }
                    }
                },
                visible = showBackgroundDrawingSetup,
                onDismiss = {
                    showBackgroundDrawingSetup = it
                }
            )
        },
        canShowScreenData = component.drawBehavior !is DrawBehavior.None,
        showActionsInTopAppBar = false,
        mainContentWeight = 0.65f
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving
    )

    var colorPickerColor by rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(Color.Black) }
    PickColorFromImageSheet(
        visible = showPickColorSheet,
        onDismiss = {
            showPickColorSheet = false
        },
        bitmap = component.colorPickerBitmap,
        onColorChange = { colorPickerColor = it },
        color = colorPickerColor
    )

    ExitWithoutSavingDialog(
        onExit = {
            if (component.drawBehavior !is DrawBehavior.None) {
                component.resetDrawBehavior()
                themeState.updateColorTuple(appColorTuple)
            } else component.onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    DrawLockScreenOrientation()
}