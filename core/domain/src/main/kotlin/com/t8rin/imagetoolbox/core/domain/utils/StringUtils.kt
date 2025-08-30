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

@file:Suppress("unused")

package com.t8rin.imagetoolbox.core.domain.utils

fun String.trimTrailingZero(): String {
    val value = this
    return if (value.isNotEmpty()) {
        if (value.indexOf(".") < 0) {
            value
        } else {
            value.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        }
    } else {
        value
    }
}

/**
 * Returns a minimal set of characters that have to be removed from (or added to) the respective
 * strings to make the strings equal.
 */
fun String.differenceFrom(
    other: String
): Pair<String, String> = diffHelper(
    a = this,
    b = other,
    lookup = HashMap()
)

/**
 * Recursively compute a minimal set of characters while remembering already computed substrings.
 * Runs in O(n^2).
 */
private fun diffHelper(
    a: String,
    b: String,
    lookup: MutableMap<Long, Pair<String, String>>
): Pair<String, String> {
    val key = (a.length.toLong()) shl 32 or b.length.toLong()
    if (!lookup.containsKey(key)) {
        val value: Pair<String, String> = if (a.isEmpty() || b.isEmpty()) {
            a to b
        } else if (a[0] == b[0]) {
            diffHelper(
                a = a.substring(1),
                b = b.substring(1),
                lookup = lookup
            )
        } else {
            val aa = diffHelper(a.substring(1), b, lookup)
            val bb = diffHelper(a, b.substring(1), lookup)

            if (aa.first.length + aa.second.length < bb.first.length + bb.second.length) {
                (b[0].toString() + bb.second) to aa.second
            } else {
                bb.first to (b[0].toString() + bb.second)
            }
        }
        lookup[key] = value
    }
    return lookup.getOrElse(key) { "" to "" }
}