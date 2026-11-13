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

package com.t8rin.imagetoolbox.core.games

import android.os.Bundle
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset
import com.t8rin.imagetoolbox.core.utils.makeLog
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

private val ArcadeJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

@Serializable
private data class ArcadeSnapshot<T : ArcadeRun>(
    val run: T,
    val phase: ArcadePhase,
    val score: Int,
    val animating: Boolean,
    val levelTransition: Float = 0f,
    val version: Int = 1
)

internal fun <T : ArcadeRun> arcadeRunSaver(serializer: KSerializer<T>) = Saver<T, Bundle>(
    save = { run ->
        if (!run.prepared) null else Bundle().apply {
            putString(
                "snapshot",
                ArcadeJson.encodeToString(
                    ArcadeSnapshot.serializer(serializer),
                    ArcadeSnapshot(
                        run = run,
                        phase = run.phase,
                        score = run.score,
                        animating = run.isAnimating,
                        levelTransition = run.levelTransition
                    )
                )
            )
        }
    },
    restore = { state ->
        state.getString("snapshot")?.let { saved ->
            try {
                val snapshot =
                    ArcadeJson.decodeFromString(ArcadeSnapshot.serializer(serializer), saved)
                snapshot.run.takeIf { snapshot.version == 1 && it.isValid() }?.apply {
                    prepared = true
                    phase =
                        if (snapshot.phase == ArcadePhase.Running) ArcadePhase.Paused else snapshot.phase
                    score = snapshot.score.coerceAtLeast(0)
                    isAnimating = snapshot.animating
                    levelTransition = snapshot.levelTransition.coerceIn(0f, LevelTransitionDuration)
                    onRestore()
                }
            } catch (exception: IllegalArgumentException) {
                exception.makeLog("Arcade")
                null
            }
        }
    }
)

internal typealias ArcadeOffset = @Serializable(ArcadeOffsetSerializer::class) Offset
internal typealias ArcadeDeque<T> = @Serializable(ArcadeDequeSerializer::class) ArrayDeque<T>

internal object ArcadeOffsetSerializer : KSerializer<Offset> {
    @Serializable
    private data class Point(val x: Float, val y: Float)

    override val descriptor: SerialDescriptor = Point.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Offset) =
        encoder.encodeSerializableValue(Point.serializer(), Point(x = value.x, y = value.y))

    override fun deserialize(decoder: Decoder): Offset =
        decoder.decodeSerializableValue(Point.serializer()).let { Offset(x = it.x, y = it.y) }
}

internal class ArcadeDequeSerializer<T>(serializer: KSerializer<T>) : KSerializer<ArrayDeque<T>> {
    private val list = ListSerializer(serializer)
    override val descriptor: SerialDescriptor = list.descriptor

    override fun serialize(encoder: Encoder, value: ArrayDeque<T>) =
        encoder.encodeSerializableValue(list, value)

    override fun deserialize(decoder: Decoder): ArrayDeque<T> =
        ArrayDeque(decoder.decodeSerializableValue(list))
}