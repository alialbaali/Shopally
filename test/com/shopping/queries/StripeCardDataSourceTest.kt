package com.shopping.queries

// class StripeCardDataSourceTest : DefaultSpec(dataSourceModule) {
//
//    private val stripeCardDataSource by inject<StripeCardDataSource>()
//
//    private val stripeCustomerDataSource by inject<StripeCustomerDataSource>()
//
//    init {
//
//        Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc"
//
//        Given("a number, expMonth, expYear and cvc") {
//            And("a stripe customer id") {
//                When("creating stripe card") {
//                    Then("it should create it successfully") {
//
//                        val stripeCustomerId = stripeCustomerDataSource.createStripeCustomer(
//                            name = "John doe",
//                            email = "johndoe@mail.com"
//                        ).getOrThrow().id
//
//                        val number = "5555555555554444"
//                        val expMonth = 8
//                        val expYear = 2021
//                        val cvc = 232L
// //
// //                        val stripeCard = shouldNotThrowAny {
// //                            stripeCardDataSource.createStripeCard(stripeCustomerId, number, expMonth, expYear, cvc)
// //                                .getOrThrow()
// //                        }
//
// //                        number shouldContain stripeCard.last4
// //                        println(stripeCard)
// //                        stripeCard.expMonth shouldBe expMonth
// //                        stripeCard.expYear shouldBe expYear
//                    }
//                }
//            }
//        }
//    }
// }
