package ru.tech.imageresizershrinker.main_screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import ru.tech.imageresizershrinker.ImageResizerTheme
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize.BatchResizeScreen
import ru.tech.imageresizershrinker.crash_screen.CrashActivity
import ru.tech.imageresizershrinker.crash_screen.GlobalExceptionHandler
import ru.tech.imageresizershrinker.crop_screen.CropScreen
import ru.tech.imageresizershrinker.generate_palette.GeneratePaletteScreen
import ru.tech.imageresizershrinker.main_screen.components.*
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.pick_color.PickColorScreen
import ru.tech.imageresizershrinker.resize_screen.SingleResizeScreen
import ru.tech.imageresizershrinker.resize_screen.components.*
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
                }
                BackHandler { showExitDialog = true }

                Surface(Modifier.fillMaxSize()) {
                    AnimatedNavHost(controller = viewModel.navController) { screen ->
                        when (screen) {
                            is Screen.Main -> {
                                MainScreen(viewModel.navController)
                            }
                            is Screen.SingleResize -> {
                                SingleResizeScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) }
                                )
                            }
                            is Screen.PickColor -> {
                                PickColorScreen(
                                    uriState = screen.uri.takeIf { it != null } ?: viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) }
                                )
                            }
                            is Screen.Crop -> {
                                CropScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) }
                                )
                            }
                            is Screen.BatchResize -> {
                                BatchResizeScreen(
                                    uriState = viewModel.uris,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUris(null) }
                                )
                            }
                            is Screen.GeneratePalette -> {
                                GeneratePaletteScreen(
                                    uriState = screen.uri.takeIf { it != null } ?: viewModel.uri,
                                    navController = viewModel.navController,
                                    onGoBack = { viewModel.updateUri(null) }
                                )
                            }
                        }
                    }
                }

                if (viewModel.showSelectDialog) {
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
                                        viewModel.navController.navigate(Screen.SingleResize)
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CropPreference(
                                    onClick = {
                                        viewModel.navController.navigate(Screen.Crop)
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PickColorPreference(
                                    onClick = {
                                        viewModel.navController.navigate(
                                            Screen.PickColor(viewModel.uri)
                                        )
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                GeneratePalettePreference(
                                    onClick = {
                                        viewModel.navController.navigate(
                                            Screen.GeneratePalette(viewModel.uri)
                                        )
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                            }
                        }
                    )
                }
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

@Parcelize
sealed class Screen : Parcelable {
    object Main : Screen()
    object SingleResize : Screen()
    object BatchResize : Screen()

    @Parcelize
    class PickColor(
        val uri: Uri? = null
    ) : Screen()

    @Parcelize
    class GeneratePalette(
        val uri: Uri? = null
    ) : Screen()

    object Crop : Screen()
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

fun Modifier.block(shape: Shape = RoundedCornerShape(16.dp)) = composed {
    background(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = shape
    ).padding(4.dp)
}

fun Activity.requestPermission() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        1
    )
}

fun Context.isExternalStorageWritable(): Boolean {
    return if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
    else ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}