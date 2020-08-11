package main.sqldelight.com.shopping.db

import com.shopping.domain.model.inline.*
import com.squareup.sqldelight.ColumnAdapter
import java.time.LocalDate
import java.util.*

object IdColumnAdapter : ColumnAdapter<Id, String> {

    override fun decode(databaseValue: String): Id = Id(UUID.fromString(databaseValue))

    override fun encode(value: Id): String = value.value.toString()

}

object NameColumnAdapter : ColumnAdapter<Name, String> {

    override fun decode(databaseValue: String): Name = Name(databaseValue)

    override fun encode(value: Name): String = value.value

}


object EmailColumnAdapter : ColumnAdapter<Email, String> {

    override fun decode(databaseValue: String): Email = Email(databaseValue)

    override fun encode(value: Email): String = value.value

}


object PasswordColumnAdapter : ColumnAdapter<Password, String> {

    override fun decode(databaseValue: String): Password = Password(databaseValue)

    override fun encode(value: Password): String = value.value

}


object LocalDateColumnAdapter : ColumnAdapter<LocalDate, String> {

    override fun decode(databaseValue: String): LocalDate = LocalDate.parse(databaseValue)

    override fun encode(value: LocalDate): String = value.toString()

}

object ImageColumnAdapter : ColumnAdapter<Image, String> {

    override fun decode(databaseValue: String): Image = Image(databaseValue)

    override fun encode(value: Image): String = value.value

}




