package com.atk.app.environment

import androidx.test.espresso.IdlingRegistry
import com.jakewharton.espresso.OkHttp3IdlingResource
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

fun lazyJobFromAction(block: () -> Unit) =
    GlobalScope.launch(start = CoroutineStart.LAZY) { block() }

fun setupMockEnvironment(
    disposable: LazyJobs,
    func: EnvironmentConfigurator.() -> Unit
): EnvironmentConfigurator {

    val dispatcher = PredicateDispatcher()
    val httpsMockServer = MockWebServer().apply {
        this.dispatcher = dispatcher
        start()
        disposable.add(lazyJobFromAction { close() })
    }

    val baseUrl = httpsMockServer.url("").toString()

    val environmentConfigurator =
        EnvironmentConfiguratorImplementation(dispatcher, baseUrl, disposable)
    func(environmentConfigurator)
    environmentConfigurator.mocksIsFrozen = true

    return environmentConfigurator
}

fun setupOkHttpClientIdler(okHttpClient: OkHttpClient, disposable: LazyJobs) {
    val resource = OkHttp3IdlingResource.create("OkHttp", okHttpClient)
    IdlingRegistry.getInstance().register(resource)
    disposable.add(lazyJobFromAction { IdlingRegistry.getInstance().unregister(resource) })
}

class EnvironmentConfiguratorImplementation(
    private val dispatcher: PredicateDispatcher,
    private val baseUrl: String,
    private val disposable: LazyJobs
) : EnvironmentConfigurator {

    internal var mocksIsFrozen = false

    override val mocksVerifyInfo = MockServerVerifyInfoImpl()

    override fun setupMockSever(setupFunc: MockServerConfigurator.() -> Unit) {
        setupFunc(MockServerConfigurator(dispatcher, mocksVerifyInfo, baseUrl))
    }

    private fun assertCanCreateMocks() {
        if (mocksIsFrozen) {
            throw IllegalStateException("You're allowed create mocks only during initialization, otherwise it can't be injected in Dagger graph")
        }
    }

    override fun replaceMockServerConfig(setupFunc: MockServerConfigurator.() -> Unit) {
        dispatcher.resetDispatcher()
        setupFunc(MockServerConfigurator(dispatcher, mocksVerifyInfo, baseUrl))
    }
}

class MockServerVerifyInfoImpl : MocksVerifyInfo {
    private val personalProfileIdCalls = mutableListOf<Long>()
    private val projectIdCalls = mutableListOf<Long>()

    override var allApiCalls: Int = 0

    override var peopleApiCallCount: Int = 0
        set(value) {
            field = value; if (value != 0) allApiCalls++
        }
    override var divisionsApiCallCount: Int = 0
        set(value) {
            field = value; if (value != 0) allApiCalls++
        }
    override var holidaysApiCallCount: Int = 0
        set(value) {
            field = value; if (value != 0) allApiCalls++
        }
    override var projectApiCallCount: Int = 0
        set(value) {
            field = value; if (value != 0) allApiCalls++
        }

    override var refreshTokenCallCount: Int = 0
    override fun wasProfileLoadedFor(id: Long): Boolean = personalProfileIdCalls.contains(id)
    override fun wasProjectLoadedFor(id: Long): Boolean = projectIdCalls.contains(id)

    override val allLoadedProfiles: List<Long> get() = personalProfileIdCalls
    override val allLoadedProjects: List<Long> get() = projectIdCalls

    internal fun addIdToCalledAtPersonalProfile(id: Long) {
        personalProfileIdCalls.add(id)
    }

    internal fun addIdToCalledAtProject(id: Long) {
        projectIdCalls.add(id)
    }

    override var lastLogoutRequest: RecordedRequest? = null
    override var logoutRequestCallCount: Int = 0
}

class PredicateDispatcher : Dispatcher() {

    private class PredicateDispatcherItem(
        val predicate: (RecordedRequest) -> Boolean,
        val responseFactory: (RecordedRequest) -> MockResponse
    )

    private val items = mutableListOf<PredicateDispatcherItem>()

    override fun dispatch(request: RecordedRequest): MockResponse =
        items.find { it.predicate(request) }?.responseFactory?.invoke(request)
            ?: MockResponse().setResponseCode(404)

    fun handleRequest(
        predicate: (RecordedRequest) -> Boolean,
        response: (RecordedRequest) -> MockResponse
    ) {
        items.add(PredicateDispatcherItem(predicate, response))
    }

    fun resetDispatcher() {
        items.clear()
    }
}