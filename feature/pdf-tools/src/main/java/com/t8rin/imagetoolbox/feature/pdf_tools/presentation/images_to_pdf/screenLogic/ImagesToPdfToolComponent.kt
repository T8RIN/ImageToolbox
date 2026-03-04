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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.images_to_pdf.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCreationParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ImagesToPdfToolComponent @AssistedInject internal constructor(
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
    override val _haveChanges: MutableState<Boolean> = mutableStateOf(initialUris != null)
    override val haveChanges: Boolean by _haveChanges

    private val _uris: MutableState<List<Uri>?> = mutableStateOf(initialUris)
    val uris by _uris

    private val _presetSelected: MutableState<Preset.Percentage> =
        mutableStateOf(Preset.Percentage(100))
    val presetSelected by _presetSelected

    private val _quality: MutableState<Int> = mutableIntStateOf(85)
    val quality by _quality

    private val _scaleSmallImagesToLarge: MutableState<Boolean> = mutableStateOf(false)
    val scaleSmallImagesToLarge by _scaleSmallImagesToLarge

    private val pdfCreationParams: PdfCreationParams
        get() = PdfCreationParams(
            scaleSmallImagesToLarge = _scaleSmallImagesToLarge.value,
            preset = _presetSelected.value,
            quality = _quality.value
        )

    fun setUris(uris: List<Uri>?) {
        if (uris == null) {
            registerChangesCleared()
        } else {
            registerChanges()
        }
        _uris.update { uris }
    }

    fun addUris(uris: List<Uri>) {
        setUris(this.uris.orEmpty().plus(uris).distinct())
    }

    fun removeAt(index: Int) {
        runCatching {
            _uris.update {
                it?.toMutableList()?.apply { removeAt(index) }
            }
            registerChanges()
        }
    }

    fun toggleScaleSmallImagesToLarge() {
        _scaleSmallImagesToLarge.update { !it }
        registerChanges()
    }

    fun setQuality(quality: Int) {
        _quality.update { quality }
        registerChanges()
    }

    fun selectPreset(preset: Preset.Percentage) {
        _presetSelected.update { preset }
        registerChanges()
    }

    override fun saveTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        doSaving(
            action = {
                val processed = pdfManager.createPdf(
                    imageUris = uris?.map { it.toString() } ?: emptyList(),
                    params = pdfCreationParams
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
                        pdfManager.createPdf(
                            imageUris = uris?.map { it.toString() } ?: emptyList(),
                            params = pdfCreationParams
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
            initialUris: List<Uri>?,
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ImagesToPdfToolComponent
    }
}