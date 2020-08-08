package com.shopping.di

import com.shopping.data.repository.CustomerRepository
import com.shopping.data.repository.CustomerRepositoryImpl
import com.shopping.db.CustomersQueries
import com.shopping.db.ShoppingDatabase
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

private const val LOCAL_DATABASE_URL = "jdbc:sqlite:identifier.sqlite"

val dbModule = module {

    single<JdbcSqliteDriver> { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY) }

    single<ShoppingDatabase> {
        ShoppingDatabase.Schema.create(get<JdbcSqliteDriver>())
        ShoppingDatabase(get<JdbcSqliteDriver>())
    }

}

val dataSourceModule = module {

    single<CustomersQueries> { get<ShoppingDatabase>().customersQueries }

}

val repositoryModule = module {

    single<CustomerRepository> { CustomerRepositoryImpl(get<CustomersQueries>()) }

}