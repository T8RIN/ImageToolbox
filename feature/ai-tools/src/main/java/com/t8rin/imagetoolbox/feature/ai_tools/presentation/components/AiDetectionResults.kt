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

package com.t8rin.imagetoolbox.feature.ai_tools.presentation.components

import android.net.Uri
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.AiDetectionResult
import kotlin.math.roundToInt

@Composable
internal fun AiDetectionResults(
    results: List<AiDetectionPreviewResult>,
    onOpen: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSizeNoClip(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        results.forEach { result ->
            AiDetectionResultItem(
                result = result,
                onOpen = { onOpen(result.uri) }
            )
        }
    }
}

@Composable
private fun AiDetectionResultItem(
    result: AiDetectionPreviewResult,
    onOpen: () -> Unit
) {
    val probability = result.result.aiProbability
    val (verdict, verdictColor) = when (result.result.verdict) {
        AiDetectionResult.Verdict.LikelyAi -> {
            stringResource(R.string.ai_detector_likely_ai) to MaterialTheme.colorScheme.error
        }

        AiDetectionResult.Verdict.Uncertain -> {
            stringResource(R.string.ai_detector_uncertain) to MaterialTheme.colorScheme.tertiary
        }

        AiDetectionResult.Verdict.LikelyHuman -> {
            stringResource(R.string.ai_detector_likely_human) to MaterialTheme.colorScheme.primary
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val shape = shapeByInteraction(
        shape = ShapeDefaults.large,
        pressedShape = ShapeDefaults.pressed,
        interactionSource = interactionSource
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .container(
                shape = shape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                resultPadding = 0.dp
            )
            .hapticsClickable(
                onClick = onOpen,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(108.dp)
                .container(
                    shape = ShapeDefaults.small,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    resultPadding = 0.dp
                )
        ) {
            Picture(
                model = result.uri,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = rememberFilename(result.uri) ?: result.uri.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = verdict,
                color = verdictColor,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(
                    R.string.ai_probability,
                    "${(probability * 100).roundToInt()}%"
                ),
                style = MaterialTheme.typography.bodySmall
            )
            LinearWavyProgressIndicator(
                progress = { probability },
                modifier = Modifier.fillMaxWidth(),
                color = verdictColor,
                trackColor = verdictColor.copy(alpha = 0.18f),
                amplitude = {
                    if (it >= 0.05f) 0.8f else 0f
                }
            )
        }
    }
}
