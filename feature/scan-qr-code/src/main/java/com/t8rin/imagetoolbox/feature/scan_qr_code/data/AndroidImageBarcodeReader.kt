/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.scan_qr_code.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.data.utils.toSoftware
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.toQrType
import com.t8rin.imagetoolbox.feature.scan_qr_code.domain.ImageBarcodeReader
import io.github.g00fy2.quickie.extensions.readQrCode
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

internal class AndroidImageBarcodeReader @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    resourceManager: ResourceManager,
    dispatchersHolder: DispatchersHolder
) : ImageBarcodeReader, DispatchersHolder by dispatchersHolder, ResourceManager by resourceManager {

    override suspend fun readBarcode(
        image: Any
    ): Result<QrType> = withContext(defaultDispatcher) {
        val bitmap = image as? Bitmap
            ?: imageGetter.getImage(
                data = image,
                originalSize = false
            )

        if (bitmap == null) {
            return@withContext Result.failure(NullPointerException(getString(R.string.something_went_wrong)))
        }

        suspendCancellableCoroutine { continuation ->
            bitmap.toSoftware().readQrCode(
                barcodeFormats = IntArray(0),
                onSuccess = {
                    continuation.resume(Result.success(it.toQrType()))
                },
                onFailure = {
                    continuation.resume(Result.failure(it))
                }
            )
        }
    }

}