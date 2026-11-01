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

package com.t8rin.imagetoolbox.feature.assistant.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantReplyTimeoutTest {
    @Test
    fun streamingContinuesPastTheInitialAndIdleDeadlines() = runBlocking {
        val answer = withAssistantReplyTimeout(
            firstTextTimeoutMillis = 150,
            idleTimeoutMillis = 1_000
        ) { onText ->
            buildString {
                repeat(4) {
                    append("part ")
                    onText()
                    delay(400)
                }
            }
        }
        assertEquals("part part part part ", answer)
    }

    @Test
    fun noVisibleTextStillTimesOutAndStopsGeneration() = runBlocking {
        var stopped = false
        val failure = runCatching {
            withAssistantReplyTimeout(firstTextTimeoutMillis = 150) {
                try {
                    awaitCancellation()
                } finally {
                    stopped = true
                }
            }
        }.exceptionOrNull()
        assertTrue(failure is TimeoutCancellationException)
        assertTrue(stopped)
    }

    @Test
    fun stalledStreamTimesOutAndKeepsItsPartialText() = runBlocking {
        val text = StringBuilder()
        var stopped = false
        val failure = runCatching {
            withAssistantReplyTimeout(
                firstTextTimeoutMillis = 5_000,
                idleTimeoutMillis = 150
            ) { onText ->
                try {
                    text.append("unfinished")
                    onText()
                    awaitCancellation()
                } finally {
                    stopped = true
                }
            }
        }.exceptionOrNull()
        assertTrue(failure is TimeoutCancellationException)
        assertEquals("unfinished", text.toString())
        assertTrue(stopped)
    }

    @Test
    fun userCancellationStopsTheStreamWithoutBecomingATimeout() = runBlocking {
        val started = CompletableDeferred<Unit>()
        var stopped = false
        val response = async {
            withAssistantReplyTimeout(firstTextTimeoutMillis = 5_000) { onText ->
                try {
                    onText()
                    started.complete(Unit)
                    awaitCancellation()
                } finally {
                    stopped = true
                }
            }
        }
        started.await()
        val cancellation = CancellationException("User stopped generation")
        response.cancel(cancellation)
        val failure = runCatching { response.await() }.exceptionOrNull()
        response.join()
        assertTrue(failure is CancellationException)
        assertFalse(failure is TimeoutCancellationException)
        assertEquals(cancellation.message, failure?.message)
        assertTrue(stopped)
    }

    @Test
    fun completedAnswerDoesNotWaitForTheWatchdog() = runBlocking {
        val answer = withTimeout(1_000) {
            withAssistantReplyTimeout(firstTextTimeoutMillis = 5_000) { "complete" }
        }
        assertEquals("complete", answer)
    }

    @Test
    fun generationFailureIsNotReplacedByATimeout() = runBlocking {
        val failure = runCatching {
            withAssistantReplyTimeout(firstTextTimeoutMillis = 5_000) {
                error("Generation failed")
            }
        }.exceptionOrNull()
        assertTrue(failure is IllegalStateException)
        assertEquals("Generation failed", failure?.message)
    }
}
