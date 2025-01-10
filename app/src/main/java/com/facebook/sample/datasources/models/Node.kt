package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Node(
    @SerialName("children")
    var children: List<Int> = emptyList(),
    @SerialName("mesh")
    var mesh: Int? = null,
    @SerialName("name")
    var name: String? = null,
    @SerialName("rotation")
    var rotation: List<Double> = emptyList(),
    @SerialName("scale")
    var scale: List<Double> = emptyList()
)