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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
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

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact


    AdaptiveLayoutScreen(
        isPortrait = isPortrait,
        canShowScreenData = true,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.gradient_maker),
                input = Unit,
                isLoading = false,
                size = null
            )
        },
        onGoBack = onGoBack,
        actions = {
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
        },
        imagePreview = {
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
                                brush = brush
                                    ?: SolidColor(MaterialTheme.colorScheme.surfaceContainer),
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
        },
        controls = {
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
        },
        buttons = { actions ->
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
            BottomButtonsBlock(
                targetState = (false) to isPortrait,
                onSecondaryButtonClick = {},
                isPickImageButtonVisible = false,
                isPrimaryButtonVisible = viewModel.brush != null,
                onPrimaryButtonClick = saveBitmap,
                actions = {
                    if (isPortrait) actions()
                }
            )
        }
    )

    if (viewModel.isSaving) {
        LoadingDialog(viewModel.isSaving) {
            viewModel.cancelSaving()
        }
    }
}