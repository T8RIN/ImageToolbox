package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OCRLanguage

@Composable
fun LanguageSelector(
    value: OCRLanguage,
    availableLanguages: List<OCRLanguage>,
    isLanguagesLoading: Boolean,
    onValueChange: (OCRLanguage) -> Unit
) {
    val downloadedLanguages by remember(availableLanguages) {
        derivedStateOf {
            availableLanguages.filter { it.downloaded }
        }
    }
    val notDownloadedLanguages by remember(availableLanguages) {
        derivedStateOf {
            availableLanguages.filter { !it.downloaded }
        }
    }

    var showDetailedLanguageSheet by remember {
        mutableStateOf(false)
    }
//TODO
    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.language),
        subtitle = value.name,
        onClick = {
            showDetailedLanguageSheet = true
        },
        icon = Icons.Outlined.Language,
        endIcon = Icons.Rounded.CreateAlt
    )
}