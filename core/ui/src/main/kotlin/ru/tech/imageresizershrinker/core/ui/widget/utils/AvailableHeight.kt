package ru.tech.imageresizershrinker.core.ui.widget.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.observeAsState
import com.t8rin.modalsheet.FullscreenPopup
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageHeaderState

@Composable
fun rememberAvailableHeight(
    imageState: ImageHeaderState,
    expanded: Boolean = false
): Dp {
    val fullHeight = rememberFullHeight()

    return animateDpAsState(
        targetValue = fullHeight.times(
            when {
                expanded || imageState.position == 4 -> 1f
                imageState.position == 3 -> 0.7f
                imageState.position == 2 -> 0.5f
                imageState.position == 1 -> 0.35f
                else -> 0.2f
            }
        )
    ).value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberFullHeight(): Dp {
    var fullHeight by rememberSaveable(
        LocalConfiguration.current,
        LocalLifecycleOwner.current.lifecycle.observeAsState().value
    ) { mutableStateOf(0f) }

    val density = LocalDensity.current

    if (fullHeight == 0f) {
        FullscreenPopup {
            Column {
                TopAppBar(
                    title = { Text(" ") },
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .onSizeChanged {
                            with(density) {
                                fullHeight = it.height.toDp().value
                            }
                        }
                )
                BottomAppBar(
                    containerColor = Color.Transparent,
                    floatingActionButton = {},
                    actions = {}
                )
            }
        }
    }

    return fullHeight.dp
}

fun ImageHeaderState.isExpanded() = this.position == 4 && isBlocked

fun middleImageState() = ImageHeaderState()