# 📐 Modules Graph

```mermaid
%%{
    init: {
      "theme": "base",
      "themeVariables": {
        "mainBkg": "#121418",
        "primaryColor": "#1b3a1b",
        "primaryTextColor": "#e0e3de",
        "primaryBorderColor": "#76c893",
        "nodeBorder": "#76c893",
        "lineColor": "#76c893",
        "secondaryColor": "#1f2721",
        "tertiaryColor": "#232c26",
        "clusterBkg": "#182018",
        "clusterBorder": "#4caf50",
        "nodeTextColor": "#e0e3de",
        "edgeLabelBackground": "#111316",
        "edgeLabelColor": "#cfe9de",
        "fontSize": "28px",
        "fontFamily": "JetBrains Mono, Inter, system-ui"
      }
    }
}%%

graph LR
    subgraph :core
        :core:data("data")
        :core:ui("ui")
        :core:domain("domain")
        :core:resources("resources")
        :core:settings("settings")
        :core:di("di")
        :core:crash("crash")
        :core:utils("utils")
        :core:filters("filters")
        :core:ksp("ksp")
    end
    subgraph :feature
        :feature:root("root")
        :feature:main("main")
        :feature:load-net-image("load-net-image")
        :feature:crop("crop")
        :feature:limits-resize("limits-resize")
        :feature:cipher("cipher")
        :feature:image-preview("image-preview")
        :feature:weight-resize("weight-resize")
        :feature:compare("compare")
        :feature:delete-exif("delete-exif")
        :feature:palette-tools("palette-tools")
        :feature:resize-convert("resize-convert")
        :feature:pdf-tools("pdf-tools")
        :feature:single-edit("single-edit")
        :feature:erase-background("erase-background")
        :feature:draw("draw")
        :feature:filters("filters")
        :feature:image-stitch("image-stitch")
        :feature:pick-color("pick-color")
        :feature:recognize-text("recognize-text")
        :feature:gradient-maker("gradient-maker")
        :feature:watermarking("watermarking")
        :feature:gif-tools("gif-tools")
        :feature:apng-tools("apng-tools")
        :feature:zip("zip")
        :feature:jxl-tools("jxl-tools")
        :feature:settings("settings")
        :feature:easter-egg("easter-egg")
        :feature:svg-maker("svg-maker")
        :feature:format-conversion("format-conversion")
        :feature:document-scanner("document-scanner")
        :feature:scan-qr-code("scan-qr-code")
        :feature:image-stacking("image-stacking")
        :feature:image-splitting("image-splitting")
        :feature:color-tools("color-tools")
        :feature:webp-tools("webp-tools")
        :feature:noise-generation("noise-generation")
        :feature:collage-maker("collage-maker")
        :feature:libraries-info("libraries-info")
        :feature:markup-layers("markup-layers")
        :feature:base64-tools("base64-tools")
        :feature:checksum-tools("checksum-tools")
        :feature:mesh-gradients("mesh-gradients")
        :feature:edit-exif("edit-exif")
        :feature:image-cutting("image-cutting")
        :feature:audio-cover-extractor("audio-cover-extractor")
        :feature:library-details("library-details")
        :feature:wallpapers-export("wallpapers-export")
        :feature:ascii-art("ascii-art")
        :feature:ai-tools("ai-tools")
        :feature:media-picker("media-picker")
        :feature:quick-tiles("quick-tiles")
    end
    :feature:root --> :core:data
    :feature:root --> :core:ui
    :feature:root --> :core:domain
    :feature:root --> :core:resources
    :feature:root --> :core:settings
    :feature:root --> :core:di
    :feature:root --> :core:crash
    :feature:root --> :feature:main
    :feature:root --> :feature:load-net-image
    :feature:root --> :feature:crop
    :feature:root --> :feature:limits-resize
    :feature:root --> :feature:cipher
    :feature:root --> :feature:image-preview
    :feature:root --> :feature:weight-resize
    :feature:root --> :feature:compare
    :feature:root --> :feature:delete-exif
    :feature:root --> :feature:palette-tools
    :feature:root --> :feature:resize-convert
    :feature:root --> :feature:pdf-tools
    :feature:root --> :feature:single-edit
    :feature:root --> :feature:erase-background
    :feature:root --> :feature:draw
    :feature:root --> :feature:filters
    :feature:root --> :feature:image-stitch
    :feature:root --> :feature:pick-color
    :feature:root --> :feature:recognize-text
    :feature:root --> :feature:gradient-maker
    :feature:root --> :feature:watermarking
    :feature:root --> :feature:gif-tools
    :feature:root --> :feature:apng-tools
    :feature:root --> :feature:zip
    :feature:root --> :feature:jxl-tools
    :feature:root --> :feature:settings
    :feature:root --> :feature:easter-egg
    :feature:root --> :feature:svg-maker
    :feature:root --> :feature:format-conversion
    :feature:root --> :feature:document-scanner
    :feature:root --> :feature:scan-qr-code
    :feature:root --> :feature:image-stacking
    :feature:root --> :feature:image-splitting
    :feature:root --> :feature:color-tools
    :feature:root --> :feature:webp-tools
    :feature:root --> :feature:noise-generation
    :feature:root --> :feature:collage-maker
    :feature:root --> :feature:libraries-info
    :feature:root --> :feature:markup-layers
    :feature:root --> :feature:base64-tools
    :feature:root --> :feature:checksum-tools
    :feature:root --> :feature:mesh-gradients
    :feature:root --> :feature:edit-exif
    :feature:root --> :feature:image-cutting
    :feature:root --> :feature:audio-cover-extractor
    :feature:root --> :feature:library-details
    :feature:root --> :feature:wallpapers-export
    :feature:root --> :feature:ascii-art
    :feature:root --> :feature:ai-tools
    :feature:erase-background --> :core:data
    :feature:erase-background --> :core:ui
    :feature:erase-background --> :core:domain
    :feature:erase-background --> :core:resources
    :feature:erase-background --> :core:settings
    :feature:erase-background --> :core:di
    :feature:erase-background --> :core:crash
    :feature:erase-background --> :feature:draw
    :feature:edit-exif --> :core:data
    :feature:edit-exif --> :core:ui
    :feature:edit-exif --> :core:domain
    :feature:edit-exif --> :core:resources
    :feature:edit-exif --> :core:settings
    :feature:edit-exif --> :core:di
    :feature:edit-exif --> :core:crash
    :feature:limits-resize --> :core:data
    :feature:limits-resize --> :core:ui
    :feature:limits-resize --> :core:domain
    :feature:limits-resize --> :core:resources
    :feature:limits-resize --> :core:settings
    :feature:limits-resize --> :core:di
    :feature:limits-resize --> :core:crash
    :feature:jxl-tools --> :core:data
    :feature:jxl-tools --> :core:ui
    :feature:jxl-tools --> :core:domain
    :feature:jxl-tools --> :core:resources
    :feature:jxl-tools --> :core:settings
    :feature:jxl-tools --> :core:di
    :feature:jxl-tools --> :core:crash
    :feature:libraries-info --> :core:data
    :feature:libraries-info --> :core:ui
    :feature:libraries-info --> :core:domain
    :feature:libraries-info --> :core:resources
    :feature:libraries-info --> :core:settings
    :feature:libraries-info --> :core:di
    :feature:libraries-info --> :core:crash
    :feature:gif-tools --> :core:data
    :feature:gif-tools --> :core:ui
    :feature:gif-tools --> :core:domain
    :feature:gif-tools --> :core:resources
    :feature:gif-tools --> :core:settings
    :feature:gif-tools --> :core:di
    :feature:gif-tools --> :core:crash
    :feature:library-details --> :core:data
    :feature:library-details --> :core:ui
    :feature:library-details --> :core:domain
    :feature:library-details --> :core:resources
    :feature:library-details --> :core:settings
    :feature:library-details --> :core:di
    :feature:library-details --> :core:crash
    :feature:pdf-tools --> :core:data
    :feature:pdf-tools --> :core:ui
    :feature:pdf-tools --> :core:domain
    :feature:pdf-tools --> :core:resources
    :feature:pdf-tools --> :core:settings
    :feature:pdf-tools --> :core:di
    :feature:pdf-tools --> :core:crash
    :feature:watermarking --> :core:data
    :feature:watermarking --> :core:ui
    :feature:watermarking --> :core:domain
    :feature:watermarking --> :core:resources
    :feature:watermarking --> :core:settings
    :feature:watermarking --> :core:di
    :feature:watermarking --> :core:crash
    :feature:watermarking --> :feature:compare
    :app --> :core:data
    :app --> :core:ui
    :app --> :core:domain
    :app --> :core:resources
    :app --> :core:settings
    :app --> :core:di
    :app --> :core:crash
    :app --> :core:utils
    :app --> :feature:root
    :app --> :feature:media-picker
    :app --> :feature:quick-tiles
    :feature:resize-convert --> :core:data
    :feature:resize-convert --> :core:ui
    :feature:resize-convert --> :core:domain
    :feature:resize-convert --> :core:resources
    :feature:resize-convert --> :core:settings
    :feature:resize-convert --> :core:di
    :feature:resize-convert --> :core:crash
    :feature:resize-convert --> :feature:compare
    :feature:easter-egg --> :core:data
    :feature:easter-egg --> :core:ui
    :feature:easter-egg --> :core:domain
    :feature:easter-egg --> :core:resources
    :feature:easter-egg --> :core:settings
    :feature:easter-egg --> :core:di
    :feature:easter-egg --> :core:crash
    :feature:webp-tools --> :core:data
    :feature:webp-tools --> :core:ui
    :feature:webp-tools --> :core:domain
    :feature:webp-tools --> :core:resources
    :feature:webp-tools --> :core:settings
    :feature:webp-tools --> :core:di
    :feature:webp-tools --> :core:crash
    :feature:markup-layers --> :core:data
    :feature:markup-layers --> :core:ui
    :feature:markup-layers --> :core:domain
    :feature:markup-layers --> :core:resources
    :feature:markup-layers --> :core:settings
    :feature:markup-layers --> :core:di
    :feature:markup-layers --> :core:crash
    :feature:media-picker --> :core:data
    :feature:media-picker --> :core:ui
    :feature:media-picker --> :core:domain
    :feature:media-picker --> :core:resources
    :feature:media-picker --> :core:settings
    :feature:media-picker --> :core:di
    :feature:media-picker --> :core:crash
    :feature:image-stitch --> :core:data
    :feature:image-stitch --> :core:ui
    :feature:image-stitch --> :core:domain
    :feature:image-stitch --> :core:resources
    :feature:image-stitch --> :core:settings
    :feature:image-stitch --> :core:di
    :feature:image-stitch --> :core:crash
    :feature:image-stitch --> :core:filters
    :core:ui --> :core:resources
    :core:ui --> :core:domain
    :core:ui --> :core:utils
    :core:ui --> :core:di
    :core:ui --> :core:settings
    :feature:noise-generation --> :core:data
    :feature:noise-generation --> :core:ui
    :feature:noise-generation --> :core:domain
    :feature:noise-generation --> :core:resources
    :feature:noise-generation --> :core:settings
    :feature:noise-generation --> :core:di
    :feature:noise-generation --> :core:crash
    :feature:wallpapers-export --> :core:data
    :feature:wallpapers-export --> :core:ui
    :feature:wallpapers-export --> :core:domain
    :feature:wallpapers-export --> :core:resources
    :feature:wallpapers-export --> :core:settings
    :feature:wallpapers-export --> :core:di
    :feature:wallpapers-export --> :core:crash
    :feature:document-scanner --> :core:data
    :feature:document-scanner --> :core:ui
    :feature:document-scanner --> :core:domain
    :feature:document-scanner --> :core:resources
    :feature:document-scanner --> :core:settings
    :feature:document-scanner --> :core:di
    :feature:document-scanner --> :core:crash
    :feature:document-scanner --> :feature:pdf-tools
    :feature:gradient-maker --> :core:data
    :feature:gradient-maker --> :core:ui
    :feature:gradient-maker --> :core:domain
    :feature:gradient-maker --> :core:resources
    :feature:gradient-maker --> :core:settings
    :feature:gradient-maker --> :core:di
    :feature:gradient-maker --> :core:crash
    :feature:gradient-maker --> :feature:compare
    :feature:zip --> :core:data
    :feature:zip --> :core:ui
    :feature:zip --> :core:domain
    :feature:zip --> :core:resources
    :feature:zip --> :core:settings
    :feature:zip --> :core:di
    :feature:zip --> :core:crash
    :core:utils --> :core:domain
    :core:utils --> :core:resources
    :core:utils --> :core:settings
    :feature:cipher --> :core:data
    :feature:cipher --> :core:ui
    :feature:cipher --> :core:domain
    :feature:cipher --> :core:resources
    :feature:cipher --> :core:settings
    :feature:cipher --> :core:di
    :feature:cipher --> :core:crash
    :feature:draw --> :core:data
    :feature:draw --> :core:ui
    :feature:draw --> :core:domain
    :feature:draw --> :core:resources
    :feature:draw --> :core:settings
    :feature:draw --> :core:di
    :feature:draw --> :core:crash
    :feature:draw --> :core:filters
    :feature:draw --> :feature:pick-color
    :feature:ai-tools --> :core:data
    :feature:ai-tools --> :core:ui
    :feature:ai-tools --> :core:domain
    :feature:ai-tools --> :core:resources
    :feature:ai-tools --> :core:settings
    :feature:ai-tools --> :core:di
    :feature:ai-tools --> :core:crash
    :feature:audio-cover-extractor --> :core:data
    :feature:audio-cover-extractor --> :core:ui
    :feature:audio-cover-extractor --> :core:domain
    :feature:audio-cover-extractor --> :core:resources
    :feature:audio-cover-extractor --> :core:settings
    :feature:audio-cover-extractor --> :core:di
    :feature:audio-cover-extractor --> :core:crash
    :core:crash --> :core:ui
    :core:crash --> :core:settings
    :feature:delete-exif --> :core:data
    :feature:delete-exif --> :core:ui
    :feature:delete-exif --> :core:domain
    :feature:delete-exif --> :core:resources
    :feature:delete-exif --> :core:settings
    :feature:delete-exif --> :core:di
    :feature:delete-exif --> :core:crash
    :feature:collage-maker --> :core:data
    :feature:collage-maker --> :core:ui
    :feature:collage-maker --> :core:domain
    :feature:collage-maker --> :core:resources
    :feature:collage-maker --> :core:settings
    :feature:collage-maker --> :core:di
    :feature:collage-maker --> :core:crash
    :feature:compare --> :core:data
    :feature:compare --> :core:ui
    :feature:compare --> :core:domain
    :feature:compare --> :core:resources
    :feature:compare --> :core:settings
    :feature:compare --> :core:di
    :feature:compare --> :core:crash
    :feature:mesh-gradients --> :core:data
    :feature:mesh-gradients --> :core:ui
    :feature:mesh-gradients --> :core:domain
    :feature:mesh-gradients --> :core:resources
    :feature:mesh-gradients --> :core:settings
    :feature:mesh-gradients --> :core:di
    :feature:mesh-gradients --> :core:crash
    :core:settings --> :core:domain
    :core:settings --> :core:resources
    :core:settings --> :core:di
    :feature:scan-qr-code --> :core:data
    :feature:scan-qr-code --> :core:ui
    :feature:scan-qr-code --> :core:domain
    :feature:scan-qr-code --> :core:resources
    :feature:scan-qr-code --> :core:settings
    :feature:scan-qr-code --> :core:di
    :feature:scan-qr-code --> :core:crash
    :feature:scan-qr-code --> :core:filters
    :feature:svg-maker --> :core:data
    :feature:svg-maker --> :core:ui
    :feature:svg-maker --> :core:domain
    :feature:svg-maker --> :core:resources
    :feature:svg-maker --> :core:settings
    :feature:svg-maker --> :core:di
    :feature:svg-maker --> :core:crash
    :feature:weight-resize --> :core:data
    :feature:weight-resize --> :core:ui
    :feature:weight-resize --> :core:domain
    :feature:weight-resize --> :core:resources
    :feature:weight-resize --> :core:settings
    :feature:weight-resize --> :core:di
    :feature:weight-resize --> :core:crash
    :feature:image-splitting --> :core:data
    :feature:image-splitting --> :core:ui
    :feature:image-splitting --> :core:domain
    :feature:image-splitting --> :core:resources
    :feature:image-splitting --> :core:settings
    :feature:image-splitting --> :core:di
    :feature:image-splitting --> :core:crash
    :benchmark --> :app
    :feature:checksum-tools --> :core:data
    :feature:checksum-tools --> :core:ui
    :feature:checksum-tools --> :core:domain
    :feature:checksum-tools --> :core:resources
    :feature:checksum-tools --> :core:settings
    :feature:checksum-tools --> :core:di
    :feature:checksum-tools --> :core:crash
    :feature:base64-tools --> :core:data
    :feature:base64-tools --> :core:ui
    :feature:base64-tools --> :core:domain
    :feature:base64-tools --> :core:resources
    :feature:base64-tools --> :core:settings
    :feature:base64-tools --> :core:di
    :feature:base64-tools --> :core:crash
    :feature:palette-tools --> :core:data
    :feature:palette-tools --> :core:ui
    :feature:palette-tools --> :core:domain
    :feature:palette-tools --> :core:resources
    :feature:palette-tools --> :core:settings
    :feature:palette-tools --> :core:di
    :feature:palette-tools --> :core:crash
    :feature:palette-tools --> :feature:pick-color
    :feature:settings --> :core:data
    :feature:settings --> :core:ui
    :feature:settings --> :core:domain
    :feature:settings --> :core:resources
    :feature:settings --> :core:settings
    :feature:settings --> :core:di
    :feature:settings --> :core:crash
    :core:data --> :core:utils
    :core:data --> :core:domain
    :core:data --> :core:resources
    :core:data --> :core:filters
    :core:data --> :core:settings
    :core:data --> :core:di
    :feature:pick-color --> :core:data
    :feature:pick-color --> :core:ui
    :feature:pick-color --> :core:domain
    :feature:pick-color --> :core:resources
    :feature:pick-color --> :core:settings
    :feature:pick-color --> :core:di
    :feature:pick-color --> :core:crash
    :feature:load-net-image --> :core:data
    :feature:load-net-image --> :core:ui
    :feature:load-net-image --> :core:domain
    :feature:load-net-image --> :core:resources
    :feature:load-net-image --> :core:settings
    :feature:load-net-image --> :core:di
    :feature:load-net-image --> :core:crash
    :feature:quick-tiles --> :core:data
    :feature:quick-tiles --> :core:ui
    :feature:quick-tiles --> :core:domain
    :feature:quick-tiles --> :core:resources
    :feature:quick-tiles --> :core:settings
    :feature:quick-tiles --> :core:di
    :feature:quick-tiles --> :core:crash
    :feature:quick-tiles --> :feature:erase-background
    :feature:recognize-text --> :core:data
    :feature:recognize-text --> :core:ui
    :feature:recognize-text --> :core:domain
    :feature:recognize-text --> :core:resources
    :feature:recognize-text --> :core:settings
    :feature:recognize-text --> :core:di
    :feature:recognize-text --> :core:crash
    :feature:recognize-text --> :core:filters
    :feature:recognize-text --> :feature:single-edit
    :core:domain --> :core:resources
    :feature:single-edit --> :core:data
    :feature:single-edit --> :core:ui
    :feature:single-edit --> :core:domain
    :feature:single-edit --> :core:resources
    :feature:single-edit --> :core:settings
    :feature:single-edit --> :core:di
    :feature:single-edit --> :core:crash
    :feature:single-edit --> :feature:crop
    :feature:single-edit --> :feature:erase-background
    :feature:single-edit --> :feature:draw
    :feature:single-edit --> :feature:filters
    :feature:single-edit --> :feature:pick-color
    :feature:single-edit --> :feature:compare
    :feature:image-cutting --> :core:data
    :feature:image-cutting --> :core:ui
    :feature:image-cutting --> :core:domain
    :feature:image-cutting --> :core:resources
    :feature:image-cutting --> :core:settings
    :feature:image-cutting --> :core:di
    :feature:image-cutting --> :core:crash
    :feature:image-cutting --> :feature:compare
    :feature:crop --> :core:data
    :feature:crop --> :core:ui
    :feature:crop --> :core:domain
    :feature:crop --> :core:resources
    :feature:crop --> :core:settings
    :feature:crop --> :core:di
    :feature:crop --> :core:crash
    :feature:color-tools --> :core:data
    :feature:color-tools --> :core:ui
    :feature:color-tools --> :core:domain
    :feature:color-tools --> :core:resources
    :feature:color-tools --> :core:settings
    :feature:color-tools --> :core:di
    :feature:color-tools --> :core:crash
    :feature:format-conversion --> :core:data
    :feature:format-conversion --> :core:ui
    :feature:format-conversion --> :core:domain
    :feature:format-conversion --> :core:resources
    :feature:format-conversion --> :core:settings
    :feature:format-conversion --> :core:di
    :feature:format-conversion --> :core:crash
    :feature:format-conversion --> :feature:compare
    :feature:ascii-art --> :core:data
    :feature:ascii-art --> :core:ui
    :feature:ascii-art --> :core:domain
    :feature:ascii-art --> :core:resources
    :feature:ascii-art --> :core:settings
    :feature:ascii-art --> :core:di
    :feature:ascii-art --> :core:crash
    :feature:ascii-art --> :feature:filters
    :core:filters --> :core:domain
    :core:filters --> :core:ui
    :core:filters --> :core:resources
    :core:filters --> :core:settings
    :core:filters --> :core:utils
    :feature:apng-tools --> :core:data
    :feature:apng-tools --> :core:ui
    :feature:apng-tools --> :core:domain
    :feature:apng-tools --> :core:resources
    :feature:apng-tools --> :core:settings
    :feature:apng-tools --> :core:di
    :feature:apng-tools --> :core:crash
    :feature:image-preview --> :core:data
    :feature:image-preview --> :core:ui
    :feature:image-preview --> :core:domain
    :feature:image-preview --> :core:resources
    :feature:image-preview --> :core:settings
    :feature:image-preview --> :core:di
    :feature:image-preview --> :core:crash
    :feature:filters --> :core:filters
    :feature:filters --> :core:data
    :feature:filters --> :core:ui
    :feature:filters --> :core:domain
    :feature:filters --> :core:resources
    :feature:filters --> :core:settings
    :feature:filters --> :core:di
    :feature:filters --> :core:crash
    :feature:filters --> :core:ksp
    :feature:filters --> :feature:draw
    :feature:filters --> :feature:pick-color
    :feature:filters --> :feature:compare
    :feature:image-stacking --> :core:data
    :feature:image-stacking --> :core:ui
    :feature:image-stacking --> :core:domain
    :feature:image-stacking --> :core:resources
    :feature:image-stacking --> :core:settings
    :feature:image-stacking --> :core:di
    :feature:image-stacking --> :core:crash
    :feature:main --> :core:data
    :feature:main --> :core:ui
    :feature:main --> :core:domain
    :feature:main --> :core:resources
    :feature:main --> :core:settings
    :feature:main --> :core:di
    :feature:main --> :core:crash
    :feature:main --> :feature:settings
```