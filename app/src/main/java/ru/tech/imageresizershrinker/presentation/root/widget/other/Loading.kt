package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import ru.tech.imageresizershrinker.presentation.root.shapes.DavidStarShape
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState


@Composable
fun Loading(modifier: Modifier = Modifier) {
    val borderWidth = LocalSettingsState.current.borderWidth

    BoxWithConstraints(
        modifier
            .then(
                if (modifier == Modifier) {
                    Modifier.sizeIn(maxWidth = 84.dp, maxHeight = 84.dp)
                } else Modifier
            )
            .aspectRatio(1f)
            .then(
                if (borderWidth <= 0.dp) {
                    Modifier.rsBlurShadow(radius = 2.dp, shape = DavidStarShape)
                } else Modifier
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = DavidStarShape
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    0.1f,
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = DavidStarShape
            )
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .size(minHeight / 2),
            strokeCap = StrokeCap.Round,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    KeepScreenOn()
}

@Composable
fun BoxScope.Loading(done: Int, left: Int) {
    val borderWidth = LocalSettingsState.current.borderWidth
    Column(
        modifier = Modifier
            .size(108.dp)
            .then(
                if (borderWidth <= 0.dp) {
                    Modifier.rsBlurShadow(radius = 2.dp, shape = DavidStarShape)
                } else Modifier
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = DavidStarShape
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outlineVariant(
                    0.1f,
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = DavidStarShape
            )
            .align(Alignment.Center),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(maxWidth),
                color = MaterialTheme.colorScheme.tertiary.copy(0.5f),
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                strokeCap = StrokeCap.Round,
            )
            val progress by animateFloatAsState(targetValue = done / left.toFloat())
            CircularProgressIndicator(
                modifier = Modifier.size(maxWidth),
                progress = { progress },
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                strokeCap = StrokeCap.Round,
            )
            AutoSizeText(
                text = "$done / $left",
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(maxWidth * 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
    KeepScreenOn()
}