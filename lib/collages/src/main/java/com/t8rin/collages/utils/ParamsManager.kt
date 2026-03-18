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

package com.t8rin.collages.utils

import android.os.Bundle
import com.t8rin.collages.view.PhotoItem

typealias ParamT = Int

internal typealias ItemUpdate = (photoItem: PhotoItem, values: FloatArray) -> Unit

internal class ParamsManager(
    private val values: FloatArray,
    private val itemUpdateFunctions: List<ItemUpdate?>,
    private val itemHandles: List<List<Handle>>,
    private val itemsByIndex: List<PhotoItem?>,
    private val paramToDependentItems: Array<IntArray>
) {
    class InvalidValues : RuntimeException()

    companion object {
        const val minSize: Float = 0.05f
    }

    var onItemUpdated: (Int) -> Unit = {}

    fun getHandles(itemIndex: Int): List<Handle> =
        if (itemIndex in itemHandles.indices) itemHandles[itemIndex] else emptyList()

    fun snapshotValues(): FloatArray = values.copyOf()

    // Only for handles registered with this manager
    internal fun valuesRef(): FloatArray = values

    fun updateParams(params: List<ParamT>, newValues: FloatArray, notify: Boolean = true) {
        val previous = FloatArray(params.size) { i -> values[params[i]] }
        try {
            for (i in params.indices) {
                values[params[i]] = newValues[i]
            }

            val affected: MutableSet<Int> = mutableSetOf()
            for (param in params) {
                val arr =
                    if (param in paramToDependentItems.indices) paramToDependentItems[param] else intArrayOf()
                for (item in arr) affected.add(item)
            }
            for (itemIndex in affected) {
                val photoItem =
                    if (itemIndex in itemsByIndex.indices) itemsByIndex[itemIndex] else null
                val update =
                    if (itemIndex in itemUpdateFunctions.indices) itemUpdateFunctions[itemIndex] else null
                if (photoItem != null && update != null) update(photoItem, values)
            }
        } catch (e: InvalidValues) {
            //rollback
            updateParams(params, previous, false)
            throw e
        }

        if (!notify) return

        val notified: MutableSet<Int> = mutableSetOf()
        for (param in params) {
            val arr =
                if (param in paramToDependentItems.indices) paramToDependentItems[param] else intArrayOf()
            for (item in arr) if (notified.add(item)) onItemUpdated(item)
        }
    }

    fun saveInstanceState(outState: Bundle) {
        outState.putFloatArray("collage_params_values", values.copyOf())
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        val saved = savedInstanceState.getFloatArray("collage_params_values") ?: return
        val count = minOf(saved.size, values.size)
        if (count <= 0) return
        val indices = (0 until count).toList()
        val newValues = FloatArray(count) { i -> saved[i] }
        updateParams(indices, newValues, notify = false)
    }
}