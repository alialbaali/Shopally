package com.shopping

import com.cloudinary.Cloudinary
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
import com.shopping.helper.JWTHelper
import com.shopping.repository.CustomerRepositoryImpl
import com.shopping.repository.OrderRepositoryImpl
import com.shopping.repository.ProductRepositoryImpl
import com.shopping.service.CustomerServiceImpl
import com.shopping.service.JWTAuthService
import com.shopping.service.OrderServiceImpl
import com.shopping.service.ProductServiceImpl
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import main.sqldelight.com.shopping.db.*
import org.koin.dsl.module

private const val LOCAL_DATABASE_URL = "jdbc:sqlite:identifier.sqlite"

val dbModule = module {

    single<JdbcSqliteDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            ShoppingDatabase.Schema.create(this)
        }
    }

    factory<ShoppingDatabase> {
        ShoppingDatabase(
            get<JdbcSqliteDriver>(),
            get<CustomerAddresses.Adapter>(),
            get<CustomerCards.Adapter>(),
            get<CustomerCart.Adapter>(),
            get<Customers.Adapter>(),
            get<OrderItems.Adapter>(),
            get<Orders.Adapter>(),
            get<ProductImages.Adapter>(),
            get<ProductReviews.Adapter>(),
            get<ProductSpecs.Adapter>(),
            get<Products.Adapter>(),
        )
    }

    single {
        Customers.Adapter(
            IDColumnAdapter,
            EmailColumnAdapter,
            PasswordColumnAdapter,
            LocalDateColumnAdapter
        )
    }

    single {
        Products.Adapter(IDColumnAdapter, CategoryColumnAdapter, LocalDateColumnAdapter, LocalDateColumnAdapter)
    }

    single {
        ProductImages.Adapter(IDColumnAdapter)
    }

    single {
        ProductSpecs.Adapter(IDColumnAdapter)
    }

    single {
        Orders.Adapter(
            IDColumnAdapter,
            IDColumnAdapter,
            LocalDateColumnAdapter,
        )
    }

    single {
        CustomerAddresses.Adapter(IDColumnAdapter)
    }

    single {
        CustomerCards.Adapter(IDColumnAdapter)
    }

    single {
        OrderItems.Adapter(IDColumnAdapter, IDColumnAdapter)
    }

    single {
        ProductReviews.Adapter(IDColumnAdapter, IDColumnAdapter, RatingColumnAdapter, LocalDateColumnAdapter)
    }

    single {
        CustomerCart.Adapter(IDColumnAdapter, IDColumnAdapter)
    }
}

val dataSourceModule = module {

    single<CustomersQueries> { get<ShoppingDatabase>().customersQueries }

    single<CustomerCartQueries> { get<ShoppingDatabase>().customerCartQueries }

    single<CustomerAddressesQueries> { get<ShoppingDatabase>().customerAddressesQueries }

    single<CustomerCardsQueries> { get<ShoppingDatabase>().customerCardsQueries }

    single<OrderItemsQueries> { get<ShoppingDatabase>().orderItemsQueries }

    single<OrdersQueries> { get<ShoppingDatabase>().ordersQueries }

    single<ProductsQueries> { get<ShoppingDatabase>().productsQueries }

    single<ProductImagesQueries> { get<ShoppingDatabase>().productImagesQueries }

    single<ProductSpecsQueries> { get<ShoppingDatabase>().productSpecsQueries }

    single<ProductReviewsQueries> { get<ShoppingDatabase>().productReviewsQueries }

    single<Cloudinary> {

        cloudinary {

            apiKey = System.getenv("CLOUDINARY_API_KEY")
            apiSecret = System.getenv("CLOUDINARY_API_SECRET")
            cloudName = System.getenv("CLOUDINARY_CLOUD_NAME")
        }
    }

    single<CloudDataSource> { CloudinaryDataSource(get<Cloudinary>()) }

    single<StripeCustomerDataSource> { StripeCustomerDao() }

    single<StripeCardDataSource> { StripeCardDao() }
}

val repositoryModule = module {

    single<CustomerRepository> {
        CustomerRepositoryImpl(
            get<CustomersQueries>(),
            get<CustomerCartQueries>(),
            get<CustomerAddressesQueries>(),
            get<CustomerCardsQueries>(),
            get<StripeCustomerDataSource>(),
            get<StripeCardDataSource>(),
            get<CloudDataSource>(),
        )
    }

    single<OrderRepository> {
        OrderRepositoryImpl(
            get<OrdersQueries>(),
            get<OrderItemsQueries>(),
        )
    }

    single<ProductRepository> {
        ProductRepositoryImpl(
            get<ProductsQueries>(),
            get<ProductImagesQueries>(),
            get<ProductSpecsQueries>(),
            get<ProductReviewsQueries>(),
            get<CloudDataSource>()
        )
    }
}

val serviceModule = module {

    single<ProductService> { ProductServiceImpl(get<ProductRepository>()) }

    single<OrderService> { OrderServiceImpl(get<OrderRepository>()) }

    single<CustomerService> { CustomerServiceImpl(get<CustomerRepository>()) }

    single<AuthService> { JWTAuthService(get<CustomerRepository>()) }
}

val helperModule = module {

    single { JWTHelper() }
}
