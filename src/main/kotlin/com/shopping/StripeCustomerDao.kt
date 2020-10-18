package com.shopping

import com.shopping.domain.StripeCustomer
import com.shopping.domain.StripeCustomerDataSource
import com.stripe.model.Customer
import com.stripe.param.CustomerCreateParams
import com.stripe.param.CustomerUpdateParams

private typealias StripeCustomerCreateParams = CustomerCreateParams
private typealias StripeCustomerUpdateParams = CustomerUpdateParams

class StripeCustomerDao : StripeCustomerDataSource {

    override suspend fun getStripeCustomerById(stripeCustomerId: String): Result<StripeCustomer> = runCatching {
        StripeCustomer.retrieve(stripeCustomerId)
    }

    override suspend fun createStripeCustomer(name: String, email: String): Result<StripeCustomer> = runCatching {

        val createParams = StripeCustomerCreateParams.builder()
            .setName(name)
            .setEmail(email)
            .build()

        StripeCustomer.create(createParams)
    }

    override suspend fun updateStripeCustomerById(
        stripeCustomerId: String,
        name: String,
        email: String
    ): Result<StripeCustomer> = runCatching {

        val updateParams = StripeCustomerUpdateParams.builder()
            .setName(name)
            .setEmail(email)
            .build()

        StripeCustomer.retrieve(stripeCustomerId).update(updateParams)
    }

    override suspend fun deleteStripeCustomerById(stripeCustomerId: String): Result<String> = runCatching {
        StripeCustomer.retrieve(stripeCustomerId).delete()
        stripeCustomerId
    }
}
