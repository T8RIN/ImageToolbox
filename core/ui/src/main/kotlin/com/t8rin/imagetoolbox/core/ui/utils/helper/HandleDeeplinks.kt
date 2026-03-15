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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import com.t8rin.imagetoolbox.core.domain.BACKUP_FILE_EXT
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
    val type = intent.type
    if (type != null && !isHasUris) onColdStart()

    val action = intent.action

    if (action == Intent.ACTION_BUG_REPORT) {
        onWantGithubReview()
        return
    }

    if (intent.getScreenOpeningShortcut(onNavigate)) return

    val data = intent.data
    val clipData = intent.clipData

    runCatching {
        val startsWithImage = type?.startsWith("image/") == true
        val hasExtraFormats = clipData?.clipList()
            ?.any {
                it.toString().endsWith(".jxl") || it.toString().endsWith(".qoi")
            } == true
        val dataHasExtraFormats = data.toString().let {
            it.endsWith(".jxl") || it.endsWith(".qoi")
        }

        if ((startsWithImage || hasExtraFormats || dataHasExtraFormats)) {
            when (action) {
                Intent.ACTION_VIEW -> {
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
                                if (type?.contains("gif") == true) {
                                    onHasExtraDataType(ExtraDataType.Gif)
                                }
                                onGetUris(listOf(it))
                            }
                        }
                    }
                }

                Intent.ACTION_SEND_MULTIPLE -> {
                    intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                        if (type?.contains("gif") == true) {
                            onHasExtraDataType(ExtraDataType.Gif)
                            it.firstOrNull()?.let { uri ->
                                onGetUris(listOf(uri))
                            }
                        } else onGetUris(it)
                    }
                }

                else -> {
                    data?.let {
                        if (type?.contains("gif") == true) {
                            onHasExtraDataType(ExtraDataType.Gif)
                        }
                        onGetUris(listOf(it))
                    }
                }
            }
        } else if (type != null) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            val isPdf = type.contains("pdf")

            if (text != null) {
                onHasExtraDataType(ExtraDataType.Text(text))
                onGetUris(listOf())
            } else {
                val isAudio = type.startsWith("audio/")

                when (action) {
                    Intent.ACTION_SEND_MULTIPLE -> {
                        intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                            when {
                                isAudio -> {
                                    onHasExtraDataType(ExtraDataType.Audio)
                                    onGetUris(it)
                                }

                                isPdf -> {
                                    onHasExtraDataType(ExtraDataType.Pdf)
                                    onGetUris(it)
                                }

                                else -> onNavigate(Screen.Zip(it))
                            }
                        }
                    }

                    Intent.ACTION_SEND -> {
                        intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                            if (it.toString().contains(BACKUP_FILE_EXT, true)) {
                                onHasExtraDataType(ExtraDataType.Backup(it.toString()))
                                return
                            }
                            when {
                                isAudio -> onHasExtraDataType(ExtraDataType.Audio)
                                isPdf -> onHasExtraDataType(ExtraDataType.Pdf)
                                else -> onHasExtraDataType(ExtraDataType.File)
                            }

                            onGetUris(listOf(it))
                        }
                    }

                    Intent.ACTION_VIEW -> {
                        val uris =
                            clipData?.clipList() ?: data?.let { listOf(it) }
                            ?: listOfNotNull(intent.parcelable<Uri>(Intent.EXTRA_STREAM))

                        if (uris.size == 1) {
                            val uri = uris.first()

                            if (uri.toString().contains(BACKUP_FILE_EXT, true)) {
                                onHasExtraDataType(ExtraDataType.Backup(uri.toString()))
                                return
                            }

                            when {
                                isPdf -> {
                                    onNavigate(Screen.PdfTools.Preview(uri))
                                    return
                                }

                                isAudio -> {
                                    onHasExtraDataType(ExtraDataType.Audio)
                                }

                                else -> {
                                    onHasExtraDataType(ExtraDataType.File)
                                }
                            }

                            onGetUris(uris)
                        } else if (uris.isNotEmpty()) {
                            when {
                                isPdf -> {
                                    onHasExtraDataType(ExtraDataType.Pdf)
                                    onGetUris(uris)
                                }

                                isAudio -> {
                                    onHasExtraDataType(ExtraDataType.Audio)
                                    onGetUris(uris)
                                }

                                else -> {
                                    onNavigate(Screen.Zip(uris))
                                }
                            }
                        } else {
                            Unit
                        }
                    }

                    else -> null
                } ?: AppToastHost.showToast(
                    appContext.getString(R.string.unsupported_type, type),
                    Icons.Rounded.ErrorOutline
                )
            }
        } else Unit
    }.getOrNull() ?: AppToastHost.showToast(
        appContext.getString(R.string.something_went_wrong),
        Icons.Rounded.ErrorOutline
    )
}