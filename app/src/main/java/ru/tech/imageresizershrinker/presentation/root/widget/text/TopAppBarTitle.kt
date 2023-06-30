package ru.tech.imageresizershrinker.presentation.root.widget.text

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.utils.helper.readableByteCount

@Composable
fun TopAppBarTitle(
    title: String,
    bitmap: Bitmap?,
    isLoading: Boolean,
    size: Long
) {
    Marquee(
        edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        AnimatedContent(
            targetState = Triple(
                bitmap,
                isLoading,
                size
            ),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (bmp, loading, sizeInBytes) ->
            if (bmp == null) {
                Text(title)
            } else if (!loading && sizeInBytes != 0L) {
                Text(
                    stringResource(
                        R.string.size,
                        readableByteCount(sizeInBytes)
                    )
                )
            } else {
                Text(
                    stringResource(R.string.loading)
                )
            }
        }
    }
}