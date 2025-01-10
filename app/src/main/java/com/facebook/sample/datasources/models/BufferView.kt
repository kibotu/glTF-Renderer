package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BufferView(
    @SerialName("buffer")
    var buffer: Int = 0,
    @SerialName("byteLength")
    var byteLength: Int? = null,
    @SerialName("byteOffset")
    var byteOffset: Int? = null,
    @SerialName("target")
    var target: Int? = null
)