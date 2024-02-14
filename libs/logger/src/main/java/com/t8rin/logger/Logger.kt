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

package com.t8rin.logger

import android.util.Log

data object Logger {

    inline fun <reified T> makeLog(
        tag: String = "Logger" + (T::class.simpleName?.let { "_$it" } ?: ""),
        level: Level = Level.Debug,
        dataBlock: () -> T
    ) {
        val data = dataBlock()
        val message = if (data is Throwable) {
            Log.getStackTraceString(data)
        } else data.toString()

        when (level) {
            is Level.Assert -> Log.println(level.priority, tag, message)
            Level.Debug -> Log.d(tag, message)
            Level.Error -> Log.e(tag, message)
            Level.Info -> Log.i(tag, message)
            Level.Verbose -> Log.v(tag, message)
            Level.Warn -> Log.w(tag, message)
        }
    }

    inline fun <reified T> makeLog(
        tag: String = "Logger" + (T::class.simpleName?.let { "_$it" } ?: ""),
        level: Level = Level.Debug,
        data: T
    ) = makeLog(tag = tag, level = level) { data }

    fun makeLog(
        level: Level = Level.Debug,
        separator: String = " - ",
        vararg data: Any
    ) {
        makeLog(level = level) { data.toList().joinToString(separator) { it.toString() } }
    }

    sealed interface Level {
        data class Assert(
            val priority: Int
        ) : Level

        data object Error : Level
        data object Warn : Level
        data object Info : Level
        data object Debug : Level
        data object Verbose : Level
    }

}

inline fun <reified T> T.makeLog(
    tag: String = "Logger" + (T::class.simpleName?.let { "_$it" } ?: ""),
    level: Logger.Level = Logger.Level.Debug,
    dataBlock: (T) -> Any? = { it }
): T = also {
    if (it is Throwable) {
        Log.e(tag, it.localizedMessage, it)
        Logger.makeLog(
            tag = tag,
            level = level,
            dataBlock = { dataBlock(it) }
        )
    } else {
        Logger.makeLog(
            tag = tag,
            level = level,
            dataBlock = { dataBlock(it) }
        )
    }
}

inline infix fun <reified T> T.makeLog(
    tag: String
): T = makeLog(tag) { this }