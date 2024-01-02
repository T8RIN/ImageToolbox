package ru.tech.imageresizershrinker.coreui.widget.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import ru.tech.imageresizershrinker.coreui.utils.helper.ContextUtils.findActivity

@Composable
inline fun <reified VM : ViewModel> ScopedViewModelContainer(
    crossinline content: @Composable VM.(disposable: @Composable () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController(startDestination = 0)
    NavHost(navController) { nav ->
        with(hiltViewModel<VM>()) {
            content {
                DisposableEffect(Unit) {
                    onDispose {
                        if (context.findActivity()?.isChangingConfigurations == false) {
                            navController.navigate(nav + 1)
                        }
                    }
                }
            }
        }
    }
}