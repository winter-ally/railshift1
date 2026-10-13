package com.railshift.passenger.data.network

import com.example.BuildConfig
import com.railshift.passenger.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class LiveTrainApiClient(
    private val customBaseUrl: String? = null,
    private val customAppKey: String? = null
) {

    private val baseUrl: String
        get() {
            val configUrl = customBaseUrl ?: BuildConfig.RAILSHIFT_API_URL
            return if (configUrl.isNotBlank()) configUrl.trimEnd('/') else "https://railshift1.onrender.com"
        }

    private val appKey: String
        get() {
            val key = (customAppKey ?: BuildConfig.RAILSHIFT_APP_KEY).trim()
            return if (key.isEmpty() || key == "placeholder" || key == "default_key") "Abhi@1234" else key
        }

    suspend fun fetchLiveStatus(
        trainNumber: String,
        date: String? = null,
        full: Boolean = true
    ): LiveTrainResult = withContext(Dispatchers.IO) {
        val cleanNumber = trainNumber.trim()
        if (cleanNumber.length < 4 || !cleanNumber.all { it.isDigit() }) {
            return@withContext LiveTrainResult.Error(
                errorType = LiveTrainErrorType.INVALID_TRAIN_NUMBER,
                message = "Please enter a valid 5-digit train number (e.g. 12711)."
            )
        }

        val urlBuilder = StringBuilder("$baseUrl/trains/$cleanNumber/live?full=$full")
        if (!date.isNullOrBlank()) {
            urlBuilder.append("&date=$date")
        }

        val targetUrl = urlBuilder.toString()
        var connection: HttpURLConnection? = null

        try {
            val url = URL(targetUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15_000 // 15s connect
                readTimeout = 75_000    // 75s read (handles Render cold start)
                setRequestProperty("Accept", "application/json")
                if (appKey.isNotEmpty()) {
                    setRequestProperty("X-App-Key", appKey)
                }
            }

            val responseCode = connection.responseCode
            val inputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }

            val responseBody = inputStream?.bufferedReader()?.use { it.readText() }.orEmpty()

            when (responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val status = parseLiveTrainResponse(responseBody, cleanNumber)
                    LiveTrainResult.Success(status)
                }
                HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_FORBIDDEN -> {
                    LiveTrainResult.Error(
                        errorType = LiveTrainErrorType.INVALID_APP_KEY,
                        message = "The app key was rejected or missing. Please configure your RAILSHIFT_APP_KEY in the Secrets panel or .env file.",
                        rawDetails = responseBody
                    )
                }
                HttpURLConnection.HTTP_NOT_FOUND -> {
                    LiveTrainResult.Error(
                        errorType = LiveTrainErrorType.TRAIN_NOT_FOUND,
                        message = "Train #$cleanNumber not found or not scheduled to run on the selected date.",
                        rawDetails = responseBody
                    )
                }
                422 -> {
                    LiveTrainResult.Error(
                        errorType = LiveTrainErrorType.INVALID_TRAIN_NUMBER,
                        message = "Invalid train number or request parameter rejected by server.",
                        rawDetails = responseBody
                    )
                }
                HttpURLConnection.HTTP_BAD_GATEWAY,
                HttpURLConnection.HTTP_UNAVAILABLE,
                HttpURLConnection.HTTP_GATEWAY_TIMEOUT,
                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                    LiveTrainResult.Error(
                        errorType = LiveTrainErrorType.SERVICE_UNAVAILABLE,
                        message = "Live tracking service or upstream provider is temporarily unavailable. Please try again shortly.",
                        rawDetails = responseBody
                    )
                }
                else -> {
                    LiveTrainResult.Error(
                        errorType = LiveTrainErrorType.UNKNOWN,
                        message = "Server returned response code $responseCode: ${extractDetailMessage(responseBody)}",
                        rawDetails = responseBody
                    )
                }
            }
        } catch (e: SocketTimeoutException) {
            LiveTrainResult.Error(
                errorType = LiveTrainErrorType.TIMEOUT,
                message = "Request timed out after 75s. The Render backend service may still be waking up — please try again.",
                rawDetails = e.message
            )
        } catch (e: UnknownHostException) {
            LiveTrainResult.Error(
                errorType = LiveTrainErrorType.NETWORK_ERROR,
                message = "Unable to reach the live tracking server. Please check your internet connection.",
                rawDetails = e.message
            )
        } catch (e: Exception) {
            LiveTrainResult.Error(
                errorType = LiveTrainErrorType.NETWORK_ERROR,
                message = "Network error: ${e.localizedMessage ?: "Failed to connect to tracking server."}",
                rawDetails = e.message
            )
        } finally {
            connection?.disconnect()
        }
    }

    companion object {
        fun extractDetailMessage(jsonString: String): String {
            return try {
                val json = JSONObject(jsonString)
                val detail = json.optString("detail", "")
                if (detail.isNotEmpty()) return detail
                val msg = json.optString("message", "")
                if (msg.isNotEmpty()) return msg
                jsonString
            } catch (_: Exception) {
                jsonString
            }
        }

        private fun formatTimeOrIso(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            if (raw.contains("T")) {
                val timePart = raw.substringAfter("T")
                val clean = timePart.substringBefore("+").substringBefore("Z")
                val parts = clean.split(":")
                if (parts.size >= 2) {
                    return "${parts[0]}:${parts[1]}"
                }
            }
            return raw
        }

        fun parseLiveTrainResponse(jsonString: String, fallbackNumber: String): LiveTrainStatus {
            val root = JSONObject(jsonString)
            val dataObj = if (root.has("data") && root.get("data") is JSONObject) {
                root.getJSONObject("data")
            } else {
                root
            }

            val trainNumber = dataObj.optString("trainNumber")
                .ifEmpty { dataObj.optString("train_number") }
                .ifEmpty { dataObj.optString("number", fallbackNumber) }
            val trainName = dataObj.optString("trainName")
                .ifEmpty { dataObj.optString("train_name") }
                .ifEmpty { dataObj.optString("name", "Express") }

            val delayMins = dataObj.optInt("delayMinutes", dataObj.optInt("delay_minutes", dataObj.optInt("delay", 0)))
            val rawStatus = dataObj.optString("status")
                .ifEmpty { dataObj.optString("status_as_of") }
                .ifEmpty { dataObj.optString("current_status", "") }

            val isCached = root.optBoolean("cached", dataObj.optBoolean("is_cached", dataObj.optString("source") == "cache"))

            val todayFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val runningDate = dataObj.optString("journeyDate")
                .ifEmpty { dataObj.optString("date", todayFormatted) }
            val lastUpdated = dataObj.optString("updatedAt")
                .ifEmpty { dataObj.optString("updated_at") }
                .ifEmpty { dataObj.optString("last_updated", "Just now") }

            // Parse stations list if available
            val stationList = mutableListOf<LiveStation>()
            val stationsArray = dataObj.optJSONArray("route") ?: dataObj.optJSONArray("stations")

            if (stationsArray != null) {
                for (i in 0 until stationsArray.length()) {
                    val stObj = stationsArray.optJSONObject(i) ?: continue
                    val statusStr = stObj.optString("status", "")
                    val isDeparted = statusStr.equals("departed", ignoreCase = true) ||
                        stObj.optBoolean("has_departed", false) ||
                        stObj.optBoolean("hasDeparted", false)

                    val arrTime = formatTimeOrIso(
                        stObj.optString("scheduledArrival")
                            .ifEmpty { stObj.optString("scheduled_arrival") }
                            .ifEmpty { stObj.optString("arrival_time") }
                    )
                    val depTime = formatTimeOrIso(
                        stObj.optString("scheduledDeparture")
                            .ifEmpty { stObj.optString("scheduled_departure") }
                            .ifEmpty { stObj.optString("departure_time") }
                    )
                    val actArrTime = formatTimeOrIso(
                        stObj.optString("actualArrival")
                            .ifEmpty { stObj.optString("actual_arrival") }
                            .ifEmpty { stObj.optString("eta") }
                    )
                    val actDepTime = formatTimeOrIso(
                        stObj.optString("actualDeparture")
                            .ifEmpty { stObj.optString("actual_departure") }
                    )

                    val delay = stObj.optInt(
                        "delayDeparture",
                        stObj.optInt("delayArrival", stObj.optInt("delayMinutes", stObj.optInt("delay", 0)))
                    )

                    val distance = stObj.optInt("distance", stObj.optInt("distance_km", stObj.optDouble("distance", 0.0).toInt()))

                    stationList.add(
                        LiveStation(
                            stationCode = stObj.optString("stationCode")
                                .ifEmpty { stObj.optString("station_code") }
                                .ifEmpty { stObj.optString("code", "STN") },
                            stationName = stObj.optString("stationName")
                                .ifEmpty { stObj.optString("station_name") }
                                .ifEmpty { stObj.optString("name", "Station") },
                            arrivalTime = arrTime,
                            departureTime = depTime,
                            actualArrivalTime = actArrTime,
                            actualDepartureTime = actDepTime,
                            delayMinutes = delay,
                            hasDeparted = isDeparted,
                            platformNumber = stObj.optString("platform").ifEmpty { null },
                            distanceKm = distance
                        )
                    )
                }
            }

            // Extract current and next station (handles String codes or JSONObjects)
            val currentStationObj = dataObj.optJSONObject("current_station") ?: dataObj.optJSONObject("currentStation")
            val currentStationCode = dataObj.optString("currentStation").ifEmpty { dataObj.optString("current_station") }

            val currentStation: LiveStation? = if (currentStationCode.isNotEmpty()) {
                stationList.find { it.stationCode.equals(currentStationCode, ignoreCase = true) }
                    ?: LiveStation(stationCode = currentStationCode, stationName = currentStationCode, hasDeparted = true)
            } else if (currentStationObj != null) {
                LiveStation(
                    stationCode = currentStationObj.optString("code", "CUR"),
                    stationName = currentStationObj.optString("name", "Current Station"),
                    actualDepartureTime = formatTimeOrIso(currentStationObj.optString("actual_departure")),
                    hasDeparted = true
                )
            } else if (stationList.isNotEmpty()) {
                stationList.lastOrNull { it.hasDeparted } ?: stationList.firstOrNull()
            } else {
                null
            }

            val nextStationObj = dataObj.optJSONObject("next_station") ?: dataObj.optJSONObject("nextStation")
            val nextStationCode = dataObj.optString("nextStation").ifEmpty { dataObj.optString("next_station") }

            val nextStation: LiveStation? = if (nextStationCode.isNotEmpty()) {
                stationList.find { it.stationCode.equals(nextStationCode, ignoreCase = true) }
                    ?: LiveStation(stationCode = nextStationCode, stationName = nextStationCode, hasDeparted = false)
            } else if (nextStationObj != null) {
                LiveStation(
                    stationCode = nextStationObj.optString("code", "NXT"),
                    stationName = nextStationObj.optString("name", "Next Station"),
                    actualArrivalTime = formatTimeOrIso(nextStationObj.optString("eta")),
                    platformNumber = nextStationObj.optString("platform").ifEmpty { null }
                )
            } else if (stationList.isNotEmpty()) {
                stationList.firstOrNull { !it.hasDeparted } ?: stationList.lastOrNull()
            } else {
                null
            }

            val statusSummary = if (rawStatus.isNotBlank() && !rawStatus.equals("running", ignoreCase = true)) {
                rawStatus
            } else if (delayMins > 0) {
                "Running late by $delayMins mins" + if (nextStation != null) " • Next: ${nextStation.stationName} (${nextStation.stationCode})" else ""
            } else {
                "Running on time" + if (nextStation != null) " • Next: ${nextStation.stationName} (${nextStation.stationCode})" else ""
            }

            return LiveTrainStatus(
                trainNumber = trainNumber,
                trainName = trainName,
                runningDate = runningDate,
                statusSummary = statusSummary,
                delayMinutes = delayMins,
                isRunningOnTime = delayMins <= 5,
                currentStation = currentStation,
                nextStation = nextStation,
                sourceStation = stationList.firstOrNull(),
                destinationStation = stationList.lastOrNull(),
                lastUpdated = lastUpdated,
                isCached = isCached,
                stations = stationList
            )
        }

        fun getDemoLiveStatus(trainNumber: String): LiveTrainStatus {
            val number = if (trainNumber.isNotBlank()) trainNumber else "12711"
            val name = when (number) {
                "12711" -> "Pinakini Express"
                "12727" -> "Godavari Express"
                "12704" -> "Falaknuma Express"
                "12603" -> "Hyderabad Express"
                else -> "Superfast Express"
            }

            val demoStations = listOf(
                LiveStation("BZA", "Vijayawada Jn", "06:10", "06:25", "06:10", "06:25", 0, true, "1", 0),
                LiveStation("TEL", "Tenali Jn", "06:54", "06:55", "06:54", "06:55", 0, true, "3", 31),
                LiveStation("BPP", "Bapatla", "07:29", "07:30", "07:29", "07:30", 0, true, "2", 74),
                LiveStation("CLX", "Chirala", "07:44", "07:45", "07:45", "07:47", 2, true, "2", 89),
                LiveStation("OGL", "Ongole", "08:24", "08:25", "08:24", "08:25", 0, true, "3", 138),
                LiveStation("SKM", "Singarayakonda", "08:49", "08:50", "08:50", null, 1, false, "2", 166),
                LiveStation("KVZ", "Kavali", "09:19", "09:20", "09:20", null, 0, false, "1", 204),
                LiveStation("NLR", "Nellore", "10:04", "10:05", "10:05", null, 0, false, "3", 255),
                LiveStation("GDR", "Gudur Jn", "10:48", "10:50", "10:50", null, 0, false, "1", 293),
                LiveStation("MAS", "Chennai Central", "13:00", null, "13:00", null, 0, false, "5", 431)
            )

            return LiveTrainStatus(
                trainNumber = number,
                trainName = name,
                runningDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                statusSummary = "Running on time • Next: Singarayakonda (SKM) in 15 mins",
                delayMinutes = 0,
                isRunningOnTime = true,
                currentStation = demoStations[4], // Ongole
                nextStation = demoStations[5],    // Singarayakonda
                sourceStation = demoStations.first(),
                destinationStation = demoStations.last(),
                lastUpdated = "Live (Demo preview)",
                isCached = false,
                stations = demoStations
            )
        }
    }
}
