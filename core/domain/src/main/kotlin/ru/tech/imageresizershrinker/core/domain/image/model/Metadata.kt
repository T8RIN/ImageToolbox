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

@file:Suppress("MemberVisibilityCanBePrivate")

package ru.tech.imageresizershrinker.core.domain.image.model

object Metadata {
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


    val metaTags = listOf(
        TAG_BITS_PER_SAMPLE,
        TAG_COMPRESSION,
        TAG_PHOTOMETRIC_INTERPRETATION,
        TAG_SAMPLES_PER_PIXEL,
        TAG_PLANAR_CONFIGURATION,
        TAG_Y_CB_CR_SUB_SAMPLING,
        TAG_Y_CB_CR_POSITIONING,
        TAG_X_RESOLUTION,
        TAG_Y_RESOLUTION,
        TAG_RESOLUTION_UNIT,
        TAG_STRIP_OFFSETS,
        TAG_ROWS_PER_STRIP,
        TAG_STRIP_BYTE_COUNTS,
        TAG_JPEG_INTERCHANGE_FORMAT,
        TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
        TAG_TRANSFER_FUNCTION,
        TAG_WHITE_POINT,
        TAG_PRIMARY_CHROMATICITIES,
        TAG_Y_CB_CR_COEFFICIENTS,
        TAG_REFERENCE_BLACK_WHITE,
        TAG_DATETIME,
        TAG_IMAGE_DESCRIPTION,
        TAG_MAKE,
        TAG_MODEL,
        TAG_SOFTWARE,
        TAG_ARTIST,
        TAG_COPYRIGHT,
        TAG_EXIF_VERSION,
        TAG_FLASHPIX_VERSION,
        TAG_COLOR_SPACE,
        TAG_GAMMA,
        TAG_PIXEL_X_DIMENSION,
        TAG_PIXEL_Y_DIMENSION,
        TAG_COMPRESSED_BITS_PER_PIXEL,
        TAG_MAKER_NOTE,
        TAG_USER_COMMENT,
        TAG_RELATED_SOUND_FILE,
        TAG_DATETIME_ORIGINAL,
        TAG_DATETIME_DIGITIZED,
        TAG_OFFSET_TIME,
        TAG_OFFSET_TIME_ORIGINAL,
        TAG_OFFSET_TIME_DIGITIZED,
        TAG_SUBSEC_TIME,
        TAG_SUBSEC_TIME_ORIGINAL,
        TAG_SUBSEC_TIME_DIGITIZED,
        TAG_EXPOSURE_TIME,
        TAG_F_NUMBER,
        TAG_EXPOSURE_PROGRAM,
        TAG_SPECTRAL_SENSITIVITY,
        TAG_PHOTOGRAPHIC_SENSITIVITY,
        TAG_OECF,
        TAG_SENSITIVITY_TYPE,
        TAG_STANDARD_OUTPUT_SENSITIVITY,
        TAG_RECOMMENDED_EXPOSURE_INDEX,
        TAG_ISO_SPEED,
        TAG_ISO_SPEED_LATITUDE_YYY,
        TAG_ISO_SPEED_LATITUDE_ZZZ,
        TAG_SHUTTER_SPEED_VALUE,
        TAG_APERTURE_VALUE,
        TAG_BRIGHTNESS_VALUE,
        TAG_EXPOSURE_BIAS_VALUE,
        TAG_MAX_APERTURE_VALUE,
        TAG_SUBJECT_DISTANCE,
        TAG_METERING_MODE,
        TAG_FLASH,
        TAG_SUBJECT_AREA,
        TAG_FOCAL_LENGTH,
        TAG_FLASH_ENERGY,
        TAG_SPATIAL_FREQUENCY_RESPONSE,
        TAG_FOCAL_PLANE_X_RESOLUTION,
        TAG_FOCAL_PLANE_Y_RESOLUTION,
        TAG_FOCAL_PLANE_RESOLUTION_UNIT,
        TAG_SUBJECT_LOCATION,
        TAG_EXPOSURE_INDEX,
        TAG_SENSING_METHOD,
        TAG_FILE_SOURCE,
        TAG_CFA_PATTERN,
        TAG_CUSTOM_RENDERED,
        TAG_EXPOSURE_MODE,
        TAG_WHITE_BALANCE,
        TAG_DIGITAL_ZOOM_RATIO,
        TAG_FOCAL_LENGTH_IN_35MM_FILM,
        TAG_SCENE_CAPTURE_TYPE,
        TAG_GAIN_CONTROL,
        TAG_CONTRAST,
        TAG_SATURATION,
        TAG_SHARPNESS,
        TAG_DEVICE_SETTING_DESCRIPTION,
        TAG_SUBJECT_DISTANCE_RANGE,
        TAG_IMAGE_UNIQUE_ID,
        TAG_CAMERA_OWNER_NAME,
        TAG_BODY_SERIAL_NUMBER,
        TAG_LENS_SPECIFICATION,
        TAG_LENS_MAKE,
        TAG_LENS_MODEL,
        TAG_LENS_SERIAL_NUMBER,
        TAG_GPS_VERSION_ID,
        TAG_GPS_LATITUDE_REF,
        TAG_GPS_LATITUDE,
        TAG_GPS_LONGITUDE_REF,
        TAG_GPS_LONGITUDE,
        TAG_GPS_ALTITUDE_REF,
        TAG_GPS_ALTITUDE,
        TAG_GPS_TIMESTAMP,
        TAG_GPS_SATELLITES,
        TAG_GPS_STATUS,
        TAG_GPS_MEASURE_MODE,
        TAG_GPS_DOP,
        TAG_GPS_SPEED_REF,
        TAG_GPS_SPEED,
        TAG_GPS_TRACK_REF,
        TAG_GPS_TRACK,
        TAG_GPS_IMG_DIRECTION_REF,
        TAG_GPS_IMG_DIRECTION,
        TAG_GPS_MAP_DATUM,
        TAG_GPS_DEST_LATITUDE_REF,
        TAG_GPS_DEST_LATITUDE,
        TAG_GPS_DEST_LONGITUDE_REF,
        TAG_GPS_DEST_LONGITUDE,
        TAG_GPS_DEST_BEARING_REF,
        TAG_GPS_DEST_BEARING,
        TAG_GPS_DEST_DISTANCE_REF,
        TAG_GPS_DEST_DISTANCE,
        TAG_GPS_PROCESSING_METHOD,
        TAG_GPS_AREA_INFORMATION,
        TAG_GPS_DATESTAMP,
        TAG_GPS_DIFFERENTIAL,
        TAG_GPS_H_POSITIONING_ERROR,
        TAG_INTEROPERABILITY_INDEX,
        TAG_DNG_VERSION,
        TAG_DEFAULT_CROP_SIZE,
        TAG_ORF_PREVIEW_IMAGE_START,
        TAG_ORF_PREVIEW_IMAGE_LENGTH,
        TAG_ORF_ASPECT_FRAME,
        TAG_RW2_SENSOR_BOTTOM_BORDER,
        TAG_RW2_SENSOR_LEFT_BORDER,
        TAG_RW2_SENSOR_RIGHT_BORDER,
        TAG_RW2_SENSOR_TOP_BORDER,
        TAG_RW2_ISO,
    )
}
