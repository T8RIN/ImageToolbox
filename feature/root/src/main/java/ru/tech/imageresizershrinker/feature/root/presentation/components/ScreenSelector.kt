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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import ru.tech.imageresizershrinker.colllage_maker.presentation.CollageMakerContent
import ru.tech.imageresizershrinker.color_tools.presentation.ColorToolsContent
import ru.tech.imageresizershrinker.core.domain.utils.Lambda
import ru.tech.imageresizershrinker.core.settings.presentation.provider.rememberAppColorTuple
import ru.tech.imageresizershrinker.core.ui.utils.animation.AlphaEasing
import ru.tech.imageresizershrinker.core.ui.utils.animation.FancyTransitionEasing
import ru.tech.imageresizershrinker.core.ui.utils.animation.PointToPointEasing
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
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
import ru.tech.imageresizershrinker.feature.root.presentation.components.navigation.NavigationChild
import ru.tech.imageresizershrinker.feature.root.presentation.screenLogic.RootComponent
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
    component: RootComponent,
    onRegisterScreenOpen: (Screen) -> Unit,
) {
    val appColorTuple = rememberAppColorTuple()

    val context = LocalComponentActivity.current
    val themeState = LocalDynamicThemeState.current
    val onGoBack: () -> Unit = {
        component.updateUris(null)
        component.navigateBack()
        themeState.updateColorTuple(appColorTuple)
    }

    val onNavigate: (Screen) -> Unit = { destination ->
        component.navigateTo(destination)
        onRegisterScreenOpen(destination)
    }

    val onTryGetUpdate: (Boolean, Lambda) -> Unit = { isNewRequest, onNoUpdates ->
        component.tryGetUpdate(
            isNewRequest = isNewRequest,
            isInstalledFromMarket = context.isInstalledFromPlayStore(),
            onNoUpdates = onNoUpdates
        )
    }

    val childStack by component.childStack.subscribeAsState()
    Children(
        stack = childStack,
        modifier = Modifier.fillMaxSize(),
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            onBack = onGoBack,
            fallbackAnimation = stackAnimation(
                fade(
                    tween(
                        durationMillis = 300,
                        easing = AlphaEasing
                    )
                ) + slide(
                    tween(
                        durationMillis = 400,
                        easing = FancyTransitionEasing
                    )
                ) + scale(
                    tween(
                        durationMillis = 500,
                        easing = PointToPointEasing
                    )
                )
            ),
            selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
        )
    ) { screen ->
        when (val instance = screen.instance) {
            is NavigationChild.Settings -> {
                SettingsContent(
                    component = instance.component,
                    onTryGetUpdate = onTryGetUpdate,
                    isUpdateAvailable = component.isUpdateAvailable,
                    onGoBack = onGoBack,
                    onNavigate = { destination ->
                        component.navigateToNew(destination)
                        onRegisterScreenOpen(destination)
                    }
                )
            }

            is NavigationChild.EasterEgg -> {
                EasterEggContent(
                    onGoBack = onGoBack,
                    component = instance.component
                )
            }

            is NavigationChild.Main -> {
                MainContent(
                    onTryGetUpdate = onTryGetUpdate,
                    isUpdateAvailable = component.isUpdateAvailable,
                    onUpdateUris = component::updateUris,
                    onNavigate = { destination ->
                        if (childStack.items.lastOrNull()?.configuration != Screen.Main) {
                            component.navigateBack()
                        }
                        component.navigateToNew(destination)
                        onRegisterScreenOpen(destination)
                    },
                    onToggleFavorite = component::toggleFavoriteScreen,
                    settingsComponent = instance.component
                )
            }

            is NavigationChild.SingleEdit -> {
                SingleEditContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.ResizeAndConvert -> {
                ResizeAndConvertContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.DeleteExif -> {
                DeleteExifContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.WeightResize -> {
                WeightResizeContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.Crop -> {
                CropContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.PickColorFromImage -> {
                PickColorFromImageContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.ImagePreview -> {
                ImagePreviewContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.GeneratePalette -> {
                GeneratePaletteContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.Compare -> {
                CompareContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.LoadNetImage -> {
                LoadNetImageContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.Filter -> {
                FiltersContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.LimitResize -> {
                LimitsResizeContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.Draw -> {
                DrawContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.Cipher -> {
                CipherContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.EraseBackground -> {
                EraseBackgroundContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.ImageStitching -> {
                ImageStitchingContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.PdfTools -> {
                PdfToolsContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.RecognizeText -> {
                RecognizeTextContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.GradientMaker -> {
                GradientMakerContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.Watermarking -> {
                WatermarkingContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.GifTools -> {
                GifToolsContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.ApngTools -> {
                ApngToolsContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.Zip -> {
                ZipContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.JxlTools -> {
                JxlToolsContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.SvgMaker -> {
                SvgMakerContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.FormatConversion -> {
                FormatConversionContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.DocumentScanner -> {
                DocumentScannerContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.ScanQrCode -> {
                ScanQrCodeContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.ImageStacking -> {
                ImageStackingContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.ImageSplitting -> {
                ImageSplitterContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.ColorTools -> {
                ColorToolsContent(
                    component = instance.component,
                    onGoBack = onGoBack
                )
            }

            is NavigationChild.WebpTools -> {
                WebpToolsContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.NoiseGeneration -> {
                NoiseGenerationContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is NavigationChild.CollageMaker -> {
                CollageMakerContent(
                    component = instance.component,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }
        }
    }
    ScreenBasedMaxBrightnessEnforcement(childStack.items.lastOrNull()?.configuration)
}