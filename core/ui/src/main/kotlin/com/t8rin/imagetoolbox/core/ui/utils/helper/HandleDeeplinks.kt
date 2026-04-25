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
import com.t8rin.imagetoolbox.core.domain.BACKUP_FILE_EXT
import com.t8rin.imagetoolbox.core.domain.TEMPLATE_EXT
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Error
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getScreenExtra
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getScreenOpeningShortcut
import com.t8rin.imagetoolbox.core.ui.utils.helper.IntentUtils.parcelable
import com.t8rin.imagetoolbox.core.ui.utils.helper.IntentUtils.parcelableArrayList
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.filename

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
    val intent = this ?: return

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
        fun String?.isEndsWith(ext: String): Boolean = this?.lowercase().orEmpty().endsWith(ext)

        fun Uri?.isMarkupProject(): Boolean = this?.toString().isEndsWith(".itp") ||
                this?.filename().isEndsWith(".itp")

        fun Uri?.isTemplate(): Boolean = this?.toString().isEndsWith(TEMPLATE_EXT) ||
                this?.filename().isEndsWith(TEMPLATE_EXT)

        val startsWithImage = type?.startsWith("image/") == true
        val hasExtraFormats = clipData?.clipList()
            ?.any {
                it.toString().endsWith(".jxl") ||
                        it.toString().endsWith(".qoi") ||
                        it.toString().endsWith(".itp")
            } == true
        val dataHasExtraFormats = data.toString().let {
            it.endsWith(".jxl") || it.endsWith(".qoi") || it.endsWith(".itp")
        }

        when {
            data.isTemplate() -> onHasExtraDataType(ExtraDataType.Template(data.toString()))

            data.isMarkupProject() -> onNavigate(Screen.MarkupLayers(data))

            startsWithImage || hasExtraFormats || dataHasExtraFormats -> {
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
                                is Screen.PickColorFromImage -> onNavigate(
                                    Screen.PickColorFromImage(
                                        it
                                    )
                                )

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

                    Intent.ACTION_EDIT,
                    Intent.ACTION_INSERT,
                    Intent.ACTION_INSERT_OR_EDIT -> {
                        val uris =
                            clipData?.clipList() ?: data?.let { listOf(it) } ?: return@runCatching
                        if (type?.contains("gif") == true) {
                            onHasExtraDataType(ExtraDataType.Gif)
                        }
                        onGetUris(uris)
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
            }

            type != null -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)

                if (text != null) {
                    onHasExtraDataType(ExtraDataType.Text(text))
                    onGetUris(listOf())
                } else {
                    val isPdf = type.contains("pdf")
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
                                if (it.isMarkupProject()) {
                                    onNavigate(Screen.MarkupLayers(it))
                                    return
                                }

                                if (it.isTemplate()) {
                                    onHasExtraDataType(ExtraDataType.Template(it.toString()))
                                    return
                                }

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

                                if (uri.isMarkupProject()) {
                                    onNavigate(Screen.MarkupLayers(uri))
                                    return
                                }

                                if (uri.isTemplate()) {
                                    onHasExtraDataType(ExtraDataType.Template(uri.toString()))
                                    return
                                }

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
                        message = appContext.getString(R.string.unsupported_type, type),
                        icon = Icons.Outlined.Error
                    )
                }
            }

            else -> Unit
        }
    }.getOrNull() ?: AppToastHost.showToast(
        message = appContext.getString(R.string.something_went_wrong),
        icon = Icons.Outlined.Error
    )
}
