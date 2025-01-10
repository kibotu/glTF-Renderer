package com.facebook.sample.datasources.models


import android.util.Base64
import com.facebook.sample.rendering.GLTFConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Serializable
data class Buffer(
    @SerialName("byteLength")
    var byteLength: Int? = null,

    @SerialName("uri")
    var uri: String? = null
) {
    val data: ByteBuffer?
        get() {

            if (uri != null) {
                val dataURI: String =
                    uri?.replaceFirst(GLTFConstants.DATA_URI_PREFIX.toRegex(), "") ?: return null
                val bufferData = Base64.decode(dataURI, Base64.DEFAULT)
                // Important to allocateDirect(...); wrap(...) doesn't work as GLES20.glBufferData wants a direct buffer.
                val result =
                    ByteBuffer.allocateDirect(bufferData.size).order(ByteOrder.nativeOrder())
                result.put(bufferData)
                result.rewind()
                return result
            }

            val result = ByteBuffer.allocateDirect(byteLength!!).order(ByteOrder.nativeOrder())

            return result
        }
}

