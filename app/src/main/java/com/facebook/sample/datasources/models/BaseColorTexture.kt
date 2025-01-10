package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseColorTexture(
    @SerialName("index")
    var index: Int? = null
)