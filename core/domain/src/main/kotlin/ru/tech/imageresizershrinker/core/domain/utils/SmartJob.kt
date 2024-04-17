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

package ru.tech.imageresizershrinker.core.domain.utils

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class SmartJob<T>(
    private val onCancelled: (Job) -> Unit = {}
) : ReadWriteProperty<T, Job?> {

    private var job: Job? = null

    override fun getValue(
        thisRef: T,
        property: KProperty<*>
    ): Job? = job

    override fun setValue(
        thisRef: T,
        property: KProperty<*>,
        value: Job?
    ) {
        job?.apply {
            onCancelled(this)
            cancelChildren()
            cancel()
        }
        job = value
    }
}

fun <T> smartJob(
    onCancelled: (Job) -> Unit = {}
): ReadWriteProperty<T, Job?> = SmartJob(onCancelled)