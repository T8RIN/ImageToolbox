package ru.tech.imageresizershrinker.presentation.main_screen.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import org.xmlpull.v1.XmlPullParser
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import java.util.Locale

@Composable
fun ChangeLanguagePreference() {
    val context = LocalContext.current
    val showDialog = rememberSaveable { mutableStateOf(false) }
    Column(Modifier.animateContentSize()) {
        PreferenceRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            applyHorPadding = false,
            title = stringResource(R.string.language),
            subtitle = context.getCurrentLocaleString(),
            endContent = {
                Icon(
                    imageVector = Icons.Rounded.Language,
                    contentDescription = null,
                )
            },
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
        entries = context.getLanguages(),
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
    val settingsState = LocalSettingsState.current
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
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    entries.forEach { locale ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    onSelect(locale.key)
                                    visible.value = false
                                }
                                .padding(start = 12.dp, end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected == locale.value,
                                onClick = {
                                    onSelect(locale.key)
                                    visible.value = false
                                }
                            )
                            Text(locale.value)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                HorizontalDivider(Modifier.align(Alignment.TopCenter))
                HorizontalDivider(Modifier.align(Alignment.BottomCenter))
            }
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ), onClick = {
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
    val languages = mutableListOf<Pair<String, String>>()
    val parser = resources.getXml(R.xml.locales_config)
    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && parser.name == "locale") {
            for (i in 0 until parser.attributeCount) {
                if (parser.getAttributeName(i) == "name") {
                    val langTag = parser.getAttributeValue(i)
                    val displayName = getDisplayName(langTag)
                    if (displayName.isNotEmpty()) {
                        languages.add(Pair(langTag, displayName))
                    }
                }
            }
        }
        eventType = parser.next()
    }

    languages.sortBy { it.second }
    languages.add(0, Pair("", getString(R.string.system)))

    return languages.toMap()
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
