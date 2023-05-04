
<h1 align="center">ImageResizer</h1>

<p align="center">
  <img alt="API" src="https://img.shields.io/badge/Api%2021+-50f270?logo=android&logoColor=black&style=for-the-badge"/></a>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-a503fc?logo=kotlin&logoColor=white&style=for-the-badge"/></a>
  <img alt="Jetpack Compose" src="https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label="/></a> 
  <img alt="material" src="https://custom-icon-badges.demolab.com/badge/material%20you-lightblue?style=for-the-badge&logoColor=333&logo=material-you"/></a>
  </br>
  <a href="https://github.com/T8RIN/ImageResizer/actions"><img alt="Build" src="https://img.shields.io/github/actions/workflow/status/t8rin/imageresizer/android.yml?logo=github&style=for-the-badge"/></a>
  <a href="https://hits.sh/github.com/t8rin/ImageResizer/"><img alt="Hits" src="https://hits.sh/github.com/t8rin/ImageResizer.svg?style=for-the-badge&label=Views&extraCount=7500&color=3b8963"/></a>
  <a href="https://github.com/t8rin/ImageResizer/releases">
      <img src="https://img.shields.io/github/downloads/t8rin/ImageResizer/total?color=orange&style=for-the-badge"/>
  </a>
</p>

<h4 align="center">Powerful image resizer with width/height selection, cropping, EXIF editing, quality and output image type picking and batch processing feature. Based on Material You design following official Google guidelines</h4>


![Frame 5](https://user-images.githubusercontent.com/52178347/229357418-3ed5e83b-df35-4e61-87ca-e74135e831d1.png)

<div align="center">

# Download
Go to the [Releases](https://github.com/t8rin/imageresizer/releases/latest) and download latest apk or click badges below.

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
      align="center"
      alt='Get it on Google Play'
      height="80">](https://play.google.com/store/apps/details?id=ru.tech.imageresizershrinker)
[<img src="https://gitlab.com/fdroid/artwork/-/raw/master/badge/get-it-on-en-au.png"
      alt='Get it on fdroid'
      align="center"
      height="80">](https://f-droid.org/packages/ru.tech.imageresizershrinker)
[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png"
      alt='Get it on izzy'
      align="center"
      height="80">](https://apt.izzysoft.de/fdroid/index/apk/ru.tech.imageresizershrinker)
[<img src="https://user-images.githubusercontent.com/69304392/148696068-0cfea65d-b18f-4685-82b5-329a330b1c0d.png"
      alt='Get it on GitHub'
      align="center"
      height="80">](https://github.com/t8rin/imageresizer/releases/latest)
[<img src="https://user-images.githubusercontent.com/52178347/236238307-c5ac806f-eaaf-4e61-874b-328474bcbb54.svg"
      alt='Get it on RuStore'
      align="center"
      height="54">](https://apps.rustore.ru/app/ru.tech.imageresizershrinker) 
      

# Buy me a coffee
This application is completely free, but if you want to support the project development, you can go to the [QIWI](https://qiwi.com/n/T8RIN) page :heart:

</div>

# Functions
- Batch processing
- Saving to any specific folder
- EXIF metadata editing
- Image Resizing
  - Width changing
  - Height changing
  - Adaptive resize
  - Resize retaining aspect ratio
- Image Shrinking
  - Quality compressing
  - Preset shrinking
  - Reducing size by given weight (in KB)
- Cropping
  - Regular crop
  - Crop by aspect ratio
- Format Convertion
  - WEBP
  - PNG
  - JPEG
  - JPG
  - Telegram sticker PNG format
- Color Utils
  - Palette generation
  - Picking color from image
- Additional Features
  - Rotating
  - Flipping
  - Comparing images
  

#
![Frame 6](https://user-images.githubusercontent.com/52178347/229357423-22ea1144-d838-438d-bc39-6ae431e491e6.png)

  
# UI tweaks
- Custom app color scheme
- Light/Dark mode
- AMOLED mode
- Monet implementation (Dynamic colors) for android 12 and above by [Dynamic Theme](https://github.com/T8RIN/DynamicTheme)
- Dynamic colors theming even for Android versions less than 12
- Image based color scheme

(Yes, app supports dynamic coloring based on wallpapers for every android version)


# Tech stack & Open-source libraries
- Minimum SDK level 21

- [Kotlin](https://kotlinlang.org/) based 

- [Dynamic Theme](https://github.com/T8RIN/DynamicTheme) - library, which allows you to easily implement custom color theming.

- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) for asynchronous work.

- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) to emit values from data layer reactively.

- [Accompanist](https://github.com/google/accompanist) to expand jetpack compose opportunities.

- [Hilt](https://dagger.dev/hilt/) for dependency injection.

- [Coil](https://github.com/coil-kt/coil) for loading images.

- [Compose Navigation Reimagined](https://github.com/olshevski/compose-navigation-reimagined) - High intelligence navigation library for Jetpack Compose.

- [Konfetti](https://github.com/DanielMartinus/Konfetti) to establish beatyfull particle system.

- Jetpack
  - [Compose](https://developer.android.com/jetpack/compose) - Modern Declarative UI style framework based on composable functions.
  
  - [Material You Kit](https://developer.android.com/jetpack/androidx/releases/compose-material3) - Material 3 powerful UI components.
  
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Manages UI-related data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations.
  
  - [Data Store](https://developer.android.com/jetpack/androidx/releases/datastore) - Store data asynchronously, consistently, and transactionally.
  
  - [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) - Observe Android lifecycles and handle UI states upon the lifecycle changes.
  
  - [Exif Interface](https://developer.android.com/jetpack/androidx/releases/exifinterface) - Read and write image file EXIF tags.

<div align="center">


# Translation
You can help translate ImageResizer into your language on [Hosted Weblate](https://hosted.weblate.org/engage/image-resizer/)

[![Состояние перевода](https://hosted.weblate.org/widgets/image-resizer/-/image-resizer/multi-auto.svg)](https://hosted.weblate.org/engage/image-resizer/)
</br>
[![Translation status](https://hosted.weblate.org/widgets/image-resizer/-/image-resizer/287x66-black.png)](https://hosted.weblate.org/engage/image-resizer/)


# Find this repository useful? :heart:
Support it by joining __[stargazers](https://github.com/t8rin/ImageResizer/stargazers)__ for this repository. :star: <br>
And __[follow](https://github.com/t8rin)__ me for my next creations! 🤩


# Star History
[![Star History Chart](https://api.star-history.com/svg?repos=t8rin/ImageResizer&type=Date)](https://star-history.com/#t8rin/ImageResizer&Date)

# Contributors
<a href="https://github.com/t8rin/imageresizer/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=t8rin/Imageresizer" />
</a>

# License
```xml
Designed and developed by 2023 T8RIN

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

</div>
