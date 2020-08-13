package com.shopping.domain.model.inline

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class NameTest : BehaviorSpec() {

    init {

        Given("a non empty name") {
            When("creating an inline Name class") {
                Then("it should create it successfully") {

                    val name = Name.create("John").getOrNull()

                    name.shouldNotBeNull()
                    name.value shouldBe "John"

                }
            }
        }

        Given("an empty name") {
            When("creating an inline Name class") {
                Then("it should fail") {

                    val name = Name.create("").getOrNull()

                    name.shouldBeNull()

                }
            }
        }

        Given("a name with only whitespaces") {
            When("creating an inline Name class") {
                Then("it should fail") {

                    val name = Name.create("    ").getOrNull()

                    name.shouldBeNull()

                }
            }
        }

    }

}