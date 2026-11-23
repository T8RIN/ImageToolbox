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

package com.t8rin.imagetoolbox.feature.delete_exif.domain.model

import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random

internal enum class ExifReplacementPreset(private val camera: Camera? = null) {
    Custom,
    Smartphone(
        phone(
            name = "Pixel 7",
            make = "Google",
            focalLength = 681,
            equivalentFocalLength = 24,
            aperture = 185,
            releaseDate = "2022-10-13",
            lensName = "Pixel 7 back camera 6.81mm f/1.85",
            software = "HDR+"
        )
    ),
    Dslr,
    Mirrorless,
    Compact,
    ActionCamera,
    RandomPhone,
    RandomCamera,
    FujifilmX100VI(
        Camera(
            name = "Fujifilm X100VI",
            make = "FUJIFILM",
            model = "X100VI",
            lens = Lens("23mm f/2", listOf(2300), 200, 35.0 / 23),
            earliestDate = LocalDateTime.of(2024, 3, 1, 0, 0)
        )
    ),
    RicohGrIII(
        Camera(
            make = "RICOH IMAGING COMPANY, LTD.",
            model = "RICOH GR III",
            lens = Lens("GR LENS 18.3mm F2.8", listOf(1830), 280, 28.0 / 18.3),
            earliestDate = LocalDateTime.of(2019, 3, 31, 0, 0)
        )
    ),
    CanonEosR6MarkII(
        Camera(
            name = "Canon EOS R6 Mark II",
            make = "Canon",
            model = "Canon EOS R6m2",
            lens = Lens("RF24-105mm F4 L IS USM", listOf(2400, 3500, 5000, 7000, 10500), 400, 1.0),
            earliestDate = LocalDateTime.of(2022, 11, 30, 0, 0)
        )
    ),
    OmSystemOm1(
        Camera(
            name = "OM System OM-1",
            make = "OM Digital Solutions",
            model = "OM-1",
            lens = Lens(
                name = "OLYMPUS M.12-40mm F2.8",
                focalLengths = listOf(1200, 1700, 2500, 3500, 4000),
                aperture = 280,
                cropFactor = 2.0,
                make = "OLYMPUS"
            ),
            earliestDate = LocalDateTime.of(2022, 3, 31, 0, 0)
        )
    ),
    GoProHero12(
        Camera(
            name = "GoPro HERO12 Black",
            make = "GoPro",
            model = "HERO12 Black",
            lens = Lens(null, listOf(271), 250, 15.0 / 2.71, fixedAperture = true),
            earliestDate = LocalDateTime.of(2023, 9, 13, 0, 0)
        )
    ),
    DjiMini4Pro(
        Camera(
            name = "DJI Mini 4 Pro",
            make = "DJI",
            model = "FC8482",
            lens = Lens(null, listOf(672), 170, 24.0 / 6.72, fixedAperture = true),
            earliestDate = LocalDateTime.of(2023, 9, 25, 0, 0)
        )
    ),
    Pixel6a(
        phone(
            name = "Pixel 6a",
            make = "Google",
            focalLength = 438,
            equivalentFocalLength = 27,
            aperture = 173,
            releaseDate = "2022-07-28"
        )
    ),
    Pixel8Pro(
        phone(
            name = "Pixel 8 Pro",
            make = "Google",
            focalLength = 690,
            equivalentFocalLength = 24,
            aperture = 168,
            releaseDate = "2023-10-12",
            lensName = "Pixel 8 Pro back camera 6.9mm f/1.68"
        )
    ),
    Pixel8a(
        phone(
            name = "Pixel 8a",
            make = "Google",
            focalLength = 543,
            equivalentFocalLength = 25,
            aperture = 189,
            releaseDate = "2024-05-14"
        )
    ),
    Pixel9(
        phone(
            name = "Pixel 9",
            make = "Google",
            focalLength = 690,
            equivalentFocalLength = 24,
            aperture = 168,
            releaseDate = "2024-08-22"
        )
    ),
    Pixel10Pro(
        phone(
            name = "Pixel 10 Pro",
            make = "Google",
            focalLength = 690,
            equivalentFocalLength = 24,
            aperture = 168,
            releaseDate = "2025-08-28",
            lensName = "Pixel 10 Pro back camera 6.9mm f/1.68"
        )
    ),
    IPhone13(
        phone(
            name = "iPhone 13",
            make = "Apple",
            focalLength = 510,
            equivalentFocalLength = 26,
            aperture = 160,
            releaseDate = "2023-01-23",
            lensName = "iPhone 13 back dual camera 5.1mm f/1.6",
            software = "16.3"
        )
    ),
    IPhone14(
        phone(
            name = "iPhone 14",
            make = "Apple",
            focalLength = 570,
            equivalentFocalLength = 26,
            aperture = 150,
            releaseDate = "2022-09-16",
            lensName = "iPhone 14 back dual wide camera 5.7mm f/1.5"
        )
    ),
    IPhone15(
        phone(
            name = "iPhone 15",
            make = "Apple",
            focalLength = 596,
            equivalentFocalLength = 26,
            aperture = 160,
            releaseDate = "2023-09-22"
        )
    ),
    IPhone16Pro(
        phone(
            name = "iPhone 16 Pro",
            make = "Apple",
            focalLength = 677,
            equivalentFocalLength = 24,
            aperture = 178,
            releaseDate = "2024-09-20"
        )
    ),
    IPhone17Pro(
        phone(
            name = "iPhone 17 Pro",
            make = "Apple",
            focalLength = 677,
            equivalentFocalLength = 24,
            aperture = 178,
            releaseDate = "2025-09-19",
            lensName = "iPhone 17 Pro back triple camera 6.765mm f/1.78"
        )
    ),
    IPhoneSe3(
        phone(
            name = "iPhone SE 3",
            make = "Apple",
            focalLength = 399,
            equivalentFocalLength = 28,
            aperture = 180,
            releaseDate = "2022-03-18",
            model = "iPhone SE (3rd generation)"
        )
    ),
    GalaxyA54(
        phone(
            name = "Galaxy A54",
            make = "samsung",
            focalLength = 554,
            equivalentFocalLength = 23,
            aperture = 180,
            releaseDate = "2023-03-31",
            model = "SM-A546B"
        )
    ),
    GalaxyS23(
        phone(
            name = "Galaxy S23",
            make = "samsung",
            focalLength = 540,
            equivalentFocalLength = 23,
            aperture = 180,
            releaseDate = "2023-03-01",
            model = "SM-S911B",
            lensName = "Galaxy S23 rear camera 5.4mm f/1.8",
            software = "S911BXXU1AWBD"
        )
    ),
    GalaxyS24(
        phone(
            name = "Galaxy S24",
            make = "samsung",
            focalLength = 540,
            equivalentFocalLength = 23,
            aperture = 180,
            releaseDate = "2024-01-31",
            model = "SM-S921B",
            lensName = "Samsung Galaxy S24 Rear Wide Camera"
        )
    ),
    GalaxyS24Ultra(
        phone(
            name = "Galaxy S24 Ultra",
            make = "samsung",
            focalLength = 630,
            equivalentFocalLength = 23,
            aperture = 170,
            releaseDate = "2024-01-31",
            model = "SM-S928B"
        )
    ),
    GalaxyS25Ultra(
        phone(
            name = "Galaxy S25 Ultra",
            make = "samsung",
            focalLength = 630,
            equivalentFocalLength = 23,
            aperture = 170,
            releaseDate = "2025-02-07"
        )
    ),
    Xiaomi14(
        phone(
            name = "Xiaomi 14",
            make = "Xiaomi",
            focalLength = 655,
            equivalentFocalLength = 23,
            aperture = 160,
            releaseDate = "2024-02-25",
            model = "23127PN0CG",
            lensName = "Xiaomi 14 Rear Wide Camera"
        )
    ),
    Xiaomi14Ultra(
        phone(
            name = "Xiaomi 14 Ultra",
            make = "Xiaomi",
            focalLength = 872,
            equivalentFocalLength = 23,
            aperture = 163,
            releaseDate = "2024-02-25",
            model = "24030PN60G"
        )
    ),
    OnePlus12(
        phone(
            name = "OnePlus 12",
            make = "OnePlus",
            focalLength = 606,
            equivalentFocalLength = 23,
            aperture = 160,
            releaseDate = "2024-01-23"
        )
    ),
    Xperia1V(
        phone(
            name = "Xperia 1 V",
            make = "Sony",
            focalLength = 608,
            equivalentFocalLength = 24,
            aperture = 190,
            releaseDate = "2023-06-01",
            model = "XQ-DQ72"
        )
    ),
    VivoX200Pro(
        phone(
            name = "vivo X200 Pro",
            make = "vivo",
            focalLength = 654,
            equivalentFocalLength = 23,
            aperture = 157,
            releaseDate = "2024-12-12",
            software = "MediaTek Camera Application"
        )
    ),
    OppoFindX8(
        phone(
            name = "OPPO Find X8",
            make = "OPPO",
            focalLength = 559,
            equivalentFocalLength = 24,
            aperture = 180,
            releaseDate = "2024-11-21"
        )
    ),
    MotorolaEdge50Pro(
        phone(
            name = "Motorola Edge 50 Pro",
            make = "motorola",
            focalLength = 590,
            equivalentFocalLength = 25,
            aperture = 140,
            releaseDate = "2024-04-16",
            model = "motorola edge 50 pro"
        )
    ),
    HuaweiPura70Ultra(
        phone(
            name = "HUAWEI Pura 70 Ultra",
            make = "HUAWEI",
            focalLength = 850,
            equivalentFocalLength = 27,
            aperture = 280,
            releaseDate = "2024-04-18"
        )
    ),
    NothingPhone2(
        phone(
            name = "Nothing Phone (2)",
            make = "Nothing",
            focalLength = 559,
            equivalentFocalLength = 23,
            aperture = 188,
            releaseDate = "2023-07-17"
        )
    ),
    NothingPhone3a(
        phone(
            name = "Nothing Phone (3a)",
            make = "Nothing",
            focalLength = 556,
            equivalentFocalLength = 24,
            aperture = 188,
            releaseDate = "2025-03-31"
        )
    ),
    AsusZenfone10(
        phone(
            name = "ASUS Zenfone 10",
            make = "ASUS",
            focalLength = 553,
            equivalentFocalLength = 24,
            aperture = 190,
            releaseDate = "2023-06-29",
            model = "ASUS_AI2302"
        )
    );

