package com.shopping

import com.shopping.db.*
import com.shopping.domain.CloudDataSource
import com.shopping.domain.StripeCardDataSource
import com.shopping.domain.StripeCustomerDataSource
import com.shopping.domain.repository.CustomerRepository
import com.shopping.domain.repository.OrderRepository
import com.shopping.domain.repository.ProductRepository
import com.shopping.domain.service.AuthService
import com.shopping.domain.service.CustomerService
import com.shopping.domain.service.OrderService
import com.shopping.domain.service.ProductService
import com.shopping.fake.FakeCustomerRepository
import com.shopping.fake.FakeOrderRepository
import com.shopping.fake.FakeProductRepository
import com.shopping.repository.CustomerRepositoryImpl
import com.shopping.repository.OrderRepositoryImpl
import com.shopping.repository.ProductRepositoryImpl
import com.shopping.service.CustomerServiceImpl
import com.shopping.service.JWTAuthService
import com.shopping.service.OrderServiceImpl
import com.shopping.service.ProductServiceImpl
import io.mockk.mockk
import org.koin.dsl.module

val testServiceModule = module(override = true) {

    single<AuthService> { JWTAuthService(get<CustomerRepository>()) }

    single<CustomerService> { CustomerServiceImpl(get<CustomerRepository>()) }

    single<OrderService> { OrderServiceImpl(get<OrderRepository>()) }

    single<ProductService> { ProductServiceImpl(get<ProductRepository>()) }

}

val fakeRepositoryModule = module(override = true) {

    single<CustomerRepository> { FakeCustomerRepository() }

    single<OrderRepository> { FakeOrderRepository() }

    single<ProductRepository> { FakeProductRepository() }
}

val testRepositoryModule = module(override = true) {

    single<CustomerRepository> {
        CustomerRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    single<ProductRepository> {
        ProductRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single<OrderRepository> {
        OrderRepositoryImpl(
            get(),
            get(),
        )
    }
}

val mockDataSourceModule = module(override = true) {

    single<CustomersQueries> { mockk() }

    single<CustomerCartQueries> { mockk() }

    single<CustomerAddressesQueries> { mockk() }

    single<CustomerCardsQueries> { mockk() }

    single<OrderItemsQueries> { mockk() }

    single<OrdersQueries> { mockk() }

    single<ProductsQueries> { mockk() }

    single<ProductImagesQueries> { mockk() }

    single<ProductSpecsQueries> { mockk() }

    single<ProductReviewsQueries> { mockk() }

    single<CloudDataSource> { mockk() }

    single<StripeCustomerDataSource> { mockk() }

    single<StripeCardDataSource> { mockk() }
}
