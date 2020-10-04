package com.shopping.domain

import com.stripe.model.Card

typealias StripeCard = Card

interface StripeCardDataSource {

    suspend fun getStripeCardById(stripeCustomerId: String, stripeCardId: String): Result<StripeCard>

    suspend fun getStripeCardsByCustomerId(stripeCustomerId: String): Result<List<StripeCard>>

    suspend fun createStripeCard(stripeCustomerId: String, number: String, expMonth: Int, expYear: Int, cvc: Long): Result<StripeCard>

    suspend fun deleteStripeCard(stripeCustomerId: String, stripeCardId: String): Result<String>

    suspend fun chargeCardById(stripeCustomerId: String, stripeCardId: String, amount: Double): Result<Unit>
}
