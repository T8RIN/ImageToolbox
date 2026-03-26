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

@file:Suppress("unused")

package com.t8rin.colors.util

// Regex for checking if this string is a 6 char hex or 8 char hex
val hexWithAlphaRegex = "^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})\$".toRegex()
val hexRegex = "^#?([0-9a-fA-F]{6})\$".toRegex()

// Check only on char if it's in range of 0-9, a-f, A-F
val hexRegexSingleChar = "[a-fA-F0-9]".toRegex()