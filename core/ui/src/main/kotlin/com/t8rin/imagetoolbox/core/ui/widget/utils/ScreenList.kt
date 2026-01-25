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

package com.t8rin.imagetoolbox.core.ui.widget.utils

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getExtension
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen

@Composable
internal fun List<Uri>.screenList(
    extraDataType: ExtraDataType?
): State<List<Screen>> {
    val uris = this

    fun Uri?.type(
        vararg extensions: String
    ): Boolean {
        if (this == null) return false

        val extension = getExtension() ?: return false

        return extensions.any(extension::contains)
    }

    val filesAvailableScreens by remember(uris) {
        derivedStateOf {
            if (uris.size > 1) {
                listOf(Screen.Zip(uris))
            } else {
                listOf(
                    Screen.Cipher(uris.firstOrNull()),
                    Screen.ChecksumTools(uris.firstOrNull()),
                    Screen.Zip(uris)
                )
            }
        }
    }
    val audioAvailableScreens by remember(uris) {
        derivedStateOf {
            listOf(
                Screen.AudioCoverExtractor(uris)
            ) + filesAvailableScreens
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
                Screen.GifTools(
                    Screen.GifTools.Type.GifToJxl(uris)
                ),
                Screen.GifTools(
                    Screen.GifTools.Type.GifToWebp(uris)
                )
            ) + filesAvailableScreens
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
                )
            ) + filesAvailableScreens
        }
    }
    val singleImageScreens by remember(uris) {
        derivedStateOf {
            listOf(
                Screen.SingleEdit(uris.firstOrNull()),
                Screen.ResizeAndConvert(uris),
                Screen.FormatConversion(uris),
                Screen.WeightResize(uris),
                Screen.Crop(uris.firstOrNull()),
                Screen.Filter(
                    type = Screen.Filter.Type.Basic(uris)
                ),
                Screen.Draw(uris.firstOrNull()),
                Screen.RecognizeText(
                    Screen.RecognizeText.Type.Extraction(uris.firstOrNull())
                ),
                Screen.EraseBackground(uris.firstOrNull()),
                Screen.Filter(
                    type = Screen.Filter.Type.Masking(uris.firstOrNull())
                ),
                Screen.AiTools(uris),
                Screen.MarkupLayers(uris.firstOrNull()),
                Screen.Watermarking(uris),
                Screen.ImageStitching(uris),
                Screen.ImageStacking(uris),
                Screen.ImageSplitting(uris.firstOrNull()),
                Screen.ImageCutter(uris),
                Screen.ScanQrCode(uriToAnalyze = uris.firstOrNull()),
                Screen.GradientMaker(uris),
                Screen.PdfTools(
                    Screen.PdfTools.Type.ImagesToPdf(uris)
                ),
                Screen.GifTools(
                    Screen.GifTools.Type.ImageToGif(uris)
                ),
                Screen.Base64Tools(uris.firstOrNull()),
                Screen.ImagePreview(uris),
                Screen.PickColorFromImage(uris.firstOrNull()),
                Screen.PaletteTools(uris.firstOrNull()),
                Screen.AsciiArt(uris.firstOrNull()),
                Screen.ApngTools(
                    Screen.ApngTools.Type.ImageToApng(uris)
                ),
                Screen.JxlTools(
                    Screen.JxlTools.Type.ImageToJxl(uris)
                ),
                Screen.SvgMaker(uris),
                Screen.EditExif(uris.firstOrNull()),
                Screen.DeleteExif(uris),
                Screen.LimitResize(uris)
            ).let { list ->
                val mergedList = list + filesAvailableScreens

                val uri = uris.firstOrNull()

                if (uri.type("png")) {
                    mergedList + Screen.ApngTools(
                        Screen.ApngTools.Type.ApngToImage(uris.firstOrNull())
                    )
                } else if (uri.type("jpg", "jpeg")) {
                    mergedList + Screen.JxlTools(
                        Screen.JxlTools.Type.JpegToJxl(uris)
                    )
                } else if (uri.type("jxl")) {
                    mergedList + Screen.JxlTools(
                        Screen.JxlTools.Type.JxlToJpeg(uris)
                    ) + Screen.JxlTools(
                        Screen.JxlTools.Type.JxlToImage(uris.firstOrNull())
                    )
                } else if (uri.type("webp")) {
                    mergedList + Screen.WebpTools(
                        Screen.WebpTools.Type.WebpToImage(uris.firstOrNull())
                    )
                } else mergedList
            }
        }
    }
    val multipleImageScreens by remember(uris) {
        derivedStateOf {
            mutableListOf(
                Screen.ResizeAndConvert(uris),
                Screen.WeightResize(uris),
                Screen.FormatConversion(uris),
                Screen.Filter(
                    type = Screen.Filter.Type.Basic(uris)
                ),
            ).apply {
                add(Screen.ImageStitching(uris))
                add(Screen.PdfTools(Screen.PdfTools.Type.ImagesToPdf(uris)))
                if (uris.size == 2) add(Screen.Compare(uris))
                if (uris.size in 1..10) {
                    add(Screen.CollageMaker(uris))
                }
                add(Screen.AiTools(uris))
                add(Screen.GradientMaker(uris))
                add(
                    Screen.RecognizeText(
                        Screen.RecognizeText.Type.WriteToFile(uris)
                    )
                )
                add(
                    Screen.RecognizeText(
                        Screen.RecognizeText.Type.WriteToMetadata(uris)
                    )
                )
                add(Screen.Watermarking(uris))
                add(
                    Screen.GifTools(
                        Screen.GifTools.Type.ImageToGif(uris)
                    )
                )
                add(Screen.ImageStacking(uris))
                add(Screen.ImageCutter(uris))
                add(Screen.ImagePreview(uris))
                add(Screen.LimitResize(uris))
                addAll(filesAvailableScreens)
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
                add(
                    Screen.WebpTools(
                        Screen.WebpTools.Type.ImageToWebp(uris)
                    )
                )
                add(Screen.DeleteExif(uris))
            }
        }
    }
    val imageScreens by remember(uris) {
        derivedStateOf {
            if (uris.size == 1) singleImageScreens
            else multipleImageScreens
        }
    }

    val textAvailableScreens by remember(extraDataType) {
        derivedStateOf {
            val text = (extraDataType as? ExtraDataType.Text)?.text ?: ""
            listOf(
                Screen.ScanQrCode(text),
                Screen.LoadNetImage(text)
            )
        }
    }

    val favoriteScreens = LocalSettingsState.current.favoriteScreenList

    return remember(
        favoriteScreens,
        extraDataType,
        uris,
        pdfAvailableScreens,
        audioAvailableScreens,
        imageScreens
    ) {
        derivedStateOf {
            when (extraDataType) {
                is ExtraDataType.Backup -> filesAvailableScreens
                is ExtraDataType.Text -> textAvailableScreens
                ExtraDataType.Audio -> audioAvailableScreens
                ExtraDataType.File -> filesAvailableScreens
                ExtraDataType.Gif -> gifAvailableScreens
                ExtraDataType.Pdf -> pdfAvailableScreens
                null, ExtraDataType.Jxl -> imageScreens
            }.sortedWith(compareBy(nullsLast()) { s -> favoriteScreens.find { it == s.id } })
        }
    }
}