package ru.tech.imageresizershrinker.main_screen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material.icons.outlined.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.text.isDigitsOnly
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize_screen.BatchResizeScreen
import ru.tech.imageresizershrinker.bytes_resize_screen.BytesResizeScreen
import ru.tech.imageresizershrinker.common.APP_RELEASES
import ru.tech.imageresizershrinker.compare_screen.CompareScreen
import ru.tech.imageresizershrinker.crop_screen.CropScreen
import ru.tech.imageresizershrinker.delete_exif_screen.DeleteExifScreen
import ru.tech.imageresizershrinker.generate_palette_screen.GeneratePaletteScreen
import ru.tech.imageresizershrinker.image_preview_screen.ImagePreviewScreen
import ru.tech.imageresizershrinker.main_screen.components.MainScreen
import ru.tech.imageresizershrinker.main_screen.components.PermissionDialog
import ru.tech.imageresizershrinker.main_screen.components.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.pick_color_from_image_screen.PickColorFromImageScreen
import ru.tech.imageresizershrinker.single_resize_screen.SingleResizeScreen
import ru.tech.imageresizershrinker.theme.Emoji
import ru.tech.imageresizershrinker.theme.ImageResizerTheme
import ru.tech.imageresizershrinker.theme.allIcons
import ru.tech.imageresizershrinker.theme.blend
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.LocalConfettiController
import ru.tech.imageresizershrinker.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.utils.helper.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.utils.navigation.Screen
import ru.tech.imageresizershrinker.utils.storage.FileParams
import ru.tech.imageresizershrinker.utils.storage.LocalFileController
import ru.tech.imageresizershrinker.utils.storage.rememberFileController
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.ToastHost
import ru.tech.imageresizershrinker.widget.activity.M3Activity
import ru.tech.imageresizershrinker.widget.rememberToastHostState
import ru.tech.imageresizershrinker.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.widget.text.HtmlText
import ru.tech.imageresizershrinker.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.widget.utils.isNightMode
import ru.tech.imageresizershrinker.widget.utils.rememberSettingsState
import ru.tech.imageresizershrinker.widget.utils.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.widget.utils.toAlignment
import java.util.concurrent.TimeUnit


