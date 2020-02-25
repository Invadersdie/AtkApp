package com.atk.app.environment

import kotlinx.coroutines.Job

class LazyJobs {

    private val jobsList: MutableList<Job> = mutableListOf()

    fun execute() {
        jobsList.forEach { it.start() }
    }

    fun add(job: Job) {
        jobsList.add(job)
    }

}