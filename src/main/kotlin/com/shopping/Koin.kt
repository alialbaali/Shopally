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
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import main.sqldelight.com.shopping.db.*
import org.koin.dsl.module
import org.postgresql.ds.PGSimpleDataSource

private const val LOCAL_DATABASE_URL = "jdbc:sqlite:identifier.sqlite"

val dbModule = module {

    single<SqlDriver> {
        PGSimpleDataSource()
            .apply {
                user = "postgres"
                databaseName = "shopping_database"
                password = "zxcvbnm"
            }
            .asJdbcDriver()
            .apply { initSchema() }
    }

    factory<ShoppingDatabase> {
        ShoppingDatabase(
            get<SqlDriver>(),
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

            apiKey = System.getenv("CLOUDINARY_API_KEY") ?: "765587984541786"
            apiSecret = System.getenv("CLOUDINARY_API_SECRET") ?: "glbkGVpN9wwydVZ1iaTlM_BPI3o"
            cloudName = System.getenv("CLOUDINARY_CLOUD_NAME") ?: "shopping-cloud"
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

private fun SqlDriver.initSchema() {
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS Customers (
          |  id TEXT NOT NULL PRIMARY KEY,
          |  stripe_id TEXT NOT NULL UNIQUE,
          |  name TEXT NOT NULL,
          |  email TEXT NOT NULL UNIQUE,
          |  password TEXT NOT NULL,
          |  image_url TEXT NOT NULL,
          |  creation_date TEXT NOT NULL
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS Products (
          |id              TEXT          NOT NULL    PRIMARY KEY,
          |category        TEXT    NOT NULL,
          |brand           TEXT           NOT NULL,
          |name            TEXT           NOT NULL,
          |description     TEXT           NOT NULL,
          |price           REAL    NOT NULL,
          |release_date    TEXT   NOT NULL,
          |creation_date   TEXT   NOT NULL
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS CustomerAddresses (
          |customer_id TEXT NOT NULL REFERENCES Customers(id),
          |name TEXT NOT NULL,
          |country TEXT NOT NULL,
          |city TEXT NOT NULL,
          |line TEXT NOT NULL,
          |zip_code TEXT NOT NULL,
          |UNIQUE(customer_id, name)
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS Orders (
          |id TEXT NOT NULL PRIMARY KEY,
          |customer_id TEXT REFERENCES Customers(id) NOT NULL,
          |address_name TEXT NOT NULL,
          |card_last_4_numbers INTEGER NOT NULL,
          |creation_date TEXT NOT NULL
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS ProductReviews (
          |product_id TEXT REFERENCES Products(id) NOT NULL,
          |customer_id TEXT REFERENCES Customers(id) NOT NULL,
          |rating TEXT NOT NULL,
          |description TEXT,
          |creation_date TEXT NOT NULL,
          |UNIQUE(product_id, customer_id)
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS CustomerCards (
          |customer_id TEXT REFERENCES Customers(id) NOT NULL,
          |stripe_card_id TEXT NOT NULL,
          |card_last_4_numbers INTEGER NOT NULL,
          |UNIQUE(customer_id, stripe_card_id, card_last_4_numbers)
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS ProductSpecs (
          |product_id TEXT REFERENCES Products(id) NOT NULL,
          |key TEXT NOT NULL,
          |value TEXT NOT NULL,
          |UNIQUE(product_id, key, value)
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS CustomerCart(
          |customer_id TEXT REFERENCES Customers(id) NOT NULL,
          |product_id TEXT REFERENCES Products(id) NOT NULL,
          |quantity INTEGER NOT NULL,
          |UNIQUE(customer_id, product_id)
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS ProductImages (
          |product_id TEXT REFERENCES Products(id) NOT NULL,
          |image_url TEXT NOT NULL UNIQUE,
          |UNIQUE(product_id, image_url)
          |)
          """.trimMargin(),
        0
    )
    execute(
        null,
        """
          |CREATE TABLE IF NOT EXISTS OrderItems (
          |order_id TEXT REFERENCES Orders(id) NOT NULL,
          |product_id TEXT REFERENCES Products(id) NOT NULL,
          |quantity INTEGER NOT NULL,
          |UNIQUE(order_id, product_id)
          |)
          """.trimMargin(),
        0
    )
}
