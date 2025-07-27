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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials

private class DocumentScannerImpl(
    private val context: ComponentActivity,
    private val scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    private val onFailure: (Throwable) -> Unit
) : DocumentScanner {

    override fun scan() {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        scanner.getStartScanIntent(context)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener(onFailure)
    }

}

@Composable
internal fun rememberDocumentScannerImpl(
    onSuccess: (ScanResult) -> Unit
): DocumentScanner {
    val essentials = rememberLocalEssentials()
    val context = LocalComponentActivity.current

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            runCatching {
                GmsDocumentScanningResult.fromActivityResultIntent(result.data)?.apply {
                    onSuccess(
                        ScanResult(
                            imageUris = pages?.let { pages ->
                                pages.map { it.imageUri }
                            } ?: emptyList(),
                            pdfUri = pdf?.uri
                        )
                    )
                }
            }.onFailure(essentials::showFailureToast)
        }
    }

    return remember(context, scannerLauncher) {
        DocumentScannerImpl(
            context = context,
            scannerLauncher = scannerLauncher,
            onFailure = essentials::showFailureToast
        )
    }
}