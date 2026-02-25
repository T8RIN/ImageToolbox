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

package com.t8rin.imagetoolbox.feature.pdf_tools.domain

import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCheckResult
import kotlinx.coroutines.flow.StateFlow

interface PdfHelper {

    val savedSignatures: StateFlow<List<String>>

    suspend fun saveSignature(signature: Any): Boolean

    fun setMasterPassword(
        password: String?
    )

    fun createTempName(
        key: String,
        uri: String? = null
    ): String

    fun clearPdfCache(uri: String?)

    suspend fun checkPdf(
        uri: String
    ): PdfCheckResult

    suspend fun getPdfPages(
        uri: String
    ): List<Int>

    suspend fun getPdfPageSizes(
        uri: String
    ): List<IntegerSize>

}