/**
 * Copyright 2018 Facebook Inc. All Rights Reserved.
 *
 * Licensed under the Creative Commons CC BY-NC 4.0 Attribution-NonCommercial
 * License (the "License"). You may obtain a copy of the License at
 * https://creativecommons.org/licenses/by-nc/4.0/.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.facebook.sample.rendering

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.facebook.sample.datasources.Repository
import com.facebook.sample.datasources.models.GLTFScene
import com.facebook.sample.datasources.stringFromAssets
import com.facebook.sample.gles.GLHelpers
import com.facebook.sample.gles.ShaderProgram
import com.facebook.sample.rendering.SampleGLTFRenderer.GLTFRenderObject
import java.io.IOException
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.ArrayList

/**
 * This is a trivial glTF renderer that issues GLES draw commands to render the primitive meshes
 * parsed by *SampleGLTFReader*. Note that this renderer is a sample that is only intended to
 * render helloworld.glb. This is not intended to be a generic glTF renderer; it is intended to
 * be an introduction to 3D scene rendering with OpenGL ES APIs and the glTF format.
 */
class SampleGLTFRenderer {
    private var gltfRenderObjects: List<GLTFRenderObject> = emptyList()

    private var shaderProgram: ShaderProgram? = null

    private var modelViewProjectionUniform = 0
    private var positionAttribute = 0

    private val modelMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    // For simplicity we're hardcoding the component types for our render objects rather than
    // creating the typed Buffers based on the accessor's componentType.
    //
    // We'll reference the BufferView buffers directly to avoid copying data. So we need the
    // byte offset and length for the vertex and index buffers.
    class GLTFRenderObject {
        var indices: ShortBuffer? = null
        var indexByteOffset: Int = 0
        var indexByteLength: Int = 0
        var indexBufferId: Int = 0

        var vertices: FloatBuffer? = null
        var vertexByteOffset: Int = 0
        var vertexByteLength: Int = 0
        var vertexBufferId: Int = 0
    }

    // Prepares render data for each glTF mesh primitive.
    private fun createGLTFRenderObjects(gltfScene: GLTFScene): ArrayList<GLTFRenderObject> {
        val renderObjects = ArrayList<GLTFRenderObject>()
        // Note in real glTF nodes can have children. Here we're only loading top level siblings for the sample.
        for (i in gltfScene.meshes.indices) {
            val mesh = gltfScene.meshes[i]
            // Add each primitive into the render object list.
            for (j in mesh.primitives.indices) {
                val renderObject = GLTFRenderObject()

                val primitive = mesh.primitives[j]
                // Find which accessor contains the data for this attribute
                val accessorIdx: Int = primitive.attributes!!.position!!
                var accessor = gltfScene.accessors[accessorIdx]
                var bufferView = gltfScene.bufferViews[accessor.bufferView]
                var buffer = gltfScene.buffers[bufferView.buffer]

                // Load vertex data embedded in JSON
                if (accessor.componentType == GLTFConstants.COMPONENT_TYPE_FLOAT) {
                    renderObject.vertices = buffer.data?.asFloatBuffer()
                    renderObject.vertexByteLength = bufferView.byteLength!!
                    renderObject.vertexByteOffset = bufferView.byteOffset!!
                    renderObject.vertices!!.position((bufferView.byteOffset!! / BYTES_PER_FLOAT))
                } else {
                    // Not needed for our example.
                    // Would need to initialize the correct Buffer type given the componentType.
                    Log.e(TAG, "Not implemented")
                }

                // Load index data embedded in JSON
                val indicesAccessor = primitive.indices
                accessor = gltfScene.accessors.get(indicesAccessor!!)
                bufferView = gltfScene.bufferViews.get(accessor.bufferView)
                buffer = gltfScene.buffers[bufferView.buffer]

                if (bufferView.target == GLTFConstants.TARGET_ELEMENT_ARRAY_BUFFER) {
                    renderObject.indices = buffer.data?.asShortBuffer()!!
                    renderObject.indexByteLength = bufferView.byteLength!!
                    renderObject.indexByteOffset = bufferView.byteOffset ?: 0
                    renderObject.indices!!.position((bufferView.byteOffset ?: 0) / BYTES_PER_SHORT)
                } else {
                    Log.e(TAG, "Index buffer is invalid")
                }

                // Prepare and upload GPU data.
                val buffers = IntArray(2)

                GLES20.glGenBuffers(2, buffers, 0)
                renderObject.vertexBufferId = buffers[0]
                renderObject.indexBufferId = buffers[1]

                // Upload vertex buffer to GPU
                renderObject.vertices!!.position(renderObject.vertexByteOffset / BYTES_PER_FLOAT)
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, renderObject.vertexBufferId)
                GLES20.glBufferData(
                    GLES20.GL_ARRAY_BUFFER,
                    renderObject.vertexByteLength,
                    renderObject.vertices,
                    GLES20.GL_STATIC_DRAW
                )

                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)

                // Upload index buffer to GPU
                renderObject.indices!!.position(renderObject.indexByteOffset / BYTES_PER_SHORT)
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, renderObject.indexBufferId)
                GLES20.glBufferData(
                    GLES20.GL_ELEMENT_ARRAY_BUFFER,
                    renderObject.indexByteLength,
                    renderObject.indices,
                    GLES20.GL_STATIC_DRAW
                )

                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

