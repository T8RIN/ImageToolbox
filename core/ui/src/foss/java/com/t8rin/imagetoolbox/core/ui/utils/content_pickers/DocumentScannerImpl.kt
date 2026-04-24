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

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.onPrimaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.primaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ScanResult
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.utils.appContext
import com.websitebeaver.documentscanner.DocumentScanner as DocumentScannerDelegate

private class DocumentScannerImpl(
    private val context: Context,
    private val scanner: DocumentScannerDelegate,
    private val scannerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    private val requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) : DocumentScanner {

    override fun scan() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scannerLauncher.launch(scanner.createDocumentScanIntent())
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

}

@Composable
internal fun rememberDocumentScannerImpl(
    onSuccess: (ScanResult) -> Unit
): DocumentScanner {
    val context = LocalComponentActivity.current
    val colorScheme = MaterialTheme.colorScheme
    val cropperHandleColor = colorScheme.onPrimaryContainerFixed
    val cropperFrameColor = colorScheme.onPrimaryContainerFixed
    val buttonTintColor = colorScheme.onPrimaryContainerFixed
    val buttonContainerColor = colorScheme.primaryContainerFixed
    val cropperStrokeWidthDp = 1.2f
    val doneButtonTintColor = colorScheme.onPrimaryFixed
    val doneButtonContainerColor = colorScheme.primaryFixed

    val scanner = remember(
        context,
        cropperHandleColor,
        cropperFrameColor,
        buttonTintColor,
        buttonContainerColor,
        buttonContainerColor,
        cropperStrokeWidthDp,
        doneButtonTintColor,
        doneButtonContainerColor
    ) {
        DocumentScannerDelegate(
            activity = context,
            cropperHandleColor = cropperHandleColor.toArgb(),
            cropperHandleOutlineColor = buttonContainerColor.toArgb(),
            cropperFrameColor = cropperFrameColor.toArgb(),
            cropperStrokeWidthDp = cropperStrokeWidthDp,
            buttonTintColor = buttonTintColor.toArgb(),
            buttonContainerColor = buttonContainerColor.toArgb(),
            doneButtonTintColor = doneButtonTintColor.toArgb(),
            doneButtonContainerColor = doneButtonContainerColor.toArgb(),
            successHandler = { imageUris ->
                onSuccess(
                    ScanResult(imageUris.map { it.toUri() })
                )
            },
            errorHandler = {
                if (it.contains(MediaStore.ACTION_IMAGE_CAPTURE)) {
                    AppToastHost.showToast(
                        message = context.getString(R.string.camera_failed_to_open),
                        icon = Icons.Outlined.CameraAlt
                    )
                } else {
                    AppToastHost.showFailureToast(it)
                }
            }
        )
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        scanner.handleDocumentScanIntentResult(result)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scannerLauncher.launch(scanner.createDocumentScanIntent())
        } else {
            AppToastHost.showToast(
                message = appContext.getString(R.string.grant_camera_permission_to_scan_document_scanner),
                icon = Icons.Outlined.CameraAlt
            )
        }
    }

    return remember(context, scanner, scannerLauncher, requestPermissionLauncher) {
        DocumentScannerImpl(
            context = context,
            scanner = scanner,
            scannerLauncher = scannerLauncher,
            requestPermissionLauncher = requestPermissionLauncher
        )
    }
}
