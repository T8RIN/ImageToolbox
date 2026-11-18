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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Script
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ProvideTypography
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun FontCoverageBadges(
    coverage: Map<Script, Float>,
    variable: Boolean?,
    selected: Boolean,
    source: String?
) {
    ProvideTypography(LocalSettingsState.current.font) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            if (source != null) FontBadge(
                label = source,
                containerColor = takeColorFromScheme {
                    if (selected) secondary else secondaryContainer
                },
                contentColor = takeColorFromScheme {
                    if (selected) onSecondary else onSecondaryContainer
                }
            )
            if (variable != null) FontBadge(
                label = stringResource(if (variable) R.string.font_variable else R.string.regular),
                containerColor = takeColorFromScheme {
                    if (selected) primary else primaryContainer
                },
                contentColor = takeColorFromScheme {
                    if (selected) onPrimary else onPrimaryContainer
                }
            )
            if (coverage.isNotEmpty()) fontScriptLabels.sortedBy { (script, _) ->
                val percent = coverage[script] ?: 0f
                when {
                    percent >= 100f -> 0
                    percent > 0f -> 1
                    else -> 2
                }
            }.forEach { (script, label) ->
                val percent = coverage[script] ?: 0f
                if (percent >= 50f || script in listOf(Script.Latin, Script.Cyrillic, Script.Han)) {
                    val supported = percent >= 100f
                    FontBadge(
                        label = stringResource(label) + if (percent > 0f && !supported) " ≈" else "",
                        unavailable = percent == 0f,
                        containerColor = takeColorFromScheme {
                            when {
                                supported -> if (selected) tertiary.blend(secondary, 0.3f)
                                else tertiaryContainer.blend(secondaryContainer, 0.3f)

                                percent > 0f -> if (selected) secondary else secondaryContainer
                                else -> if (selected) surface else surfaceVariant
                            }
                        },
                        contentColor = takeColorFromScheme {
                            when {
                                supported -> if (selected) onTertiary.blend(onSecondary, 0.65f)
                                else onTertiaryContainer.blend(onSecondaryContainer, 0.65f)

                                percent > 0f -> if (selected) onSecondary else onSecondaryContainer
                                else -> if (selected) onSurface else onSurfaceVariant
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun FontBadge(
    label: String,
    containerColor: Color,
    contentColor: Color,
    unavailable: Boolean = false,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .height(18.dp)
            .container(
                color = containerColor,
                shape = ShapeDefaults.circle,
                autoShadowElevation = 0.dp,
                resultPadding = 0.dp
            )
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val color = contentColor.copy(alpha = if (unavailable) 0.5f else 1f)
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = color
            )
        }
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 12.sp),
            textDecoration = if (unavailable) TextDecoration.LineThrough else null
        )
    }
}

internal val fontScriptLabels = listOf(
    Script.Latin to R.string.font_script_latin,
    Script.Cyrillic to R.string.font_script_cyrillic,
    Script.Han to R.string.font_script_han,
    Script.Hiragana to R.string.font_script_hiragana,
    Script.Katakana to R.string.font_script_katakana,
    Script.Hangul to R.string.font_script_hangul,
    Script.Greek to R.string.font_script_greek,
    Script.Arabic to R.string.font_script_arabic,
    Script.Hebrew to R.string.font_script_hebrew,
    Script.Devanagari to R.string.font_script_devanagari,
    Script.Bengali to R.string.font_script_bengali,
    Script.Tamil to R.string.font_script_tamil,
    Script.Telugu to R.string.font_script_telugu,
    Script.Thai to R.string.font_script_thai,
    Script.Armenian to R.string.font_script_armenian,
    Script.Georgian to R.string.font_script_georgian,
    Script.Khmer to R.string.font_script_khmer,
    Script.Gujarati to R.string.font_script_gujarati,
    Script.Gurmukhi to R.string.font_script_gurmukhi,
    Script.Kannada to R.string.font_script_kannada,
    Script.Malayalam to R.string.font_script_malayalam,
    Script.Sinhala to R.string.font_script_sinhala,
    Script.Lao to R.string.font_script_lao,
    Script.Myanmar to R.string.font_script_myanmar,
    Script.Ethiopic to R.string.font_script_ethiopic,
    Script.Oriya to R.string.font_script_oriya,
    Script.Syriac to R.string.font_script_syriac,
    Script.Thaana to R.string.font_script_thaana,
    Script.Tibetan to R.string.font_script_tibetan,
    Script.Mongolian to R.string.font_script_mongolian
)
