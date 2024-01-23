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

package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.findActivity
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.feature.bytes_resize.presentation.BytesResizeScreen
import ru.tech.imageresizershrinker.feature.cipher.presentation.FileCipherScreen
import ru.tech.imageresizershrinker.feature.compare.presentation.CompareScreen
import ru.tech.imageresizershrinker.feature.crop.presentation.CropScreen
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.DeleteExifScreen
import ru.tech.imageresizershrinker.feature.draw.presentation.DrawScreen
import ru.tech.imageresizershrinker.feature.erase_background.presentation.EraseBackgroundScreen
import ru.tech.imageresizershrinker.feature.filters.presentation.FiltersScreen
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.GeneratePaletteScreen
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.GradientMakerScreen
import ru.tech.imageresizershrinker.feature.image_preview.presentation.ImagePreviewScreen
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.ImageStitchingScreen
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.LimitsResizeScreen
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.LoadNetImageScreen
import ru.tech.imageresizershrinker.feature.main.presentation.MainScreen
import ru.tech.imageresizershrinker.feature.main.presentation.viewModel.MainViewModel
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.PdfToolsScreen
import ru.tech.imageresizershrinker.feature.pick_color.presentation.PickColorFromImageScreen
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.RecognizeTextScreen
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.ResizeAndConvertScreen
import ru.tech.imageresizershrinker.feature.single_edit.presentation.SingleEditScreen

@Composable
fun ScreenSelector(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = viewModel.navController
    val settingsState = LocalSettingsState.current
    val themeState = LocalDynamicThemeState.current
    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )
    val onGoBack: () -> Unit = {
        viewModel.updateUris(null)
        navController.apply {
            if (backstack.entries.size > 1) pop()
        }
        scope.launch {
            kotlinx.coroutines.delay(350L)
            themeState.updateColorTuple(appColorTuple)
        }
    }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = { action, _, _ ->
            if (action != NavAction.Pop) {
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
        }
    ) { screen ->
        when (screen) {
            is Screen.Main -> {
                MainScreen(
                    viewModel = viewModel
                )
            }

            is Screen.SingleEdit -> {
                SingleEditScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.ResizeAndConvert -> {
                ResizeAndConvertScreen(
                    uriState = screen.uris,
                    onGoBack = onGoBack
                )
            }

            is Screen.DeleteExif -> {
                DeleteExifScreen(
                    uriState = screen.uris,
                    onGoBack = onGoBack
                )
            }

            is Screen.ResizeByBytes -> {
                BytesResizeScreen(
                    uriState = screen.uris,
                    onGoBack = onGoBack
                )
            }

            is Screen.Crop -> {
                CropScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.PickColorFromImage -> {
                PickColorFromImageScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.ImagePreview -> {
                ImagePreviewScreen(
                    uriState = screen.uris,
                    onGoBack = {
                        if (screen.uris != null) {
                            context.findActivity()?.finishAffinity()
                        } else onGoBack()
                    }
                )
            }

            is Screen.GeneratePalette -> {
                GeneratePaletteScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.Compare -> {
                CompareScreen(
                    comparableUris = screen.uris
                        ?.takeIf { it.size == 2 }
                        ?.let { it[0] to it[1] },
                    onGoBack = onGoBack
                )
            }

            is Screen.LoadNetImage -> {
                LoadNetImageScreen(
                    url = screen.url,
                    onGoBack = onGoBack
                )
            }

            is Screen.Filter -> {
                FiltersScreen(
                    type = screen.type,
                    onGoBack = onGoBack
                )
            }

            is Screen.LimitResize -> {
                LimitsResizeScreen(
                    uriState = screen.uris,
                    onGoBack = onGoBack
                )
            }

            is Screen.Draw -> {
                DrawScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.Cipher -> {
                FileCipherScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.EraseBackground -> {
                EraseBackgroundScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.ImageStitching -> {
                ImageStitchingScreen(
                    uriState = screen.uris,
                    onGoBack = onGoBack
                )
            }

            is Screen.PdfTools -> {
                PdfToolsScreen(
                    type = screen.type,
                    onGoBack = {
                        if (screen.type != null) {
                            context.findActivity()?.finishAffinity()
                        } else onGoBack()
                    }
                )
            }

            is Screen.RecognizeText -> {
                RecognizeTextScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            Screen.GradientMaker -> {
                GradientMakerScreen(onGoBack = onGoBack)
            }
        }
    }
    val currentScreen by remember(navController.backstack.entries) {
        derivedStateOf {
            navController.backstack.entries.lastOrNull()?.destination
        }
    }
    ScreenBasedMaxBrightnessEnforcement(currentScreen)
}