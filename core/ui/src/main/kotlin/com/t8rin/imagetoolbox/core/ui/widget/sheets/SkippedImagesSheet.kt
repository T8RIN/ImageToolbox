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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.WarningAmber
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppSkippedImagesHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.shareUris
import com.t8rin.imagetoolbox.core.ui.utils.helper.SkippedImagesHostState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagePager
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.path

@Composable
fun SkippedImagesSheetHost(
    hostState: SkippedImagesHostState = AppSkippedImagesHost.state,
    onNavigate: (Screen) -> Unit
) {
    SkippedImagesSheet(
        uris = hostState.skippedImageUris,
        onNavigate = onNavigate,
        onDismiss = hostState::dismiss
    )
}

@Composable
private fun SkippedImagesSheet(
    uris: List<Uri>,
    onNavigate: (Screen) -> Unit,
    onDismiss: () -> Unit
) {
    EnhancedModalBottomSheet(
        visible = uris.isNotEmpty(),
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {
            TitleItem(
                text = stringResource(
                    R.string.skipped_saving_count,
                    uris.size
                ),
                icon = Icons.Outlined.WarningAmber
            )
        },
        confirmButton = {
            EnhancedButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        var selectedUri by remember {
            mutableStateOf<Uri?>(null)
        }
        val context = LocalContext.current

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                item {
                    InfoContainer(
                        text = stringResource(R.string.skipped_images_warning),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.8f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.8f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                itemsIndexed(
                    items = uris,
                    key = { _, uri -> uri.toString() }
                ) { index, uri ->
                    SkippedImageItem(
                        uri = uri,
                        shape = ShapeDefaults.byIndex(
                            index = index,
                            size = uris.size
                        ),
                        onClick = {
                            selectedUri = uri
                        }
                    )
                }
            }
        }

        ImagePager(
            visible = selectedUri != null,
            selectedUri = selectedUri,
            uris = uris,
            onNavigate = {
                selectedUri = null
                onDismiss()
                onNavigate(it)
            },
            onUriSelected = {
                selectedUri = it
            },
            onShare = {
                context.shareUris(listOf(it))
            },
            onDismiss = {
                selectedUri = null
            }
        )
    }
}

@Composable
private fun SkippedImageItem(
    uri: Uri,
    shape: Shape,
    onClick: () -> Unit
) {
    val filename = rememberFilename(uri)
    val path = remember(uri, filename) {
        uri.path()?.let { path ->
            filename?.let { name ->
                path
                    .removeSuffix("/$name")
                    .removeSuffix("/${name.substringBeforeLast('.')}")
            } ?: path
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .container(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = shape,
                resultPadding = 0.dp
            )
            .hapticsClickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Picture(
            model = uri,
            modifier = Modifier.size(68.dp),
            shape = ShapeDefaults.mini,
            contentDescription = filename,
            filterQuality = FilterQuality.High
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = filename ?: uri.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            path?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }
        }
    }
}
