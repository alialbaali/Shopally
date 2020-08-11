package com.shopping.domain.model

import com.shopping.domain.model.inline.*
import java.time.LocalDate

data class Customer(
    val id: Id = Id(),
    val name: Name,
    val email: Email,
    val password: Password,
    val image: Image = Image.DEFAULT,
    val creationDate: LocalDate = LocalDate.now()
)