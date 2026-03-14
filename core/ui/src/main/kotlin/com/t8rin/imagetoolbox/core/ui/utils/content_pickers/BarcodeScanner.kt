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

package com.t8rin.imagetoolbox.core.ui.utils.content_pickers

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.onPrimaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.onTertiaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.primaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.core.utils.toQrType
import com.t8rin.logger.makeLog
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig

private class BarcodeScannerImpl(
    private val tint: Color,
    private val container: Color,
    private val frame: Color,
    private val frameHighlighted: Color,
    private val topIcon: Color,
    private val topText: Color,
    private val scannerLauncher: ManagedActivityResultLauncher<ScannerConfig, QRResult>
) : BarcodeScanner {

    override fun scan() {
        val config = ScannerConfig.build {
            setBarcodeFormats(listOf(BarcodeFormat.ALL_FORMATS))
            setOverlayStringRes(R.string.scan_barcode)
            setOverlayDrawableRes(R.drawable.ic_24_barcode_scanner)
            setHapticSuccessFeedback(true)
            setShowTorchToggle(true)
            setShowCloseButton(true)
            setKeepScreenOn(true)
            setColors(
                buttonTint = tint.toArgb(),
                buttonBackground = container.toArgb(),
                frame = frame.toArgb(),
                frameHighlighted = frameHighlighted.toArgb(),
                topIcon = topIcon.toArgb(),
                topText = topText.toArgb()
            )
        }.makeLog("Barcode Scanner")

        scannerLauncher.launch(config)
    }

}

@Stable
@Immutable
interface BarcodeScanner : Scanner

@Composable
fun rememberBarcodeScanner(
    onSuccess: (QrType) -> Unit
): BarcodeScanner {
    val scannerLauncher = rememberLauncherForActivityResult(ScanCustomCode()) { result ->
        result.makeLog("Barcode Scanner")

        when (result) {
            is QRResult.QRError -> {
                appContext.apply {
                    val message = result.exception.localizedMessage ?: ""

                    AppToastHost.showFailureToast(
                        if ("NotFound" in message) {
                            getString(R.string.no_barcode_found)
                        } else {
                            getString(R.string.smth_went_wrong, message)
                        }
                    )
                }
            }

            QRResult.QRMissingPermission -> {
                AppToastHost.showToast(
                    message = getString(R.string.grant_camera_permission_to_scan_qr_code),
                    icon = Icons.Outlined.CameraAlt
                )
            }

            is QRResult.QRSuccess -> onSuccess(result.content.toQrType())

            QRResult.QRUserCanceled -> Unit
        }
    }

    val tint = MaterialTheme.colorScheme.onPrimaryContainerFixed
    val container = MaterialTheme.colorScheme.primaryContainerFixed
    val frame = MaterialTheme.colorScheme.onPrimaryContainerFixed
    val frameHighlighted = MaterialTheme.colorScheme.onTertiaryContainerFixed
    val topIcon = MaterialTheme.colorScheme.secondaryFixed
    val topText = MaterialTheme.colorScheme.secondaryFixed

    return remember(tint, container, frame, frameHighlighted, topIcon, topText, scannerLauncher) {
        BarcodeScannerImpl(
            tint = tint,
            container = container,
            frame = frame,
            frameHighlighted = frameHighlighted,
            topIcon = topIcon,
            topText = topText,
            scannerLauncher = scannerLauncher,
        )
    }
}