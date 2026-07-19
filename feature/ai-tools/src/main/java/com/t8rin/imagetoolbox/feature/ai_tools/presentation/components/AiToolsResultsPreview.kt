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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.RemoveCircle
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun AiToolsResultsPreview(
    results: List<AiToolsPreviewResult>,
    onCompare: (AiToolsPreviewResult) -> Unit,
    onRemove: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSizeNoClip(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        results.forEachIndexed { index, result ->
            AiToolsResultItem(
                index = index,
                result = result,
                onCompare = { onCompare(result) },
                onRemove = { onRemove(result.cachedUri) }
            )
        }
    }
}

@Composable
private fun AiToolsResultItem(
    index: Int,
    result: AiToolsPreviewResult,
    onCompare: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .container(
                shape = ShapeDefaults.large,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                resultPadding = 0.dp
            )
            .hapticsClickable(onClick = onCompare)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EnhancedBadge(
                modifier = Modifier.size(24.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text(
                    text = (index + 1).toString(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Text(
                text = rememberFilename(result.originalUri) ?: result.originalUri.toString(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
            EnhancedIconButton(
                onClick = onRemove,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                forceMinimumInteractiveComponentSize = false,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.RemoveCircle,
                    contentDescription = stringResource(R.string.remove),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResultImage(
                uri = result.originalUri,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.scale(-1f)
            )
            ResultImage(
                uri = result.cachedUri,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ResultImage(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .container(
                shape = ShapeDefaults.small,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                resultPadding = 0.dp
            )
    ) {
        Picture(
            model = uri,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
@Preview
private fun Preview() = ImageToolboxThemeForPreview(true) {
    AiToolsResultsPreview(
        results = List(10) {
            AiToolsPreviewResult(
                originalUri = "212121112".toUri(),
                cachedUri = "".toUri(),
                imageInfo = ImageInfo()
            )
        },
        onCompare = {},
        onRemove = {}
    )
}