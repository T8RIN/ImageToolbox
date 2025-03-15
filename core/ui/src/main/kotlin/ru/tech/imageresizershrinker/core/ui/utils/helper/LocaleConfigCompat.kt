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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.app.LocaleConfig
import android.content.Context
import android.content.res.XmlResourceParser
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.XmlRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.LocaleListCompat
import com.t8rin.logger.makeLog
import org.xmlpull.v1.XmlPullParser
import java.io.FileNotFoundException
import java.util.Locale

/**
 * @see android.app.LocaleConfig
 */
class LocaleConfigCompat(context: Context) {
    var status = 0
        private set

    var supportedLocales: LocaleListCompat? = null
        private set

    init {
        val impl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Api33Impl(context)
        } else {
            Api21Impl(context)
        }
        status = impl.status
        supportedLocales = impl.supportedLocales
    }

    companion object {
        /**
         * Succeeded reading the LocaleConfig structure stored in an XML file.
         */
        const val STATUS_SUCCESS = 0

        /**
         * No android:localeConfig tag on <application>.
         */
        const val STATUS_NOT_SPECIFIED = 1

        /**
         * Malformed input in the XML file where the LocaleConfig was stored.
         */
        const val STATUS_PARSING_FAILED = 2
    }

    private abstract class Impl {
        abstract val status: Int
        abstract val supportedLocales: LocaleListCompat?
    }

    private class Api21Impl(context: Context) : Impl() {
        override var status = 0

        override var supportedLocales: LocaleListCompat? = null

        init {
            val resourceId = try {
                getLocaleConfigResourceId(context)
            } catch (e: Throwable) {
                "The resource file pointed to by the given resource ID isn't found.".makeLog(TAG)

                ResourcesCompat.ID_NULL
            }
            if (resourceId == ResourcesCompat.ID_NULL) {
                status = STATUS_NOT_SPECIFIED
            } else {
                val resources = context.resources
                try {
                    supportedLocales = resources.getXml(resourceId).use { parseLocaleConfig(it) }
                    status = STATUS_SUCCESS
                } catch (e: Throwable) {
                    val resourceEntryName = resources.getResourceEntryName(resourceId)
                    "Failed to parse XML configuration from $resourceEntryName".makeLog(TAG)
                    status = STATUS_PARSING_FAILED
                }
            }
        }

        // @see com.android.server.pm.pkg.parsing.ParsingPackageUtils
        @XmlRes
        private fun getLocaleConfigResourceId(context: Context): Int {
            // Java cookies starts at 1, while passing 0 (invalid cookie for Java) makes
            // AssetManager pick the last asset containing such a file name.
            // We should go over all the assets containing AndroidManifest.xml, however there's no
            // API to do that, so the best we can do is to start from the first asset and iterate
            // until we can't find the next asset containing AndroidManifest.xml.
            var cookie = 1
            var isAndroidManifestFound = false
            while (true) {
                val parser = try {
                    context.assets.openXmlResourceParser(cookie, FILE_NAME_ANDROID_MANIFEST)
                } catch (_: FileNotFoundException) {
                    if (!isAndroidManifestFound) {
                        ++cookie
                        continue
                    } else {
                        break
                    }
                }
                isAndroidManifestFound = true
                parser.use {
                    do {
                        if (parser.eventType != XmlPullParser.START_TAG) {
                            continue
                        }
                        if (parser.name != TAG_MANIFEST) {
                            parser.skipCurrentTag()
                            continue
                        }
                        if (parser.getAttributeValue(null, ATTR_PACKAGE) != context.packageName) {
                            break
                        }
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.eventType != XmlPullParser.START_TAG) {
                                continue
                            }
                            if (parser.name != TAG_APPLICATION) {
                                parser.skipCurrentTag()
                                continue
                            }
                            return parser.getAttributeResourceValue(
                                NAMESPACE_ANDROID, ATTR_LOCALE_CONFIG, ResourcesCompat.ID_NULL
                            )
                        }
                    } while (parser.next() != XmlPullParser.END_DOCUMENT)
                }
                ++cookie
            }
            return ResourcesCompat.ID_NULL
        }

        private fun parseLocaleConfig(parser: XmlResourceParser): LocaleListCompat {
            val localeNames = mutableSetOf<String>()
            do {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                if (parser.name != TAG_LOCALE_CONFIG) {
                    parser.skipCurrentTag()
                    continue
                }
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.eventType != XmlPullParser.START_TAG) {
                        continue
                    }
                    if (parser.name != TAG_LOCALE) {
                        parser.skipCurrentTag()
                        continue
                    }
                    localeNames += parser.getAttributeValue(NAMESPACE_ANDROID, ATTR_NAME)
                    parser.skipCurrentTag()
                }
            } while (parser.next() != XmlPullParser.END_DOCUMENT)
            return LocaleListCompat.forLanguageTags(localeNames.joinToString(","))
        }

        private fun XmlPullParser.skipCurrentTag() {
            val outerDepth = depth
            var type: Int
            do {
                type = next()
            } while (type != XmlPullParser.END_DOCUMENT &&
                (type != XmlPullParser.END_TAG || depth > outerDepth)
            )
        }

        companion object {
            private const val TAG = "LocaleConfigCompat"

            private const val FILE_NAME_ANDROID_MANIFEST = "AndroidManifest.xml"

            private const val TAG_APPLICATION = "application"
            private const val TAG_LOCALE_CONFIG = "locale-config"
            private const val TAG_LOCALE = "locale"
            private const val TAG_MANIFEST = "manifest"

            private const val NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android"

            private const val ATTR_LOCALE_CONFIG = "localeConfig"
            private const val ATTR_NAME = "name"
            private const val ATTR_PACKAGE = "package"
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private class Api33Impl(context: Context) : Impl() {
        override var status: Int = 0

        override var supportedLocales: LocaleListCompat? = null

        init {
            val platformLocaleConfig = LocaleConfig(context)
            status = platformLocaleConfig.status
            supportedLocales = platformLocaleConfig.supportedLocales
                ?.let { LocaleListCompat.wrap(it) }
        }
    }
}

fun LocaleListCompat.toList(): List<Locale> = List(size()) { this[it]!! }