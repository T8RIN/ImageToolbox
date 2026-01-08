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

package com.t8rin.imagetoolbox.feature.watermarking.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.feature.watermarking.domain.HiddenWatermark

@Composable
internal fun HiddenWatermarkInfo(
    hiddenWatermark: HiddenWatermark?
) {
    val essentials = rememberLocalEssentials()

    AnimatedContent(
        targetState = hiddenWatermark,
        modifier = Modifier.fillMaxWidth()
    ) { hidden ->
        hidden?.let {
            PreferenceItemOverload(
                title = stringResource(
                    when (hidden) {
                        is HiddenWatermark.Text -> R.string.hidden_watermark_text_detected
                        is HiddenWatermark.Image -> R.string.hidden_watermark_image_detected
                    }
                ),
                subtitle = when (hidden) {
                    is HiddenWatermark.Text -> hidden.text
                    is HiddenWatermark.Image -> stringResource(R.string.this_image_was_hidden)
                },
                onClick = if (hidden is HiddenWatermark.Text) {
                    { essentials.copyToClipboard(hidden.text) }
                } else null,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                titleFontStyle = PreferenceItemDefaults.TitleFontStyleSmall,
                startIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                },
                endIcon = {
                    when (hidden) {
                        is HiddenWatermark.Text -> {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = null
                            )
                        }

                        is HiddenWatermark.Image -> {
                            Picture(
                                model = hidden.image,
                                shape = CloverShape,
                                modifier = Modifier.size(48.dp),
                                error = {
                                    Icon(
                                        imageVector = Icons.TwoTone.AddPhotoAlt,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CloverShape)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer
                                                    .copy(0.5f)
                                                    .compositeOver(MaterialTheme.colorScheme.surfaceContainer)
                                            )
                                            .padding(8.dp)
                                    )
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeDefaults.large
            )
        }
    }
}