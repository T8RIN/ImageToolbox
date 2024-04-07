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

package ru.tech.imageresizershrinker.core.data.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import java.util.concurrent.Executors
import javax.inject.Inject

internal class DispatchersHolderImpl @Inject constructor() : DispatchersHolder {

    override val uiDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate

    override val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override val encodingDispatcher: CoroutineDispatcher = Executors
        .newSingleThreadExecutor().asCoroutineDispatcher()

    override val decodingDispatcher: CoroutineDispatcher = Executors
        .newFixedThreadPool(
            2 * Runtime.getRuntime().availableProcessors() + 1
        ).asCoroutineDispatcher()

    override val defaultDispatcher: CoroutineDispatcher =
        Executors.newCachedThreadPool().asCoroutineDispatcher()

}