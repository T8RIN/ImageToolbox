package ru.tech.imageresizershrinker.presentation.theme

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
import ru.tech.imageresizershrinker.presentation.theme.emoji.Alien
import ru.tech.imageresizershrinker.presentation.theme.emoji.Alienmonster
import ru.tech.imageresizershrinker.presentation.theme.emoji.Amulet
import ru.tech.imageresizershrinker.presentation.theme.emoji.Anger
import ru.tech.imageresizershrinker.presentation.theme.emoji.Apple
import ru.tech.imageresizershrinker.presentation.theme.emoji.Avocado
import ru.tech.imageresizershrinker.presentation.theme.emoji.Axe
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bacon
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bagel
import ru.tech.imageresizershrinker.presentation.theme.emoji.Ball
import ru.tech.imageresizershrinker.presentation.theme.emoji.Banana
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bandage
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bang
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bathtub
import ru.tech.imageresizershrinker.presentation.theme.emoji.Battery
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bear
import ru.tech.imageresizershrinker.presentation.theme.emoji.Beaver
import ru.tech.imageresizershrinker.presentation.theme.emoji.Beef
import ru.tech.imageresizershrinker.presentation.theme.emoji.Beer
import ru.tech.imageresizershrinker.presentation.theme.emoji.Biohazard
import ru.tech.imageresizershrinker.presentation.theme.emoji.Blossom
import ru.tech.imageresizershrinker.presentation.theme.emoji.Blowfish
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bone
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bottle
import ru.tech.imageresizershrinker.presentation.theme.emoji.Brain
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bread
import ru.tech.imageresizershrinker.presentation.theme.emoji.Broccoli
import ru.tech.imageresizershrinker.presentation.theme.emoji.Bullseye
import ru.tech.imageresizershrinker.presentation.theme.emoji.Buoy
import ru.tech.imageresizershrinker.presentation.theme.emoji.Cactus
import ru.tech.imageresizershrinker.presentation.theme.emoji.Camera
import ru.tech.imageresizershrinker.presentation.theme.emoji.Candle
import ru.tech.imageresizershrinker.presentation.theme.emoji.Card
import ru.tech.imageresizershrinker.presentation.theme.emoji.Cat
import ru.tech.imageresizershrinker.presentation.theme.emoji.Cherries
import ru.tech.imageresizershrinker.presentation.theme.emoji.Cigarette
import ru.tech.imageresizershrinker.presentation.theme.emoji.Clinking1
import ru.tech.imageresizershrinker.presentation.theme.emoji.Clinking2
import ru.tech.imageresizershrinker.presentation.theme.emoji.Cloud
import ru.tech.imageresizershrinker.presentation.theme.emoji.Clover
import ru.tech.imageresizershrinker.presentation.theme.emoji.Cocktail
import ru.tech.imageresizershrinker.presentation.theme.emoji.Coffin
import ru.tech.imageresizershrinker.presentation.theme.emoji.Comet
import ru.tech.imageresizershrinker.presentation.theme.emoji.ComputerMouse
import ru.tech.imageresizershrinker.presentation.theme.emoji.Construction
import ru.tech.imageresizershrinker.presentation.theme.emoji.Couch
import ru.tech.imageresizershrinker.presentation.theme.emoji.Crown
import ru.tech.imageresizershrinker.presentation.theme.emoji.CrystalBall
import ru.tech.imageresizershrinker.presentation.theme.emoji.Diamond
import ru.tech.imageresizershrinker.presentation.theme.emoji.Die
import ru.tech.imageresizershrinker.presentation.theme.emoji.Disco
import ru.tech.imageresizershrinker.presentation.theme.emoji.Disk
import ru.tech.imageresizershrinker.presentation.theme.emoji.Droplets
import ru.tech.imageresizershrinker.presentation.theme.emoji.Eggplant
import ru.tech.imageresizershrinker.presentation.theme.emoji.Eyes
import ru.tech.imageresizershrinker.presentation.theme.emoji.Fire
import ru.tech.imageresizershrinker.presentation.theme.emoji.Firecracker
import ru.tech.imageresizershrinker.presentation.theme.emoji.Frog
import ru.tech.imageresizershrinker.presentation.theme.emoji.Gift
import ru.tech.imageresizershrinker.presentation.theme.emoji.Glass
import ru.tech.imageresizershrinker.presentation.theme.emoji.Globe
import ru.tech.imageresizershrinker.presentation.theme.emoji.Hamburger
import ru.tech.imageresizershrinker.presentation.theme.emoji.Heart
import ru.tech.imageresizershrinker.presentation.theme.emoji.Heartbroken
import ru.tech.imageresizershrinker.presentation.theme.emoji.Ice
import ru.tech.imageresizershrinker.presentation.theme.emoji.Key
import ru.tech.imageresizershrinker.presentation.theme.emoji.Lamp
import ru.tech.imageresizershrinker.presentation.theme.emoji.Lipstick
import ru.tech.imageresizershrinker.presentation.theme.emoji.Locked
import ru.tech.imageresizershrinker.presentation.theme.emoji.Malesign
import ru.tech.imageresizershrinker.presentation.theme.emoji.Map
import ru.tech.imageresizershrinker.presentation.theme.emoji.Medal
import ru.tech.imageresizershrinker.presentation.theme.emoji.Moai
import ru.tech.imageresizershrinker.presentation.theme.emoji.Money
import ru.tech.imageresizershrinker.presentation.theme.emoji.Moneybag
import ru.tech.imageresizershrinker.presentation.theme.emoji.MoonA
import ru.tech.imageresizershrinker.presentation.theme.emoji.MoonB
import ru.tech.imageresizershrinker.presentation.theme.emoji.Package
import ru.tech.imageresizershrinker.presentation.theme.emoji.Palette
import ru.tech.imageresizershrinker.presentation.theme.emoji.Paperclip
import ru.tech.imageresizershrinker.presentation.theme.emoji.Park
import ru.tech.imageresizershrinker.presentation.theme.emoji.Party
import ru.tech.imageresizershrinker.presentation.theme.emoji.Peach
import ru.tech.imageresizershrinker.presentation.theme.emoji.Pepper
import ru.tech.imageresizershrinker.presentation.theme.emoji.Phone
import ru.tech.imageresizershrinker.presentation.theme.emoji.Picture
import ru.tech.imageresizershrinker.presentation.theme.emoji.Pill
import ru.tech.imageresizershrinker.presentation.theme.emoji.Pole
import ru.tech.imageresizershrinker.presentation.theme.emoji.Pushpin
import ru.tech.imageresizershrinker.presentation.theme.emoji.Puzzle
import ru.tech.imageresizershrinker.presentation.theme.emoji.Radio
import ru.tech.imageresizershrinker.presentation.theme.emoji.Radioactive
import ru.tech.imageresizershrinker.presentation.theme.emoji.Recycling
import ru.tech.imageresizershrinker.presentation.theme.emoji.Rice
import ru.tech.imageresizershrinker.presentation.theme.emoji.Ring
import ru.tech.imageresizershrinker.presentation.theme.emoji.Rocket
import ru.tech.imageresizershrinker.presentation.theme.emoji.Sauropod
import ru.tech.imageresizershrinker.presentation.theme.emoji.Siren
import ru.tech.imageresizershrinker.presentation.theme.emoji.Skull
import ru.tech.imageresizershrinker.presentation.theme.emoji.Slots
import ru.tech.imageresizershrinker.presentation.theme.emoji.Snowflake
import ru.tech.imageresizershrinker.presentation.theme.emoji.Sparkles
import ru.tech.imageresizershrinker.presentation.theme.emoji.Star
import ru.tech.imageresizershrinker.presentation.theme.emoji.Sunrise
import ru.tech.imageresizershrinker.presentation.theme.emoji.Taxi
import ru.tech.imageresizershrinker.presentation.theme.emoji.Tick
import ru.tech.imageresizershrinker.presentation.theme.emoji.Tree
import ru.tech.imageresizershrinker.presentation.theme.emoji.Ufo
import ru.tech.imageresizershrinker.presentation.theme.emoji.Warning
import ru.tech.imageresizershrinker.presentation.theme.emoji.Wine
import ru.tech.imageresizershrinker.presentation.theme.emoji.Wood
import ru.tech.imageresizershrinker.presentation.theme.emoji.Zap
import ru.tech.imageresizershrinker.presentation.theme.emoji.Zzz

object Emoji

private var EmojiList: List<ImageVector>? = null

val Emoji.allIcons: List<ImageVector>
    get() {
        EmojiList?.let { return it }
        EmojiList = listOf(Sparkles) + listOf(
            Alien,
            Alienmonster,
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
            Key,
            Lamp,
            Lipstick,
            Locked,
            Malesign,
            Map,
            Medal,
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
            Ring,
            Rocket,
            Skull,
            Snowflake,
            Star,
            Sunrise,
            Taxi,
            Tree,
            Ufo,
            Warning,
            Wood,
            Zap,
            Zzz,
            Axe,
            Bread,
            Bullseye,
            Buoy,
            Cactus,
            Coffin,
            Comet,
            ComputerMouse,
            Construction,
            CrystalBall,
            Die,
            Firecracker,
            Pushpin,
            Puzzle,
            Radio,
            Rice,
            Sauropod,
            Slots,
            Bagel,
            Banana,
            Battery,
            Candle,
            Card,
            Cherries,
            Cigarette,
            Clinking1,
            Clinking2,
            Paperclip,
            Tick,
            Wine, Bathtub, Bone, Cocktail, Couch, Pole, Siren
        ).sortedBy { it.name }
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