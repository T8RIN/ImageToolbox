# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keep class * implements com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
-keepclassmembers class * implements com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter {
    <init>(...);
}
-keep class * implements com.t8rin.imagetoolbox.core.filters.domain.model.Filter
-keepclassmembers class * implements com.t8rin.imagetoolbox.core.filters.domain.model.Filter {
    <init>(...);
}
-keepclassmembers class com.t8rin.imagetoolbox.core.filters.** {
    <init>(...);
}
-keep class com.t8rin.imagetoolbox.core.filters.**
-keep class com.t8rin.imagetoolbox.core.filters.*

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-keep class org.beyka.tiffbitmapfactory.**{ *; }

-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }

-dontwarn com.google.re2j.Matcher
-dontwarn com.google.re2j.Pattern

-keepclassmembers class * {
    public java.lang.String name();
}

-keep enum * { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * extends java.lang.Enum {
    public java.lang.String name();
}

-keep class ai.onnxruntime.** { *; }

-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.analytics.** { *; }
-keep class androidx.pdf.** { *; }
-keepnames class androidx.pdf.** { *; }

# Moshi reflective adapters need generic signatures in release.
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

-keep class com.t8rin.imagetoolbox.core.filters.domain.model.shader.** { *; }
-keep class  com.t8rin.imagetoolbox.core.filters.domain.model.shader.**
-keep class  com.t8rin.imagetoolbox.core.filters.domain.model.shader.*

-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.** { *; }
-keep class  com.t8rin.imagetoolbox.feature.markup_layers.data.project.**
-keep class  com.t8rin.imagetoolbox.feature.markup_layers.data.project.*

# Moshi reflects image export preset models when saving/loading presets.
-keep class com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfiles { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.Preset { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.Preset$* { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.ResizeType { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.ResizeType$* { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.Quality { *; }
-keep class com.t8rin.imagetoolbox.core.domain.image.model.Quality$* { *; }
-keep class com.t8rin.imagetoolbox.core.domain.model.IntegerSize { *; }
-keep class com.t8rin.imagetoolbox.core.data.json.PresetJson { *; }
-keep class com.t8rin.imagetoolbox.core.data.json.ResizeTypeJson { *; }
-keep class com.t8rin.imagetoolbox.core.data.json.ImageScaleModeJson { *; }

# Moshi reflects app history/statistics models for recent tools.
-keep class com.t8rin.imagetoolbox.core.domain.history.model.LastUsedTool { *; }
-keep class com.t8rin.imagetoolbox.core.domain.history.model.AppUsageStatistics { *; }
-keep class com.t8rin.imagetoolbox.core.data.history.LastUsedTools { *; }
-keep class com.t8rin.imagetoolbox.core.data.history.SavedFormatCounters { *; }

# coil-resvg uses JNA/Uniffi generated names at runtime.
-keep class com.hashsequence.coilresvg.** { *; }
-keep class com.sun.jna.** { *; }
-dontwarn java.awt.Component
-dontwarn java.awt.GraphicsEnvironment
-dontwarn java.awt.HeadlessException
-dontwarn java.awt.Window

-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.naming.directory.SearchControls
-dontwarn javax.naming.directory.SearchResult

-assumevalues public class androidx.compose.runtime.ComposeRuntimeFlags {
    static boolean isLinkBufferComposerEnabled return true;
}