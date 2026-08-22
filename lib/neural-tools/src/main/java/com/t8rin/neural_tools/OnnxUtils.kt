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

package com.t8rin.neural_tools

import ai.onnxruntime.OnnxTensorLike
import ai.onnxruntime.OnnxValue
import ai.onnxruntime.OrtSession
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

fun OrtSession.runWithOptions(
    inputs: Map<String, OnnxTensorLike>,
    runOptions: OrtSession.RunOptions?
): OrtSession.Result = if (runOptions != null) {
    run(inputs, runOptions)
} else {
    run(inputs)
}

suspend fun OrtSession.runCancellable(
    inputs: Map<String, OnnxTensorLike>
): OrtSession.Result = runCancellable {
    run(inputs, it)
}

suspend fun OrtSession.runCancellable(
    inputs: Map<String, OnnxTensorLike>,
    pinnedOutputs: Map<String, OnnxValue>
): OrtSession.Result = runCancellable {
    run(inputs, outputNames, pinnedOutputs, it)
}

private suspend inline fun OrtSession.runCancellable(
    crossinline action: OrtSession.(OrtSession.RunOptions) -> OrtSession.Result
): OrtSession.Result = suspendCancellableCoroutine { continuation ->
    val runOptions = OrtSession.RunOptions()

    continuation.invokeOnCancellation { runOptions.terminate() }

    runCatching {
        action(runOptions)
    }.onSuccess { result ->
        continuation.resume(result) { _, cancelledResult, _ ->
            cancelledResult.close()
        }
    }.onFailure { throwable ->
        continuation.resumeWithException(throwable)
    }.also {
        runOptions.closeSafely()
    }
}

suspend fun <T> withCancellableRunOptions(
    onCancellation: (T) -> Unit = {},
    action: (OrtSession.RunOptions) -> T
): T = suspendCancellableCoroutine { continuation ->
    val runOptions = OrtSession.RunOptions()

    continuation.invokeOnCancellation { runOptions.terminate() }

    runCatching {
        action(runOptions)
    }.onSuccess { result ->
        continuation.resume(result) { _, cancelledResult, _ ->
            onCancellation(cancelledResult)
        }
    }.onFailure { throwable ->
        continuation.resumeWithException(throwable)
    }.also {
        runOptions.closeSafely()
    }
}

private fun OrtSession.RunOptions.terminate() = synchronized(this) {
    runCatching {
        setTerminate(true)
    }
}

private fun OrtSession.RunOptions.closeSafely() = synchronized(this) {
    close()
}