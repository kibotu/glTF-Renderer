package com.facebook.sample.datasources.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GLTFScene(
    @SerialName("accessors")
    var accessors: List<Accessor> = emptyList(),
    @SerialName("asset")
    var asset: Asset? = null,
    @SerialName("bufferViews")
    var bufferViews: List<BufferView> = emptyList(),
    @SerialName("buffers")
    var buffers: List<Buffer> = emptyList(),
    @SerialName("images")
    var images: List<Image> = emptyList(),
    @SerialName("materials")
    var materials: List<Material> = emptyList(),
    @SerialName("meshes")
    var meshes: List<Mesh> = emptyList(),
    @SerialName("nodes")
    var nodes: List<Node> = emptyList(),
    @SerialName("samplers")
    var samplers: List<Sampler> = emptyList(),
    @SerialName("scene")
    var scene: Int? = null,
    @SerialName("scenes")
    var scenes: List<Scene> = emptyList(),
    @SerialName("textures")
    var textures: List<Texture> = emptyList()
)