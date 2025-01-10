package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Accessor(
    @SerialName("bufferView")
    var bufferView: Int = 0,
    @SerialName("componentType")
    var componentType: Int? = null,
    @SerialName("count")
    var count: Int? = null,
    @SerialName("max")
    var max: List<Double> = emptyList(),
    @SerialName("min")
    var min: List<Double> = emptyList(),
    @SerialName("type")
    var type: String? = null
)