@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.root.utils.helper.plus
import ru.tech.imageresizershrinker.presentation.root.widget.other.Loading


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
    var settings: List<Pair<SettingsGroup, SettingItem>>? by remember { mutableStateOf(null) }
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(searchKeyword) {
        delay(150)
        loading = true
        settings = searchKeyword.takeIf { it.trim().isNotEmpty() }?.let {
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
        loading = false
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

    val focus = LocalFocusManager.current
    Column(
        Modifier
            .then(
                if (settings?.isEmpty() == true || loading) Modifier.fillMaxSize()
                else Modifier.verticalScroll(rememberScrollState())
            )
            .pointerInput(Unit) {
                detectTapGestures { focus.clearFocus() }
            }
            .padding(padding)
    ) {
        AnimatedContent(
            targetState = settings to loading,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { (settingsAnimated, isLoading) ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Loading()
                }
            } else {
                Column {
                    settingsAnimated?.let {
                        settingsAnimated
                            .takeIf { it.isNotEmpty() }
                            ?.forEachIndexed { index, (group, setting) ->
                                SearchableSettingItem(
                                    shape = when {
                                        settingsAnimated.size == 1 -> SettingsShapeDefaults.defaultShape
                                        index == 0 -> SettingsShapeDefaults.topShape
                                        index == settingsAnimated.lastIndex -> SettingsShapeDefaults.bottomShape
                                        else -> SettingsShapeDefaults.centerShape
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    group = group,
                                    setting = setting,
                                    viewModel = viewModel
                                )
                            }
                        if (settingsAnimated.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = stringResource(R.string.nothing_found_by_search),
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        end = 24.dp,
                                        top = 8.dp,
                                        bottom = 8.dp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Rounded.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(2f)
                                        .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                                        .fillMaxSize()
                                )
                                Spacer(Modifier.weight(1f))
                            }
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
}