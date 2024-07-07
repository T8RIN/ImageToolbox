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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavTransitionQueueing
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.animation.NavigationTransition
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.navigation.currentDestination
import ru.tech.imageresizershrinker.core.ui.utils.navigation.navigateNew
import ru.tech.imageresizershrinker.core.ui.utils.navigation.safePop
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.ApngToolsContent
import ru.tech.imageresizershrinker.feature.bytes_resize.presentation.BytesResizeContent
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
import ru.tech.imageresizershrinker.feature.zip.presentation.ZipContent
import ru.tech.imageresizershrinker.image_splitting.presentation.ImageSplitterContent

@Composable
internal fun ScreenSelector(
    viewModel: RootViewModel,
    onRegisterScreenOpen: (Screen) -> Unit,
) {
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
        navController.safePop()
        scope.launch {
            delay(350L) //delay for screen anim
            themeState.updateColorTuple(appColorTuple)
        }
    }

    val onNavigate: (Screen) -> Unit = { destination ->
        navController.navigate(destination)
        onRegisterScreenOpen(destination)
    }

    AnimatedNavHost(
        controller = navController,
        transitionQueueing = NavTransitionQueueing.ConflateQueued,
        transitionSpec = NavigationTransition
    ) { screen ->
        when (screen) {
            is Screen.Settings -> {
                SettingsContent(
                    onTryGetUpdate = viewModel::tryGetUpdate,
                    updateAvailable = viewModel.updateAvailable,
                    onGoBack = onGoBack,
                    onNavigateToEasterEgg = {
                        navController.navigateNew(Screen.EasterEgg).also {
                            if (it) onRegisterScreenOpen(Screen.EasterEgg)
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigateNew(Screen.Settings).also {
                            if (it) onRegisterScreenOpen(Screen.Settings)
                        }
                    }
                )
            }

            is Screen.EasterEgg -> {
                EasterEggContent(onGoBack = onGoBack)
            }

            is Screen.Main -> {
                MainContent(
                    onTryGetUpdate = viewModel::tryGetUpdate,
                    updateAvailable = viewModel.updateAvailable,
                    updateUris = viewModel::updateUris,
                    onNavigateToSettings = {
                        navController.navigateNew(Screen.Settings).also {
                            if (it) onRegisterScreenOpen(Screen.Settings)
                        }
                    },
                    onNavigateToScreenWithPopUpTo = { destination ->
                        navController.popUpTo { it == Screen.Main }
                        onNavigate(destination)
                    },
                    onNavigateToEasterEgg = {
                        navController.navigateNew(Screen.EasterEgg).also {
                            if (it) onRegisterScreenOpen(Screen.EasterEgg)
                        }
                    }
                )
            }

            is Screen.SingleEdit -> {
                SingleEditContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.ResizeAndConvert -> {
                ResizeAndConvertContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.DeleteExif -> {
                DeleteExifContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.ResizeByBytes -> {
                BytesResizeContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.Crop -> {
                CropContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.PickColorFromImage -> {
                PickColorFromImageContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.ImagePreview -> {
                ImagePreviewContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.GeneratePalette -> {
                GeneratePaletteContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.Compare -> {
                CompareContent(
                    comparableUris = screen.uris
                        ?.takeIf { it.size == 2 }
                        ?.let { it[0] to it[1] },
                    onGoBack = onGoBack
                )
            }

            is Screen.LoadNetImage -> {
                LoadNetImageContent(
                    url = screen.url,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.Filter -> {
                FiltersContent(
                    type = screen.type,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.LimitResize -> {
                LimitsResizeContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.Draw -> {
                DrawContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.Cipher -> {
                CipherContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.EraseBackground -> {
                EraseBackgroundContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.ImageStitching -> {
                ImageStitchingContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.PdfTools -> {
                PdfToolsContent(
                    type = screen.type,
                    onGoBack = onGoBack
                )
            }

            is Screen.RecognizeText -> {
                RecognizeTextContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.GradientMaker -> {
                GradientMakerContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.Watermarking -> {
                WatermarkingContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.GifTools -> {
                GifToolsContent(
                    typeState = screen.type,
                    onGoBack = onGoBack
                )
            }

            is Screen.ApngTools -> {
                ApngToolsContent(
                    typeState = screen.type,
                    onGoBack = onGoBack
                )
            }

            is Screen.Zip -> {
                ZipContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack
                )
            }

            is Screen.JxlTools -> {
                JxlToolsContent(
                    typeState = screen.type,
                    onGoBack = onGoBack
                )
            }

            is Screen.SvgMaker -> {
                SvgMakerContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack
                )
            }

            is Screen.FormatConversion -> {
                FormatConversionContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.DocumentScanner -> {
                DocumentScannerContent(onGoBack = onGoBack)
            }

            is Screen.ScanQrCode -> {
                ScanQrCodeContent(
                    qrCodeContent = screen.qrCodeContent,
                    onGoBack = onGoBack
                )
            }

            is Screen.ImageStacking -> {
                ImageStackingContent(
                    uriState = screen.uris,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }

            is Screen.ImageSplitting -> {
                ImageSplitterContent(
                    uriState = screen.uri,
                    onGoBack = onGoBack,
                    onNavigate = onNavigate
                )
            }
        }
    }
    ScreenBasedMaxBrightnessEnforcement(navController.currentDestination())
}