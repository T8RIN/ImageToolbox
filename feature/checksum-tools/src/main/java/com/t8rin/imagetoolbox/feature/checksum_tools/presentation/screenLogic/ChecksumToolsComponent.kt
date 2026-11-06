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

package com.t8rin.imagetoolbox.feature.checksum_tools.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.checksum_tools.domain.ChecksumManager
import com.t8rin.imagetoolbox.feature.checksum_tools.domain.ChecksumSource
import com.t8rin.imagetoolbox.feature.checksum_tools.presentation.components.ChecksumPage
import com.t8rin.imagetoolbox.feature.checksum_tools.presentation.components.UriWithHash
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ChecksumToolsComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    private val checksumManager: ChecksumManager,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _hashingType = fileController.savable(
        scope = componentScope,
        initial = HashingType.entries.first()
    )
    val hashingType: HashingType by _hashingType

    private val _calculateFromUriPage: MutableState<ChecksumPage.CalculateFromUri> =
        mutableStateOf(ChecksumPage.CalculateFromUri.Empty)
    val calculateFromUriPage: ChecksumPage.CalculateFromUri by _calculateFromUriPage

    private val _calculateFromTextPage: MutableState<ChecksumPage.CalculateFromText> =
        mutableStateOf(ChecksumPage.CalculateFromText.Empty)
    val calculateFromTextPage: ChecksumPage.CalculateFromText by _calculateFromTextPage

    private val _compareWithUriPage: MutableState<ChecksumPage.CompareWithUri> =
        mutableStateOf(ChecksumPage.CompareWithUri.Empty)
    val compareWithUriPage: ChecksumPage.CompareWithUri by _compareWithUriPage

    private val _compareWithUrisPage: MutableState<ChecksumPage.CompareWithUris> =
        mutableStateOf(ChecksumPage.CompareWithUris.Empty)
    val compareWithUrisPage: ChecksumPage.CompareWithUris by _compareWithUrisPage


    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

    private var uriJob by smartJob()
    private var textJob by smartJob()
    private var comparisonJob by smartJob()

    fun setUri(uri: Uri) {
        _calculateFromUriPage.update { it.copy(uri = uri, checksum = "") }
        val type = hashingType
        uriJob = componentScope.launch {
            val checksum =
                checksumManager.calculateChecksum(type, ChecksumSource.Uri(uri.toString()))
            _calculateFromUriPage.update { it.copy(checksum = checksum) }
        }
    }

    fun setText(text: String) {
        _calculateFromTextPage.update { it.copy(text = text, checksum = "") }
        val type = hashingType
        textJob = componentScope.launch {
            val checksum = if (text.isEmpty()) "" else {
                checksumManager.calculateChecksum(type, ChecksumSource.Text(text))
            }
            _calculateFromTextPage.update { it.copy(checksum = checksum) }
        }
    }

    fun setDataForComparison(uri: Uri?) {
        comparisonJob = null
        _compareWithUriPage.update { it.copy(uri = uri, checksum = "", isCorrect = false) }
        if (uri == null) return
        val type = hashingType
        comparisonJob = componentScope.launch {
            val checksum =
                checksumManager.calculateChecksum(type, ChecksumSource.Uri(uri.toString()))
            _compareWithUriPage.update {
                it.copy(
                    checksum = checksum,
                    isCorrect = checksum.isNotEmpty() && checksum == it.targetChecksum
                )
            }
        }
    }

    fun setTargetChecksum(checksum: String) {
        _compareWithUriPage.update {
            it.copy(
                targetChecksum = checksum,
                isCorrect = it.checksum.isNotEmpty() && it.checksum == checksum
            )
        }
    }

    fun updateChecksumType(type: HashingType) {
        _hashingType.update { type }
        calculateFromUriPage.uri?.let(::setUri)
        calculateFromTextPage.text.let(::setText)
        compareWithUriPage.uri?.let(::setDataForComparison)
        setDataForBatchComparison(forceReload = true)
    }

    private var treeJob: Job? by smartJob {
        _filesLoadingProgress.update { -1f }
    }

    fun setDataForBatchComparison(
        uris: List<Uri> = compareWithUrisPage.uris.map { it.uri },
        targetChecksum: String = compareWithUrisPage.targetChecksum,
        forceReload: Boolean = false
    ) {
        _compareWithUrisPage.update {
            it.copy(
                targetChecksum = targetChecksum
            )
        }

        val targetUris = compareWithUrisPage.uris.map { it.uri }

        if (targetUris != uris || forceReload) {
            treeJob = componentScope.launch {
                delay(500)
                _filesLoadingProgress.update { 0f }

                val urisWithHash = uris.mapIndexed { index, uri ->
                    val checksum = checksumManager.calculateChecksum(
                        type = hashingType,
                        source = ChecksumSource.Uri(uri.toString())
                    )

                    _filesLoadingProgress.update {
                        (index + 1) / uris.size.toFloat()
                    }

                    UriWithHash(
                        uri = uri,
                        checksum = checksum
                    )
                }

                _compareWithUrisPage.update {
                    it.copy(
                        uris = urisWithHash
                    )
                }
                _filesLoadingProgress.update { -1f }
            }
        }
    }

    private val _filesLoadingProgress = mutableFloatStateOf(-1f)
    val filesLoadingProgress by _filesLoadingProgress

    fun setDataForBatchComparisonFromTree(uri: Uri) {
        treeJob = componentScope.launch {
            delay(500)
            _filesLoadingProgress.update { 0f }
            fileController.listFilesInDirectory(uri.toString())
                .map { it.toUri() }
                .let(::setDataForBatchComparison)
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
        ): ChecksumToolsComponent
    }

}