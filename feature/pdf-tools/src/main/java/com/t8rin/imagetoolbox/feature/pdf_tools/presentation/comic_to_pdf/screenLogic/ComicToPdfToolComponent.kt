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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.comic_to_pdf.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Manga
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.ComicToPdfParams
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common.BasePdfToolComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ComicToPdfToolComponent @AssistedInject internal constructor(
    @Assisted initialUri: Uri?,
    @Assisted componentContext: ComponentContext,
    @Assisted onGoBack: () -> Unit,
    @Assisted onNavigate: (Screen) -> Unit,
    private val pdfManager: PdfManager,
    private val fileController: FileController,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : BasePdfToolComponent(
    onGoBack = onGoBack,
    onNavigate = onNavigate,
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext,
    pdfManager = pdfManager
) {
    val initialUri: Uri? = initialUri?.takeIf { it.isSupportedComicArchive() }

    override val _haveChanges: MutableState<Boolean> = mutableStateOf(this.initialUri != null)
    override val haveChanges: Boolean by _haveChanges

    override val extraDataType: ExtraDataType = ExtraDataType.Pdf
    override val mimeType: MimeType.Single = MimeType.Pdf

    private val _uri: MutableState<Uri?> = mutableStateOf(this.initialUri)
    val uri by _uri

    private val _passphrase: MutableState<String> = mutableStateOf("")
    val passphrase by _passphrase

    private val _params: MutableState<ComicToPdfParams> = mutableStateOf(ComicToPdfParams())
    val params by _params

    fun setUri(uri: Uri?) {
        if (uri != null && !uri.isSupportedComicArchive()) {
            AppToastHost.showToast(
                message = R.string.select_comic_archive_to_start,
                icon = Icons.Outlined.Manga
            )
            return
        }
        if (uri == null) registerChangesCleared() else registerChanges()
        _uri.update { uri }
    }

    fun setPassphrase(passphrase: String) {
        registerChanges()
        _passphrase.update { passphrase }
    }

    fun updateParams(params: ComicToPdfParams) {
        registerChanges()
        _params.update { params }
    }

    override fun createTargetFilename(): String =
        "${uri?.filename()?.substringBeforeLast('.') ?: timestamp()}.pdf"

    override fun saveTo(uri: Uri) {
        doSaving {
            val processed = pdfManager.convertComicBookToPdf(
                uri = _uri.value.toString(),
                passphrase = passphrase.takeIf(String::isNotBlank),
                params = params
            )

            fileController.transferBytes(
                fromUri = processed,
                toUri = uri.toString()
            ).onSuccess(::registerSave)
        }
    }

    override fun performSharing(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        prepareForSharing(
            onSuccess = { uris ->
                shareProvider.shareUri(
                    uri = uris.single().toString(),
                    type = MimeType.Pdf,
                    onComplete = onSuccess
                )
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
                        pdfManager.convertComicBookToPdf(
                            uri = _uri.value.toString(),
                            passphrase = passphrase.takeIf(String::isNotBlank),
                            params = params
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
        ): ComicToPdfToolComponent
    }
}

private fun Uri.isSupportedComicArchive(): Boolean =
    filename()
        ?.substringAfterLast('.', "")
        ?.lowercase() in SupportedComicExtensions

private val SupportedComicExtensions = setOf("cbr", "cbz", "cb7", "cbt")
