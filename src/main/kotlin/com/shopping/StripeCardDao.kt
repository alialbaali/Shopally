package com.shopping

import com.shopping.domain.StripeCard
import com.shopping.domain.StripeCardDataSource
import com.stripe.model.Customer
import com.stripe.param.PaymentSourceCollectionCreateParams

private const val StripeNumber = "number"
private const val StripeExpMonth = "exp_month"
private const val StripeExpYear = "exp_year"
private const val StripeCvc = "cvc"
private const val Source = "source"
private const val MasterCard = "tok_mastercard"
private const val Visa = "tok_visa"

class StripeCardDao : StripeCardDataSource {

    override suspend fun getStripeCardById(stripeCustomerId: String, stripeCardId: String): Result<StripeCard> = runCatching {
        StripeCustomer.retrieve(stripeCustomerId).sources.retrieve(stripeCardId) as StripeCard
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getStripeCardsByCustomerId(stripeCustomerId: String): Result<List<StripeCard>> = runCatching {
        StripeCustomer.retrieve(stripeCustomerId).sources.list(emptyMap()).data as List<StripeCard>
    }

    override suspend fun createStripeCard(
        stripeCustomerId: String,
        number: String,
        expMonth: Int,
        expYear: Int,
        cvc: Long,
    ): Result<StripeCard> = runCatching {
        val params = PaymentSourceCollectionCreateParams.builder()
//            .putExtraParam(StripeNumber, number)
//            .putExtraParam(StripeExpMonth, expMonth)
//            .putExtraParam(StripeExpYear, expYear)
//            .putExtraParam(StripeCvc, cvc)
            .putExtraParam(Source, if (cvc > 500) MasterCard else Visa)
            .build()
        Customer.retrieve(stripeCustomerId).sources.create(params) as StripeCard
    }

    override suspend fun deleteStripeCard(stripeCustomerId: String, stripeCardId: String): Result<String> = runCatching {
        val stripeCard = StripeCustomer.retrieve(stripeCustomerId).sources.retrieve(stripeCardId) as StripeCard
        stripeCard.delete()
        stripeCardId
    }
}