package ru.tech.imageresizershrinker.main_screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dev.olshevski.navigation.reimagined.*
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.ImageResizerTheme
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize.BatchResizeScreen
import ru.tech.imageresizershrinker.crash_screen.CrashActivity
import ru.tech.imageresizershrinker.crash_screen.GlobalExceptionHandler
import ru.tech.imageresizershrinker.crop_screen.CropScreen
import ru.tech.imageresizershrinker.generate_palette.GeneratePaletteScreen
import ru.tech.imageresizershrinker.main_screen.components.*
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.pick_color_from_image.PickColorFromImageScreen
import ru.tech.imageresizershrinker.resize_screen.SingleResizeScreen
import ru.tech.imageresizershrinker.resize_screen.components.*
import ru.tech.imageresizershrinker.theme.Github
import ru.tech.imageresizershrinker.utils.IntentUtils.parcelable
import ru.tech.imageresizershrinker.utils.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.utils.setContentWithWindowSizeClass

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)

        parseImageFromIntent(intent)

        setContentWithWindowSizeClass {
            var showExitDialog by rememberSaveable { mutableStateOf(false) }
            ImageResizerTheme {
                BackHandler { showExitDialog = true }

                Surface(Modifier.fillMaxSize()) {
                    AnimatedNavHost(
                        controller = viewModel.navController,
                        transitionSpec = { action, from, to ->
                            if (to != Screen.Main) {
                                slideInVertically() + fadeIn() with fadeOut()
                            } else {
                                fadeIn() with fadeOut() + slideOutVertically()
                            }
                        }
                    ) { screen ->
                        when (screen) {
                            is Screen.Main -> {
                                MainScreen(viewModel.navController)
                            }
                            is Screen.SingleResize -> {
                                SingleResizeScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) },
                                    pushNewUri = viewModel::updateUri
                                )
                            }
                            is Screen.PickColorFromImage -> {
                                PickColorFromImageScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) },
                                    pushNewUri = viewModel::updateUri
                                )
                            }
                            is Screen.Crop -> {
                                CropScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) },
                                    pushNewUri = viewModel::updateUri
                                )
                            }
                            is Screen.BatchResize -> {
                                BatchResizeScreen(
                                    uriState = viewModel.uris,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) },
                                    pushNewUris = viewModel::updateUris
                                )
                            }
                            is Screen.GeneratePalette -> {
                                GeneratePaletteScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) },
                                    pushNewUri = viewModel::updateUri
                                )
                            }
                        }
                    }
                }

                if (showExitDialog) {
                    AlertDialog(
                        onDismissRequest = { showExitDialog = false },
                        dismissButton = {
                            FilledTonalButton(
                                onClick = {
                                    finishAffinity()
                                }
                            ) {
                                Text(stringResource(R.string.close))
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showExitDialog = false }) {
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
                } else if (viewModel.showSelectDialog) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { stringResource(R.string.image) },
                        confirmButton = {
                            TextButton(onClick = { viewModel.updateUri(null) }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        },
                        text = {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                SingleResizePreference(
                                    onClick = {
                                        viewModel.navController.popUpTo { it == Screen.Main }
                                        viewModel.navController.navigate(Screen.SingleResize)
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CropPreference(
                                    onClick = {
                                        viewModel.navController.popUpTo { it == Screen.Main }
                                        viewModel.navController.navigate(Screen.Crop)
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PickColorPreference(
                                    onClick = {
                                        viewModel.navController.popUpTo { it == Screen.Main }
                                        viewModel.navController.navigate(Screen.PickColorFromImage)
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                GeneratePalettePreference(
                                    onClick = {
                                        viewModel.navController.popUpTo { it == Screen.Main }
                                        viewModel.navController.navigate(Screen.GeneratePalette)
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                            }
                        }
                    )
                } else if (viewModel.showUpdateDialog) {
                    AlertDialog(
                        onDismissRequest = {},
                        icon = {
                            Icon(Icons.Rounded.Github, null)
                        },
                        title = { Text(stringResource(R.string.new_version, viewModel.tag)) },
                        text = {
                            Text(stringResource(R.string.new_version_sub, BuildConfig.VERSION_NAME))
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://github.com/t8rin/imageresizer/releases/tag/${viewModel.tag}")
                                        )
                                    )
                                }
                            ) {
                                Text(stringResource(id = R.string.update))
                            }
                        },
                        dismissButton = {
                            FilledTonalButton(onClick = { viewModel.cancelledUpdate() }) {
                                Text(stringResource(id = R.string.close))
                            }
                        }
                    )
                }

                SideEffect { viewModel.tryGetUpdate() }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseImageFromIntent(intent)
    }

    private fun parseImageFromIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                        viewModel.updateUri(it)
                    }
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                if (intent.type?.startsWith("image/") == true) {
                    intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                        viewModel.updateUris(it)
                    }
                }
            }
        }
    }
}

