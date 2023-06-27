package ru.tech.imageresizershrinker.presentation.widget.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.presentation.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.widget.buttons.GroupRipple
import ru.tech.imageresizershrinker.presentation.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState

@Composable
fun ExtensionGroup(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    mimeType: MimeType,
    orientation: Orientation = Orientation.Horizontal,
    onMimeChange: (MimeType) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val cornerRadius = 20.dp

    val disColor = MaterialTheme.colorScheme.onSurface
        .copy(alpha = 0.38f)
        .compositeOver(MaterialTheme.colorScheme.surface)

    ProvideTextStyle(
        value = TextStyle(
            color = if (!enabled) disColor
            else Color.Unspecified
        )
    ) {
        if (orientation == Orientation.Horizontal) {
            Column(
                modifier = modifier
                    .block(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    )
                    .offset(x = 0.dp, y = 9.dp)
                    .padding(start = 4.dp, end = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.extension),
                    Modifier
                        .fillMaxWidth()
                        .offset(x = 0.dp, y = (-1).dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    TODO()
                }
            }
        } else {
            Column(
                modifier = modifier
                    .block(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    )
                    .offset(x = 0.dp, y = 9.dp)
                    .padding(start = 4.dp, end = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.extension),
                    Modifier
                        .offset(x = 0.dp, y = (-1).dp)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    TODO()
                }
            }
        }
    }
}