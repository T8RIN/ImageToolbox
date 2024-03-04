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

@file:Suppress("PrivatePropertyName")

package ru.tech.imageresizershrinker.feature.draw.presentation


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.observeAsState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.ImageTooltip
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EraseModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.AlphaSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.BackgroundColorSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.SaveExifWidget
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.draw.domain.DrawBehavior
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.domain.pt
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BitmapDrawer
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawColorSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawModeSaver
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawModeSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawPathModeSaver
import ru.tech.imageresizershrinker.feature.draw.presentation.components.DrawPathModeSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.LineWidthSelector
import ru.tech.imageresizershrinker.feature.draw.presentation.components.OpenColorPickerCard
import ru.tech.imageresizershrinker.feature.draw.presentation.components.PtSaver
import ru.tech.imageresizershrinker.feature.draw.presentation.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: DrawViewModel = hiltViewModel()
) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.drawBehavior !is DrawBehavior.None && viewModel.isBitmapChanged) showExitDialog =
            true
        else if (viewModel.drawBehavior !is DrawBehavior.None) {
            viewModel.resetDrawBehavior()
            themeState.updateColorTuple(appColorTuple)
        } else onGoBack()
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it)
            viewModel.decodeBitmapByUri(
                uri = it,
                onGetMimeType = viewModel::updateMimeType,
                onGetExif = {},
                onGetBitmap = viewModel::updateBitmap,
                onError = {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            )
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        if (allowChangeColor) {
            viewModel.bitmap?.let {
                themeState.updateColorByImage(it)
            }
        }
    }

    val pickImageLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Single)
        ) { uris ->
            uris.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
                viewModel.setUri(it)
                viewModel.decodeBitmapByUri(
                    uri = it,
                    onGetMimeType = viewModel::updateMimeType,
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                    onError = {
                        scope.launch {
                            toastHostState.showError(context, it)
                        }
                    }
                )
            }
        }

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val saveBitmap: () -> Unit = {
        viewModel.saveBitmap { saveResult ->
            parseSaveResult(
                saveResult = saveResult,
                onSuccess = showConfetti,
                toastHostState = toastHostState,
                scope = scope,
                context = context
            )
        }
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

    val showPickColorSheet = rememberSaveable { mutableStateOf(false) }

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

    var panEnabled by rememberSaveable(viewModel.drawBehavior) { mutableStateOf(false) }

    var strokeWidth by rememberSaveable(
        viewModel.drawBehavior,
        stateSaver = PtSaver
    ) { mutableStateOf(20.pt) }

    var drawColor by rememberSaveable(
        stateSaver = ColorSaver,
        inputs = arrayOf(viewModel.drawBehavior)
    ) { mutableStateOf(Color.Black) }
    var isEraserOn by rememberSaveable(viewModel.drawBehavior) { mutableStateOf(false) }
    var drawMode by rememberSaveable(
        stateSaver = DrawModeSaver,
        inputs = arrayOf(viewModel.drawBehavior)
    ) { mutableStateOf(DrawMode.Pen) }
    var alpha by rememberSaveable(viewModel.drawBehavior, drawMode) {
        mutableFloatStateOf(if (drawMode is DrawMode.Highlighter) 0.4f else 1f)
    }
    var brushSoftness by rememberSaveable(viewModel.drawBehavior, drawMode, stateSaver = PtSaver) {
        mutableStateOf(if (drawMode is DrawMode.Neon) 35.pt else 0.pt)
    }
    var drawPathMode by rememberSaveable(viewModel.drawBehavior, stateSaver = DrawPathModeSaver) {
        mutableStateOf(DrawPathMode.Free)
    }

    val controls = @Composable {
        OpenColorPickerCard(
            onOpen = {
                viewModel.openColorPicker()
                showPickColorSheet.value = true
            }
        )
        LineWidthSelector(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp
            ),
            value = strokeWidth.value,
            onValueChange = { strokeWidth = it.pt }
        )
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Highlighter && drawMode !is DrawMode.PathEffect,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BrushSoftnessSelector(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp, start = 16.dp),
                value = brushSoftness.value,
                onValueChange = { brushSoftness = it.pt }
            )
        }
        if (viewModel.drawBehavior is DrawBehavior.Background) {
            BackgroundColorSelector(
                value = viewModel.backgroundColor,
                onColorChange = viewModel::updateBackgroundColor
            )
        } else {
            Spacer(Modifier.height(16.dp))
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.PathEffect,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DrawColorSelector(
                drawColor = drawColor,
                onColorChange = { drawColor = it }
            )
        }
        AnimatedVisibility(
            visible = drawMode !is DrawMode.Neon && drawMode !is DrawMode.PathEffect,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AlphaSelector(
                value = alpha,
                onValueChange = { alpha = it }
            )
        }
        DrawModeSelector(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            value = drawMode,
            onValueChange = { drawMode = it }
        )
        DrawPathModeSelector(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            value = drawPathMode,
            onValueChange = { drawPathMode = it }
        )
        SaveExifWidget(
            modifier = Modifier.padding(horizontal = 16.dp),
            checked = viewModel.saveExif,
            imageFormat = viewModel.imageFormat,
            onCheckedChange = viewModel::setSaveExif,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        )
        ImageFormatSelector(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            forceEnabled = viewModel.drawBehavior is DrawBehavior.Background,
            value = viewModel.imageFormat,
            onValueChange = viewModel::updateMimeType,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        )
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
            onClick = viewModel::undo,
            enabled = viewModel.lastPaths.isNotEmpty() || viewModel.paths.isNotEmpty()
        ) {
            Icon(Icons.AutoMirrored.Rounded.Undo, null)
        }
        EnhancedIconButton(
            containerColor = Color.Transparent,
            borderColor = MaterialTheme.colorScheme.outlineVariant(
                luminance = 0.1f
            ),
            onClick = viewModel::redo,
            enabled = viewModel.undonePaths.isNotEmpty()
        ) {
            Icon(Icons.AutoMirrored.Rounded.Redo, null)
        }
        EraseModeButton(
            selected = isEraserOn,
            enabled = !panEnabled,
            onClick = {
                isEraserOn = !isEraserOn
            }
        )
    }

    val bitmap = viewModel.bitmap ?: (viewModel.drawBehavior as? DrawBehavior.Background)?.run {
        remember { ImageBitmap(width, height).asAndroidBitmap() }
    } ?: remember {
        ImageBitmap(
            configuration.screenWidthDp,
            configuration.screenHeightDp
        ).asAndroidBitmap()
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topAppBar: @Composable () -> Unit = {
        AnimatedContent(
            targetState = viewModel.drawBehavior,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(false)
            }
        ) { drawBehavior ->
            if (drawBehavior !is DrawBehavior.None) {
                TopAppBar(
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(stringResource(R.string.draw))
                        }
                    },
                    actions = {
                        if (portrait) {
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
                                Icon(Icons.Rounded.Tune, null)
                            }
                        }
                        ShareButton(
                            enabled = viewModel.drawBehavior !is DrawBehavior.None,
                            onShare = {
                                viewModel.shareBitmap(showConfetti)
                            },
                            onCopy = { manager ->
                                viewModel.cacheCurrentImage { uri ->
                                    manager.setClip(uri.asClip(context))
                                    showConfetti()
                                }
                            }
                        )
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = {
                                viewModel.clearDrawing()
                            },
                            enabled = viewModel.drawBehavior !is DrawBehavior.None && viewModel.isBitmapChanged
                        ) {
                            Icon(Icons.Outlined.Delete, null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    }
                )
            } else {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(stringResource(R.string.draw))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        TopAppBarEmoji()
                    }
                )
            }
        }
    }

    val drawBox = @Composable {
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
                paths = viewModel.paths,
                strokeWidth = strokeWidth,
                brushSoftness = brushSoftness,
                drawColor = drawColor.copy(alpha),
                onAddPath = viewModel::addPath,
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
                    .aspectRatio(aspectRatio, portrait)
                    .fillMaxSize(),
                panEnabled = panEnabled,
                onDraw = {},
                onRequestFiltering = viewModel::filter,
                drawPathMode = drawPathMode,
                backgroundColor = viewModel.backgroundColor
            )
        }
    }


    val screenWidth = LocalConfiguration.current.screenWidthDp
    val easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)
    AnimatedContent(
        transitionSpec = {
            if (this.targetState !is DrawBehavior.None) {
                slideInHorizontally(
                    animationSpec = tween(600, easing = easing),
                    initialOffsetX = { screenWidth }) + fadeIn(
                    tween(300, 100)
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(600, easing = easing),
                    targetOffsetX = { -screenWidth }) + fadeOut(
                    tween(300, 100)
                )
            } else {
                slideInHorizontally(
                    animationSpec = tween(600, easing = easing),
                    initialOffsetX = { -screenWidth }) + fadeIn(
                    tween(300, 100)
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(600, easing = easing),
                    targetOffsetX = { screenWidth }) + fadeOut(
                    tween(300, 100)
                )
            }
        },
        targetState = viewModel.drawBehavior
    ) { drawBehavior ->
        if (drawBehavior is DrawBehavior.None) {
            Box(Modifier.fillMaxSize()) {
                val showBackgroundDrawingSetup = rememberSaveable { mutableStateOf(false) }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    topAppBar()
                    val cutout = WindowInsets.displayCutout.asPaddingValues()
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.weight(1f),
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
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                            )
                        }
                        item {
                            PreferenceItem(
                                onClick = { showBackgroundDrawingSetup.value = true },
                                startIcon = Icons.Outlined.FormatPaint,
                                title = stringResource(R.string.draw_on_background),
                                subtitle = stringResource(R.string.draw_on_background_sub),
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawHorizontalStroke(true)
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    3.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        EnhancedFloatingActionButton(
                            onClick = pickImage,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(16.dp),
                            content = {
                                Spacer(Modifier.width(16.dp))
                                Icon(Icons.Rounded.AddPhotoAlternate, null)
                                Spacer(Modifier.width(16.dp))
                                Text(stringResource(R.string.pick_image_alt))
                                Spacer(Modifier.width(16.dp))
                            }
                        )
                    }
                }

                val density = LocalDensity.current
                var height by remember(showBackgroundDrawingSetup.value, configuration) {
                    mutableIntStateOf(with(density) { configuration.screenHeightDp.dp.roundToPx() })
                }
                var width by remember(showBackgroundDrawingSetup.value, configuration) {
                    mutableIntStateOf(with(density) { configuration.screenWidthDp.dp.roundToPx() })
                }
                var sheetBackgroundColor by remember { mutableStateOf(Color.White) }
                SimpleSheet(
                    title = {
                        TitleItem(
                            text = stringResource(R.string.draw),
                            icon = Icons.Rounded.Draw
                        )
                    },
                    confirmButton = {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                showBackgroundDrawingSetup.value = false
                                viewModel.startDrawOnBackground(
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
                                                bottom = 8.dp,
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
                                                bottom = 8.dp,
                                                end = 8.dp
                                            ),
                                    )
                                }
                                BackgroundColorSelector(
                                    value = sheetBackgroundColor,
                                    onColorChange = { sheetBackgroundColor = it }
                                )
                            }
                        }
                    },
                    visible = showBackgroundDrawingSetup
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
                                },
                                floatingActionButton = {
                                    Row {
                                        if (drawBehavior is DrawBehavior.Image) {
                                            EnhancedFloatingActionButton(
                                                onClick = pickImage,
                                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.AddPhotoAlternate,
                                                    contentDescription = null
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        EnhancedFloatingActionButton(
                                            onClick = saveBitmap
                                        ) {
                                            Icon(Icons.Rounded.Save, null)
                                        }
                                    }
                                }
                            )
                            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                                item {
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
                            drawBox()
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
                                    resultPadding = 0.dp,
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                )
                                .weight(1.2f)
                                .clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            drawBox()
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
                                Row(
                                    Modifier
                                        .padding(16.dp)
                                        .container(shape = CircleShape)
                                ) {
                                    secondaryControls()
                                }
                                controls()
                            }
                        }
                        val direction = LocalLayoutDirection.current
                        Column(
                            Modifier
                                .container(
                                    shape = RectangleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer
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
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddPhotoAlternate,
                                    contentDescription = null
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            EnhancedFloatingActionButton(
                                onClick = saveBitmap
                            ) {
                                Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.isSaving || viewModel.isImageLoading) {
        LoadingDialog(viewModel.isSaving) {
            viewModel.cancelSaving()
        }
    }

    var colorPickerColor by rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(Color.Black) }
    PickColorFromImageSheet(
        visible = showPickColorSheet,
        bitmap = viewModel.colorPickerBitmap,
        onColorChange = { colorPickerColor = it },
        color = colorPickerColor
    )

    ExitWithoutSavingDialog(
        onExit = {
            if (viewModel.drawBehavior !is DrawBehavior.None) {
                viewModel.resetDrawBehavior()
                themeState.updateColorTuple(appColorTuple)
            } else onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(onBack = onBack)

    DrawLockScreenOrientation()
}