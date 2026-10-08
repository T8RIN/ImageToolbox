# Image Toolbox → iOS: инвентаризация фич

Что здесь: полный список функций Android-версии, сгруппированный по сложности порта, с нативным
аналогом на iOS и ключевыми зависимостями. Статус портирования ведётся отдельно в
[TODO.md](TODO.md).

Источники: [README.md](../../README.md) и исходники Android (`feature/*`, `lib/*`, `core/*`).
LOC — число строк Kotlin в `src` модуля, посчитано по репозиторию (всего ~514k строк Kotlin).

## Легенда сложности

Оценка предварительная: окончательно уточняется, когда фича доходит до очереди.

- **S** — чистая логика или стандартный UI, прямой аналог в Apple SDK. Порт за один заход.
- **M** — UI плюс несколько фреймворков Apple, либо порт нетривиального алгоритма.
- **L** — крупный объём кода или алгоритмов, либо нужна сторонняя C/C++-библиотека.
- **XL** — большой объём плюс AI-модели, GPU-шейдеры или нет прямого аналога на iOS.

Порядок работ: от S к XL. Внутри уровня порядок произвольный.

---

## 0. Фундамент (сквозное, до фич)

Эти части нужны всем инструментам. Они не отдельные фичи, но без них ни одна фича не портируется.

| Компонент | Android | Путь на iOS | Сложность |
|---|---|---|---|
| Навигация и экраны | Decompose, `feature/root`, `feature/main` | `NavigationStack`, сетка инструментов | M |
| Дизайн-система | `core/ui` (63k LOC), `lib/dynamic-theme`, `lib/modalsheet` | SwiftUI, Liquid Glass (задел), токены вместо Material You | L |
| Ресурсы и локализация | `core/resources` (74k LOC, строки, иконки) | String Catalog (`.xcstrings`), перенос строк, Weblate сохраняется | M |
| Настройки | `core/settings`, DataStore | `UserDefaults` / `@AppStorage`, Codable | M |
| Выбор изображений | `feature/media-picker`, `ImageGetter` | `PhotosPicker`, `fileImporter` | M |
| Сохранение и шаринг | `core/domain` (saving, `FileController`, `ShareProvider`) | `fileExporter`, `ShareLink`, PhotoKit | M |
| Пакетная обработка | батч-режим во всех инструментах | `TaskGroup`, общий прогресс | M |
| Undo / redo | в редакторах | собственный стек команд | M |
| DI | Hilt, `core/di`, `core/ksp` | ручной composition root через протоколы | S–M |
| Логи и краши | `core/crash`, `feature/app-logs` | `os.Logger`, MetricKit, экспорт логов | S |

---

## 1. Фичи по уровням сложности

### S — простые

| Фича | Модуль | LOC | Путь на iOS |
|---|---|---|---|
| Base64: картинка ↔ строка | `base64-tools` | 937 | кодек готов (`Base64Codec`), картинка через ImageIO |
| Удаление EXIF | `delete-exif` | 775 | пересохранение через ImageIO без метаданных |
| Редактирование EXIF | `edit-exif` | 467 | `CGImageMetadata` + ImageIO |
| Загрузка изображения по URL | `load-net-image` | 1039 | `URLSession` |
| Пипетка (выбор цвета с картинки) | `pick-color` | 1126 | SwiftUI + Core Image, тап по пикселю |
| Библиотека цветов (33k имён) | `color-library` | 507 | данные + поиск |
| Просмотр изображений (SVG, DNG, PSD, DJVU и др.) | `image-preview` | 623 | QuickLook; DJVU и часть форматов не покрыты, см. раздел 3 |
| ASCII-арт | `ascii-art`, `lib/ascii` | 583 | рендер текста через CoreGraphics |
| Перлин-шум | `noise-generation` | 1141 | порт алгоритма, при нужде Metal |
| Снежинки | `lib/snowfall` | 584 | SwiftUI `Canvas` или SpriteKit |
| Лицензии библиотек | `libraries-info` | 690 | экран со списком зависимостей |
| Справка и подсказки | `help` | 3117 | экран с поиском |
| Статистика использования | `usage-statistics` | 886 | локальное хранение, SwiftUI-графики |
| Логи приложения | `app-logs` | 768 | `os.Logger` + экспорт файла |
| Поворот и отражение | общая операция в `core` | — | `CGImage` / Core Image |
| Пасхалка | `easter-egg` | 365 | — |

