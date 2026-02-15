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

package com.t8rin.imagetoolbox.core.data.coil

import coil3.util.DebugLogger
import coil3.util.Logger
import com.t8rin.logger.makeLog
import com.t8rin.logger.Logger as RealLogger

internal class CoilLogger : Logger {

    private val delegate = DebugLogger()

    override var minLevel: Logger.Level
        get() = delegate.minLevel
        set(value) {
            delegate.minLevel = value
        }

    override fun log(
        tag: String,
        level: Logger.Level,
        message: String?,
        throwable: Throwable?
    ) {
        message?.takeIf { "NullRequestData" !in it }?.makeLog(tag, level.toLogger())
        throwable?.takeIf { "The request's data is null" !in it.message.orEmpty() }?.makeLog(tag)

        delegate.log(
            tag = tag,
            level = level,
            message = message,
            throwable = throwable
        )
    }

    private fun Logger.Level.toLogger(): RealLogger.Level = when (this) {
        Logger.Level.Verbose -> RealLogger.Level.Verbose
        Logger.Level.Debug -> RealLogger.Level.Debug
        Logger.Level.Info -> RealLogger.Level.Info
        Logger.Level.Warn -> RealLogger.Level.Warn
        Logger.Level.Error -> RealLogger.Level.Error
    }

}