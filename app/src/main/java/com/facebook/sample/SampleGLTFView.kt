/**
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
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
package com.facebook.sample

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.TextureView
import com.facebook.sample.SampleGLTFView.RenderThread
import com.facebook.sample.gles.EGLRenderTarget
import com.facebook.sample.gles.GLHelpers.checkGlError
import com.facebook.sample.rendering.SampleGLTFRenderer
import java.io.IOException

class SampleGLTFView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr) {
    private var aspectRatio = 1.0f

    private var renderThread: RenderThread? = null
    private val gltfObject = SampleGLTFRenderer()

    fun initRenderThread(surface: SurfaceTexture?, width: Int, height: Int) {
        renderThread = RenderThread(RENDER_THREAD_NAME)
        renderThread?.start()

        val msg = Message.obtain()
        msg.what = MSG_SURFACE_AVAILABLE
        msg.obj = surface
        msg.arg1 = width
        msg.arg2 = height
        renderThread?.handler?.sendMessage(msg)
    }

    fun releaseResources() {
        renderThread?.handler?.sendEmptyMessage(MSG_SURFACE_DESTROYED)
    }

    private inner class RenderThread(name: String?) : HandlerThread(name) {
        var handler: Handler? = null
        private val frameCallback: FrameCallback = ChoreographerCallback()

        private val eglRenderTarget: EGLRenderTarget = EGLRenderTarget()

        private val modelMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)

        private inner class ChoreographerCallback : FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                handler?.sendEmptyMessage(MSG_VSYNC)
            }
        }

        @Synchronized
        override fun start() {
            super.start()

            handler = object : Handler(getLooper()) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MSG_SURFACE_AVAILABLE -> onSurfaceAvailable(
                            msg.obj as SurfaceTexture?,
                            msg.arg1,
                            msg.arg2
                        )

                        MSG_VSYNC -> onVSync()
                        MSG_SURFACE_DESTROYED -> onSurfaceDestroyed()
                    }
                }
            }
        }

        fun onSurfaceAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
            Log.d(TAG, "onSurfaceAvailable w: $width h: $height")

            eglRenderTarget.createRenderSurface(surfaceTexture)

            Choreographer.getInstance().postFrameCallback(frameCallback)

            GLES20.glViewport(0, 0, width, height)
            checkGlError("glViewport")

            aspectRatio = width.toFloat() / height
            Matrix.perspectiveM(projectionMatrix, 0, FOVY, aspectRatio, Z_NEAR, Z_FAR)
            Matrix.setIdentityM(viewMatrix, 0)
            Matrix.setIdentityM(modelMatrix, 0)
            GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1f)

            try {
                gltfObject.createOnGlThread(context)
            } catch (e: IOException) {
                Log.e(TAG, e.message!!)
            }
        }

        fun onVSync() {
            if (!eglRenderTarget.hasValidContext()) {
                return
            }

            Choreographer.getInstance().postFrameCallback(frameCallback)

            eglRenderTarget.makeCurrent()
            //            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_STENCIL_BUFFER_BIT)

            updateCamera()
            gltfObject.updateModelMatrix(modelMatrix, SCALE_FACTOR * aspectRatio)

            gltfObject.draw(viewMatrix, projectionMatrix)

            eglRenderTarget.swapBuffers()
        }

        fun updateCamera() {
            Matrix.setLookAtM(
                viewMatrix, 0,
                0f, 0f, -1f,
                0f, 0f, 0f,
                0f, 1f, 0f
            )
        }

        fun onSurfaceDestroyed() {
            eglRenderTarget.release()
            gltfObject.release()
        }
    }

    companion object {
        private val TAG: String = SampleGLTFView::class.java.getSimpleName()
        private const val RENDER_THREAD_NAME = "GLTFRenderThread"

        private const val SCALE_FACTOR = 0.5f


        private const val MSG_SURFACE_AVAILABLE = 0x1
        private const val MSG_VSYNC = 0x2
        private const val MSG_SURFACE_DESTROYED = 0x3

        private const val FOVY = 70f
        private const val Z_NEAR = 1f
        private const val Z_FAR = 1000f
    }
}
