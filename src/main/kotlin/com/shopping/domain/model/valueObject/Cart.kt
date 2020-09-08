package com.shopping.domain.model.valueObject

import com.shopping.domain.model.Order

inline class Cart(val value: Set<Order.OrderItem>) {

    companion object {
        val EMPTY = Cart(mutableSetOf())
    }
}
