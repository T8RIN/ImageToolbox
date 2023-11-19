@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.root.utils.helper.plus


@Composable
fun SettingsBlock(
    searchKeyword: String,
    viewModel: MainViewModel
) {
    val layoutDirection = LocalLayoutDirection.current
    val initialSettingGroups = remember {
        SettingsGroup.entries.filter {
            !(it is SettingsGroup.Firebase && BuildConfig.FLAVOR == "foss")
        }
    }

    val context = LocalContext.current
    val settings by remember(searchKeyword) {
        derivedStateOf {
            searchKeyword.takeIf { it.trim().isNotEmpty() }?.let {
                val newList = mutableListOf<Pair<SettingsGroup, SettingItem>>()
                initialSettingGroups.forEach { group ->
                    group.settingsList.forEach { setting ->
                        val title = context.getString(group.titleId)
                        val icon = group.icon.name
                        val keywords = mutableListOf<String>()
                        keywords.add(title)
                        keywords.add(icon)
                        keywords.add(setting.getTitle(context))
                        keywords.add(setting.getSubtitle(context))
                        if (
                            keywords.any {
                                it.contains(searchKeyword, ignoreCase = true)
                            } && setting !is SettingItem.CheckUpdatesButton
                        ) {
                            newList.add(group to setting)
                        }
                    }
                }
                newList
            }
        }
    }

    val padding = WindowInsets.navigationBars
        .asPaddingValues()
        .plus(
            paddingValues = WindowInsets.displayCutout
                .asPaddingValues()
                .run {
                    PaddingValues(
                        top = 8.dp,
                        bottom = calculateBottomPadding() + 8.dp,
                        end = calculateEndPadding(layoutDirection)
                    )
                }
        )

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(padding)
    ) {
        AnimatedContent(
            targetState = settings,
            modifier = Modifier.fillMaxSize()
        ) { settingsAnimated ->
            Column {
                settingsAnimated?.let {
                    settingsAnimated.forEach { (group, setting) ->
                        SearchableSettingItem(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            group = group,
                            setting = setting,
                            viewModel = viewModel
                        )
                    }
                } ?: initialSettingGroups.forEach { group ->
                    SettingGroupItem(
                        icon = group.icon,
                        text = stringResource(group.titleId),
                        initialState = group.initialState
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            group.settingsList.forEach { setting ->
                                SettingItem(
                                    setting = setting,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}