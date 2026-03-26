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

package com.t8rin.cropper.model

import com.t8rin.cropper.util.createRectShape

/**
 * Aspect ratio list with pre-defined aspect ratios
 */
val aspectRatios = listOf(
    CropAspectRatio(
        title = "9:16",
        shape = createRectShape(AspectRatio(9 / 16f)),
        aspectRatio = AspectRatio(9 / 16f)
    ),
    CropAspectRatio(
        title = "2:3",
        shape = createRectShape(AspectRatio(2 / 3f)),
        aspectRatio = AspectRatio(2 / 3f)
    ),
    CropAspectRatio(
        title = "Original",
        shape = createRectShape(AspectRatio.Original),
        aspectRatio = AspectRatio.Original
    ),
    CropAspectRatio(
        title = "1:1",
        shape = createRectShape(AspectRatio(1 / 1f)),
        aspectRatio = AspectRatio(1 / 1f)
    ),
    CropAspectRatio(
        title = "16:9",
        shape = createRectShape(AspectRatio(16 / 9f)),
        aspectRatio = AspectRatio(16 / 9f)
    ),
    CropAspectRatio(
        title = "1.91:1",
        shape = createRectShape(AspectRatio(1.91f / 1f)),
        aspectRatio = AspectRatio(1.91f / 1f)
    ),
    CropAspectRatio(
        title = "3:2",
        shape = createRectShape(AspectRatio(3 / 2f)),
        aspectRatio = AspectRatio(3 / 2f)
    ),
    CropAspectRatio(
        title = "3:4",
        shape = createRectShape(AspectRatio(3 / 4f)),
        aspectRatio = AspectRatio(3 / 4f)
    ),
    CropAspectRatio(
        title = "3:5",
        shape = createRectShape(AspectRatio(3 / 5f)),
        aspectRatio = AspectRatio(3 / 5f)
    )
)