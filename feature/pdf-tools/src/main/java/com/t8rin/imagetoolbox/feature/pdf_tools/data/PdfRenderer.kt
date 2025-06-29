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

package com.t8rin.imagetoolbox.feature.pdf_tools.data

import android.annotation.SuppressLint
import android.graphics.pdf.LoadParams
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.ext.SdkExtensions
import androidx.annotation.ChecksSdkIntAtLeast
import com.t8rin.imagetoolbox.core.crash.presentation.components.DeviceInfo

fun ParcelFileDescriptor.createPdfRenderer(
    password: String?,
    onFailure: (Throwable) -> Unit,
    onPasswordRequest: (() -> Unit)?
): PdfRenderer? = runCatching {
    if (canUseNewPdf()) {
        runCatching {
            @SuppressLint("NewApi")
            PdfRenderer(
                this@createPdfRenderer,
                LoadParams.Builder().setPassword(password).build()
            )
        }.onFailure {
            when {
                it is SecurityException -> onPasswordRequest?.invoke() ?: throw it

                "No direct method" in it.message.orEmpty() -> {
                    DeviceInfo.pushLog("createPdfRenderer, no LoadParams")

                    return@runCatching PdfRenderer(this)
                }

                else -> throw it
            }
        }.getOrNull()
    } else {
        PdfRenderer(this)
    }
}.onFailure { throwable ->
    when (throwable) {
        is SecurityException -> onPasswordRequest?.invoke() ?: onFailure(throwable)
        else -> onFailure(throwable)
    }
}.getOrNull()


@ChecksSdkIntAtLeast(api = 13, extension = Build.VERSION_CODES.S)
fun canUseNewPdf(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
        || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13