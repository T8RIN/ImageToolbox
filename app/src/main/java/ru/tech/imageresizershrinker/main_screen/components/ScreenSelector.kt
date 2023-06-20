package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.runtime.Composable
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.pop
import ru.tech.imageresizershrinker.batch_resize_screen.BatchResizeScreen
import ru.tech.imageresizershrinker.bytes_resize_screen.BytesResizeScreen
import ru.tech.imageresizershrinker.compare_screen.CompareScreen
import ru.tech.imageresizershrinker.crop_screen.CropScreen
import ru.tech.imageresizershrinker.delete_exif_screen.DeleteExifScreen
import ru.tech.imageresizershrinker.draw_screen.DrawScreen
import ru.tech.imageresizershrinker.file_cipher_screen.FileCipherScreen
import ru.tech.imageresizershrinker.filters_screen.FiltersScreen
import ru.tech.imageresizershrinker.generate_palette_screen.GeneratePaletteScreen
import ru.tech.imageresizershrinker.image_preview_screen.ImagePreviewScreen
import ru.tech.imageresizershrinker.limits_resize_screen.LimitsResizeScreen
import ru.tech.imageresizershrinker.load_net_image_screen.LoadNetImageScreen
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.pick_color_from_image_screen.PickColorFromImageScreen
import ru.tech.imageresizershrinker.single_resize_screen.SingleResizeScreen
import ru.tech.imageresizershrinker.utils.navigation.Screen
import ru.tech.imageresizershrinker.widget.utils.isNightMode

@Composable
fun ScreenSelector(
    viewModel: MainViewModel
) {
    val navController = viewModel.navController
    val themeState = LocalDynamicThemeState.current
    val appColorTuple = getAppColorTuple(
        defaultColorTuple = viewModel.appColorTuple,
        dynamicColor = viewModel.dynamicColors,
        darkTheme = viewModel.nightMode.isNightMode()
    )
    val onGoBack: () -> Unit = {
        viewModel.updateUris(null)
        themeState.updateColorTuple(appColorTuple)
        navController.apply {
            if (backstack.entries.size > 1) pop()
        }
    }
    AnimatedNavHost(
        controller = navController,
        transitionSpec = NavTransitionSpec
    ) { screen ->
        when (screen) {
            is Screen.Main -> {
                MainScreen(
                    viewModel = viewModel,
                    screenList = viewModel.screenList
                )
            }

            is Screen.SingleResize -> {
                SingleResizeScreen(
                    uriState = screen.uri,
                    onGoBack = onGoBack
                )
            }

            is Screen.BatchResize -> {
                BatchResizeScreen(
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
                    onGoBack = onGoBack
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
                    onGoBack = onGoBack
                )
            }

            is Screen.Filter -> {
                FiltersScreen(
                    uriState = screen.uris,
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
        }
    }
}