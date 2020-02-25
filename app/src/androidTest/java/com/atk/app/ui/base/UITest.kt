package com.atk.app.ui.base

import com.atk.app.environment.LazyJobs
import com.atk.app.environment.PredicateDispatcher
import com.atk.app.environment.lazyJobFromAction
import com.atk.app.environment.setupOkHttpClientIdler
import dagger.hilt.android.testing.HiltAndroidRule
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

open class UITest {
    private val lazyJobs = LazyJobs()

    protected open fun PredicateDispatcher.mockSetup() {}
    private val dispatcher = PredicateDispatcher().apply {
        mockSetup()
    }

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()

        MockWebServer().apply {
            dispatcher = this@UITest.dispatcher
            start(8080)
            lazyJobs.add(lazyJobFromAction { close() })
        }

        setupOkHttpClientIdler(okHttpClient, lazyJobs)
    }

    @After
    fun teardown() {
        lazyJobs.execute()
    }
}