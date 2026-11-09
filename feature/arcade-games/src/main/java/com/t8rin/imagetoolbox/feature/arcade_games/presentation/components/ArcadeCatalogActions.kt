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

package com.t8rin.imagetoolbox.feature.arcade_games.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.FinanceMode
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
internal fun ArcadeCatalogActions(
    searching: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onOpenStatistics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val insets = WindowInsets.navigationBars.union(
        WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
    )
    val settings = LocalSettingsState.current

    AnimatedContent(
        targetState = searching,
        modifier = modifier.fillMaxWidth()
    ) { isSearching ->
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawHorizontalStroke(top = true)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .pointerInput(Unit) { detectTapGestures { } }
                    .windowInsetsPadding(insets)
                    .padding(16.dp)
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                    RoundedTextField(
                        maxLines = 1,
                        hint = { Text(stringResource(R.string.search_here)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search,
                            autoCorrectEnabled = null
                        ),
                        value = query,
                        onValueChange = onQueryChange,
                        endIcon = { focused ->
                            EnhancedIconButton(
                                onClick = {
                                    if (query.isNotEmpty()) onQueryChange("")
                                    else onCloseSearch()
                                },
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Close,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = if (focused) 1f else 0.5f
                                    )
                                )
                            }
                        },
                        shape = ShapeDefaults.circle
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(insets)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.align(
                        settings.fabAlignment.takeIf { it != Alignment.BottomCenter }
                            ?: Alignment.BottomEnd
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnhancedFloatingActionButton(
                        type = EnhancedFloatingActionButtonType.SecondaryHorizontal,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = onOpenSearch
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    }
                    EnhancedFloatingActionButton(onClick = onOpenStatistics) {
                        Icon(
                            imageVector = Icons.Outlined.FinanceMode,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}