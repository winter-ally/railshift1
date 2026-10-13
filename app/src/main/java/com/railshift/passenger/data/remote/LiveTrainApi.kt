package com.railshift.passenger.data.remote

import com.railshift.passenger.data.models.LiveTrainStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LiveTrainException(message: String) : Exception(message)

/** Calls GET {baseUrl}/trains/{number}/live on the RailShift backend. */
class LiveTrainApi(
    private val baseUrl: String,
    private val appKey: String
) {
    suspend fun fetch(trainNumber: String): Result<LiveTrainStatus> = withContext(Dispatchers.IO) {
        try {
            Result.success(request(trainNumber))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun request(trainNumber: String): LiveTrainStatus {
        val url = URL("${baseUrl.trimEnd('/')}/trains/$trainNumber/live")
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 15_000
            // A free Render instance can take about a minute to wake up.
            connection.readTimeout = 75_000
            if (appKey.isNotBlank()) connection.setRequestProperty("X-App-Key", appKey)

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw LiveTrainException(errorMessage(code))
            return parse(body)
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        fun errorMessage(code: Int): String = when (code) {
            400, 422 -> "That train number isn't valid."
            401 -> "The app key was rejected. Check RAILSHIFT_APP_KEY."
            404 -> "Train not found, or it isn't running today."
            502 -> "The live data provider is unavailable right now."
            503 -> "Live data is temporarily unavailable. Try again later."
            else -> "Server error ($code)."
        }

        fun parse(json: String): LiveTrainStatus {
            val o = JSONObject(json)
            fun text(key: String): String? = if (o.isNull(key)) null else o.optString(key)
            fun number(key: String): Double? = if (o.isNull(key)) null else o.optDouble(key)
            return LiveTrainStatus(
                trainNumber = text("trainNumber").orEmpty(),
                trainName = text("trainName"),
                status = text("status"),
                delayMinutes = o.optInt("delayMinutes", 0),
                currentStation = text("currentStation"),
                nextStation = text("nextStation"),
                speedKmh = number("speedKmh"),
                platform = text("platform"),
                source = text("source") ?: "live"
            )
        }
    }
}
