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

package com.t8rin.imagetoolbox.core.domain.remote

interface RemoteResourcesStore {

    /**
     *  Get cached remote resources
     *
     *      val resourcesStore: RemoteResourcesStore = ...
     *
     *      resourcesStore.getResources(
     *          name = RemoteResources.CUBE_LUT,
     *          forceUpdate = true,
     *          onDownloadRequest = { name ->
     *              resourcesStore.downloadResources(
     *                  name = name,
     *                  onProgress = { progress ->
     *
     *                  },
     *                  onFailure = { throwable ->
     *
     *                  }
     *              )
     *          }
     *     )
     **/
    suspend fun getResources(
        name: String,
        forceUpdate: Boolean,
        onDownloadRequest: suspend (name: String) -> RemoteResources?
    ): RemoteResources?


    /**
     * Download Resources from [ImageToolboxRemoteResources](https://github.com/T8RIN/ImageToolboxRemoteResources)
     *
     *      val resourcesStore: RemoteResourcesStore = ...
     *
     *      resourcesStore.downloadResources(
     *           name = name,
     *           onProgress = { progress ->
     *
     *           },
     *           onFailure = { throwable ->
     *
     *           }
     *      )
     */
    suspend fun downloadResources(
        name: String,
        onProgress: (RemoteResourcesDownloadProgress) -> Unit,
        onFailure: (Throwable) -> Unit,
        downloadOnlyNewData: Boolean = false
    ): RemoteResources?

}