@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : M3Activity() {

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseImageFromIntent(intent)

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
                val settingsState = LocalSettingsState.current

                val showSelectSheet =
                    rememberSaveable(viewModel.showSelectDialog) { mutableStateOf(viewModel.showSelectDialog) }
                LaunchedEffect(showSelectSheet.value) {
                    if (!showSelectSheet.value) {
                        kotlinx.coroutines.delay(600)
                        viewModel.hideSelectDialog()
                        viewModel.updateUris(null)
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
                            transitionSpec = { action, _, _ ->
                                fun <T> animationSpec(
                                    duration: Int = 500,
                                    delay: Int = 0
                                ) = tween<T>(
                                    durationMillis = duration,
                                    delayMillis = delay,
                                    easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
                                )
                                if (action == NavAction.Pop) {
                                    slideInHorizontally(
                                        animationSpec()
                                    ) { -it / 3 } + fadeIn(
                                        animationSpec(500)
                                    ) togetherWith slideOutHorizontally(
                                        animationSpec()
                                    ) { it / 3 } + fadeOut(
                                        animationSpec(150)
                                    )
                                } else {
                                    slideInHorizontally(
                                        animationSpec()
                                    ) { it / 3 } + fadeIn(
                                        animationSpec(),
                                    ) togetherWith slideOutHorizontally(
                                        animationSpec()
                                    ) { -it / 3 } + fadeOut(
                                        animationSpec(250)
                                    )
                                }
                            }
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
                            }
                        }

                        SimpleSheet(
                            visible = editPresetsState,
                            title = {
                                TitleItem(
                                    text = stringResource(R.string.presets),
                                    icon = Icons.Rounded.PhotoSizeSelectSmall
                                )
                            },
                            sheetContent = {
                                val data = settingsState.presets
                                Box {
                                    AnimatedContent(
                                        targetState = data,
                                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                    ) { list ->
                                        FlowRow(
                                            Modifier
                                                .align(Alignment.Center)
                                                .fillMaxWidth()
                                                .padding(top = 8.dp, bottom = 8.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            list.forEach {
                                                OutlinedIconButton(
                                                    onClick = {
                                                        if (list.size > 7) {
                                                            viewModel.updatePresets(list - it)
                                                        }
                                                    },
                                                    border = BorderStroke(
                                                        settingsState.borderWidth.coerceAtLeast(1.dp),
                                                        MaterialTheme.colorScheme.outlineVariant
                                                    ),
                                                    colors = IconButtonDefaults.outlinedIconButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                            alpha = 0.3f
                                                        ),
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    )
                                                ) {
                                                    AutoSizeText(it.toString())
                                                }
                                            }
                                            var expanded by remember { mutableStateOf(false) }
                                            OutlinedIconButton(
                                                onClick = {
                                                    expanded = true
                                                },
                                                border = BorderStroke(
                                                    max(
                                                        settingsState.borderWidth,
                                                        1.dp
                                                    ),
                                                    MaterialTheme.colorScheme.outlineVariant
                                                ),
                                                colors = IconButtonDefaults.outlinedIconButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                        alpha = 0.3f
                                                    ),
                                                    contentColor = MaterialTheme.colorScheme.onSurface
                                                )
                                            ) {
                                                Icon(Icons.Rounded.AddCircle, null)
                                            }
                                            if (expanded) {
                                                var value by remember { mutableStateOf("") }
                                                AlertDialog(
                                                    modifier = Modifier.alertDialog(),
                                                    onDismissRequest = { expanded = false },
                                                    icon = {
                                                        Icon(
                                                            Icons.Outlined.PhotoSizeSelectSmall,
                                                            null
                                                        )
                                                    },
                                                    title = {
                                                        Text(stringResource(R.string.presets))
                                                    },
                                                    text = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            OutlinedTextField(
                                                                shape = RoundedCornerShape(16.dp),
                                                                value = value,
                                                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                                                    textAlign = TextAlign.Center
                                                                ),
                                                                maxLines = 1,
                                                                keyboardOptions = KeyboardOptions(
                                                                    keyboardType = KeyboardType.Number
                                                                ),
                                                                onValueChange = {
                                                                    if (it.isDigitsOnly()) {
                                                                        value = it
                                                                    }
                                                                }
                                                            )
                                                            Text(
                                                                text = "%",
                                                                style = MaterialTheme.typography.titleMedium.copy(
                                                                    textAlign = TextAlign.Center
                                                                )
                                                            )
                                                        }
                                                    },
                                                    confirmButton = {
                                                        OutlinedButton(
                                                            onClick = {
                                                                viewModel.updatePresets(
                                                                    list + (value.toIntOrNull()
                                                                        ?: 0).coerceIn(10..500)
                                                                )
                                                                expanded = false
                                                            },
                                                            colors = ButtonDefaults.outlinedButtonColors(
                                                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                                    alpha = if (settingsState.isNightMode) 0.5f
                                                                    else 1f
                                                                ),
                                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                            ),
                                                            border = BorderStroke(
                                                                settingsState.borderWidth,
                                                                MaterialTheme.colorScheme.outlineVariant(
                                                                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                                )
                                                            ),
                                                        ) {
                                                            Text(stringResource(R.string.add))
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Divider(Modifier.align(Alignment.TopCenter))
                                    Divider(Modifier.align(Alignment.BottomCenter))
                                }
                            },
                            confirmButton = {
                                OutlinedButton(
                                    onClick = { editPresetsState.value = false },
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant()
                                    )
                                ) {
                                    Text(stringResource(R.string.close))
                                }
                            }
                        )
                        ProcessImagesPreferenceSheet(
                            uris = viewModel.uris ?: emptyList(),
                            visible = showSelectSheet
                        )
                    }

                    if (showExitDialog && !tiramisu) {
                        AlertDialog(
                            modifier = Modifier.alertDialog(),
                            onDismissRequest = { showExitDialog = false },
                            dismissButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                    ),
                                    onClick = {
                                        finishAffinity()
                                    }
                                ) {
                                    Text(stringResource(R.string.close))
                                }
                            },
                            confirmButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ), onClick = { showExitDialog = false }) {
                                    Text(stringResource(R.string.stay))
                                }
                            },
                            title = { Text(stringResource(R.string.app_closing)) },
                            text = {
                                Text(
                                    stringResource(R.string.app_closing_sub),
                                    textAlign = TextAlign.Center
                                )
                            },
                            icon = { Icon(Icons.Outlined.DoorBack, null) }
                        )
                    } else if (viewModel.showUpdateDialog) {
                        AlertDialog(
                            modifier = Modifier.alertDialog(),
                            onDismissRequest = { },
                            icon = {
                                Icon(Icons.Rounded.Download, null)
                            },
                            title = { Text(stringResource(R.string.new_version, viewModel.tag)) },
                            text = {
                                Box {
                                    Column(Modifier.verticalScroll(rememberScrollState())) {
                                        Spacer(Modifier.height(16.dp))
                                        HtmlText(
                                            viewModel.nightMode.isNightMode(),
                                            viewModel.changelog
                                        )
                                    }
                                    Divider(Modifier.align(Alignment.TopCenter))
                                    Divider(Modifier.align(Alignment.BottomCenter))
                                }
                            },
                            confirmButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ),
                                    onClick = {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("$APP_RELEASES/tag/${viewModel.tag}")
                                            )
                                        )
                                    }
                                ) {
                                    Text(stringResource(id = R.string.update))
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                    ), onClick = { viewModel.cancelledUpdate() }
                                ) {
                                    Text(stringResource(id = R.string.close))
                                }
                            }
                        )
                    }

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
        parseImageFromIntent(intent)
    }

    private fun parseImageFromIntent(intent: Intent?) {
        viewModel.hideSelectDialog()
        if (intent?.type != null && viewModel.uris == null) {
            viewModel.shouldShowExitDialog(false)
        }
        if (intent?.type?.startsWith("image/") == true) {

            when (intent.action) {
                Intent.ACTION_VIEW -> {
                    val data = intent.data
                    val clipData = intent.clipData
                    if (clipData != null) {
                        viewModel.navController.navigate(
                            Screen.ImagePreview(
                                List(
                                    size = clipData.itemCount,
                                    init = {
                                        clipData.getItemAt(it).uri
                                    }
                                )
                            )
                        )
                    } else if (data != null) {
                        viewModel.navController.navigate(Screen.ImagePreview(listOf(data)))
                    }
                }

                Intent.ACTION_SEND -> {
                    intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                        viewModel.updateUris(listOf(it))
                    }
                }

                Intent.ACTION_SEND_MULTIPLE -> {
                    intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                        viewModel.updateUris(it)
                    }
                }

                else -> {
                    intent.data?.let { viewModel.updateUris(listOf(it)) }
                }
            }
        } else if (intent?.type != null) {
            viewModel.showToast(
                message = getString(R.string.unsupported_type, intent.type),
                icon = Icons.Rounded.ErrorOutline
            )
        }
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
