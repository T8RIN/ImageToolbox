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
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.ImageResizerTheme
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.crash_screen.CrashActivity
import ru.tech.imageresizershrinker.crash_screen.GlobalExceptionHandler
import ru.tech.imageresizershrinker.main_screen.components.MainScreen
import ru.tech.imageresizershrinker.main_screen.components.PickColorPreference
import ru.tech.imageresizershrinker.main_screen.components.SingleResizePreference
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.pcik_color.PickColorScreen
import ru.tech.imageresizershrinker.resize_screen.SingleResizeScreen
import ru.tech.imageresizershrinker.resize_screen.components.*
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
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
                    AnimatedNavHost(controller = viewModel.navController) {
                        when (it) {
                            is Screen.Main -> {
                                MainScreen(viewModel.navController)
                            }
                            is Screen.SingleResize -> {
                                SingleResizeScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController
                                )
                            }
                            Screen.PickColor -> {
                                PickColorScreen(
                                    uriState = viewModel.uri,
                                    navController = viewModel.navController
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
                            Column {
                                SingleResizePreference(
                                    onClick = {
                                        viewModel.navController.navigate(Screen.SingleResize)
                                        viewModel.hideSelectDialog()
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PickColorPreference(
                                    onClick = {
                                        viewModel.navController.navigate(Screen.PickColor)
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
        }
    }
}

sealed class Screen {
    object Main : Screen()
    object SingleResize : Screen()
    object PickColor : Screen()
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
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