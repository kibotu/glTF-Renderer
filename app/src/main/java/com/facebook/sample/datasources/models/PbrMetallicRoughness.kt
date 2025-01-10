package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PbrMetallicRoughness(
    @SerialName("baseColorTexture")
    var baseColorTexture: BaseColorTexture? = null,
    @SerialName("metallicRoughnessTexture")
    var metallicRoughnessTexture: MetallicRoughnessTexture? = null
)