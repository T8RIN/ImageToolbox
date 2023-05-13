package ru.tech.imageresizershrinker.theme

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.theme.emoji.Alien
import ru.tech.imageresizershrinker.theme.emoji.Bang
import ru.tech.imageresizershrinker.theme.emoji.Clover
import ru.tech.imageresizershrinker.theme.emoji.Diamond
import ru.tech.imageresizershrinker.theme.emoji.Fire
import ru.tech.imageresizershrinker.theme.emoji.Frog
import ru.tech.imageresizershrinker.theme.emoji.Heart
import ru.tech.imageresizershrinker.theme.emoji.Moai
import ru.tech.imageresizershrinker.theme.emoji.Party
import ru.tech.imageresizershrinker.theme.emoji.Picture
import ru.tech.imageresizershrinker.theme.emoji.Pumpkin
import ru.tech.imageresizershrinker.theme.emoji.Snowflake
import ru.tech.imageresizershrinker.theme.emoji.Sparkles
import ru.tech.imageresizershrinker.theme.emoji.Star
import ru.tech.imageresizershrinker.theme.emoji.Sunrise
import kotlin.collections.List as ____KtList

public object Emoji

private var __Emoji: ____KtList<ImageVector>? = null

public val Emoji.list: ____KtList<ImageVector>
    get() {
        if (__Emoji != null) {
            return __Emoji!!
        }
        __Emoji = listOf(
            Sparkles,
            Alien,
            Bang,
            Clover,
            Diamond,
            Fire,
            Frog,
            Heart,
            Moai,
            Party,
            Picture,
            Pumpkin,
            Snowflake,
            Star,
            Sunrise
        )
        return __Emoji!!
    }

@Composable
fun EmojiItem(emoji: ImageVector?, fontSize: TextUnit = LocalTextStyle.current.fontSize) {
    AnimatedContent(targetState = emoji to fontSize) { (emoji, fontSize) ->
        emoji?.let {
            Box {
                1.sp
                Icon(
                    imageVector = emoji,
                    contentDescription = null,
                    modifier = Modifier
                        .size(
                            with(LocalDensity.current) {
                                fontSize.toDp()
                            }
                        )
                        .offset(1.dp, 1.dp),
                    tint = Color(0, 0, 0, 40)
                )
                Icon(
                    imageVector = emoji,
                    contentDescription = null,
                    modifier = Modifier.size(
                        with(LocalDensity.current) {
                            fontSize.toDp()
                        }
                    ),
                    tint = Color.Unspecified
                )
            }
        }
    }
}