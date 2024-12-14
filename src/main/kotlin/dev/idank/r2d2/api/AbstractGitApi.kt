package dev.idank.r2d2.api

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.HttpURLConnection
import java.net.URL

abstract class AbstractGitApi(private val issue: Issue) : GitApi {

    protected val objectMapper = ObjectMapper()

    protected fun sendRequest(method: RequestMethod, url: String, body: String? = null): String? {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = method.name
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer ${issue.token}")

        if (body != null) {
            connection.doOutput = true
            connection.outputStream.write(body.toByteArray())
        }

        val responseCode = connection.responseCode
        val inputStream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
        return inputStream.bufferedReader().readText()
    }

    protected abstract fun getProjectUrl(): String
}