### M — средние

| Фича | Модуль | LOC | Путь на iOS |
|---|---|---|---|
| Ресайз и конвертация | `resize-convert` | 1308 | vImage / Core Graphics |
| Ресайз до заданного веса файла | `weight-resize` | 1289 | итеративное сжатие через ImageIO |
| Ресайз по лимитам | `limits-resize` | 1260 | vImage |
| Кроп с масками-формами (~30 форм) | `crop`, `lib/cropper` | 2321 + 7212 | SwiftUI-жесты, маска через `Path` |
| Нарезка | `image-cutting` | 1630 | Core Graphics, пакетный режим |
| Разделение на части | `image-splitting` | 1220 | Core Graphics |
| Склейка | `image-stitch` | 4241 | Core Graphics / vImage |
| Стекинг | `image-stacking` | 1438 | Core Image / vImage |
| Сравнение (слайдер, toggle, 6 метрик, GIF до/после) | `compare` | 4463 | GIF через ImageIO, метрики через vDSP |
| Водяные знаки (текст, картинка, штамп, timestamp, стеганография) | `watermarking` | 2791 | Core Graphics; стеганография — порт алгоритма |
| Редактирование одного файла и экспорт-профили (61 профиль) | `single-edit` | 4011 | профили — данные, редактор — SwiftUI |
| Пакетное переименование по шаблону | `batch-rename` | 2018 | `FileManager` + шаблоны |
| Поиск дубликатов (точные и похожие) | `duplicate-finder` | 2469 | Vision `VNGenerateImageFeaturePrintRequest`, точные — хеш |
| Чексуммы (64 алгоритма в Android) | `checksum-tools` | 1618 | Реализовано 68: MD2/MD4/MD5, SHA-1/2/3, Keccak, SHAKE, BLAKE2, RIPEMD-160, Tiger, Whirlpool, SM3, GOST 34.11-94, Streebog-256/512, Skein-512-512, CRC, FNV, xxHash и др. Сверка с bcprov-jdk18on 1.86: не хватает 32 настоящих алгоритмов (Skein-256/512/1024 с другими длинами, Skein-1024, RIPEMD-128/256/320, ParallelHash, TupleHash, DSTU7564, Haraka, BLAKE3, KECCAK-288, SHAKE с другой длиной); 32 наших имени (CRC, FNV, xxHash и др.) в списке Android нет |
| Цветовые утилиты (палитры, гармонии, смешивание) | `color-tools` | 1443 | порт алгоритмов |
| GIF-инструменты | `gif-tools` | 2766 | ImageIO, анимация |
| APNG | `apng-tools` | 2391 | ImageIO не пишет APNG: свой энкодер (чанки acTL, fcTL, fdAT) |
| WebP | `webp-tools` | 2379 | декод — ImageIO, энкод — libwebp (ImageIO на iOS 26 WebP не пишет, проверено) |
| Сканер документов | `document-scanner`, `lib/documentscanner` | 626 + 2674 | VisionKit `VNDocumentCameraViewController` |
| Сканер QR и штрихкодов | `scan-qr-code`, `lib/qrose` | 4426 + 5924 | AVFoundation / Vision (barcode) |
| Экспорт обоев | `wallpapers-export` | 1154 | Core Graphics |
| Извлечение обложки из аудио | `audio-cover-extractor` | 823 | AVFoundation, метаданные |
| Скриншот-рамка | `screenshot-framing` | 1238 | Core Graphics |
| Превью кода (190+ языков, 256 тем) | `code-preview` | 2186 | подсветка синтаксиса через библиотеку |
| Градиенты и mesh-градиенты | `gradient-maker`, `mesh-gradients` | 3921 + 253 | Core Graphics; mesh — Metal |
| Палитра в PDF | `palette-pdf` | 1306 | PDFKit / Core Graphics |
| Фотомозаика | `photomosaic` | 2014 | Core Graphics + vImage |

