package com.example.adas

object AdasLogic {
    private const val KNOWN_CAR_WIDTH_M = 1.8f
    private const val FOCAL_LENGTH_PIX = 900f
    private const val VEHICLE_SPEED_M_S = 5.0f // Assumed relative speed if not measuring

    fun evaluate(detections: List<Detection>): Triple<String, Float?, Float?> {
        if (detections.isEmpty()) {
            return Triple("No object", null, null)
        }

        var minDistance = Float.MAX_VALUE

        for (detection in detections) {
            val widthPixels = detection.x2 - detection.x1
            if (widthPixels <= 0) continue

            val realWidthM = when (detection.label) {
                "person" -> 0.5f
                "bicycle", "motorbike" -> 0.7f
                else -> KNOWN_CAR_WIDTH_M // car, bus, truck
            }

            val distanceM = (realWidthM * FOCAL_LENGTH_PIX) / widthPixels
            if (distanceM < minDistance) {
                minDistance = distanceM
            }
        }

        if (minDistance == Float.MAX_VALUE) {
            return Triple("No object", null, null)
        }

        val ttcS = if (VEHICLE_SPEED_M_S > 0) minDistance / VEHICLE_SPEED_M_S else null

        val status = when {
            minDistance < 3.0f -> "VERY CLOSE - BRAKE"
            minDistance < 8.0f -> "Close"
            else -> "Far"
        }

        return Triple(status, minDistance, ttcS)
    }
}
