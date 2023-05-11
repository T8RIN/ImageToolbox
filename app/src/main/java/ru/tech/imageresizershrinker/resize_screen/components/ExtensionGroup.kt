package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.theme.mixedColor
import ru.tech.imageresizershrinker.theme.onMixedColor
import ru.tech.imageresizershrinker.widget.AutoSizeText
import ru.tech.imageresizershrinker.widget.GroupRipple

@Composable
fun ExtensionGroup(
    enabled: Boolean,
    mime: Int,
    onMimeChange: (Int) -> Unit
) {
    val cornerRadius = 12.dp

    val disColor = MaterialTheme.colorScheme.onSurface
        .copy(alpha = 0.38f)
        .compositeOver(MaterialTheme.colorScheme.surface)

    ProvideTextStyle(
        value = TextStyle(
            color = if (!enabled) disColor
            else Color.Unspecified
        )
    ) {
        Column(
            modifier = Modifier
                .block(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                )
                .offset(x = 0.dp, y = 9.dp)
                .padding(start = 3.dp, end = 2.dp),
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

            CompositionLocalProvider(
                LocalRippleTheme provides GroupRipple
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("JPG", "WEBP").forEachIndexed { index, item ->
                            OutlinedButton(
                                enabled = enabled,
                                onClick = { onMimeChange(index) },
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier
                                    .widthIn(min = 48.dp)
                                    .weight(1f)
                                    .then(
                                        when (index) {
                                            0 ->
                                                Modifier
                                                    .offset(0.dp, 0.dp)
                                                    .zIndex(if (mime == 0) 1f else 0f)

                                            else ->
                                                Modifier
                                                    .offset((-1 * index).dp, 0.dp)
                                                    .zIndex(if (mime == index) 1f else 0f)
                                        }
                                    ),
                                shape = when (index) {
                                    0 -> RoundedCornerShape(
                                        topStart = cornerRadius,
                                        topEnd = 0.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 0.dp
                                    )

                                    else -> RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = cornerRadius,
                                        bottomStart = 0.dp,
                                        bottomEnd = 0.dp
                                    )
                                },
                                border = BorderStroke(
                                    max(LocalBorderWidth.current, 1.dp),
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (!enabled) disColor
                                    else if (mime == index) MaterialTheme.colorScheme.mixedColor
                                    else Color.Transparent
                                )
                            ) {
                                AutoSizeText(
                                    text = item,
                                    color = if (!enabled) disColor
                                    else if (mime == index) MaterialTheme.colorScheme.onMixedColor
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("JPEG", "PNG").forEachIndexed { i, item ->
                            val index = i + 2
                            OutlinedButton(
                                enabled = enabled,
                                onClick = { onMimeChange(index) },
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier
                                    .widthIn(min = 48.dp)
                                    .weight(1f)
                                    .then(
                                        when (index) {
                                            2 ->
                                                Modifier
                                                    .offset(0.dp, (-9).dp)
                                                    .zIndex(if (mime == 0) 2f else 1f)

                                            else ->
                                                Modifier
                                                    .offset((-1 * i).dp, (-9).dp)
                                                    .zIndex(if (mime == index) 2f else 1f)
                                        }
                                    ),
                                shape = when (index) {
                                    2 -> RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = cornerRadius,
                                        bottomEnd = 0.dp
                                    )

                                    else -> RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = cornerRadius
                                    )
                                },
                                border = BorderStroke(
                                    max(LocalBorderWidth.current, 1.dp),
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (!enabled) disColor
                                    else if (mime == index) MaterialTheme.colorScheme.mixedColor
                                    else Color.Transparent
                                )
                            ) {
                                AutoSizeText(
                                    text = item,
                                    color = if (!enabled) disColor
                                    else if (mime == index) MaterialTheme.colorScheme.onMixedColor
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}