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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.utils.isExpanded
import ru.tech.imageresizershrinker.core.ui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.ColorStopSelection
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientPropertiesSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientSizeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.GradientTypeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.TileModeSelector
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.viewModel.GradientMakerViewModel

@Composable
fun GradientMakerScreen(
    onGoBack: () -> Unit,
    viewModel: GradientMakerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val context = LocalContext.current

    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch { confettiController.showEmpty() }
    }
    val toastHostState = LocalToastHost.current

    LaunchedEffect(viewModel.brush) {
        if (allowChangeColor) {
            viewModel.createGradientBitmap(
                IntegerSize(1000, 1000)
            )?.let { bitmap ->
                themeState.updateColorByImage(bitmap)
            }
        }
    }

    val imageInside =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    var imageState by remember { mutableStateOf(middleImageState()) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, canScroll = { !imageState.isExpanded() }
    )

    LaunchedEffect(imageState) {
        if (imageState.isExpanded()) {
            while (topAppBarState.heightOffset > topAppBarState.heightOffsetLimit) {
                topAppBarState.heightOffset -= 5f
                delay(1)
            }
        }
    }

    val saveBitmap: () -> Unit = {
        if (viewModel.brush != null) {
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
    }

    val imageBlock = @Composable {
        Box(
            modifier = Modifier
                .container()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            val brush = viewModel.brush
            AnimatedContent(
                targetState = viewModel
                    .gradientSize
                    .aspectRatio
                    .roundToTwoDigits()
                    .coerceIn(0.01f..100f)
            ) { aspectRatio ->
                Box(
                    modifier = Modifier
                        .aspectRatio(aspectRatio)
                        .clip(MaterialTheme.shapes.medium)
                        .transparencyChecker()
                        .background(
                            brush = brush ?: SolidColor(MaterialTheme.colorScheme.surfaceContainer),
                            shape = MaterialTheme.shapes.medium
                        )
                        .onSizeChanged {
                            viewModel.setPreviewSize(
                                Size(
                                    it.width.toFloat(),
                                    it.height.toFloat()
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(visible = brush == null) {
                        Icon(
                            imageVector = Icons.Outlined.BrokenImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.5f)
                        )
                    }
                }
            }
        }
    }

    val actions: @Composable RowScope.() -> Unit = {
        EnhancedIconButton(
            containerColor = Color.Transparent,
            contentColor = LocalContentColor.current,
            enableAutoShadowAndBorder = false,
            onClick = {
                viewModel.shareBitmap { showConfetti() }
            },
            enabled = viewModel.brush != null
        ) {
            Icon(Icons.Outlined.Share, null)
        }
    }

    val buttons = @Composable {
        BottomButtonsBlock(
            targetState = (false) to imageInside,
            onPickImage = {},
            isPickImageButtonVisible = false,
            isPrimaryButtonVisible = viewModel.brush != null,
            onPrimaryButtonClick = saveBitmap,
            actions = {
                if (imageInside) actions()
            }
        )
    }

    val controls: @Composable () -> Unit = {
        GradientSizeSelector(
            value = viewModel.gradientSize,
            onWidthChange = viewModel::updateWidth,
            onHeightChange = viewModel::updateHeight
        )
        Spacer(Modifier.height(8.dp))
        GradientTypeSelector(
            value = viewModel.gradientType,
            onValueChange = viewModel::setGradientType
        ) {
            GradientPropertiesSelector(
                gradientType = viewModel.gradientType,
                linearAngle = viewModel.angle,
                onLinearAngleChange = viewModel::updateLinearAngle,
                centerFriction = viewModel.centerFriction,
                radiusFriction = viewModel.radiusFriction,
                onRadialDimensionsChange = viewModel::setRadialProperties
            )
        }
        Spacer(Modifier.height(8.dp))
        ColorStopSelection(
            colorStops = viewModel.colorStops,
            onRemoveClick = viewModel::removeColorStop,
            onValueChange = viewModel::updateColorStop,
            onAddColorStop = viewModel::addColorStop
        )
        Spacer(Modifier.height(8.dp))
        TileModeSelector(
            value = viewModel.tileMode,
            onValueChange = viewModel::setTileMode
        )
        Spacer(Modifier.height(8.dp))
        ExtensionGroup(
            value = viewModel.imageFormat,
            enabled = true,
            onValueChange = viewModel::setImageFormat,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }

    val focus = LocalFocusManager.current
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                focus.clearFocus()
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(Modifier.fillMaxSize()) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            TopAppBarTitle(
                                title = stringResource(R.string.gradient_maker),
                                input = Unit,
                                isLoading = false,
                                size = null
                            )
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
                            onClick = onGoBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (!imageInside) actions()
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside) {
                        Box(
                            Modifier
                                .container(
                                    RectangleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                )
                                .weight(1.2f)
                                .padding(20.dp)
                        ) {
                            Box(Modifier.align(Alignment.Center)) {
                                imageBlock()
                            }
                        }
                    }
                    val internalHeight = rememberAvailableHeight(imageState)

                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = WindowInsets
                                .navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding() + WindowInsets.ime
                                .asPaddingValues()
                                .calculateBottomPadding() + (if (!imageInside) 20.dp else 100.dp),
                            top = if (!imageInside) 20.dp else 0.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        imageStickyHeader(
                            visible = imageInside,
                            internalHeight = internalHeight,
                            imageState = imageState,
                            onStateChange = { imageState = it },
                            imageBlock = imageBlock
                        )
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                controls()
                            }
                        }
                    }
                    if (!imageInside) {
                        buttons()
                    }
                }
            }

            if (imageInside) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons()
                }
            }

            BackHandler { onGoBack() }
        }
    }

    if (viewModel.isSaving) {
        LoadingDialog(viewModel.isSaving) {
            viewModel.cancelSaving()
        }
    }
}