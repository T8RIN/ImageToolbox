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

package com.t8rin.imagetoolbox.core.ui.widget.palette_selection

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileExport
import com.t8rin.imagetoolbox.core.resources.icons.FileImport
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.SaveResultHandler
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteColor
import com.t8rin.palette.PaletteFormat
import com.t8rin.palette.decode
import com.t8rin.palette.getCoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun GradientPaletteImportExport(
    value: GradientPalette,
    onValueChange: (GradientPalette) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var loadingJob by remember { mutableStateOf<Job?>(null) }

    val picker = rememberFilePicker { uri: Uri ->
        loadingJob = scope.launch {
            isLoading = true
            try {
                val result = withContext(Dispatchers.IO) {
                    runCatching {
                        val data = context.contentResolver.openInputStream(uri)?.use {
                            it.readBytes()
                        } ?: throw PaletteCoderException.InvalidFormat()
                        val fallbackFormats = PaletteFormat.entries - setOf(
                            PaletteFormat.CSV,
                            PaletteFormat.HEX_RGBA
                        ) + listOf(PaletteFormat.HEX_RGBA, PaletteFormat.CSV)
                        val formats = (PaletteFormat.matchingFilename(
                            uri.filename(context) ?: uri.toString()
                        ) + fallbackFormats).distinct()
                        val colors = formats.firstNotNullOfOrNull { format ->
                            ensureActive()
                            runCatching {
                                format.getCoder().decode(data).allColors().map {
                                    ColorModel(it.toArgb())
                                }
                            }.getOrNull()?.takeIf { it.isNotEmpty() }
                        } ?: throw PaletteCoderException.InvalidFormat()

                        GradientPalette.fromColors(
                            if (colors.size == 1) colors + colors else colors
                        )
                    }
                }
                result.onSuccess(onValueChange).onFailure(AppToastHost::showFailureToast)
            } finally {
                isLoading = false
            }
        }
    }
    val creator = rememberFileCreator(
        mimeType = MimeType.Single("application/json"),
        onSuccess = { uri ->
            loadingJob = scope.launch {
                isLoading = true
                try {
                    val result = withContext(Dispatchers.IO) {
                        runCatching {
                            val palette = Palette(
                                colors = value.colors.map { PaletteColor(color = it.toColor()) }
                            )
                            context.contentResolver.openOutputStream(uri, "wt")?.use {
                                PaletteFormat.JSON.getCoder().encode(palette, it)
                            } ?: throw PaletteCoderException.InvalidFormat()
                        }
                    }
                    result.onSuccess {
                        SaveResultHandler.parseFileSaveResult(
                            SaveResult.Success(savingPath = uri.toString())
                        )
                    }.onFailure(AppToastHost::showFailureToast)
                } finally {
                    isLoading = false
                }
            }
        }
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .container(shape = ShapeDefaults.extraLarge, resultPadding = 8.dp)
            .height(IntrinsicSize.Max)
    ) {
        PreferenceRow(
            title = stringResource(R.string.import_word),
            onClick = picker::pickFile,
            enabled = !isLoading,
            shape = ShapeDefaults.start,
            titleFontStyle = PreferenceItemDefaults.TitleFontStyleCentered,
            startIcon = Icons.Rounded.FileImport,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface
        )
        PreferenceRow(
            title = stringResource(R.string.export),
            onClick = { creator.make("Palette_${timestamp()}.json") },
            enabled = !isLoading,
            shape = ShapeDefaults.end,
            titleFontStyle = PreferenceItemDefaults.TitleFontStyleCentered,
            startIcon = Icons.Rounded.FileExport,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface
        )
    }

    LoadingDialog(
        visible = isLoading,
        onCancelLoading = { loadingJob?.cancel() }
    )
}