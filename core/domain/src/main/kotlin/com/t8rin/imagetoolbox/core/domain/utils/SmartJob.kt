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

package com.t8rin.imagetoolbox.core.domain.utils

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

/**
 * [Job] delegate which automatically cancels previous instance after setting new value,
 * @param onCancelled called when previous job is about to cancel
 **/
private class SmartJobImpl(
    private val onCancelled: (Job) -> Unit = {}
) : ReadWriteDelegate<Job?> {

    private var job: Job? = null

    override fun get(): Job? = job

    override fun set(value: Job?) {
        job?.apply {
            onCancelled(this)
            cancelChildren()
            cancel()
        }
        job = value
    }
}

/**
 * [Job] delegate which automatically cancels previous instance after setting new value,
 * @param onCancelled called when previous job is about to cancel
 **/
fun smartJob(
    onCancelled: (Job) -> Unit = {}
): ReadWriteDelegate<Job?> = SmartJobImpl(onCancelled)