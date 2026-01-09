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