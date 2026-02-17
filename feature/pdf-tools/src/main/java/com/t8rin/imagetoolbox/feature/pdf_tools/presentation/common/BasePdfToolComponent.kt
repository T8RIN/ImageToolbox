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
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.logger.makeLog
import kotlinx.coroutines.Job
import kotlin.random.Random

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

    fun checkPdf(
        uri: Uri?,
        onDecrypted: (Uri) -> Unit
    ) {
        uri ?: return

        componentScope.launch {
            runSuspendCatching {
                pdfManager.checkIsPdfEncrypted(uri.toString())?.let { decrypted ->
                    pdfManager.setMasterPassword(null)
                    onDecrypted(decrypted.toUri())
                }
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

    fun generatePdfFilename(): String {
        val timeStamp = "${timestamp()}_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        return "PDF_$timeStamp.pdf"
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