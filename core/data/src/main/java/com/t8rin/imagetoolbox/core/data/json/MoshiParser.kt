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

package com.t8rin.imagetoolbox.core.data.json

import com.squareup.moshi.Moshi
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.logger.makeLog
import java.lang.reflect.Type
import javax.inject.Inject

internal class MoshiParser @Inject constructor(
    private val moshi: Moshi
) : JsonParser {

    override fun <T> toJson(
        obj: T,
        type: Type,
    ): String? = runCatching {
        moshi.adapter<T>(type).toJson(obj)
    }.onFailure { it.makeLog("MoshiParser toJson") }.getOrNull()

    override fun <T> fromJson(
        json: String,
        type: Type,
    ): T? = runCatching {
        moshi.adapter<T>(type).fromJson(json)
    }.onFailure { it.makeLog("MoshiParser fromJson") }.getOrNull()

}