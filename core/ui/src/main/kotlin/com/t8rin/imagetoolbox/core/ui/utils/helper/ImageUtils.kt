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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.OpenableColumns
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.core.net.toFile
import androidx.core.text.isDigitsOnly
import coil3.Image
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getStringLocalized
import java.util.Locale


object ImageUtils {

    fun Drawable.toBitmap(): Bitmap = toBitmap(config = getSuitableConfig())

    fun MetadataTag.localizedName(
        context: Context,
        locale: Locale? = null
    ): String {
        val resId = titleResId

        return locale?.let {
            context.getStringLocalized(
                resId = resId,
                locale = locale
            )
        } ?: context.getString(resId)
    }

    private val MetadataTag.titleResId: Int
        @StringRes
        get() = when (this) {
            is MetadataTag.BitsPerSample -> R.string.tag_bits_per_sample
            is MetadataTag.Compression -> R.string.tag_compression
            is MetadataTag.PhotometricInterpretation -> R.string.tag_photometric_interpretation
            is MetadataTag.SamplesPerPixel -> R.string.tag_samples_per_pixel
            is MetadataTag.PlanarConfiguration -> R.string.tag_planar_configuration
            is MetadataTag.YCbCrSubSampling -> R.string.tag_y_cb_cr_sub_sampling
            is MetadataTag.YCbCrPositioning -> R.string.tag_y_cb_cr_positioning
            is MetadataTag.XResolution -> R.string.tag_x_resolution
            is MetadataTag.YResolution -> R.string.tag_y_resolution
            is MetadataTag.ResolutionUnit -> R.string.tag_resolution_unit
            is MetadataTag.StripOffsets -> R.string.tag_strip_offsets
            is MetadataTag.RowsPerStrip -> R.string.tag_rows_per_strip
            is MetadataTag.StripByteCounts -> R.string.tag_strip_byte_counts
            is MetadataTag.JpegInterchangeFormat -> R.string.tag_jpeg_interchange_format
            is MetadataTag.JpegInterchangeFormatLength -> R.string.tag_jpeg_interchange_format_length
            is MetadataTag.TransferFunction -> R.string.tag_transfer_function
            is MetadataTag.WhitePoint -> R.string.tag_white_point
            is MetadataTag.PrimaryChromaticities -> R.string.tag_primary_chromaticities
            is MetadataTag.YCbCrCoefficients -> R.string.tag_y_cb_cr_coefficients
            is MetadataTag.ReferenceBlackWhite -> R.string.tag_reference_black_white
            is MetadataTag.Datetime -> R.string.tag_datetime
            is MetadataTag.ImageDescription -> R.string.tag_image_description
            is MetadataTag.Make -> R.string.tag_make
            is MetadataTag.Model -> R.string.tag_model
            is MetadataTag.Software -> R.string.tag_software
            is MetadataTag.Artist -> R.string.tag_artist
            is MetadataTag.Copyright -> R.string.tag_copyright
            is MetadataTag.ExifVersion -> R.string.tag_exif_version
            is MetadataTag.FlashpixVersion -> R.string.tag_flashpix_version
            is MetadataTag.ColorSpace -> R.string.tag_color_space
            is MetadataTag.Gamma -> R.string.tag_gamma
            is MetadataTag.PixelXDimension -> R.string.tag_pixel_x_dimension
            is MetadataTag.PixelYDimension -> R.string.tag_pixel_y_dimension
            is MetadataTag.CompressedBitsPerPixel -> R.string.tag_compressed_bits_per_pixel
            is MetadataTag.MakerNote -> R.string.tag_maker_note
            is MetadataTag.UserComment -> R.string.tag_user_comment
            is MetadataTag.RelatedSoundFile -> R.string.tag_related_sound_file
            is MetadataTag.DatetimeOriginal -> R.string.tag_datetime_original
            is MetadataTag.DatetimeDigitized -> R.string.tag_datetime_digitized
            is MetadataTag.OffsetTime -> R.string.tag_offset_time
            is MetadataTag.OffsetTimeOriginal -> R.string.tag_offset_time_original
            is MetadataTag.OffsetTimeDigitized -> R.string.tag_offset_time_digitized
            is MetadataTag.SubsecTime -> R.string.tag_subsec_time
            is MetadataTag.SubsecTimeOriginal -> R.string.tag_subsec_time_original
            is MetadataTag.SubsecTimeDigitized -> R.string.tag_subsec_time_digitized
            is MetadataTag.ExposureTime -> R.string.tag_exposure_time
            is MetadataTag.FNumber -> R.string.tag_f_number
            is MetadataTag.ExposureProgram -> R.string.tag_exposure_program
            is MetadataTag.SpectralSensitivity -> R.string.tag_spectral_sensitivity
            is MetadataTag.PhotographicSensitivity -> R.string.tag_photographic_sensitivity
            is MetadataTag.Oecf -> R.string.tag_oecf
            is MetadataTag.SensitivityType -> R.string.tag_sensitivity_type
            is MetadataTag.StandardOutputSensitivity -> R.string.tag_standard_output_sensitivity
            is MetadataTag.RecommendedExposureIndex -> R.string.tag_recommended_exposure_index
            is MetadataTag.IsoSpeed -> R.string.tag_iso_speed
            is MetadataTag.IsoSpeedLatitudeYyy -> R.string.tag_iso_speed_latitude_yyy
            is MetadataTag.IsoSpeedLatitudeZzz -> R.string.tag_iso_speed_latitude_zzz
            is MetadataTag.ShutterSpeedValue -> R.string.tag_shutter_speed_value
            is MetadataTag.ApertureValue -> R.string.tag_aperture_value
            is MetadataTag.BrightnessValue -> R.string.tag_brightness_value
            is MetadataTag.ExposureBiasValue -> R.string.tag_exposure_bias_value
            is MetadataTag.MaxApertureValue -> R.string.tag_max_aperture_value
            is MetadataTag.SubjectDistance -> R.string.tag_subject_distance
            is MetadataTag.MeteringMode -> R.string.tag_metering_mode
            is MetadataTag.Flash -> R.string.tag_flash
            is MetadataTag.SubjectArea -> R.string.tag_subject_area
            is MetadataTag.FocalLength -> R.string.tag_focal_length
            is MetadataTag.FlashEnergy -> R.string.tag_flash_energy
            is MetadataTag.SpatialFrequencyResponse -> R.string.tag_spatial_frequency_response
            is MetadataTag.FocalPlaneXResolution -> R.string.tag_focal_plane_x_resolution
            is MetadataTag.FocalPlaneYResolution -> R.string.tag_focal_plane_y_resolution
            is MetadataTag.FocalPlaneResolutionUnit -> R.string.tag_focal_plane_resolution_unit
            is MetadataTag.SubjectLocation -> R.string.tag_subject_location
            is MetadataTag.ExposureIndex -> R.string.tag_exposure_index
            is MetadataTag.SensingMethod -> R.string.tag_sensing_method
            is MetadataTag.FileSource -> R.string.tag_file_source
            is MetadataTag.CfaPattern -> R.string.tag_cfa_pattern
            is MetadataTag.CustomRendered -> R.string.tag_custom_rendered
            is MetadataTag.ExposureMode -> R.string.tag_exposure_mode
            is MetadataTag.WhiteBalance -> R.string.tag_white_balance
            is MetadataTag.DigitalZoomRatio -> R.string.tag_digital_zoom_ratio
            is MetadataTag.FocalLengthIn35mmFilm -> R.string.tag_focal_length_in_35mm_film
            is MetadataTag.SceneCaptureType -> R.string.tag_scene_capture_type
            is MetadataTag.GainControl -> R.string.tag_gain_control
            is MetadataTag.Contrast -> R.string.tag_contrast
            is MetadataTag.Saturation -> R.string.tag_saturation
            is MetadataTag.Sharpness -> R.string.tag_sharpness
            is MetadataTag.DeviceSettingDescription -> R.string.tag_device_setting_description
            is MetadataTag.SubjectDistanceRange -> R.string.tag_subject_distance_range
            is MetadataTag.ImageUniqueId -> R.string.tag_image_unique_id
            is MetadataTag.CameraOwnerName -> R.string.tag_camera_owner_name
            is MetadataTag.BodySerialNumber -> R.string.tag_body_serial_number
            is MetadataTag.LensSpecification -> R.string.tag_lens_specification
            is MetadataTag.LensMake -> R.string.tag_lens_make
            is MetadataTag.LensModel -> R.string.tag_lens_model
            is MetadataTag.LensSerialNumber -> R.string.tag_lens_serial_number
            is MetadataTag.GpsVersionId -> R.string.tag_gps_version_id
            is MetadataTag.GpsLatitudeRef -> R.string.tag_gps_latitude_ref
            is MetadataTag.GpsLatitude -> R.string.tag_gps_latitude
            is MetadataTag.GpsLongitudeRef -> R.string.tag_gps_longitude_ref
            is MetadataTag.GpsLongitude -> R.string.tag_gps_longitude
            is MetadataTag.GpsAltitudeRef -> R.string.tag_gps_altitude_ref
            is MetadataTag.GpsAltitude -> R.string.tag_gps_altitude
            is MetadataTag.GpsTimestamp -> R.string.tag_gps_timestamp
            is MetadataTag.GpsSatellites -> R.string.tag_gps_satellites
            is MetadataTag.GpsStatus -> R.string.tag_gps_status
            is MetadataTag.GpsMeasureMode -> R.string.tag_gps_measure_mode
            is MetadataTag.GpsDop -> R.string.tag_gps_dop
            is MetadataTag.GpsSpeedRef -> R.string.tag_gps_speed_ref
            is MetadataTag.GpsSpeed -> R.string.tag_gps_speed
            is MetadataTag.GpsTrackRef -> R.string.tag_gps_track_ref
            is MetadataTag.GpsTrack -> R.string.tag_gps_track
            is MetadataTag.GpsImgDirectionRef -> R.string.tag_gps_img_direction_ref
            is MetadataTag.GpsImgDirection -> R.string.tag_gps_img_direction
            is MetadataTag.GpsMapDatum -> R.string.tag_gps_map_datum
            is MetadataTag.GpsDestLatitudeRef -> R.string.tag_gps_dest_latitude_ref
            is MetadataTag.GpsDestLatitude -> R.string.tag_gps_dest_latitude
            is MetadataTag.GpsDestLongitudeRef -> R.string.tag_gps_dest_longitude_ref
            is MetadataTag.GpsDestLongitude -> R.string.tag_gps_dest_longitude
            is MetadataTag.GpsDestBearingRef -> R.string.tag_gps_dest_bearing_ref
            is MetadataTag.GpsDestBearing -> R.string.tag_gps_dest_bearing
            is MetadataTag.GpsDestDistanceRef -> R.string.tag_gps_dest_distance_ref
            is MetadataTag.GpsDestDistance -> R.string.tag_gps_dest_distance
            is MetadataTag.GpsProcessingMethod -> R.string.tag_gps_processing_method
            is MetadataTag.GpsAreaInformation -> R.string.tag_gps_area_information
            is MetadataTag.GpsDatestamp -> R.string.tag_gps_datestamp
            is MetadataTag.GpsDifferential -> R.string.tag_gps_differential
            is MetadataTag.GpsHPositioningError -> R.string.tag_gps_h_positioning_error
            is MetadataTag.InteroperabilityIndex -> R.string.tag_interoperability_index
            is MetadataTag.DngVersion -> R.string.tag_dng_version
            is MetadataTag.DefaultCropSize -> R.string.tag_default_crop_size
            is MetadataTag.OrfPreviewImageStart -> R.string.tag_orf_preview_image_start
            is MetadataTag.OrfPreviewImageLength -> R.string.tag_orf_preview_image_length
            is MetadataTag.OrfAspectFrame -> R.string.tag_orf_aspect_frame
            is MetadataTag.Rw2SensorBottomBorder -> R.string.tag_rw2_sensor_bottom_border
            is MetadataTag.Rw2SensorLeftBorder -> R.string.tag_rw2_sensor_left_border
            is MetadataTag.Rw2SensorRightBorder -> R.string.tag_rw2_sensor_right_border
            is MetadataTag.Rw2SensorTopBorder -> R.string.tag_rw2_sensor_top_border
            is MetadataTag.Rw2Iso -> R.string.tag_rw2_iso
        }

