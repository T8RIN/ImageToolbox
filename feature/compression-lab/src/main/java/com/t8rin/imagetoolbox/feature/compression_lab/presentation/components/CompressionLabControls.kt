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

package com.t8rin.imagetoolbox.feature.compression_lab.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Labs
import com.t8rin.imagetoolbox.core.resources.icons.Png
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic.CompressionLabComponent
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic.CompressionLabResult
import com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic.CompressionSearchMode
import java.util.Locale

@Composable
internal fun CompressionLabControls(
    component: CompressionLabComponent
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val formatOptions = CompressionLabComponent.AvailableFormats.map { format ->
            FormatOption(
                format = format,
                selected = format in component.selectedFormats
            )
        }
        DataSelector(
            value = formatOptions.first { it.selected },
            onValueChange = { component.toggleFormat(it.format) },
            entries = formatOptions,
            title = stringResource(R.string.compression_lab_formats),
            titleIcon = Icons.Outlined.Png,
            itemContentText = { it.format.title },
            itemEqualityDelegate = { _, item -> item.selected },
            shape = ShapeDefaults.large,
            modifier = Modifier.fillMaxWidth()
        )

        EnhancedButtonGroup(
            modifier = Modifier.container(ShapeDefaults.large),
            entries = CompressionSearchMode.entries,
            value = component.searchMode,
            itemContent = { Text(it.title()) },
            title = stringResource(R.string.compression_lab_search_mode),
            onValueChange = component::setSearchMode
        )

        AnimatedContent(
            targetState = component.searchMode
        ) { searchMode ->
            when (searchMode) {
                CompressionSearchMode.Manual -> EnhancedSliderItem(
                    value = component.manualQuality,
                    title = stringResource(R.string.quality),
                    valueRange = 0f..100f,
                    steps = 99,
                    shape = ShapeDefaults.large,
                    onValueChange = { component.setManualQuality(it.toInt()) }
                )

                CompressionSearchMode.TargetQuality -> EnhancedSliderItem(
                    value = component.targetQuality,
                    title = stringResource(R.string.compression_lab_target_quality),
                    valueSuffix = "% SSIM",
                    valueRange = 50f..100f,
                    steps = 49,
                    shape = ShapeDefaults.large,
                    onValueChange = { component.setTargetQuality(it.toInt()) }
                )

                CompressionSearchMode.TargetSize -> EnhancedSliderItem(
                    value = component.targetSizeKb,
                    title = stringResource(R.string.compression_lab_target_size),
                    valueSuffix = " kB",
                    valueRange = 10f..10_000f,
                    shape = ShapeDefaults.large,
                    onValueChange = { component.setTargetSizeKb(it.toInt()) }
                )
            }
        }

        FilledTonalButton(
            onClick = component::runLab,
            enabled = !component.isImageLoading && !component.isSaving,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.Labs,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.compression_lab_run),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (component.results.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                component.results.forEachIndexed { index, result ->
                    ResultCard(
                        result = result,
                        selected = index == component.selectedResultIndex,
                        index = index,
                        count = component.results.size,
                        onClick = { component.selectResult(index) }
                    )
                }
            }
        }

        if (component.failedFormats.isNotEmpty()) {
            Text(
                text = stringResource(
                    R.string.compression_lab_failed_formats,
                    component.failedFormats.joinToString()
                ),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun ResultCard(
    result: CompressionLabResult,
    selected: Boolean,
    index: Int,
    count: Int,
    onClick: () -> Unit
) {
    val locale = LocalLocale.current.platformLocale
    val encoderSetting = result.qualityValue?.let { "Q $it" }
        ?: stringResource(R.string.compression_lab_fixed_settings)
    val details = stringResource(
        R.string.compression_lab_result_details,
        encoderSetting,
        result.encodingTimeMillis,
        String.format(locale, "%.4f", result.ssim),
        result.psnr.toDisplayString(locale)
    )
    val targetWarning = if (result.targetSatisfied) {
        null
    } else {
        stringResource(R.string.compression_lab_target_not_reached)
    }

    PreferenceItem(
        title = "${result.format.title} · ${humanFileSize(result.sizeBytes)}",
        subtitle = listOfNotNull(details, targetWarning).joinToString("\n"),
        endIcon = if (selected) {
            Icons.Rounded.RadioButtonChecked
        } else {
            Icons.Rounded.RadioButtonUnchecked
        },
        onClick = onClick,
        shape = ShapeDefaults.byIndex(index, count),
        containerColor = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        modifier = Modifier.fillMaxWidth()
    )
}

private data class FormatOption(
    val format: ImageFormat,
    val selected: Boolean
)

@Composable
private fun CompressionSearchMode.title(): String = stringResource(
    when (this) {
        CompressionSearchMode.Manual -> R.string.compression_lab_mode_manual
        CompressionSearchMode.TargetQuality -> R.string.compression_lab_mode_target_quality
        CompressionSearchMode.TargetSize -> R.string.compression_lab_mode_target_size
    }
)

private fun Double.toDisplayString(locale: Locale): String = if (isInfinite()) {
    "∞ dB"
} else {
    String.format(locale, "%.2f dB", this)
}
