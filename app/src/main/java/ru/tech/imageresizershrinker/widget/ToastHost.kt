package ru.tech.imageresizershrinker.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.harmonizeWithPrimary
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import kotlin.coroutines.resume

@Composable
fun ToastHost(
    hostState: ToastHostState,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.fillMaxSize(),
    alignment: Alignment = Alignment.BottomCenter,
    transitionSpec: AnimatedContentTransitionScope<ToastData?>.() -> ContentTransform = { ToastDefaults.transition },
    toast: @Composable (ToastData) -> Unit = { Toast(it) }
) {
    val currentToastData = hostState.currentToastData
    val accessibilityManager = LocalAccessibilityManager.current
    LaunchedEffect(currentToastData) {
        if (currentToastData != null) {
            val duration = currentToastData.visuals.duration.toMillis(accessibilityManager)
            delay(duration)
            currentToastData.dismiss()
        }
    }

    AnimatedContent(
        modifier = Modifier.zIndex(100f),
        targetState = currentToastData,
        transitionSpec = transitionSpec
    ) {
        Box(modifier = modifier) {
            Box(modifier = Modifier.align(alignment)) {
                it?.let { toast(it) }
            }
        }
    }

}

@Composable
fun Toast(
    toastData: ToastData,
    modifier: Modifier = Modifier,
    shape: Shape = ToastDefaults.shape,
    containerColor: Color = ToastDefaults.color,
    contentColor: Color = ToastDefaults.contentColor,
) {
    val configuration = LocalConfiguration.current
    val sizeMin = configuration.screenWidthDp.coerceAtMost(configuration.screenHeightDp).dp

    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = if (modifier != Modifier) modifier else
            Modifier
                .heightIn(min = 48.dp)
                .widthIn(min = 0.dp, max = (sizeMin * 0.7f))
                .padding(
                    bottom = sizeMin * 0.2f,
                    top = 24.dp,
                    start = 12.dp,
                    end = 12.dp
                )
                .imePadding()
                .systemBarsPadding()
                .border(
                    width = LocalSettingsState.current.borderWidth,
                    color = MaterialTheme.colorScheme
                        .outlineVariant(0.3f, contentColor)
                        .copy(alpha = 0.95f),
                    shape = shape
                )
                .alpha(0.95f),
        shape = shape
    ) {
        Row(
            Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            toastData.visuals.icon?.let { Icon(it, null) }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = toastData.visuals.message,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(end = 5.dp)
            )
        }
    }
}

@Stable
class ToastHostState {

    private val mutex = Mutex()

    var currentToastData by mutableStateOf<ToastData?>(null)
        private set

    @OptIn(ExperimentalMaterial3Api::class)
    suspend fun showToast(
        message: String,
        icon: ImageVector? = null,
        duration: ToastDuration = ToastDuration.Short
    ) = showToast(ToastVisualsImpl(message, icon, duration))

    suspend fun showEmpty(
        duration: ToastDuration = ToastDuration(4500L)
    ) = showToast(message = "", duration = duration)

    @ExperimentalMaterial3Api
    suspend fun showToast(visuals: ToastVisuals) = mutex.withLock {
        try {
            suspendCancellableCoroutine { continuation ->
                currentToastData = ToastDataImpl(visuals, continuation)
            }
        } finally {
            currentToastData = null
        }
    }

    private class ToastVisualsImpl(
        override val message: String,
        override val icon: ImageVector? = null,
        override val duration: ToastDuration
    ) : ToastVisuals {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ToastVisualsImpl

            if (message != other.message) return false
            if (icon != other.icon) return false
            if (duration != other.duration) return false

            return true
        }

        override fun hashCode(): Int {
            var result = message.hashCode()
            result = 31 * result + icon.hashCode()
            result = 31 * result + duration.hashCode()
            return result
        }
    }

    private class ToastDataImpl(
        override val visuals: ToastVisuals,
        private val continuation: CancellableContinuation<Unit>
    ) : ToastData {

        override fun dismiss() {
            if (continuation.isActive) continuation.resume(Unit)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ToastDataImpl

            if (visuals != other.visuals) return false
            if (continuation != other.continuation) return false

            return true
        }

        override fun hashCode(): Int {
            var result = visuals.hashCode()
            result = 31 * result + continuation.hashCode()
            return result
        }
    }
}

@Stable
interface ToastData {
    val visuals: ToastVisuals
    fun dismiss()
}

@Stable
interface ToastVisuals {
    val message: String
    val icon: ImageVector?
    val duration: ToastDuration
}

open class ToastDuration(val time: kotlin.Long) {
    object Short : ToastDuration(3500L)
    object Long : ToastDuration(6500L)
}

object ToastDefaults {
    val transition: ContentTransform
        get() = fadeIn(tween(300)) + scaleIn(
            tween(500),
            transformOrigin = TransformOrigin(0.5f, 1f)
        ) + slideInVertically(
            tween(500)
        ) { it / 2 } togetherWith fadeOut(tween(250)) + slideOutVertically(tween(500)) { it / 2 } + scaleOut(
            tween(750),
            transformOrigin = TransformOrigin(0.5f, 1f)
        )
    val contentColor: Color @Composable get() = MaterialTheme.colorScheme.inverseOnSurface.harmonizeWithPrimary()
    val color: Color @Composable get() = MaterialTheme.colorScheme.inverseSurface.harmonizeWithPrimary()
    val shape: Shape @Composable get() = MaterialTheme.shapes.extraLarge
}

private fun ToastDuration.toMillis(
    accessibilityManager: AccessibilityManager?
): Long {
    val original = this.time
    return accessibilityManager?.calculateRecommendedTimeoutMillis(
        original,
        containsIcons = true,
        containsText = true
    ) ?: original
}

@Composable
fun rememberToastHostState() = remember { ToastHostState() }

val LocalToastHost = compositionLocalOf { ToastHostState() }

suspend fun ToastHostState.showError(
    context: Context,
    error: Throwable
) = showToast(
    context.getString(
        R.string.smth_went_wrong,
        error.localizedMessage ?: ""
    ),
    Icons.Rounded.ErrorOutline
)