package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Material(
    @SerialName("alphaMode")
    var alphaMode: String? = null,
    @SerialName("name")
    var name: String? = null,
    @SerialName("normalTexture")
    var normalTexture: NormalTexture? = null,
    @SerialName("occlusionTexture")
    var occlusionTexture: OcclusionTexture? = null,
    @SerialName("pbrMetallicRoughness")
    var pbrMetallicRoughness: PbrMetallicRoughness? = null
)