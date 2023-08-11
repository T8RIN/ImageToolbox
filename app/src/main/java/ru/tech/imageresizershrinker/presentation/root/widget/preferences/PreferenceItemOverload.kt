package ru.tech.imageresizershrinker.presentation.root.widget.preferences

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreferenceItemOverload(
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    contentColor: Color = if (color == MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)) contentColorFor(
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    ) else contentColorFor(backgroundColor = color),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
) {

    val settingsState = LocalSettingsState.current

    ProvideTextStyle(value = LocalTextStyle.current.copy(textAlign = TextAlign.Start)) {
        Card(
            shape = shape,
            modifier = modifier
                .border(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(0.1f, color),
                    shape
                )
                .clip(shape)
                .then(
                    if (onClick != null) {
                        Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
                    } else Modifier
                ),
            colors = CardDefaults.cardColors(
                containerColor = color,
                contentColor = contentColor
            )
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = icon,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { icon ->
                    icon?.let {
                        Row {
                            it()
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    AnimatedContent(
                        targetState = title,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { title ->
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                    AnimatedContent(
                        targetState = subtitle,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { sub ->
                        sub?.let {
                            Column {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sub,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 14.sp,
                                    color = LocalContentColor.current.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
                AnimatedContent(
                    targetState = endIcon,
                    transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                ) { icon ->
                    icon?.let {
                        it()
                    }
                }
            }
        }
    }
}