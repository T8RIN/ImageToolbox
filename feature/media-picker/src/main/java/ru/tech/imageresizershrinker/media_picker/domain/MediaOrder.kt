/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package ru.tech.imageresizershrinker.media_picker.domain

sealed class MediaOrder(private val orderType: OrderType) {
    class Label(orderType: OrderType) : MediaOrder(orderType)
    class Date(orderType: OrderType) : MediaOrder(orderType)
    class Expiry(orderType: OrderType = OrderType.Descending) : MediaOrder(orderType)

    fun copy(orderType: OrderType): MediaOrder {
        return when (this) {
            is Date -> Date(orderType)
            is Label -> Label(orderType)
            is Expiry -> Expiry(orderType)
        }
    }

    fun sortMedia(media: List<Media>): List<Media> {
        return when (orderType) {
            OrderType.Ascending -> {
                when (this) {
                    is Date -> media.sortedBy { it.timestamp }
                    is Label -> media.sortedBy { it.label.lowercase() }
                    is Expiry -> media.sortedBy { it.expiryTimestamp ?: it.timestamp }
                }
            }

            OrderType.Descending -> {
                when (this) {
                    is Date -> media.sortedByDescending { it.timestamp }
                    is Label -> media.sortedByDescending { it.label.lowercase() }
                    is Expiry -> media.sortedByDescending { it.expiryTimestamp ?: it.timestamp }
                }
            }
        }
    }

    fun sortAlbums(albums: List<Album>): List<Album> {
        return when (orderType) {
            OrderType.Ascending -> {
                when (this) {
                    is Date -> albums.sortedBy { it.timestamp }
                    is Label -> albums.sortedBy { it.label.lowercase() }
                    else -> albums
                }
            }

            OrderType.Descending -> {
                when (this) {
                    is Date -> albums.sortedByDescending { it.timestamp }
                    is Label -> albums.sortedByDescending { it.label.lowercase() }
                    else -> albums
                }
            }
        }
    }
}