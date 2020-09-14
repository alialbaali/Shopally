package com.shopping.domain.model.valueObject

import com.shopping.domain.model.Order

typealias CartItem = Order.OrderItem

inline class Cart(val value: Set<CartItem>) {

    companion object {
        val Empty = Cart(emptySet())
    }
}
