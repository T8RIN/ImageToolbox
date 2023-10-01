package ru.tech.imageresizershrinker.presentation.single_edit_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.Metadata
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ImageUtils.toMap
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditExifSheet(
    visible: MutableState<Boolean>,
    exif: ExifInterface?,
    onClearExif: () -> Unit,
    onUpdateTag: (String, String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    var showClearExifDialog by rememberSaveable { mutableStateOf(false) }
    val showAddExifDialog = rememberSaveable { mutableStateOf(false) }

    var exifMap by remember(exif) {
        mutableStateOf(exif?.toMap())
    }

    SimpleSheet(
        nestedScrollEnabled = false,
        endConfirmButtonPadding = 0.dp,
        confirmButton = {
            EnhancedButton(
                onClick = { visible.value = false }
            ) {
                AutoSizeText(stringResource(R.string.ok))
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
                    EnhancedButton(
                        containerColor = Color.Transparent,
                        onClick = {
                            showClearExifDialog = true
                        }
                    ) {
                        Text(stringResource(R.string.clear))
                    }
                    Spacer(Modifier.width(8.dp))
                }
                if (count > 0) {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { showAddExifDialog.value = true }
                    ) {
                        Text(stringResource(R.string.add_tag))
                    }
                }
            }
        },
        dragHandle = {
            SimpleDragHandle {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleItem(
                        text = stringResource(id = R.string.edit_exif),
                        icon = Icons.Rounded.Fingerprint
                    )
                }
            }
        },
        visible = visible,
        sheetContent = {
            if (exifMap?.isEmpty() == false) {
                Box {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(exifMap!!.toList()) { (tag, value) ->
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .container(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                            alpha = 0.1f
                                        )
                                    )
                            ) {
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
            } else {
                Box {
                    Text(
                        stringResource(R.string.no_exif),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            val tags by remember(exifMap) {
                derivedStateOf {
                    Metadata.metaTags.filter {
                        it !in (exifMap?.keys ?: emptyList())
                    }.sorted()
                }
            }
            if (tags.isEmpty()) {
                SideEffect {
                    showAddExifDialog.value = false
                }
            }
            var query by rememberSaveable { mutableStateOf("") }
            val list by remember(tags, query) {
                derivedStateOf {
                    tags.filter {
                        query.lowercase() in it.lowercase()
                    }
                }
            }
            SimpleSheet(
                nestedScrollEnabled = false,
                visible = showAddExifDialog,
                dragHandle = {
                    SimpleDragHandle {
                        Column {
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
                        }
                    }
                },
                confirmButton = {
                    EnhancedButton(
                        onClick = { showAddExifDialog.value = false }
                    ) {
                        AutoSizeText(stringResource(R.string.ok))
                    }
                },
                title = {
                    TitleItem(
                        text = stringResource(R.string.add_tag),
                        icon = Icons.Rounded.Fingerprint
                    )
                },
                sheetContent = {
                    Column {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 8.dp),
                            modifier = Modifier.weight(1f, false)
                        ) {
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
                    }
                }
            )
            if (showClearExifDialog) {
                AlertDialog(
                    modifier = Modifier.alertDialogBorder(),
                    onDismissRequest = { showClearExifDialog = false },
                    title = { Text(stringResource(R.string.clear_exif)) },
                    icon = { Icon(Icons.Rounded.Delete, null) },
                    confirmButton = {
                        EnhancedButton(
                            onClick = { showClearExifDialog = false }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    dismissButton = {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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