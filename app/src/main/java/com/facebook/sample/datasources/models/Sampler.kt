package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sampler(
    @SerialName("magFilter")
    var magFilter: Int? = null,
    @SerialName("minFilter")
    var minFilter: Int? = null,
    @SerialName("wrapS")
    var wrapS: Int? = null,
    @SerialName("wrapT")
    var wrapT: Int? = null
)