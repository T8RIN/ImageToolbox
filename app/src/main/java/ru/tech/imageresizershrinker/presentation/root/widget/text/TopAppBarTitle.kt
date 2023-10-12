package ru.tech.imageresizershrinker.presentation.root.widget.text

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
fun <T : Any> TopAppBarTitle(
    title: String,
    input: T?,
    isLoading: Boolean,
    size: Long?,
    updateOnSizeChange: Boolean = true
) {
    Marquee(
        edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        AnimatedContent(
            targetState = Triple(
                input,
                isLoading,
                if (updateOnSizeChange) size else Unit
            ),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (inp, loading, _) ->
            if (loading) {
                Text(
                    stringResource(R.string.loading)
                )
            } else if (inp == null || size == null) {
                Text(title)
            } else if (size != 0L) {
                Text(
                    stringResource(
                        R.string.size,
                        readableByteCount(size)
                    )
                )
            }
        }
    }
}