/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.ui.widget.other

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.harmonizeWithPrimary
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.autoElevatedBorder
import com.t8rin.modalsheet.FullscreenPopup
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

@Composable
fun ToastHost(
    hostState: ToastHostState = LocalToastHostState.current,
    modifier: Modifier = Modifier.fillMaxSize(),
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


    FullscreenPopup(
        placeAboveAll = true
    ) {
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
}

@Composable
fun Toast(
    toastData: ToastData,
    modifier: Modifier = Modifier,
    shape: Shape = ToastDefaults.shape,
    containerColor: Color = ToastDefaults.color,
    contentColor: Color = ToastDefaults.contentColor,
) {
    val screenSize = LocalScreenSize.current
    val sizeMin = screenSize.width.coerceAtMost(screenSize.height)

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
                .autoElevatedBorder(
                    color = MaterialTheme.colorScheme
                        .outlineVariant(0.3f, contentColor)
                        .copy(alpha = 0.92f),
                    shape = shape,
                    autoElevation = animateDpAsState(
                        if (LocalSettingsState.current.drawContainerShadows) 6.dp
                        else 0.dp
                    ).value
                )
                .alpha(0.95f),
        shape = shape
    ) {
        Row(
            Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            toastData.visuals.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
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
@Immutable
open class ToastHostState {

    private val mutex = Mutex()

    var currentToastData by mutableStateOf<ToastData?>(null)
        private set

    suspend fun showToast(
        message: String,
        icon: ImageVector? = null,
        duration: ToastDuration = ToastDuration.Short
    ) = showToast(ToastVisualsImpl(message, icon, duration))

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
            return duration == other.duration
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
            return continuation == other.continuation
        }

        override fun hashCode(): Int {
            var result = visuals.hashCode()
            result = 31 * result + continuation.hashCode()
            return result
        }
    }
}

@Stable
@Immutable
interface ToastData {
    val visuals: ToastVisuals
    fun dismiss()
}

@Stable
@Immutable
interface ToastVisuals {
    val message: String
    val icon: ImageVector?
    val duration: ToastDuration
}

@Stable
@Immutable
open class ToastDuration(val time: kotlin.Long) {
    object Short : ToastDuration(3500L)
    object Long : ToastDuration(6500L)
}

@Stable
@Immutable
object ToastDefaults {
    val transition: ContentTransform
        get() = fadeIn(tween(300)) + scaleIn(
            animationSpec = spring(
                dampingRatio = 0.65f,
                stiffness = Spring.StiffnessMediumLow
            ),
            transformOrigin = TransformOrigin(0.5f, 1f)
        ) + slideInVertically(
            spring(
                stiffness = Spring.StiffnessHigh
            )
        ) { it / 2 } togetherWith fadeOut(tween(250)) + slideOutVertically(
            tween(500)
        ) { it / 2 } + scaleOut(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            transformOrigin = TransformOrigin(0.5f, 1f)
        )
    val contentColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.inverseOnSurface.harmonizeWithPrimary()

    val color: Color
        @Composable
        get() = MaterialTheme.colorScheme.inverseSurface.harmonizeWithPrimary()

    val shape: Shape = ShapeDefaults.extremeLarge
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

val LocalToastHostState = compositionLocalOf { ToastHostState() }

suspend fun ToastHostState.showFailureToast(
    context: Context,
    throwable: Throwable
) = showFailureToast(
    message = context.getString(
        R.string.smth_went_wrong,
        throwable.localizedMessage ?: ""
    )
)

suspend fun ToastHostState.showFailureToast(
    message: String
) = showToast(
    message = message,
    icon = Icons.Rounded.ErrorOutline,
    duration = ToastDuration.Long
)