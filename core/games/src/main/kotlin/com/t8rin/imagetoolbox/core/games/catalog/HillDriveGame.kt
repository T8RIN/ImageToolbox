/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathSegment
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.components.longPress
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.UnpavedRoad
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asinh
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object HillDriveGame : ArcadeGameDefinition {
    override val title = R.string.arcade_hilldrive
    override val description = R.string.arcade_hilldrive_sub
    override val icon get() = Icons.Outlined.UnpavedRoad
    override val order = 40

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::HillDriveRun
        )
        val measurer = rememberTextMeasurer(cacheSize = 32)
        val style = context.style
        val colors = MaterialTheme.colorScheme
        val palette = remember(colors, style) { HillDrivePalette(colors, style) }
        val road = remember { Path() }
        val details = remember { Path() }
        val haptics = LocalHapticFeedback.current
        val running = run.phase == ArcadePhase.Running && !context.isPaused

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            Box(Modifier.fillMaxSize()) {
                ArcadeField(
                    run = run,
                    lightFeedback = true,
                    worldSize = Size(480f, 220f)
                ) {
                    drawHillDriveScene(run, palette, road, details)
                    val fieldScale = size.width / run.width
                    if (fieldScale > 0f) {
                        scale(1f / fieldScale, Offset.Zero) {
                            drawHillDriveHud(run, style, palette, measurer, fieldScale)
                        }
                    }
                }
                if (running) {
                    // The hit regions share the same screen-space bounds as the Canvas pedals.
                    Box(
                        Modifier
                            .matchParentSize()
                            .pointerInput(run, haptics) {
                                awaitEachGesture {
                                    val gestureSize = size
                                    val width = gestureSize.width.toFloat()
                                    val height = gestureSize.height.toFloat()
                                    val brakeBounds =
                                        hillDrivePedalBounds(width, height, density, gas = false)
                                    val gasBounds =
                                        hillDrivePedalBounds(width, height, density, gas = true)
                                    var gasFinger: PointerId? = null
                                    var brakeFinger: PointerId? = null
                                    try {
                                        val down = awaitFirstDown()
                                        when {
                                            brakeBounds.contains(down.position) -> brakeFinger =
                                                down.id

                                            gasBounds.contains(down.position) -> gasFinger = down.id
                                        }
                                        if (brakeFinger != null || gasFinger != null) {
                                            down.consume()
                                            haptics.longPress()
                                        }
                                        run.brake = brakeFinger != null
                                        run.gas = gasFinger != null
                                        do {
                                            val event = awaitPointerEvent()
                                            if (size != gestureSize || run.phase != ArcadePhase.Running) break
                                            for (change in event.changes) {
                                                if (change.id == gasFinger || change.id == brakeFinger) {
                                                    if (!change.pressed || change.isConsumed ||
                                                        change.position.x !in 0f..width ||
                                                        change.position.y !in 0f..height
                                                    ) {
                                                        if (change.id == gasFinger) gasFinger = null
                                                        if (change.id == brakeFinger) brakeFinger =
                                                            null
                                                    }
                                                    change.consume()
                                                } else if (change.pressed && !change.previousPressed && !change.isConsumed) {
                                                    when {
                                                        brakeFinger == null && brakeBounds.contains(
                                                            change.position
                                                        ) ->
                                                            brakeFinger = change.id

                                                        gasFinger == null && gasBounds.contains(
                                                            change.position
                                                        ) ->
                                                            gasFinger = change.id
                                                    }
                                                    if (change.id == gasFinger || change.id == brakeFinger) {
                                                        change.consume()
                                                        haptics.longPress()
                                                    }
                                                }
                                            }
                                            run.brake = brakeFinger != null
                                            run.gas = gasFinger != null
                                        } while (event.changes.any { it.pressed })
                                    } finally {
                                        run.stopInput()
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

private enum class HillDriveBiome { Meadow, Sand, Water, Snow, Moon }

private data class HillDrivePond(val start: Double, val end: Double, val level: Float)

private class HillDrivePalette(private val colors: ColorScheme, style: ArcadeStyle) {
    private val dark = colors.surface.luminance() < 0.5f
    private val light = if (dark) colors.onSurface else colors.surfaceBright
    val sky = lerp(colors.surfaceContainerLow, style.playerColor, 0.025f)
    val skyLow = lerp(colors.surfaceContainer, style.playerColor, 0.035f)
    val water = lerp(colors.primaryContainer, colors.secondary, 0.3f)
    private val sand = lerp(colors.tertiaryContainer, light, 0.35f)
    private val snow = lerp(light, colors.primaryContainer, 0.12f)
    private val rock = lerp(colors.surfaceContainerHighest, colors.primaryContainer, 0.2f)

    fun sky(biome: HillDriveBiome) = when (biome) {
        HillDriveBiome.Moon -> lerp(sky, ink, 0.8f)
        HillDriveBiome.Sand -> lerp(sky, sand, 0.18f)
        HillDriveBiome.Snow -> lerp(sky, snow, 0.2f)
        else -> sky
    }

    fun surface(biome: HillDriveBiome) = when (biome) {
        HillDriveBiome.Sand -> sand
        HillDriveBiome.Snow -> snow
        HillDriveBiome.Moon -> rock
        HillDriveBiome.Water -> lerp(grass, water, 0.35f)
        else -> grass
    }
    val ink =
        lerp(if (dark) colors.surfaceContainerLowest else colors.inverseSurface, colors.scrim, 0.1f)
    val body = lerp(colors.primaryContainer, light, 0.88f)
    val bodyLight = lerp(body, light, 0.6f)
    val bodyShade = lerp(style.playerColor, colors.surfaceContainerHigh, 0.48f)
    val paint = style.playerColor
    val metal = lerp(bodyShade, body, 0.25f)
    val grass = lerp(style.playerColor, body, 0.48f)
    val grassLight = lerp(grass, body, 0.28f)
    val earth = lerp(colors.surfaceContainer, style.playerColor, 0.085f)
    val earthShade = lerp(colors.surfaceContainerLow, style.playerColor, 0.045f)
    val window = lerp(if (dark) earthShade else ink, style.playerColor, 0.025f)
    val cloud = lerp(colors.outline, style.playerColor, 0.2f).copy(alpha = 0.46f)
    val dust = lerp(body, grass, 0.2f)
    val glint = lerp(style.accentColor, light, 0.82f)
    val gold = style.accentColor
    val goldShade = lerp(gold, ink, 0.35f)
    val goldMark = lerp(gold, ink, 0.78f)
    val can = lerp(colors.errorContainer, style.hazardColor, if (dark) 0.36f else 0.88f)
    val canShade = lerp(can, ink, 0.28f)
    val bridge = lerp(colors.secondary, bodyShade, 0.4f)
    val bridgeLight = lerp(bridge, body, 0.36f)
    val hud = colors.surfaceContainerHigh
    val pedal = lerp(colors.secondaryContainer, light, 0.78f)
    val pedalLight = lerp(pedal, light, 0.55f)
    val pedalShade = lerp(pedal, bodyShade, 0.32f)
    val pedalRim = lerp(pedal, earthShade, 0.56f)
    val pedalHole = lerp(earthShade, ink, 0.35f)
    val tire = lerp(earthShade, ink, if (dark) 0.28f else 0.8f)
    val carColors = listOf(
        lerp(earthShade, bodyShade, 0.28f), bodyShade, body, bodyLight,
        lerp(body, paint, 0.08f), lerp(bodyShade, earth, 0.3f), paint,
        lerp(paint, body, 0.32f), window, lerp(window, bodyShade, 0.12f),
        lerp(window, body, 0.08f), metal, lerp(body, paint, 0.18f), glint, can
    )
    val wheelColors = listOf(
        tire, lerp(tire, bodyShade, 0.14f), lerp(tire, bodyShade, 0.36f),
        lerp(body, paint, 0.45f), bodyLight, bodyShade, metal
    )
    val canColors = listOf(
        canShade, can, lerp(can, light, 0.3f),
        lerp(can, canShade, 0.32f), lerp(can, ink, 0.82f)
    )
    val coinColors = listOf(
        goldShade, lerp(gold, goldShade, 0.55f),
        lerp(gold, glint, 0.28f), gold, lerp(gold, glint, 0.55f),
        lerp(gold, goldShade, 0.25f), goldMark
    )
    val pedalColors = listOf(
        pedalHole, lerp(pedalRim, ink, 0.28f), pedalShade,
        pedal, lerp(pedal, pedalLight, 0.18f), pedalLight
    )
}

private fun DrawScope.drawHillDriveSprite(
    layers: Map<Int, ArcadeSprite>,
    colors: List<Color>,
    x: Float,
    y: Float,
    pixelSize: Float = 1f
) {
    translate(x, y) {
        scale(pixelSize, Offset.Zero) {
            for ((color, sprite) in layers) {
                drawArcadeSprite(sprite, colors[color], 0f, 0f)
            }
        }
    }
}

private fun DrawScope.drawHillDriveScene(
    run: HillDriveRun,
    palette: HillDrivePalette,
    road: Path,
    details: Path
) {
    val left = run.cameraX - run.cameraAnchorX
    val ground = run.cameraAnchorY - run.cameraY
    val width = run.width
    val height = run.height

    val biome = run.biomeAt(run.cameraX)
    val previousBiome = run.biomeAt(run.cameraX - 450.0)
    val blend = run.biomeTransition(run.cameraX)
    val sky = lerp(palette.sky(previousBiome), palette.sky(biome), blend)
    val surface = lerp(palette.surface(previousBiome), palette.surface(biome), blend)
    val moon = run.biomeWeight(run.cameraX, HillDriveBiome.Moon)
    drawRect(
        brush = Brush.verticalGradient(listOf(sky, lerp(sky, palette.skyLow, 0.3f)), endY = height),
        size = Size(width, height)
    )

    if (moon > 0f) {
        for (star in 0..28) {
            val hash = hillDriveHash(star.toLong(), 193)
            val x = (hash and 1023) / 1024f * width
            val y = (hash ushr 10 and 255) / 256f * height * 0.6f
            pixelRect(palette.glint.copy(alpha = moon * 0.65f), x, y, 2f, 2f)
        }
    }
    val cloudScroll = left * 0.15
    var cloud = floor((cloudScroll - 80.0) / 227.0).toLong()
    while (cloud * 227.0 - cloudScroll < width + 80f) {
        val variant = hillDriveHash(cloud, 115)
        val x = (cloud * 227.0 - cloudScroll).toFloat() + (variant and 31)
        val y = height * (0.23f + (variant ushr 8 and 3) * 0.065f) - run.cameraY * 0.06f
        val sprite = if (variant and 32 == 0) HillDriveCloud else HillDriveCloudSmall
        drawArcadeSprite(
            sprite,
            palette.cloud.copy(alpha = palette.cloud.alpha * 0.36f * (1f - moon)),
            x,
            y
        )
        pixelRect(
            palette.cloud.copy(alpha = palette.cloud.alpha * 0.32f * (1f - moon)),
            x + 5f, y + sprite.height - 3f, sprite.width - 10f, 2f
        )
        cloud++
    }

    road.rewind()
    var column = floor(left / 2.0).toLong()
    var screenX = (column * 2.0 - left).toFloat()
    var drawing = false
    while (screenX <= width + 2f) {
        val worldX = column * 2.0
        if (run.isOnBridge(worldX)) {
            if (drawing) {
                road.lineTo(screenX, height + 160f)
                road.close()
                drawing = false
            }
        } else {
            val y = run.terrainHeight(worldX).roundToInt().toFloat() + ground
            if (!drawing) {
                road.moveTo(screenX, height + 160f)
                drawing = true
            }
            road.lineTo(screenX, y)
            road.lineTo(screenX + 2f, y)
        }
        column++
        screenX = (column * 2.0 - left).toFloat()
    }
    if (drawing) {
        road.lineTo(screenX, height + 160f)
        road.close()
    }
    drawPath(road, lerp(surface, palette.bodyLight, 0.28f))
    translate(top = 2f) { drawPath(road, surface) }
    translate(top = 6f) { drawPath(road, lerp(palette.earthShade, surface, 0.2f)) }
    details.rewind()
    val coordinates = FloatArray(8)
    var tile = floor(left / HillDriveSoil.width).toLong()
    while (tile * HillDriveSoil.width - left < width) {
        val tileX = tile * HillDriveSoil.width.toDouble()
        val iterator = HillDriveSoil.path.iterator()
        while (iterator.hasNext()) {
            when (val segment = iterator.next(coordinates)) {
                PathSegment.Type.Move, PathSegment.Type.Line -> {
                    val index = if (segment == PathSegment.Type.Move) 0 else 2
                    val worldX = tileX + coordinates[index]
                    val x = (worldX - left).toFloat()
                    val y =
                        ground + run.terrainHeight(worldX)
                            .roundToInt() + 6f + coordinates[index + 1]
                    if (segment == PathSegment.Type.Move) details.moveTo(x, y)
                    else details.lineTo(x, y)
                }

                PathSegment.Type.Close -> details.close()
                else -> Unit
            }
        }
        tile++
    }
    clipPath(road) { drawPath(details, palette.earth) }
    column = floor(left / 16.0).toLong()
    while (column * 16.0 - left < width) {
        val worldX = column * 16.0
        if (!run.isOnBridge(worldX) && hillDriveHash(column, 72) and 7 == 3) {
            val y = ground + run.terrainHeight(worldX).roundToInt()
            val biome = run.biomeAt(worldX)
            val sprite = if (biome == HillDriveBiome.Sand) HillDriveCactus else HillDriveGrass
            if (biome != HillDriveBiome.Moon) {
                drawArcadeSprite(
                    sprite,
                    if (biome == HillDriveBiome.Snow) palette.cloud else palette.grassLight,
                    (worldX - left).toFloat() - 6f,
                    y - sprite.height
                )
            } else {
                pixelRect(palette.bodyShade, (worldX - left).toFloat(), y - 2f, 7f, 3f)
            }
        }
        column++
    }

    var iceX = floor(left / 6.0).toLong() * 6.0
    while (iceX - left < width) {
        if (!run.isOnBridge(iceX) &&
            (run.biomeWeight(iceX, HillDriveBiome.Snow) > 0.2f || run.gripAt(iceX) < 0.75f)
        ) {
            val y = ground + run.terrainHeight(iceX).roundToInt()
            pixelRect(palette.cloud.copy(alpha = 0.85f), (iceX - left).toFloat(), y - 1f, 7f, 3f)
            pixelRect(
                palette.cloud.copy(alpha = 0.35f),
                (iceX - left).toFloat() + 2f,
                y + 3f,
                3f,
                2f
            )
        }
        iceX += 6.0
    }
    run.drawBridges(this, left, ground, palette)
    for (pickup in run.pickups) {
        val x = (pickup.x - left).toFloat()
        if (x < -20f || x > width + 20f) continue
        val y = ground + run.pickupHeight(pickup)
        if (pickup.fuel) {
            drawHillDriveSprite(
                HillDriveCan,
                palette.canColors,
                x - 11.7f,
                y - 14.4f,
                HillDrivePickupPixelSize
            )
        } else {
            val coin = HillDriveCoinSprites[pickup.value] ?: HillDriveCoinSprites.getValue(10)
            drawHillDriveSprite(
                coin,
                palette.coinColors,
                x - 11.7f,
                y - 11.7f,
                HillDrivePickupPixelSize
            )
        }
    }
    run.drawDust(this, left, ground, palette.dust)
    drawHillDriveCar(run, left, ground, palette)
    run.drawWater(this, left, ground, palette.water)
    run.drawCollections(this, left, ground, palette)
}

private fun DrawScope.drawHillDriveCar(
    run: HillDriveRun,
    left: Double,
    ground: Float,
    palette: HillDrivePalette
) {
    val car = Offset((run.x - left).toFloat(), ground + run.y)
    val facing = Offset(cos(run.angle), sin(run.angle))
    val down = Offset(-facing.y, facing.x)
    for (side in -1..1 step 2) {
        val wheel = if (side < 0) run.rear else run.front
        val axle = Offset((wheel.x - left).toFloat(), ground + wheel.y)
        val mount = car + facing * (side * HillDriveWheelbase) + down * HillDriveMountY
        drawLine(palette.ink, mount, axle, strokeWidth = 4f)
        drawLine(palette.metal, mount, axle, strokeWidth = 1.4f)
        val spring = axle - mount
        val length = sqrt(spring.x * spring.x + spring.y * spring.y).coerceAtLeast(1f)
        val perpendicular = Offset(-spring.y, spring.x) / length * 1.8f
        var previous = mount
        for (coil in 1..6) {
            val point =
                mount + spring * (coil / 7f) + perpendicular * if (coil % 2 == 0) -1f else 1f
            drawLine(palette.bodyShade, previous, point, strokeWidth = 1.6f)
            previous = point
        }
        drawLine(palette.bodyShade, previous, axle, strokeWidth = 1.6f)

        drawCircle(palette.tire, HillDriveWheelRadius, axle)
        drawCircle(palette.wheelColors[1], HillDriveWheelRadius - 1f, axle)
        drawCircle(palette.tire, HillDriveWheelRadius - 2.5f, axle)
        drawCircle(palette.wheelColors[3], 5f, axle)
        rotate(wheel.rotation * HillDriveDegrees, axle) {
            repeat(12) { tread ->
                val angle = tread * HillDriveTurn / 12f
                val direction = Offset(cos(angle), sin(angle))
                drawLine(
                    palette.wheelColors[2],
                    axle + direction * (HillDriveWheelRadius - 1.8f),
                    axle + direction * (HillDriveWheelRadius - 0.5f),
                    strokeWidth = 1f
                )
            }
            drawHillDriveSprite(
                HillDriveWheelSprite,
                palette.wheelColors,
                axle.x - HillDriveWheelRadius,
                axle.y - HillDriveWheelRadius,
                HillDriveWheelPixelSize
            )
        }
    }

    rotate(run.angle * HillDriveDegrees, car) {
        val x = car.x - 42f
        val y = car.y + HillDriveVisualAxleY - HillDriveBodyLift - 30f
        drawHillDriveSprite(HillDriveCar, palette.carColors, x, y, HillDriveCarPixelSize)
    }
}

private fun hillDrivePedalBounds(width: Float, height: Float, density: Float, gas: Boolean): Rect {
    val margin = 12f * density
    val heightScale = (height / (480f * density)).coerceIn(0.5f, 1f)
    val pedalWidth = (width * 0.29f).coerceAtMost(136f * density * heightScale)
    val pedalHeight = (height * 0.5f).coerceAtMost(114f * density * heightScale) *
            (width / (500f * density)).coerceIn(0.9f, 1f)
    val x = if (gas) width - margin - pedalWidth else margin
    return Rect(x, height - pedalHeight, x + pedalWidth, height)
}

private fun DrawScope.drawHillDrivePedal(
    bounds: Rect,
    gas: Boolean,
    pressed: Boolean,
    palette: HillDrivePalette
) {
    val sprite = if (gas) HillDriveGas else HillDriveBrake
    val unit =
        minOf(3.4f * density, bounds.height / 32f, bounds.width / sprite.values.first().width)
    val x = bounds.center.x - sprite.values.first().width * unit / 2f
    val y =
        bounds.bottom + 8.dp.toPx() - sprite.values.first().height * unit + if (pressed) 3.dp.toPx() else 0f
    drawHillDriveSprite(sprite, palette.pedalColors, x, y, unit)
}

private fun DrawScope.drawHillDriveHud(
    run: HillDriveRun,
    style: ArcadeStyle,
    palette: HillDrivePalette,
    measurer: TextMeasurer,
    fieldScale: Float
) {
    if (run.phase != ArcadePhase.Ready) {
        scale(density, Offset.Zero) {
            val color = if (run.fuel < 0.25f) style.hazardColor else style.accentColor
            val can = Offset(22f, 22f)
            drawArcadeSprite(HillDriveFuelIcon, color, can.x - 6f, can.y - 8f)
            pixelRect(
                color = style.detailColor.copy(alpha = 0.3f),
                x = 34f,
                y = can.y - 2f,
                width = 42f,
                height = 4f
            )
            pixelRect(
                color = color,
                x = 34f,
                y = can.y - 2f,
                width = 42f * run.fuel,
                height = 4f
            )
        }
    }

    if (run.phase != ArcadePhase.Running) return
    drawHillDrivePedal(
        bounds = hillDrivePedalBounds(size.width, size.height, density, gas = false),
        gas = false,
        pressed = run.brake,
        palette = palette
    )
    drawHillDrivePedal(
        bounds = hillDrivePedalBounds(size.width, size.height, density, gas = true),
        gas = true,
        pressed = run.gas,
        palette = palette
    )

    val bonus = run.trickBonus
    if (bonus > 0) {
        val text = measurer.measure("+$bonus", style.scoreTextStyle, maxLines = 1)
        val x =
            ((run.x - run.cameraX + run.cameraAnchorX).toFloat() * fieldScale - text.size.width / 2f)
                .coerceIn(
                    8.dp.toPx(),
                    (size.width - text.size.width - 8.dp.toPx()).coerceAtLeast(8.dp.toPx())
                )
        val y =
            ((run.cameraAnchorY - run.cameraY + run.y - 46f) * fieldScale).coerceAtLeast(39.dp.toPx())
        val pad = 4.dp.toPx()
        drawRoundRect(
            color = palette.hud.copy(alpha = 0.87f * run.bonusAlpha),
            topLeft = Offset(x - pad, y - pad / 2f),
            size = Size(text.size.width + pad * 2f, text.size.height + pad),
            cornerRadius = CornerRadius(pad)
        )
        drawText(
            text,
            color = style.accentColor.copy(alpha = run.bonusAlpha),
            topLeft = Offset(x, y)
        )
    }
}

private val HillDriveFuelIcon = ArcadeSprite(
    width = 12, height = 16,
    data = "M2 0H7V1H8V3H10V4H12V6H11V15H1V14H0V5H1V3H2Z M3 1V3H6V1Z M2 6V13H9V6Z " +
            "M3 7H4V8H5V9H6V8H7V7H8V9H7V10H8V12H7V11H6V10H5V11H4V12H3V10H4V9H3Z"
)

private val HillDriveCloud = ArcadeSprite(
    width = 68, height = 25,
    data = "M0 17H5V12H12V7H22V3H29V0H41V3H48V8H55V12H62V17H68V23H63V25H5V23H0Z"
)

private val HillDriveCloudSmall = ArcadeSprite(
    width = 47, height = 20,
    data = "M0 13H5V8H12V3H19V0H29V4H36V8H42V13H47V18H42V20H4V18H0Z"
)

private val HillDriveCactus = ArcadeSprite(
    12, 17,
    "M5,0h3v8h2v-4h2v7h-4v6h-3v-5h-5v-8h2v5h3z"
)

private val HillDriveGrass = ArcadeSprite(
    width = 13, height = 8,
    data = "M0 2H2V4H3V7H5V4H4V1H6V0H7V4H8V6H9V3H11V1H13V3H12V5H11V8H2V6H1V4H0Z"
)

private val HillDriveCar = mapOf(
    2 to ArcadeSprite(
        width = 80, height = 34,
        data = "M4 14H16V12H18V10H20V8H22V6H24V4H27V2H48V3H51V5H53V7H55V9H57V12H67V13H72V14H76V16H78V20H80V25H76V29H74V25H72V23H70V22H61V23H59V25H57V28H30V25H28V23H26V22H14V23H12V26H10V31H3V25H2V18H4Z M3 2H13V5H3Z"
    ),
    0 to ArcadeSprite(
        width = 80, height = 34,
        data = "M7 5H9V14H7Z M30 28H57V30H30Z M3 29H10V32H3Z M76 25H80V28H76Z"
    ),
    8 to ArcadeSprite(
        width = 80, height = 34,
        data = "M21 10H23V8H25V6H28V4H34V13H19V12H21Z M37 4H47V5H49V7H51V9H53V12H37Z"
    ),
    9 to ArcadeSprite(
        width = 80, height = 34,
        data = "M25 8H27V6H29V5H31V6H28V8H27V10H25Z M39 5H41V11H39Z"
    ),
    6 to ArcadeSprite(
        width = 80, height = 34,
        data = "M4 24H12V26H10V29H4Z M28 24H31V25H56V24H59V26H57V28H30V27H28Z M73 21H78V24H76V26H74V24H73Z"
    ),
    7 to ArcadeSprite(
        width = 80, height = 34,
        data = "M4 22H13V24H4Z M29 22H35V23H56V22H60V24H57V26H31V25H29Z M73 20H78V22H74V23H73Z"
    ),
    1 to ArcadeSprite(
        width = 80, height = 34,
        data = "M35 3H37V14H38V27H36V16H35Z M3 29H10V31H3Z M30 28H57V29H30Z M76 25H80V27H76Z M3 5H13V6H3Z"
    ),
    3 to ArcadeSprite(
        width = 80, height = 34,
        data = "M27 2H48V3H27Z M3 2H13V3H3Z M5 15H12V16H5Z M58 12H67V13H73V14H58Z"
    ),
    5 to ArcadeSprite(
        width = 80, height = 34,
        data = "M39 16H42V17H39Z M14 21H26V22H14Z M61 21H70V22H61Z"
    ),
    13 to ArcadeSprite(
        width = 80, height = 34,
        data = "M78 17H79V20H78Z"
    ),
    14 to ArcadeSprite(
        width = 80, height = 34,
        data = "M3 19H4V23H3Z"
    )
)

private val HillDriveWheelSprite = mapOf(
    3 to ArcadeSprite(
        width = 18, height = 18,
        data = "M7 5H11V6H12V7H13V11H12V12H11V13H7V12H6V11H5V7H6V6H7Z"
    ),
    4 to ArcadeSprite(
        width = 18, height = 18,
        data = "M7 5H11V6H8V7H7V10H6V11H5V7H6V6H7Z"
    ),
    5 to ArcadeSprite(
        width = 18, height = 18,
        data = "M8 7H10V8H11V10H10V11H8V10H7V8H8Z"
    ),
    6 to ArcadeSprite(
        width = 18, height = 18,
        data = "M8 6H10V8H12V10H10V12H8V10H6V8H8Z"
    )
)

private val HillDriveCan = mapOf(
    0 to ArcadeSprite(
        width = 26, height = 32,
        data = "M4 0H14V1H16V3H18V7H20V5H22V6H24V8H26V11H24V13H23V29H21V31H3V30H1V28H0V10H2V8H4Z M6 2V7H14V4H12V2Z"
    ),
    1 to ArcadeSprite(
        width = 26, height = 32,
        data = "M4 1H13V2H15V4H17V8H20V7H22V9H24V11H22V28H20V30H4V29H2V27H1V11H3V9H5V8H4Z M6 2V7H14V4H12V2Z"
    ),
    2 to ArcadeSprite(
        width = 26, height = 32,
        data = "M4 0H13V1H15V2H6V7H4Z M3 9H17V10H3V27H2V11H3Z M20 5H22V6H24V8H25V10H23V8H21V7H20Z"
    ),
    3 to ArcadeSprite(
        width = 26, height = 32,
        data = "M21 13H23V29H21V31H4V30H20V28H21Z"
    ),
    4 to ArcadeSprite(
        width = 26, height = 32,
        data = "M6 14H8V16H10V18H12V19H13V18H15V16H17V14H19V16H18V18H16V20H14V22H16V24H18V26H19V28H17V26H15V24H13V23H12V24H10V26H8V28H6V26H7V24H9V22H11V20H9V18H7V16H6Z"
    )
)

private val HillDriveCoin = mapOf(
    0 to ArcadeSprite(
        width = 26, height = 26,
        data = "M8 0H18V1H21V3H23V5H25V8H26V18H25V21H23V23H21V25H18V26H8V25H5V23H3V21H1V18H0V8H1V5H3V3H5V1H8Z"
    ),
    3 to ArcadeSprite(
        width = 26, height = 26,
        data = "M8 1H18V2H21V4H23V8H25V18H23V22H21V24H18V25H8V24H5V22H3V18H1V8H3V4H5V2H8Z"
    ),
    4 to ArcadeSprite(
        width = 26, height = 26,
        data = "M8 0H18V2H8V3H5V5H3V8H2V18H0V8H1V5H3V3H5V1H8Z"
    ),
    2 to ArcadeSprite(
        width = 26, height = 26,
        data = "M8 3H18V4H21V6H22V8H23V18H22V21H20V23H8V22H5V20H4V8H5V5H8Z"
    ),
    5 to ArcadeSprite(
        width = 26, height = 26,
        data = "M3 18H4V21H7V23H18V22H21V20H23V8H25V18H23V22H21V24H18V25H8V24H5V22H3Z"
    )
)

private val HillDriveCoinSprites = mapOf(
    5 to (HillDriveCoin + (6 to ArcadeSprite(
        width = 26, height = 26,
        data = "M10 8H16V10H12V12H16V18H10V16H14V14H10Z"
    ))),
    500 to (HillDriveCoin + (6 to ArcadeSprite(
        width = 26, height = 26,
        data = "M2 8H8V10H4V12H8V18H2V16H6V14H2Z M10 8H16V18H10Z M12 10V16H14V10Z M18 8H24V18H18Z M20 10V16H22V10Z"
    ))),
    100 to (HillDriveCoin + (6 to ArcadeSprite(
        width = 26, height = 26,
        data = "M2 10H4V8H6V16H8V18H2V16H4V12H2Z M10 8H16V18H10Z M12 10V16H14V10Z M18 8H24V18H18Z M20 10V16H22V10Z"
    ))),
    10 to (HillDriveCoin + (6 to ArcadeSprite(
        width = 26, height = 26,
        data = "M6 10H8V8H10V16H12V18H6V16H8V12H6Z M14 8H20V18H14Z M16 10V16H18V10Z"
    ))),
    25 to (HillDriveCoin + (6 to ArcadeSprite(
        width = 26, height = 26,
        data = "M6 8H12V14H8V16H12V18H6V12H10V10H6Z M14 8H20V10H16V12H20V18H14V16H18V14H14Z"
    )))
)

private val HillDriveBrake = mapOf(
    1 to ArcadeSprite(
        width = 25, height = 30,
        data = "M3 0H22V1H24V3H25V20H24V22H22V23H15V30H10V23H3V22H1V20H0V3H1V1H3Z"
    ),
    3 to ArcadeSprite(
        width = 25, height = 30,
        data = "M3 1H21V2H23V4H24V19H23V21H21V22H4V21H2V19H1V4H2V2H3Z"
    ),
    2 to ArcadeSprite(
        width = 25, height = 30,
        data = "M2 19H23V4H24V20H23V22H3V21H2Z M10 22H15V30H10Z"
    ),
    5 to ArcadeSprite(
        width = 25, height = 30,
        data = "M3 1H21V2H3V3H2V19H1V4H2V2H3Z M11 23H13V30H11Z"
    ),
    0 to ArcadeSprite(
        width = 25, height = 30,
        data = "M5 6H7V4H9V6H11V8H9V10H7V8H5Z M15 6H17V4H19V6H21V8H19V10H17V8H15Z M5 14H7V12H9V14H11V16H9V18H7V16H5Z M15 14H17V12H19V14H21V16H19V18H17V16H15Z"
    )
)

private val HillDriveGas = mapOf(
    1 to ArcadeSprite(
        width = 18, height = 32,
        data = "M3 0H15V1H17V3H18V22H17V24H12V32H6V24H1V22H0V3H1V1H3Z"
    ),
    3 to ArcadeSprite(
        width = 18, height = 32,
        data = "M3 1H15V2H16V3H17V21H16V23H2V21H1V3H2V2H3Z"
    ),
    2 to ArcadeSprite(
        width = 18, height = 32,
        data = "M1 21H16V3H17V22H16V24H12V32H6V24H2V23H1Z"
    ),
    5 to ArcadeSprite(
        width = 18, height = 32,
        data = "M3 1H15V2H3V3H2V21H1V3H2V2H3Z M7 24H9V32H7Z"
    ),
    0 to ArcadeSprite(
        width = 18, height = 32,
        data = "M3 5H15V7H3Z M3 10H15V12H3Z M3 15H15V17H3Z M3 20H15V22H3Z"
    )
)

private val HillDriveSoil = ArcadeSprite(
    width = 126, height = 36,
    data = "M0 0H2V10H0Z M0 14H2V20H0Z M2 0H4V10H2Z M2 14H4V20H2Z M4 0H6V12H4Z M6 0H8V8H6Z M8 0H10V8H8Z M8 14H10V16H8Z " +
            "M10 0H12V10H10Z M10 12H12V16H10Z M12 0H14V8H12Z M12 12H14V20H12Z M14 0H16V10H14Z M14 14H16V20H14Z " +
            "M16 0H18V10H16Z M18 0H20V12H18Z M18 14H20V16H18Z M20 0H22V12H20Z M20 14H22V16H20Z M22 0H24V12H22Z " +
            "M22 14H24V20H22Z M24 0H26V12H24Z M24 14H26V20H24Z M26 0H28V10H26Z M26 14H28V20H26Z M28 0H30V8H28Z " +
            "M28 12H30V20H28Z M28 28H30V34H28Z M30 0H32V10H30Z M30 12H32V20H30Z M30 28H32V34H30Z M32 0H34V10H32Z " +
            "M32 14H34V20H32Z M32 28H34V34H32Z M34 0H36V12H34Z M34 14H36V16H34Z M36 0H38V12H36Z M38 0H40V12H38Z " +
            "M40 0H42V12H40Z M42 0H44V12H42Z M44 0H46V10H44Z M44 16H46V22H44Z M46 0H48V8H46Z M46 16H48V22H46Z " +
            "M46 32H48V34H46Z M48 0H50V8H48Z M48 16H50V22H48Z M48 32H50V34H48Z M50 0H52V12H50Z M50 16H52V22H50Z " +
            "M50 32H52V34H50Z M52 0H54V12H52Z M52 16H54V22H52Z M54 0H56V10H54Z M56 0H58V10H56Z M56 14H58V16H56Z " +
            "M58 0H60V12H58Z M58 14H60V16H58Z M60 0H62V12H60Z M60 14H62V16H60Z M62 0H64V16H62Z M62 20H64V26H62Z " +
            "M64 0H66V14H64Z M64 20H66V26H64Z M64 34H66V36H64Z M66 0H68V14H66Z M66 20H68V26H66Z M68 0H70V12H68Z " +
            "M68 18H70V24H68Z M70 0H72V12H70Z M70 20H72V22H70Z M72 0H74V14H72Z M74 0H76V12H74Z M76 0H78V12H76Z " +
            "M78 0H80V12H78Z M78 20H80V24H78Z M80 0H82V8H80Z M80 18H82V24H80Z M82 0H84V12H82Z M82 18H84V22H82Z " +
            "M84 0H86V12H84Z M86 0H88V12H86Z M88 0H90V14H88Z M90 0H92V10H90Z M92 0H94V12H92Z M94 0H96V10H94Z M94 16H96V18H94Z " +
            "M96 0H98V10H96Z M96 14H98V20H96Z M98 0H100V12H98Z M98 14H100V16H98Z M100 0H102V12H100Z M102 0H104V10H102Z " +
            "M104 0H106V8H104Z M106 0H108V8H106Z M106 16H108V22H106Z M108 0H110V10H108Z M108 16H110V20H108Z " +
            "M110 0H112V10H110Z M110 14H112V20H110Z M112 0H114V8H112Z M112 12H114V18H112Z M114 0H116V10H114Z " +
            "M116 0H118V10H116Z M118 0H120V12H118Z M120 0H122V10H120Z M122 0H124V8H122Z M122 14H124V16H122Z " +
            "M124 0H126V10H124Z M124 14H126V18H124Z"
)

private const val HillDriveCarPixelSize = 1f
private const val HillDriveWheelPixelSize = 1f
private const val HillDrivePickupPixelSize = 0.9f
private const val HillDriveSectionLength = 600.0
private const val HillDriveMountainLength = 3000.0
private const val HillDriveJumpLength = 3000.0
private const val HillDriveBridgeInterval = 4800.0
private const val HillDriveBridgeApproach = 120f
private const val HillDriveBridgeNodeMass = 0.16f
private const val HillDriveReverseDistance = 500.0
private const val HillDrivePhysicsStep = 1f / 240f
private const val HillDriveWheelbase = 23f
private const val HillDriveWheelRadius = 9f
private const val HillDriveBodyY = -3f
private const val HillDriveBodyLift = 3f
private const val HillDriveMountY = HillDriveBodyY + 4f
private const val HillDriveRest = 15.5f
private const val HillDriveDroop = 21f
private const val HillDriveBumpStop = 5.5f
private const val HillDriveGravity = 300f
private const val HillDriveSpring = 85f
private const val HillDriveDamping = 7f
private const val HillDriveInertia = 780f
private const val HillDriveWheelMass = 0.18f
private const val HillDriveRotatingMass = 0.12f
private const val HillDriveVisualAxleY =
    HillDriveMountY + HillDriveRest - HillDriveGravity / (2f * HillDriveSpring)
private const val HillDriveTurn = 2f * PI.toFloat()
private const val HillDriveDegrees = 180f / PI.toFloat()

// Reference body coordinates are relative to the resting wheel axles.
private val HillDriveHull = arrayOf(
    Offset(-40f, -5f), Offset(-38f, -17f), Offset(-25f, -18f), Offset(-15f, -28f),
    Offset(6f, -28f), Offset(14f, -18f), Offset(30f, -16f), Offset(37f, -11f),
    Offset(38f, 0f), Offset(31f, -2f), Offset(17f, -4f), Offset(0f, -4f),
    Offset(-13f, -4f), Offset(-33f, -2f)
).map { it + Offset(0f, HillDriveVisualAxleY - HillDriveBodyLift - HillDriveBodyY) }

private val HillDriveStartKnots = floatArrayOf(0f, 65f, 185f, 300f, 425f, 535f, 600f)
private val HillDriveStartHeights = floatArrayOf(0f, 0f, 20f, 24f, -22f, -8f, 0f)
private val HillDriveHillKnots = floatArrayOf(0f, 87f, 190f, 293f, 402f, 509f, 600f)
private val HillDriveHills = arrayOf(
    floatArrayOf(0f, 0.30f, -0.51f, -0.82f, 0.38f, 0.20f, 0f),
    floatArrayOf(0f, -0.30f, 0.54f, 0.84f, -0.27f, -0.12f, 0f),
    floatArrayOf(0f, -0.63f, 0.25f, -0.42f, 0.50f, -0.16f, 0f),
    floatArrayOf(0f, 0.51f, 0.60f, -0.53f, -0.67f, 0.10f, 0f),
    floatArrayOf(0f, -0.24f, -0.79f, 0.32f, 0.54f, -0.34f, 0f)
)
private val HillDriveJumpKnots = arrayOf(
    floatArrayOf(0f, 180f, 390f, 520f, 780f, 1120f, 1550f, 2050f, 2550f, 3000f),
    floatArrayOf(0f, 200f, 380f, 540f, 830f, 1180f, 1600f, 2130f, 2650f, 3000f),
    floatArrayOf(0f, 160f, 360f, 500f, 760f, 1080f, 1520f, 2070f, 2600f, 3000f)
)
private val HillDriveJumpHeights = arrayOf(
    floatArrayOf(0f, -12f, -85f, -140f, 220f, 440f, 400f, 235f, 70f, 0f),
    floatArrayOf(0f, 8f, -65f, -150f, 250f, 460f, 380f, 205f, 55f, 0f),
    floatArrayOf(0f, -18f, -75f, -130f, 200f, 420f, 370f, 210f, 60f, 0f)
)

private class HillDriveSection(
    private val knots: FloatArray,
    heights: FloatArray,
    slopes: FloatArray
) {
    private val spans = Array(knots.lastIndex) { index ->
        val width = knots[index + 1] - knots[index]
        val rise = heights[index + 1] - heights[index]
        val start = slopes[index] * width
        val end = slopes[index + 1] * width
        Span(
            inverseWidth = 1f / width,
            height = heights[index],
            linear = start,
            quadratic = 3f * rise - 2f * start - end,
            cubic = start + end - 2f * rise
        )
    }

    private class Span(
        val inverseWidth: Float,
        val height: Float,
        val linear: Float,
        val quadratic: Float,
        val cubic: Float
    )

    // The result holds the height and derivative at the same point on the spline.
    fun sample(x: Float): Offset {
        var index = 0
        while (index < spans.lastIndex && x >= knots[index + 1]) index++
        val span = spans[index]
        val t = (x - knots[index]) * span.inverseWidth
        return Offset(
            x = span.height + t * (span.linear + t * (span.quadratic + t * span.cubic)),
            y = (span.linear + t * (2f * span.quadratic + t * 3f * span.cubic)) * span.inverseWidth
        )
    }
}

@Serializable
private class HillDriveBridgeState(
    val pointX: FloatArray,
    val pointY: FloatArray,
    val velocityX: FloatArray,
    val velocityY: FloatArray
)

private enum class HillDriveBridgeKind { Rope, Timber, Arch }

private class HillDriveBridge(
    val start: Double,
    val end: Double,
    from: Offset,
    to: Offset,
    before: Offset,
    after: Offset,
    val kind: HillDriveBridgeKind,
    slack: Double
) {
    private val length = (end - start).toFloat()
    val plankCount = (length / 12f).toInt()
    private val pointX = FloatArray(plankCount + 1)
    private val pointY = FloatArray(pointX.size)
    private val previousX = FloatArray(pointX.size)
    private val previousY = FloatArray(pointX.size)
    private val velocityX = FloatArray(pointX.size)
    private val velocityY = FloatArray(pointX.size)
    private val restLengths = FloatArray(plankCount)
    private val arcLengths = FloatArray(pointX.size)
    private val initialX: FloatArray
    private val initialY: FloatArray
    private val entrance: HillDriveSection
    private val exit: HillDriveSection

    init {
        val rise = (to.x - from.x).toDouble()
        val ropeLength = sqrt(length.toDouble() * length + rise * rise) * slack
        val horizontal = sqrt(ropeLength * ropeLength - rise * rise) / length
        var low = 0.001
        var high = 3.0
        repeat(32) {
            val middle = (low + high) / 2.0
            if (sinh(middle) / middle < horizontal) low = middle else high = middle
        }
        val a = length / (low + high)
        val center = length / 2.0 + a * asinh(rise / (2.0 * a * sinh(length / (2.0 * a))))
        val origin = sinh(-center / a)
        val bottom = from.x + a * cosh(center / a)
        for (index in pointX.indices) {
            val x = center + a * asinh(origin + ropeLength * index / plankCount / a)
            pointX[index] = x.toFloat()
            pointY[index] = (bottom - a * cosh((x - center) / a)).toFloat()
        }
        if (kind != HillDriveBridgeKind.Rope) {
            for (index in pointX.indices) {
                val t = index.toFloat() / plankCount
                pointX[index] = length * t
                pointY[index] = from.x + (to.x - from.x) * t -
                        if (kind == HillDriveBridgeKind.Arch) sin(t * PI.toFloat()) * length * 0.09f else 0f
            }
        }
        pointX[0] = 0f
        pointY[0] = from.x
        pointX[plankCount] = length
        pointY[plankCount] = to.x
        initialX = pointX.copyOf()
        initialY = pointY.copyOf()
        for (index in restLengths.indices) {
            restLengths[index] = (point(index + 1) - point(index)).getDistance()
            arcLengths[index + 1] = arcLengths[index] + restLengths[index]
        }
        val first = point(1) - point(0)
        val last = point(plankCount) - point(plankCount - 1)
        entrance = HillDriveSection(
            floatArrayOf(0f, HillDriveBridgeApproach),
            floatArrayOf(before.x, from.x),
            floatArrayOf(before.y, first.y / first.x)
        )
        exit = HillDriveSection(
            floatArrayOf(0f, HillDriveBridgeApproach),
            floatArrayOf(to.x, after.x),
            floatArrayOf(last.y / last.x, after.y)
        )
    }

    fun saveState() = HillDriveBridgeState(pointX, pointY, velocityX, velocityY)

    fun restore(state: HillDriveBridgeState) {
        if (kind != HillDriveBridgeKind.Rope) return
        if (state.pointX.size != pointX.size || state.pointY.size != pointY.size ||
            state.velocityX.size != velocityX.size || state.velocityY.size != velocityY.size
        ) return
        state.pointX.copyInto(pointX)
        state.pointY.copyInto(pointY)
        state.velocityX.copyInto(velocityX)
        state.velocityY.copyInto(velocityY)
    }

    fun advance(seconds: Float, gravity: Float) {
        if (kind != HillDriveBridgeKind.Rope) return
        val damping = exp(-seconds * 2.6f)
        for (index in 1 until plankCount) {
            previousX[index] = pointX[index]
            previousY[index] = pointY[index]
            velocityX[index] *= damping
            velocityY[index] = velocityY[index] * damping + gravity * seconds
            pointX[index] += velocityX[index] * seconds
            pointY[index] += velocityY[index] * seconds
        }
        repeat(20) { iteration ->
            for (step in restLengths.indices) {
                val index = if (iteration % 2 == 0) step else restLengths.lastIndex - step
                val dx = pointX[index + 1] - pointX[index]
                val dy = pointY[index + 1] - pointY[index]
                val distance = sqrt(dx * dx + dy * dy).coerceAtLeast(0.01f)
                val first = if (index == 0) 0f else 1f
                val second = if (index + 1 == plankCount) 0f else 1f
                val correction = (distance - restLengths[index]) / (distance * (first + second))
                pointX[index] += dx * correction * first
                pointY[index] += dy * correction * first
                pointX[index + 1] -= dx * correction * second
                pointY[index + 1] -= dy * correction * second
            }
            for (index in 1 until plankCount) {
                limitLength(index, pointX.first(), pointY.first(), arcLengths[index])
                limitLength(
                    index,
                    pointX.last(),
                    pointY.last(),
                    arcLengths.last() - arcLengths[index]
                )
            }
        }
        for (index in 1 until plankCount) {
            velocityX[index] = ((pointX[index] - previousX[index]) / seconds).coerceIn(-160f, 160f)
            velocityY[index] = ((pointY[index] - previousY[index]) / seconds).coerceIn(-160f, 160f)
        }
    }

    private fun limitLength(index: Int, x: Float, y: Float, maximum: Float) {
        val dx = pointX[index] - x
        val dy = pointY[index] - y
        val distance = sqrt(dx * dx + dy * dy)
        if (distance > maximum) {
            pointX[index] = x + dx * (maximum / distance)
            pointY[index] = y + dy * (maximum / distance)
        }
    }

    fun point(index: Int): Offset = Offset(pointX[index], pointY[index])

    fun segment(x: Double): Int = segment((x - start).toFloat(), pointX)

    private fun segment(x: Float, nodes: FloatArray): Int {
        var low = 0
        var high = plankCount - 1
        while (low < high) {
            val middle = (low + high) ushr 1
            if (x >= nodes[middle + 1]) low = middle + 1 else high = middle
        }
        return low
    }

    fun fraction(index: Int, x: Double): Float =
        (((x - start).toFloat() - pointX[index]) /
                (pointX[index + 1] - pointX[index]).coerceAtLeast(0.1f)).coerceIn(0f, 1f)

    fun sample(x: Double, terrain: Boolean = false): Offset {
        val local = (x - start).toFloat()
        val xs = if (terrain) initialX else pointX
        val ys = if (terrain) initialY else pointY
        val index = segment(local, xs)
        val dx = (xs[index + 1] - xs[index]).coerceAtLeast(0.1f)
        val dy = ys[index + 1] - ys[index]
        val t = ((local - xs[index]) / dx).coerceIn(0f, 1f)
        val height = ys[index] + dy * t
        return Offset(height, dy / dx)
    }

    fun inverseMass(index: Int, fraction: Float): Float {
        if (kind != HillDriveBridgeKind.Rope) return 0f
        val first = if (index == 0) 0f else (1f - fraction) * (1f - fraction)
        val second = if (index + 1 == plankCount) 0f else fraction * fraction
        return (first + second) / HillDriveBridgeNodeMass
    }

    fun surfaceVelocity(index: Int, fraction: Float): Offset = Offset(
        velocityX[index] * (1f - fraction) + velocityX[index + 1] * fraction,
        velocityY[index] * (1f - fraction) + velocityY[index + 1] * fraction
    )

    fun applyImpulse(index: Int, fraction: Float, impulse: Offset) {
        if (kind != HillDriveBridgeKind.Rope) return
        if (index > 0) {
            velocityX[index] += impulse.x * ((1f - fraction) / HillDriveBridgeNodeMass)
            velocityY[index] += impulse.y * ((1f - fraction) / HillDriveBridgeNodeMass)
        }
        if (index + 1 < plankCount) {
            velocityX[index + 1] += impulse.x * (fraction / HillDriveBridgeNodeMass)
            velocityY[index + 1] += impulse.y * (fraction / HillDriveBridgeNodeMass)
        }
    }

    fun approach(x: Double): Offset = if (x < start) {
        entrance.sample((x - start + HillDriveBridgeApproach).toFloat())
    } else {
        exit.sample((x - end).toFloat())
    }
}

@Serializable
private class HillDrivePickup(
    val x: Double,
    val y: Float,
    val fuel: Boolean,
    val value: Int = 10
)

@Serializable
private class HillDriveWheel {
    var x = 0.0
    var y = 0f
    var velocity: ArcadeOffset = Offset.Zero
    var rotation = 0f
    var surfaceSpeed = 0f
    var travel = HillDriveRest
    var grounded = false

    @Transient
    var normal = Offset(0f, -1f)

    @Transient
    var penetration = 0f

    @Transient
    var normalImpulse = 0f

    @Transient
    var tangentImpulse = 0f

    @Transient
    var bounceSpeed = 0f

    @Transient
    var bridge: HillDriveBridge? = null

    @Transient
    var bridgeSegment = 0

    @Transient
    var bridgeFraction = 0f
}

private class HillDriveDust(
    var x: Double,
    var y: Float,
    val drift: Float,
    var life: Float = 0.42f
)

private class HillDriveCollection(
    val x: Double,
    val y: Float,
    val fuel: Boolean,
    val value: Int,
    var age: Float = 0f
)

@Serializable
private class HillDriveRun : ArcadeRun() {
    var x = 0.0
        private set
    var y =
        -HillDriveMountY - HillDriveRest + HillDriveGravity / (2f * HillDriveSpring) - HillDriveWheelRadius
        private set
    var angle = 0f
        private set
    var cameraX = 0.0
        private set
    var cameraY = 0f
        private set
    var fuel = 1f
        private set
    var crashed = false
        private set
    private var stoppedTime = 0f

    @Transient
    private var bodyGrounded = false

    @Transient
    private val ponds = LinkedHashMap<Long, HillDrivePond>()
    var coins = 0
        private set
    var time = 0f
        private set

    val rear = HillDriveWheel()
    val front = HillDriveWheel()
    val pickups: List<HillDrivePickup>
        field = mutableListOf<HillDrivePickup>()

    private var velocity: ArcadeOffset = Offset.Zero
    private var spin = 0f
    private var throttle = 0f
    private var physicsRemainder = 0f
    private var backing = false
    private var wheelsInitialized = false
    private var rolloverTime = 0f
    private var farthestX = 0.0
    private var seed = 0
    private var nextSection = 0L
    private var airTime = 0f
    private var airRotation = 0f
    private var takeoffX = 0.0
    private var landingTime = 0f
    private var pendingBonus = 0
    private var coinPoints = -1L
    private var trickPoints = 0
    private var lastTrick = 0
    private var trickTime = 0f

    @Transient
    var gas = false

    @Transient
    var brake = false

    @Transient
    private val biomes = mutableListOf(HillDriveBiome.Meadow)

    @Transient
    private val biomeStarts = mutableListOf(0.0)

    @Transient
    private val sections = LinkedHashMap<Long, HillDriveSection>()

    @Transient
    private val mountains = LinkedHashMap<Long, HillDriveSection>()

    @Transient
    private val jumps = LinkedHashMap<Long, HillDriveSection>()

    private val bridgeStates = LinkedHashMap<Long, HillDriveBridgeState>()

    @Transient
    private val bridges = LinkedHashMap<Long, HillDriveBridge>()

    @Transient
    private val dust = ArrayList<HillDriveDust>(20)

    @Transient
    private val collections = ArrayList<HillDriveCollection>(24)

    @Transient
    private var dustTimer = 0f

    val collectedPoints get() = if (coinPoints < 0L) coins * 10L else coinPoints
    private val reverseLimit get() = farthestX - HillDriveReverseDistance
    val distance get() = (farthestX / 10.0).toInt()
    val trickBonus get() = if (trickTime > 0f) lastTrick else 0
    val bonusAlpha get() = (trickTime * 4f).coerceIn(0f, 1f)
    val cameraAnchorX get() = (width * 0.29f).coerceAtMost(270f)
    val cameraAnchorY get() = (height * 0.64f).coerceAtMost(height - 58f)

    override fun reset() {
        x = 0.0
        y =
            -HillDriveMountY - HillDriveRest + HillDriveGravity / (2f * HillDriveSpring) - HillDriveWheelRadius
        angle = 0f
        velocity = Offset.Zero
        spin = 0f
        throttle = 0f
        physicsRemainder = 0f
        backing = false
        rolloverTime = 0f
        farthestX = 0.0
        cameraX = 0.0
        cameraY = 0f
        fuel = 1f
        crashed = false
        stoppedTime = 0f
        bodyGrounded = false
        ponds.clear()
        coins = 0
        coinPoints = 0L
        time = 0f
        seed = Random.nextInt()
        biomes.clear()
        biomes += HillDriveBiome.Meadow
        biomeStarts.clear()
        biomeStarts += 0.0
        airTime = 0f
        airRotation = 0f
        takeoffX = 0.0
        landingTime = 0f
        pendingBonus = 0
        trickPoints = 0
        lastTrick = 0
        trickTime = 0f
        nextSection = 0
        sections.clear()
        mountains.clear()
        jumps.clear()
        bridges.clear()
        bridgeStates.clear()
        dust.clear()
        collections.clear()
        dustTimer = 0f
        pickups.clear()
        placeWheels()
        extendRoute()
    }

    private fun placeWheels() {
        val facing = Offset(cos(angle), sin(angle))
        val down = Offset(-facing.y, facing.x)
        val settled = HillDriveRest - HillDriveGravity / (2f * HillDriveSpring)
        for (side in -1..1 step 2) {
            val wheel = if (side < 0) rear else front
            val arm = facing * (side * HillDriveWheelbase) + down * (HillDriveMountY + settled)
            wheel.x = x + arm.x
            wheel.y = y + arm.y
            wheel.velocity = velocity + Offset(-arm.y, arm.x) * spin
            wheel.travel = settled
            wheel.grounded = false
            wheel.rotation = 0f
            wheel.surfaceSpeed = velocity.x
        }
        wheelsInitialized = true
    }

    override fun onRestore() {
        if (!wheelsInitialized) placeWheels()
        stopInput()
    }

    override fun onResize(previous: Size) {
        stopInput()
        extendRoute()
    }

    override fun stopInput() {
        gas = false
        brake = false
    }

    override fun advance(seconds: Float) {
        if (!wheelsInitialized) placeWheels()
        // A tiny remainder would amplify the suspension constraint bias.
        val elapsed = physicsRemainder.toDouble() + seconds
        val steps = (elapsed / HillDrivePhysicsStep).toInt()
        physicsRemainder = (elapsed - steps * HillDrivePhysicsStep.toDouble()).toFloat()
        repeat(steps) {
            if (!simulate(HillDrivePhysicsStep)) return
        }
        time += seconds
        trickTime = (trickTime - seconds).coerceAtLeast(0f)
        dustTimer -= seconds
        for (particle in dust) {
            particle.x += particle.drift * seconds
            particle.y -= 13f * seconds
            particle.life -= seconds
        }
        dust.removeAll { it.life <= 0f }
        for (collection in collections) collection.age += seconds
        collections.removeAll { it.age >= 0.65f }
        val targetX = x + velocity.x.coerceIn(-60f, 180f) * 0.25f
        cameraX += (targetX - cameraX) * (1f - exp(-seconds * 4.7f))
        val roadAhead = groundHeight(x + velocity.x.coerceIn(0f, 190f) * 0.19f)
        val targetY = roadAhead * 0.82f + (y + 21f) * 0.18f
        cameraY += (targetY - cameraY) * (1f - exp(-seconds * 3.1f))
        cameraY = cameraY.coerceIn(y - 40f, y + (cameraAnchorY - topInset - 38f).coerceAtLeast(28f))
        extendRoute()
        fuel = (fuel - seconds * (0.025f + throttle * 0.0125f)).coerceAtLeast(0f)
        score =
            (distance + collectedPoints + trickPoints).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val settling = crashed || fuel == 0f
        val still = velocity.getDistance() < 2f && abs(spin) < 0.08f &&
                rear.velocity.getDistance() < 3f && front.velocity.getDistance() < 3f &&
                (bodyGrounded || rear.grounded || front.grounded)
        stoppedTime = if (settling && still) stoppedTime + seconds else 0f
        if (stoppedTime > 0.7f) finish()
    }

    private fun simulate(step: Float): Boolean {
        advanceBridges(step)
        val facing = Offset(cos(angle), sin(angle))
        val down = Offset(-facing.y, facing.x)
        if (crashed) stopInput()
        val gravity = gravityAt(x)
        val water = waterLevel(x)
        val bodyImmersion = if (water == null) 0f else ((y + 25f - water) / 35f).coerceIn(0f, 1f)
        val immersion = maxOf(bodyImmersion, (wheelImmersion(rear) + wheelImmersion(front)) / 2f)
        val sand = biomeWeight(x, HillDriveBiome.Sand)
        val snow = biomeWeight(x, HillDriveBiome.Snow)
        val groundDrag = if (rear.grounded || front.grounded) sand * 0.32f else 0f
        val drag = 0.035f + immersion * (2.4f + abs(velocity.x) * 0.012f) +
                groundDrag + if (crashed && bodyGrounded) 0.9f else 0f
        throttle += ((if (gas && !brake && fuel > 0f && !crashed) 1f else 0f) - throttle) * (1f - exp(
            -step * 18f
        ))
        backing = brake && !gas
        velocity += Offset(
            -velocity.x * drag,
            gravity * (1f - immersion * 0.45f) - velocity.y * immersion
        ) * step
        val bodyVelocity = velocity
        val bodySpin = spin
        for (side in -1..1 step 2) {
            val wheel = if (side < 0) rear else front
            val arm = Offset((wheel.x - x).toFloat(), wheel.y - y)
            val travel = dot(arm, down) - HillDriveMountY
            val relative = wheel.velocity - bodyVelocity - Offset(-arm.y, arm.x) * bodySpin
            val spring = (
                    (travel - HillDriveRest) * HillDriveSpring + dot(
                        relative,
                        down
                    ) * HillDriveDamping
                    ).coerceIn(-2200f, 1600f)
            val springImpulse = down * (spring * step)
            velocity += springImpulse
            spin += cross(arm, springImpulse) / HillDriveInertia
            wheel.velocity += Offset(
                0f,
                gravity * step
            ) - springImpulse / HillDriveWheelMass
            val wheelWater = wheelImmersion(wheel)
            wheel.velocity *= exp(-wheelWater * step * 3.2f)

            val ratio = if (side < 0) 0.78f else 0.22f
            if (brake && !backing) {
                val braking = 850f * step
                val change = wheel.surfaceSpeed.coerceIn(-braking, braking)
                wheel.surfaceSpeed -= change
                if (wheel.grounded) {
                    spin += change * HillDriveRotatingMass * HillDriveWheelRadius * 0.6f / HillDriveInertia
                }
            } else {
                val pull = (300f + 90f * (1f - abs(wheel.surfaceSpeed) / 110f).coerceIn(0f, 1f)) *
                        (1f - sand * 0.2f)
                val motor = when {
                    backing && x <= reverseLimit + 0.5 -> 0f
                    backing -> -pull * (1f + wheel.surfaceSpeed / 210f).coerceIn(0f, 1.1f)
                    else -> throttle * pull * (1f - wheel.surfaceSpeed / 310f).coerceIn(0f, 1.1f)
                }
                val impulse = motor * (1f - wheelWater * 0.55f) * ratio * step
                wheel.surfaceSpeed += impulse / HillDriveRotatingMass
                if (wheel.grounded) spin -= impulse * HillDriveWheelRadius * 0.6f / HillDriveInertia
            }
            val rolling = (7f + sand * 23f - snow * 4f) * step
            wheel.surfaceSpeed -= wheel.surfaceSpeed.coerceIn(-rolling, rolling)
            wheel.surfaceSpeed *= exp(-step * 0.045f)
            prepareContact(wheel)
        }

        repeat(6) {
            solveWheelContact(rear, step)
            solveWheelContact(front, step)
            solveJoint(rear, -1f, facing, down, step)
            solveJoint(front, 1f, facing, down, step)
        }

        val airborne = !rear.grounded && !front.grounded
        if (airborne) {
            if (airTime > 0.15f) pendingBonus = 0
            val input = (if (brake) 1f else 0f) - (if (gas) 1f else 0f)
            val control = 6f + (airTime / 0.4f).coerceIn(0f, 1f) * 2.5f
            spin += input * control * sqrt(gravity / HillDriveGravity) * step
        }
        val maximumSpin = if (airborne) 6.5f else 5.5f
        spin = (spin * exp(-step * 0.12f)).coerceIn(-maximumSpin, maximumSpin)
        val turn = spin * step
        angle = (angle + turn) % HillDriveTurn
        x += velocity.x * step
        y += velocity.y * step
        rear.x += rear.velocity.x * step
        rear.y += rear.velocity.y * step
        front.x += front.velocity.x * step
        front.y += front.velocity.y * step

        repeat(3) {
            correctWheel(rear)
            correctWheel(front)
            correctJoint(rear, -1f)
            correctJoint(front, 1f)
        }
        if (resolveChassis(step)) {
            crashed = true
            stopInput()
        }
        if (crashed && bodyGrounded) spin *= exp(-step * 1.8f)
        if (!crashed) collectPickups(
            Offset(cos(angle), sin(angle)),
            Offset(-sin(angle), cos(angle))
        )

        if (airborne) {
            if (airTime == 0f) takeoffX = x
            airTime += step
            airRotation += turn
            landingTime = 0f
        } else {
            if (airTime > 0.35f && x - takeoffX > 45.0 && x > farthestX - 10.0) {
                val slope = sampleRoad(x).y
                val heading = atan2(slope, 1f) - (angle - airRotation)
                val landingTurn = atan2(sin(heading), cos(heading))
                val flips = abs(((airRotation - landingTurn) / HillDriveTurn).roundToInt())
                val jump = ((airTime - 0.2f) * 8f).toInt() * 5
                pendingBonus = maxOf(pendingBonus, jump + flips * 250)
            }
            airTime = 0f
            airRotation = 0f
            val stable = rear.grounded && front.grounded && abs(spin) < 2f &&
                    dot(Offset(sin(angle), -cos(angle)), roadNormal(sampleRoad(x).y)) > 0.8f
            landingTime = if (pendingBonus > 0 && stable) landingTime + step else 0f
            if (landingTime >= 0.12f) {
                lastTrick = pendingBonus
                trickPoints += lastTrick
                trickTime = 1.45f
                pendingBonus = 0
                landingTime = 0f
            }
        }
        for (side in -1..1 step 2) {
            val wheel = if (side < 0) rear else front
            wheel.rotation =
                (wheel.rotation + wheel.surfaceSpeed / HillDriveWheelRadius * step) % HillDriveTurn
            val arm = Offset((wheel.x - x).toFloat(), wheel.y - y)
            wheel.travel = dot(arm, Offset(-sin(angle), cos(angle))) - HillDriveMountY
        }
        if (rear.grounded && abs(rear.velocity.x) > 52f && dustTimer <= 0f) {
            if (dust.size >= 20) dust.removeAt(0)
            dust += HillDriveDust(
                rear.x,
                rear.y + HillDriveWheelRadius - 1f,
                -rear.velocity.x * 0.12f
            )
            dustTimer = 0.065f
        }
        farthestX = maxOf(farthestX, x)
        if (x < reverseLimit) {
            val correction = reverseLimit - x
            x += correction
            rear.x += correction
            front.x += correction
            velocity = Offset(velocity.x.coerceAtLeast(0f), velocity.y)
            rear.velocity = Offset(rear.velocity.x.coerceAtLeast(0f), rear.velocity.y)
            front.velocity = Offset(front.velocity.x.coerceAtLeast(0f), front.velocity.y)
            rear.surfaceSpeed = rear.surfaceSpeed.coerceAtLeast(0f)
            front.surfaceSpeed = front.surfaceSpeed.coerceAtLeast(0f)
        }
        return true
    }

    private fun solveJoint(
        wheel: HillDriveWheel,
        side: Float,
        facing: Offset,
        down: Offset,
        step: Float
    ) {
        val arm = Offset((wheel.x - x).toFloat(), wheel.y - y)
        val relative = wheel.velocity - velocity - Offset(-arm.y, arm.x) * spin
        val error = dot(arm, facing) - side * HillDriveWheelbase
        val lever = cross(arm, facing)
        val mass = 1f + 1f / HillDriveWheelMass + lever * lever / HillDriveInertia
        val impulse = facing * (-(dot(relative, facing) + error * 0.16f / step) / mass)
        applyJointImpulse(wheel, arm, impulse)

        val length = dot(arm, down) - HillDriveMountY
        val limit = when {
            length < HillDriveBumpStop -> length - HillDriveBumpStop
            length > HillDriveDroop -> length - HillDriveDroop
            else -> return
        }
        val speed = dot(wheel.velocity - velocity - Offset(-arm.y, arm.x) * spin, down)
        val axialLever = cross(arm, down)
        val axialMass = 1f + 1f / HillDriveWheelMass + axialLever * axialLever / HillDriveInertia
        var magnitude = -(speed + limit * 0.18f / step) / axialMass
        magnitude = if (limit < 0f) magnitude.coerceAtLeast(0f) else magnitude.coerceAtMost(0f)
        applyJointImpulse(wheel, arm, down * magnitude)
    }

    private fun applyJointImpulse(wheel: HillDriveWheel, arm: Offset, impulse: Offset) {
        wheel.velocity += impulse / HillDriveWheelMass
        velocity -= impulse
        spin -= cross(arm, impulse) / HillDriveInertia
    }

    private fun prepareContact(wheel: HillDriveWheel) {
        val sample = sampleRoad(wheel.x)
        var normal = roadNormal(sample.y)
        val pointX = wheel.x - normal.x * HillDriveWheelRadius
        val ground = sampleRoad(pointX)
        normal = roadNormal(ground.y)
        val clearance = ((wheel.x - pointX).toFloat() * normal.x + (wheel.y - ground.x) * normal.y)
        wheel.normal = normal
        wheel.penetration = HillDriveWheelRadius - clearance
        wheel.normalImpulse = 0f
        wheel.tangentImpulse = 0f
        wheel.grounded = false
        wheel.bridge = bridgeAt(pointX)
        wheel.bridge?.let { bridge ->
            wheel.bridgeSegment = bridge.segment(pointX)
            wheel.bridgeFraction = bridge.fraction(wheel.bridgeSegment, pointX)
        }
        val approach = dot(wheel.velocity - bridgeVelocity(wheel), normal)
        wheel.bounceSpeed = if (approach < -35f) -approach * 0.08f else 0f
    }

    private fun bridgeVelocity(wheel: HillDriveWheel): Offset =
        wheel.bridge?.surfaceVelocity(wheel.bridgeSegment, wheel.bridgeFraction) ?: Offset.Zero

    private fun biomeIndex(position: Double): Int {
        val x = position.coerceAtLeast(0.0)
        while (true) {
            val lengthVariant = hillDriveHash(biomes.lastIndex.toLong(), seed xor 8837)
            val length = HillDriveSectionLength * (5 + (lengthVariant ushr 1) % 11)
            val end = biomeStarts.last() + length
            if (x < end) break
            val previous = biomes.last().ordinal
            val variant = hillDriveHash(biomes.size.toLong(), seed xor 4591)
            val next = (variant and Int.MAX_VALUE) % (HillDriveBiome.entries.size - 1)
            biomes += HillDriveBiome.entries[if (next >= previous) next + 1 else next]
            biomeStarts += end
        }
        val index = biomeStarts.binarySearch(x)
        return if (index >= 0) index else -index - 2
    }

    fun biomeAt(position: Double): HillDriveBiome = biomes[biomeIndex(position)]

    fun biomeTransition(position: Double): Float {
        val local = (position.coerceAtLeast(0.0) - biomeStarts[biomeIndex(position)]).toFloat()
        val t = (local / 450f).coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }

    fun biomeWeight(position: Double, biome: HillDriveBiome): Float {
        val before = if (biomeAt(position - 450.0) == biome) 1f else 0f
        val after = if (biomeAt(position) == biome) 1f else 0f
        return before + (after - before) * biomeTransition(position)
    }

    private fun gravityAt(position: Double) =
        HillDriveGravity * (1f - biomeWeight(position, HillDriveBiome.Moon) * 0.75f)

    private fun pond(index: Long): HillDrivePond = ponds.getOrPut(index) {
        val start = index * HillDriveSectionLength
        val heights = FloatArray(31) { groundHeight(start + it * 20.0) }
        var result = HillDrivePond(0.0, 0.0, 0f)
        var deepest = 0f
        for (peak in 1 until heights.lastIndex) {
            if (heights[peak] <= heights[peak - 1] || heights[peak] <= heights[peak + 1]) continue
            if (isOnBridge(start + peak * 20.0)) continue
            val banks = maxOf(
                (0 until peak).minOf { heights[it] },
                (peak + 1..heights.lastIndex).minOf { heights[it] }
            )
            val level = maxOf(banks + 3f, heights[peak] - 18f)
            val depth = heights[peak] - level
            if (depth < 8f || depth <= deepest) continue
            var left = peak
            var right = peak
            while (left > 0 && heights[left] > level) left--
            while (right < heights.lastIndex && heights[right] > level) right++
            val from = left + (level - heights[left]) / (heights[left + 1] - heights[left])
            val to = right - (level - heights[right]) / (heights[right - 1] - heights[right])
            result = HillDrivePond(start + from * 20.0, start + to * 20.0, level)
            deepest = depth
        }
        if (ponds.size > 12) ponds.remove(ponds.keys.first())
        result
    }

    private fun waterLevel(position: Double): Float? {
        if (biomeWeight(position, HillDriveBiome.Water) < 0.5f || isOnBridge(position)) return null
        val pond = pond((position / HillDriveSectionLength).toLong())
        return pond.level.takeIf { position > pond.start && position < pond.end }
    }

    private fun wheelImmersion(wheel: HillDriveWheel): Float {
        val level = waterLevel(wheel.x) ?: return 0f
        return ((wheel.y + HillDriveWheelRadius - level) / (2f * HillDriveWheelRadius))
            .coerceIn(0f, 1f)
    }

    fun drawWater(scope: DrawScope, left: Double, ground: Float, color: Color) = with(scope) {
        var at = floor(left / 4.0).toLong() * 4.0
        while (at < left + width) {
            val level = waterLevel(at)
            if (level != null) {
                val y = ground + level
                val bottom = ground + groundHeight(at)
                val x = (at - left).toFloat()
                pixelRect(color.copy(alpha = 0.28f), x, y, 4.5f, (bottom - y).coerceAtLeast(0f))
                pixelRect(color.copy(alpha = 0.7f), x, y, 4.5f, 1.5f)
                if ((at.toLong() / 4) % 7L == 0L) {
                    pixelRect(color.copy(alpha = 0.35f), x, y + 4f, 7f, 1f)
                }
            }
            at += 4.0
        }
    }

    fun gripAt(position: Double): Float {
        val snow = biomeWeight(position, HillDriveBiome.Snow)
        val water = biomeWeight(position, HillDriveBiome.Water)
        val moon = biomeWeight(position, HillDriveBiome.Moon)
        val sand = biomeWeight(position, HillDriveBiome.Sand)
        val surfaceGrip = 1f - snow * 0.62f - water * 0.28f - moon * 0.18f - sand * 0.18f
        val section = floor(position / 720.0).toLong()
        val local = position - section * 720.0
        return if (biomeWeight(position, HillDriveBiome.Meadow) > 0.5f &&
            section >= 3 && section % 4L == 3L && local in 180.0..430.0 &&
            !isOnBridge(position) && abs(sampleRoad(position).y) < 0.22f
        ) 0.68f * surfaceGrip else surfaceGrip
    }

    private fun solveWheelContact(wheel: HillDriveWheel, step: Float) {
        if (wheel.penetration < -0.35f) return
        val normal = wheel.normal
        val bridge = wheel.bridge
        val bridgeMass = bridge?.inverseMass(wheel.bridgeSegment, wheel.bridgeFraction) ?: 0f
        val speed = dot(wheel.velocity - bridgeVelocity(wheel), normal)
        val bias = (wheel.penetration - 0.15f).coerceAtLeast(0f) * 0.15f / step
        val impulse = (bias.coerceAtMost(60f) + wheel.bounceSpeed - speed) /
                (1f / HillDriveWheelMass + bridgeMass)
        val accumulated = (wheel.normalImpulse + impulse).coerceAtLeast(0f)
        val change = accumulated - wheel.normalImpulse
        wheel.normalImpulse = accumulated
        wheel.velocity += normal * (change / HillDriveWheelMass)
        bridge?.applyImpulse(wheel.bridgeSegment, wheel.bridgeFraction, normal * -change)

        val tangent = Offset(-normal.y, normal.x)
        val slip = dot(wheel.velocity - bridgeVelocity(wheel), tangent) - wheel.surfaceSpeed
        val grip = accumulated * 1.35f * gripAt(wheel.x)
        val tangentImpulse = -slip /
                (1f / HillDriveWheelMass + 1f / HillDriveRotatingMass + bridgeMass)
        val traction = (wheel.tangentImpulse + tangentImpulse).coerceIn(-grip, grip)
        val applied = traction - wheel.tangentImpulse
        wheel.tangentImpulse = traction
        wheel.velocity += tangent * (applied / HillDriveWheelMass)
        wheel.surfaceSpeed -= applied / HillDriveRotatingMass
        bridge?.applyImpulse(wheel.bridgeSegment, wheel.bridgeFraction, tangent * -applied)
        wheel.grounded = accumulated > 0.015f
    }

    private fun correctWheel(wheel: HillDriveWheel) {
        val sample = sampleRoad(wheel.x)
        var normal = roadNormal(sample.y)
        val pointX = wheel.x - normal.x * HillDriveWheelRadius
        val ground = sampleRoad(pointX)
        normal = roadNormal(ground.y)
        val clearance = ((wheel.x - pointX).toFloat() * normal.x + (wheel.y - ground.x) * normal.y)
        val depth = (HillDriveWheelRadius - clearance - 0.10f).coerceAtLeast(0f)
        if (depth <= 0f) return
        val correction = depth.coerceAtMost(8f) * 0.70f
        wheel.x += normal.x * correction
        wheel.y += normal.y * correction
    }

    private fun correctJoint(wheel: HillDriveWheel, side: Float) {
        val facing = Offset(cos(angle), sin(angle))
        val down = Offset(-facing.y, facing.x)
        val arm = Offset((wheel.x - x).toFloat(), wheel.y - y)
        val lateral = dot(arm, facing) - side * HillDriveWheelbase
        correctJointAxis(wheel, arm, facing, lateral)
        val travel = dot(arm, down) - HillDriveMountY
        val error = travel - travel.coerceIn(HillDriveBumpStop, HillDriveDroop)
        if (error != 0f) correctJointAxis(wheel, arm, down, error)
    }

    private fun correctJointAxis(wheel: HillDriveWheel, arm: Offset, axis: Offset, error: Float) {
        val lever = cross(arm, axis)
        val mass = 1f + 1f / HillDriveWheelMass + lever * lever / HillDriveInertia
        val impulse = axis * (error.coerceIn(-9f, 9f) * 0.58f / mass)
        wheel.x -= impulse.x / HillDriveWheelMass
        wheel.y -= impulse.y / HillDriveWheelMass
        x += impulse.x
        y += impulse.y
        angle += cross(arm, impulse) / HillDriveInertia
    }

    private fun resolveChassis(step: Float): Boolean {
        var roofTouch = false
        var fatal = false
        bodyGrounded = false
        repeat(3) {
            val facing = Offset(cos(angle), sin(angle))
            val down = Offset(-facing.y, facing.x)
            var penetration = 0f
            var contact = Offset.Zero
            var normal = Offset.Zero
            for (vertex in HillDriveHull) {
                val arm = facing * vertex.x + down * (vertex.y + HillDriveBodyY)
                val road = sampleRoad(x + arm.x)
                val depth = y + arm.y - road.x
                if (depth <= 0f) continue
                bodyGrounded = true
                val surfaceNormal = roadNormal(road.y)
                val speed = dot(velocity + Offset(-arm.y, arm.x) * spin, surfaceNormal)
                val alignment = dot(down, surfaceNormal)
                if (alignment > -0.2f && depth > 0.05f) roofTouch = true
                if ((vertex.y < -9f && alignment > -0.34f || alignment > 0.55f) &&
                    (depth > 0.8f || speed < -27f)
                ) fatal = true
                val overlap = depth * -surfaceNormal.y
                if (overlap > penetration) {
                    penetration = overlap
                    contact = arm
                    normal = surfaceNormal
                }
            }
            if (penetration <= 0f) return@repeat

            val lever = cross(contact, normal)
            val inverseMass = 1f + lever * lever / HillDriveInertia
            val correction = (penetration * 0.72f).coerceAtMost(6f) / inverseMass
            x += normal.x * correction
            y += normal.y * correction
            angle += lever * correction / HillDriveInertia

            val contactX = x + contact.x
            val bridge = bridgeAt(contactX)
            val segment = bridge?.segment(contactX) ?: 0
            val fraction = bridge?.fraction(segment, contactX) ?: 0f
            val surface = bridge?.surfaceVelocity(segment, fraction) ?: Offset.Zero
            val bridgeMass = bridge?.inverseMass(segment, fraction) ?: 0f
            val relative = velocity + Offset(-contact.y, contact.x) * spin - surface
            val impactSpeed = dot(relative, normal)
            val impulse = (-impactSpeed * 1.04f / (inverseMass + bridgeMass)).coerceAtLeast(0f)
            val tangent = Offset(-normal.y, normal.x)
            val tangentSpeed = dot(relative, tangent)
            val friction = (-tangentSpeed * 0.22f).coerceIn(-0.5f * impulse, 0.5f * impulse)
            val change = normal * impulse + tangent * friction
            velocity += change
            spin += cross(contact, change) / HillDriveInertia
            bridge?.applyImpulse(segment, fraction, -change)
        }
        rolloverTime = if (roofTouch) rolloverTime + step else 0f
        return fatal || rolloverTime > 0.26f
    }

    fun drawDust(scope: DrawScope, left: Double, ground: Float, color: Color) = with(scope) {
        for (particle in dust) {
            val radius = (1f + (0.42f - particle.life) * 3f).roundToInt().toFloat()
            pixelRect(
                color = color.copy(alpha = (particle.life * 2f).coerceIn(0f, 1f)),
                x = (particle.x - left).toFloat().roundToInt() - radius,
                y = (ground + particle.y).roundToInt() - radius,
                width = radius * 2f,
                height = radius * 2f
            )
        }
    }

    fun drawCollections(
        scope: DrawScope,
        left: Double,
        ground: Float,
        palette: HillDrivePalette
    ) = with(scope) {
        for (collection in collections) {
            val progress = collection.age / 0.65f
            val alpha = (1f - progress).coerceIn(0f, 1f)
            val center = Offset((collection.x - left).toFloat(), ground + collection.y)
            val color = if (collection.fuel) palette.can else palette.gold
            repeat(8) { index ->
                val angle = index * HillDriveTurn / 8f
                val point = center + Offset(cos(angle), sin(angle)) * (5f + progress * 23f)
                val radius = if (index % 2 == 0) 1.5f else 1f
                pixelRect(
                    color.copy(alpha = alpha),
                    point.x.roundToInt() - radius,
                    point.y.roundToInt() - radius - progress * 8f,
                    radius * 2f,
                    radius * 2f
                )
            }
            if (!collection.fuel) {
                val sprite = HillDriveCoinSprites.getValue(collection.value).getValue(6)
                drawArcadeSprite(
                    sprite,
                    palette.gold.copy(alpha = alpha),
                    center.x - 13f,
                    center.y - 22f - progress * 24f
                )
            }
        }
    }

    private fun advanceBridges(seconds: Float) {
        val margin = maxOf(width, 240f)
        for (bridge in bridges.values) {
            if (x in (bridge.start - margin)..(bridge.end + margin)) {
                bridge.advance(seconds, gravityAt((bridge.start + bridge.end) / 2.0))
            }
        }
    }

    fun drawBridges(
        scope: DrawScope,
        left: Double,
        ground: Float,
        palette: HillDrivePalette
    ) = with(scope) {
        val first = floor(left / HillDriveBridgeInterval).toLong().coerceAtLeast(0L)
        val last = ((left + width) / HillDriveBridgeInterval).toLong()
        for (index in first..last) {
            val bridge = bridge(index)
            val start = bridge.start
            val end = bridge.end
            if (end < left || start > left + width) continue
            val firstPlank = bridge.segment(maxOf(left - 16.0, start))
            val lastPlank = bridge.segment(minOf(left + width + 16.0, end))
            for (plank in firstPlank..lastPlank) {
                val from = bridge.point(plank) + Offset((start - left).toFloat(), ground)
                val to = bridge.point(plank + 1) + Offset((start - left).toFloat(), ground)
                val delta = to - from
                val center = (from + to) / 2f
                val length = delta.getDistance()
                rotate(atan2(delta.y, delta.x) * HillDriveDegrees, center) {
                    pixelRect(palette.ink, center.x - length / 2f, center.y, length, 5f)
                    pixelRect(
                        palette.bridge,
                        center.x - length / 2f + 0.5f,
                        center.y,
                        length - 1f,
                        3f
                    )
                    pixelRect(
                        palette.bridgeLight,
                        center.x - length / 2f + 0.5f,
                        center.y,
                        length - 1f,
                        1f
                    )
                    pixelRect(palette.metal, center.x - 1f, center.y + 1f, 1f, 1f)
                }
                val railHeight = if (bridge.kind == HillDriveBridgeKind.Rope) -18f else -12f
                drawLine(
                    palette.bridge,
                    from + Offset(0f, railHeight),
                    to + Offset(0f, railHeight),
                    strokeWidth = 1.5f
                )
                if (plank % 4 == 0) {
                    drawLine(palette.bridge, from, from + Offset(0f, railHeight), 2f)
                }
            }
            for (edge in 0..1) {
                val x = if (edge == 0) start else end
                val y = ground + groundHeight(x)
                pixelRect(palette.bridge, (x - left).toFloat() - 2f, y - 5f, 4f, 22f)
                pixelRect(palette.bridgeLight, (x - left).toFloat() - 2f, y - 5f, 1f, 22f)
            }
        }
    }

    fun groundHeight(x: Double): Float = sampleRoad(x).x

    fun terrainHeight(x: Double): Float = sampleRoad(x, terrain = true).x

    fun pickupHeight(pickup: HillDrivePickup): Float {
        val bridge = bridgeAt(pickup.x) ?: return pickup.y
        return pickup.y + bridge.sample(pickup.x).x - bridge.sample(pickup.x, terrain = true).x
    }

    fun isOnBridge(x: Double): Boolean {
        return bridgeAt(x) != null
    }

    private fun bridgeAt(x: Double): HillDriveBridge? {
        if (x < 0.0) return null
        val bridge = bridge((x / HillDriveBridgeInterval).toLong())
        return bridge.takeIf { x in it.start..it.end }
    }

    private fun bridge(index: Long): HillDriveBridge {
        val bridge = bridges.getOrPut(index) {
            val variant = hillDriveHash(index, seed xor 2713)
            val difficulty = (index / 6f).coerceAtMost(1f)
            val kind =
                HillDriveBridgeKind.entries[(variant ushr 3 and 3) % HillDriveBridgeKind.entries.size]
            val length = when (variant and 3) {
                0 -> 240.0
                1 -> 420.0
                2 -> 680.0
                else -> 960.0
            } + difficulty * 180.0
            val base = index * HillDriveBridgeInterval
            var start = base + 1980.0
            var best = Float.MAX_VALUE
            // Find banks that suit a bridge instead of stretching a rope across
            // whichever two slopes happen to be under a fixed interval.
            for (candidate in 0..20) {
                val x = base + 1380.0 + ((candidate + (variant ushr 4 and 15)) % 21) * 75.0
                val from = sampleGround(x)
                val to = sampleGround(x + length)
                val incline = abs(to.x - from.x) / length.toFloat()
                val cost = incline * 3f + abs(from.y) + abs(to.y)
                if (cost < best) {
                    best = cost
                    start = x
                }
            }
            val end = start + length
            HillDriveBridge(
                start = start,
                end = end,
                from = sampleGround(start),
                to = sampleGround(end),
                before = sampleGround(start - HillDriveBridgeApproach),
                after = sampleGround(end + HillDriveBridgeApproach),
                kind = kind,
                slack = 1.008 + (variant ushr 6 and 3) * 0.012
            ).apply {
                bridgeStates[index]?.let(::restore)
                bridgeStates[index] = saveState()
            }
        }

        if (bridges.size > 8) {
            val oldest = bridges.keys.first()
            bridges.remove(oldest)
            bridgeStates.remove(oldest)
        }
        return bridge
    }

    private fun section(index: Long): HillDriveSection {
        val section = sections.getOrPut(index) { makeSection(index) }
        if (sections.size > 16) sections.remove(sections.keys.first())
        return section
    }

    private fun sampleRoad(x: Double, terrain: Boolean = false): Offset {
        if (x < 0.0) return Offset.Zero
        val bridge = bridge((x / HillDriveBridgeInterval).toLong())
        return when (x) {
            in bridge.start..bridge.end -> bridge.sample(x, terrain)
            in (bridge.start - HillDriveBridgeApproach)..bridge.start,
            in bridge.end..(bridge.end + HillDriveBridgeApproach) -> bridge.approach(x)

            else -> sampleGround(x)
        }
    }

    private fun sampleGround(x: Double): Offset {
        if (x <= 0.0) return Offset.Zero
        val jumpIndex = (x / HillDriveJumpLength).toLong()
        val jump = jump(jumpIndex)
        val shape = if (jump != null) {
            jump.sample((x - jumpIndex * HillDriveJumpLength).toFloat())
        } else {
            val index = (x / HillDriveSectionLength).toLong()
            section(index).sample((x - index * HillDriveSectionLength).toFloat())
        }
        return shape + sampleMountain(x)
    }

    private fun jump(index: Long): HillDriveSection? {
        val group = index / 4L
        val slot = 1L + (hillDriveHash(group, seed xor 3571) ushr 3 and 1)
        if (index % 4L != slot) return null
        val variant = hillDriveHash(index, seed xor 1357)
        val jump = jumps.getOrPut(index) {
            val random = Random(variant)
            val profile = (variant and Int.MAX_VALUE) % HillDriveJumpKnots.size
            val knots = HillDriveJumpKnots[profile]
            val scale = 0.85f + random.nextFloat() * 0.25f
            val heights = FloatArray(knots.size) { point ->
                HillDriveJumpHeights[profile][point] * scale +
                        if (point in 5 until knots.lastIndex) random.nextFloat() * 36f - 18f else 0f
            }
            val slopes = FloatArray(knots.size)
            for (point in 1 until knots.lastIndex) {
                val left = (heights[point] - heights[point - 1]) / (knots[point] - knots[point - 1])
                val right =
                    (heights[point + 1] - heights[point]) / (knots[point + 1] - knots[point])
                if (left * right > 0f) slopes[point] = 2f * left * right / (left + right)
            }
            slopes[3] = when (profile) {
                0 -> -0.6f
                1 -> -0.7f
                else -> -0.5f
            }
            HillDriveSection(knots, heights, slopes)
        }
        if (jumps.size > 8) jumps.remove(jumps.keys.first())
        return jump
    }

    private fun sampleMountain(x: Double): Offset {
        val index = (x / HillDriveMountainLength).toLong()
        val mountain = mountains.getOrPut(index) {
            fun slope(point: Long): Float {
                val left = mountainHeight(point) - mountainHeight(point - 1)
                val right = mountainHeight(point + 1) - mountainHeight(point)
                return if (left * right > 0f) {
                    (2f * left * right / (left + right) / HillDriveMountainLength).toFloat()
                } else 0f
            }
            HillDriveSection(
                floatArrayOf(0f, HillDriveMountainLength.toFloat()),
                floatArrayOf(mountainHeight(index), mountainHeight(index + 1)),
                floatArrayOf(slope(index), slope(index + 1))
            )
        }
        if (mountains.size > 8) mountains.remove(mountains.keys.first())
        return mountain.sample((x - index * HillDriveMountainLength).toFloat())
    }

    private fun mountainHeight(index: Long): Float {
        if (index <= 0L) return 0f
        val variant = hillDriveHash(index, seed xor 6949)
        val amplitude = (65f + (index / 6f).coerceAtMost(1f) * 625f) *
                (0.85f + (variant and 255) / 850f)
        return amplitude * when (index % 4L) {
            1L -> -1f
            2L -> -0.28f
            3L -> 0.35f
            else -> -0.12f
        }
    }

    private fun makeSection(index: Long): HillDriveSection {
        val difficulty = (index / 30f).coerceAtMost(1f)
        val variant = hillDriveHash(index, seed)
        val random = Random(variant)
        val knots = if (index == 0L) HillDriveStartKnots else {
            FloatArray(HillDriveHillKnots.size) { point ->
                HillDriveHillKnots[point] +
                        if (point in 1 until HillDriveHillKnots.lastIndex) random.nextFloat() * 20f - 10f else 0f
            }
        }
        val biome = biomeAt(index * HillDriveSectionLength + HillDriveSectionLength / 2)
        val heights = if (index == 0L) {
            HillDriveStartHeights.copyOf()
        } else {
            val profile = when (biome) {
                HillDriveBiome.Sand -> floatArrayOf(0f, 0.08f, -0.55f, -1f, -0.55f, 0.08f, 0f)
                HillDriveBiome.Snow -> floatArrayOf(0f, -0.3f, -1.1f, -0.15f, 0.9f, 0.3f, 0f)
                HillDriveBiome.Water -> floatArrayOf(0f, -0.08f, 0.18f, 0.85f, 0.18f, -0.08f, 0f)
                HillDriveBiome.Moon -> floatArrayOf(0f, 0.12f, 0.62f, 0.9f, 0.62f, 0.12f, 0f)
                else -> HillDriveHills[(variant and Int.MAX_VALUE) % HillDriveHills.size]
            }
            val variation = HillDriveHills[(variant ushr 12) % HillDriveHills.size]
            val relief = (24f + difficulty * 48f) * (0.65f + random.nextFloat() * 0.8f)
            FloatArray(profile.size) { point ->
                (profile[point] * 0.65f + variation[point] * 0.35f) * relief *
                        (0.8f + random.nextFloat() * 0.4f)
            }
        }
        val slopes = FloatArray(knots.size)
        for (i in 1 until knots.lastIndex) {
            val left = (heights[i] - heights[i - 1]) / (knots[i] - knots[i - 1])
            val right = (heights[i + 1] - heights[i]) / (knots[i + 1] - knots[i])
            if (left * right > 0f) slopes[i] = 2f * left * right / (left + right)
        }
        val section = HillDriveSection(knots, heights, slopes)
        val maximumSlope = (0.45f + difficulty * 0.25f) *
                (1f - biomeWeight(
                    index * HillDriveSectionLength + 300.0,
                    HillDriveBiome.Moon
                ) * 0.2f)
        var scale = 1f
        // Small hills and the long mountain profile must share the same slope budget.
        for (point in 0..75) {
            val local = point * 8f
            val slope = section.sample(local).y
            val mountain = sampleMountain(index * HillDriveSectionLength + local).y
            val allowance = when {
                slope > 0f -> (maximumSlope - mountain) / slope
                slope < 0f -> (-maximumSlope - mountain) / slope
                else -> 1f
            }
            scale = minOf(scale, allowance.coerceIn(0f, 1f))
        }
        return if (scale < 1f) {
            HillDriveSection(
                knots,
                FloatArray(heights.size) { heights[it] * scale },
                FloatArray(slopes.size) { slopes[it] * scale }
            )
        } else section
    }

    private fun extendRoute() {
        val right = maxOf(farthestX + HillDriveSectionLength, cameraX + width - cameraAnchorX + 80f)
        while (nextSection * HillDriveSectionLength <= right) {
            val start = nextSection * HillDriveSectionLength
            val variant = hillDriveHash(nextSection, seed)
            for (group in 0..1) {
                val first =
                    if (group == 0) 105 + (variant and 31) else 340 + (variant ushr 6 and 31)
                val rows =
                    if (nextSection == 0L && group == 0) 1 else 2 + (variant ushr (group + 12) and 1)
                val columns = 4 + (variant ushr (group + 16) and 1)
                for (column in 0 until columns) {
                    val coinX = start + first + column * 25.0
                    val surface = sampleRoad(coinX, terrain = true)
                    if (abs(surface.y) > 0.65f) continue
                    val baseY = surface.x - 25f
                    for (row in 0 until rows) {
                        val value = when {
                            row == 0 -> if (column % 3 == 0) 5 else 10
                            row == 1 -> if (column % 2 == 0) 10 else 25
                            column != columns / 2 -> 25
                            variant ushr 20 and 15 == 0 -> 500
                            else -> 100
                        }
                        pickups += HillDrivePickup(
                            x = coinX,
                            y = baseY - row * 25f,
                            fuel = false,
                            value = value
                        )
                    }
                }
            }
            if (nextSection % 3L == 1L) {
                val canX = (0..5).map { start + 500.0 + it * 16.0 }
                    .minBy { abs(sampleRoad(it, terrain = true).y) }
                pickups += HillDrivePickup(canX, terrainHeight(canX) - 23f, fuel = true)
            }
            nextSection++
        }
    }

    private fun collectPickups(facing: Offset, down: Offset) {
        val oldest = farthestX - maxOf(width.toDouble() + 100.0, 2 * HillDriveSectionLength)
        if (coinPoints < 0L) coinPoints = coins * 10L
        pickups.removeAll { pickup ->
            if (pickup.x < oldest) return@removeAll true
            val dx = pickup.x - x
            val radius = if (pickup.fuel) 13.5f else 11.7f
            val reach = HillDriveWheelbase + HillDriveDroop + HillDriveWheelRadius + radius
            if (abs(dx) > reach) return@removeAll false
            val pickupY = pickupHeight(pickup)
            if (abs(pickupY - y) > reach) return@removeAll false
            val point = Offset(dx.toFloat(), pickupY - y)
            val local = Offset(dot(point, facing), dot(point, down) - HillDriveBodyY)
            val wheelRadius = radius + HillDriveWheelRadius
            val rearX = (pickup.x - rear.x).toFloat()
            val rearY = pickupY - rear.y
            val frontX = (pickup.x - front.x).toFloat()
            val frontY = pickupY - front.y
            val touchesWheel = rearX * rearX + rearY * rearY <= wheelRadius * wheelRadius ||
                    frontX * frontX + frontY * frontY <= wheelRadius * wheelRadius
            if (!touchesWheel && !touchesBody(local, radius)) return@removeAll false
            if (collections.size >= 24) collections.removeAt(0)
            collections += HillDriveCollection(pickup.x, pickupY, pickup.fuel, pickup.value)
            if (pickup.fuel) fuel = 1f
            else {
                coins++
                coinPoints += pickup.value
            }
            true
        }
    }

    private fun touchesBody(point: Offset, radius: Float): Boolean {
        var inside = false
        var previous = HillDriveHull.last()
        for (vertex in HillDriveHull) {
            val edge = vertex - previous
            val offset = point - previous
            val length = dot(edge, edge)
            val t = if (length > 0f) (dot(offset, edge) / length).coerceIn(0f, 1f) else 0f
            val distance = offset - edge * t
            if (dot(distance, distance) <= radius * radius) return true
            if ((previous.y > point.y) != (vertex.y > point.y) &&
                point.x < previous.x + (point.y - previous.y) * edge.x / edge.y
            ) inside = !inside
            previous = vertex
        }
        return inside
    }

    private fun roadNormal(slope: Float): Offset = Offset(slope, -1f) / sqrt(1f + slope * slope)

    private fun dot(first: Offset, second: Offset) = first.x * second.x + first.y * second.y

    private fun cross(first: Offset, second: Offset) = first.x * second.y - first.y * second.x
}

private fun hillDriveHash(index: Long, seed: Int): Int {
    var bits = (index xor seed.toLong()) * -7046029254386353131L
    bits = (bits xor (bits ushr 30)) * -4658895280553007687L
    bits = (bits xor (bits ushr 27)) * -7723592293110705685L
    return (bits xor (bits ushr 31)).toInt()
}