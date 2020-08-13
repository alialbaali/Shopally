package com.shopping.domain.model.inline

import com.shopping.hash
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldNotBeEqualComparingTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class PasswordTest : BehaviorSpec() {

    init {

        Given("an 8 characters and one number password") {
            When("creating an inline Password class") {
                Then("it should create a hashed password successfully") {

                    val password = "Password0"

                    val inlinedPassword = Password.create(password) { hash() }

                    val hashedPassword = inlinedPassword.getOrNull()

                    hashedPassword.shouldNotBeNull()
                    hashedPassword.value shouldNotBeEqualComparingTo password

                }
            }
        }

        Given("an 8 characters password ") {
            When("creating an inline Password class") {
                Then("it should fail") {

                    val password = "Password"

                    val inlinedPassword = Password.create(password) { hash() }

                    val hashedPassword = inlinedPassword.getOrNull()

                    hashedPassword.shouldBeNull()

                }
            }
        }


        Given("symbol characters and one number password") {
            When("creating an inline Password class") {
                Then("it should create a hashed password successfully") {

                    val password = "!@#$%^&(0"

                    val inlinedPassword = Password.create(password) { hash() }

                    val hashedPassword = inlinedPassword.getOrNull()

                    hashedPassword.shouldNotBeNull()
                    hashedPassword.value shouldNotBeEqualComparingTo password

                }
            }
        }

    }

}