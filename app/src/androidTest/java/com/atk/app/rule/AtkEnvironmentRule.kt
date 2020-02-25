package com.atk.app.rule

import com.atk.app.environment.EnvironmentConfigurator
import com.atk.app.environment.LazyJobs
import com.atk.app.environment.setupMockEnvironment
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class AtkEnvironmentRule(
    private val disposableJobs: LazyJobs,
    private val environmentConfig: EnvironmentConfigurator.() -> Unit
) :
    TestRule {

    lateinit var environmentConfigurator: EnvironmentConfigurator private set

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                environmentConfigurator = setupMockEnvironment(disposableJobs, environmentConfig)
                try {
                    base.evaluate()
                } finally {
                    disposableJobs.execute()
                }
            }
        }
    }
}