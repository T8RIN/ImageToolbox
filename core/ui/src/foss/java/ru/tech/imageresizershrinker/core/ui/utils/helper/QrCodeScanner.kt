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

package ru.tech.imageresizershrinker.core.ui.utils.helper

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

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError


class QrCodeScanner internal constructor(
    private val context: Context,
    private val scannerLauncher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>,
    private val onFailure: (Throwable) -> Unit
) {

    fun scan() {
        runCatching {
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setBeepEnabled(false)
                setOrientationLocked(false)
                addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
                setPrompt(context.getString(R.string.scan_qr_code))
            }

            scannerLauncher.launch(options)
        }.onFailure(onFailure)
    }

}


@Composable
fun rememberQrCodeScanner(
    onSuccess: (String) -> Unit
): QrCodeScanner {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current as ComponentActivity

    val scannerLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let(onSuccess)
    }

    return remember(context, scannerLauncher) {
        QrCodeScanner(
            context = context,
            scannerLauncher = scannerLauncher,
            onFailure = {
                scope.launch {
                    toastHostState.showError(
                        context = context,
                        error = it
                    )
                }
            }
        )
    }
}