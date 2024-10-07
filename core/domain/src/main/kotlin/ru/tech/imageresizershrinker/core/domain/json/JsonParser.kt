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

package ru.tech.imageresizershrinker.core.domain.json

import java.lang.reflect.Type

interface JsonParser {

    /**
     * [type] is type of [obj]: [T], which is converted to json
     *
     * @return Json from given object
     */
    fun <T> toJson(
        obj: T,
        type: Type,
    ): String?

    /**
     * [type] is type of [T], which is will be parsed from json
     *
     * @return Object from given json
     */
    fun <T> fromJson(
        json: String,
        type: Type,
    ): T?

}