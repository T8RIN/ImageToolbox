package ru.tech.imageresizershrinker.main_screen

import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.olshevski.navigation.reimagined.*
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize.BatchResizeScreen
import ru.tech.imageresizershrinker.bytes_resize_screen.BytesResizeScreen
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
import ru.tech.imageresizershrinker.theme.ImageResizerTheme
import ru.tech.imageresizershrinker.utils.IntentUtils.parcelable
import ru.tech.imageresizershrinker.utils.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.utils.getSavingFolder
import ru.tech.imageresizershrinker.utils.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.utils.toUiPath

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "image_resizer")

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    @Suppress("UNCHECKED_CAST")
    private class MainViewModelFactory(
        private val dataStore: DataStore<Preferences>
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(dataStore) as T
    }

    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(dataStore)
    }

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

            val saveFolderUri = viewModel.saveFolderUri

            ImageResizerTheme {
                BackHandler {
                    if (viewModel.shouldShowDialog) showExitDialog = true
                    else finishAffinity()
                }

                Surface(Modifier.fillMaxSize()) {
                    AnimatedNavHost(
                        controller = viewModel.navController,
                        transitionSpec = { _, _, to ->
                            if (to != Screen.Main) {
                                slideInVertically() + fadeIn() with fadeOut()
                            } else {
                                fadeIn() with fadeOut() + slideOutVertically()
                            }
                        }
                    ) { screen ->
                        when (screen) {
                            is Screen.Main -> {
                                MainScreen(
                                    navController = viewModel.navController,
                                    currentFolderUri = saveFolderUri,
                                    onGetNewFolder = {
                                        viewModel.updateSaveFolderUri(it)
                                    }
                                )
                            }
                            is Screen.SingleResize -> {
                                SingleResizeScreen(
                                    uriState = viewModel.uris?.firstOrNull(),
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) },
                                    pushNewUri = viewModel::updateUri,
                                    getSavingFolder = { name, ext ->
                                        getSavingFolder(
                                            treeUri = saveFolderUri,
                                            filename = name,
                                            extension = ext
                                        )
                                    },
                                    savingPathString = saveFolderUri.toUiPath(
                                        context = this@MainActivity,
                                        default = stringResource(R.string.default_folder)
                                    )
                                )
                            }
                            is Screen.PickColorFromImage -> {
                                PickColorFromImageScreen(
                                    uriState = viewModel.uris?.firstOrNull(),
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) },
                                    pushNewUri = viewModel::updateUri
                                )
                            }
                            is Screen.Crop -> {
                                CropScreen(
                                    uriState = viewModel.uris?.firstOrNull(),
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) },
                                    pushNewUri = viewModel::updateUri,
                                    getSavingFolder = { name, ext ->
                                        getSavingFolder(
                                            treeUri = saveFolderUri,
                                            filename = name,
                                            extension = ext
                                        )
                                    },
                                    savingPathString = saveFolderUri.toUiPath(
                                        context = this@MainActivity,
                                        default = stringResource(R.string.default_folder)
                                    )
                                )
                            }
                            is Screen.BatchResize -> {
                                BatchResizeScreen(
                                    uriState = viewModel.uris,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) },
                                    pushNewUris = viewModel::updateUris,
                                    getSavingFolder = { name, ext ->
                                        getSavingFolder(
                                            treeUri = saveFolderUri,
                                            filename = name,
                                            extension = ext
                                        )
                                    },
                                    savingPathString = saveFolderUri.toUiPath(
                                        context = this@MainActivity,
                                        default = stringResource(R.string.default_folder)
                                    )
                                )
                            }
                            is Screen.GeneratePalette -> {
                                GeneratePaletteScreen(
                                    uriState = viewModel.uris?.firstOrNull(),
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) },
                                    pushNewUri = viewModel::updateUri
                                )
                            }
                            Screen.ResizeByBytes -> {
                                BytesResizeScreen(
                                    uriState = viewModel.uris,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) },
                                    pushNewUris = viewModel::updateUris,
                                    getSavingFolder = { name, ext ->
                                        getSavingFolder(
                                            treeUri = saveFolderUri,
                                            filename = name,
                                            extension = ext
                                        )
                                    },
                                    savingPathString = saveFolderUri.toUiPath(
                                        context = this@MainActivity,
                                        default = stringResource(R.string.default_folder)
                                    )
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
                            TextButton(
                                onClick = {
                                    viewModel.hideSelectDialog()
                                    viewModel.updateUris(null)
                                }
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        },
                        text = {
                            if ((viewModel.uris?.size ?: 0) <= 1) {
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
                                    BytesResizePreference(
                                        onClick = {
                                            viewModel.navController.popUpTo { it == Screen.Main }
                                            viewModel.navController.navigate(Screen.ResizeByBytes)
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
                            } else {
                                Column(Modifier.verticalScroll(rememberScrollState())) {
                                    BatchResizePreference(
                                        onClick = {
                                            viewModel.navController.popUpTo { it == Screen.Main }
                                            viewModel.navController.navigate(Screen.BatchResize)
                                            viewModel.hideSelectDialog()
                                        },
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    BytesResizePreference(
                                        onClick = {
                                            viewModel.navController.popUpTo { it == Screen.Main }
                                            viewModel.navController.navigate(Screen.ResizeByBytes)
                                            viewModel.hideSelectDialog()
                                        },
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                }
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

                ToastHost(hostState = viewModel.toastHostState)

                SideEffect { viewModel.tryGetUpdate() }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseImageFromIntent(intent)
    }

    private fun parseImageFromIntent(intent: Intent?) {
        if (intent?.type != null && viewModel.uris == null) {
            viewModel.shouldShowExitDialog(false)
        }
        if (intent?.type?.startsWith("image/") == true) {
            when (intent.action) {
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