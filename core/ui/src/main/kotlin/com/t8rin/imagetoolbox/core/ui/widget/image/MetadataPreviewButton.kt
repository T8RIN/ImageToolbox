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

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.image.toMap
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.onSecondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.theme.secondaryContainerFixed
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.localizedName
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberImageMetadataAsState
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextFieldColors
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun MetadataPreviewButton(
    uri: Uri?
) {
    AnimatedContent(
        targetState = uri
    ) { uri ->
        val metadata by rememberImageMetadataAsState(
            uri ?: return@AnimatedContent
        )
        val tagMap by remember(metadata) {
            derivedStateOf {
                metadata?.toMap().orEmpty().toList()
                    .filter { it.second.isNotBlank() }
            }
        }
        if (tagMap.isNotEmpty()) {
            var showExif by rememberSaveable {
                mutableStateOf(false)
            }
            SupportingButton(
                onClick = { showExif = true },
                contentColor = MaterialTheme.colorScheme.onSecondaryContainerFixed,
                containerColor = MaterialTheme.colorScheme.secondaryContainerFixed,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(20.dp),
                iconPadding = 2.dp
            )
            EnhancedModalBottomSheet(
                visible = showExif,
                onDismiss = { showExif = false },
                title = {
                    TitleItem(
                        text = stringResource(R.string.exif),
                        icon = Icons.Rounded.Exif
                    )
                },
                confirmButton = {
                    EnhancedButton(
                        onClick = { showExif = false }
                    ) {
                        Text(text = stringResource(R.string.close))
                    }
                },
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(tagMap) { index, (tag, value) ->
                        val shape = ShapeDefaults.byIndex(
                            index = index,
                            size = tagMap.size
                        )
                        RoundedTextField(
                            onValueChange = {},
                            readOnly = true,
                            value = value,
                            label = tag.localizedName,
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            colors = RoundedTextFieldColors(
                                isError = false,
                                containerColor = EnhancedBottomSheetDefaults.contentContainerColor,
                                unfocusedIndicatorColor = Color.Transparent
                            ).copy(
                                unfocusedLabelColor = MaterialTheme.colorScheme
                                    .surfaceVariant.inverse({ 0.3f })
                            ),
                            shape = shape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .container(
                                    color = EnhancedBottomSheetDefaults.contentContainerColor,
                                    shape = shape,
                                    resultPadding = 0.dp
                                )
                        )
                    }
                }
            }
        }
    }
}