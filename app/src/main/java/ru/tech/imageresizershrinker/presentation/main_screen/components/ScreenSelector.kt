package ru.tech.imageresizershrinker.presentation.main_screen.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.coreui.utils.helper.ContextUtils.findActivity
import ru.tech.imageresizershrinker.coreui.utils.navigation.Screen
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.LoadNetImageScreen
import ru.tech.imageresizershrinker.feature.bytes_resize.presentation.BytesResizeScreen
import ru.tech.imageresizershrinker.feature.compare.presentation.CompareScreen
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.DeleteExifScreen
import ru.tech.imageresizershrinker.presentation.draw_screen.DrawScreen
import ru.tech.imageresizershrinker.presentation.erase_background_screen.EraseBackgroundScreen
import ru.tech.imageresizershrinker.feature.cipher.presentation.FileCipherScreen
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.LimitsResizeScreen
import ru.tech.imageresizershrinker.presentation.filters_screen.FiltersScreen
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.GeneratePaletteScreen
import ru.tech.imageresizershrinker.feature.image_preview.presentation.ImagePreviewScreen
import ru.tech.imageresizershrinker.presentation.image_stitching_screen.ImageStitchingScreen
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.pdf_tools_screen.PdfToolsScreen
import ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.PickColorFromImageScreen
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.ResizeAndConvertScreen
import ru.tech.imageresizershrinker.presentation.single_edit_screen.SingleEditScreen

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
                MainScreen(viewModel = viewModel)
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
                ru.tech.imageresizershrinker.feature.crop.presentation.CropScreen(
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
        }
    }
}