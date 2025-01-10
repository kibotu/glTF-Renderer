package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("bufferView")
    var bufferView: Int? = null,
    @SerialName("mimeType")
    var mimeType: String? = null
)