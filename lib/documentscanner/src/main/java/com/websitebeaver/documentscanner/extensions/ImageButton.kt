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

import android.widget.ImageButton

/**
 * This function adds an on click listener to the button. It makes the button not clickable,
 * calls the on click function, and then makes the button clickable. This prevents the on click
 * function from being called while it runs.
 *
 * @param onClick the click event handler
 */
fun ImageButton.onClick(onClick: () -> Unit) {
    setOnClickListener {
        isClickable = false
        onClick()
        isClickable = true
    }
}