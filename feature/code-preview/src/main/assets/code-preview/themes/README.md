# Highlight.js Themes

This directory contains **256 minified CSS theme files** for
[highlight.js](https://highlightjs.org/) version **11.11.1**, used by the
ImageToolbox Code Preview tool.

## Source

All themes were downloaded from the
[cdnjs CDN](https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/)
using the cdnjs API:

```
https://api.cdnjs.com/libraries/highlight.js/11.11.1?fields=files
```

Only the **minified** variants (`*.min.css`) are included - one file per theme.
The full list of available themes is also browsable at the
[highlight.js GitHub repository](https://github.com/highlightjs/highlight.js/tree/main/src/styles).

## Directory structure

```
themes/
├── a11y-dark.min.css          # 80 root-level themes
├── atom-one-dark.min.css
├── github.min.css
├── ...
└── base16/                    # 176 Base16-family themes
    ├── 3024.min.css
    ├── dracula.min.css
    └── ...
```

> **Note:** The `compose-highlight` library also bundles 4 themes as its own
> assets (`atom-one-dark`, `atom-one-light`, `tomorrow`, `tomorrow-night`).
> Those live in the library module under
> `compose-highlight/src/main/assets/compose-highlight/themes/` and are
> separate from this directory.

## How themes are loaded

Theme files are loaded at runtime and parsed using `HighlightTheme.fromCss()`.
CSS variables and compound background values are resolved first because they
cannot be represented directly by Compose `SpanStyle`.

```kotlin
// Discover all theme names at runtime from AssetManager
val root  = context.assets.list("code-preview/themes")
              ?.filter { it.endsWith(".min.css") }
              ?.map    { it.removeSuffix(".min.css") }
              ?: emptyList()

val base16 = context.assets.list("code-preview/themes/base16")
              ?.filter { it.endsWith(".min.css") }
              ?.map    { "base16/${it.removeSuffix(".min.css")}" }
              ?: emptyList()

val allThemes = (root + base16).sorted()   // 256 themes

// Load a specific theme by name
val theme = HighlightTheme.fromCss(
    cssText = themeCss,
    name    = selectedThemeName,
)

// Pass it to the composable
SyntaxHighlightedCode(
    code     = code,
    language = "javascript",
    theme    = theme,
)
```
