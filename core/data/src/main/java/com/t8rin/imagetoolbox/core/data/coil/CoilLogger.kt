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

import coil3.network.HttpException
import coil3.util.Logger
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.utils.makeLog
import io.ktor.http.URLParserException
import kotlinx.coroutines.CancellationException
import java.io.IOException
import com.t8rin.imagetoolbox.core.utils.Logger as RealLogger

internal class CoilLogger : Logger {

    override var minLevel = if (BuildConfig.DEBUG) Logger.Level.Debug else Logger.Level.Warn

    override fun log(
        tag: String,
        level: Logger.Level,
        message: String?,
        throwable: Throwable?
    ) {
        if (level < minLevel) return

        val logLevel = throwable?.imageLoadLogLevel() ?: level.toLogger()
        message?.takeIf {
            "NullRequestData" !in it && "PDF" !in it
        }?.makeLog(tag, logLevel)

        throwable?.takeIf {
            "The request's data is null" !in it.message.orEmpty() && "PDF" !in it.message.orEmpty()
        }?.let { it.makeLog(tag, it.imageLoadLogLevel()) }
    }

    private fun Logger.Level.toLogger(): RealLogger.Level = when (this) {
        Logger.Level.Verbose -> RealLogger.Level.Verbose
        Logger.Level.Debug -> RealLogger.Level.Debug
        Logger.Level.Info -> RealLogger.Level.Info
        Logger.Level.Warn -> RealLogger.Level.Warn
        Logger.Level.Error -> RealLogger.Level.Error
    }

}

internal fun Throwable.imageLoadLogLevel(): RealLogger.Level {
    val causes = generateSequence(this) { it.cause }
        .takeWhile(mutableSetOf<Throwable>()::add)
        .toList()
    if (causes.any { it is Error }) return RealLogger.Level.Error

    return when {
        causes.any { it is IOException || it is SecurityException || it is CancellationException } ->
            RealLogger.Level.Warn

        this is HttpException || this is URLParserException -> RealLogger.Level.Warn
        message.orEmpty().let {
            it.startsWith("BitmapFactory returned a null bitmap") ||
                    it.startsWith("Failed to create image decoder") ||
                    it.startsWith("Unable to create a decoder") ||
                    it == "SVG document is empty" ||
                    it.startsWith("XML parser problem") ||
                    it == "Could not scale to target size!" ||
                    it.startsWith("Only owner is able to interact with pending/trashed item") ||
                    (it.startsWith("Failed to determine if ") &&
                            "java.io.FileNotFoundException: Missing file" in it)
        } -> RealLogger.Level.Warn

        else -> RealLogger.Level.Error
    }
}