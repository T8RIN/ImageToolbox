package ru.tech.imageresizershrinker.presentation.single_resize_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.Metadata
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ImageUtils.toMap
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditExifSheet(
    visible: MutableState<Boolean>,
    exif: ExifInterface?,
    onClearExif: () -> Unit,
    onUpdateTag: (String, String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    val settingsState = LocalSettingsState.current
    var showClearExifDialog by rememberSaveable { mutableStateOf(false) }
    val showAddExifDialog = rememberSaveable { mutableStateOf(false) }

    var exifMap by remember(exif) {
        mutableStateOf(exif?.toMap())
    }

    SimpleSheet(
        nestedScrollEnabled = false,
        endConfirmButtonPadding = 0.dp,
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                ),
                onClick = { visible.value = false }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        title = {
            val count =
                remember(exifMap) {
                    Metadata.metaTags.count {
                        it !in (exifMap?.keys ?: emptyList())
                    }
                }
            Row {
                if (exifMap?.isEmpty() == false) {
                    OutlinedButton(
                        onClick = {
                            showClearExifDialog = true
                        }, border = BorderStroke(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme.outlineVariant()
                        )
                    ) {
                        Text(stringResource(R.string.clear))
                    }
                    Spacer(Modifier.width(8.dp))
                }
                if (count > 0) {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                        border = BorderStroke(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                        ),
                        onClick = { showAddExifDialog.value = true }
                    ) {
                        Text(stringResource(R.string.add_tag))
                    }
                }
            }
        },
        visible = visible,
        sheetContent = {
            if (exifMap?.isEmpty() == false) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleItem(
                        text = stringResource(id = R.string.edit_exif),
                        icon = Icons.Rounded.Fingerprint
                    )
                }
                Box {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(exifMap!!.toList()) { (tag, value) ->
                            OutlinedCard(
                                border = BorderStroke(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant()
                                ),
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = 0.1f
                                    )
                                )
                            ) {
                                Column(Modifier.fillMaxWidth()) {
                                    Row {
                                        Text(
                                            text = tag,
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .weight(1f),
                                            textAlign = TextAlign.Start
                                        )
                                        IconButton(
                                            onClick = {
                                                onRemoveTag(tag)
                                                exifMap = exifMap?.toMutableMap()
                                                    ?.apply { remove(tag) }
                                            }
                                        ) {
                                            Icon(
                                                Icons.Rounded.RemoveCircleOutline,
                                                null
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        onValueChange = {
                                            onUpdateTag(tag, it)
                                            exifMap = exifMap?.toMutableMap()
                                                ?.apply {
                                                    this[tag] = it
                                                }
                                        },
                                        value = value,
                                        textStyle = LocalTextStyle.current.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                    Divider(Modifier.align(Alignment.TopCenter))
                    Divider(Modifier.align(Alignment.BottomCenter))
                }
            } else {
                Box {
                    Text(
                        stringResource(R.string.no_exif),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                    Divider(Modifier.align(Alignment.TopCenter))
                    Divider(Modifier.align(Alignment.BottomCenter))
                }
            }
            SimpleSheet(
                nestedScrollEnabled = false,
                visible = showAddExifDialog,
                sheetContent = {
                    Column {
                        val tags =
                            remember(exifMap) {
                                Metadata.metaTags.filter {
                                    it !in (exifMap?.keys ?: emptyList())
                                }.sorted()
                            }
                        if (tags.isEmpty()) {
                            SideEffect {
                                showAddExifDialog.value = false
                            }
                        }
                        var query by rememberSaveable { mutableStateOf("") }
                        val list = remember(tags, query) {
                            tags.filter {
                                query.lowercase() in it.lowercase()
                            }
                        }
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 8.dp),
                            modifier = Modifier.weight(1f, false)
                        ) {
                            stickyHeader {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                10.dp
                                            )
                                        )
                                ) {
                                    RoundedTextField(
                                        textStyle = LocalTextStyle.current.copy(
                                            textAlign = TextAlign.Start
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        shape = RoundedCornerShape(30),
                                        label = stringResource(R.string.search_here),
                                        onValueChange = { query = it },
                                        value = query
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Divider()
                                }
                            }
                            item {
                                Spacer(Modifier.height(8.dp))
                            }
                            items(list) { tag ->
                                PreferenceItemOverload(
                                    title = tag,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    endIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.AddCircleOutline,
                                            contentDescription = null
                                        )
                                    },
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        0.1f
                                    ),
                                    onClick = {
                                        onRemoveTag(tag)
                                        exifMap = exifMap?.toMutableMap()
                                            ?.apply { this[tag] = "" }
                                    }
                                )
                            }
                            if (list.isEmpty()) {
                                item {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 16.dp,
                                                bottom = 16.dp,
                                                start = 24.dp,
                                                end = 24.dp
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.nothing_found_by_search),
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        Divider()
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        10.dp
                                    )
                                )
                                .padding(16.dp)
                                .padding(end = 16.dp)
                                .navigationBarsPadding()
                        ) {
                            TitleItem(
                                text = stringResource(R.string.add_tag),
                                icon = Icons.Rounded.Fingerprint
                            )
                            Spacer(Modifier.weight(1f))
                            OutlinedButton(
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                border = BorderStroke(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                ),
                                onClick = { showAddExifDialog.value = false }
                            ) {
                                Text(stringResource(R.string.ok))
                            }
                        }
                    }
                }
            )
            if (showClearExifDialog) {
                AlertDialog(
                    modifier = Modifier.alertDialog(),
                    onDismissRequest = { showClearExifDialog = false },
                    title = { Text(stringResource(R.string.clear_exif)) },
                    icon = { Icon(Icons.Rounded.Delete, null) },
                    confirmButton = {
                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            border = BorderStroke(
                                settingsState.borderWidth,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                            ),
                            onClick = { showClearExifDialog = false }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                            border = BorderStroke(
                                settingsState.borderWidth,
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                            ),
                            onClick = {
                                showClearExifDialog = false
                                onClearExif()
                                exifMap = emptyMap()
                            }
                        ) {
                            Text(stringResource(R.string.clear))
                        }
                    },
                    text = {
                        Text(stringResource(R.string.clear_exif_sub))
                    }
                )
            }
        }
    )
}