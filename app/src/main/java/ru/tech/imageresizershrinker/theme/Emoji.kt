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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.theme.emoji.Alien
import ru.tech.imageresizershrinker.theme.emoji.Amulet
import ru.tech.imageresizershrinker.theme.emoji.Apple
import ru.tech.imageresizershrinker.theme.emoji.Bang
import ru.tech.imageresizershrinker.theme.emoji.Biohazard
import ru.tech.imageresizershrinker.theme.emoji.Cloud
import ru.tech.imageresizershrinker.theme.emoji.Clover
import ru.tech.imageresizershrinker.theme.emoji.Diamond
import ru.tech.imageresizershrinker.theme.emoji.Droplets
import ru.tech.imageresizershrinker.theme.emoji.Eggplant
import ru.tech.imageresizershrinker.theme.emoji.Eyes
import ru.tech.imageresizershrinker.theme.emoji.Fire
import ru.tech.imageresizershrinker.theme.emoji.Frog
import ru.tech.imageresizershrinker.theme.emoji.Heart
import ru.tech.imageresizershrinker.theme.emoji.Ice
import ru.tech.imageresizershrinker.theme.emoji.Malesign
import ru.tech.imageresizershrinker.theme.emoji.Moai
import ru.tech.imageresizershrinker.theme.emoji.Money
import ru.tech.imageresizershrinker.theme.emoji.Moneybag
import ru.tech.imageresizershrinker.theme.emoji.Package
import ru.tech.imageresizershrinker.theme.emoji.Party
import ru.tech.imageresizershrinker.theme.emoji.Peach
import ru.tech.imageresizershrinker.theme.emoji.Picture
import ru.tech.imageresizershrinker.theme.emoji.Pill
import ru.tech.imageresizershrinker.theme.emoji.Pumpkin
import ru.tech.imageresizershrinker.theme.emoji.Radioactive
import ru.tech.imageresizershrinker.theme.emoji.Recycling
import ru.tech.imageresizershrinker.theme.emoji.Snowflake
import ru.tech.imageresizershrinker.theme.emoji.Sparkles
import ru.tech.imageresizershrinker.theme.emoji.Star
import ru.tech.imageresizershrinker.theme.emoji.Sunrise
import ru.tech.imageresizershrinker.theme.emoji.Warning
import ru.tech.imageresizershrinker.theme.emoji.Zzz
import kotlin.collections.List as ____KtList

object Emoji

private var __Emoji: ____KtList<ImageVector>? = null

public val Emoji.list: ____KtList<ImageVector>
    get() {
        if (__Emoji != null) {
            return __Emoji!!
        }
        __Emoji = listOf(
            Sparkles,
            Alien,
            Amulet,
            Apple,
            Bang,
            Biohazard,
            Cloud,
            Clover,
            Diamond,
            Droplets,
            Eggplant,
            Eyes,
            Fire,
            Frog,
            Heart,
            Ice,
            Malesign,
            Moai,
            Money,
            Moneybag,
            Package,
            Party,
            Peach,
            Picture,
            Pill,
            Pumpkin,
            Radioactive,
            Recycling,
            Snowflake,
            Star,
            Sunrise,
            Warning,
            Zzz
        )
        return __Emoji!!
    }

@Composable
fun EmojiItem(
    emoji: ImageVector?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
    onNoEmoji: @Composable (size: Dp) -> Unit = {}
) {
    AnimatedContent(
        targetState = emoji to fontSize,
        modifier = modifier
    ) { (emoji, fontSize) ->
        val size = with(LocalDensity.current) { fontSize.toDp() }
        emoji?.let {
            Box {
                1.sp
                Icon(
                    imageVector = emoji,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size)
                        .offset(1.dp, 1.dp),
                    tint = Color(0, 0, 0, 40)
                )
                Icon(
                    imageVector = emoji,
                    contentDescription = null,
                    modifier = Modifier.size(size),
                    tint = Color.Unspecified
                )
            }
        } ?: onNoEmoji(size)
    }
}