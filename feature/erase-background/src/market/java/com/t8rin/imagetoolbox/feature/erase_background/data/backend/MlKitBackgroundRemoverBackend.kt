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

package com.t8rin.imagetoolbox.feature.erase_background.data.backend

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import com.t8rin.imagetoolbox.feature.erase_background.data.backend.impl.MlKitBackgroundRemover
import com.t8rin.imagetoolbox.feature.erase_background.data.backend.impl.MlKitSubjectBackgroundRemover
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemoverBackend
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal object MlKitBackgroundRemoverBackend : AutoBackgroundRemoverBackend<Bitmap> {

    override suspend fun performBackgroundRemove(
        image: Bitmap
    ): Result<Bitmap> = suspendCancellableCoroutine { continuation ->
        runCatching {
            autoRemove(
                image = image,
                onFinish = { continuation.resume(it) }
            )
        }.onFailure {
            continuation.resume(Result.failure(it))
        }
    }

    @SuppressLint("NewApi")
    private fun autoRemove(
        type: ApiType = ApiType.Best,
        image: Bitmap,
        onFinish: (Result<Bitmap>) -> Unit
    ) {
        val old = {
            MlKitBackgroundRemover.removeBackground(
                bitmap = image,
                onFinish = onFinish
            )
        }
        val new = {
            MlKitSubjectBackgroundRemover.removeBackground(
                bitmap = image,
                onFinish = {
                    if (it.isFailure) old() else onFinish(it)
                }
            )
        }

        when (type) {
            ApiType.Old -> old()
            ApiType.New -> new()
        }
    }

    private enum class ApiType {
        Old, New;

        companion object Companion {
            val Best: ApiType get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) New else Old
        }
    }

}