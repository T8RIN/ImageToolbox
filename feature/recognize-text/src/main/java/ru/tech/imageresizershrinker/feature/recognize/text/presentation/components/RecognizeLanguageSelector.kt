package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.MultipleStop
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.theme.Green
import ru.tech.imageresizershrinker.core.ui.theme.Red
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.GradientEdge
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OCRLanguage
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecognizeLanguageSelector(
    currentRecognitionType: RecognitionType,
    value: List<OCRLanguage>,
    availableLanguages: List<OCRLanguage>,
    onValueChange: (List<OCRLanguage>, RecognitionType) -> Unit
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

    var showDetailedLanguageSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.language),
        subtitle = value.joinToString(separator = ", ") { it.name },
        onClick = {
            showDetailedLanguageSheet = true
        },
        icon = Icons.Outlined.Language,
        endIcon = Icons.Rounded.CreateAlt
    )

    var allowMultipleLanguagesSelection by rememberSaveable {
        mutableStateOf(value.isNotEmpty())
    }

    val onValueChangeImpl: (Boolean, RecognitionType, OCRLanguage) -> Unit =
        { selected, type, lang ->
            if (allowMultipleLanguagesSelection) {
                if (selected) {
                    onValueChange(
                        (value - lang).distinct(),
                        type
                    )
                } else onValueChange(
                    (value + lang).distinct(),
                    type
                )
            } else onValueChange(listOf(lang), type)
        }

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
        AnimatedContent(targetState = value.isEmpty()) { loading ->
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
                LaunchedEffect(downloadedLanguages) {
                    downloadedLanguages.indexOf(value.firstOrNull()).takeIf {
                        it != -1
                    }.let {
                        listState.scrollToItem(it ?: 0)
                    }
                }
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        bottom = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stickyHeader {
                        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                        val density = LocalDensity.current
                        PreferenceRowSwitch(
                            title = stringResource(R.string.allow_multiple_languages),
                            color = animateColorAsState(
                                if (allowMultipleLanguagesSelection) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerLow
                            ).value,
                            modifier = Modifier
                                .layout { measurable, constraints ->
                                    val result = measurable.measure(
                                        constraints.copy(
                                            maxWidth = with(density) {
                                                screenWidth.roundToPx()
                                            }
                                        )
                                    )
                                    layout(result.measuredWidth, result.measuredHeight) {
                                        result.place(0, 0)
                                    }
                                }
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                                .padding(16.dp),
                            shape = RoundedCornerShape(28.dp),
                            checked = allowMultipleLanguagesSelection,
                            startIcon = Icons.Rounded.MultipleStop,
                            onClick = {
                                if (!it) onValueChange(value.take(1), currentRecognitionType)
                                allowMultipleLanguagesSelection = it
                            }
                        )
                        GradientEdge(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp),
                            startColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                            endColor = Color.Transparent
                        )
                    }
                    if (downloadedLanguages.isNotEmpty()) {
                        item {
                            TitleItem(
                                icon = Icons.Rounded.DownloadDone,
                                text = stringResource(id = R.string.downloaded_languages)
                            )
                        }
                    }
                    itemsIndexed(downloadedLanguages) { index, lang ->
                        val selected by remember(value, lang) {
                            derivedStateOf {
                                lang in value
                            }
                        }
                        Row(
                            Modifier
                                .animateItemPlacement()
                                .fillMaxWidth()
                                .container(
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index = index,
                                        size = downloadedLanguages.size
                                    ),
                                    color = animateColorAsState(
                                        if (selected) MaterialTheme.colorScheme.mixedContainer
                                        else MaterialTheme.colorScheme.surfaceContainerLow
                                    ).value,
                                    resultPadding = 0.dp
                                )
                                .clickable {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onValueChangeImpl(selected, currentRecognitionType, lang)
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedVisibility(visible = value.size > 1) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = {
                                        onValueChangeImpl(selected, currentRecognitionType, lang)
                                    }
                                )
                            }
                            Text(text = lang.name)
                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.container()
                            ) {
                                RecognitionType.entries.forEach { type ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                haptics.performHapticFeedback(
                                                    HapticFeedbackType.LongPress
                                                )
                                                onValueChange(value, type)
                                            }
                                    ) {
                                        val notDownloaded by remember(type, lang.downloaded) {
                                            derivedStateOf {
                                                type !in lang.downloaded
                                            }
                                        }
                                        val displayName by remember(type) {
                                            derivedStateOf {
                                                type.displayName.first().uppercase()
                                            }
                                        }
                                        val green = Green
                                        val red = Red
                                        val color by remember(
                                            currentRecognitionType,
                                            red,
                                            green,
                                            lang.downloaded
                                        ) {
                                            derivedStateOf {
                                                when (type) {
                                                    currentRecognitionType -> if (type in lang.downloaded) {
                                                        green
                                                    } else red

                                                    !in lang.downloaded -> red.copy(0.3f)
                                                    else -> green.copy(0.3f)
                                                }
                                            }
                                        }
                                        Text(
                                            text = displayName,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Icon(
                                            imageVector = if (notDownloaded) {
                                                Icons.Rounded.Cancel
                                            } else Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = animateColorAsState(color).value,
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
                                .animateItemPlacement()
                                .fillMaxWidth()
                                .container(
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index = index,
                                        size = notDownloadedLanguages.size
                                    ),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    resultPadding = 0.dp
                                )
                                .clickable {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onValueChangeImpl(false, currentRecognitionType, lang)
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