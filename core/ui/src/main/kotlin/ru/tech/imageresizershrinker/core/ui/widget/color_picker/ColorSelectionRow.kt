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

package ru.tech.imageresizershrinker.core.ui.widget.color_picker

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.rounded.ContentPasteGo
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.ui.theme.inverse
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.pasteColorFromClipboard
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import kotlin.math.roundToInt

@Composable
fun ColorSelectionRow(
    modifier: Modifier = Modifier,
    defaultColors: List<Color> = ColorSelectionRowDefaults.colorList,
    allowAlpha: Boolean = false,
    allowScroll: Boolean = true,
    value: Color,
    onValueChange: (Color) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current
    var customColor by remember { mutableStateOf<Color?>(null) }
    var showColorPicker by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(value) {
        if (value !in defaultColors) {
            customColor = value
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(1.2.dp * 40 + 32.dp)
            .fadingEdges(listState),
        userScrollEnabled = allowScroll,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            val background = customColor ?: MaterialTheme.colorScheme.primary
            Box(
                Modifier
                    .size(
                        animateDpAsState(
                            40.dp.times(
                                if (customColor != null) 1.3f else 1f
                            )
                        ).value
                    )
                    .aspectRatio(1f)
                    .container(
                        shape = CircleShape,
                        color = background,
                        resultPadding = 0.dp
                    )
                    .transparencyChecker()
                    .background(background, CircleShape)
                    .combinedClickable(
                        onLongClick = {
                            context.pasteColorFromClipboard(
                                onPastedColor = {
                                    onValueChange(Color(it))
                                    customColor = Color(it)
                                },
                                onPastedColorFailure = { message ->
                                    scope.launch {
                                        toastHostState.showToast(
                                            message = message,
                                            icon = Icons.Outlined.Error
                                        )
                                    }
                                }
                            )
                        }
                    ) {
                        showColorPicker = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = null,
                    tint = background.inverse(
                        fraction = {
                            if (it) 0.8f
                            else 0.5f
                        },
                        darkMode = background.luminance() < 0.3f
                    ),
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = background.copy(alpha = 1f),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                )
            }
        }
        items(defaultColors) { color ->
            Box(
                Modifier
                    .size(
                        animateDpAsState(
                            40.dp.times(
                                if (value == color && customColor == null) {
                                    1.3f
                                } else 1f
                            )
                        ).value
                    )
                    .aspectRatio(1f)
                    .container(
                        shape = CircleShape,
                        color = color,
                        resultPadding = 0.dp
                    )
                    .transparencyChecker()
                    .background(color, CircleShape)
                    .clickable {
                        onValueChange(color)
                        customColor = null
                    }
            )
        }
    }
    var tempColor by remember(showColorPicker) {
        mutableIntStateOf(customColor?.toArgb() ?: 0)
    }
    val settingsState = LocalSettingsState.current
    val simpleSettingsInteractor = LocalSimpleSettingsInteractor.current
    SimpleSheet(
        sheetContent = {
            Box {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    val favoriteColors = settingsState.favoriteColors
                    BoxAnimatedVisibility(favoriteColors.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .container(
                                    shape = RoundedCornerShape(24.dp),
                                    resultPadding = 0.dp
                                )
                                .padding(16.dp)
                        ) {
                            TitleItem(
                                text = stringResource(R.string.recently_used),
                                icon = Icons.Outlined.History,
                                modifier = Modifier
                            )
                            Spacer(Modifier.height(16.dp))
                            val rowState = rememberLazyListState()
                            val itemWidth = with(LocalDensity.current) { 48.dp.toPx() }
                            val possibleCount by remember(rowState, itemWidth) {
                                derivedStateOf {
                                    (rowState.layoutInfo.viewportSize.width / itemWidth).roundToInt()
                                }
                            }
                            LazyRow(
                                state = rowState,
                                modifier = modifier
                                    .fillMaxWidth()
                                    .fadingEdges(rowState),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(favoriteColors) { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .aspectRatio(1f)
                                            .container(
                                                shape = CircleShape,
                                                color = color,
                                                resultPadding = 0.dp
                                            )
                                            .transparencyChecker()
                                            .background(color, CircleShape)
                                            .clickable {
                                                tempColor = color.toArgb()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.ContentPasteGo,
                                            contentDescription = null,
                                            tint = color.inverse(
                                                fraction = {
                                                    if (it) 0.8f
                                                    else 0.5f
                                                },
                                                darkMode = color.luminance() < 0.3f
                                            ),
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(
                                                    color = color.copy(alpha = 1f),
                                                    shape = CircleShape
                                                )
                                                .padding(3.dp)
                                        )
                                    }
                                }
                                if (favoriteColors.size < possibleCount) {
                                    items(possibleCount - favoriteColors.size) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .alpha(0.4f)
                                                .transparencyChecker()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (allowAlpha) {
                        AlphaColorSelection(
                            color = tempColor,
                            onColorChange = {
                                tempColor = it
                            }
                        )
                    } else {
                        ColorSelection(
                            color = tempColor,
                            onColorChange = {
                                tempColor = it
                            }
                        )
                    }
                }
            }
        },
        visible = showColorPicker,
        onDismiss = {
            showColorPicker = it
        },
        title = {
            TitleItem(
                text = stringResource(R.string.color),
                icon = Icons.Rounded.Draw
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    scope.launch {
                        simpleSettingsInteractor.toggleFavoriteColor(
                            color = ColorModel(colorInt = tempColor),
                            forceExclude = false
                        )
                    }
                    onValueChange(Color(tempColor))
                    customColor = Color(tempColor)
                    showColorPicker = false
                }
            ) {
                AutoSizeText(stringResource(R.string.ok))
            }
        }
    )
}

object ColorSelectionRowDefaults {
    val colorList by lazy {
        listOf(
            Color(0xFFf8130d),
            Color(0xFFb8070d),
            Color(0xFF7a000b),
            Color(0xFF8a3a00),
            Color(0xFFff7900),
            Color(0xFFfcf721),
            Color(0xFFf8df09),
            Color(0xFFc0dc18),
            Color(0xFF88dd20),
            Color(0xFF07ddc3),
            Color(0xFF01a0a3),
            Color(0xFF59cbf0),
            Color(0xFF005FFF),
            Color(0xFFfa64e1),
            Color(0xFFfc50a6),
            Color(0xFFd7036a),
            Color(0xFFdb94fe),
            Color(0xFFb035f8),
            Color(0xFF7b2bec),
            Color(0xFF022b6d),
            Color(0xFFFFFFFF),
            Color(0xFF768484),
            Color(0xFF333333),
            Color(0xFF000000),
        )
    }
}