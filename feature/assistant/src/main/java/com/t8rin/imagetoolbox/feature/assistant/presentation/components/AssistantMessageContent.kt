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

package com.t8rin.imagetoolbox.feature.assistant.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Help
import com.t8rin.imagetoolbox.core.resources.icons.OpenInNew
import com.t8rin.imagetoolbox.core.resources.icons.Refresh
import com.t8rin.imagetoolbox.core.resources.icons.Settings
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantMessage
import com.t8rin.imagetoolbox.feature.assistant.presentation.screenLogic.AssistantComponent

@Composable
internal fun AssistantMessageContent(
    message: AssistantMessage,
    component: AssistantComponent,
    isGenerating: Boolean,
    canRegenerate: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (message.question.isNotEmpty() || message.context.selectedUris.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.End)
                    .widthIn(max = 560.dp),
                shape = ShapeDefaults.large,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (message.context.selectedUris.isNotEmpty()) {
                        val visibleImages = message.context.selectedUris.size.coerceAtMost(4)
                        LazyRow(
                            modifier = Modifier.width(72.dp * visibleImages + 8.dp * (visibleImages - 1)),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            flingBehavior = enhancedFlingBehavior()
                        ) {
                            items(message.context.selectedUris) { uri ->
                                Picture(
                                    model = uri,
                                    modifier = Modifier.size(72.dp),
                                    shape = ShapeDefaults.small
                                )
                            }
                        }
                    }
                    if (message.question.isNotEmpty()) {
                        SelectionContainer { Text(message.question) }
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (message.answer.text.isNotBlank()) {
                SelectionContainer { AssistantAnswerText(message.answer.text) }
            }
            message.error?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (message.answer.text.isNotBlank() && !isGenerating) {
                AssistantLinks(
                    message = message,
                    component = component
                )
            }
            if (message.answer.text.isNotBlank() || isGenerating || canRegenerate) {
                AssistantMessageControls(
                    message = message,
                    component = component,
                    isGenerating = isGenerating,
                    canRegenerate = canRegenerate
                )
            }
        }
    }
}

@Composable
private fun AssistantLinks(message: AssistantMessage, component: AssistantComponent) {
    if (message.answer.tools.isEmpty() && message.answer.references.isEmpty() &&
        message.answer.settings.isEmpty()
    ) return
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        message.answer.tools.forEach { tool ->
            EnhancedChip(
                selected = false,
                onClick = { component.openTool(tool, message.context) },
                selectedColor = MaterialTheme.colorScheme.primaryContainer,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = stringResource(R.string.open_tool),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(tool.title)
                }
            }
        }
        message.answer.settings.forEach { setting ->
            EnhancedChip(
                selected = false,
                onClick = { component.openSetting(setting) },
                selectedColor = MaterialTheme.colorScheme.primaryContainer,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.settings),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(setting.title)
                }
            }
        }
        message.answer.references.forEach { reference ->
            EnhancedChip(
                selected = false,
                onClick = { component.openReference(reference) },
                selectedColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedColor = MaterialTheme.colorScheme.surfaceContainer,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Help,
                        contentDescription = stringResource(R.string.help_tips),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(reference.title, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun AssistantMessageControls(
    message: AssistantMessage,
    component: AssistantComponent,
    isGenerating: Boolean,
    canRegenerate: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (message.answer.text.isNotBlank() || canRegenerate) {
            Row(
                modifier = Modifier.container(
                    shape = ShapeDefaults.circle,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    resultPadding = 0.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (message.answer.text.isNotBlank()) {
                    EnhancedIconButton(onClick = { Clipboard.copy(message.answer.text) }) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(R.string.copy),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (canRegenerate) {
                    if (message.answer.text.isNotBlank()) {
                        VerticalDivider(Modifier.height(20.dp))
                    }
                    EnhancedIconButton(onClick = component::regenerate) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = stringResource(R.string.assistant_regenerate),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        if (isGenerating) {
            EnhancedCircularProgressIndicator(
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp),
                strokeWidth = 3.dp
            )
        }
    }
}