package ru.tech.imageresizershrinker.widget.sheets

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.transform.Transformation
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.text.TitleItem
import ru.tech.imageresizershrinker.widget.image.Picture
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@Composable
fun PickImageFromUrisSheet(
    visible: Boolean,
    transformations: List<Transformation>,
    uris: List<Uri>?,
    selectedUri: Uri?,
    onUriRemoved: (Uri) -> Unit,
    onDismiss: () -> Unit,
    columns: Int,
    onUriPicked: (Uri) -> Unit
) {
    val settingsState = LocalSettingsState.current
    SimpleSheet(
        sheetContent = {
            val gridState = rememberLazyGridState()
            LaunchedEffect(Unit) {
                gridState.scrollToItem(
                    uris?.indexOf(selectedUri) ?: 0
                )
            }
            Box {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(4.dp),
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    uris?.let { uris ->
                        items(uris, key = { it.toString() }) { uri ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .block(RoundedCornerShape(8.dp))
                            ) {
                                Picture(
                                    transformations = transformations,
                                    model = uri,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onUriPicked(uri)
                                            onDismiss()
                                        }
                                        .then(
                                            if (uri == selectedUri) {
                                                Modifier.border(
                                                    settingsState.borderWidth * 2,
                                                    MaterialTheme.colorScheme.outlineVariant(),
                                                    RoundedCornerShape(8.dp)
                                                )
                                            } else Modifier
                                        )
                                        .block(RoundedCornerShape(8.dp)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                    border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(
                                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ),
                                    onClick = {
                                        onUriRemoved(uri)
                                    },
                                    contentPadding = PaddingValues(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    modifier = Modifier.defaultMinSize(minHeight = 10.dp)
                                ) {
                                    Text(stringResource(R.string.remove))
                                }
                            }
                        }
                    }
                }
                Divider(Modifier.align(Alignment.TopCenter))
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant()
                )
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.change_preview),
                icon = Icons.Rounded.PhotoLibrary
            )
        },
        visible = ((uris?.size ?: 0) > 1) && visible,
        onDismiss = {
            onDismiss()
        }
    )

    if (!((uris?.size ?: 0) > 1 && visible)) onDismiss()
}