### L — большие

| Фича | Модуль | LOC | Путь на iOS |
|---|---|---|---|
| Кривые тонов и осциллограф, гистограммы | `curves`, `lib/curves` | 1073 + 6609 | Metal / Core Image; scopes — compute |
| Конвертация форматов (HEIF, AVIF, JXL, JP2, QOI, TIFF, ICO, Telegram-стикер и др.) | `format-conversion` | 908 | ImageIO для базовых, остальные кодеки — XCFramework |
| Мульти-кадровое слияние | `multi-frame-fusion` | 2222 | Vision + Core Image |
| Лаборатория сжатия | `compression-lab` | 1285 | несколько кодеков сразу |
| Рисование (кисти, фигуры, warp, spot healing, лассо) | `draw` | 13472 | PencilKit частично; остальное — Metal / Core Graphics |
| Маркап (стикеры, текст, слои) | `markup-layers` | 13709 | SwiftUI + Core Graphics |
| OCR (120+ языков, searchable PDF) | `recognize-text` | 5932 | Vision `VNRecognizeTextRequest` или Tesseract — решение, см. раздел 4 |
| Удаление фона (вручную и автоматически, 9 моделей) | `erase-background` | 2277 | Vision `VNGenerateForegroundInstanceMaskRequest` (iOS 17+); модели — ONNX Runtime или Core ML |
| Коллажи | `collage-maker`, `lib/collages` | 1382 + 14907 | SwiftUI + Core Graphics |
| Архивы (ZIP, 7z, TAR, RAR, CPIO, AR, ISO, сжатия) | `archive-tools`, `lib/archive` | 2547 + 2796 | libarchive (XCFramework); RAR — см. раздел 4 |
| Палитры: импорт и экспорт в 41 формат | `palette-tools`, `lib/palette` | 2545 + 9740 | порт парсеров |
| PDF-инструменты (объединение, разделение, сжатие, конвертация комиксов) | `pdf-tools` | 18541 | PDFKit + Core Graphics; OCR через Vision |
| Шифрование файлов (100+ алгоритмов) | `cipher` | 1737 | CryptoKit (AES, ChaCha20); остальное — порт |
| Трассировка растра в SVG | `svg-maker` | 2435 | C/C++-трассировщик через XCFramework |
| Фракталы (75 типов, 2D и 3D) | `fractal-generation` | 5859 | Metal compute |
| Текстуры (170+ пресетов) | `texture-generation` | 7402 | Metal-шейдеры |
| JPEG XL (кодирование и анимация) | `jxl-tools` | 2815 | libjxl (XCFramework). ImageIO на iOS 26 JXL только читает |

### XL — очень большие

| Фича | Модуль | LOC | Путь на iOS |
|---|---|---|---|
| Фильтры: 500+ фильтров, цепочки, пользовательские шаблоны | `core/filters` (670 файлов, 41852 LOC) + `feature/filters` (33287 LOC) | ~75k | Core Image покрывает часть; остальное — Metal и порт CPU-алгоритмов (Aire, Trickle, GPUImage) |
| AI-инструменты (100+ моделей) | `ai-tools`, `lib/neural-tools` | 10995 + 3095 | ONNX Runtime iOS или Core ML; модели конвертируются отдельно |
| Shader Studio (редактор GLSL-шейдеров) | `shader-studio` | 1817 | GLSL → Metal: нужен транслятор или свой DSL, см. раздел 4 |

---

## 2. Что в README, но не отдельный модуль

Эти пункты входят в фичи выше, но учитываются отдельно при проверке полноты:

- Выбор источника: встроенный пикер, системный, галерея, файлы, камера, вся папка → раздел 0.
- 61 экспорт-профиль под соцсети и веб → `single-edit`, данные.
- Случайное имя файла, имя по чексумме, режим «сохранить рядом» → раздел 0 (сохранение).
- Гистограммы (RGB, яркость, «как камера») → `color-tools` или `curves`, уточнить при портировании.
- Undo / redo в редакторах → раздел 0.

---

