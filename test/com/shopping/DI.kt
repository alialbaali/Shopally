package com.shopping

import com.shopping.domain.repository.CustomerRepository
import org.koin.dsl.module

val FakeRepositoryModule = module {

    single<CustomerRepository> { FakeCustomerRepository() }

}