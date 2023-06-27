package ru.tech.imageresizershrinker.presentation.widget.other

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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.presentation.theme.EmojiItem
import ru.tech.imageresizershrinker.presentation.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.utils.modifier.scaleOnTap
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState

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
                        emoji = settingsState.selectedEmoji,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                }
            }
        }
    }
}