## 3. Платформенные расхождения (1:1 не будет)

Эти пункты нельзя перенести буквально. Их нужно решить продуктово, до портирования соответствующих фич.

- **Сохранение в произвольную папку и «рядом с оригиналом».** Песочница iOS не даёт писать в любую папку. Сохранение идёт через `fileExporter` / Files, каждый раз с выбором места. Режим «сохранить рядом» не воспроизводится.
- **Удаление оригиналов после экспорта.** Удалять фото из галереи можно только через системный запрос PhotoKit, молча не выйдет.
- **Quick Settings плитки.** Аналог — Control Center controls (iOS 18+) и App Intents. Это не 1:1, нужна отдельная задача.
- **Открытие из других приложений.** На Android есть intent-фильтры. На iOS — Share Extension и «Открыть в…». Отдельная фича.
- **Формат DJVU и часть RAW/PSD.** QuickLook покрывает не всё. Нужна либо библиотека, либо отказ от формата на iOS.
- **Фоновые задачи.** Длинные батчи на iOS ограничены по времени. Нужна обработка в приложении на переднем плане или `BGTaskScheduler` с ограничениями.

---

## 4. Нативные зависимости: Android → iOS

| Android | Назначение | Путь на iOS | Статус решения |
|---|---|---|---|
| GPUImage | GPU-фильтры | Core Image / Metal | принято |
| Aire, Trickle (C++) | CPU-фильтры | vImage / Accelerate / Metal, порт алгоритмов | принято, порт по мере фич |
| avif-coder, jxl-coder | AVIF, HEIF, JXL | ImageIO пишет AVIF и HEIC (проверено на iOS 26.3). JXL ImageIO только читает, запись — libjxl | AVIF/HEIC — принято, JXL — отложено до `jxl-tools` |
| Tesseract4Android, PaddleOCR | OCR | Vision `VNRecognizeTextRequest` или Tesseract iOS | **решение нужно** |
| OpenCV 5 | компьютерное зрение | официальный `opencv2.framework` | принято |
| ONNX Runtime Android | AI-модели | ONNX Runtime iOS (SPM) или Core ML | **решение нужно** |
| ML Kit segmentation, subject segmentation | удаление фона | Vision (iOS 17+) | принято |
| ML Kit document scanner | сканер | VisionKit | принято |
| ZXing core | QR и штрихкоды | Vision barcode / AVFoundation | принято |
| libarchive-android, commons-compress, xz | архивы | libarchive, XZ, Zstd, LZ4, Brotli как C-библиотеки | принято |
| junrar | чтение RAR | unrar (лицензия проверить) | **решение нужно** |
| pdfbox-android | PDF | PDFKit + Core Graphics | принято |
| BouncyCastle | криптография | CryptoKit + порт остального | принято |
| Coil (GIF, SVG, resvg) | изображения | ImageIO (GIF, PNG, JPEG), SVG через resvg XCFramework | SVG — решение нужно |
| Lottie (dotlottie) | анимации | lottie-ios (SPM) | принято |
| Konfetti | частицы | SwiftUI `Canvas` / SpriteKit | принято |
| Decompose | навигация и компоненты | `NavigationStack` + `@Observable` | принято |
| Hilt | DI | ручной composition root | принято |
| DataStore | настройки | `UserDefaults` / Codable | принято |
| AboutLibraries | экран лицензий | список SPM-зависимостей | принято |

**Проверка ImageIO на iOS 26.3** (симулятор iPhone 17 Pro, тест `ImageIOCapabilityTests`). Пишутся: GIF, BMP, HEIC, AVIF, JPEG, PNG, TIFF. WebP ImageIO не пишет (кодирует libwebp). JXL читается, но не пишется (`public.jpeg-xl` есть только в списке чтения). APNG ImageIO не пишет, его собирает свой писатель. Проверено в симуляторе, на физическом устройстве не проверено.

**Решения, которые нужно принять до соответствующих фич:** OCR (Vision или Tesseract), движок AI-моделей (ONNX Runtime или Core ML), RAR (лицензия unrar), SVG-рендеринг, GLSL → Metal для Shader Studio.
