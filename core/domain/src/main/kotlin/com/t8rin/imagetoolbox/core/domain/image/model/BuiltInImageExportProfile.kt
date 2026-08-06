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

package com.t8rin.imagetoolbox.core.domain.image.model

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.resources.R

data class BuiltInImageExportProfile(
    val id: String,
    val platform: Platform,
    @StringRes val titleRes: Int,
    val profile: ImageExportProfile
) {

    enum class Platform(val title: String) {
        Web("Web"),
        Instagram("Instagram"),
        Facebook("Facebook"),
        X("X"),
        YouTube("YouTube"),
        TikTok("TikTok"),
        Threads("Threads"),
        Bluesky("Bluesky"),
        LinkedIn("LinkedIn"),
        Pinterest("Pinterest"),
        Telegram("Telegram"),
        Discord("Discord"),
        Twitch("Twitch")
    }

    companion object {
        val entries: List<BuiltInImageExportProfile> by lazy {
            listOf(
                webProfile(
                    id = "web_optimized",
                    titleRes = R.string.export_profile_web_optimized,
                    maxSize = 1920,
                    quality = 85
                ),
                webProfile(
                    id = "web_thumbnail",
                    titleRes = R.string.export_profile_web_thumbnail,
                    maxSize = 640,
                    quality = 80
                ),
                socialProfile(
                    id = "instagram_square",
                    platform = Platform.Instagram,
                    titleRes = R.string.export_profile_instagram_square,
                    width = 1080,
                    height = 1080
                ),
                socialProfile(
                    id = "instagram_portrait",
                    platform = Platform.Instagram,
                    titleRes = R.string.export_profile_instagram_portrait,
                    width = 1080,
                    height = 1440
                ),
                socialProfile(
                    id = "instagram_landscape",
                    platform = Platform.Instagram,
                    titleRes = R.string.export_profile_instagram_landscape,
                    width = 1080,
                    height = 566
                ),
                socialProfile(
                    id = "instagram_story",
                    platform = Platform.Instagram,
                    titleRes = R.string.export_profile_instagram_story,
                    width = 1080,
                    height = 1920
                ),
                socialProfile(
                    id = "instagram_reel_cover",
                    platform = Platform.Instagram,
                    titleRes = R.string.export_profile_instagram_reel_cover,
                    width = 1080,
                    height = 1920
                ),
                socialProfile(
                    id = "instagram_profile",
                    platform = Platform.Instagram,
                    titleRes = R.string.export_profile_instagram_profile,
                    width = 320,
                    height = 320
                ),
                socialProfile(
                    id = "facebook_post",
                    platform = Platform.Facebook,
                    titleRes = R.string.export_profile_facebook_post,
                    width = 1200,
                    height = 630
                ),
                socialProfile(
                    id = "facebook_square",
                    platform = Platform.Facebook,
                    titleRes = R.string.export_profile_facebook_square,
                    width = 1080,
                    height = 1080
                ),
                socialProfile(
                    id = "facebook_story",
                    platform = Platform.Facebook,
                    titleRes = R.string.export_profile_facebook_story,
                    width = 1080,
                    height = 1920
                ),
                socialProfile(
                    id = "facebook_cover",
                    platform = Platform.Facebook,
                    titleRes = R.string.export_profile_facebook_cover,
                    width = 851,
                    height = 315
                ),
                socialProfile(
                    id = "facebook_event_cover",
                    platform = Platform.Facebook,
                    titleRes = R.string.export_profile_facebook_event_cover,
                    width = 1920,
                    height = 1005
                ),
                socialProfile(
                    id = "facebook_profile",
                    platform = Platform.Facebook,
                    titleRes = R.string.export_profile_facebook_profile,
                    width = 320,
                    height = 320
                ),
                socialProfile(
                    id = "x_post",
                    platform = Platform.X,
                    titleRes = R.string.export_profile_x_post,
                    width = 1600,
                    height = 900
                ),
                socialProfile(
                    id = "x_square",
                    platform = Platform.X,
                    titleRes = R.string.export_profile_x_square,
                    width = 1080,
                    height = 1080
                ),
                socialProfile(
                    id = "x_portrait",
                    platform = Platform.X,
                    titleRes = R.string.export_profile_x_portrait,
                    width = 1080,
                    height = 1350
                ),
                socialProfile(
                    id = "x_header",
                    platform = Platform.X,
                    titleRes = R.string.export_profile_x_header,
                    width = 1500,
                    height = 500
                ),
                socialProfile(
                    id = "x_profile",
                    platform = Platform.X,
                    titleRes = R.string.export_profile_x_profile,
                    width = 400,
                    height = 400
                ),
                socialProfile(
                    id = "youtube_thumbnail",
                    platform = Platform.YouTube,
                    titleRes = R.string.export_profile_youtube_thumbnail,
                    width = 3840,
                    height = 2160
                ),
                socialProfile(
                    id = "youtube_banner",
                    platform = Platform.YouTube,
                    titleRes = R.string.export_profile_youtube_banner,
                    width = 2560,
                    height = 1440
                ),
                socialProfile(
                    id = "youtube_profile",
                    platform = Platform.YouTube,
                    titleRes = R.string.export_profile_youtube_profile,
                    width = 800,
                    height = 800
                ),
                socialProfile(
                    id = "youtube_community",
                    platform = Platform.YouTube,
                    titleRes = R.string.export_profile_youtube_community,
                    width = 1200,
                    height = 1200
                ),
                socialProfile(
                    id = "tiktok_portrait",
                    platform = Platform.TikTok,
                    titleRes = R.string.export_profile_tiktok_portrait,
                    width = 1080,
                    height = 1920
                ),
                socialProfile(
                    id = "tiktok_landscape",
                    platform = Platform.TikTok,
                    titleRes = R.string.export_profile_tiktok_landscape,
                    width = 1200,
                    height = 628
                ),
                socialProfile(
                    id = "tiktok_square",
                    platform = Platform.TikTok,
                    titleRes = R.string.export_profile_tiktok_square,
                    width = 640,
                    height = 640
                ),
                socialProfile(
                    id = "tiktok_profile",
                    platform = Platform.TikTok,
                    titleRes = R.string.export_profile_tiktok_profile,
                    width = 200,
                    height = 200
                ),
                socialProfile(
                    id = "threads_square",
                    platform = Platform.Threads,
                    titleRes = R.string.export_profile_threads_square,
                    width = 1080,
                    height = 1080
                ),
                socialProfile(
                    id = "threads_portrait",
                    platform = Platform.Threads,
                    titleRes = R.string.export_profile_threads_portrait,
                    width = 1080,
                    height = 1440
                ),
                socialProfile(
                    id = "threads_landscape",
                    platform = Platform.Threads,
                    titleRes = R.string.export_profile_threads_landscape,
                    width = 1080,
                    height = 566
                ),
                socialProfile(
                    id = "bluesky_landscape",
                    platform = Platform.Bluesky,
                    titleRes = R.string.export_profile_bluesky_landscape,
                    width = 1200,
                    height = 675
                ),
                socialProfile(
                    id = "bluesky_square",
                    platform = Platform.Bluesky,
                    titleRes = R.string.export_profile_bluesky_square,
                    width = 1080,
                    height = 1080
                ),
                socialProfile(
                    id = "bluesky_portrait",
                    platform = Platform.Bluesky,
                    titleRes = R.string.export_profile_bluesky_portrait,
                    width = 1080,
                    height = 1350
                ),
                socialProfile(
                    id = "linkedin_post",
                    platform = Platform.LinkedIn,
                    titleRes = R.string.export_profile_linkedin_post,
                    width = 1200,
                    height = 627
                ),
                socialProfile(
                    id = "linkedin_portrait",
                    platform = Platform.LinkedIn,
                    titleRes = R.string.export_profile_linkedin_portrait,
                    width = 1080,
                    height = 1350
                ),
                socialProfile(
                    id = "linkedin_cover",
                    platform = Platform.LinkedIn,
                    titleRes = R.string.export_profile_linkedin_cover,
                    width = 1584,
                    height = 396
                ),
                socialProfile(
                    id = "linkedin_article_cover",
                    platform = Platform.LinkedIn,
                    titleRes = R.string.export_profile_linkedin_article_cover,
                    width = 1920,
                    height = 1080
                ),
                socialProfile(
                    id = "linkedin_company_cover",
                    platform = Platform.LinkedIn,
                    titleRes = R.string.export_profile_linkedin_company_cover,
                    width = 4200,
                    height = 700
                ),
                socialProfile(
                    id = "linkedin_profile",
                    platform = Platform.LinkedIn,
                    titleRes = R.string.export_profile_linkedin_profile,
                    width = 400,
                    height = 400
                ),
                socialProfile(
                    id = "pinterest_pin",
                    platform = Platform.Pinterest,
                    titleRes = R.string.export_profile_pinterest_pin,
                    width = 1000,
                    height = 1500
                ),
                socialProfile(
                    id = "pinterest_square",
                    platform = Platform.Pinterest,
                    titleRes = R.string.export_profile_pinterest_square,
                    width = 1000,
                    height = 1000
                ),
                socialProfile(
                    id = "pinterest_story",
                    platform = Platform.Pinterest,
                    titleRes = R.string.export_profile_pinterest_story,
                    width = 1080,
                    height = 1920
                ),
                BuiltInImageExportProfile(
                    id = "telegram_sticker",
                    platform = Platform.Telegram,
                    titleRes = R.string.export_profile_telegram_sticker,
                    profile = ImageExportProfile(
                        name = "Telegram sticker",
                        imageInfo = ImageInfo(
                            width = 512,
                            height = 512,
                            quality = Quality.Base(100),
                            imageFormat = ImageFormat.Png.Lossless,
                            resizeType = ResizeType.Flexible,
                            imageScaleMode = ImageScaleMode.Lanczos3()
                        ),
                        preset = Preset.Telegram
                    )
                ),
                fixedPngProfile(
                    id = "telegram_custom_emoji",
                    platform = Platform.Telegram,
                    titleRes = R.string.export_profile_telegram_custom_emoji,
                    width = 100,
                    height = 100
                ).let {
                    it.copy(
                        profile = it.profile.copy(
                            imageInfo = it.profile.imageInfo.copy(
                                resizeType = ResizeType.Flexible
                            )
                        )
                    )
                },
                socialProfile(
                    id = "discord_profile_banner",
                    platform = Platform.Discord,
                    titleRes = R.string.export_profile_discord_profile_banner,
                    width = 680,
                    height = 240
                ),
                socialProfile(
                    id = "discord_server_banner",
                    platform = Platform.Discord,
                    titleRes = R.string.export_profile_discord_server_banner,
                    width = 960,
                    height = 540
                ),
                socialProfile(
                    id = "discord_invite_splash",
                    platform = Platform.Discord,
                    titleRes = R.string.export_profile_discord_invite_splash,
                    width = 1920,
                    height = 1080
                ),
                fixedPngProfile(
                    id = "discord_avatar",
                    platform = Platform.Discord,
                    titleRes = R.string.export_profile_discord_avatar,
                    width = 512,
                    height = 512
                ),
                socialProfile(
                    id = "twitch_profile_banner",
                    platform = Platform.Twitch,
                    titleRes = R.string.export_profile_twitch_profile_banner,
                    width = 1200,
                    height = 480
                ),
                socialProfile(
                    id = "twitch_thumbnail",
                    platform = Platform.Twitch,
                    titleRes = R.string.export_profile_twitch_thumbnail,
                    width = 1280,
                    height = 720
                ),
                fixedPngProfile(
                    id = "twitch_profile",
                    platform = Platform.Twitch,
                    titleRes = R.string.export_profile_twitch_profile,
                    width = 256,
                    height = 256
                )
            )
        }

        private fun webProfile(
            id: String,
            @StringRes titleRes: Int,
            maxSize: Int,
            quality: Int
        ) = BuiltInImageExportProfile(
            id = id,
            platform = Platform.Web,
            titleRes = titleRes,
            profile = ImageExportProfile(
                name = id.toProfileName(),
                imageInfo = ImageInfo(
                    width = maxSize,
                    height = maxSize,
                    quality = Quality.Base(quality),
                    imageFormat = ImageFormat.Webp.Lossy,
                    resizeType = ResizeType.Flexible,
                    imageScaleMode = ImageScaleMode.Lanczos3()
                ),
                preset = Preset.None,
                keepExif = false
            )
        )

        private fun socialProfile(
            id: String,
            platform: Platform,
            @StringRes titleRes: Int,
            width: Int,
            height: Int
        ) = BuiltInImageExportProfile(
            id = id,
            platform = platform,
            titleRes = titleRes,
            profile = ImageExportProfile(
                name = id.toProfileName(),
                imageInfo = ImageInfo(
                    width = width,
                    height = height,
                    quality = Quality.Base(SOCIAL_QUALITY),
                    imageFormat = ImageFormat.Jpg,
                    resizeType = ResizeType.CenterCrop(upscaleToFitCanvas = true),
                    imageScaleMode = ImageScaleMode.Lanczos3()
                ),
                preset = Preset.None,
                keepExif = false
            )
        )

        private fun fixedPngProfile(
            id: String,
            platform: Platform,
            @StringRes titleRes: Int,
            width: Int,
            height: Int
        ) = BuiltInImageExportProfile(
            id = id,
            platform = platform,
            titleRes = titleRes,
            profile = ImageExportProfile(
                name = id.toProfileName(),
                imageInfo = ImageInfo(
                    width = width,
                    height = height,
                    quality = Quality.Base(100),
                    imageFormat = ImageFormat.Png.Lossless,
                    resizeType = ResizeType.CenterCrop(upscaleToFitCanvas = true),
                    imageScaleMode = ImageScaleMode.Lanczos3()
                ),
                preset = Preset.None,
                keepExif = false
            )
        )

        private fun String.toProfileName(): String = replace('_', ' ')
            .replaceFirstChar(Char::uppercaseChar)

        private const val SOCIAL_QUALITY = 90
    }
}
