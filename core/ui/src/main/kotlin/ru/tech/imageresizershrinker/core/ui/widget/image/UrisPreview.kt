/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.image

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.rounded.NoteAdd
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText

@Composable
fun UrisPreview(
    modifier: Modifier = Modifier,
    uris: List<Uri>,
    isPortrait: Boolean,
    onRemoveUri: ((Uri) -> Unit)?,
    onAddUris: (() -> Unit)?,
    isAddUrisVisible: Boolean = true,
    addUrisContent: @Composable BoxScope.(width: Dp) -> Unit = { width ->
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.NoteAdd,
            contentDescription = stringResource(R.string.add),
            modifier = Modifier.size(width / 3f)
        )
    },
    onClickUri: ((Uri) -> Unit)? = null
) {
    val context = LocalContext.current

    BoxWithConstraints {
        val size = uris.size + 1f

        val count = if (isPortrait) {
            size.coerceAtLeast(2f).coerceAtMost(3f)
        } else {
            size.coerceAtLeast(2f).coerceAtMost(8f)
        }

        val width = this.maxWidth / count - 4.dp * (count - 1)

        ContextualFlowRow(
            modifier = modifier,
            itemCount = uris.size + 1,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) { index ->
            val uri = uris.getOrNull(index)
            if (uri != null && uri != Uri.EMPTY) {
                Box(
                    modifier = Modifier.container(
                        shape = RoundedCornerShape(4.dp),
                        resultPadding = 0.dp,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Picture(
                        model = uri,
                        error = {
                            Box {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(width / 3f)
                                        .align(Alignment.Center),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier
                            .then(
                                if (onClickUri != null) {
                                    Modifier.hapticsClickable {
                                        onClickUri(uri)
                                    }
                                } else Modifier
                            )
                            .width(width)
                            .aspectRatio(1f)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(0.5f)),
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.TopStart)
                        )
                        if (onRemoveUri != null) {
                            Icon(
                                imageVector = Icons.Rounded.RemoveCircleOutline,
                                contentDescription = stringResource(R.string.remove),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.scrim.copy(
                                            animateFloatAsState(if (uris.size > 1) 0.2f else 0f).value
                                        )
                                    )
                                    .hapticsClickable(
                                        enabled = uris.size > 1
                                    ) {
                                        onRemoveUri(uri)
                                    }
                                    .padding(4.dp)
                                    .align(Alignment.TopEnd),
                                tint = Color.White.copy(
                                    animateFloatAsState(if (uris.size > 1) 0.7f else 0f).value
                                ),
                            )
                        }
                        val filename by remember(uri) {
                            derivedStateOf {
                                context.getFilename(uri)
                            }
                        }
                        filename?.let {
                            AutoSizeText(
                                text = it,
                                style = LocalTextStyle.current.copy(
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.End
                                ),
                                maxLines = 3,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }
                }
            } else {
                AnimatedVisibility(visible = isAddUrisVisible) {
                    Box(
                        modifier = Modifier
                            .container(
                                shape = RoundedCornerShape(4.dp),
                                resultPadding = 0.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                            .width(width)
                            .aspectRatio(1f)
                            .then(
                                if (onAddUris != null) {
                                    Modifier.hapticsClickable(onClick = onAddUris)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center,
                        content = {
                            addUrisContent(width)
                        }
                    )
                }
            }
        }
    }
}
