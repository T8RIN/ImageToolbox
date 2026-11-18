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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AutoFixHigh
import com.t8rin.imagetoolbox.core.resources.icons.Bolt
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BorderStyle
import com.t8rin.imagetoolbox.core.resources.icons.BrushColor
import com.t8rin.imagetoolbox.core.resources.icons.Cancel
import com.t8rin.imagetoolbox.core.resources.icons.Castle
import com.t8rin.imagetoolbox.core.resources.icons.CompareArrows
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.FilterAlt
import com.t8rin.imagetoolbox.core.resources.icons.FormatBold
import com.t8rin.imagetoolbox.core.resources.icons.FormatShapes
import com.t8rin.imagetoolbox.core.resources.icons.GridOn
import com.t8rin.imagetoolbox.core.resources.icons.Hexagon
import com.t8rin.imagetoolbox.core.resources.icons.History
import com.t8rin.imagetoolbox.core.resources.icons.Language
import com.t8rin.imagetoolbox.core.resources.icons.Manga
import com.t8rin.imagetoolbox.core.resources.icons.MatchCase
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.resources.icons.RoundedCorner
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.Serif
import com.t8rin.imagetoolbox.core.resources.icons.Stylus
import com.t8rin.imagetoolbox.core.resources.icons.Terminal
import com.t8rin.imagetoolbox.core.resources.icons.Texture
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Category
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Script
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.fontScriptLabels
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun FontFilterSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    categories: List<Category>,
    onCategoriesChange: (List<Category>) -> Unit,
    scripts: List<Script>,
    onScriptsChange: (List<Script>) -> Unit,
    favoritesOnly: Boolean,
    onFavoritesOnlyChange: (Boolean) -> Unit,
    keyword: String,
    onKeywordChange: (String) -> Unit
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.filter),
                icon = Icons.Rounded.FilterAlt
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .enhancedVerticalScroll(rememberScrollState())
                .padding(16.dp)
                .clearFocusOnTap(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FontFilterGroup(title = stringResource(R.string.group)) {
                FontGroupChip(
                    title = stringResource(R.string.favorite),
                    icon = Icons.Outlined.Bookmark,
                    selected = favoritesOnly,
                    onClick = { onFavoritesOnlyChange(!favoritesOnly) }
                )
                Category.entries.forEach { category ->
                    FontGroupChip(
                        title = stringResource(category.titleRes),
                        icon = category.icon,
                        selected = category in categories,
                        onClick = { onCategoriesChange(categories.toggle(category)) }
                    )
                }
            }
            FontFilterGroup(title = stringResource(R.string.font_full_coverage)) {
                fontScriptLabels.forEach { (script, label) ->
                    EnhancedChip(
                        selected = script in scripts,
                        onClick = { onScriptsChange(scripts.toggle(script)) },
                        selectedColor = MaterialTheme.colorScheme.secondary,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        shape = ShapeDefaults.circle
                    ) {
                        Text(stringResource(label))
                    }
                }
            }
            RoundedTextField(
                value = keyword,
                onValueChange = onKeywordChange,
                label = stringResource(R.string.keyword),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onDismiss() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        shape = ShapeDefaults.large,
                        resultPadding = 8.dp,
                        color = EnhancedBottomSheetDefaults.contentContainerColor
                    ),
                startIcon = Icons.Outlined.Search,
                endIcon = {
                    AnimatedVisibility(keyword.isNotEmpty()) {
                        EnhancedIconButton(onClick = { onKeywordChange("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun FontGroupChip(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    EnhancedChip(
        selected = selected,
        onClick = onClick,
        selectedColor = MaterialTheme.colorScheme.secondary,
        contentPadding = PaddingValues(horizontal = 8.dp),
        shape = ShapeDefaults.circle
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(title)
        }
    }
}

@Composable
private fun FontFilterGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 8.dp,
                color = EnhancedBottomSheetDefaults.contentContainerColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleItem(text = title, modifier = Modifier.padding(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            content()
        }
    }
}

internal val Category.titleRes: Int
    get() = when (this) {
        Category.Handwriting -> R.string.font_group_handwriting
        Category.Brush -> R.string.font_group_brush
        Category.Calligraphy -> R.string.font_group_calligraphy
        Category.Display -> R.string.font_group_display
        Category.Retro -> R.string.font_group_retro
        Category.ArtDeco -> R.string.font_group_art_deco
        Category.Textured -> R.string.font_group_textured
        Category.Comic -> R.string.font_group_comic
        Category.Pixel -> R.string.font_group_pixel
        Category.Monospace -> R.string.font_group_monospace
        Category.Serif -> R.string.font_group_serif
        Category.SlabSerif -> R.string.font_group_slab_serif
        Category.SansSerif -> R.string.font_group_sans_serif
        Category.Rounded -> R.string.font_group_rounded
        Category.Condensed -> R.string.font_group_condensed
        Category.International -> R.string.font_group_international
        Category.Color -> R.string.font_group_color
        Category.Blackletter -> R.string.font_group_blackletter
        Category.Stencil -> R.string.font_group_stencil
        Category.Outline -> R.string.font_group_outline
        Category.Techno -> R.string.font_group_techno
    }

internal val Category.icon
    get() = when (this) {
        Category.Handwriting -> Icons.Outlined.Draw
        Category.Brush -> Icons.Outlined.BrushColor
        Category.Calligraphy -> Icons.Outlined.Stylus
        Category.Display -> Icons.Outlined.AutoFixHigh
        Category.Retro -> Icons.Rounded.History
        Category.ArtDeco -> Icons.Outlined.Hexagon
        Category.Textured -> Icons.Outlined.Texture
        Category.Comic -> Icons.Outlined.Manga
        Category.Pixel -> Icons.Outlined.GridOn
        Category.Monospace -> Icons.Outlined.Terminal
        Category.Serif -> Icons.Outlined.Serif
        Category.SlabSerif -> Icons.Rounded.FormatBold
        Category.SansSerif -> Icons.Outlined.MatchCase
        Category.Rounded -> Icons.Rounded.RoundedCorner
        Category.Condensed -> Icons.Rounded.CompareArrows
        Category.International -> Icons.Rounded.Language
        Category.Color -> Icons.Outlined.Palette
        Category.Blackletter -> Icons.Outlined.Castle
        Category.Stencil -> Icons.Rounded.BorderStyle
        Category.Outline -> Icons.Outlined.FormatShapes
        Category.Techno -> Icons.Outlined.Bolt
    }
