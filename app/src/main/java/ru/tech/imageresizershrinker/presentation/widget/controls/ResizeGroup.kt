package ru.tech.imageresizershrinker.presentation.widget.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRichTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.presentation.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResizeGroup(
    enabled: Boolean,
    resizeType: Int,
    onResizeChange: (Int) -> Unit
) {
    val state = rememberRichTooltipState(true)
    val text = when (resizeType) {
        0 -> stringResource(R.string.explicit_description)
        1 -> stringResource(R.string.flexible_description)
        else -> stringResource(R.string.ratio_description)
    }
    val title = when (resizeType) {
        0 -> stringResource(R.string.explicit)
        1 -> stringResource(R.string.flexible)
        else -> stringResource(R.string.ratio)
    }
    val settingsState = LocalSettingsState.current
    RichTooltipBox(
        modifier = Modifier.alertDialog(),
        shape = MaterialTheme.shapes.extraLarge,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = text,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        action = {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant()
                ),
                onClick = {
                    state.dismiss()
                },
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        tooltipState = state
    ) {
        ToggleGroupButton(
            modifier = Modifier
                .block(shape = RoundedCornerShape(24.dp))
                .padding(start = 3.dp, end = 2.dp),
            enabled = enabled,
            title = {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.resize_type),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                CircleShape
                            )
                            .padding(1.dp)
                            .size(
                                with(LocalDensity.current) {
                                    LocalTextStyle.current.fontSize.toDp()
                                }
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        state.show()
                                        tryAwaitRelease()
                                        state.dismiss()
                                    }
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            },
            items = listOf(
                stringResource(R.string.explicit),
                stringResource(R.string.flexible),
                stringResource(R.string.ratio)
            ),
            selectedIndex = resizeType,
            indexChanged = onResizeChange
        )
    }
}