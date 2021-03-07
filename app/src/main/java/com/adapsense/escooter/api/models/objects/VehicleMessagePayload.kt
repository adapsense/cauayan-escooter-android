package com.adapsense.escooter.api.models.objects

enum class VehicleMessagePayload(val value: String) {
    LOCK("<LOCK>"),
    UNLOCK("<UNLOCK>"),
    ALARM("<ALARM>")
}