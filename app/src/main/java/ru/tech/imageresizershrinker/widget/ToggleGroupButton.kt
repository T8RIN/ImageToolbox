package ru.tech.imageresizershrinker.widget

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.theme.mixedColor
import ru.tech.imageresizershrinker.theme.onMixedColor

@Composable
fun ToggleGroupButton(
    @SuppressLint("ModifierParameter") modifier: Modifier = defaultModifier,
    enabled: Boolean,
    items: List<String>,
    selectedIndex: Int,
    title: String? = null,
    indexChanged: (Int) -> Unit
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
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
            }
            CompositionLocalProvider(
                LocalRippleTheme provides GroupRipple
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        OutlinedButton(
                            enabled = enabled,
                            onClick = { indexChanged(index) },
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier
                                .widthIn(min = 48.dp)
                                .weight(1f)
                                .then(
                                    when (index) {
                                        0 ->
                                            Modifier
                                                .offset(0.dp, 0.dp)
                                                .zIndex(if (selectedIndex == 0) 1f else 0f)
                                        else ->
                                            Modifier
                                                .offset((-1 * index).dp, 0.dp)
                                                .zIndex(if (selectedIndex == index) 1f else 0f)
                                    }
                                ),
                            shape = when (index) {
                                0 -> RoundedCornerShape(
                                    topStart = cornerRadius,
                                    topEnd = 0.dp,
                                    bottomStart = cornerRadius,
                                    bottomEnd = 0.dp
                                )
                                items.size - 1 -> RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = cornerRadius,
                                    bottomStart = 0.dp,
                                    bottomEnd = cornerRadius
                                )
                                else -> RoundedCornerShape(0.dp)
                            },
                            border = BorderStroke(
                                max(LocalBorderWidth.current, 1.dp),
                                MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (!enabled) disColor
                                else if (selectedIndex == index) MaterialTheme.colorScheme.mixedColor
                                else Color.Transparent
                            )
                        ) {
                            AutoSizeText(
                                text = item,
                                color = if (!enabled) disColor
                                else if (selectedIndex == index) MaterialTheme.colorScheme.onMixedColor
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

private var defaultModifier = Modifier
    .fillMaxWidth()
    .padding(8.dp)


object GroupRipple : RippleTheme {
    private const val alpha = 0.1f

    @Composable
    override fun defaultColor(): Color = if (isSystemInDarkTheme()) Color.White
    else Color.Black

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(alpha, alpha, alpha, alpha)
}