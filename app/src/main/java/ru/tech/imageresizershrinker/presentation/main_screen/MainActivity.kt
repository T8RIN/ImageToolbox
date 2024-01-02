package ru.tech.imageresizershrinker.presentation.main_screen

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.navigate
import nl.dionsegijn.konfetti.compose.KonfettiView
import ru.tech.imageresizershrinker.coreui.model.toUiState
import ru.tech.imageresizershrinker.coreui.theme.ImageToolboxTheme
import ru.tech.imageresizershrinker.coreui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.coreui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.coreui.utils.helper.ContextUtils.parseImageFromIntent
import ru.tech.imageresizershrinker.coreui.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSliderInit
import ru.tech.imageresizershrinker.coreui.widget.haptics.customHapticFeedback
import ru.tech.imageresizershrinker.coreui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.coreui.widget.other.ToastHost
import ru.tech.imageresizershrinker.coreui.widget.other.rememberToastHostState
import ru.tech.imageresizershrinker.coreui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalImageLoader
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.coreui.widget.utils.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.presentation.main_screen.components.AppExitDialog
import ru.tech.imageresizershrinker.presentation.main_screen.components.EditPresetsSheet
import ru.tech.imageresizershrinker.presentation.main_screen.components.FirstLaunchSetupDialog
import ru.tech.imageresizershrinker.presentation.main_screen.components.JxlWarning
import ru.tech.imageresizershrinker.presentation.main_screen.components.PermissionDialog
import ru.tech.imageresizershrinker.presentation.main_screen.components.ScreenSelector
import ru.tech.imageresizershrinker.coreui.widget.UpdateSheet
import ru.tech.imageresizershrinker.presentation.main_screen.components.particles
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.coreui.widget.activity.M3Activity

@AndroidEntryPoint
class MainActivity : M3Activity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseImage(intent)

        setContentWithWindowSizeClass {
            var showExitDialog by rememberSaveable { mutableStateOf(false) }
            val editPresetsState = rememberSaveable { mutableStateOf(false) }

            EnhancedSliderInit()

            CompositionLocalProvider(
                LocalToastHost provides viewModel.toastHostState,
                LocalSettingsState provides viewModel.settingsState.toUiState(),
                LocalNavController provides viewModel.navController,
                LocalEditPresetsState provides editPresetsState,
                LocalConfettiController provides rememberToastHostState(),
                LocalImageLoader provides viewModel.imageLoader,
                LocalHapticFeedback provides customHapticFeedback(viewModel.settingsState.hapticsStrength)
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
                val conf = LocalConfiguration.current
                val systemUiController = rememberSystemUiController()
                LaunchedEffect(conf.orientation) {
                    if (conf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        systemUiController.isNavigationBarVisible = false
                        systemUiController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    } else {
                        systemUiController.isNavigationBarVisible = true
                        systemUiController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    }
                }
                ImageToolboxTheme {
                    val tiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

                    if (!tiramisu) {
                        BackHandler {
                            if (viewModel.shouldShowDialog) showExitDialog = true
                            else finishAffinity()
                        }
                    }

                    Surface(Modifier.fillMaxSize()) {
                        ScreenSelector(viewModel)

                        EditPresetsSheet(
                            editPresetsState = editPresetsState,
                            updatePresets = viewModel::setPresets
                        )
                        ProcessImagesPreferenceSheet(
                            uris = viewModel.hasPdfUri?.let {
                                listOf(it)
                            } ?: viewModel.uris ?: emptyList(),
                            hasPdf = viewModel.hasPdfUri != null,
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

                    ToastHost(
                        hostState = LocalToastHost.current
                    )

                    SideEffect {
                        viewModel.tryGetUpdate(
                            installedFromMarket = isInstalledFromPlayStore()
                        )
                    }
                    JxlWarning()

                    FirstLaunchSetupDialog(
                        toggleShowUpdateDialog = viewModel::toggleShowUpdateDialog,
                        toggleAllowBetas = viewModel::toggleAllowBetas
                    )

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
            onHasPdfUri = {
                viewModel.updateHasPdfUri(it)
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