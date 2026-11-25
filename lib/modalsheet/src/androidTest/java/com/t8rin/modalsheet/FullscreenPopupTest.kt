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

package com.t8rin.modalsheet

import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullscreenPopupTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun disposingPopupDuringDecorViewDispatchDrawDoesNotCrash() {
        var showPopup by mutableStateOf(true)
        var triggerDisposeOnNextPreDraw = false
        var attachedPreDrawListener = false

        composeRule.setContent {
            val hostView = LocalView.current

            FullscreenPopup(placeAboveAll = true) {
                Box(Modifier.fillMaxSize())
            }

            SubcomposeLayout(Modifier.fillMaxSize()) { constraints ->
                val placeables = if (showPopup) {
                    subcompose("popup") {
                        FullscreenPopup {
                            Text("Transient Popup")
                        }
                    }.map { it.measure(constraints) }
                } else {
                    emptyList()
                }
                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeables.forEach { it.placeRelative(0, 0) }
                }
            }

            if (!attachedPreDrawListener) {
                attachedPreDrawListener = true
                hostView.viewTreeObserver.addOnPreDrawListener(
                    object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            if (triggerDisposeOnNextPreDraw) {
                                triggerDisposeOnNextPreDraw = false
                                Snapshot.withMutableSnapshot {
                                    showPopup = false
                                }
                                hostView.viewTreeObserver.removeOnPreDrawListener(this)
                            }
                            return true
                        }
                    }
                )
            }
        }

        composeRule.onNodeWithText("Transient Popup").assertIsDisplayed()

        composeRule.runOnIdle {
            triggerDisposeOnNextPreDraw = true
            composeRule.activity.window.decorView.invalidate()
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Transient Popup").assertDoesNotExist()
    }

    @Test
    fun showingPopupDuringDecorViewDispatchDrawDoesNotCrash() {
        var showPopup by mutableStateOf(false)
        var triggerShowOnNextPreDraw = false
        var attachedPreDrawListener = false

        composeRule.setContent {
            val hostView = LocalView.current

            FullscreenPopup(placeAboveAll = true) {
                Box(Modifier.fillMaxSize())
            }
            FullscreenPopup(placeAboveAll = true) {
                Box(Modifier.fillMaxSize())
            }

            SubcomposeLayout(Modifier.fillMaxSize()) { constraints ->
                val placeables = if (showPopup) {
                    subcompose("popup") {
                        FullscreenPopup {
                            Text("Deferred Popup")
                        }
                    }.map { it.measure(constraints) }
                } else {
                    emptyList()
                }
                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeables.forEach { it.placeRelative(0, 0) }
                }
            }

            if (!attachedPreDrawListener) {
                attachedPreDrawListener = true
                hostView.viewTreeObserver.addOnPreDrawListener(
                    object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            if (triggerShowOnNextPreDraw) {
                                triggerShowOnNextPreDraw = false
                                Snapshot.withMutableSnapshot {
                                    showPopup = true
                                }
                                hostView.viewTreeObserver.removeOnPreDrawListener(this)
                            }
                            return true
                        }
                    }
                )
            }
        }

        composeRule.waitForIdle()

        composeRule.runOnIdle {
            triggerShowOnNextPreDraw = true
            composeRule.activity.window.decorView.invalidate()
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Deferred Popup").assertIsDisplayed()
    }

    @Test
    fun rapidShowAndDismissCleansUpDecorViewChildren() {
        var showPopup by mutableStateOf(false)
        var cycle by mutableIntStateOf(0)
        var baselineChildren = 0

        composeRule.setContent {
            FullscreenPopup(placeAboveAll = true) {
                Box(Modifier.fillMaxSize())
            }
            if (showPopup) {
                FullscreenPopup {
                    Text("Cycle $cycle")
                }
            }
        }

        composeRule.runOnIdle {
            baselineChildren = (composeRule.activity.window.decorView as ViewGroup).childCount
        }

        repeat(10) { i ->
            composeRule.runOnIdle {
                cycle = i
                showPopup = true
            }
            composeRule.runOnIdle {
                showPopup = false
            }
            composeRule.waitForIdle()
        }

        composeRule.runOnIdle {
            val currentChildren = (composeRule.activity.window.decorView as ViewGroup).childCount
            assertEquals(baselineChildren, currentChildren)
        }
    }

    @Test
    fun modalSheetShowAndDismissDuringPreDrawDoesNotCrash() {
        var visible by mutableStateOf(true)
        var triggerHideOnNextPreDraw = false
        var attachedPreDrawListener = false

        composeRule.setContent {
            val hostView = LocalView.current

            FullscreenPopup(placeAboveAll = true) {
                Box(Modifier.fillMaxSize())
            }

            SubcomposeLayout(Modifier.fillMaxSize()) { constraints ->
                val placeables = subcompose("sheet") {
                    ModalSheet(
                        visible = visible,
                        onVisibleChange = { visible = it }
                    ) {
                        Text("Modal Content")
                    }
                }.map { it.measure(constraints) }
                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeables.forEach { it.placeRelative(0, 0) }
                }
            }

            if (!attachedPreDrawListener) {
                attachedPreDrawListener = true
                hostView.viewTreeObserver.addOnPreDrawListener(
                    object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            if (triggerHideOnNextPreDraw) {
                                triggerHideOnNextPreDraw = false
                                Snapshot.withMutableSnapshot {
                                    visible = false
                                }
                                hostView.viewTreeObserver.removeOnPreDrawListener(this)
                            }
                            return true
                        }
                    }
                )
            }
        }

        composeRule.onNodeWithText("Modal Content").assertIsDisplayed()

        composeRule.runOnIdle {
            triggerHideOnNextPreDraw = true
            composeRule.activity.window.decorView.invalidate()
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Modal Content").assertDoesNotExist()
    }

    @Test
    fun immediateShowAndDisposeLeavesNoPopupAttached() {
        var showPopup by mutableStateOf(false)
        var baselineChildren = 0

        composeRule.setContent {
            if (showPopup) {
                FullscreenPopup {
                    Text("Ephemeral Popup")
                }
            }
        }

        composeRule.runOnIdle {
            baselineChildren = (composeRule.activity.window.decorView as ViewGroup).childCount
            showPopup = true
            showPopup = false
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Ephemeral Popup").assertDoesNotExist()
        composeRule.runOnIdle {
            val currentChildren = (composeRule.activity.window.decorView as ViewGroup).childCount
            assertEquals(baselineChildren, currentChildren)
        }
    }
}

