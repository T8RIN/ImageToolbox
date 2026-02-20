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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.compress.screenLogic.CompressPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.screenLogic.CropPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.flatten.screenLogic.FlattenPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.grayscale.screenLogic.GrayscalePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.metadata.screenLogic.MetadataPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.page_numbers.screenLogic.PageNumbersPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.protect.screenLogic.ProtectPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rearrange.screenLogic.RearrangePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.remove_pages.screenLogic.RemovePagesPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.repair.screenLogic.RepairPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.rotate.screenLogic.RotatePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.screenLogic.SignaturePdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.split.screenLogic.SplitPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.unlock.screenLogic.UnlockPdfToolComponent
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.watermark.screenLogic.WatermarkPdfToolComponent
import com.t8rin.logger.makeLog
import kotlinx.coroutines.Job

abstract class BasePdfToolComponent(
    val onGoBack: () -> Unit,
    val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder,
    componentContext: ComponentContext,
    private val pdfManager: PdfManager<Bitmap>,
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    protected val _showPasswordRequestDialog: MutableState<Boolean> = mutableStateOf(false)
    val showPasswordRequestDialog by _showPasswordRequestDialog

    protected var isRtl = false

    open val extraDataType: ExtraDataType = ExtraDataType.Pdf

    init {
        doOnDestroy {
            pdfManager.setMasterPassword(null)
        }
    }

    protected var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    open fun setPassword(password: String) {
        _showPasswordRequestDialog.update { false }
        pdfManager.setMasterPassword(password)
    }

    fun hidePasswordRequestDialog() {
        _showPasswordRequestDialog.update { false }
    }

    fun updateIsRtl(isRtl: Boolean) {
        this.isRtl = isRtl
    }

    fun checkPdf(
        uri: Uri?,
        onDecrypted: (Uri) -> Unit,
        onSuccess: (Uri) -> Unit = {}
    ) {
        uri ?: return

        componentScope.launch {
            runSuspendCatching {
                pdfManager.checkIsPdfEncrypted(uri.toString())?.let { decrypted ->
                    pdfManager.setMasterPassword(null)
                    onDecrypted(decrypted.toUri())
                    onSuccess(decrypted.toUri())
                } ?: onSuccess(uri)
            }.onFailure {
                if (it is SecurityException) {
                    _showPasswordRequestDialog.update { true }
                } else {
                    it.makeLog("checkPdf")
                }
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    open fun generatePdfFilename(): String = getKey().let { (key, uri) ->
        pdfManager.createTempName(
            key = key,
            uri = uri?.toString()
        )
    }

    private fun getKey(): Pair<String, Uri?> = when (this) {
        is SplitPdfToolComponent -> "split" to uri
        is RotatePdfToolComponent -> "rotated" to uri
        is RearrangePdfToolComponent -> "rearranged" to uri
        is PageNumbersPdfToolComponent -> "numbered" to uri
        is WatermarkPdfToolComponent -> "watermarked" to uri
        is SignaturePdfToolComponent -> "signed" to uri
        is CompressPdfToolComponent -> "compressed" to uri
        is GrayscalePdfToolComponent -> "grayscale" to uri
        is RepairPdfToolComponent -> "repaired" to uri
        is ProtectPdfToolComponent -> "protected" to uri
        is UnlockPdfToolComponent -> "unlocked" to uri
        is MetadataPdfToolComponent -> "metadata" to uri
        is RemovePagesPdfToolComponent -> "removed" to uri
        is CropPdfToolComponent -> "cropped" to uri
        is FlattenPdfToolComponent -> "flattened" to uri
        else -> "" to null
    }

    abstract fun saveTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    )

    abstract fun performSharing(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    abstract fun prepareForSharing(
        onSuccess: suspend (List<Uri>) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    protected fun doSaving(
        action: suspend KeepAliveService.() -> SaveResult,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
            runSuspendCatching {
                _isSaving.value = true
                onResult(action())
            }.onFailure {
                if (it is SecurityException) {
                    _showPasswordRequestDialog.update { true }
                } else {
                    onResult(SaveResult.Error.Exception(it))
                }
            }
            _isSaving.value = false
        }
    }

    protected fun <T> doSharing(
        action: suspend KeepAliveService.() -> T,
        onSuccess: (T) -> Unit = {},
        onFailure: (Throwable) -> Unit
    ) {
        savingJob = trackProgress {
            runSuspendCatching {
                _isSaving.value = true
                onSuccess(action())
            }.onFailure {
                if (it is SecurityException) {
                    _showPasswordRequestDialog.update { true }
                } else {
                    onFailure(it)
                }
            }
            _isSaving.value = false
        }
    }

}