package com.shopping.domain

import com.stripe.model.Customer

typealias StripeCustomer = Customer

interface StripeCustomerDataSource {

    suspend fun getStripeCustomerById(stripeCustomerId: String): Result<StripeCustomer>

    suspend fun createStripeCustomer(name: String, email: String): Result<StripeCustomer>

    suspend fun updateStripeCustomerById(stripeCustomerId: String, name: String, email: String): Result<StripeCustomer>

    suspend fun deleteStripeCustomerById(stripeCustomerId: String): Result<String>
}
