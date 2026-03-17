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

import android.app.Application
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

abstract class NeuralTool {
    protected val context get() = application

    companion object {
        private var _httpClient: HttpClient = HttpClient {
            install(Logging) {
                level = LogLevel.INFO
            }
        }
        internal val httpClient: HttpClient get() = _httpClient

        internal var baseUrl = ""

        private var _context: Application? = null
        internal val application: Application
            get() = _context
                ?: throw NullPointerException("Call NeuralTool.init() in Application onCreate to use this feature")

        fun init(
            context: Application,
            httpClient: HttpClient? = null,
            baseUrl: String
        ) {
            _httpClient = httpClient ?: _httpClient
            this.baseUrl = baseUrl
            _context = context
        }
    }
}