    val cameraName get() = camera?.name

    fun generateTags(
        settings: ExifReplacementSettings,
        index: Int
    ): Map<MetadataTag, String> {
        val random = Random(settings.seed + if (settings.sameForBatch) 0 else index.toLong())
        val camera = camera ?: when (this) {
            Dslr -> dslrs.random(random)
            Mirrorless -> mirrorless
            Compact -> compact
            ActionCamera -> actionCamera
            RandomPhone -> phones.random(random)
            RandomCamera -> cameras.random(random)
            else -> return emptyMap()
        }
        val lighting = settings.lighting.takeUnless { it == ExifLighting.Auto }
            ?: ExifLighting.entries.drop(1).random(random)
        val lens = camera.lens
        val aperture = if (lens.fixedAperture) lens.aperture else {
            apertureStops.filter { it >= lens.aperture && it <= lighting.maxAperture }
                .random(random)
        }
        val focalLength = lens.focalLengths.random(random)
        val start =
            maxOf(settings.now.minusYears(3), camera.earliestDate).coerceAtMost(settings.now)
        val date = settings.captureTime ?: start.plusSeconds(
            random.nextLong(ChronoUnit.SECONDS.between(start, settings.now) + 1)
        )
        val timestamp = date.format(dateFormat)
        val shutterSpeed = lighting.shutterSpeeds.random(random)
        val iso = lighting.isoValues.random(random).toString()

        return buildMap {
            put(MetadataTag.Make, camera.make)
            put(MetadataTag.Model, camera.model)
            if (settings.includeSoftware) camera.software?.let { put(MetadataTag.Software, it) }
            put(MetadataTag.LensMake, lens.make ?: camera.make)
            lens.name?.let { put(MetadataTag.LensModel, it) }
            put(
                MetadataTag.LensSpecification,
                listOf(
                    lens.focalLengths.min(),
                    lens.focalLengths.max(),
                    lens.wideAperture,
                    lens.aperture
                ).joinToString(",") { "$it/100" }
            )
            put(MetadataTag.Datetime, timestamp)
            put(MetadataTag.DatetimeOriginal, timestamp)
            put(MetadataTag.DatetimeDigitized, timestamp)
            put(MetadataTag.FocalLength, "$focalLength/100")
            put(
                MetadataTag.FocalLengthIn35mmFilm,
                (focalLength * lens.cropFactor / 100).roundToInt().toString()
            )
            put(MetadataTag.FNumber, "$aperture/100")
            put(MetadataTag.ApertureValue, (2 * log2(aperture / 100.0)).toExifRational())
            put(MetadataTag.ExposureTime, "1/$shutterSpeed")
            put(MetadataTag.ShutterSpeedValue, log2(shutterSpeed.toDouble()).toExifRational())
            put(MetadataTag.PhotographicSensitivity, iso)
            put(MetadataTag.SensitivityType, "3")
            put(MetadataTag.IsoSpeed, iso)
            put(MetadataTag.ExposureProgram, "2")
            put(MetadataTag.ExposureMode, "0")
            put(MetadataTag.ExposureBiasValue, "0/1")
            put(MetadataTag.WhiteBalance, "0")
            put(MetadataTag.MeteringMode, settings.metering.exifValue)
            put(MetadataTag.Flash, "16")
            put(MetadataTag.DigitalZoomRatio, "0/1")
            put(MetadataTag.SceneCaptureType, "0")
            val coordinates = when (settings.location) {
                ExifLocation.None -> null
                ExifLocation.Random -> random.nextDouble(-0.25, 0.25) to random.nextDouble(
                    -0.25,
                    0.25
                )

                ExifLocation.Manual -> settings.coordinates
            }
            coordinates?.let { (latitude, longitude) ->
                put(MetadataTag.GpsLatitude, latitude.toDms())
                put(MetadataTag.GpsLatitudeRef, if (latitude < 0) "S" else "N")
                put(MetadataTag.GpsLongitude, longitude.toDms())
                put(MetadataTag.GpsLongitudeRef, if (longitude < 0) "W" else "E")
                put(MetadataTag.GpsMapDatum, "WGS-84")
                put(MetadataTag.GpsDatestamp, timestamp.substringBefore(' '))
                put(MetadataTag.GpsTimestamp, timestamp.substringAfter(' '))
            }
        }
    }

