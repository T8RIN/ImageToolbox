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

package ru.tech.imageresizershrinker.feature.filters.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import ru.tech.imageresizershrinker.core.filters.presentation.widget.FilterReorderSheet
import ru.tech.imageresizershrinker.core.filters.presentation.widget.addFilters.AddFiltersSheet
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.sheets.PickImageFromUrisSheet
import ru.tech.imageresizershrinker.feature.filters.presentation.components.addEditMaskSheet.AddEditMaskSheet
import ru.tech.imageresizershrinker.feature.filters.presentation.screenLogic.FiltersComponent

@Composable
internal fun FiltersContentSheets(
    component: FiltersComponent
) {
    val essentials = rememberLocalEssentials()
    val isPortrait by isPortraitOrientationAsState()

    if (component.filterType is Screen.Filter.Type.Basic) {
        val transformations by remember(component.basicFilterState, component.imageInfo) {
            derivedStateOf(component::getFiltersTransformation)
        }

        PickImageFromUrisSheet(
            transformations = transformations,
            visible = component.isPickImageFromUrisSheetVisible,
            onDismiss = component::hidePickImageFromUrisSheet,
            uris = component.basicFilterState.uris,
            selectedUri = component.basicFilterState.selectedUri,
            onUriPicked = { uri ->
                component.updateSelectedUri(
                    uri = uri,
                    onFailure = essentials::showFailureToast
                )
            },
            onUriRemoved = { uri ->
                component.updateUrisSilently(removedUri = uri)
            },
            columns = if (isPortrait) 2 else 4,
        )

        AddFiltersSheet(
            visible = component.isAddFiltersSheetVisible,
            onDismiss = component::hideAddFiltersSheet,
            previewBitmap = component.previewBitmap,
            onFilterPicked = component::addFilterNewInstance,
            onFilterPickedWithParams = component::addFilter,
            component = component.addFiltersSheetComponent,
            filterTemplateCreationSheetComponent = component.filterTemplateCreationSheetComponent
        )

        FilterReorderSheet(
            filterList = component.basicFilterState.filters,
            visible = component.isReorderSheetVisible,
            onDismiss = component::hideReorderSheet,
            onReorder = component::updateFiltersOrder
        )
    } else if (component.filterType is Screen.Filter.Type.Masking) {
        AddEditMaskSheet(
            visible = component.isAddFiltersSheetVisible,
            targetBitmapUri = component.maskingFilterState.uri,
            onMaskPicked = component::addMask,
            onDismiss = component::hideAddFiltersSheet,
            masks = component.maskingFilterState.masks,
            component = component.addMaskSheetComponent
        )

        MaskReorderSheet(
            maskList = component.maskingFilterState.masks,
            visible = component.isReorderSheetVisible,
            onDismiss = component::hideReorderSheet,
            onReorder = component::updateMasksOrder
        )
    }
}