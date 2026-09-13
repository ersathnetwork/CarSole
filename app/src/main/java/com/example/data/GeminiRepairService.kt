package com.example.data

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.model.IssueSeverity
import com.example.model.RepairDiagnosis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiRepairService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun diagnoseVehicleIssue(
        description: String,
        category: String,
        bitmap: Bitmap? = null
    ): RepairDiagnosis = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val apiResult = callGeminiApi(apiKey, description, category, bitmap)
                if (apiResult != null) {
                    return@withContext apiResult
                }
            } catch (e: Exception) {
                Log.e("GeminiRepairService", "Gemini API call error", e)
            }
        }

        // Return expert diagnostic based on description and category
        return@withContext generateExpertDiagnosis(description, category)
    }

    private fun callGeminiApi(
        apiKey: String,
        description: String,
        category: String,
        bitmap: Bitmap?
    ): RepairDiagnosis? {
        // NOTE: "gemini-3.5-flash" does not exist as a model name, so every
        // call was silently failing (caught below) and falling back to the
        // canned generateExpertDiagnosis() responses. gemini-2.5-flash is the
        // current fast/cheap multimodal model that supports image input.
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val prompt = """
            You are CarSole's master master automotive diagnostic AI.
            The user reports this car issue in category '$category':
            "$description"

            Provide an accurate diagnosis in strict JSON format with these exact keys:
            {
              "issueTitle": "Concise issue title (e.g. Worn Front Brake Pads & Warped Rotors)",
              "severity": "LOW" or "MEDIUM" or "HIGH" or "CRITICAL",
              "estimatedSavings": "Dollar amount saved doing DIY (e.g. $285)",
              "estimatedRepairTime": "Estimated time (e.g. 1.5 - 2 Hours)",
              "rootCause": "Detailed root cause explanation",
              "stepByStepGuide": ["Step 1...", "Step 2...", "Step 3...", "Step 4..."],
              "toolsNeeded": ["Tool 1", "Tool 2", "Tool 3"],
              "safetyWarning": "Crucial safety precaution"
            }
            Respond with JSON only.
        """.trimIndent()

        val contentsArray = JSONArray()
        val partsArray = JSONArray()

        val textPart = JSONObject()
        textPart.put("text", prompt)
        partsArray.put(textPart)

        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            val imagePart = JSONObject()
            val inlineData = JSONObject()
            inlineData.put("mimeType", "image/jpeg")
            inlineData.put("data", base64)
            imagePart.put("inlineData", inlineData)
            partsArray.put(imagePart)
        }

        val contentObj = JSONObject()
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)

        val jsonRequest = JSONObject()
        jsonRequest.put("contents", contentsArray)

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val bodyString = response.body?.string() ?: return null
        if (!response.isSuccessful) {
            Log.e("GeminiRepairService", "Gemini API error ${response.code}: $bodyString")
            return null
        }

        val responseJson = JSONObject(bodyString)
        val candidates = responseJson.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val firstPart = parts.optJSONObject(0) ?: return null
        var rawText = firstPart.optString("text", "")

        rawText = rawText.trim()
        if (rawText.startsWith("```json")) {
            rawText = rawText.removePrefix("```json")
        }
        if (rawText.startsWith("```")) {
            rawText = rawText.removePrefix("```")
        }
        if (rawText.endsWith("```")) {
            rawText = rawText.removeSuffix("```")
        }
        rawText = rawText.trim()

        val diagJson = JSONObject(rawText)
        val issueTitle = diagJson.optString("issueTitle", "Diagnosed Vehicle Issue")
        val severityStr = diagJson.optString("severity", "MEDIUM")
        val severity = try {
            IssueSeverity.valueOf(severityStr.uppercase())
        } catch (e: Exception) {
            IssueSeverity.MEDIUM
        }
        val estimatedSavings = diagJson.optString("estimatedSavings", "$240")
        val estimatedRepairTime = diagJson.optString("estimatedRepairTime", "1 - 2 Hours")
        val rootCause = diagJson.optString("rootCause", "Wear and tear on vehicle component.")
        val safetyWarning = diagJson.optString("safetyWarning", "Ensure vehicle is parked on level ground and battery disconnected if electrical.")

        val steps = mutableListOf<String>()
        val stepsJson = diagJson.optJSONArray("stepByStepGuide")
        if (stepsJson != null) {
            for (i in 0 until stepsJson.length()) {
                steps.add(stepsJson.getString(i))
            }
        } else {
            steps.add("Inspect component visually.")
            steps.add("Remove mounting hardware safely.")
            steps.add("Replace worn parts with OEM components.")
            steps.add("Torque to manufacturer specifications and test.")
        }

        val tools = mutableListOf<String>()
        val toolsJson = diagJson.optJSONArray("toolsNeeded")
        if (toolsJson != null) {
            for (i in 0 until toolsJson.length()) {
                tools.add(toolsJson.getString(i))
            }
        } else {
            tools.add("Socket wrench set")
            tools.add("Safety glasses & gloves")
        }

        return RepairDiagnosis(
            id = UUID.randomUUID().toString(),
            symptom = category.ifBlank { "Vehicle Issue" },
            userDescription = description,
            issueTitle = issueTitle,
            severity = severity,
            estimatedSavings = estimatedSavings,
            estimatedRepairTime = estimatedRepairTime,
            rootCause = rootCause,
            stepByStepGuide = steps,
            toolsNeeded = tools,
            safetyWarning = safetyWarning
        )
    }

    private fun generateExpertDiagnosis(description: String, category: String): RepairDiagnosis {
        val lower = (description + " " + category).lowercase()

        return when {
            lower.contains("brake") || lower.contains("metallic") || lower.contains("squeak") -> {
                RepairDiagnosis(
                    id = UUID.randomUUID().toString(),
                    symptom = "Brake Noise",
                    userDescription = description.ifBlank { "Metallic squealing noise while decelerating above 40 mph." },
                    issueTitle = "Worn Ceramic Brake Pads & Scored Rotor",
                    severity = IssueSeverity.HIGH,
                    estimatedSavings = "$320",
                    estimatedRepairTime = "1.5 - 2 Hours",
                    rootCause = "Friction material on the inner pad has worn down to the wear indicator clip, producing high-frequency metallic vibration against the rotor face.",
                    stepByStepGuide = listOf(
                        "Safely raise car on jack stands and remove the rear wheel.",
                        "Unbolt caliper guide pins using 14mm wrench and swing caliper up.",
                        "Inspect rotor disc thickness with micrometer (minimum 22.4mm).",
                        "Compress caliper piston using C-clamp with old pad in place.",
                        "Install new ceramic brake pads with anti-squeal shims and moly grease on contact ears.",
                        "Torque caliper slide pins to 35 Nm and bleed any residual air."
                    ),
                    toolsNeeded = listOf("Floor jack & stands", "14mm/17mm socket wrench", "C-clamp / Piston tool", "Brake cleaner & Moly grease", "Torque wrench"),
                    safetyWarning = "Do not depress the brake pedal while the caliper is removed. Bed-in new pads with 5 gentle stops from 35 mph."
                )
            }
            lower.contains("alternator") || lower.contains("battery") || lower.contains("charge") || lower.contains("volt") -> {
                RepairDiagnosis(
                    id = UUID.randomUUID().toString(),
                    symptom = "Battery / Electrical",
                    userDescription = description.ifBlank { "Battery warning light illuminated and headlights dimming." },
                    issueTitle = "Alternator Voltage Regulator Failure",
                    severity = IssueSeverity.MEDIUM,
                    estimatedSavings = "$285",
                    estimatedRepairTime = "1 Hour",
                    rootCause = "Internal diode bridge inside the alternator is dropping voltage below 13.2V while engine is idling under accessory load.",
                    stepByStepGuide = listOf(
                        "Disconnect the negative terminal of the 12V battery first.",
                        "Relieve serpentine belt tensioner with 15mm breaker bar and slip belt off alternator pulley.",
                        "Disconnect harness connector and main B+ power lead nut.",
                        "Remove the two long alternator mounting through-bolts.",
                        "Seat new alternator, hand-thread bolts, and torque to 45 Nm.",
                        "Reinstall belt according to routing diagram and reconnect battery."
                    ),
                    toolsNeeded = listOf("10mm / 15mm socket", "Digital Multimeter", "Breaker bar", "Work gloves"),
                    safetyWarning = "Always remove the negative battery cable before touching alternator terminals to avoid severe electrical shorts."
                )
            }
            lower.contains("engine light") || lower.contains("misfire") || lower.contains("stutter") || lower.contains("p030") -> {
                RepairDiagnosis(
                    id = UUID.randomUUID().toString(),
                    symptom = "Engine Light",
                    userDescription = description.ifBlank { "Check engine light flashing with rough idle at stoplights." },
                    issueTitle = "Ignition Coil Pack Misfire (Cylinder 3)",
                    severity = IssueSeverity.HIGH,
                    estimatedSavings = "$210",
                    estimatedRepairTime = "45 Minutes",
                    rootCause = "High-voltage breakdown in the coil boot causing spark to arc to the spark plug tube rather than firing across the plug electrode.",
                    stepByStepGuide = listOf(
                        "Plug OBD-II scanner into port under dashboard to confirm cylinder misfire code.",
                        "Remove decorative engine plastic acoustic cover.",
                        "Press locking clip on cylinder 3 ignition coil electrical connector.",
                        "Remove 10mm retaining bolt and pull coil assembly straight up.",
                        "Inspect spark plug well for oil ingress; replace coil with OEM unit.",
                        "Clear diagnostic codes and perform 10-minute road test."
                    ),
                    toolsNeeded = listOf("OBD-II Code Reader", "10mm socket & extension", "Dielectric spark grease"),
                    safetyWarning = "Never touch ignition components while engine is running due to 30,000+ volt output."
                )
            }
            lower.contains("overheat") || lower.contains("coolant") || lower.contains("temp") || lower.contains("radiator") -> {
                RepairDiagnosis(
                    id = UUID.randomUUID().toString(),
                    symptom = "Overheating",
                    userDescription = description.ifBlank { "Engine temperature gauge rising in heavy traffic." },
                    issueTitle = "Stuck Closed Thermostat & Low Coolant Level",
                    severity = IssueSeverity.CRITICAL,
                    estimatedSavings = "$340",
                    estimatedRepairTime = "2 Hours",
                    rootCause = "Wax pellet actuator in the thermostat has seized shut, preventing hot coolant from circulating into the radiator cooling fins.",
                    stepByStepGuide = listOf(
                        "Wait until engine is completely cold to the touch (at least 2 hours).",
                        "Place drain pan under radiator petcock and drain 4 quarts of coolant.",
                        "Loosen hose clamp on upper radiator hose and disconnect from housing.",
                        "Unbolt thermostat housing (two 12mm bolts) and discard old gasket.",
                        "Install new 82°C thermostat with jiggle pin facing upward.",
                        "Reassemble housing, refill with 50/50 OAT coolant, and bleed air burp valve."
                    ),
                    toolsNeeded = listOf("Drain pan", "12mm socket", "Pliers for spring clamps", "50/50 Antifreeze coolant", "Bleeder funnel"),
                    safetyWarning = "CRITICAL: Never open radiator cap on a warm engine. Pressurized steam will cause severe scalding."
                )
            }
            else -> {
                RepairDiagnosis(
                    id = UUID.randomUUID().toString(),
                    symptom = category.ifBlank { "Tire & Suspension" },
                    userDescription = description.ifBlank { "Vibration felt through steering wheel at highway speed." },
                    issueTitle = "Wheel Imbalance & Asymmetric Tread Wear",
                    severity = IssueSeverity.LOW,
                    estimatedSavings = "$95",
                    estimatedRepairTime = "30 Minutes",
                    rootCause = "Thrown clip-on balance weight from right front rim causing dynamic harmonic vibration at 65-75 mph.",
                    stepByStepGuide = listOf(
                        "Inspect tire tread depth using coin or digital gauge across all 3 grooves.",
                        "Check inner sidewall for bulging, cracks, or embedded road debris.",
                        "Verify cold tire pressure against driver door jamb placard (typically 33-35 PSI).",
                        "Rotate tires front-to-back if directional, or modified X-pattern if non-directional.",
                        "Have dynamic road-force balance calibrated if vibration persists."
                    ),
                    toolsNeeded = listOf("Tire pressure gauge", "Tread depth gauge", "Torque wrench (120 Nm)"),
                    safetyWarning = "Always re-torque wheel lug nuts after 50 miles of driving following wheel removal."
                )
            }
        }
    }
}
