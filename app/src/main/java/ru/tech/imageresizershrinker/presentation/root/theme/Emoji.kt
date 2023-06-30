package ru.tech.imageresizershrinker.presentation.root.theme

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
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Alien
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Alienmonster
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Amulet
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Anger
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Apple
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Avocado
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Axe
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bacon
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bagel
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Ball
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Banana
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bandage
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bang
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bathtub
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Battery
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bear
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Beaver
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Beef
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Beer
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Biohazard
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Blossom
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Blowfish
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bone
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bottle
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Brain
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bread
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Broccoli
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Bullseye
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Buoy
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Cactus
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Camera
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Candle
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Card
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Cat
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Cherries
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Cigarette
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Clinking1
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Clinking2
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Cloud
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Clover
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Cocktail
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Coffin
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Comet
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.ComputerMouse
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Construction
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Couch
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Crown
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.CrystalBall
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Diamond
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Die
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Disco
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Disk
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Droplets
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Eggplant
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Eyes
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Fire
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Firecracker
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Frog
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Gift
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Glass
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Globe
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Hamburger
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Heart
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Heartbroken
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Ice
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Key
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Lamp
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Lipstick
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Locked
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Malesign
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Map
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Medal
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Moai
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Money
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Moneybag
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.MoonA
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.MoonB
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Package
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Palette
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Paperclip
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Park
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Party
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Peach
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Pepper
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Phone
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Picture
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Pill
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Pole
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Pushpin
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Puzzle
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Radio
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Radioactive
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Recycling
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Rice
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Ring
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Rocket
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Sauropod
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Siren
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Skull
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Slots
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Snowflake
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Sparkles
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Star
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Sunrise
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Taxi
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Tick
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Tree
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Ufo
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Warning
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Wine
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Wood
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Zap
import ru.tech.imageresizershrinker.presentation.root.theme.emoji.Zzz

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