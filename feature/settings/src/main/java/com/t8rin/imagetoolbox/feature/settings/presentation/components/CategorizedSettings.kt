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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent

@Composable
internal fun CategorizedSettings(
    groups: List<SettingsGroup>,
    selectedGroup: SettingsGroup?,
    onSelectGroup: (SettingsGroup) -> Unit,
    component: SettingsComponent,
    gridState: LazyStaggeredGridState,
    categoryGridState: LazyStaggeredGridState,
    contentPadding: PaddingValues,
    targetSetting: Setting?,
    targetModifier: Modifier,
    highlightedContainerColor: Color
) {
    val settingsState = LocalSettingsState.current
    val visibleGroups = groups.filter {
        it != SettingsGroup.Shadows || settingsState.borderWidth <= 0.dp
    }
    val layoutDirection = LocalLayoutDirection.current

    BoxWithConstraints(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedGroup,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { group ->
            val padding = if (group == null) contentPadding else PaddingValues(
                start = contentPadding.calculateStartPadding(layoutDirection) - 8.dp,
                top = contentPadding.calculateTopPadding(),
                end = contentPadding.calculateEndPadding(layoutDirection) - 8.dp,
                bottom = contentPadding.calculateBottomPadding()
            )
            val contentWidth = maxWidth - padding.calculateLeftPadding(layoutDirection) -
                    padding.calculateRightPadding(layoutDirection)
            val oneColumn = contentWidth < 600.dp
            val state = remember(group) {
                if (group == null) gridState else categoryGridState
            }

            LazyVerticalStaggeredGrid(
                state = state,
                contentPadding = padding,
                columns = StaggeredGridCells.Adaptive(300.dp),
                verticalItemSpacing = if (group == null || oneColumn) 4.dp else 16.dp,
                horizontalArrangement = Arrangement.spacedBy(if (group == null) 4.dp else 0.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                if (group == null) {
                    itemsIndexed(
                        items = visibleGroups,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        PreferenceItem(
                            title = stringResource(item.titleId),
                            subtitle = item.settingsList.take(3).map {
                                stringResource(it.title)
                            }.joinToString(", "),
                            startIcon = item.icon,
                            endIcon = null,
                            shape = ShapeDefaults.byIndex(
                                index = if (oneColumn) index else -1,
                                size = visibleGroups.size
                            ),
                            onClick = { onSelectGroup(item) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    itemsIndexed(
                        items = group.settingsList,
                        key = { _, setting -> setting.hashCode() }
                    ) { index, setting ->
                        ProvideContainerDefaults(
                            shape = ShapeDefaults.byIndex(
                                index = if (oneColumn) index else -1,
                                size = group.settingsList.size
                            )
                        ) {
                            Column(if (setting == targetSetting) targetModifier else Modifier) {
                                SettingItem(
                                    setting = setting,
                                    component = component,
                                    containerColor = if (setting == targetSetting) {
                                        highlightedContainerColor
                                    } else MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}