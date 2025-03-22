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

@file:Suppress("SpellCheckingInspection")

package ru.tech.imageresizershrinker.core.domain.image.model

sealed class MetadataTag(
    val key: String
) : Comparable<MetadataTag> {
    object BitsPerSample : MetadataTag(TAG_BITS_PER_SAMPLE)
    object Compression : MetadataTag(TAG_COMPRESSION)
    object PhotometricInterpretation : MetadataTag(TAG_PHOTOMETRIC_INTERPRETATION)
    object SamplesPerPixel : MetadataTag(TAG_SAMPLES_PER_PIXEL)
    object PlanarConfiguration : MetadataTag(TAG_PLANAR_CONFIGURATION)
    object YCbCrSubSampling : MetadataTag(TAG_Y_CB_CR_SUB_SAMPLING)
    object YCbCrPositioning : MetadataTag(TAG_Y_CB_CR_POSITIONING)
    object XResolution : MetadataTag(TAG_X_RESOLUTION)
    object YResolution : MetadataTag(TAG_Y_RESOLUTION)
    object ResolutionUnit : MetadataTag(TAG_RESOLUTION_UNIT)
    object StripOffsets : MetadataTag(TAG_STRIP_OFFSETS)
    object RowsPerStrip : MetadataTag(TAG_ROWS_PER_STRIP)
    object StripByteCounts : MetadataTag(TAG_STRIP_BYTE_COUNTS)
    object JpegInterchangeFormat : MetadataTag(TAG_JPEG_INTERCHANGE_FORMAT)
    object JpegInterchangeFormatLength : MetadataTag(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH)
    object TransferFunction : MetadataTag(TAG_TRANSFER_FUNCTION)
    object WhitePoint : MetadataTag(TAG_WHITE_POINT)
    object PrimaryChromaticities : MetadataTag(TAG_PRIMARY_CHROMATICITIES)
    object YCbCrCoefficients : MetadataTag(TAG_Y_CB_CR_COEFFICIENTS)
    object ReferenceBlackWhite : MetadataTag(TAG_REFERENCE_BLACK_WHITE)
    object Datetime : MetadataTag(TAG_DATETIME)
    object ImageDescription : MetadataTag(TAG_IMAGE_DESCRIPTION)
    object Make : MetadataTag(TAG_MAKE)
    object Model : MetadataTag(TAG_MODEL)
    object Software : MetadataTag(TAG_SOFTWARE)
    object Artist : MetadataTag(TAG_ARTIST)
    object Copyright : MetadataTag(TAG_COPYRIGHT)
    object ExifVersion : MetadataTag(TAG_EXIF_VERSION)
    object FlashpixVersion : MetadataTag(TAG_FLASHPIX_VERSION)
    object ColorSpace : MetadataTag(TAG_COLOR_SPACE)
    object Gamma : MetadataTag(TAG_GAMMA)
    object PixelXDimension : MetadataTag(TAG_PIXEL_X_DIMENSION)
    object PixelYDimension : MetadataTag(TAG_PIXEL_Y_DIMENSION)
    object CompressedBitsPerPixel : MetadataTag(TAG_COMPRESSED_BITS_PER_PIXEL)
    object MakerNote : MetadataTag(TAG_MAKER_NOTE)
    object UserComment : MetadataTag(TAG_USER_COMMENT)
    object RelatedSoundFile : MetadataTag(TAG_RELATED_SOUND_FILE)
    object DatetimeOriginal : MetadataTag(TAG_DATETIME_ORIGINAL)
    object DatetimeDigitized : MetadataTag(TAG_DATETIME_DIGITIZED)
    object OffsetTime : MetadataTag(TAG_OFFSET_TIME)
    object OffsetTimeOriginal : MetadataTag(TAG_OFFSET_TIME_ORIGINAL)
    object OffsetTimeDigitized : MetadataTag(TAG_OFFSET_TIME_DIGITIZED)
    object SubsecTime : MetadataTag(TAG_SUBSEC_TIME)
    object SubsecTimeOriginal : MetadataTag(TAG_SUBSEC_TIME_ORIGINAL)
    object SubsecTimeDigitized : MetadataTag(TAG_SUBSEC_TIME_DIGITIZED)
    object ExposureTime : MetadataTag(TAG_EXPOSURE_TIME)
    object FNumber : MetadataTag(TAG_F_NUMBER)
    object ExposureProgram : MetadataTag(TAG_EXPOSURE_PROGRAM)
    object SpectralSensitivity : MetadataTag(TAG_SPECTRAL_SENSITIVITY)
    object PhotographicSensitivity : MetadataTag(TAG_PHOTOGRAPHIC_SENSITIVITY)
    object Oecf : MetadataTag(TAG_OECF)
    object SensitivityType : MetadataTag(TAG_SENSITIVITY_TYPE)
    object StandardOutputSensitivity : MetadataTag(TAG_STANDARD_OUTPUT_SENSITIVITY)
    object RecommendedExposureIndex : MetadataTag(TAG_RECOMMENDED_EXPOSURE_INDEX)
    object IsoSpeed : MetadataTag(TAG_ISO_SPEED)
    object IsoSpeedLatitudeYyy : MetadataTag(TAG_ISO_SPEED_LATITUDE_YYY)
    object IsoSpeedLatitudeZzz : MetadataTag(TAG_ISO_SPEED_LATITUDE_ZZZ)
    object ShutterSpeedValue : MetadataTag(TAG_SHUTTER_SPEED_VALUE)
    object ApertureValue : MetadataTag(TAG_APERTURE_VALUE)
    object BrightnessValue : MetadataTag(TAG_BRIGHTNESS_VALUE)
    object ExposureBiasValue : MetadataTag(TAG_EXPOSURE_BIAS_VALUE)
    object MaxApertureValue : MetadataTag(TAG_MAX_APERTURE_VALUE)
    object SubjectDistance : MetadataTag(TAG_SUBJECT_DISTANCE)
    object MeteringMode : MetadataTag(TAG_METERING_MODE)
    object Flash : MetadataTag(TAG_FLASH)
    object SubjectArea : MetadataTag(TAG_SUBJECT_AREA)
    object FocalLength : MetadataTag(TAG_FOCAL_LENGTH)
    object FlashEnergy : MetadataTag(TAG_FLASH_ENERGY)
    object SpatialFrequencyResponse : MetadataTag(TAG_SPATIAL_FREQUENCY_RESPONSE)
    object FocalPlaneXResolution : MetadataTag(TAG_FOCAL_PLANE_X_RESOLUTION)
    object FocalPlaneYResolution : MetadataTag(TAG_FOCAL_PLANE_Y_RESOLUTION)
    object FocalPlaneResolutionUnit : MetadataTag(TAG_FOCAL_PLANE_RESOLUTION_UNIT)
    object SubjectLocation : MetadataTag(TAG_SUBJECT_LOCATION)
    object ExposureIndex : MetadataTag(TAG_EXPOSURE_INDEX)
    object SensingMethod : MetadataTag(TAG_SENSING_METHOD)
    object FileSource : MetadataTag(TAG_FILE_SOURCE)
    object CfaPattern : MetadataTag(TAG_CFA_PATTERN)
    object CustomRendered : MetadataTag(TAG_CUSTOM_RENDERED)
    object ExposureMode : MetadataTag(TAG_EXPOSURE_MODE)
    object WhiteBalance : MetadataTag(TAG_WHITE_BALANCE)
    object DigitalZoomRatio : MetadataTag(TAG_DIGITAL_ZOOM_RATIO)
    object FocalLengthIn35mmFilm : MetadataTag(TAG_FOCAL_LENGTH_IN_35MM_FILM)
    object SceneCaptureType : MetadataTag(TAG_SCENE_CAPTURE_TYPE)
    object GainControl : MetadataTag(TAG_GAIN_CONTROL)
    object Contrast : MetadataTag(TAG_CONTRAST)
    object Saturation : MetadataTag(TAG_SATURATION)
    object Sharpness : MetadataTag(TAG_SHARPNESS)
    object DeviceSettingDescription : MetadataTag(TAG_DEVICE_SETTING_DESCRIPTION)
    object SubjectDistanceRange : MetadataTag(TAG_SUBJECT_DISTANCE_RANGE)
    object ImageUniqueId : MetadataTag(TAG_IMAGE_UNIQUE_ID)
    object CameraOwnerName : MetadataTag(TAG_CAMERA_OWNER_NAME)
    object BodySerialNumber : MetadataTag(TAG_BODY_SERIAL_NUMBER)
    object LensSpecification : MetadataTag(TAG_LENS_SPECIFICATION)
    object LensMake : MetadataTag(TAG_LENS_MAKE)
    object LensModel : MetadataTag(TAG_LENS_MODEL)
    object LensSerialNumber : MetadataTag(TAG_LENS_SERIAL_NUMBER)
    object GpsVersionId : MetadataTag(TAG_GPS_VERSION_ID)
    object GpsLatitudeRef : MetadataTag(TAG_GPS_LATITUDE_REF)
    object GpsLatitude : MetadataTag(TAG_GPS_LATITUDE)
    object GpsLongitudeRef : MetadataTag(TAG_GPS_LONGITUDE_REF)
    object GpsLongitude : MetadataTag(TAG_GPS_LONGITUDE)
    object GpsAltitudeRef : MetadataTag(TAG_GPS_ALTITUDE_REF)
    object GpsAltitude : MetadataTag(TAG_GPS_ALTITUDE)
    object GpsTimestamp : MetadataTag(TAG_GPS_TIMESTAMP)
    object GpsSatellites : MetadataTag(TAG_GPS_SATELLITES)
    object GpsStatus : MetadataTag(TAG_GPS_STATUS)
    object GpsMeasureMode : MetadataTag(TAG_GPS_MEASURE_MODE)
    object GpsDop : MetadataTag(TAG_GPS_DOP)
    object GpsSpeedRef : MetadataTag(TAG_GPS_SPEED_REF)
    object GpsSpeed : MetadataTag(TAG_GPS_SPEED)
    object GpsTrackRef : MetadataTag(TAG_GPS_TRACK_REF)
    object GpsTrack : MetadataTag(TAG_GPS_TRACK)
    object GpsImgDirectionRef : MetadataTag(TAG_GPS_IMG_DIRECTION_REF)
    object GpsImgDirection : MetadataTag(TAG_GPS_IMG_DIRECTION)
    object GpsMapDatum : MetadataTag(TAG_GPS_MAP_DATUM)
    object GpsDestLatitudeRef : MetadataTag(TAG_GPS_DEST_LATITUDE_REF)
    object GpsDestLatitude : MetadataTag(TAG_GPS_DEST_LATITUDE)
    object GpsDestLongitudeRef : MetadataTag(TAG_GPS_DEST_LONGITUDE_REF)
    object GpsDestLongitude : MetadataTag(TAG_GPS_DEST_LONGITUDE)
    object GpsDestBearingRef : MetadataTag(TAG_GPS_DEST_BEARING_REF)
    object GpsDestBearing : MetadataTag(TAG_GPS_DEST_BEARING)
    object GpsDestDistanceRef : MetadataTag(TAG_GPS_DEST_DISTANCE_REF)
    object GpsDestDistance : MetadataTag(TAG_GPS_DEST_DISTANCE)
    object GpsProcessingMethod : MetadataTag(TAG_GPS_PROCESSING_METHOD)
    object GpsAreaInformation : MetadataTag(TAG_GPS_AREA_INFORMATION)
    object GpsDatestamp : MetadataTag(TAG_GPS_DATESTAMP)
    object GpsDifferential : MetadataTag(TAG_GPS_DIFFERENTIAL)
    object GpsHPositioningError : MetadataTag(TAG_GPS_H_POSITIONING_ERROR)
    object InteroperabilityIndex : MetadataTag(TAG_INTEROPERABILITY_INDEX)
    object DngVersion : MetadataTag(TAG_DNG_VERSION)
    object DefaultCropSize : MetadataTag(TAG_DEFAULT_CROP_SIZE)
    object OrfPreviewImageStart : MetadataTag(TAG_ORF_PREVIEW_IMAGE_START)
    object OrfPreviewImageLength : MetadataTag(TAG_ORF_PREVIEW_IMAGE_LENGTH)
    object OrfAspectFrame : MetadataTag(TAG_ORF_ASPECT_FRAME)
    object Rw2SensorBottomBorder : MetadataTag(TAG_RW2_SENSOR_BOTTOM_BORDER)
    object Rw2SensorLeftBorder : MetadataTag(TAG_RW2_SENSOR_LEFT_BORDER)
    object Rw2SensorRightBorder : MetadataTag(TAG_RW2_SENSOR_RIGHT_BORDER)
    object Rw2SensorTopBorder : MetadataTag(TAG_RW2_SENSOR_TOP_BORDER)
    object Rw2Iso : MetadataTag(TAG_RW2_ISO)

    override fun compareTo(other: MetadataTag): Int = key.compareTo(other.key)

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (other !is MetadataTag) return false
        return other.key == key
    }

    override fun hashCode(): Int = key.hashCode()

    companion object {
        const val TAG_BITS_PER_SAMPLE: String = "BitsPerSample"
        const val TAG_COMPRESSION: String = "Compression"
        const val TAG_PHOTOMETRIC_INTERPRETATION: String = "PhotometricInterpretation"
        const val TAG_SAMPLES_PER_PIXEL: String = "SamplesPerPixel"
        const val TAG_PLANAR_CONFIGURATION: String = "PlanarConfiguration"
        const val TAG_Y_CB_CR_SUB_SAMPLING: String = "YCbCrSubSampling"
        const val TAG_Y_CB_CR_POSITIONING: String = "YCbCrPositioning"
        const val TAG_X_RESOLUTION: String = "XResolution"
        const val TAG_Y_RESOLUTION: String = "YResolution"
        const val TAG_RESOLUTION_UNIT: String = "ResolutionUnit"
        const val TAG_STRIP_OFFSETS: String = "StripOffsets"
        const val TAG_ROWS_PER_STRIP: String = "RowsPerStrip"
        const val TAG_STRIP_BYTE_COUNTS: String = "StripByteCounts"
        const val TAG_JPEG_INTERCHANGE_FORMAT: String = "JPEGInterchangeFormat"
        const val TAG_JPEG_INTERCHANGE_FORMAT_LENGTH: String = "JPEGInterchangeFormatLength"
        const val TAG_TRANSFER_FUNCTION: String = "TransferFunction"
        const val TAG_WHITE_POINT: String = "WhitePoint"
        const val TAG_PRIMARY_CHROMATICITIES: String = "PrimaryChromaticities"
        const val TAG_Y_CB_CR_COEFFICIENTS: String = "YCbCrCoefficients"
        const val TAG_REFERENCE_BLACK_WHITE: String = "ReferenceBlackWhite"
        const val TAG_DATETIME: String = "DateTime"
        const val TAG_IMAGE_DESCRIPTION: String = "ImageDescription"
        const val TAG_MAKE: String = "Make"
        const val TAG_MODEL: String = "Model"
        const val TAG_SOFTWARE: String = "Software"
        const val TAG_ARTIST: String = "Artist"
        const val TAG_COPYRIGHT: String = "Copyright"
        const val TAG_EXIF_VERSION: String = "ExifVersion"
        const val TAG_FLASHPIX_VERSION: String = "FlashpixVersion"
        const val TAG_COLOR_SPACE: String = "ColorSpace"
        const val TAG_GAMMA: String = "Gamma"
        const val TAG_PIXEL_X_DIMENSION: String = "PixelXDimension"
        const val TAG_PIXEL_Y_DIMENSION: String = "PixelYDimension"
        const val TAG_COMPRESSED_BITS_PER_PIXEL: String = "CompressedBitsPerPixel"
        const val TAG_MAKER_NOTE: String = "MakerNote"
        const val TAG_USER_COMMENT: String = "UserComment"
        const val TAG_RELATED_SOUND_FILE: String = "RelatedSoundFile"
        const val TAG_DATETIME_ORIGINAL: String = "DateTimeOriginal"
        const val TAG_DATETIME_DIGITIZED: String = "DateTimeDigitized"
        const val TAG_OFFSET_TIME: String = "OffsetTime"
        const val TAG_OFFSET_TIME_ORIGINAL: String = "OffsetTimeOriginal"
        const val TAG_OFFSET_TIME_DIGITIZED: String = "OffsetTimeDigitized"
        const val TAG_SUBSEC_TIME: String = "SubSecTime"
        const val TAG_SUBSEC_TIME_ORIGINAL: String = "SubSecTimeOriginal"
        const val TAG_SUBSEC_TIME_DIGITIZED: String = "SubSecTimeDigitized"
        const val TAG_EXPOSURE_TIME: String = "ExposureTime"
        const val TAG_F_NUMBER: String = "FNumber"
        const val TAG_EXPOSURE_PROGRAM: String = "ExposureProgram"
        const val TAG_SPECTRAL_SENSITIVITY: String = "SpectralSensitivity"
        const val TAG_PHOTOGRAPHIC_SENSITIVITY: String = "PhotographicSensitivity"
        const val TAG_OECF: String = "OECF"
        const val TAG_SENSITIVITY_TYPE: String = "SensitivityType"
        const val TAG_STANDARD_OUTPUT_SENSITIVITY: String = "StandardOutputSensitivity"
        const val TAG_RECOMMENDED_EXPOSURE_INDEX: String = "RecommendedExposureIndex"
        const val TAG_ISO_SPEED: String = "ISOSpeed"
        const val TAG_ISO_SPEED_LATITUDE_YYY: String = "ISOSpeedLatitudeyyy"
        const val TAG_ISO_SPEED_LATITUDE_ZZZ: String = "ISOSpeedLatitudezzz"
        const val TAG_SHUTTER_SPEED_VALUE: String = "ShutterSpeedValue"
        const val TAG_APERTURE_VALUE: String = "ApertureValue"
        const val TAG_BRIGHTNESS_VALUE: String = "BrightnessValue"
        const val TAG_EXPOSURE_BIAS_VALUE: String = "ExposureBiasValue"
        const val TAG_MAX_APERTURE_VALUE: String = "MaxApertureValue"
        const val TAG_SUBJECT_DISTANCE: String = "SubjectDistance"
        const val TAG_METERING_MODE: String = "MeteringMode"
        const val TAG_FLASH: String = "Flash"
        const val TAG_SUBJECT_AREA: String = "SubjectArea"
        const val TAG_FOCAL_LENGTH: String = "FocalLength"
        const val TAG_FLASH_ENERGY: String = "FlashEnergy"
        const val TAG_SPATIAL_FREQUENCY_RESPONSE: String = "SpatialFrequencyResponse"
        const val TAG_FOCAL_PLANE_X_RESOLUTION: String = "FocalPlaneXResolution"
        const val TAG_FOCAL_PLANE_Y_RESOLUTION: String = "FocalPlaneYResolution"
        const val TAG_FOCAL_PLANE_RESOLUTION_UNIT: String = "FocalPlaneResolutionUnit"
        const val TAG_SUBJECT_LOCATION: String = "SubjectLocation"
        const val TAG_EXPOSURE_INDEX: String = "ExposureIndex"
        const val TAG_SENSING_METHOD: String = "SensingMethod"
        const val TAG_FILE_SOURCE: String = "FileSource"
        const val TAG_CFA_PATTERN: String = "CFAPattern"
        const val TAG_CUSTOM_RENDERED: String = "CustomRendered"
        const val TAG_EXPOSURE_MODE: String = "ExposureMode"
        const val TAG_WHITE_BALANCE: String = "WhiteBalance"
        const val TAG_DIGITAL_ZOOM_RATIO: String = "DigitalZoomRatio"
        const val TAG_FOCAL_LENGTH_IN_35MM_FILM: String = "FocalLengthIn35mmFilm"
        const val TAG_SCENE_CAPTURE_TYPE: String = "SceneCaptureType"
        const val TAG_GAIN_CONTROL: String = "GainControl"
        const val TAG_CONTRAST: String = "Contrast"
        const val TAG_SATURATION: String = "Saturation"
        const val TAG_SHARPNESS: String = "Sharpness"
        const val TAG_DEVICE_SETTING_DESCRIPTION: String = "DeviceSettingDescription"
        const val TAG_SUBJECT_DISTANCE_RANGE: String = "SubjectDistanceRange"
        const val TAG_IMAGE_UNIQUE_ID: String = "ImageUniqueID"
        const val TAG_CAMERA_OWNER_NAME: String = "CameraOwnerName"
        const val TAG_BODY_SERIAL_NUMBER: String = "BodySerialNumber"
        const val TAG_LENS_SPECIFICATION: String = "LensSpecification"
        const val TAG_LENS_MAKE: String = "LensMake"
        const val TAG_LENS_MODEL: String = "LensModel"
        const val TAG_LENS_SERIAL_NUMBER: String = "LensSerialNumber"
        const val TAG_GPS_VERSION_ID: String = "GPSVersionID"
        const val TAG_GPS_LATITUDE_REF: String = "GPSLatitudeRef"
        const val TAG_GPS_LATITUDE: String = "GPSLatitude"
        const val TAG_GPS_LONGITUDE_REF: String = "GPSLongitudeRef"
        const val TAG_GPS_LONGITUDE: String = "GPSLongitude"
        const val TAG_GPS_ALTITUDE_REF: String = "GPSAltitudeRef"
        const val TAG_GPS_ALTITUDE: String = "GPSAltitude"
        const val TAG_GPS_TIMESTAMP: String = "GPSTimeStamp"
        const val TAG_GPS_SATELLITES: String = "GPSSatellites"
        const val TAG_GPS_STATUS: String = "GPSStatus"
        const val TAG_GPS_MEASURE_MODE: String = "GPSMeasureMode"
        const val TAG_GPS_DOP: String = "GPSDOP"
        const val TAG_GPS_SPEED_REF: String = "GPSSpeedRef"
        const val TAG_GPS_SPEED: String = "GPSSpeed"
        const val TAG_GPS_TRACK_REF: String = "GPSTrackRef"
        const val TAG_GPS_TRACK: String = "GPSTrack"
        const val TAG_GPS_IMG_DIRECTION_REF: String = "GPSImgDirectionRef"
        const val TAG_GPS_IMG_DIRECTION: String = "GPSImgDirection"
        const val TAG_GPS_MAP_DATUM: String = "GPSMapDatum"
        const val TAG_GPS_DEST_LATITUDE_REF: String = "GPSDestLatitudeRef"
        const val TAG_GPS_DEST_LATITUDE: String = "GPSDestLatitude"
        const val TAG_GPS_DEST_LONGITUDE_REF: String = "GPSDestLongitudeRef"
        const val TAG_GPS_DEST_LONGITUDE: String = "GPSDestLongitude"
        const val TAG_GPS_DEST_BEARING_REF: String = "GPSDestBearingRef"
        const val TAG_GPS_DEST_BEARING: String = "GPSDestBearing"
        const val TAG_GPS_DEST_DISTANCE_REF: String = "GPSDestDistanceRef"
        const val TAG_GPS_DEST_DISTANCE: String = "GPSDestDistance"
        const val TAG_GPS_PROCESSING_METHOD: String = "GPSProcessingMethod"
        const val TAG_GPS_AREA_INFORMATION: String = "GPSAreaInformation"
        const val TAG_GPS_DATESTAMP: String = "GPSDateStamp"
        const val TAG_GPS_DIFFERENTIAL: String = "GPSDifferential"
        const val TAG_GPS_H_POSITIONING_ERROR: String = "GPSHPositioningError"
        const val TAG_INTEROPERABILITY_INDEX: String = "InteroperabilityIndex"
        const val TAG_DNG_VERSION: String = "DNGVersion"
        const val TAG_DEFAULT_CROP_SIZE: String = "DefaultCropSize"
        const val TAG_ORF_PREVIEW_IMAGE_START: String = "PreviewImageStart"
        const val TAG_ORF_PREVIEW_IMAGE_LENGTH: String = "PreviewImageLength"
        const val TAG_ORF_ASPECT_FRAME: String = "AspectFrame"
        const val TAG_RW2_SENSOR_BOTTOM_BORDER: String = "SensorBottomBorder"
        const val TAG_RW2_SENSOR_LEFT_BORDER: String = "SensorLeftBorder"
        const val TAG_RW2_SENSOR_RIGHT_BORDER: String = "SensorRightBorder"
        const val TAG_RW2_SENSOR_TOP_BORDER: String = "SensorTopBorder"
        const val TAG_RW2_ISO: String = "ISO"

        val entries by lazy {
            listOf(
                BitsPerSample,
                Compression,
                PhotometricInterpretation,
                SamplesPerPixel,
                PlanarConfiguration,
                YCbCrSubSampling,
                YCbCrPositioning,
                XResolution,
                YResolution,
                ResolutionUnit,
                StripOffsets,
                RowsPerStrip,
                StripByteCounts,
                JpegInterchangeFormat,
                JpegInterchangeFormatLength,
                TransferFunction,
                WhitePoint,
                PrimaryChromaticities,
                YCbCrCoefficients,
                ReferenceBlackWhite,
                Datetime,
                ImageDescription,
                Make,
                Model,
                Software,
                Artist,
                Copyright,
                ExifVersion,
                FlashpixVersion,
                ColorSpace,
                Gamma,
                PixelXDimension,
                PixelYDimension,
                CompressedBitsPerPixel,
                MakerNote,
                UserComment,
                RelatedSoundFile,
                DatetimeOriginal,
                DatetimeDigitized,
                OffsetTime,
                OffsetTimeOriginal,
                OffsetTimeDigitized,
                SubsecTime,
                SubsecTimeOriginal,
                SubsecTimeDigitized,
                ExposureTime,
                FNumber,
                ExposureProgram,
                SpectralSensitivity,
                PhotographicSensitivity,
                Oecf,
                SensitivityType,
                StandardOutputSensitivity,
                RecommendedExposureIndex,
                IsoSpeed,
                IsoSpeedLatitudeYyy,
                IsoSpeedLatitudeZzz,
                ShutterSpeedValue,
                ApertureValue,
                BrightnessValue,
                ExposureBiasValue,
                MaxApertureValue,
                SubjectDistance,
                MeteringMode,
                Flash,
                SubjectArea,
                FocalLength,
                FlashEnergy,
                SpatialFrequencyResponse,
                FocalPlaneXResolution,
                FocalPlaneYResolution,
                FocalPlaneResolutionUnit,
                SubjectLocation,
                ExposureIndex,
                SensingMethod,
                FileSource,
                CfaPattern,
                CustomRendered,
                ExposureMode,
                WhiteBalance,
                DigitalZoomRatio,
                FocalLengthIn35mmFilm,
                SceneCaptureType,
                GainControl,
                Contrast,
                Saturation,
                Sharpness,
                DeviceSettingDescription,
                SubjectDistanceRange,
                ImageUniqueId,
                CameraOwnerName,
                BodySerialNumber,
                LensSpecification,
                LensMake,
                LensModel,
                LensSerialNumber,
                GpsVersionId,
                GpsLatitudeRef,
                GpsLatitude,
                GpsLongitudeRef,
                GpsLongitude,
                GpsAltitudeRef,
                GpsAltitude,
                GpsTimestamp,
                GpsSatellites,
                GpsStatus,
                GpsMeasureMode,
                GpsDop,
                GpsSpeedRef,
                GpsSpeed,
                GpsTrackRef,
                GpsTrack,
                GpsImgDirectionRef,
                GpsImgDirection,
                GpsMapDatum,
                GpsDestLatitudeRef,
                GpsDestLatitude,
                GpsDestLongitudeRef,
                GpsDestLongitude,
                GpsDestBearingRef,
                GpsDestBearing,
                GpsDestDistanceRef,
                GpsDestDistance,
                GpsProcessingMethod,
                GpsAreaInformation,
                GpsDatestamp,
                GpsDifferential,
                GpsHPositioningError,
                InteroperabilityIndex,
                DngVersion,
                DefaultCropSize,
                OrfPreviewImageStart,
                OrfPreviewImageLength,
                OrfAspectFrame,
                Rw2SensorBottomBorder,
                Rw2SensorLeftBorder,
                Rw2SensorRightBorder,
                Rw2SensorTopBorder,
                Rw2Iso,
            )
        }
    }
}
