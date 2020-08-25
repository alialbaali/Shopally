package com.shopping

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

class KoinTestListener(private vararg val modules: Module) : TestListener {

    override suspend fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        startKoin { modules(*modules) }
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        super.afterEach(testCase, result)
        stopKoin()
    }

}