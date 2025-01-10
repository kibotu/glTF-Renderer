package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Scene(
    @SerialName("name")
    var name: String? = null,
    @SerialName("nodes")
    var nodes: List<Int> = emptyList()
)