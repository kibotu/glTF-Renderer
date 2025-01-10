package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Primitive(
    @SerialName("attributes")
    var attributes: Attributes? = null,
    @SerialName("indices")
    var indices: Int? = null,
    @SerialName("material")
    var material: Int? = null,
    @SerialName("mode")
    var mode: Int? = null
)