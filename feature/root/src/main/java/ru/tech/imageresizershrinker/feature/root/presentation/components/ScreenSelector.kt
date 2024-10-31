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

package ru.tech.imageresizershrinker.feature.root.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.backStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.pushNew
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.colllage_maker.presentation.CollageMakerContent
import ru.tech.imageresizershrinker.color_tools.presentation.ColorToolsContent
import ru.tech.imageresizershrinker.core.domain.utils.Lambda
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.ApngToolsContent
import ru.tech.imageresizershrinker.feature.cipher.presentation.CipherContent
import ru.tech.imageresizershrinker.feature.compare.presentation.CompareContent
import ru.tech.imageresizershrinker.feature.crop.presentation.CropContent
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.DeleteExifContent
import ru.tech.imageresizershrinker.feature.document_scanner.presentation.DocumentScannerContent
import ru.tech.imageresizershrinker.feature.draw.presentation.DrawContent
import ru.tech.imageresizershrinker.feature.easter_egg.presentation.EasterEggContent
import ru.tech.imageresizershrinker.feature.erase_background.presentation.EraseBackgroundContent
import ru.tech.imageresizershrinker.feature.filters.presentation.FiltersContent
import ru.tech.imageresizershrinker.feature.format_conversion.presentation.FormatConversionContent
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.GeneratePaletteContent
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.GifToolsContent
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.GradientMakerContent
import ru.tech.imageresizershrinker.feature.image_preview.presentation.ImagePreviewContent
import ru.tech.imageresizershrinker.feature.image_stacking.presentation.ImageStackingContent
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.ImageStitchingContent
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.JxlToolsContent
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.LimitsResizeContent
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.LoadNetImageContent
import ru.tech.imageresizershrinker.feature.main.presentation.MainContent
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.PdfToolsContent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.PickColorFromImageContent
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.RecognizeTextContent
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.ResizeAndConvertContent
import ru.tech.imageresizershrinker.feature.root.presentation.viewModel.RootViewModel
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.ScanQrCodeContent
import ru.tech.imageresizershrinker.feature.settings.presentation.SettingsContent
import ru.tech.imageresizershrinker.feature.single_edit.presentation.SingleEditContent
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.SvgMakerContent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.WatermarkingContent
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.WebpToolsContent
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.WeightResizeContent
import ru.tech.imageresizershrinker.feature.zip.presentation.ZipContent
import ru.tech.imageresizershrinker.image_splitting.presentation.ImageSplitterContent
import ru.tech.imageresizershrinker.noise_generation.presentation.NoiseGenerationContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
internal fun ScreenSelector(
    viewModel: RootViewModel,
    onRegisterScreenOpen: (Screen) -> Unit,
) {
    val context = LocalContext.current as ComponentActivity
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
        navController.pop()
        scope.launch {
            delay(350L) //delay for screen anim
            themeState.updateColorTuple(appColorTuple)
        }
    }

    val onNavigate: (Screen) -> Unit = { destination ->
        navController.push(destination)
        onRegisterScreenOpen(destination)
    }

    val onTryGetUpdate: (Boolean, Lambda) -> Unit = { isNewRequest, onNoUpdates ->
        viewModel.tryGetUpdate(
            isNewRequest = isNewRequest,
            isInstalledFromMarket = context.isInstalledFromPlayStore(),
            onNoUpdates = onNoUpdates
        )
    }

    Children(
        stack = viewModel.childStack,
        modifier = Modifier.fillMaxSize(),
        animation = predictiveBackAnimation(
            backHandler = viewModel.backHandler,
            fallbackAnimation = stackAnimation(fade() + scale()),
            onBack = viewModel::onBackClicked
        )
    ) { screen ->
        when (val instance = screen.instance) {
            is RootViewModel.Child.Settings -> {
                SettingsContent(
                    viewModel = instance.component,
                    onTryGetUpdate = onTryGetUpdate,
                    isUpdateAvailable = viewModel.isUpdateAvailable,
                    onGoBack = onGoBack,
                    onNavigateToEasterEgg = {
                        navController.pushNew(Screen.EasterEgg)
                        onRegisterScreenOpen(Screen.EasterEgg)
                    },
                    onNavigateToSettings = {
                        navController.pushNew(Screen.Settings)
                        onRegisterScreenOpen(Screen.Settings)
                        true
                    }
                )
            }

            is RootViewModel.Child.EasterEgg -> {
                EasterEggContent(onGoBack = onGoBack)
            }

            is RootViewModel.Child.Main -> {
                MainContent(
                    onTryGetUpdate = onTryGetUpdate,
                    isUpdateAvailable = viewModel.isUpdateAvailable,
                    onUpdateUris = viewModel::updateUris,
                    onNavigateToSettings = {
                        navController.pushNew(Screen.Settings)
                        onRegisterScreenOpen(Screen.Settings)
                        true
                    },
                    onNavigateToScreenWithPopUpTo = { destination ->
                        navController.popWhile { it != Screen.Main }
                        onNavigate(destination)
                    },
                    onNavigateToEasterEgg = {
                        navController.pushNew(Screen.EasterEgg)
                        onRegisterScreenOpen(Screen.EasterEgg)
                    },
                    onToggleFavorite = viewModel::toggleFavoriteScreen
                )
            }

            is RootViewModel.Child.SingleEdit -> {
                SingleEditContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.ResizeAndConvert -> {
                ResizeAndConvertContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.DeleteExif -> {
                DeleteExifContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.WeightResize -> {
                WeightResizeContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.Crop -> {
                CropContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.PickColorFromImage -> {
                PickColorFromImageContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.ImagePreview -> {
                ImagePreviewContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.GeneratePalette -> {
                GeneratePaletteContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.Compare -> {
                CompareContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.LoadNetImage -> {
                LoadNetImageContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.Filter -> {
                FiltersContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.LimitResize -> {
                LimitsResizeContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.Draw -> {
                DrawContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.Cipher -> {
                CipherContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.EraseBackground -> {
                EraseBackgroundContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.ImageStitching -> {
                ImageStitchingContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.PdfTools -> {
                PdfToolsContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.RecognizeText -> {
                RecognizeTextContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.GradientMaker -> {
                GradientMakerContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.Watermarking -> {
                WatermarkingContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.GifTools -> {
                GifToolsContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.ApngTools -> {
                ApngToolsContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.Zip -> {
                ZipContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.JxlTools -> {
                JxlToolsContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.SvgMaker -> {
                SvgMakerContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.FormatConversion -> {
                FormatConversionContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.DocumentScanner -> {
                DocumentScannerContent(onGoBack = onGoBack)
            }

            is RootViewModel.Child.ScanQrCode -> {
                ScanQrCodeContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack
                )
            }

            is RootViewModel.Child.ImageStacking -> {
                ImageStackingContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.ImageSplitting -> {
                ImageSplitterContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.ColorTools -> {
                ColorToolsContent(onGoBack = onGoBack)
            }

            is RootViewModel.Child.WebpTools -> {
                WebpToolsContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.NoiseGeneration -> {
                NoiseGenerationContent(
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is RootViewModel.Child.CollageMaker -> {
                CollageMakerContent(
                    viewModel = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }
        }
    }
    ScreenBasedMaxBrightnessEnforcement(viewModel.childStack.backStack.lastOrNull()?.configuration)
}