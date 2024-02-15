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

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.utils.LocaleConfigCompat
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils
import ru.tech.imageresizershrinker.core.ui.utils.toList
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import java.util.Locale

@Composable
fun ChangeLanguageSettingItem(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    val context = LocalContext.current
    val showDialog = rememberSaveable { mutableStateOf(false) }
    Column(Modifier.animateContentSize()) {
        PreferenceItem(
            shape = shape,
            modifier = modifier.padding(bottom = 1.dp),
            title = stringResource(R.string.language),
            subtitle = context.getCurrentLocaleString(),
            startIcon = Icons.Outlined.Language,
            endIcon = Icons.Rounded.CreateAlt,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f),
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !ContextUtils.isMiUi()) {
                    kotlin.runCatching {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APP_LOCALE_SETTINGS,
                                Uri.parse("package:${context.packageName}")
                            )
                        )
                    }.getOrNull().let {
                        if (it == null) showDialog.value = true
                    }
                } else {
                    showDialog.value = true
                }
            }
        )
    }

    PickLanguageSheet(
        entries = remember {
            context.getLanguages()
        },
        selected = context.getCurrentLocaleString(),
        onSelect = {
            val locale = if (it == "") {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(it)
            }
            AppCompatDelegate.setApplicationLocales(locale)
        },
        visible = showDialog
    )
}

@Composable
private fun PickLanguageSheet(
    entries: Map<String, String>,
    selected: String,
    onSelect: (String) -> Unit,
    visible: MutableState<Boolean>
) {
    SimpleSheet(
        title = {
            TitleItem(
                text = stringResource(R.string.language),
                icon = Icons.Rounded.Language
            )
        },
        sheetContent = {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    entries.entries.forEachIndexed { index, locale ->
                        val isSelected =
                            selected == locale.value || (selected.isEmpty() && index == 0)
                        PreferenceItemOverload(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onSelect(locale.key)
                            },
                            resultModifier = Modifier.padding(
                                start = 16.dp,
                                end = 8.dp,
                                top = 8.dp,
                                bottom = 8.dp
                            ),
                            color = animateColorAsState(
                                if (isSelected) MaterialTheme
                                    .colorScheme
                                    .secondaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerHigh
                            ).value,
                            shape = ContainerShapeDefaults.shapeForIndex(
                                index = index,
                                size = entries.size
                            ),
                            endIcon = {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        onSelect(locale.key)
                                    }
                                )
                            },
                            title = locale.value
                        )
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    visible.value = false
                }
            ) {
                AutoSizeText(stringResource(R.string.cancel))
            }
        },
        visible = visible
    )
}

private fun Context.getLanguages(): Map<String, String> {
    val languages = mutableListOf("" to getString(R.string.system)).apply {
        addAll(
            LocaleConfigCompat(this@getLanguages)
                .supportedLocales!!.toList()
                .map {
                    it.toLanguageTag() to it.getDisplayName(it).replaceFirstChar(Char::uppercase)
                }
        )
    }

    return languages.let { tags ->
        listOf(tags.first()) + tags.drop(1).sortedBy { it.second }
    }.toMap()
}

private fun Context.getCurrentLocaleString(): String {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales == LocaleListCompat.getEmptyLocaleList()) {
        return getString(R.string.system)
    }
    return getDisplayName(locales.toLanguageTags())
}

private fun getDisplayName(lang: String?): String {
    if (lang == null) {
        return ""
    }

    val locale = when (lang) {
        "" -> LocaleListCompat.getAdjustedDefault()[0]
        else -> Locale.forLanguageTag(lang)
    }
    return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
}
