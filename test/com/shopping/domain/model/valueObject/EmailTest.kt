package com.shopping.domain.model.valueObject

import com.shopping.DefaultSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class EmailTest : DefaultSpec() {

    init {

        Given("a valid email") {
            When("creating an inline Email class") {
                Then("it should create it successfully") {

                    val result = Email.create("johndoe@mail.com").getOrNull()

                    result.shouldNotBeNull()
                    result.toString() shouldBe "johndoe@mail.com"
                }
            }
        }

        Given("an email without a top level domain") {
            When("creating an inline Email class") {
                Then("it should fail") {

                    val email = Email.create("johndoe@mail").getOrNull()

                    email.shouldBeNull()
                }
            }
        }

        Given("an email without a mail domain") {
            When("creating an inline Email class") {
                Then("it should fail") {

                    val email = Email.create("johndoe@").getOrNull()

                    email.shouldBeNull()
                }
            }
        }

        Given("an email without an @ and a top level domain") {
            When("creating an inline Email class") {
                Then("it should fail") {

                    val email = Email.create("johndoe").getOrNull()

                    email.shouldBeNull()
                }
            }
        }
    }
}
