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
import ru.tech.imageresizershrinker.theme.emoji.Anger
import ru.tech.imageresizershrinker.theme.emoji.Apple
import ru.tech.imageresizershrinker.theme.emoji.Avocado
import ru.tech.imageresizershrinker.theme.emoji.Bacon
import ru.tech.imageresizershrinker.theme.emoji.Ball
import ru.tech.imageresizershrinker.theme.emoji.Bandage
import ru.tech.imageresizershrinker.theme.emoji.Bang
import ru.tech.imageresizershrinker.theme.emoji.Bear
import ru.tech.imageresizershrinker.theme.emoji.Beaver
import ru.tech.imageresizershrinker.theme.emoji.Beef
import ru.tech.imageresizershrinker.theme.emoji.Beer
import ru.tech.imageresizershrinker.theme.emoji.Biohazard
import ru.tech.imageresizershrinker.theme.emoji.Blossom
import ru.tech.imageresizershrinker.theme.emoji.Blowfish
import ru.tech.imageresizershrinker.theme.emoji.Bottle
import ru.tech.imageresizershrinker.theme.emoji.Brain
import ru.tech.imageresizershrinker.theme.emoji.Broccoli
import ru.tech.imageresizershrinker.theme.emoji.Camera
import ru.tech.imageresizershrinker.theme.emoji.Cat
import ru.tech.imageresizershrinker.theme.emoji.Cloud
import ru.tech.imageresizershrinker.theme.emoji.Clover
import ru.tech.imageresizershrinker.theme.emoji.Crown
import ru.tech.imageresizershrinker.theme.emoji.Diamond
import ru.tech.imageresizershrinker.theme.emoji.Disco
import ru.tech.imageresizershrinker.theme.emoji.Disk
import ru.tech.imageresizershrinker.theme.emoji.Droplets
import ru.tech.imageresizershrinker.theme.emoji.Eggplant
import ru.tech.imageresizershrinker.theme.emoji.Eyes
import ru.tech.imageresizershrinker.theme.emoji.Fire
import ru.tech.imageresizershrinker.theme.emoji.Frog
import ru.tech.imageresizershrinker.theme.emoji.Gift
import ru.tech.imageresizershrinker.theme.emoji.Glass
import ru.tech.imageresizershrinker.theme.emoji.Globe
import ru.tech.imageresizershrinker.theme.emoji.Hamburger
import ru.tech.imageresizershrinker.theme.emoji.Heart
import ru.tech.imageresizershrinker.theme.emoji.Heartbroken
import ru.tech.imageresizershrinker.theme.emoji.Ice
import ru.tech.imageresizershrinker.theme.emoji.Lamp
import ru.tech.imageresizershrinker.theme.emoji.Lipstick
import ru.tech.imageresizershrinker.theme.emoji.Malesign
import ru.tech.imageresizershrinker.theme.emoji.Map
import ru.tech.imageresizershrinker.theme.emoji.Moai
import ru.tech.imageresizershrinker.theme.emoji.Money
import ru.tech.imageresizershrinker.theme.emoji.Moneybag
import ru.tech.imageresizershrinker.theme.emoji.MoonA
import ru.tech.imageresizershrinker.theme.emoji.MoonB
import ru.tech.imageresizershrinker.theme.emoji.Package
import ru.tech.imageresizershrinker.theme.emoji.Palette
import ru.tech.imageresizershrinker.theme.emoji.Park
import ru.tech.imageresizershrinker.theme.emoji.Party
import ru.tech.imageresizershrinker.theme.emoji.Peach
import ru.tech.imageresizershrinker.theme.emoji.Pepper
import ru.tech.imageresizershrinker.theme.emoji.Phone
import ru.tech.imageresizershrinker.theme.emoji.Picture
import ru.tech.imageresizershrinker.theme.emoji.Pill
import ru.tech.imageresizershrinker.theme.emoji.Radioactive
import ru.tech.imageresizershrinker.theme.emoji.Recycling
import ru.tech.imageresizershrinker.theme.emoji.Rocket
import ru.tech.imageresizershrinker.theme.emoji.Skull
import ru.tech.imageresizershrinker.theme.emoji.Snowflake
import ru.tech.imageresizershrinker.theme.emoji.Sparkles
import ru.tech.imageresizershrinker.theme.emoji.Star
import ru.tech.imageresizershrinker.theme.emoji.Sunrise
import ru.tech.imageresizershrinker.theme.emoji.Taxi
import ru.tech.imageresizershrinker.theme.emoji.Ufo
import ru.tech.imageresizershrinker.theme.emoji.Warning
import ru.tech.imageresizershrinker.theme.emoji.Wood
import ru.tech.imageresizershrinker.theme.emoji.Zap
import ru.tech.imageresizershrinker.theme.emoji.Zzz

object Emoji

private var EmojiList: List<ImageVector>? = null

public val Emoji.allIcons: List<ImageVector>
    get() {
        if (EmojiList != null) {
            return EmojiList!!
        }
        EmojiList = listOf(
            Sparkles,
            Alien,
            Amulet,
            Anger,
            Apple,
            Avocado,
            Bacon,
            Ball,
            Bandage,
            Bang,
            Bear,
            Beaver,
            Beef,
            Beer,
            Biohazard,
            Blossom,
            Blowfish,
            Bottle,
            Brain,
            Broccoli,
            Camera,
            Cat,
            Cloud,
            Clover,
            Crown,
            Diamond,
            Disco,
            Disk,
            Droplets,
            Eggplant,
            Eyes,
            Fire,
            Frog,
            Gift,
            Glass,
            Globe,
            Hamburger,
            Heart,
            Heartbroken,
            Ice,
            Lamp,
            Lipstick,
            Malesign,
            Map,
            Moai,
            Money,
            Moneybag,
            MoonA,
            MoonB,
            Package,
            Palette,
            Park,
            Party,
            Peach,
            Pepper,
            Phone,
            Picture,
            Pill,
            Radioactive,
            Recycling,
            Rocket,
            Skull,
            Snowflake,
            Star,
            Sunrise,
            Taxi,
            Ufo,
            Warning,
            Wood,
            Zap,
            Zzz
        )
        return EmojiList!!
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