    companion object {
        private val dateFormat = DateTimeFormatter.ofPattern("uuuu:MM:dd HH:mm:ss", Locale.ROOT)
        private val apertureStops = listOf(200, 280, 350, 400, 450, 560, 800, 1100)

        private val dslrs = listOf(
            Camera(
                make = "NIKON CORPORATION",
                model = "NIKON D750",
                software = "Ver.1.15",
                lens = Lens("24-70mm f/2.8", listOf(2400, 3500, 5000, 7000), 280, 1.0),
                earliestDate = LocalDateTime.of(2019, 2, 21, 0, 0)
            ),
            Camera(
                make = "Canon",
                model = "Canon EOS 90D",
                software = null,
                lens = Lens(
                    "EF-S18-135mm f/3.5-5.6 IS USM",
                    listOf(1800, 3500, 5000, 8500, 13500),
                    560,
                    1.6,
                    wideAperture = 350
                ),
                earliestDate = LocalDateTime.of(2019, 9, 30, 0, 0)
            )
        )
        private val mirrorless = Camera(
            make = "SONY",
            model = "ILCE-7M3",
            software = null,
            lens = Lens(
                "FE 28-70mm F3.5-5.6 OSS",
                listOf(2800, 3500, 5000, 7000),
                560,
                1.0,
                wideAperture = 350
            ),
            earliestDate = LocalDateTime.of(2018, 4, 9, 0, 0)
        )
        private val compact = Camera(
            make = "SONY",
            model = "DSC-RX100M7",
            software = null,
            lens = Lens(
                name = "Vario-Sonnar T* 9-72mm F2.8-4.5",
                focalLengths = listOf(900, 1800, 3600, 5400, 7200),
                aperture = 450,
                cropFactor = 2.78,
                make = "ZEISS",
                wideAperture = 280
            ),
            earliestDate = LocalDateTime.of(2019, 8, 1, 0, 0)
        )
        private val actionCamera = Camera(
            make = "GoPro",
            model = "HERO11 Black",
            software = null,
            lens = Lens("HERO11 Black Wide", listOf(270), 250, 5.93, true),
            earliestDate = LocalDateTime.of(2022, 10, 1, 0, 0)
        )
        private val cameras by lazy {
            entries.mapNotNull { it.camera } + dslrs + listOf(mirrorless, compact, actionCamera)
        }
        private val phones by lazy { cameras.filter(Camera::isPhone) }
    }
}

