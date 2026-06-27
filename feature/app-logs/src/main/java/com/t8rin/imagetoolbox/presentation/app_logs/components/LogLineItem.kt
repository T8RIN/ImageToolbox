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

package com.t8rin.imagetoolbox.presentation.app_logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.utils.LogLineReference
import com.t8rin.imagetoolbox.core.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun LogLineItem(
    line: LogLineReference,
    query: String
) {
    val text by produceState<String?>(initialValue = null, line) {
        value = withContext(Dispatchers.IO) {
            Logger.readLogLine(line)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.mini,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = line.number.toString(),
                modifier = Modifier.widthIn(min = 44.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )
            SelectionContainer(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = highlightedLogLine(
                        text = text.orEmpty(),
                        query = query
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun highlightedLogLine(
    text: String,
    query: String
): AnnotatedString {
    val background = MaterialTheme.colorScheme.tertiary
    val content = MaterialTheme.colorScheme.onTertiary

    return remember(
        text,
        query,
        background,
        content
    ) {
        if (query.isBlank()) {
            AnnotatedString(text)
        } else {
            buildAnnotatedString {
                var startIndex = 0

                while (startIndex < text.length) {
                    val matchIndex = text.indexOf(
                        string = query,
                        startIndex = startIndex,
                        ignoreCase = true
                    )

                    if (matchIndex == -1) {
                        append(text.substring(startIndex))
                        break
                    }

                    append(text.substring(startIndex, matchIndex))
                    withStyle(
                        SpanStyle(
                            background = background,
                            color = content,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(text.substring(matchIndex, matchIndex + query.length))
                    }
                    startIndex = matchIndex + query.length
                }
            }
        }
    }
}