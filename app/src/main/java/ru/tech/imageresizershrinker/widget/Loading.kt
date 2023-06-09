package ru.tech.imageresizershrinker.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@Composable
fun BoxScope.Loading() {
    val settingsState = LocalSettingsState.current
    Box(
        modifier = Modifier
            .heightIn(max = 84.dp)
            .fillMaxHeight()
            .aspectRatio(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .border(
                settingsState.borderWidth,
                MaterialTheme.colorScheme.onSecondaryContainer.copy(0.4f),
                RoundedCornerShape(24.dp)
            )
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .align(Alignment.Center)
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
fun Loading(modifier: Modifier = Modifier) {
    val settingsState = LocalSettingsState.current
    Box(
        modifier
            .heightIn(max = 84.dp)
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .border(
                settingsState.borderWidth,
                MaterialTheme.colorScheme.onSecondaryContainer.copy(0.4f),
                RoundedCornerShape(24.dp)
            )
            .background(MaterialTheme.colorScheme.secondaryContainer)
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
    val settingsState = LocalSettingsState.current
    Column(
        modifier = Modifier
            .size(108.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                settingsState.borderWidth,
                MaterialTheme.colorScheme.onSecondaryContainer.copy(0.4f),
                RoundedCornerShape(24.dp)
            )
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .align(Alignment.Center),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        if (left == 1) {
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

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Box(Modifier.fillMaxSize()) { Loading() }
    }
    KeepScreenOn()
}

@Composable
fun LoadingDialog(done: Int, left: Int) {
    Dialog(onDismissRequest = { }) {
        Box(Modifier.fillMaxSize()) {
            Loading(done, left)
        }
    }
    KeepScreenOn()
}