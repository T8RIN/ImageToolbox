/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package ru.tech.imageresizershrinker.media_picker.domain

sealed class OrderType {
    data object Ascending : OrderType()
    data object Descending : OrderType()
}
