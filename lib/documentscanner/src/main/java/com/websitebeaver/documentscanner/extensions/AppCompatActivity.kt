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

package com.websitebeaver.documentscanner.extensions

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
        /**
         * @property screenBounds the screen bounds (used to get screen width and height)
         */
val AppCompatActivity.screenBounds: Rect
    get() {
        // currentWindowMetrics was added in Android R
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return windowManager.currentWindowMetrics.bounds
        }

        // fall back to get screen width and height if using a version before Android R
        return Rect(
            0, 0, windowManager.defaultDisplay.width, windowManager.defaultDisplay.height
        )
    }

/**
 * @property screenWidth the screen width
 */
val AppCompatActivity.screenWidth: Int get() = screenBounds.width()

/**
 * @property screenHeight the screen height
 */
val AppCompatActivity.screenHeight: Int get() = screenBounds.height()