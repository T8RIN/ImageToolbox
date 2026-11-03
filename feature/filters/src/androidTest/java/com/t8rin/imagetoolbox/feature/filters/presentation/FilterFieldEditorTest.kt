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

package com.t8rin.imagetoolbox.feature.filters.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBrightnessFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiDisplacementFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.feature.filters.data.model.DisplacementFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.floatParam
import com.t8rin.imagetoolbox.feature.filters.data.model.seedParam
import com.t8rin.imagetoolbox.feature.filters.presentation.components.FilterFieldItem
import com.t8rin.imagetoolbox.feature.filters.presentation.components.requireFieldEditors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class FilterFieldEditorTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun seedUsesItsDescriptorAndPreviewCannotChangeIt() {
        var seed by mutableFloatStateOf(5f)
        var previewOnly by mutableStateOf(false)
        val filter = UiBrightnessFilter()
        val field = seedParam(R.string.amount, 5f, 0f..100f, roundTo = 0)
        val ordinary = floatParam(R.string.seed, 5f, 0f..100f)

        compose.setContent {
            ImageToolboxThemeForPreview(
                isDarkTheme = false,
                mapSettings = { it.copy(randomizeSeedAfterSave = false) }
            ) {
                Column {
                    FilterFieldItem(
                        property = Filter.Brightness.Value::bias,
                        field = field,
                        value = seed,
                        filter = filter,
                        previewOnly = previewOnly,
                        onValueChange = { seed = it }
                    )
                    FilterFieldItem(
                        property = Filter.Brightness.Value::bias,
                        field = ordinary,
                        value = 5f,
                        filter = filter,
                        previewOnly = previewOnly,
                        onValueChange = {}
                    )
                }
            }
        }

        val shuffle = string(R.string.shuffle)
        compose.onNodeWithContentDescription(shuffle).performClick()
        compose.runOnIdle {
            assertNotEquals(5f, seed)
            previewOnly = true
        }
        compose.onNodeWithContentDescription(shuffle).assertIsNotEnabled()
        val unchanged = seed
        compose.onNodeWithContentDescription(shuffle).performClick()
        compose.runOnIdle { assertEquals(unchanged, seed) }
    }

    @Test
    fun standardAndCustomFieldsShareTheEditorAndRespectPreview() {
        val filter = UiDisplacementFilter()
        val parameters = DisplacementFilter.parameters
        requireFieldEditors(parameters)
        var amount by mutableFloatStateOf(0.1f)
        var previewOnly by mutableStateOf(false)
        var customChanges = 0

        compose.setContent {
            ImageToolboxThemeForPreview(
                isDarkTheme = false,
                mapSettings = { it.copy(randomizeSeedAfterSave = false) }
            ) {
                Column {
                    FilterFieldItem(
                        property = Filter.Displacement.Value::strengthX,
                        field = parameters.field(Filter.Displacement.Value::strengthX),
                        value = amount,
                        filter = filter,
                        previewOnly = previewOnly,
                        onValueChange = { amount = it }
                    )
                    FilterFieldItem(
                        property = Filter.Displacement.Value::verticalMap,
                        field = parameters.field(Filter.Displacement.Value::verticalMap),
                        value = null,
                        filter = filter,
                        previewOnly = previewOnly,
                        onValueChange = { customChanges++ }
                    )
                }
            }
        }

        val slider = compose.onNode(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        )
        slider.performSemanticsAction(SemanticsActions.SetProgress) {
            it(0.5f)
        }
        compose.runOnIdle {
            assertEquals(0.5f, amount, 0.001f)
            previewOnly = true
        }
        slider.assertIsNotEnabled()
        compose.onNode(isToggleable()).assertIsNotEnabled().performClick()
        compose.runOnIdle {
            assertEquals(0, customChanges)
            assertEquals(0.5f, amount, 0.001f)
        }
    }

    private fun string(id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
}
