package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mesh(
    @SerialName("name")
    var name: String? = null,
    @SerialName("primitives")
    var primitives: List<Primitive> = emptyList()
)