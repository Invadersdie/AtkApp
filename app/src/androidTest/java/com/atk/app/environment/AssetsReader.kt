package com.atk.app.environment

import okio.Buffer
import okio.buffer
import okio.source
import java.io.BufferedReader
import java.io.InputStreamReader

class AssetsReader {
    companion object {

        fun readFromAssets(fileName: String): Buffer {
            val inputStream = javaClass.classLoader?.getResourceAsStream("assets/$fileName")
            val buffer = Buffer()
            inputStream?.source()?.buffer()?.readAll(buffer)
            return buffer
        }

        fun readTextFromAssets(fileName: String): String {
            val inputStream = javaClass.classLoader?.getResourceAsStream("assets/$fileName")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val result = StringBuilder()
            do {
                val line = reader.readLine()
                result.append(line)
            } while (line != null)
            return result.toString()
        }
    }
}