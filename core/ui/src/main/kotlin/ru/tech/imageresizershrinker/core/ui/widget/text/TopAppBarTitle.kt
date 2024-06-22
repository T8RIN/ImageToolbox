/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.text

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.resources.R

@Composable
fun <T : Any> TopAppBarTitle(
    title: String,
    input: T?,
    isLoading: Boolean,
    size: Long?,
    updateOnSizeChange: Boolean = true
) {
    if (updateOnSizeChange) {
        AnimatedContent(
            targetState = Triple(
                input,
                isLoading,
                size
            ),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.marquee()
        ) { (inp, loading, size) ->
            if (loading) {
                Text(
                    stringResource(R.string.loading)
                )
            } else if (inp == null || size == null || size <= 0) {
                AnimatedContent(targetState = title) {
                    Text(it)
                }
            } else {
                Text(
                    stringResource(
                        R.string.size,
                        readableByteCount(size)
                    )
                )
            }
        }
    } else {
        AnimatedContent(
            targetState = input to isLoading,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.marquee()
        ) { (inp, loading) ->
            if (loading) {
                Text(
                    stringResource(R.string.loading)
                )
            } else if (inp == null || size == null || size <= 0) {
                AnimatedContent(targetState = title) {
                    Text(it)
                }
            } else {
                Text(
                    stringResource(
                        R.string.size,
                        readableByteCount(size)
                    )
                )
            }
        }
    }
}