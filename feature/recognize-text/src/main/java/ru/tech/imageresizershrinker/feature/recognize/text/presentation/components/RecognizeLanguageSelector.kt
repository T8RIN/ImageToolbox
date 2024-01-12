package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.theme.GreenContrast
import ru.tech.imageresizershrinker.core.ui.theme.RedContrast
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OCRLanguage
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType

@Composable
fun RecognizeLanguageSelector(
    currentRecognitionType: RecognitionType,
    onRecognitionTypeChange: (RecognitionType) -> Unit,
    value: OCRLanguage,
    availableLanguages: List<OCRLanguage>,
    isLanguagesLoading: Boolean,
    onValueChange: (OCRLanguage) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val settingsState = LocalSettingsState.current

    val downloadedLanguages by remember(availableLanguages) {
        derivedStateOf {
            availableLanguages.filter {
                it.downloaded.isNotEmpty()
            }.sortedByDescending {
                it.downloaded.size
            }
        }
    }
    val notDownloadedLanguages by remember(availableLanguages) {
        derivedStateOf {
            availableLanguages.filter { it.downloaded.isEmpty() }
        }
    }

    var showDetailedLanguageSheet by remember {
        mutableStateOf(false)
    }
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

    SimpleSheet(
        visible = showDetailedLanguageSheet,
        onDismiss = {
            showDetailedLanguageSheet = it
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showDetailedLanguageSheet = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.language),
                icon = Icons.Outlined.Language
            )
        }
    ) {
        AnimatedContent(targetState = isLanguagesLoading) { loading ->
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Loading()
                }
            } else {
                val listState = rememberLazyListState()
                LaunchedEffect(Unit) {
                    downloadedLanguages.indexOf(value).takeIf {
                        it != -1
                    }.let {
                        listState.scrollToItem(it ?: 0)
                    }
                }
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (downloadedLanguages.isNotEmpty()) {
                        item {
                            TitleItem(
                                icon = Icons.Rounded.DownloadDone,
                                text = stringResource(id = R.string.downloaded_languages)
                            )
                        }
                    }
                    itemsIndexed(downloadedLanguages) { index, lang ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .container(
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index = index,
                                        size = downloadedLanguages.size
                                    ),
                                    color = animateColorAsState(
                                        if (value == lang) MaterialTheme.colorScheme.mixedContainer
                                        else MaterialTheme.colorScheme.surfaceContainerLow
                                    ).value,
                                    resultPadding = 0.dp
                                )
                                .clickable {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onValueChange(lang)
                                }
                                .padding(16.dp)
                        ) {
                            Text(text = lang.name)
                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.container()
                            ) {
                                RecognitionType.entries.forEach {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                onRecognitionTypeChange(it)
                                            }
                                    ) {
                                        Text(
                                            text = it.displayName.first().uppercase(),
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Icon(
                                            imageVector = if (it !in lang.downloaded) {
                                                Icons.Rounded.Cancel
                                            } else Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = animateColorAsState(
                                                when (it) {
                                                    currentRecognitionType -> if (it in lang.downloaded) {
                                                        GreenContrast
                                                    } else RedContrast

                                                    !in lang.downloaded -> RedContrast.copy(0.3f)
                                                    else -> GreenContrast.copy(0.3f)
                                                }
                                            ).value,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .border(
                                                    width = settingsState.borderWidth,
                                                    color = MaterialTheme.colorScheme.outlineVariant(),
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (notDownloadedLanguages.isNotEmpty()) {
                        item {
                            TitleItem(
                                icon = Icons.Rounded.FileDownloadOff,
                                text = stringResource(id = R.string.available_languages)
                            )
                        }
                    }
                    itemsIndexed(notDownloadedLanguages) { index, lang ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .container(
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index = index,
                                        size = notDownloadedLanguages.size
                                    ),
                                    color = animateColorAsState(
                                        if (value == lang) MaterialTheme.colorScheme.mixedContainer
                                        else MaterialTheme.colorScheme.surfaceContainerLow
                                    ).value,
                                    resultPadding = 0.dp
                                )
                                .clickable {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onValueChange(lang)
                                }
                                .padding(16.dp)
                        ) {
                            Text(text = lang.name)
                        }
                    }
                }
            }
        }
    }
}