    val MetadataTag.localizedName: String
        @Composable
        get() {
            val context = LocalContext.current
            return remember(this, context) {
                localizedName(context)
            }
        }

    private val possibleConfigs = mutableListOf<Bitmap.Config>().apply {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Bitmap.Config.RGBA_1010102)
        }
        if (SDK_INT >= Build.VERSION_CODES.O) {
            add(Bitmap.Config.RGBA_F16)
        }
        add(Bitmap.Config.ARGB_8888)
        add(Bitmap.Config.RGB_565)
    }

    fun getSuitableConfig(
        image: Bitmap? = null
    ): Bitmap.Config = image?.config?.takeIf {
        it in possibleConfigs
    } ?: Bitmap.Config.ARGB_8888

    private fun Uri.fileSize(context: Context): Long? {
        if (this.scheme == "content") {
            runCatching {
                context.contentResolver
                    .query(this, null, null, null, null, null)
                    .use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                            if (!cursor.isNull(sizeIndex)) {
                                return cursor.getLong(sizeIndex)
                            }
                        }
                    }
            }
        } else {
            runCatching {
                return this.toFile().length()
            }
        }
        return null
    }

    @Composable
    fun rememberFileSize(uri: Uri?): Long {
        val context = LocalContext.current

        return remember(uri, context) {
            derivedStateOf {
                uri?.fileSize(context) ?: 0L
            }
        }.value
    }

    @Composable
    fun rememberHumanFileSize(uri: Uri): String {
        val size = rememberFileSize(uri)

        return remember(size, uri) {
            derivedStateOf {
                humanFileSize(size)
            }
        }.value
    }

    @Composable
    fun rememberHumanFileSize(byteCount: Long): String {
        return remember(byteCount) {
            derivedStateOf {
                humanFileSize(byteCount)
            }
        }.value
    }

    object Dimens {
        const val MAX_IMAGE_SIZE = 8388607 * 16
    }

    fun String.restrict(with: Int): String {
        if (isEmpty()) return this

        return if ((this.toIntOrNull() ?: 0) >= with) with.toString()
        else if (this.isDigitsOnly() && (this.toIntOrNull() == null)) ""
        else this.trim()
            .filter {
                !listOf('-', '.', ',', ' ', "\n").contains(it)
            }
    }

    fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int
    ): Bitmap {
        if (width == this.width && height == this.height) return this

        return if (width < this.width && height < this.height) {
            BitmapCompat.createScaledBitmap(
                this,
                width,
                height,
                null,
                true
            )
        } else {
            this.scale(width, height)
        }
    }

    fun ImageInfo.haveChanges(original: Bitmap?): Boolean {
        if (original == null) return false
        return quality.qualityValue != 100 || rotationDegrees != 0f || isFlipped || width != original.width || height != original.height
    }

    val Bitmap.aspectRatio: Float get() = width / height.toFloat()

    val ImageBitmap.aspectRatio: Float get() = width / height.toFloat()

    val Drawable.aspectRatio: Float get() = intrinsicWidth / intrinsicHeight.toFloat()

    val Image.aspectRatio: Float get() = width / height.toFloat()

    val Bitmap.safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

    val ImageBitmap.safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

    val Image.safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

    val Drawable.safeAspectRatio: Float
        get() = aspectRatio
            .coerceAtLeast(0.005f)
            .coerceAtMost(1000f)

    val Bitmap.Config.isHardware: Boolean
        get() = SDK_INT >= 26 && this == Bitmap.Config.HARDWARE

    fun Bitmap.Config?.toSoftware(): Bitmap.Config {
        return if (this == null || isHardware) Bitmap.Config.ARGB_8888 else this
    }

    fun Bitmap.flexibleScale(max: Int): Bitmap {
        return runCatching {
            if (height >= width) {
                val aspectRatio = aspectRatio
                val targetWidth = (max * aspectRatio).toInt()
                scale(targetWidth, max)
            } else {
                val aspectRatio = 1f / aspectRatio
                val targetHeight = (max * aspectRatio).toInt()
                scale(max, targetHeight)
            }
        }.getOrNull() ?: this
    }

    fun Bitmap.applyPadding(padding: Int, paddingColor: Color = Color.White): Bitmap {
        val newWidth = this.width + padding * 2
        val newHeight = this.height + padding * 2
        return createBitmap(newWidth, newHeight, getSuitableConfig(this)).applyCanvas {
            drawColor(paddingColor.toArgb())
            drawBitmap(this@applyPadding, padding.toFloat(), padding.toFloat(), null)
        }
    }

}