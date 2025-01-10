package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Texture(
    @SerialName("sampler")
    var sampler: Int? = null,
    @SerialName("source")
    var source: Int? = null
)