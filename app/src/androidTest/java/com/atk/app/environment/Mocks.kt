package com.atk.app.environment

import okhttp3.mockwebserver.MockResponse

fun PredicateDispatcher.mockForPathEndsWith(
    text: String, fileName: String,
    action: (() -> Unit)? = null
) {
    handleRequest(
        { it.path!!.endsWith(text) },
        {
            action?.invoke()
            MockResponse().setBody(AssetsReader.readFromAssets(fileName))
        }
    )
}

fun PredicateDispatcher.mockForPathStartsWith(
    text: String,
    fileName: String,
    action: (() -> Unit)? = null
) {
    handleRequest(
        { it.path!!.startsWith(text) },
        {
            action?.invoke()
            MockResponse().setBody(AssetsReader.readFromAssets(fileName))
        }
    )
}

fun PredicateDispatcher.mockForPathContains(
    text: String, fileName: String,
    action: (() -> Unit)? = null
) {
    handleRequest(
        { it.path!!.contains(text) },
        {
            action?.invoke()
            MockResponse().setBody(AssetsReader.readFromAssets(fileName))
        }
    )
}