package main.sqldelight.com.shopping.db

import com.shopping.domain.model.Product
import com.shopping.domain.model.valueObject.Email
import com.shopping.domain.model.valueObject.ID
import com.shopping.domain.model.valueObject.Password
import com.squareup.sqldelight.ColumnAdapter
import java.time.LocalDate

object IDColumnAdapter : ColumnAdapter<ID, String> {

    override fun decode(databaseValue: String): ID = ID(databaseValue)

    override fun encode(value: ID): String = value.toString()

}

object EmailColumnAdapter : ColumnAdapter<Email, String> {

    override fun decode(databaseValue: String): Email = Email(databaseValue)

    override fun encode(value: Email): String = value.toString()

}


object PasswordColumnAdapter : ColumnAdapter<Password, String> {

    override fun decode(databaseValue: String): Password = Password(databaseValue)

    override fun encode(value: Password): String = value.toString()

}


object LocalDateColumnAdapter : ColumnAdapter<LocalDate, String> {

    override fun decode(databaseValue: String): LocalDate = LocalDate.parse(databaseValue)

    override fun encode(value: LocalDate): String = value.toString()

}

object CategoryColumnAdapter : ColumnAdapter<Product.Category, String> {

    override fun decode(databaseValue: String): Product.Category = Product.Category.valueOf(databaseValue)

    override fun encode(value: Product.Category): String = value.name

}