private data class Camera(
    val make: String,
    val model: String,
    val software: String? = null,
    val lens: Lens,
    val earliestDate: LocalDateTime,
    val name: String = model,
    val isPhone: Boolean = false
)

private data class Lens(
    val name: String?,
    val focalLengths: List<Int>,
    val aperture: Int,
    val cropFactor: Double,
    val fixedAperture: Boolean = false,
    val make: String? = null,
    val wideAperture: Int = aperture
)

private fun phone(
    name: String,
    make: String,
    focalLength: Int,
    equivalentFocalLength: Int,
    aperture: Int,
    releaseDate: String,
    model: String = name,
    lensName: String? = null,
    software: String? = null
) = Camera(
    name = name,
    isPhone = true,
    make = make,
    model = model,
    software = software,
    lens = Lens(
        name = lensName,
        focalLengths = listOf(focalLength),
        aperture = aperture,
        cropFactor = equivalentFocalLength * 100.0 / focalLength,
        fixedAperture = true
    ),
    earliestDate = LocalDate.parse(releaseDate).atStartOfDay()
)

private fun Double.toExifRational(): String = "${(this * 1000).roundToInt()}/1000"

private fun Double.toDms(): String {
    val seconds = (abs(this) * 3600 * 1_000_000).roundToLong()
    val degrees = seconds / 3_600_000_000
    val minutes = seconds / 60_000_000 % 60
    return "$degrees/1,$minutes/1,${seconds % 60_000_000}/1000000"
}