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

package com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.Wallpaper
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.screenLogic.WallpapersExportComponent

@Composable
internal fun WallpapersPreview(component: WallpapersExportComponent) {
    val wallpapers = component.wallpapers
    val isPortrait by isPortraitOrientationAsState()

    AnimatedVisibility(
        visible = wallpapers.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .then(
                    if (isPortrait) Modifier.padding(top = 20.dp)
                    else Modifier
                )
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            wallpapers.forEachIndexed { index, wallpaper ->
                val isSelected by remember(index, component.selectedImages) {
                    derivedStateOf {
                        index in component.selectedImages
                    }
                }

                WallpaperItem(
                    wallpaper = wallpaper,
                    onClick = { component.toggleSelection(index) },
                    isSelected = isSelected,
                    shape = ShapeDefaults.byIndex(
                        index = index,
                        size = wallpapers.size,
                        vertical = false
                    )
                )
            }
        }
    }
}

@Composable
private fun RowScope.WallpaperItem(
    wallpaper: Wallpaper,
    onClick: () -> Unit,
    isSelected: Boolean,
    shape: Shape
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .container(
                shape = shape,
                resultPadding = 0.dp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (wallpaper.imageUri.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BrokenImageAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(vertical = 16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val padding by animateDpAsState(
                        if (isSelected) 8.dp else 0.dp
                    )
                    val borderColor = takeColorFromScheme {
                        if (isSelected) primary else Color.Transparent
                    }
                    AsyncImage(
                        model = wallpaper.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .border(
                                width = 2.dp,
                                color = borderColor,
                                shape = ShapeDefaults.small
                            )
                            .clip(ShapeDefaults.small)
                            .hapticsClickable(onClick = onClick),
                        filterQuality = FilterQuality.None,
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        MediaCheckBox(
                            isChecked = isSelected,
                            uncheckedColor = White.copy(0.8f),
                            checkedColor = MaterialTheme.colorScheme.primary,
                            checkedIcon = Icons.Filled.CheckCircle,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    animateColorAsState(
                                        if (isSelected) MaterialTheme.colorScheme.surfaceContainer
                                        else Color.Transparent
                                    ).value
                                )
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            AutoSizeText(
                key = { wallpaper.imageUri },
                text = stringResource(wallpaper.nameRes),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onMixedContainer,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .container(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.mixedContainer
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                style = TextStyle(fontSize = 12.sp, lineHeight = 13.sp)
            )
            Spacer(Modifier.height(4.dp))
            if (!wallpaper.imageUri.isNullOrBlank()) {
                AutoSizeText(
                    key = { wallpaper.imageUri },
                    text = "${wallpaper.resolution.width}x${wallpaper.resolution.height}",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .container(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    style = TextStyle(fontSize = 11.sp, lineHeight = 12.sp)
                )
                Spacer(Modifier.height(4.dp))
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}