                GLHelpers.checkGlError("glTF buffer load")

                renderObjects.add(renderObject)
            }
        }
        return renderObjects
    }

    @Throws(IOException::class)
    fun createOnGlThread(context: Context) {
        shaderProgram = ShaderProgram(
            "gltfobjectvert.glsl".stringFromAssets(context),
            "gltfobjectfrag.glsl".stringFromAssets(context)
        )

        GLES20.glUseProgram(shaderProgram!!.getShaderHandle())

        modelViewProjectionUniform = shaderProgram!!.getUniform("u_ModelViewProjection")
        positionAttribute = shaderProgram!!.getAttribute("a_Position")
        Matrix.setIdentityM(modelMatrix, 0)

        // Read the gltf file and create render objects.
        gltfRenderObjects = createGLTFRenderObjects(Repository.model!!)
    }

    fun updateModelMatrix(modelMatrix: FloatArray?, scaleFactor: Float) {
        val scaleMatrix = FloatArray(16)
        Matrix.setIdentityM(scaleMatrix, 0)
        scaleMatrix[0] = scaleFactor
        scaleMatrix[5] = scaleFactor
        scaleMatrix[10] = scaleFactor
        Matrix.multiplyMM(this.modelMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
    }

    fun draw(cameraView: FloatArray?, cameraPerspective: FloatArray?) {
        GLHelpers.checkGlError("Before draw")

        Matrix.multiplyMM(modelViewMatrix, 0, cameraView, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, cameraPerspective, 0, modelViewMatrix, 0)

        GLES20.glUseProgram(shaderProgram!!.getShaderHandle())

        GLES20.glUniformMatrix4fv(
            modelViewProjectionUniform,
            1,
            false,
            modelViewProjectionMatrix,
            0
        )

        for (i in gltfRenderObjects.indices) {
            val renderObject = gltfRenderObjects[i]

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, renderObject.vertexBufferId)

            val verticesBaseAddress = 0

            GLES20.glVertexAttribPointer(
                positionAttribute, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, verticesBaseAddress
            )

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)

            GLES20.glEnableVertexAttribArray(positionAttribute)

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, renderObject.indexBufferId)
            val numElements = renderObject.indexByteLength / BYTES_PER_SHORT
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0)
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

            GLES20.glDisableVertexAttribArray(positionAttribute)
        }

        GLHelpers.checkGlError("After draw")
    }

    fun release() {
        shaderProgram?.release()
        for (`object` in gltfRenderObjects) {
            val buffers = intArrayOf(`object`.vertexBufferId, `object`.indexBufferId)
            GLES20.glDeleteBuffers(2, buffers, 0)
        }
    }

    companion object {
        private val TAG: String = SampleGLTFRenderer::class.java.getSimpleName()

        private const val COORDS_PER_VERTEX = 3
        private const val BYTES_PER_FLOAT = 4
        private const val BYTES_PER_SHORT = 2
    }
}
