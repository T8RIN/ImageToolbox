package ru.tech.imageresizershrinker.main_screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import ru.tech.imageresizershrinker.batch_resize_screen.BatchResizeScreen
import ru.tech.imageresizershrinker.bytes_resize_screen.BytesResizeScreen
import ru.tech.imageresizershrinker.compare_screen.CompareScreen
import ru.tech.imageresizershrinker.crop_screen.CropScreen
import ru.tech.imageresizershrinker.delete_exif_screen.DeleteExifScreen
import ru.tech.imageresizershrinker.filters_screen.FiltersScreen
import ru.tech.imageresizershrinker.generate_palette_screen.GeneratePaletteScreen
import ru.tech.imageresizershrinker.image_preview_screen.ImagePreviewScreen
import ru.tech.imageresizershrinker.load_net_image_screen.LoadNetImageScreen
import ru.tech.imageresizershrinker.main_screen.components.AppExitDialog
import ru.tech.imageresizershrinker.main_screen.components.EditPresetsSheet
import ru.tech.imageresizershrinker.main_screen.components.MainScreen
import ru.tech.imageresizershrinker.main_screen.components.NavTransitionSpec
import ru.tech.imageresizershrinker.main_screen.components.PermissionDialog
import ru.tech.imageresizershrinker.main_screen.components.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.main_screen.components.UpdateSheet
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.pick_color_from_image_screen.PickColorFromImageScreen
import ru.tech.imageresizershrinker.single_resize_screen.SingleResizeScreen
import ru.tech.imageresizershrinker.theme.Emoji
import ru.tech.imageresizershrinker.theme.ImageResizerTheme
import ru.tech.imageresizershrinker.theme.allIcons
import ru.tech.imageresizershrinker.theme.blend
import ru.tech.imageresizershrinker.utils.LocalConfettiController
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.parseImageFromIntent
import ru.tech.imageresizershrinker.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.utils.navigation.Screen
import ru.tech.imageresizershrinker.utils.storage.FileParams
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.rememberFileController
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.ToastHost
import ru.tech.imageresizershrinker.widget.activity.M3Activity
import ru.tech.imageresizershrinker.widget.rememberToastHostState
import ru.tech.imageresizershrinker.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.isNightMode
import ru.tech.imageresizershrinker.widget.utils.rememberSettingsState
import ru.tech.imageresizershrinker.widget.utils.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.widget.utils.toAlignment
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : M3Activity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseImage(intent)

        setContentWithWindowSizeClass {
            var showExitDialog by rememberSaveable { mutableStateOf(false) }
            val editPresetsState = rememberSaveable { mutableStateOf(false) }

            CompositionLocalProvider(
                LocalToastHost provides viewModel.toastHostState,
                LocalSettingsState provides rememberSettingsState(
                    isNightMode = viewModel.nightMode.isNightMode(),
                    isDynamicColors = viewModel.dynamicColors,
                    allowChangeColorByImage = viewModel.allowImageMonet,
                    isAmoledMode = viewModel.amoledMode,
                    appColorTuple = viewModel.appColorTuple,
                    borderWidth = animateDpAsState(viewModel.borderWidth.dp).value,
                    presets = viewModel.localPresets,
                    fabAlignment = viewModel.alignment.toAlignment(),
                    selectedEmoji = Emoji.allIcons.getOrNull(viewModel.selectedEmoji),
                    imagePickerModeInt = viewModel.imagePickerModeInt,
                    emojisCount = viewModel.emojisCount
                ),
                LocalNavController provides viewModel.navController,
                LocalEditPresetsState provides editPresetsState,
                LocalConfettiController provides rememberToastHostState(),
                LocalFileController provides rememberFileController(
                    LocalContext.current,
                    FileParams(
                        treeUri = viewModel.saveFolderUri,
                        filenamePrefix = viewModel.filenamePrefix,
                        addSizeInFilename = viewModel.addSizeInFilename,
                        addOriginalFilename = viewModel.addOriginalFilename,
                        addSequenceNumber = viewModel.addSequenceNumber,
                    )
                )
            ) {
                val showSelectSheet = rememberSaveable(viewModel.showSelectDialog) {
                    mutableStateOf(viewModel.showSelectDialog)
                }
                val showUpdateSheet = rememberSaveable(viewModel.showUpdateDialog) {
                    mutableStateOf(viewModel.showUpdateDialog)
                }
                LaunchedEffect(showSelectSheet.value) {
                    if (!showSelectSheet.value) {
                        kotlinx.coroutines.delay(600)
                        viewModel.hideSelectDialog()
                        viewModel.updateUris(null)
                    }
                }
                LaunchedEffect(showUpdateSheet.value) {
                    if (!showUpdateSheet.value) {
                        kotlinx.coroutines.delay(600)
                        viewModel.cancelledUpdate()
                    }
                }
                val navController = LocalNavController.current
                ImageResizerTheme {
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

                    val tiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

                    if (!tiramisu) {
                        BackHandler {
                            if (viewModel.shouldShowDialog) showExitDialog = true
                            else finishAffinity()
                        }
                    }

                    Surface(Modifier.fillMaxSize()) {
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
                            }
                        }

                        EditPresetsSheet(
                            editPresetsState = editPresetsState,
                            updatePresets = viewModel::updatePresets
                        )
                        ProcessImagesPreferenceSheet(
                            uris = viewModel.uris ?: emptyList(),
                            visible = showSelectSheet
                        )
                    }

                    AppExitDialog(
                        onDismiss = { showExitDialog = false },
                        visible = showExitDialog && !tiramisu
                    )

                    UpdateSheet(
                        tag = viewModel.tag,
                        changelog = viewModel.changelog,
                        visible = showUpdateSheet
                    )

                    ToastHost(
                        hostState = LocalConfettiController.current,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        toast = {
                            val primary = MaterialTheme.colorScheme.primary
                            KonfettiView(
                                modifier = Modifier.fillMaxSize(),
                                parties = remember { particles(primary) }
                            )
                        }
                    )

                    ToastHost(hostState = LocalToastHost.current)

                    SideEffect { viewModel.tryGetUpdate(showDialog = viewModel.showDialogOnStartUp) }

                    PermissionDialog()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseImage(intent)
    }

    private fun parseImage(intent: Intent?) {
        parseImageFromIntent(
            onStart = {
                viewModel.hideSelectDialog()
            },
            onColdStart = {
                viewModel.shouldShowExitDialog(false)
            },
            onGetUris = {
                viewModel.updateUris(it)
            },
            showToast = { message, icon ->
                viewModel.showToast(message = message, icon = icon)
            },
            navigate = {
                viewModel.navController.navigate(it)
            },
            notHasUris = viewModel.uris.isNullOrEmpty(),
            intent = intent
        )
    }
}

private fun particles(primary: Color) = listOf(
    Party(
        speed = 0f,
        maxSpeed = 15f,
        damping = 0.9f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(primary)
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 45,
        spread = 60,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(primary)
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 1.0)
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 135,
        spread = 60,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(primary)
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(1.0, 1.0)
    )
)
