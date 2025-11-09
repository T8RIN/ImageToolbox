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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.BackupFileExtension
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getScreenExtra
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getScreenOpeningShortcut
import com.t8rin.imagetoolbox.core.ui.utils.helper.IntentUtils.parcelable
import com.t8rin.imagetoolbox.core.ui.utils.helper.IntentUtils.parcelableArrayList
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.appContext

fun Intent?.handleDeeplinks(
    onStart: () -> Unit,
    onColdStart: () -> Unit,
    onShowToast: (message: String, icon: ImageVector) -> Unit,
    onNavigate: (Screen) -> Unit,
    onGetUris: (List<Uri>) -> Unit,
    onHasExtraDataType: (ExtraDataType) -> Unit,
    isHasUris: Boolean,
    onWantGithubReview: () -> Unit,
    isOpenEditInsteadOfPreview: Boolean,
) {
    if (this == null) return

    val intent = this

    onStart()
    if (intent.type != null && !isHasUris) onColdStart()

    if (intent.action == Intent.ACTION_BUG_REPORT) {
        onWantGithubReview()
        return
    }

    if (intent.getScreenOpeningShortcut(onNavigate)) return

    runCatching {
        val startsWithImage = intent.type?.startsWith("image/") == true
        val hasExtraFormats = intent.clipData?.clipList()
            ?.any {
                it.toString().endsWith(".jxl") || it.toString().endsWith(".qoi")
            } == true
        val dataHasExtraFormats = intent.data.toString().let {
            it.endsWith(".jxl") || it.endsWith(".qoi")
        }

        if ((startsWithImage || hasExtraFormats || dataHasExtraFormats)) {
            when (intent.action) {
                Intent.ACTION_VIEW -> {
                    val data = intent.data
                    val clipData = intent.clipData
                    val uris =
                        clipData?.clipList() ?: data?.let { listOf(it) } ?: return@runCatching

                    if (isOpenEditInsteadOfPreview) {
                        onGetUris(uris)
                    } else {
                        onNavigate(Screen.ImagePreview(uris))
                    }
                }

                Intent.ACTION_SEND -> {
                    intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                        when (intent.getScreenExtra()) {
                            is Screen.PickColorFromImage -> onNavigate(Screen.PickColorFromImage(it))
                            is Screen.PaletteTools -> onNavigate(Screen.PaletteTools(it))
                            else -> {
                                if (intent.type?.contains("gif") == true) {
                                    onHasExtraDataType(ExtraDataType.Gif)
                                }
                                onGetUris(listOf(it))
                            }
                        }
                    }
                }

                Intent.ACTION_SEND_MULTIPLE -> {
                    intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                        if (intent.type?.contains("gif") == true) {
                            onHasExtraDataType(ExtraDataType.Gif)
                            it.firstOrNull()?.let { uri ->
                                onGetUris(listOf(uri))
                            }
                        } else onGetUris(it)
                    }
                }

                else -> {
                    intent.data?.let {
                        if (intent.type?.contains("gif") == true) {
                            onHasExtraDataType(ExtraDataType.Gif)
                        }
                        onGetUris(listOf(it))
                    }
                }
            }
        } else if (intent.type != null) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            val multiplePdfs = intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM) != null

            if (
                intent.type?.contains("pdf") == true && !multiplePdfs
            ) {
                val uri = intent.data ?: intent.parcelable<Uri>(Intent.EXTRA_STREAM)
                uri?.let {
                    if (intent.action == Intent.ACTION_VIEW) {
                        onNavigate(Screen.PdfTools(Screen.PdfTools.Type.Preview(it)))
                    } else {
                        onHasExtraDataType(ExtraDataType.Pdf)
                        onGetUris(listOf(uri))
                    }
                }
            } else if (text != null) {
                onHasExtraDataType(ExtraDataType.Text(text))
                onGetUris(listOf())
            } else {
                val isAudio = intent.type?.startsWith("audio/") == true

                when (intent.action) {
                    Intent.ACTION_SEND_MULTIPLE -> {
                        intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (isAudio) {
                                onHasExtraDataType(ExtraDataType.Audio)
                                onGetUris(it)
                            } else {
                                onNavigate(Screen.Zip(it))
                            }
                        }
                    }

                    Intent.ACTION_SEND -> {
                        intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (it.toString().contains(BackupFileExtension, true)) {
                                onHasExtraDataType(ExtraDataType.Backup(it.toString()))
                                return
                            }
                            if (isAudio) {
                                onHasExtraDataType(ExtraDataType.Audio)
                            } else {
                                onHasExtraDataType(ExtraDataType.File)
                            }

                            onGetUris(listOf(it))
                        }
                    }

                    Intent.ACTION_VIEW -> {
                        val data = intent.data
                        val clipData = intent.clipData
                        val uris =
                            clipData?.clipList() ?: data?.let { listOf(it) } ?: emptyList()

                        if (uris.size == 1) {
                            val uri = uris.first()

                            if (uri.toString().contains(BackupFileExtension, true)) {
                                onHasExtraDataType(ExtraDataType.Backup(uri.toString()))
                                return
                            }

                            if (isAudio) {
                                onHasExtraDataType(ExtraDataType.Audio)
                            } else {
                                onHasExtraDataType(ExtraDataType.File)
                            }

                            onGetUris(uris)
                        } else if (uris.isNotEmpty()) {
                            if (isAudio) {
                                onHasExtraDataType(ExtraDataType.Audio)
                                onGetUris(uris)
                            } else {
                                onNavigate(Screen.Zip(uris))
                            }
                        } else {
                            Unit
                        }
                    }

                    else -> null
                } ?: onShowToast(
                    appContext.getString(R.string.unsupported_type, intent.type),
                    Icons.Rounded.ErrorOutline
                )
            }
        } else Unit
    }.getOrNull() ?: onShowToast(
        appContext.getString(R.string.something_went_wrong),
        Icons.Rounded.ErrorOutline
    )
}