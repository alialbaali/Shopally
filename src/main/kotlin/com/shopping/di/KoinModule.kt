package com.shopping.di

import com.shopping.db.ShoppingDatabase
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module

val dbModule = module {

    single { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY) }

    single { ShoppingDatabase(get()) }

}