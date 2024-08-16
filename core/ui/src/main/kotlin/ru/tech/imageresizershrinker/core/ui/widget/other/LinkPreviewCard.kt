package ru.tech.imageresizershrinker.core.ui.widget.other

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.utils.helper.LinkPreview
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun LinkPreviewCard(
    linkPreview: LinkPreview,
    shape: Shape
) {
    val clipboardManager =
        LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val coroutineScope = rememberCoroutineScope()
    val onLinkCopiedText = stringResource(R.string.copied)
    val linkTextLabel = stringResource(R.string.image_link)
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .container(
                shape = shape,
                color = MaterialTheme.colorScheme.surface,
                resultPadding = 0.dp
            )
            .combinedClickable(
                onClick = {
                    linkPreview.link?.let {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                it.toUri()
                            )
                        )
                    }
                },
                onLongClick = {
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText(
                            linkTextLabel,
                            linkPreview.link
                        )
                    )
                    coroutineScope.launch {
                        toastHostState.showToast(
                            message = onLinkCopiedText,
                            icon = Icons.Default.Link
                        )
                    }
                },
            )
    ) {
        var sizeOfRight by remember {
            mutableStateOf(80.dp)
        }
        val density = LocalDensity.current
        Picture(
            model = linkPreview.image,
            contentDescription = stringResource(R.string.image_link),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(80.dp)
                .height(sizeOfRight),
            alignment = Alignment.Center,
            error = {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CloverShape)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer
                                .copy(0.5f)
                                .compositeOver(MaterialTheme.colorScheme.surface)
                        )
                        .padding(8.dp)
                )
            },
            filterQuality = FilterQuality.High
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .onSizeChanged {
                    sizeOfRight = with(density) { it.height.toDp() }
                }
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            if (!linkPreview.title.isNullOrBlank()) {
                Text(
                    text = linkPreview.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            if (!linkPreview.description.isNullOrBlank()) {
                Text(
                    text = linkPreview.description,
                    maxLines = if (linkPreview.title.isNullOrBlank()) 3 else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    lineHeight = 16.sp
                )
            }
            if ((linkPreview.description.isNullOrBlank() || linkPreview.title.isNullOrBlank()) && !linkPreview.url.isNullOrBlank()) {
                Text(
                    text = linkPreview.url,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    lineHeight = 16.sp
                )
            }
        }
    }
}