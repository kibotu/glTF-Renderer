package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    @SerialName("generator")
    var generator: String? = null,
    @SerialName("version")
    var version: String? = null
)