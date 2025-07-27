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

package com.t8rin.imagetoolbox.feature.erase_background.data

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import com.t8rin.imagetoolbox.core.domain.dispatchers.DispatchersHolder
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemoverBackend
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

internal class AndroidAutoBackgroundRemoverBackend @Inject constructor(
    dispatchersHolder: DispatchersHolder
) : AutoBackgroundRemoverBackend<Bitmap>, DispatchersHolder by dispatchersHolder {

    override fun performBackgroundRemove(
        image: Bitmap,
        onFinish: (Result<Bitmap>) -> Unit
    ) {
        runCatching {
            autoRemove(
                image = image,
                onFinish = onFinish
            )
        }.onFailure {
            onFinish(Result.failure(it))
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
                scope = CoroutineScope(defaultDispatcher),
                onFinish = onFinish
            )
        }
        val new = {
            MlKitSubjectBackgroundRemover.removeBackground(
                bitmap = image,
                onFinish = {
                    if (it.isFailure) {
                        old()
                    } else onFinish(it)
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

        companion object {
            val Best: ApiType get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) New else Old
        }
    }

}