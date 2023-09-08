package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.shapes.DavidStarShape
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block

@Composable
fun BoxScope.Loading() {
    Loading(Modifier.align(Alignment.Center))
    KeepScreenOn()
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(84.dp)
            .block(
                shape = DavidStarShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                applyResultPadding = false
            )
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(
                Alignment.Center
            ),
            strokeCap = StrokeCap.Round,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    KeepScreenOn()
}

@Composable
fun BoxScope.Loading(done: Int, left: Int) {
    Column(
        modifier = Modifier
            .size(108.dp)
            .block(
                shape = DavidStarShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                applyResultPadding = false
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
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                progress = animateFloatAsState(targetValue = done / left.toFloat()).value,
                strokeCap = StrokeCap.Round,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("$done / $left")
    }
    KeepScreenOn()
}