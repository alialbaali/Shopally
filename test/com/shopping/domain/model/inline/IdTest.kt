package com.shopping.domain.model.inline

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class IdTest : BehaviorSpec() {

    init {

        Given("a generated String Id") {
            When("creating an Id using the String Id") {
                Then("it should return an Id") {

                    val id = Id.generate().toString()

                    Id.create(id).getOrNull().apply {
                        this.shouldNotBeNull()
                        this.toString() shouldBe id
                    }

                }
            }
        }

    }

}