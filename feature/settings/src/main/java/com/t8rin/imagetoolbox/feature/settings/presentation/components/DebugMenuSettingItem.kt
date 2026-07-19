/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import android.content.ActivityNotFoundException
import android.system.ErrnoException
import android.system.OsConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BugReport
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.settings.presentation.components.additional.AnimatedGradientBox
import com.t8rin.imagetoolbox.feature.settings.presentation.components.additional.FullscreenDebugMenu
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun DebugMenuSettingItem(
    shape: Shape = ShapeDefaults.default,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val showMenu = rememberSaveable {
        mutableStateOf(false)
    }
    val isNightMode = LocalSettingsState.current.isNightMode

    AnimatedGradientBox(
        colors = {
            if (isNightMode) {
                listOf(
                    errorContainer.blend(
                        color = error,
                        fraction = 0.6f
                    ),
                    primary.blend(
                        color = error,
                        fraction = 0.4f
                    ),
                    error,
                )
            } else {
                listOf(
                    error.blend(
                        color = errorContainer,
                        fraction = 0.6f
                    ),
                    primaryContainer.blend(
                        color = errorContainer,
                        fraction = 0.4f
                    ),
                    errorContainer,
                )
            }
        }
    ) {
        PreferenceItem(
            onClick = {
                showMenu.value = true
            },
            shape = shape,
            modifier = modifier,
            overrideIconShapeContentColor = true,
            containerColor = Color.Transparent,
            contentColor = if (isNightMode) {
                MaterialTheme.colorScheme.onError
            } else {
                MaterialTheme.colorScheme.onErrorContainer
            },
            title = stringResource(R.string.debug_menu),
            subtitle = stringResource(R.string.debug_menu_sub),
            startIcon = Icons.TwoTone.BugReport
        )
    }

    FullscreenDebugMenu(
        showMenuState = showMenu
    ) {
        Text("Test crashes")
        EnhancedButton(
            onClick = {
                throw OutOfMemoryError("TEST")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger OutOfMemory crash")
        }
        EnhancedButton(
            onClick = {
                throw Throwable("ForegroundServiceDidNotStartInTimeException")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger ForegroundServiceDidNotStartInTimeException")
        }
        EnhancedButton(
            onClick = {
                throw Throwable("TEST")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger regular crash")
        }

        Spacer(Modifier.height(24.dp))

        FailureToastDebugButtons()
    }
}

@Composable
private fun FailureToastDebugButtons() {
    val cases = listOf(
        FailureToastDebugCase("Out of memory") {
            AppToastHost.showFailureToast(OutOfMemoryError("TEST"))
        },
        FailureToastDebugCase("Host unreachable") {
            AppToastHost.showFailureToast(UnknownHostException("TEST"))
        },
        FailureToastDebugCase("Connection timeout") {
            AppToastHost.showFailureToast(SocketTimeoutException("TEST"))
        },
        FailureToastDebugCase("Connection failed") {
            AppToastHost.showFailureToast(ConnectException("TEST"))
        },
        FailureToastDebugCase("Connection interrupted") {
            AppToastHost.showFailureToast(SocketException("TEST"))
        },
        FailureToastDebugCase("Secure connection failed") {
            AppToastHost.showFailureToast(SSLException("TEST"))
        },
        FailureToastDebugCase("File not found") {
            AppToastHost.showFailureToast(FileNotFoundException("TEST"))
        },
        FailureToastDebugCase("Permission not granted") {
            AppToastHost.showFailureToast(SecurityException("TEST"))
        },
        FailureToastDebugCase("Activity not found") {
            AppToastHost.showFailureToast(ActivityNotFoundException("TEST"))
        },
        FailureToastDebugCase("Storage is full") {
            AppToastHost.showFailureToast(ErrnoException("TEST", OsConstants.ENOSPC))
        },
        FailureToastDebugCase("HTTP 401 / 403") {
            AppToastHost.showFailureToast(httpResponseException(401))
        },
        FailureToastDebugCase("HTTP 404") {
            AppToastHost.showFailureToast(httpResponseException(404))
        },
        FailureToastDebugCase("HTTP 429") {
            AppToastHost.showFailureToast(httpResponseException(429))
        },
        FailureToastDebugCase("HTTP 5xx") {
            AppToastHost.showFailureToast(httpResponseException(500))
        },
        FailureToastDebugCase("Unknown failure") {
            AppToastHost.showFailureToast(Throwable("TEST"))
        }
    )

    Text(
        text = "Failure toasts",
        style = MaterialTheme.typography.titleMedium
    )

    cases.forEach { case ->
        EnhancedButton(
            onClick = case.onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(case.title)
        }
    }
}

private data class FailureToastDebugCase(
    val title: String,
    val onClick: () -> Unit
)

@OptIn(InternalAPI::class)
private fun httpResponseException(statusCode: Int): Throwable = ResponseException(
    response = object : HttpResponse() {
        override val call: HttpClientCall
            get() = error("Not available in debug response")
        override val status: HttpStatusCode = HttpStatusCode.fromValue(statusCode)
        override val version: HttpProtocolVersion = HttpProtocolVersion.HTTP_1_1
        override val requestTime: GMTDate = GMTDate.START
        override val responseTime: GMTDate = GMTDate.START
        override val rawContent: ByteReadChannel = ByteReadChannel.Empty
        override val headers: Headers = Headers.Empty
        override val coroutineContext: CoroutineContext = EmptyCoroutineContext

        override fun toString(): String = "DebugHttpResponse[$status]"
    },
    cachedResponseText = "TEST"
)

@Composable
private fun PreviewContent(isDarkTheme: Boolean) = ImageToolboxThemeForPreview(isDarkTheme) {
    Box(Modifier.padding(16.dp)) {
        EnhancedButton(
            onClick = {},
            modifier = Modifier.alpha(0f)
        ) { }

        DebugMenuSettingItem()
    }
}

@Composable
@EnPreview
private fun PreviewNight() = PreviewContent(true)

@Composable
@EnPreview
private fun PreviewDay() = PreviewContent(false)