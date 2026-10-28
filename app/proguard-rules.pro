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

# These type names are part of the existing filter parameter format.
# Filter instances themselves are restored by the generated KSP registry.
-keepnames class com.t8rin.imagetoolbox.core.filters.domain.model.params.**
-keepnames class com.t8rin.imagetoolbox.core.filters.domain.model.enums.**
-keepnames class com.t8rin.imagetoolbox.core.filters.domain.model.FilterValueWrapper
-keepnames class com.t8rin.imagetoolbox.core.domain.model.ColorModel
-keepnames class com.t8rin.imagetoolbox.core.domain.model.GradientPalette
-keepnames class com.t8rin.imagetoolbox.core.domain.model.IntegerSize
-keepnames class kotlin.Pair
-keepnames class kotlin.Triple
-keepnames class kotlin.Unit
-keepnames class com.t8rin.imagetoolbox.core.domain.utils.Quad

# createFilter uses a Java proxy and checks these method names.
-keep interface com.t8rin.imagetoolbox.core.filters.domain.model.Filter {
    public java.lang.Object getValue();
}
-keep interface com.t8rin.imagetoolbox.core.filters.domain.model.Filter$NativeStackBlur
-keepclassmembers interface com.t8rin.imagetoolbox.core.domain.model.VisibilityOwner {
    public boolean isVisible();
}

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

# The cipher catalog discovers all provider algorithms dynamically.
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }

-dontwarn com.google.re2j.Matcher
-dontwarn com.google.re2j.Pattern

# Enum names are persisted in settings, presets and projects.
-keepnames enum com.t8rin.imagetoolbox.**
-keepclassmembers,allowoptimization enum com.t8rin.imagetoolbox.** {
    public static final ** *;
}

# ONNX accesses these Java classes and members through JNI.
-keep class ai.onnxruntime.** { *; }

-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.analytics.** { *; }

# The PDF viewer accesses these private fields through reflection.
-keep,allowoptimization class androidx.pdf.view.ToolBoxView {
    private com.google.android.material.floatingactionbutton.FloatingActionButton editButton;
}
-keep,allowoptimization class androidx.pdf.view.fastscroll.FastScrollDrawer {
    private android.text.TextPaint textPaint;
}

# Moshi reflective adapters need generic signatures in release.
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

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

# Moshi reflects PDF crop presets and their rectangle bounds.
-keep class com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCropPresets { *; }
-keep class com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCropPreset { *; }
-keep class com.t8rin.imagetoolbox.core.domain.model.RectModel { *; }

# Moshi reflects polymorphic settings and default markup text styles.
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType$* { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior$* { *; }
-keep class com.t8rin.imagetoolbox.core.domain.model.HashingType { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerTextDefaults { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerOutline { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerTextGeometricTransform { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerDropShadow { *; }

# Moshi reflects semantic search preferences and custom categories.
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings { *; }
-keep class com.t8rin.imagetoolbox.core.settings.domain.model.CustomSemanticSearchCategory { *; }

# Moshi reflects app history/statistics models for recent tools.
-keep class com.t8rin.imagetoolbox.core.domain.history.model.LastUsedTool { *; }
-keep class com.t8rin.imagetoolbox.core.domain.history.model.AppUsageStatistics { *; }
-keep class com.t8rin.imagetoolbox.core.data.history.LastUsedTools { *; }
-keep class com.t8rin.imagetoolbox.core.data.history.SavedFormatCounters { *; }

# Moshi also reflects models persisted through ObjectSaver.
-keep class com.t8rin.imagetoolbox.core.domain.model.CipherType { *; }
-keep class com.t8rin.imagetoolbox.core.domain.model.GradientFill { *; }
-keep class com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams { *; }
-keep class com.t8rin.imagetoolbox.color_library.presentation.components.FavoriteColors { *; }
-keep class com.t8rin.imagetoolbox.feature.image_stitch.domain.SavableCombiningParams { *; }
-keep class com.t8rin.imagetoolbox.feature.ai_tools.domain.model.SavableNeuralParams { *; }
-keep class com.t8rin.imagetoolbox.feature.ai_tools.domain.model.DepthParams { *; }
-keep class com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageParams { *; }
-keep class com.t8rin.imagetoolbox.feature.draw.domain.DrawOnBackgroundParams { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayersParams { *; }
-keep class com.t8rin.imagetoolbox.image_splitting.domain.SavableSplitParams { *; }

# Moshi reflects markup project snapshots.
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectFile { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.EditorSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.BackgroundSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.LayerSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.PositionSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.TextSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.PictureSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.ShapeSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.FontSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.OutlineSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.TextGeometricTransformSnapshot { *; }
-keep class com.t8rin.imagetoolbox.feature.markup_layers.data.project.DropShadowSnapshot { *; }
-keep enum com.t8rin.imagetoolbox.feature.markup_layers.data.project.BackgroundType { *; }
-keep enum com.t8rin.imagetoolbox.feature.markup_layers.data.project.LayerSnapshotType { *; }
-keep enum com.t8rin.imagetoolbox.feature.markup_layers.data.project.FontSnapshotType { *; }

# Shader JSON DTOs are private and read by Moshi.
-keep class com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPresetJson { *; }
-keep class com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParamJson { *; }

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