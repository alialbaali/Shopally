package com.shopping

import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.service.AuthService
import com.shopping.service.JWTAuthService
import org.koin.dsl.module

val fakeRepositoryModule = module {

    single<CustomerRepository> { FakeCustomerRepository() }

}

val testServiceModule = module {

    single<AuthService> { JWTAuthService(get()) }

}