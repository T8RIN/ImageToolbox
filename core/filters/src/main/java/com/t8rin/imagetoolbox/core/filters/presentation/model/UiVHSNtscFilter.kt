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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.NtscParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.DISTORTION)
class UiVHSNtscFilter(
    override val value: NtscParams = NtscParams()
) : UiFilter<NtscParams>(
    title = R.string.vhs_ntsc,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.amount,
            valueRange = 0f..3f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.tape_wear,
            valueRange = 0f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.chroma_bleed,
            valueRange = 0f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.tracking,
            valueRange = 0f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.noise,
            valueRange = 0f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.snow,
            valueRange = 0f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.luma_smear,
            valueRange = 0f..8f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.sharpen,
            valueRange = 0f..8f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ringing,
            valueRange = 0f..16f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.seed,
            valueRange = 0f..10_000f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_phase_offset,
            valueRange = -16f..16f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_head_switching_height,
            valueRange = 0f..160f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_head_switching_offset,
            valueRange = -80f..80f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_head_switching_shift,
            valueRange = -800f..800f,
            roundTo = 1
        ),
        FilterParam(
            title = R.string.ntsc_midline_position,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_midline_jitter,
            valueRange = 0f..2f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_tracking_noise_height,
            valueRange = 0f..160f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_tracking_wave,
            valueRange = 0f..160f,
            roundTo = 1
        ),
        FilterParam(
            title = R.string.ntsc_tracking_snow,
            valueRange = 0f..1f,
            roundTo = 3
        ),
        FilterParam(
            title = R.string.ntsc_tracking_snow_anisotropy,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_tracking_noise_intensity,
            valueRange = 0f..2f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_composite_noise_frequency,
            valueRange = 0f..8f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_composite_noise_intensity,
            valueRange = 0f..3f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_composite_noise_detail,
            valueRange = 0f..16f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_ringing_frequency,
            valueRange = 0f..8f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_ringing_power,
            valueRange = 0f..20f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_luma_noise_frequency,
            valueRange = 0f..8f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_luma_noise_intensity,
            valueRange = 0f..3f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_luma_noise_detail,
            valueRange = 0f..16f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_chroma_noise_frequency,
            valueRange = 0f..8f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_chroma_noise_intensity,
            valueRange = 0f..3f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_chroma_noise_detail,
            valueRange = 0f..16f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_snow_intensity,
            valueRange = 0f..0.1f,
            roundTo = 4
        ),
        FilterParam(
            title = R.string.ntsc_snow_anisotropy,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_chroma_phase_noise,
            valueRange = 0f..0.25f,
            roundTo = 4
        ),
        FilterParam(
            title = R.string.ntsc_chroma_phase_error,
            valueRange = -1f..1f,
            roundTo = 3
        ),
        FilterParam(
            title = R.string.ntsc_chroma_delay_horizontal,
            valueRange = -100f..100f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_chroma_delay_vertical,
            valueRange = -40f..40f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_vhs_chroma_loss,
            valueRange = 0f..0.01f,
            roundTo = 5
        ),
        FilterParam(
            title = R.string.ntsc_vhs_sharpen_intensity,
            valueRange = 0f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_vhs_sharpen_frequency,
            valueRange = 0f..12f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_vhs_edge_wave_intensity,
            valueRange = 0f..6f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_vhs_edge_wave_speed,
            valueRange = 0f..24f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_vhs_edge_wave_frequency,
            valueRange = 0f..1f,
            roundTo = 3
        ),
        FilterParam(
            title = R.string.ntsc_vhs_edge_wave_detail,
            valueRange = 0f..16f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.ntsc_scale_horizontal,
            valueRange = 0.1f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_scale_vertical,
            valueRange = 0.1f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_scale_factor_x,
            valueRange = 0.1f..4f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ntsc_scale_factor_y,
            valueRange = 0.1f..4f,
            roundTo = 2
        )
    )
), Filter.VHSNtsc
