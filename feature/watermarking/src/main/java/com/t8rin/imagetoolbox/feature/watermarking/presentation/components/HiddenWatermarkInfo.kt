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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.resources.shapes.CloverShape
import com.t8rin.imagetoolbox.core.resources.utils.compositeOverSafe
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.feature.watermarking.domain.HiddenWatermark

@Composable
internal fun HiddenWatermarkInfo(
    hiddenWatermark: HiddenWatermark?,
    isLoading: Boolean,
    onImageClick: (HiddenWatermark.Image) -> Unit
) {
    AnimatedContent(
        targetState = isLoading to hiddenWatermark,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSizeNoClip()
    ) { (loading, hidden) ->
        if (loading) {
            HiddenWatermarkLoadingCard()
        } else {
            hidden?.let {
                HiddenWatermarkResultCard(
                    hiddenWatermark = it,
                    onImageClick = onImageClick
                )
            }
        }
    }
}

@Composable
private fun HiddenWatermarkLoadingCard() {
    HiddenWatermarkCard(
        title = stringResource(R.string.loading),
        subtitle = stringResource(R.string.checking_for_hidden_watermarks),
        onClick = null,
        endIcon = {
            EnhancedCircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                strokeWidth = 3.dp
            )
        }
    )
}

@Composable
private fun HiddenWatermarkResultCard(
    hiddenWatermark: HiddenWatermark,
    onImageClick: (HiddenWatermark.Image) -> Unit
) {
    HiddenWatermarkCard(
        title = stringResource(
            when (hiddenWatermark) {
                is HiddenWatermark.Text -> R.string.hidden_watermark_text_detected
                is HiddenWatermark.Image -> R.string.hidden_watermark_image_detected
            }
        ),
        subtitle = when (hiddenWatermark) {
            is HiddenWatermark.Text -> hiddenWatermark.text
            is HiddenWatermark.Image -> stringResource(R.string.this_image_was_hidden)
        },
        onClick = {
            when (hiddenWatermark) {
                is HiddenWatermark.Text -> Clipboard.copy(hiddenWatermark.text)
                is HiddenWatermark.Image -> onImageClick(hiddenWatermark)
            }
        },
        endIcon = {
            HiddenWatermarkEndIcon(hiddenWatermark)
        }
    )
}

@Composable
private fun HiddenWatermarkCard(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
    endIcon: @Composable () -> Unit
) {
    PreferenceItemOverload(
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
        titleFontStyle = PreferenceItemDefaults.TitleFontStyleSmall,
        startIcon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null
            )
        },
        endIcon = endIcon,
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.large
    )
}

@Composable
private fun HiddenWatermarkEndIcon(hiddenWatermark: HiddenWatermark) {
    when (hiddenWatermark) {
        is HiddenWatermark.Text -> {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = null
            )
        }

        is HiddenWatermark.Image -> {
            Picture(
                model = hiddenWatermark.uri,
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
                                    .compositeOverSafe(MaterialTheme.colorScheme.surfaceContainer)
                            )
                            .padding(8.dp)
                    )
                }
            )
        }
    }
}