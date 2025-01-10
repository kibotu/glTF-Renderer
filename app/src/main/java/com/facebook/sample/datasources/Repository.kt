package com.facebook.sample.datasources

import android.content.Context
import com.facebook.sample.datasources.models.GLTFScene
import com.github.florent37.application.provider.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

object Repository {

    var model: GLTFScene? = null

    suspend fun loadModel() = withContext(Dispatchers.IO) {
//        val string = "helloworld.json".stringFromAssets() ?: return@withContext
        val string = "simplified_model.json".stringFromAssets() ?: return@withContext
//        val string = asset.stringFromAssets() ?: return@withContext
        try {
            model = Json { ignoreUnknownKeys = true }.decodeFromString(string)
            println(model)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun String.stringFromAssets(ctx: Context? = application): String? = try {
    ctx?.assets?.open(this)?.bufferedReader()?.use { it.readText() }
} catch (e: Exception) {
    e.printStackTrace()
    null
}