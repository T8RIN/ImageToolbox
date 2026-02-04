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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.rounded.NoteAdd
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.shareUris
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

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
    onClickUri: ((Uri) -> Unit)? = null,
    errorContent: @Composable BoxScope.(index: Int, width: Dp) -> Unit = { _, width ->
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier
                .size(width / 3f)
                .align(Alignment.Center),
            tint = MaterialTheme.colorScheme.primary
        )
    },
    showTransparencyChecker: Boolean = true,
    showScrimForNonSuccess: Boolean = true,
    filenameSource: (index: Int) -> Uri = { uris[it] },
    onNavigate: ((Screen) -> Unit)? = null
) {
    var previewUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    BoxWithConstraints {
        val size = uris.size + 1f

        val count = if (isPortrait) {
            size.coerceAtLeast(2f).coerceAtMost(3f)
        } else {
            size.coerceAtLeast(2f).coerceAtMost(8f)
        }

        val width = this.maxWidth / count - 4.dp * (count - 1)

        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            uris.forEachIndexed { index, uri ->
                if (uri != Uri.EMPTY) {
                    Box(
                        modifier = Modifier.container(
                            shape = ShapeDefaults.extraSmall,
                            resultPadding = 0.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    ) {
                        var isLoaded by remember(uri) {
                            mutableStateOf(false)
                        }
                        Picture(
                            model = uri,
                            error = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    content = {
                                        errorContent(index, width)
                                    }
                                )
                            },
                            onSuccess = { isLoaded = true },
                            modifier = Modifier
                                .then(
                                    if (onClickUri != null) {
                                        Modifier.hapticsClickable {
                                            onClickUri(uri)
                                        }
                                    } else {
                                        Modifier.hapticsClickable {
                                            previewUri = uri
                                        }
                                    }
                                )
                                .width(width)
                                .aspectRatio(1f),
                            showTransparencyChecker = showTransparencyChecker
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    takeColorFromScheme {
                                        scrim.copy(
                                            if (isLoaded || showScrimForNonSuccess) 0.5f
                                            else 0f
                                        )
                                    }
                                )
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
                                        .clip(ShapeDefaults.circle)
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
                            val filename = rememberFilename(filenameSource(index))

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
                                    shape = ShapeDefaults.extraSmall,
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

    if (onClickUri == null && onNavigate != null) {
        val context = LocalContext.current
        ImagePager(
            visible = previewUri != null,
            selectedUri = previewUri,
            uris = uris,
            onNavigate = onNavigate,
            onUriSelected = { previewUri = it },
            onShare = { context.shareUris(listOf(it)) },
            onDismiss = { previewUri = null }
        )
    }
}

fun Modifier.urisPreview(
    scrollState: ScrollState? = null
): Modifier = this.composed {
    val isPortrait by isPortraitOrientationAsState()

    if (!isPortrait) {
        Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    constraints = constraints.copy(
                        maxHeight = constraints.maxHeight + 48.dp.roundToPx()
                    )
                )
                layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                }
            }
            .enhancedVerticalScroll(scrollState ?: rememberScrollState())
    } else {
        Modifier
    }.padding(vertical = 24.dp)
}