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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rotate.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class RotatePdfToolComponent @AssistedInject internal constructor(
    @Assisted val initialUri: Uri?,
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
    override val _haveChanges: MutableState<Boolean> = mutableStateOf(initialUri != null)
    override val haveChanges: Boolean by _haveChanges

    private val _uri: MutableState<Uri?> = mutableStateOf(initialUri)
    val uri by _uri

    private val _rotations: MutableState<List<Int>> = mutableStateOf(emptyList())
    val rotations by _rotations

    override fun getKey(): Pair<String, Uri?> = "rotated" to uri

    fun setUri(uri: Uri?) {
        if (uri == null) {
            registerChangesCleared()
        } else {
            registerChanges()
        }
        _uri.update { uri }
        checkPdf(
            uri = uri,
            onDecrypted = { _uri.value = it }
        )
    }

    fun updateRotations(rotations: List<Int>) {
        registerChanges()
        _rotations.update { rotations }
    }

    fun autoRotate() {
        doSharing(
            action = {
                updateRotations(
                    pdfManager.detectPdfAutoRotations(
                        uri?.toString() ?: return@doSharing
                    )
                )
            },
            onFailure = {}
        )
    }

    override fun saveTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        doSaving(
            action = {
                val processed = pdfManager.rotatePdf(
                    uri = _uri.value.toString(),
                    rotations = rotations
                )

                fileController.transferBytes(
                    fromUri = processed,
                    toUri = uri.toString()
                ).onSuccess(::registerSave)
            },
            onResult = onResult
        )
    }

    override fun performSharing(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
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
    ) {
        doSharing(
            action = {
                onSuccess(
                    listOf(
                        pdfManager.rotatePdf(
                            uri = _uri.value.toString(),
                            rotations = rotations
                        ).toUri()
                    )
                )
                registerSave()
            },
            onFailure = onFailure
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            initialUri: Uri?,
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): RotatePdfToolComponent
    }
}