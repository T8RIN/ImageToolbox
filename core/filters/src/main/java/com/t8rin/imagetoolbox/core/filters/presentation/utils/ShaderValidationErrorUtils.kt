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

package com.t8rin.imagetoolbox.core.filters.presentation.utils

import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParseError
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParseException
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValidationError
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.appContext

fun ShaderParseException.localizedMessage(): String =
    errors.joinToString(separator = "\n") { it.localizedMessage() }

fun ShaderParseError.localizedMessage(): String = appContext.run {
    when (this@localizedMessage) {
        ShaderParseError.EmptyFile -> getString(R.string.shader_parse_error_empty_file)
        ShaderParseError.NotItShaderJson -> getString(R.string.shader_parse_error_not_itshader_json)
        ShaderParseError.InvalidJson -> getString(R.string.shader_parse_error_invalid_json)
        is ShaderParseError.MissingField -> getString(
            R.string.shader_parse_error_missing_field,
            field
        )

        is ShaderParseError.MissingParamField -> getString(
            R.string.shader_parse_error_missing_param_field,
            path
        )

        is ShaderParseError.UnsupportedParamType -> getString(
            R.string.shader_parse_error_unsupported_param_type,
            path,
            type,
            supportedTypes
        )

        is ShaderParseError.MissingValue -> getString(
            R.string.shader_parse_error_missing_value,
            path,
            type
        )

        is ShaderParseError.FiniteNumberExpected -> getString(
            R.string.shader_parse_error_finite_number_expected,
            path
        )

        is ShaderParseError.IntegerExpected -> getString(
            R.string.shader_parse_error_integer_expected,
            path
        )

        is ShaderParseError.BoolExpected -> getString(
            R.string.shader_parse_error_bool_expected,
            path
        )

        is ShaderParseError.ColorExpected -> getString(
            R.string.shader_parse_error_color_expected,
            path
        )

        is ShaderParseError.ColorFormatExpected -> getString(
            R.string.shader_parse_error_color_format_expected,
            path
        )

        is ShaderParseError.HexColorExpected -> getString(
            R.string.shader_parse_error_hex_color_expected,
            path
        )

        is ShaderParseError.ColorChannelCountExpected -> getString(
            R.string.shader_parse_error_color_channel_count_expected,
            path
        )

        is ShaderParseError.ColorChannelRangeExpected -> getString(
            R.string.shader_parse_error_color_channel_range_expected,
            path
        )

        is ShaderParseError.Vec2Expected -> getString(
            R.string.shader_parse_error_vec2_expected,
            path
        )

        is ShaderParseError.Vec2ComponentCountExpected -> getString(
            R.string.shader_parse_error_vec2_component_count_expected,
            path
        )
    }
}

fun ShaderValidationError.localizedMessage() = appContext.run {
    when (this@localizedMessage) {
        is ShaderValidationError.UnsupportedVersion -> getString(
            R.string.shader_error_unsupported_version,
            version,
            supportedVersion
        )

        ShaderValidationError.BlankName -> getString(R.string.shader_error_blank_name)
        ShaderValidationError.BlankSource -> getString(R.string.shader_error_blank_source)
        is ShaderValidationError.UnsupportedCharacters -> getString(
            R.string.shader_error_unsupported_characters,
            characters
        )

        is ShaderValidationError.DuplicateParameter -> getString(
            R.string.shader_error_duplicate_parameter,
            name
        )

        ShaderValidationError.BlankParameterName -> getString(
            R.string.shader_error_blank_parameter_name
        )

        is ShaderValidationError.ReservedParameterName -> getString(
            R.string.shader_error_reserved_parameter_name,
            name
        )

        is ShaderValidationError.InvalidParameterName -> getString(
            R.string.shader_error_invalid_parameter_name,
            name
        )

        is ShaderValidationError.InvalidValueType -> getString(
            R.string.shader_error_invalid_value_type,
            name,
            fieldName,
            actualType,
            expectedType
        )

        is ShaderValidationError.BoolBounds -> getString(
            R.string.shader_error_bool_bounds,
            name
        )

        is ShaderValidationError.MinGreaterThanMax -> getString(
            R.string.shader_error_min_greater_than_max,
            name
        )

        is ShaderValidationError.DefaultLowerThanMin -> getString(
            R.string.shader_error_default_lower_than_min,
            name
        )

        is ShaderValidationError.DefaultGreaterThanMax -> getString(
            R.string.shader_error_default_greater_than_max,
            name
        )

        is ShaderValidationError.MissingInputTexture -> getString(
            R.string.shader_error_missing_input_texture,
            name
        )

        is ShaderValidationError.MissingTextureCoordinate -> getString(
            R.string.shader_error_missing_texture_coordinate,
            name
        )

        ShaderValidationError.MissingMainFunction -> getString(
            R.string.shader_error_missing_main_function
        )

        ShaderValidationError.MissingFragmentColor -> getString(
            R.string.shader_error_missing_fragment_color
        )

        ShaderValidationError.ShaderToyMainImage -> getString(
            R.string.shader_error_shadertoy_main_image
        )

        ShaderValidationError.ShaderToyITime -> getString(R.string.shader_error_shadertoy_i_time)
        ShaderValidationError.ShaderToyIFrame -> getString(R.string.shader_error_shadertoy_i_frame)
        ShaderValidationError.ShaderToyIResolution -> getString(
            R.string.shader_error_shadertoy_i_resolution
        )

        ShaderValidationError.ShaderToyIChannel -> getString(
            R.string.shader_error_shadertoy_i_channel
        )

        ShaderValidationError.ExternalTextures -> getString(
            R.string.shader_error_external_textures
        )

        ShaderValidationError.ExternalTextureExtensions -> getString(
            R.string.shader_error_external_texture_extensions
        )

        ShaderValidationError.LibretroParameters -> getString(
            R.string.shader_error_libretro_parameters
        )

        is ShaderValidationError.ExtraInputTexture -> getString(
            R.string.shader_error_extra_input_texture,
            samplerType,
            samplerName
        )

        is ShaderValidationError.MissingUniform -> getString(
            R.string.shader_error_missing_uniform,
            name,
            expectedType
        )

        is ShaderValidationError.WrongUniformType -> getString(
            R.string.shader_error_wrong_uniform_type,
            name,
            actualType,
            expectedType
        )
    }
}
