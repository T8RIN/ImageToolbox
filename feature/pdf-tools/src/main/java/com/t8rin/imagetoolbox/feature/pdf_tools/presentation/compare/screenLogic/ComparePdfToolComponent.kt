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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compare.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCompareParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ComparePdfToolComponent @AssistedInject internal constructor(
    @Assisted val initialUris: List<Uri>?,
    @Assisted componentContext: ComponentContext,
    @Assisted onGoBack: () -> Unit,
    @Assisted onNavigate: (Screen) -> Unit,
    private val pdfManager: PdfManager,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BasePdfToolComponent(
    onGoBack = onGoBack,
    onNavigate = onNavigate,
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext,
    pdfManager = pdfManager
) {
    override val _haveChanges: MutableState<Boolean> = mutableStateOf(!initialUris.isNullOrEmpty())
    override val haveChanges: Boolean by _haveChanges

    private val _firstUri = mutableStateOf(initialUris?.getOrNull(0))
    val firstUri by _firstUri
    private val _secondUri = mutableStateOf(initialUris?.getOrNull(1))
    val secondUri by _secondUri
    val canProcess get() = firstUri != null && secondUri != null

    private val _params = mutableStateOf(PdfCompareParams())
    val params by _params

    fun setFirstUri(uri: Uri?) {
        _firstUri.value = uri
        updateChangesState()
    }

    fun setSecondUri(uri: Uri?) {
        _secondUri.value = uri
        updateChangesState()
    }

    fun updateParams(params: PdfCompareParams) {
        _params.value = params
        registerChanges()
    }

    private fun updateChangesState() {
        if (firstUri == null && secondUri == null) registerChangesCleared()
        else registerChanges()
    }

    private suspend fun process(): Uri = pdfManager.comparePdfs(
        firstUri = firstUri.toString(),
        secondUri = secondUri.toString(),
        params = params
    ).toUri()

    override fun saveTo(uri: Uri) {
        doSaving {
            fileController.transferBytes(
                fromUri = process().toString(),
                toUri = uri.toString()
            ).onSuccess(::registerSave)
        }
    }

    override fun performSharing(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        prepareForSharing(
            onSuccess = {
                shareProvider.shareUris(it.map(Uri::toString))
                registerSave()
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    override fun prepareForSharing(
        onSuccess: suspend (List<Uri>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = doSharing(
        action = { onSuccess(listOf(process())) },
        onFailure = onFailure
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            initialUris: List<Uri>?,
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): ComparePdfToolComponent
    }
}
