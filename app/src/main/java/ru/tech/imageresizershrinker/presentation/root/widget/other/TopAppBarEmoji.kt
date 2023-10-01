package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.EmojiItem
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun TopAppBarEmoji() {
    val settingsState = LocalSettingsState.current
    val confettiController = LocalConfettiController.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(end = 12.dp)
            .scaleOnTap(
                onRelease = {
                    scope.launch {
                        confettiController.showEmpty()
                    }
                }
            )
    ) {
        Row {
            repeat(5) {
                AnimatedVisibility(
                    visible = settingsState.emojisCount > it,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    EmojiItem(
                        fontScale = LocalSettingsState.current.fontScale
                            ?: LocalDensity.current.fontScale,
                        emoji = settingsState.selectedEmoji.toString(),
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                }
            }
        }
    }
}