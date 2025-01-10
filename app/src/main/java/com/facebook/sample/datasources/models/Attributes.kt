package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attributes(
    @SerialName("NORMAL")
    var normal: Int? = null,
    @SerialName("POSITION")
    var position: Int? = null,
    @SerialName("TANGENT")
    var tangent: Int? = null,
    @SerialName("TEXCOORD_0")
    var texcoord0: Int? = null
)