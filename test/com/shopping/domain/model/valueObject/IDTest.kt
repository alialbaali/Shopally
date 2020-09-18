package com.shopping.domain.model.valueObject

import com.shopping.DefaultSpec
import com.shopping.asID
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class IDTest : DefaultSpec() {

    init {

        Given("a generated string id") {
            When("creating an ID using the string id") {
                Then("it should return an id") {

                    val id = ID.random().toString()

                    ID.from(id).getOrNull().apply {
                        this.shouldNotBeNull()
                        this.toString() shouldBe id
                    }
                }
            }
        }

        Given("an invalid string id") {
            When("creating an ID using the invalid string id") {
                Then("it should return fail and throw an exception") {

                    val id = "12034-3242-234203-423423"

                    shouldThrowAny {
                        id.asID()
                    }
                }
            }
        }
    }
}
