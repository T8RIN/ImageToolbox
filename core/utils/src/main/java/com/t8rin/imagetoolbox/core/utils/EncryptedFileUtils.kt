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

package com.t8rin.imagetoolbox.core.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val IMAGE_TOOLBOX_ENCRYPTED_FILE_MAGIC = "ITBXCRYP"

suspend fun Uri.isEncrypted(
    context: Context = appContext
): Boolean = withContext(Dispatchers.IO) {
    try {
        context.contentResolver.openInputStream(this@isEncrypted)?.buffered()?.use { input ->
            val expected = IMAGE_TOOLBOX_ENCRYPTED_FILE_MAGIC.encodeToByteArray()
            val actual = ByteArray(expected.size)
            var offset = 0

            while (offset < actual.size) {
                val read = input.read(actual, offset, actual.size - offset)
                if (read <= 0) return@use false
                offset += read
            }

            actual.contentEquals(expected)
        } == true
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        false
    }
}
