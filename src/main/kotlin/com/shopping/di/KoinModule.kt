package com.shopping.di

import com.shopping.db.Customers
import com.shopping.db.CustomersQueries
import com.shopping.db.ShoppingDatabase
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.service.AuthService
import com.shopping.domain.service.CustomerService
import com.shopping.helper.JWTHelper
import com.shopping.repository.CustomerRepositoryImpl
import com.shopping.service.CustomerServiceImpl
import com.shopping.service.JWTAuthService
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import main.sqldelight.com.shopping.db.*
import org.koin.dsl.module

private const val LOCAL_DATABASE_URL = "jdbc:sqlite:identifier.sqlite"


val dbModule = module {

    single<JdbcSqliteDriver> { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY) }

    single<ShoppingDatabase> {
        ShoppingDatabase.Schema.create(get<JdbcSqliteDriver>())
        ShoppingDatabase(get<JdbcSqliteDriver>(), get())

    }

    single {
        Customers.Adapter(
            IdColumnAdapter,
            NameColumnAdapter,
            EmailColumnAdapter,
            PasswordColumnAdapter,
            ImageColumnAdapter,
            LocalDateColumnAdapter
        )
    }


}

val dataSourceModule = module {

    single<CustomersQueries> { get<ShoppingDatabase>().customersQueries }

}

val repositoryModule = module {

    single<CustomerRepository> { CustomerRepositoryImpl(get<CustomersQueries>()) }

}

val serviceModule = module {

    single<CustomerService> { CustomerServiceImpl(get<CustomerRepository>()) }

    single<AuthService> { JWTAuthService(get()) }

}

val helperModule = module {

    single { JWTHelper() }

}