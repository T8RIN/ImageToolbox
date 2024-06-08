/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import java.util.Locale

@Composable
internal fun List<Uri>.screenList(
    extraImageType: String?
): State<List<Screen>> {
    val uris = this
    val context = LocalContext.current

    fun Uri?.type(
        vararg extensions: String
    ) = extensions.any { ext ->
        context
            .getExtension(this.toString())
            ?.contains(ext) == true
    }

    val filesAvailableScreens by remember(uris) {
        derivedStateOf {
            listOf(
                Screen.Cipher(uris.firstOrNull()),
                Screen.Zip(uris)
            )
        }
    }
    val gifAvailableScreens by remember(uris) {
        derivedStateOf {
            listOf(
                Screen.GifTools(
                    Screen.GifTools.Type.GifToImage(
                        uris.firstOrNull()
                    )
                ),
                Screen.Cipher(uris.firstOrNull()),
                Screen.Zip(uris)
            )
        }
    }
    val pdfAvailableScreens by remember(uris) {
        derivedStateOf {
            listOf(
                Screen.PdfTools(
                    Screen.PdfTools.Type.Preview(
                        uris.firstOrNull()
                    )
                ),
                Screen.PdfTools(
                    Screen.PdfTools.Type.PdfToImages(
                        uris.firstOrNull()
                    )
                ),
                Screen.Cipher(uris.firstOrNull()),
                Screen.Zip(uris)
            )
        }
    }
    val singleImageScreens by remember(uris) {
        derivedStateOf {
            listOf(
                Screen.SingleEdit(uris.firstOrNull()),
                Screen.ResizeAndConvert(uris),
                Screen.FormatConversion(uris),
                Screen.ResizeByBytes(uris),
                Screen.Crop(uris.firstOrNull()),
                Screen.Filter(
                    type = Screen.Filter.Type.Basic(uris)
                ),
                Screen.Draw(uris.firstOrNull()),
                Screen.RecognizeText(uris.firstOrNull()),
                Screen.EraseBackground(uris.firstOrNull()),
                Screen.Filter(
                    type = Screen.Filter.Type.Masking(uris.firstOrNull())
                ),
                Screen.ImageStitching(uris),
                Screen.Watermarking(uris),
                Screen.GradientMaker(uris),
                Screen.PdfTools(
                    Screen.PdfTools.Type.ImagesToPdf(uris)
                ),
                Screen.GifTools(
                    Screen.GifTools.Type.ImageToGif(uris)
                ),
                Screen.Cipher(uris.firstOrNull()),
                Screen.ImagePreview(uris),
                Screen.PickColorFromImage(uris.firstOrNull()),
                Screen.GeneratePalette(uris.firstOrNull()),
                Screen.ApngTools(
                    Screen.ApngTools.Type.ImageToApng(uris)
                ),
                Screen.JxlTools(
                    Screen.JxlTools.Type.ImageToJxl(uris)
                ),
                Screen.SvgMaker(uris),
                Screen.Zip(uris),
                Screen.DeleteExif(uris),
                Screen.LimitResize(uris)
            ).let {
                val uri = uris.firstOrNull()

                if (uri.type("png")) {
                    it + Screen.ApngTools(
                        Screen.ApngTools.Type.ApngToImage(uris.firstOrNull())
                    )
                } else if (uri.type("jpg", "jpeg")) {
                    it + Screen.JxlTools(
                        Screen.JxlTools.Type.JpegToJxl(uris)
                    )
                } else if (uri.type("jxl")) {
                    it + Screen.JxlTools(
                        Screen.JxlTools.Type.JxlToJpeg(uris)
                    ) + Screen.JxlTools(
                        Screen.JxlTools.Type.JxlToImage(uris.firstOrNull())
                    )
                } else it
            }
        }
    }
    val multipleImagesScreens by remember(uris) {
        derivedStateOf {
            mutableListOf(
                Screen.ResizeAndConvert(uris),
                Screen.ResizeByBytes(uris),
                Screen.FormatConversion(uris),
                Screen.Filter(
                    type = Screen.Filter.Type.Basic(uris)
                )
            ).apply {
                add(Screen.ImageStitching(uris))
                add(Screen.PdfTools(Screen.PdfTools.Type.ImagesToPdf(uris)))
                if (uris.size == 2) add(Screen.Compare(uris))
                add(Screen.GradientMaker(uris))
                add(Screen.Watermarking(uris))
                add(
                    Screen.GifTools(
                        Screen.GifTools.Type.ImageToGif(uris)
                    )
                )
                add(Screen.ImagePreview(uris))
                add(Screen.LimitResize(uris))
                add(Screen.Zip(uris))
                add(Screen.SvgMaker(uris))

                var haveJpeg = false
                var haveJxl = false

                for (uri in uris) {
                    if (uri.type("jpg", "jpeg")) {
                        haveJpeg = true
                    } else if (uri.type("jxl")) {
                        haveJxl = true
                    }
                    if (haveJpeg && haveJxl) break
                }

                if (haveJpeg) {
                    add(
                        Screen.JxlTools(
                            Screen.JxlTools.Type.JpegToJxl(uris)
                        )
                    )
                } else if (haveJxl) {
                    add(
                        Screen.JxlTools(
                            Screen.JxlTools.Type.JxlToJpeg(uris)
                        )
                    )
                    add(
                        Screen.JxlTools(
                            Screen.JxlTools.Type.JxlToImage(uris.firstOrNull())
                        )
                    )
                }
                add(
                    Screen.JxlTools(
                        Screen.JxlTools.Type.ImageToJxl(uris)
                    )
                )
                add(
                    Screen.ApngTools(
                        Screen.ApngTools.Type.ImageToApng(uris)
                    )
                )
                add(Screen.DeleteExif(uris))
            }
        }
    }

    val textAvailableScreens by remember(extraImageType) {
        derivedStateOf {
            listOf(
                Screen.ScanQrCode(extraImageType ?: ""),
                Screen.LoadNetImage(extraImageType ?: "")
            )
        }
    }

    return remember(
        extraImageType,
        uris,
        pdfAvailableScreens,
        singleImageScreens,
        multipleImagesScreens
    ) {
        derivedStateOf {
            when {
                extraImageType == "pdf" -> pdfAvailableScreens
                extraImageType == "gif" -> gifAvailableScreens
                extraImageType == "file" -> filesAvailableScreens
                uris.size == 1 -> singleImageScreens
                uris.size >= 2 -> multipleImagesScreens
                extraImageType != null -> textAvailableScreens

                else -> multipleImagesScreens
            }
        }
    }
}

private fun Context.getExtension(
    uri: String
): String? {
    if (uri.endsWith(".jxl")) return "jxl"
    return if (ContentResolver.SCHEME_CONTENT == uri.toUri().scheme) {
        MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(
                contentResolver.getType(uri.toUri())
            )
    } else {
        MimeTypeMap.getFileExtensionFromUrl(uri).lowercase(Locale.getDefault())
    }?.replace(".", "")
}