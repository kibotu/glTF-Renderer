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
package com.facebook.sample.gles

import android.opengl.GLES20
import android.util.Log
import java.lang.RuntimeException

object GLHelpers {
    private val TAG: String = GLHelpers::class.java.getSimpleName()

    fun checkGlError(op: String?) {
        val error = GLES20.glGetError()
        if (error == GLES20.GL_NO_ERROR) {
            return
        }
        val msg = op + ": glError 0x" + Integer.toHexString(error)
        Log.e(TAG, msg)
        throw RuntimeException(msg)
    }
}
