package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.shapes.DavidStarShape
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState


@Composable
fun Loading(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier
            .then(
                if (modifier == Modifier) {
                    Modifier.sizeIn(maxWidth = 84.dp, maxHeight = 84.dp)
                } else Modifier
            )
            .aspectRatio(1f)
            .container(
                shape = DavidStarShape,
                autoShadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                resultPadding = 0.dp
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
    val settingsState = LocalSettingsState.current
    Column(
        modifier = Modifier
            .size(108.dp)
            .container(
                autoShadowElevation = animateDpAsState(if (settingsState.borderWidth > 0.dp) 1.dp else 10.dp).value,
                shape = DavidStarShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                resultPadding = 0.dp
            )
            .align(Alignment.Center),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        if (left == 1 || done == 0) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                strokeCap = StrokeCap.Round,
            )
        } else {
            val progress by animateFloatAsState(targetValue = done / left.toFloat())
            CircularProgressIndicator(
                progress = { progress },
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                strokeCap = StrokeCap.Round,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("$done / $left")
    }
    KeepScreenOn()
}