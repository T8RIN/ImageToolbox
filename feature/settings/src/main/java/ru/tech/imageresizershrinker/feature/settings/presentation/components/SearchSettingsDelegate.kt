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

package ru.tech.imageresizershrinker.feature.settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.settings.presentation.model.Setting
import ru.tech.imageresizershrinker.core.settings.presentation.model.SettingsGroup
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getStringLocalized
import java.util.Locale

@Composable
internal fun SearchSettingsDelegate(
    searchKeyword: String,
    onLoadingChange: (Boolean) -> Unit,
    onGetSettingsList: (List<Pair<SettingsGroup, Setting>>?) -> Unit,
    initialSettingGroups: List<SettingsGroup>
) {
    val context = LocalContext.current
    LaunchedEffect(searchKeyword) {
        delay(150)
        onLoadingChange(searchKeyword.isNotEmpty())
        onGetSettingsList(
            searchKeyword.takeIf { it.trim().isNotEmpty() }?.let {
                val newList = mutableListOf<Pair<Pair<SettingsGroup, Setting>, Int>>()
                initialSettingGroups.forEach { group ->
                    group.settingsList.forEach { setting ->
                        val keywords = mutableListOf<String>().apply {
                            add(context.getString(group.titleId))
                            add(context.getString(setting.title))
                            add(context.getStringLocalized(group.titleId, Locale.ENGLISH))
                            add(context.getStringLocalized(setting.title, Locale.ENGLISH))
                            setting.subtitle?.let {
                                add(context.getString(it))
                                add(context.getStringLocalized(it, Locale.ENGLISH))
                            }
                        }

                        val substringStart = keywords
                            .joinToString()
                            .indexOf(
                                string = searchKeyword,
                                ignoreCase = true
                            ).takeIf { it != -1 }

                        substringStart?.plus(searchKeyword.length)?.let { substringEnd ->
                            newList.add(group to setting to (substringEnd - substringStart))
                        }
                    }
                }
                newList.sortedBy { it.second }.map { it.first }
            }
        )
        onLoadingChange(false)
    }
}