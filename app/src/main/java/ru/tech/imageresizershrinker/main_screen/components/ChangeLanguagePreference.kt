package ru.tech.imageresizershrinker.main_screen.components

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import org.xmlpull.v1.XmlPullParser
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.outlineVariant
import java.util.*

@Composable
fun ChangeLanguagePreference() {
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.animateContentSize()) {
        PreferenceRow(
            title = stringResource(R.string.language),
            subtitle = context.getCurrentLocaleString(),
            endContent = {
                Icon(
                    imageVector = Icons.Rounded.Language,
                    contentDescription = null,
                )
            },
            onClick = {
                showDialog = true
            }
        )
    }

    if (showDialog) {
        PickLanguageDialog(
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
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun PickLanguageDialog(
    entries: Map<String, String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.alertDialog(),
        onDismissRequest = onDismiss,
        icon = { Icon(imageVector = Icons.Rounded.Language, contentDescription = null) },
        title = { Text(stringResource(R.string.language)) },
        text = {
            Box {
                Divider(Modifier.align(Alignment.TopCenter))
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
                                    onDismiss()
                                }
                                .padding(start = 12.dp, end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected == locale.value,
                                onClick = {
                                    onSelect(locale.key)
                                    onDismiss()
                                }
                            )
                            Text(locale.value)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                border = BorderStroke(
                    LocalBorderWidth.current,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ), onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
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
