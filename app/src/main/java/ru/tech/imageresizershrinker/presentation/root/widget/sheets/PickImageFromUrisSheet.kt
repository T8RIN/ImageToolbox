package ru.tech.imageresizershrinker.presentation.root.widget.sheets

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.transform.Transformation
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PickImageFromUrisSheet(
    visible: MutableState<Boolean>,
    transformations: List<Transformation>,
    uris: List<Uri>?,
    selectedUri: Uri?,
    onUriRemoved: (Uri) -> Unit,
    columns: Int,
    onUriPicked: (Uri) -> Unit
) {
    val hasUris = (uris?.size ?: 0) > 1
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
                    contentPadding = PaddingValues(8.dp),
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                ) {
                    uris?.let { uris ->
                        items(uris, key = { it.toString() }) { uri ->
                            val selected = selectedUri == uri
                            val color by animateColorAsState(
                                if (selected) MaterialTheme.colorScheme.primary.copy(0.5f)
                                else MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                            )
                            val padding by animateDpAsState(if (selected) 8.dp else 4.dp)
                            val pictureShape by animateDpAsState(if (selected) 18.dp else 20.dp)
                            Box(
                                modifier = Modifier
                                    .container(
                                        shape = RoundedCornerShape(24.dp),
                                        resultPadding = 0.dp,
                                        color = color
                                    )
                                    .animateItemPlacement()
                            ) {
                                Picture(
                                    transformations = transformations,
                                    model = uri,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(pictureShape))
                                        .clickable {
                                            onUriPicked(uri)
                                            visible.value = false
                                        }
                                        .padding(padding)
                                        .clip(RoundedCornerShape(pictureShape)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                                EnhancedIconButton(
                                    onClick = {
                                        onUriRemoved(uri)
                                    },
                                    forceMinimumInteractiveComponentSize = false,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(
                                        0.7f
                                    ),
                                    enabled = hasUris,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(36.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.RemoveCircleOutline,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = Color.Transparent,
                onClick = {
                    visible.value = false
                },
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(R.string.change_preview),
                icon = Icons.Rounded.PhotoLibrary
            )
        },
        visible = visible
    )

    if (!(hasUris && visible.value)) visible.value = false
}